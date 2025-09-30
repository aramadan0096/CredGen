package com.loadingbyte.credgen

import com.github.weisj.jsvg.parser.LoaderContext
import com.github.weisj.jsvg.parser.SVGLoader
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.stream.FileImageOutputStream


class Logo(file: File) {

    private val svg = requireNotNull(
        file.inputStream().use { SVGLoader().load(it, null, LoaderContext.createDefault()) }
    ) { "Failed to load SVG: $file" }

    fun rasterize(size: Int, margin: Double = 0.0, imageType: Int = BufferedImage.TYPE_INT_ARGB): BufferedImage {
        val img = BufferedImage(size, size, imageType)
        val g2 = img.createGraphics()
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        val transl = margin * size
        val scaling = (size * (1 - 2 * margin)) / svg.size().width
        g2.translate(transl, transl)
        g2.scale(scaling, scaling)
        svg.render(null, g2)
        g2.dispose()
        return img
    }

    fun transcode(vararg sizes: Int, margin: Double = 0.0, file: File) {
        // ICO encoder (TwelveMonkeys) only supports TYPE_4BYTE_ABGR. Windows/icon tooling previously displayed
        // swapped red/blue (#e64223 -> #2342e6). That indicates channel interpretation mismatch. We keep the
        // required ABGR type but pre-swap R/B so the final displayed color is correct in environments that misread it.
        val isIco = file.extension == "ico"
        val imageType = if (isIco) BufferedImage.TYPE_4BYTE_ABGR else BufferedImage.TYPE_INT_ARGB
        val images = sizes.map { size ->
            val img = rasterize(size, margin, imageType)
            if (isIco) {
                // Swap R and B for each pixel (excluding fully transparent) to compensate display swap.
                for (y in 0 until img.height) for (x in 0 until img.width) {
                    val argb = img.getRGB(x, y)
                    val a = argb ushr 24 and 0xFF
                    if (a == 0) continue
                    val r = argb ushr 16 and 0xFF
                    val g = argb ushr 8 and 0xFF
                    val b = argb and 0xFF
                    val swapped = (a shl 24) or (b shl 16) or (g shl 8) or r
                    if (swapped != argb) img.setRGB(x, y, swapped)
                }
            }
            img
        }

        file.delete()
        file.parentFile.mkdirs()
        FileImageOutputStream(file).use { stream ->
            val writer = ImageIO.getImageWritersBySuffix(file.extension).next()
            writer.output = stream
            if (images.size == 1)
                writer.write(images[0])
            else {
                writer.prepareWriteSequence(null)
                for (image in images)
                    writer.writeToSequence(IIOImage(image, null, null), null)
                writer.endWriteSequence()
            }
        }
    }

}
