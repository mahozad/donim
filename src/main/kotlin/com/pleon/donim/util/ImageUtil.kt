package com.pleon.donim.util

import com.jhlabs.image.HSBAdjustFilter
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
}
