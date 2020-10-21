package com.pleon.donim.util

import java.awt.Color.HSBtoRGB
import java.awt.Color.RGBtoHSB
import java.awt.RenderingHints.KEY_INTERPOLATION
import java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR
import java.awt.image.BufferedImage

object ImageUtil {

    fun BufferedImage.rotate(angle: Double): BufferedImage {
        val rotated = BufferedImage(width, height, type)
        val graphic = rotated.createGraphics()
        graphic.rotate(Math.toRadians(angle), width / 2.0, height / 2.0)
        graphic.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR)
        graphic.drawImage(this, null, 0, 0)
        graphic.dispose()
        return rotated
    }

    // NOTE (to calculate hueFactor to be between 0-1):
    //  hueRange = startColor.hue - endColor.hue / 360
    //  hueFactor = animationFraction * hueRange
    fun BufferedImage.tint(hueFactor: Double): BufferedImage {
        for (i in 0 until width) {
            for (j in 0 until height) {
                val rgb = getRGB(i, j)
                val (r, g, b, a) = extractColorComponents(rgb)
                val (hue, sat, bri) = RGBtoHSB(r, g, b, null)
                val new = (a shl 24) + HSBtoRGB(hue + hueFactor.toFloat(), sat, bri)
                setRGB(i, j, new)
            }
        }
        return this
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
}
