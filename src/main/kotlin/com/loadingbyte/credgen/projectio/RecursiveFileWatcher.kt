package com.loadingbyte.credgen.projectio

import com.loadingbyte.credgen.common.LOGGER
import com.loadingbyte.credgen.common.walkSafely
import java.io.IOException
import java.nio.file.FileSystems
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.*
import java.nio.file.WatchKey
import java.nio.file.attribute.FileTime
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.io.path.fileSize
import kotlin.io.path.getLastModifiedTime
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile


/** No method in this class throws exceptions. Instead, file watching is maintained on a best-effort basis. */
object RecursiveFileWatcher {

    enum class Event { MODIFY, DELETE }

    private class Order(val listener: (Event, Path) -> Unit) {
        val watchKeys = HashSet<WatchKey>()  // Use a set to speed up the "in" operation.
        val memory = HashMap<Path, MemoryEntry>()
        var continuousPolling = true
        var onetimePolling = false
    }

    private class MemoryEntry(var size: Long, var modTime: FileTime, var lastSeenTick: Long)


    private val watcher = FileSystems.getDefault().newWatchService()
    private val orders = HashMap<Path, Order>()
    private val lock = ReentrantLock()
    private var pollingTick: Long = 0

    init {
        // We schedule the polling with a fixed delay (as opposed to at a fixed rate) to guarantee that there is some
        // breathing time between poll() calls, even in cases where poll() always takes very long to complete. In that
        // breathing time, the lock is free, so calls to watch() and unwatch() actually have a chance to run.
        Thread({
            while (true) {
                poll()
                Thread.sleep(1000)
            }
        }, "FileWatcher-Poller").apply { isDaemon = true }.start()

        Thread({
            while (true) {
                val watchKey = watcher.take()
                receiveWatchKey(watchKey)
                watchKey.reset()
            }
        }, "FileWatcher-Native").apply { isDaemon = true }.start()
    }

    /** Notice: The [listener] is only notified about regular files, not directories. */
    fun watch(rootDir: Path, listener: (Event, Path) -> Unit) {
        lock.withLock {
            val order = Order(listener)
            orders[rootDir] = order
            // Recursively memorize all files below the root directory, notify the listener about them, and register
            // watching instructions for the root directory and all subdirectories.
            setupFileTree(order, rootDir, notifyListener = false)
        }
    }

    fun unwatch(rootDir: Path) {
        lock.withLock {
            orders.remove(rootDir)?.run { watchKeys.forEach(WatchKey::cancel) }
        }
    }

    private fun poll() {
        lock.withLock {
            pollingTick++
            for ((rootDir, order) in orders)
                if (order.continuousPolling || order.onetimePolling) {
                    order.onetimePolling = false
                    // Check whether the size or mod time of any file in the file tree has changed (including new
                    // files!), and if so, notify the listener. Also refresh the last seen tick of every existing file.
                    for (file in rootDir.walkSafely())
                        if (file.isRegularFile())
                            potentialModification(order, file, notifyListener = true, setLastSeenTick = true)
                    // De-memorize all files which have not been seen this tick, and notify the listener about them.
                    order.memory.entries.remAndDoIf(
                        { (_, memoryEntry) -> memoryEntry.lastSeenTick != pollingTick },
                        { (file, _) -> order.listener(Event.DELETE, file) })
                }
        }
    }

    private fun receiveWatchKey(watchKey: WatchKey) {
        lock.withLock {
            // Poll the event list now so that it is cleared even when we return early.
            val events = watchKey.pollEvents()

            // Retrieve the order which has added the watch key.
            // Sometimes, it can happen that a watch key of a deleted child directory arrives after that of the deleted
            // parent directory. In such cases, the deletion routine on the parent directory has already taken out the
            // child watch keys and notified the listener about the child directories, so we can just return here.
            val order = orders.values.firstOrNull { order -> watchKey in order.watchKeys } ?: return

            // We have just seen that the native file watcher is working, so stop polling this root directory.
            order.continuousPolling = false

            for (event in events)
                if (event.kind() == OVERFLOW) {
                    // In the rare case of too many events overflowing the native file watcher, go back to polling for
                    // just one cycle to catch up.
                    order.onetimePolling = true
                } else {
                    val file = (watchKey.watchable() as Path).resolve(event.context() as Path)

                    // When a directory is created, memorize all files below it (recursively), notify the listener about
                    // them, and register watching instructions for the new directory and all its subdirectories.
                    if (event.kind() == ENTRY_CREATE && file.isDirectory())
                        setupFileTree(order, file, notifyListener = true)

                    // When a directory is deleted, deregister the instructions for it and all its subdirectories.
                    if (event.kind() == ENTRY_DELETE) {
                        val wasDir = order.watchKeys.remAndDoIf(
                            { (it.watchable() as Path).startsWith(file) },
                            { it.cancel() })
                        // Also de-memorize all files somewhere below the directory and notify the listener about them.
                        if (wasDir)
                            order.memory.keys.remAndDoIf({ it.startsWith(file) }, { order.listener(Event.DELETE, it) })
                    }

                    // When a regular file is created or modified, check its size & mod time and notify the listener.
                    if ((event.kind() == ENTRY_CREATE || event.kind() == ENTRY_MODIFY) && file.isRegularFile())
                        potentialModification(order, file, notifyListener = true, setLastSeenTick = false)

                    // When a regular file is deleted, de-memorize it and notify the listener.
                    if (event.kind() == ENTRY_DELETE && order.memory.remove(file) != null)
                        order.listener(Event.DELETE, file)
                }
        }
    }

    private fun setupFileTree(order: Order, dir: Path, notifyListener: Boolean) {
        // Memorize all regular files and their current sizes & mod times.
        for (file in dir.walkSafely())
            if (file.isRegularFile())
                potentialModification(order, file, notifyListener = notifyListener, setLastSeenTick = false)

        // Only after the memorization is complete, register a file watcher in each directory of the file tree.
        for (file in dir.walkSafely())
            if (file.isDirectory())
                try {
                    order.watchKeys.add(file.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY))
                } catch (_: NoSuchFileException) {
                    // Once again, unlucky timing.
                } catch (e: IOException) {
                    // While we will not receive notifications from the OS for this directory, the alternative polling
                    // mechanism could potentially still work (unless we fail to watch only some but not all dirs, which
                    // would however be a very strange error condition, so we don't implement special cases for that).
                    LOGGER.error("Cannot register directory '{}' with the OS file watcher.", file, e)
                }
    }

    private fun potentialModification(order: Order, file: Path, notifyListener: Boolean, setLastSeenTick: Boolean) {
        val size: Long
        val modTime: FileTime
        try {
            size = file.fileSize()
            modTime = file.getLastModifiedTime()
        } catch (_: NoSuchFileException) {
            // The file was deleted between it being detected and this code being reached. Abort this method and let the
            // deletion detector notify the listener in a moment.
            return
        } catch (e: IOException) {
            // If we can't get the size & mod time for some other reason, hope that maybe it'll work again next time.
            LOGGER.error("Cannot get the size & mod time required to check file '{}' for changes.", file, e)
            return
        }
        val memoryEntry = order.memory.computeIfAbsent(file) { MemoryEntry(-1, FileTime.fromMillis(-1), -1) }
        if (memoryEntry.size != size || memoryEntry.modTime != modTime) {
            memoryEntry.size = size
            memoryEntry.modTime = modTime
            if (notifyListener)
                order.listener(Event.MODIFY, file)
        }
        if (setLastSeenTick)
            memoryEntry.lastSeenTick = pollingTick
    }

    private inline fun <E> MutableCollection<E>.remAndDoIf(filter: (E) -> Boolean, action: (E) -> Unit): Boolean {
        var removed = false
        val iter = iterator()
        for (elem in iter)
            if (filter(elem)) {
                removed = true
                iter.remove()
                action(elem)
            }
        return removed
    }

}
