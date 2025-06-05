@file:JvmName("DemoMain")

package com.loadingbyte.credgen

import com.loadingbyte.credgen.common.FALLBACK_TRANSLATED_LOCALE
import com.loadingbyte.credgen.common.TRANSLATED_LOCALES
import com.loadingbyte.credgen.common.comprehensivelyApplyLocale
import com.loadingbyte.credgen.demo.Demo
import com.loadingbyte.credgen.demos.*
import com.loadingbyte.credgen.ui.helper.disableSnapToSide
import com.loadingbyte.credgen.ui.helper.minimumWindowSize
import java.awt.Dimension
import kotlin.system.exitProcess


private val enabledDemos: List<Demo>
    get() = GUIDE_CONTENT_STYLE_DEMOS +
            GUIDE_CONTENT_STYLE_FLOW_LAYOUT_DEMOS +
            GUIDE_CONTENT_STYLE_GRID_LAYOUT_DEMOS +
            GUIDE_CONTENT_STYLE_PARAGRAPHS_LAYOUT_DEMOS +
            GUIDE_CREDITS_SPREADSHEET_DEMOS +
            GUIDE_LETTER_STYLE_DEMOS +
            GUIDE_LETTER_STYLE_LAYER_DEMOS +
            GUIDE_PAGE_STYLE_DEMOS +
            GUIDE_PICTURE_VIDEO_DEMOS +
            GUIDE_PROJECT_FOLDER_DEMOS +
            GUIDE_PROJECT_SETTINGS_DEMOS +
            GUIDE_TRANSITION_STYLE_DEMOS +
            GUIDE_USER_INTERFACE_DEMOS +
            HOME_DEMOS +
            SCREENCAST_DEMOS


fun main() {
    // Call the regular entry point, which will in turn call our generateDemos() function and abort the regular startup.
    demoCallback = ::generateDemos
    main(emptyArray())
}


private fun generateDemos() {
    // We want to shrink windows to smaller sizes than what would usually be sensible.
    minimumWindowSize = Dimension(300, 200)
    // If we were to leave this enabled, we would sometimes not be able to shrink the project and styling windows.
    disableSnapToSide = true

    Thread({
        for (locale in TRANSLATED_LOCALES) {
            comprehensivelyApplyLocale(locale)
            for (demo in enabledDemos)
                if (demo.isLocaleSensitive || locale == FALLBACK_TRANSLATED_LOCALE)
                    demo.doGenerate(locale)
        }
        // For some reason, some mouseTo() actions which take the mouse to the fake spreadsheet in the screencast
        // demo cause the process to not terminate by itself, so we have to do it forcefully here.
        exitProcess(0)
    }, "DemoGenerator").start()
}
