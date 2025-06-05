package com.loadingbyte.credgen.demos

import com.loadingbyte.credgen.common.l10n
import com.loadingbyte.credgen.demo.PageDemo
import com.loadingbyte.credgen.demo.StyleSettingsDemo
import com.loadingbyte.credgen.demo.parseCreditsCS
import com.loadingbyte.credgen.project.*
import kotlinx.collections.immutable.persistentListOf


private const val DIR = "guide/content-style/grid-layout"

val GUIDE_CONTENT_STYLE_GRID_LAYOUT_DEMOS
    get() = listOf(
        GuideContentStyleGridLayoutExampleDemo,
        GuideContentStyleGridLayoutSortDemo,
        GuideContentStyleGridColsDemo,
        GuideContentStyleGridLayoutFillingOrderDemo,
        GuideContentStyleGridLayoutFillingBalancedDemo,
        GuideContentStyleGridLayoutStructureDemo,
        GuideContentStyleGridLayoutForceColWidthAndRowHeightDemo,
        GuideContentStyleGridLayoutHarmonizeColWidthsDemo,
        GuideContentStyleGridLayoutHarmonizeRowHeightDemo,
        GuideContentStyleGridLayoutCellHJustifyPerColDemo,
        GuideContentStyleGridLayoutCellVJustifyDemo,
        GuideContentStyleGridLayoutRowAndColGapsDemo
    )


object GuideContentStyleGridLayoutExampleDemo : PageDemo("$DIR/example", Format.PNG, pageGuides = true) {
    override fun credits() = listOf(GRID_SPREADSHEET.parseCreditsCS(tabularCS.copy(name = "Demo")))
}


object GuideContentStyleGridLayoutSortDemo : StyleSettingsDemo<ContentStyle>(
    ContentStyle::class.java, "$DIR/sort", Format.SLOW_STEP_GIF,
    listOf(ContentStyle::sort.st()), pageGuides = true
) {
    override fun styles() = buildList<ContentStyle> {
        for (sort in Sort.entries)
            this += tabularCS.copy(name = "Demo", sort = sort)
    }

    override fun credits(style: ContentStyle) = """
@Body,@Content Style
Chantal,Demo
Eva,
Ada,
Ben,
Florian
Dan,
        """.parseCreditsCS(style)
}


object GuideContentStyleGridColsDemo : StyleSettingsDemo<ContentStyle>(
    ContentStyle::class.java, "$DIR/cols", Format.STEP_GIF,
    listOf(ContentStyle::gridCols.st()), pageGuides = true
) {
    override fun styles() = buildList<ContentStyle> {
        val c = HJustify.CENTER
        this += tabularCS.copy(name = "Demo", gridCols = 2, gridCellHJustifyPerCol = persistentListOf(c, c))
        this += last().copy(gridCols = 3, gridCellHJustifyPerCol = persistentListOf(c, c, c))
    }

    override fun credits(style: ContentStyle) = GRID_SPREADSHEET.parseCreditsCS(style)
}


object GuideContentStyleGridLayoutFillingOrderDemo : StyleSettingsDemo<ContentStyle>(
    ContentStyle::class.java, "$DIR/filling-order", Format.SLOW_STEP_GIF,
    listOf(ContentStyle::gridFillingOrder.st()), pageGuides = true
) {
    override fun styles() = buildList<ContentStyle> {
        for (order in GridFillingOrder.entries)
            this += tabularCS.copy(name = "Demo", gridFillingOrder = order, gridFillingBalanced = false)
    }

    override fun credits(style: ContentStyle) = """
@Body,@Content Style
111,Demo
222,
333,
444,
555,
666,
777,
        """.parseCreditsCS(style)
}


object GuideContentStyleGridLayoutFillingBalancedDemo : StyleSettingsDemo<ContentStyle>(
    ContentStyle::class.java, "$DIR/filling-balanced", Format.STEP_GIF,
    listOf(ContentStyle::gridFillingBalanced.st()), pageGuides = true
) {
    override fun styles() = buildList<ContentStyle> {
        this += tabularCS.copy(name = "Demo", gridFillingBalanced = true)
        this += last().copy(gridFillingBalanced = false)
    }

    override fun credits(style: ContentStyle) = GRID_SPREADSHEET.parseCreditsCS(style)
}


object GuideContentStyleGridLayoutStructureDemo : StyleSettingsDemo<ContentStyle>(
    ContentStyle::class.java, "$DIR/structure", Format.STEP_GIF,
    listOf(ContentStyle::gridStructure.st()), pageGuides = true
) {
    override fun styles() = buildList<ContentStyle> {
        for (structure in GridStructure.entries)
            this += tabularCS.copy(name = "Demo", gridStructure = structure)
    }

    override fun credits(style: ContentStyle) = """
@Body,@Content Style
Ada,Demo
Ben,
Chantal,
Dan,
Eva,
Florian
        """.parseCreditsCS(style)
}


object GuideContentStyleGridLayoutForceColWidthAndRowHeightDemo : StyleSettingsDemo<ContentStyle>(
    ContentStyle::class.java, "$DIR/force-col-width-and-row-height", Format.STEP_GIF,
    listOf(ContentStyle::gridForceColWidthPx.st(), ContentStyle::gridForceRowHeightPx.st()), pageGuides = true
) {
    override fun styles() = buildList<ContentStyle> {
        this += tabularCS.copy(name = "Demo")
        this += last().copy(gridForceColWidthPx = Opt(true, 150.0))
        this += last().copy(gridForceRowHeightPx = Opt(true, 70.0))
    }

    override fun credits(style: ContentStyle) = """
@Body,@Content Style
Ada,Demo
Ben,
Chantal,
Dan,
Eva,
Florian
        """.parseCreditsCS(style)
}


object GuideContentStyleGridLayoutHarmonizeColWidthsDemo : StyleSettingsDemo<ContentStyle>(
    ContentStyle::class.java, "$DIR/harmonize-col-widths", Format.SLOW_STEP_GIF,
    listOf(
        ContentStyle::gridHarmonizeColWidths.st(), ContentStyle::gridHarmonizeColWidthsAcrossStyles.st(),
        ContentStyle::gridHarmonizeColUnderoccupancy.st()
    ), pageGuides = true
) {
    private val altStyleName get() = l10n("project.template.contentStyleTabular") + " 2"

    override fun styles() = buildList<ContentStyle> {
        this += tabularCS.copy(
            name = "Demo", spineAttachment = SpineAttachment.BODY_LEFT, gridCols = 2,
            gridStructure = GridStructure.FREE,
            gridCellHJustifyPerCol = persistentListOf(HJustify.CENTER, HJustify.CENTER)
        )
        this += last().copy(gridHarmonizeColWidths = HarmonizeExtent.ACROSS_BLOCKS)
        this += last().copy(gridHarmonizeColWidthsAcrossStyles = persistentListOf(altStyleName))
        this += last().copy(gridHarmonizeColUnderoccupancy = GridColUnderoccupancy.LEFT_RETAIN)
        this += last().copy(gridHarmonizeColUnderoccupancy = GridColUnderoccupancy.RIGHT_RETAIN)
    }

    override fun credits(style: ContentStyle) = """
@Body,@Vertical Gap,@Content Style,@Spine Position
Benjamin Backer,,Demo,-160
Brady,,,
Christoph Caterer,,,
Dominic,,,
,,,
Draco,,,
Ian,,,
Sophia,,,
Teo,,,
,2,,
Max,,$altStyleName,
Tom,,,
Don,,,
        """.parseCreditsCS(
        style,
        tabularCS.copy(
            name = altStyleName, spineAttachment = SpineAttachment.BODY_LEFT, bodyLetterStyleName = "Small",
            gridStructure = GridStructure.FREE, gridHarmonizeColWidths = HarmonizeExtent.ACROSS_BLOCKS
        )
    )
}


object GuideContentStyleGridLayoutHarmonizeRowHeightDemo : StyleSettingsDemo<ContentStyle>(
    ContentStyle::class.java, "$DIR/harmonize-row-height", Format.SLOW_STEP_GIF,
    listOf(ContentStyle::gridHarmonizeRowHeight.st(), ContentStyle::gridHarmonizeRowHeightAcrossStyles.st()),
    pageGuides = true
) {
    private val altStyleName get() = l10n("project.template.contentStyleTabular") + " 2"

    override fun styles() = buildList<ContentStyle> {
        this += tabularCS.copy(name = "Demo", gridStructure = GridStructure.FREE)
        this += last().copy(gridHarmonizeRowHeight = HarmonizeExtent.WITHIN_BLOCK)
        this += last().copy(gridHarmonizeRowHeight = HarmonizeExtent.ACROSS_BLOCKS)
        this += last().copy(gridHarmonizeRowHeightAcrossStyles = persistentListOf(altStyleName))
    }

    override fun credits(style: ContentStyle) = """
@Body,@Vertical Gap,@Content Style
Benjamin Backer,,Demo
{{Pic logo.svg x50}},,
Christoph Caterer,,
Dominic Donor,,
Draco Driver,,
Ian Inspiration,,
,,
Ludwig,,
Richard,,
Sophia,,
,2,
Valerie,,$altStyleName
Brady,,
Harold,,
        """.parseCreditsCS(
        style,
        tabularCS.copy(
            name = altStyleName, bodyLetterStyleName = "Small",
            gridStructure = GridStructure.FREE, gridHarmonizeRowHeight = HarmonizeExtent.ACROSS_BLOCKS
        )
    )
}


object GuideContentStyleGridLayoutCellHJustifyPerColDemo : StyleSettingsDemo<ContentStyle>(
    ContentStyle::class.java, "$DIR/cell-hjustify-per-col", Format.STEP_GIF,
    listOf(ContentStyle::gridCellHJustifyPerCol.st()), pageGuides = true
) {
    override fun styles() = buildList<ContentStyle> {
        val c = HJustify.CENTER
        this += tabularCS.copy(name = "Demo", gridCols = 3, gridCellHJustifyPerCol = persistentListOf(c, c, c))
        this += last().copy(gridCellHJustifyPerCol = persistentListOf(HJustify.LEFT, c, HJustify.RIGHT))
    }

    override fun credits(style: ContentStyle) = GRID_SPREADSHEET.parseCreditsCS(style)
}


object GuideContentStyleGridLayoutCellVJustifyDemo : StyleSettingsDemo<ContentStyle>(
    ContentStyle::class.java, "$DIR/cell-vjustify", Format.STEP_GIF,
    listOf(ContentStyle::gridCellVJustify.st()), pageGuides = true
) {
    override fun styles() = buildList<ContentStyle> {
        for (vJustify in VJustify.entries)
            this += tabularCS.copy(name = "Demo", gridCellVJustify = vJustify, gridRowGapPx = 32.0)
    }

    override fun credits(style: ContentStyle) = """
@Body,@Content Style
Benjamin Backer,Demo
{{Pic logo.svg x100}},
Brady “Bar” Owner,
Christoph Caterer,
        """.parseCreditsCS(style)
}


object GuideContentStyleGridLayoutRowAndColGapsDemo : StyleSettingsDemo<ContentStyle>(
    ContentStyle::class.java, "$DIR/row-and-col-gaps", Format.STEP_GIF,
    listOf(ContentStyle::gridRowGapPx.st(), ContentStyle::gridColGapPx.st()), pageGuides = true
) {
    override fun styles() = buildList<ContentStyle> {
        this += tabularCS.copy(name = "Demo")
        this += last().copy(gridRowGapPx = 32.0)
        this += last().copy(gridRowGapPx = 64.0)
        this += last().copy(gridColGapPx = 64.0)
    }

    override fun credits(style: ContentStyle) = GRID_SPREADSHEET.parseCreditsCS(style)
}


private val tabularCS = PRESET_CONTENT_STYLE.copy(
    bodyLetterStyleName = "Name", bodyLayout = BodyLayout.GRID, gridCols = 3,
    gridStructure = GridStructure.EQUAL_WIDTH_COLS,
    gridCellHJustifyPerCol = persistentListOf(HJustify.CENTER, HJustify.CENTER, HJustify.CENTER), gridColGapPx = 32.0
)

private const val GRID_SPREADSHEET = """
@Body,@Content Style
Benjamin Backer,Demo
Brady “Bar” Owner,
Christoph Caterer,
Dominic Donor,
Draco Driver,
Ian Inspiration,
Ludwig Lender,
Richard Renter,
Sophia Supporter,
Valerie Volunteer,
"""
