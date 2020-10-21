package com.pleon.donim.util

import com.jhlabs.image.HSBAdjustFilter
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
        val hsbFilter = HSBAdjustFilter().apply { setHFactor(hueFactor.toFloat()) }
        val destination = hsbFilter.createCompatibleDestImage(image, null)
        return hsbFilter.filter(image, destination)
    }

    // /**
    //  * This does not use external libraries like jhlabs (which is not maintained and id about 300 KiB)
    //  */
    // fun tintImage2(image: BufferedImage, hueFactor: Double): BufferedImage {
    //     for (i in 0 until image.width) {
    //         for (j in 0 until image.height) {
    //             val originalColor = java.awt.Color(image.getRGB(i, j), true)
    //             val originalColorFX = Color(originalColor.red.toDouble() / 255.0, originalColor.green.toDouble() / 255.0, originalColor.blue.toDouble() / 255.0, originalColor.alpha / 255.0)
    //             val newColorFX =/* originalColorFX.interpolate(AQUA, 0.5) */originalColorFX.deriveColor(hueFactor * 360, 1.0, 1.0, 1.0)
    //             val newColor = java.awt.Color(newColorFX.red.toFloat(), newColorFX.green.toFloat(), newColorFX.blue.toFloat(), newColorFX.opacity.toFloat())
    //             // val rgb = ((newColorFX.opacity * 255).toInt() shl 24) + ((newColorFX.red * 255).toInt() shl 16) + ((newColorFX.green * 255).toInt() shl 8) + (newColorFX.blue * 255).toInt()
    //             image.setRGB(i, j, newColor.rgb)
    //         }
    //     }
    //     return image
    // }

    fun tintImage3(image: BufferedImage, hueFactor: Float): BufferedImage {
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
