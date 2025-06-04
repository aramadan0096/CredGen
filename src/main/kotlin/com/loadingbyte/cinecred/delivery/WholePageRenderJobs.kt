package com.loadingbyte.cinecred.delivery

import com.loadingbyte.cinecred.common.*
import com.loadingbyte.cinecred.delivery.RenderFormat.Config
import com.loadingbyte.cinecred.delivery.RenderFormat.Config.Assortment.Companion.choice
import com.loadingbyte.cinecred.delivery.RenderFormat.Config.Assortment.Companion.fixed
import com.loadingbyte.cinecred.delivery.RenderFormat.PDFProfile.*
import com.loadingbyte.cinecred.delivery.RenderFormat.Property.Companion.DEPTH
import com.loadingbyte.cinecred.delivery.RenderFormat.Property.Companion.DPX_COMPRESSION
import com.loadingbyte.cinecred.delivery.RenderFormat.Property.Companion.EXR_COMPRESSION
import com.loadingbyte.cinecred.delivery.RenderFormat.Property.Companion.HDR
import com.loadingbyte.cinecred.delivery.RenderFormat.Property.Companion.PDF_PROFILE
import com.loadingbyte.cinecred.delivery.RenderFormat.Property.Companion.PRIMARIES
import com.loadingbyte.cinecred.delivery.RenderFormat.Property.Companion.SPATIAL_SCALING_LOG2
import com.loadingbyte.cinecred.delivery.RenderFormat.Property.Companion.TIFF_COMPRESSION
import com.loadingbyte.cinecred.delivery.RenderFormat.Property.Companion.TRANSFER
import com.loadingbyte.cinecred.delivery.RenderFormat.Property.Companion.TRANSPARENCY
import com.loadingbyte.cinecred.delivery.RenderFormat.Transparency.*
import com.loadingbyte.cinecred.imaging.*
import com.loadingbyte.cinecred.imaging.Bitmap.PixelFormat.Family.GRAY
import com.loadingbyte.cinecred.imaging.Bitmap.PixelFormat.Family.RGB
import com.loadingbyte.cinecred.imaging.ColorSpace.Primaries.Companion.BT709
import com.loadingbyte.cinecred.imaging.ColorSpace.Transfer.Companion.BLENDING
import com.loadingbyte.cinecred.imaging.ColorSpace.Transfer.Companion.LINEAR
import com.loadingbyte.cinecred.imaging.ColorSpace.Transfer.Companion.SRGB
import com.loadingbyte.cinecred.imaging.DeferredImage.Companion.STATIC
import com.loadingbyte.cinecred.imaging.DeferredImage.Companion.TAPES
import com.loadingbyte.cinecred.imaging.Y.Companion.toY
import com.loadingbyte.cinecred.project.Styling
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.graphics.color.PDOutputIntent
import org.bytedeco.ffmpeg.global.avutil.AV_PIX_FMT_GRAYF32
import java.io.ByteArrayInputStream
import java.nio.file.Path
import java.util.*
import javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI
import javax.xml.XMLConstants.XML_NS_URI
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import kotlin.io.path.bufferedWriter
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.math.pow
import kotlin.math.roundToInt


class WholePageSequenceRenderJob private constructor(
    private val format: RenderFormat,
    private val config: Config,
    private val styling: Styling,
    private val pageDefImages: List<DeferredImage>,
    private val dir: Path,
    private val filenamePattern: String
) : RenderJob {

    override val prefix: Path
        get() = dir

    override fun render(progressCallback: (Int) -> Unit) {
        if (dir.exists())
            dir.cleanDirectory()
        dir.createDirectoriesSafely()

        val ground = config[TRANSPARENCY] == GROUNDED
        val embedAlpha = config[TRANSPARENCY] == TRANSPARENT
        val matte = config[TRANSPARENCY] == MATTE
        val family = if (matte) GRAY else RGB
        val spatialScaling = 2.0.pow(config[SPATIAL_SCALING_LOG2])
        val colorSpace = if (matte) null else ColorSpace.of(config[PRIMARIES], config[TRANSFER])
        val ceiling = if (config.getOrDefault(HDR) || colorSpace?.transfer?.isHDR == true) null else 1f
        val global = styling.global

        val bitmapWriter = when (format) {
            PNG -> BitmapWriter.PNG(family, embedAlpha, colorSpace, config[DEPTH])
            TIFF -> BitmapWriter.TIFF(family, embedAlpha, colorSpace, config[DEPTH], config[TIFF_COMPRESSION])
            DPX -> BitmapWriter.DPX(family, embedAlpha, colorSpace, config[DEPTH], config[DPX_COMPRESSION])
            EXR -> BitmapWriter.EXR(family, embedAlpha, colorSpace?.primaries, config[DEPTH], config[EXR_COMPRESSION])
            else -> null
        }

        for ((idx, unscaledPageDefImage) in pageDefImages.withIndex()) {
            if (Thread.interrupted()) return

            val pageDefImage = unscaledPageDefImage.copy(universeScaling = spatialScaling)
            val pageWidth = pageDefImage.width.roundToInt()
            val pageHeight = pageDefImage.height.resolve().roundToInt()
            val pageFile = dir.resolve(filenamePattern.format(idx + 1))

            when (format) {
                PNG, TIFF, DPX, EXR -> {
                    val res = Resolution(pageWidth, pageHeight)
                    val rep = Canvas.compatibleRepresentation(ColorSpace.of(colorSpace?.primaries ?: BT709, BLENDING))
                    Bitmap.allocate(Bitmap.Spec(res, rep)).use { bitmap ->
                        Canvas.forBitmap(bitmap, ceiling).use { canvas ->
                            if (ground) canvas.fill(Canvas.Shader.Solid(global.grounding)) else bitmap.zero()
                            pageDefImage.materialize(canvas, cache = null, layers = listOf(STATIC, TAPES))
                        }
                        if (!matte)
                            bitmapWriter!!.convertAndWrite(bitmap, pageFile, promiseOpaque = !embedAlpha)
                        else {
                            val matteRep = Bitmap.Representation(Bitmap.PixelFormat.of(AV_PIX_FMT_GRAYF32))
                            Bitmap.allocate(Bitmap.Spec(res, matteRep)).use { matteBitmap ->
                                matteBitmap.blitComponent(bitmap, 3, 0)
                                bitmapWriter!!.convertAndWrite(matteBitmap, pageFile, promiseOpaque = true)
                            }
                        }
                    }
                }
                SVG -> {
                    val doc = DocumentBuilderFactory.newNSInstance().newDocumentBuilder().domImplementation
                        .createDocument(SVG_NS_URI, "svg", null)
                    val svg = doc.documentElement

                    svg.setAttributeNS(XMLNS_ATTRIBUTE_NS_URI, "xmlns:xlink", XLINK_NS_URI)
                    svg.setAttributeNS(XML_NS_URI, "xml:lang", global.locale.toLanguageTag())
                    svg.setAttribute("width", pageWidth.toString())
                    svg.setAttribute("height", pageHeight.toString())
                    svg.setAttribute("viewBox", "0 0 $pageWidth $pageHeight")
                    if (ground)
                        svg.appendChild(doc.createElementNS(SVG_NS_URI, "rect").apply {
                            setAttribute("width", pageWidth.toString())
                            setAttribute("height", pageHeight.toString())
                            setAttribute("fill", global.grounding.toSRGBHexString())
                        })
                    pageDefImage.materialize(svg, listOf(STATIC, TAPES))

                    pageFile.bufferedWriter().use { writer ->
                        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                        // Note: We have verified that the XML writer used a bit further down writes system-dependent
                        // line breaks, so we do too.
                        writer.newLine()
                        writer.write("<!-- Created with CredGen $VERSION -->")
                        // The XML writer sadly doesn't put a newline after a comment placed before the root element.
                        // The simplest solution is to just write this comment ourselves.
                        writer.newLine()
                        TransformerFactory.newInstance().newTransformer().apply {
                            setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
                            setOutputProperty(OutputKeys.INDENT, "yes")
                            setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
                        }.transform(DOMSource(doc), StreamResult(writer))
                    }
                }
            }

            progressCallback(MAX_RENDER_PROGRESS * (idx + 1) / pageDefImages.size)
        }
    }


    companion object {

        private val PNG = Format(
            "png",
            transparencyTimesColorSpace(default = SRGB) * choice(DEPTH, 8, 16)
        )
        private val TIFF = Format(
            "tiff",
            transparencyTimesColorSpace(default = SRGB) * choice(DEPTH, 8, 16) * choice(TIFF_COMPRESSION)
        )
        private val DPX = Format(
            "dpx",
            transparencyTimesColorSpace() * choice(DEPTH, 8, 10, 12, 16) * choice(DPX_COMPRESSION) -
                    fixed(DEPTH, 10) * fixed(TRANSPARENCY, TRANSPARENT)
        )
        private val EXR = Format(
            "exr",
            choice(DEPTH, 16, 32, default = 32) * choice(EXR_COMPRESSION) * (
                    choice(TRANSPARENCY, GROUNDED, TRANSPARENT) * choice(PRIMARIES) * fixed(TRANSFER, LINEAR)
                            * choice(HDR)
                            + fixed(TRANSPARENCY, MATTE)
                    )
        )
        private val SVG = Format(
            "svg",
            choice(TRANSPARENCY, GROUNDED, TRANSPARENT) * fixed(PRIMARIES, BT709) * fixed(TRANSFER, SRGB)
        )

        val FORMATS = listOf<RenderFormat>(PNG, TIFF, DPX, EXR, SVG)

        private fun transparencyTimesColorSpace(default: ColorSpace.Transfer = TRANSFER.standardDefault) =
            choice(TRANSPARENCY, GROUNDED, TRANSPARENT) * choice(PRIMARIES) * choice(TRANSFER, default = default) +
                    fixed(TRANSPARENCY, MATTE)

    }


    private class Format(fileExt: String, configAssortment: Config.Assortment) : RenderFormat(
        fileExt.uppercase(), auxLabel = null, fileSeq = true, setOf(fileExt), fileExt,
        configAssortment * choice(SPATIAL_SCALING_LOG2)
    ) {
        override fun createRenderJob(
            config: Config,
            sliders: Sliders,
            styling: Styling,
            pageDefImages: List<DeferredImage>?,
            video: DeferredVideo?,
            fileOrDir: Path,
            filenamePattern: String?
        ) = WholePageSequenceRenderJob(this, config, styling, pageDefImages!!, fileOrDir, filenamePattern!!)
    }

}


class WholePagePDFRenderJob private constructor(
    private val config: Config,
    private val styling: Styling,
    private val pageDefImages: List<DeferredImage>,
    private val file: Path
) : RenderJob {

    override val prefix: Path
        get() = file

    override fun render(progressCallback: (Int) -> Unit) {
        file.deleteIfExists()
        file.parent.createDirectoriesSafely()

        val ground = config[TRANSPARENCY] == GROUNDED
        val spatialScaling = 2.0.pow(config[SPATIAL_SCALING_LOG2])
        val profile = config[PDF_PROFILE]
        val lossy = profile == LOSSY_VECTORSVG || profile == LOSSY_RASTERSVG
        val rasterizeSVGs = profile == LOSSY_RASTERSVG || profile == LOSSLESS_RASTERSVG
        val global = styling.global

        // We blend in sRGB because (a) this mirrors SVG, (b) it's most widely supported, and (c) sRGB is very close to
        // our actual blending transfer characteristics of pure gamma 2.2.
        val colorSpace = ColorSpace.SRGB

        val pdfDoc = PDDocument()

        // We're embedding OpenType fonts, which requires PDF 1.6.
        pdfDoc.version = 1.6f
        pdfDoc.documentInformation.apply {
            creator = "CredGen $VERSION"
            creationDate = Calendar.getInstance()
        }
        pdfDoc.documentCatalog.language = global.locale.toLanguageTag()

        // Add an output intent with the blending color space.
        val iccBytes = ICCProfile.of(colorSpace).bytes
        pdfDoc.documentCatalog.addOutputIntent(PDOutputIntent(pdfDoc, ByteArrayInputStream(iccBytes)).apply {
            val id = "sRGB IEC61966-2.1"
            outputConditionIdentifier = id
            info = id
        })

        for ((idx, unscaledPageDefImage) in pageDefImages.withIndex()) {
            if (Thread.interrupted()) return

            val page = unscaledPageDefImage.copy(universeScaling = spatialScaling)

            val pdfPage = PDPage(PDRectangle(page.width.toFloat(), page.height.resolve().toFloat()))
            pdfDoc.addPage(pdfPage)

            PDPageContentStream(pdfDoc, pdfPage).use { cs ->
                // Let the backend draw the grounding so that it takes care of all the color space stuff.
                DeferredImage(page.width, page.height).apply {
                    if (ground)
                        drawRect(global.grounding, 0.0, 0.0.toY(), page.width, page.height, fill = true)
                    drawDeferredImage(page, 0.0, 0.0.toY())
                }.materialize(pdfDoc, pdfPage, cs, colorSpace, lossy, lossy, rasterizeSVGs, listOf(STATIC, TAPES))
            }

            progressCallback(MAX_RENDER_PROGRESS * (idx + 1) / pageDefImages.size)
        }

        pdfDoc.save(file.toFile())
        pdfDoc.close()
    }


    private class Format : RenderFormat(
        "PDF", auxLabel = null, fileSeq = false, setOf("pdf"), "pdf",
        choice(TRANSPARENCY, GROUNDED, TRANSPARENT) * choice(SPATIAL_SCALING_LOG2) *
                fixed(PRIMARIES, BT709) * fixed(TRANSFER, SRGB) * choice(PDF_PROFILE)
    ) {
        override fun createRenderJob(
            config: Config,
            sliders: Sliders,
            styling: Styling,
            pageDefImages: List<DeferredImage>?,
            video: DeferredVideo?,
            fileOrDir: Path,
            filenamePattern: String?
        ) = WholePagePDFRenderJob(config, styling, pageDefImages!!, fileOrDir)
    }


    companion object {
        val FORMATS = listOf<RenderFormat>(Format())
    }

}
