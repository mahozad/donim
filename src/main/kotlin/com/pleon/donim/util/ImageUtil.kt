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
    fun BufferedImage.tint(hueShift: Double): BufferedImage {
        for (i in 0 until width) {
            for (j in 0 until height) {
                val rgb = getRGB(i, j)
                val (r, g, b, a) = getColorComponents(rgb)
                val (h, s, v) = RGBtoHSB(r, g, b, null)
                val new = (a shl 24) + HSBtoRGB(h + hueShift.toFloat(), s, v)
                setRGB(i, j, new)
            }
        }
        return this
    }

    private fun getColorComponents(rgb: Int) = arrayOf(
            /* r */ rgb shr 16 and 0xff,
            /* g */ rgb shr 8 and 0xff,
            /* b */ rgb shr 0 and 0xff,
            /* a */ (rgb shr 24 and 0xff) - 0xff
    )
}
