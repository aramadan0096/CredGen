package com.loadingbyte.credgen.projectio

import com.loadingbyte.credgen.common.*
import com.loadingbyte.credgen.projectio.service.Account
import com.loadingbyte.credgen.projectio.service.WRITTEN_SERVICE_LINK_EXT
import com.loadingbyte.credgen.projectio.service.writeServiceLink
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.notExists
import kotlin.io.path.writeText
import kotlin.math.max


/** @throws IOException */
fun tryCopyTemplate(destDir: Path, template: Template) {
    tryCopyTemplate(destDir, template, null, null, null)
}

/** @throws IOException */
fun tryCopyTemplate(destDir: Path, template: Template, creditsFormat: SpreadsheetFormat) {
    tryCopyTemplate(destDir, template, creditsFormat, null, null)
}

/** @throws IOException */
fun tryCopyTemplate(destDir: Path, template: Template, creditsAccount: Account, creditsFilename: String) {
    tryCopyTemplate(destDir, template, null, creditsAccount, creditsFilename)
}

class Template(
    val locale: Locale,
    val resolution: Resolution,
    val fps: FPS,
    val timecodeFormat: TimecodeFormat,
    val sample: Boolean
) {
    // We use a heuristic to determine by how much to scale all sizes in the template:
    // For image widths < 3000, we scale by 1. For image widths >= 3000 and < 5000, we scale by 2. And so on...
    val scale: Int = max(1, (resolution.widthPx + 1000) / 2000)
}


private fun tryCopyTemplate(
    destDir: Path,
    template: Template,
    creditsFormat: SpreadsheetFormat?,
    creditsAccount: Account?,
    creditsFilename: String?
) {
    // First try to write the credits file, so that if something goes wrong (which is likely with online services),
    // the project folder just isn't created at all, instead of being half-created.
    if (creditsFormat != null || creditsAccount != null)
        tryCopyCreditsTemplate(destDir, template, creditsFormat, creditsAccount, creditsFilename)
    tryCopyStylingTemplate(destDir, template)
    if (template.sample) {
        tryCopyLogoFile(destDir, "credgenH.svg", "credgen H.svg")
        tryCopyLogoFile(destDir, "credgenV.svg", "credgen V.svg")
    }
}


private fun tryCopyCreditsTemplate(
    destDir: Path,
    template: Template,
    creditsFormat: SpreadsheetFormat?,
    creditsAccount: Account?,
    creditsFilename: String?
) {
    var csv = useResourceStream("/template/credits.csv") { it.bufferedReader().readLines() }
    // If desired, cut off the sample credits and only keep the table header.
    if (!template.sample)
        csv = csv.subList(0, 2)
    val spreadsheetName = l10n("project.template.spreadsheetName", template.locale)
    val spreadsheet = CsvFormat.read(csv.joinToString("\n"), spreadsheetName).map { fillIn(it, template) }
    val look = SpreadsheetLook(
        rowLooks = mapOf(
            0 to SpreadsheetLook.RowLook(height = 140, fontSize = 8, italic = true, wrap = true),
            1 to SpreadsheetLook.RowLook(bold = true, borderBottom = true)
        ),
        colWidths = listOf(45, 45, 15, 15, 25, 15, 40, 20, 25, 25)
    )
    when {
        creditsFormat != null -> {
            val destFile = destDir.resolve("Credits.${creditsFormat.fileExt}")
            if (!destFile.notExists())
                return
            destDir.createDirectoriesSafely()
            creditsFormat.write(destFile, spreadsheet, look)
        }
        creditsAccount != null -> {
            val destFile = destDir.resolve("Credits.$WRITTEN_SERVICE_LINK_EXT")
            if (!destFile.notExists())
                return
            val name = if (creditsAccount.service.uploadNeedsFilename) requireNotNull(creditsFilename) else null
            val link = creditsAccount.upload(name, spreadsheet, look)
            // Uploading the credits file can take some time. If the user cancels in the meantime, the uploader is
            // actually not interrupted. So instead, we detect interruption here and stop project initialization.
            if (Thread.interrupted())
                throw InterruptedException()
            destDir.createDirectoriesSafely()
            writeServiceLink(destFile, link)
        }
        else -> throw IllegalArgumentException()
    }
}


private fun tryCopyStylingTemplate(destDir: Path, template: Template) {
    val file = destDir.resolve(STYLING_FILE_NAME)
    if (file.notExists()) {
        var lines = useResourceStream("/template/styling.toml") { it.bufferedReader().readLines() }
        // If desired, cut off the template where the first sample style declaration starts.
        if (!template.sample)
            lines = lines.subList(0, lines.indexOfFirst { "[[" in it })
        destDir.createDirectoriesSafely()
        file.writeText(fillIn(lines.joinToString("\n"), template))
    }
}


private fun tryCopyLogoFile(destDir: Path, from: String, to: String) {
    val logoFile = destDir.resolve("Logos").resolve(to)
    if (logoFile.notExists()) {
        logoFile.parent.createDirectoriesSafely()
        useResourceStream("/template/$from") { Files.copy(it, logoFile) }
    }
}


private fun fillIn(string: String, template: Template): String = string
    .replace(PLACEHOLDER_REGEX) { match ->
        when (val key = match.groups[1]!!.value) {
            "locale" -> template.locale.toLanguageTag()
            "resolution" -> template.resolution.toString()
            "fps" -> template.fps.toString()
            "timecodeFormat" -> template.timecodeFormat.name
            "subsequentGapFrames" -> template.fps.run { numerator / denominator }.toString()
            "cardRuntimeFrames" -> template.fps.run { 5 * numerator / denominator }.toString()
            "cardFadeFrames" -> template.fps.run { numerator / (2 * denominator) }.toString()
            "scrollPxPerFrame" -> max(1, template.fps.run { 78 * denominator / numerator } * template.scale).toString()
            "projectIO.credits.table.headDesc" -> {
                val styleKw = l10n("projectIO.credits.table.style", template.locale)
                val stylePlaceholder = "[${l10n("ui.styling.letter.name", template.locale)}]"
                val name = l10n("project.template.letterStyleName", template.locale)
                l10n(
                    key,
                    l10nQuoted("{{$styleKw $stylePlaceholder}}", template.locale),
                    stylePlaceholder,
                    l10nQuoted("{{$styleKw}}", template.locale),
                    l10nEnumQuoted("Copyright 2023 {{$styleKw $name}}Thomas Cash", locale = template.locale),
                    locale = template.locale
                )
            }
            "projectIO.credits.table.tailDesc", "projectIO.credits.table.bodyDesc" -> {
                val picKw = l10n("projectIO.credits.table.pic", template.locale)
                val videoKw = l10n("projectIO.credits.table.video", template.locale)
                val filenamePlaceholder = "[${l10n("filename", template.locale)}]"
                l10n(
                    key,
                    l10nQuoted("{{${l10n("projectIO.credits.table.style", template.locale)} …}}", template.locale),
                    "@" + l10n("projectIO.credits.table.head", template.locale),
                    l10nQuoted("{{${l10n("blank", template.locale)}}}", template.locale),
                    l10nQuoted("{{$picKw $filenamePlaceholder}}", template.locale),
                    l10nQuoted("{{$videoKw $filenamePlaceholder}}", template.locale),
                    l10nEnumQuoted(
                        "{{$picKw credgen Logo}}", "{{$videoKw Blooper 3.mov XXL}}",
                        locale = template.locale
                    ),
                    locale = template.locale
                )
            }
            "projectIO.credits.table.vGapDesc" ->
                l10n(key, l10nQuoted("px", template.locale), locale = template.locale)
            "projectIO.credits.table.breakHarmonizationDesc" ->
                l10n(
                    key,
                    l10nQuoted(l10n("projectIO.credits.table.head", template.locale), template.locale),
                    l10nQuoted(l10n("projectIO.credits.table.body", template.locale), template.locale),
                    l10nQuoted(l10n("projectIO.credits.table.tail", template.locale), template.locale),
                    locale = template.locale
                )
            "projectIO.credits.table.spinePosDesc" -> {
                val below = l10n("projectIO.credits.table.below", template.locale)
                val parallel = l10n("projectIO.credits.table.parallel", template.locale)
                val hook = l10n("projectIO.credits.table.hook", template.locale)
                val top = l10n("projectIO.credits.table.top", template.locale)
                val mid = l10n("projectIO.credits.table.middle", template.locale)
                val bot = l10n("projectIO.credits.table.bottom", template.locale)
                l10n(
                    key,
                    l10nQuoted(below, template.locale),
                    l10nQuoted(l10n("projectIO.credits.table.above", template.locale), template.locale),
                    l10nEnumQuoted("-400", "-400 200", "-400 200 $below", locale = template.locale),
                    l10nEnumQuoted("-400", locale = template.locale),
                    l10nQuoted(parallel, template.locale),
                    l10nQuoted(hook, template.locale),
                    l10nEnumQuoted(
                        "-400 $parallel", "$hook 1 $bot-$top", "$hook 1 $top-$top 800", "$hook 2 $bot-$mid 800 100",
                        locale = template.locale
                    ),
                    locale = template.locale
                )
            }
            "projectIO.credits.table.pageRuntimeDesc" ->
                l10n(
                    key,
                    l10nEnumQuoted("00:04:56:23", "XYZ 00:04:56:23", "XYZ", locale = template.locale),
                    locale = template.locale
                )
            "projectIO.credits.table.pageGapDesc" -> {
                val melt = l10n("projectIO.credits.table.melt", template.locale)
                val linear = l10n("project.template.transitionStyleLinear", template.locale)
                l10n(
                    key,
                    l10nQuoted(melt, template.locale),
                    l10nEnumQuoted(
                        "00:04:56:23", "-00:04:56:23", melt, "$melt 00:00:02:00 $linear",
                        locale = template.locale
                    ),
                    locale = template.locale
                )
            }
            else -> l10n(key, template.locale)
        }
    }
    .replace(SCALING_REGEX) { match ->
        val num = match.groups[1]!!.value
        (num.toInt() * template.scale).toString()
    }
    .replace(TIMECODE_REGEX) { match ->
        val num = match.groups[1]!!.value
        val frames = Timecode.Clock(num.toLong(), 1).toFramesCeil(template.fps).frames
        formatTimecode(template.fps, template.timecodeFormat, frames)
    }

private val PLACEHOLDER_REGEX = Regex("\\{([a-zA-Z0-9.]+)}")
private val SCALING_REGEX = Regex("<([0-9]+)>")
private val TIMECODE_REGEX = Regex("\\[([0-9]+)s]")
