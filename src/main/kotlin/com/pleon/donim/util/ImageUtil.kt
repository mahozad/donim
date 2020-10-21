package com.pleon.donim.util

import java.awt.Color.HSBtoRGB
import java.awt.Color.RGBtoHSB
import java.awt.RenderingHints.KEY_INTERPOLATION
import java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR
import java.awt.image.BufferedImage

object ImageUtil {

    fun rotateImage(image: BufferedImage, angle: Double): BufferedImage {
        val rotated = BufferedImage(image.width, image.height, image.type)
        val graphic = rotated.createGraphics()
        graphic.rotate(Math.toRadians(angle), image.width / 2.0, image.height / 2.0)
        graphic.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR)
        graphic.drawImage(image, null, 0, 0)
        graphic.dispose()
        return rotated
    }

    fun tintImage(image: BufferedImage, hueFactor: Double): BufferedImage {
        return tintImage(image, hueFactor.toFloat())
    }

    fun tintImage(image: BufferedImage, hueFactor: Float): BufferedImage {
        for (i in 0 until image.width) {
            for (j in 0 until image.height) {
                val rgb = image.getRGB(i, j)
                val (r, g, b, a) = extractColorComponents(rgb)
                val (hue, sat, bri) = RGBtoHSB(r, g, b, null)
                val new = (a shl 24) + HSBtoRGB(hue + hueFactor, sat, bri)
                image.setRGB(i, j, new)
            }
        }
        return image
    }

    /**
     *  Extracts red, green, blue, alpha in that order.
     */
    private fun extractColorComponents(rgb: Int) = arrayOf(
            rgb shr 16 and 0xff,
            rgb shr 8 and 0xff,
            rgb shr 0 and 0xff,
            rgb shr 24 and 0xff
    )

    // NOTE:
    //  hueRange = startColor.hue - endColor.hue / 360
    //  hueFactor = animationFraction * hueRange
    //  newColor = filter.setHueFactor(hueFactor)

}
