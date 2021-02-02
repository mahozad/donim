package ir.mahozad.donim.util

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import kotlin.test.assertEquals

class ExtensionsTest {

    lateinit var image: BufferedImage

    @BeforeEach
    fun setup() {
        image = ImageIO.read(javaClass.getResource("/image.png"))
    }

    @Test
    fun `hueShift 0 should return the same color`() {
        val pixel = image.getRGB(0, 0)
        val result = image.tint(0.0)
        assertEquals(pixel, result.getRGB(0, 0))
    }
}
