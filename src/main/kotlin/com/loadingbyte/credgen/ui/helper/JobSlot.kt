package com.loadingbyte.credgen.ui.helper

import com.loadingbyte.credgen.common.GLOBAL_THREAD_POOL
import com.loadingbyte.credgen.common.throwableAwareTask
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


class JobSlot(private val delay: Long = 0L) {

    private val lock = ReentrantLock()
    private var job: Runnable? = null
    private var jobStartTime: Long = 0
    private var isRunning = false

    fun submit(job: Runnable) {
        val jobStartTime = System.currentTimeMillis() + delay
        lock.withLock {
            this.job = job
            this.jobStartTime = jobStartTime
            if (!isRunning) {
                isRunning = true
                GLOBAL_THREAD_POOL.submit(throwableAwareTask(::run))
            }
        }
    }

    fun cancel() {
        lock.withLock { job = null }
    }

    private fun run() {
        while (true) {
            val job: Runnable
            val jobStartTime: Long
            lock.withLock {
                job = this.job ?: run {
                    isRunning = false
                    return
                }
                jobStartTime = this.jobStartTime
            }
            val sleepTime = jobStartTime - System.currentTimeMillis()
            if (sleepTime > 0L) {
                Thread.sleep(sleepTime)
                if (lock.withLock { this.job !== job })
                    continue
            }
            job.run()
            lock.withLock {
                if (this.job === job) {
                    this.job = null
                    isRunning = false
                    return
                }
            }
        }
    }

}
