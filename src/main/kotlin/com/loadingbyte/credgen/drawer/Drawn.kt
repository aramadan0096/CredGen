package com.loadingbyte.credgen.drawer

import com.loadingbyte.credgen.imaging.DeferredImage
import com.loadingbyte.credgen.imaging.DeferredVideo
import com.loadingbyte.credgen.imaging.Y
import com.loadingbyte.credgen.project.Credits
import com.loadingbyte.credgen.project.Page
import com.loadingbyte.credgen.project.Project
import kotlinx.collections.immutable.PersistentList


class DrawnProject(val project: Project, val drawnCredits: PersistentList<DrawnCredits>)
class DrawnCredits(val credits: Credits, val drawnPages: PersistentList<DrawnPage>, val video: DeferredVideo)
class DrawnPage(val page: Page, val defImage: DeferredImage, val stageInfo: PersistentList<DrawnStageInfo>)


sealed interface DrawnStageInfo {

    class Card(
        val middleY: Y
    ) : DrawnStageInfo

    class Scroll(
        val scrollStartY: Y,
        val scrollStopY: Y,
        val startRampHeight: Double,
        val stopRampHeight: Double,
        val ownedScrollHeight: Y,
        val frames: Int,
        val steadyFrames: Int,
        val rampFrames: Int
    ) : DrawnStageInfo

}
