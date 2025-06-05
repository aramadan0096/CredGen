package com.loadingbyte.credgen.ui.comms

import com.loadingbyte.credgen.ui.ProjectController
import java.awt.GraphicsConfiguration
import java.awt.event.KeyEvent
import java.nio.file.Path


interface MasterCtrlComms {

    fun onGlobalKeyEvent(event: KeyEvent): Boolean
    fun showWelcomeFrame(openProjectDir: Path? = null, tab: WelcomeTab? = null)
    fun showOverlayCreation()
    fun showDeliveryDestTemplateCreation()
    fun tryCloseProjectsAndDisposeAllFrames(force: Boolean = false): Boolean

    // ========== FOR WELCOME CTRL ==========

    fun onCloseWelcomeFrame()
    /** @throws ProjectController.ProjectInitializationAbortedException */
    fun openProject(projectDir: Path, openOnScreen: GraphicsConfiguration)
    fun isProjectDirOpen(projectDir: Path): Boolean

}
