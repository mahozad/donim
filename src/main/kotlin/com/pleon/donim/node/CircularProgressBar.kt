package com.pleon.donim.node

import javafx.beans.InvalidationListener
import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import javafx.scene.paint.Color.hsb
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class CircularProgressBar : Canvas() {

    // https://www.youtube.com/watch?v=V2yXGdC98jM
    // https://stackoverflow.com/q/24533556/8583692

    private val sliceLength = 2             // in degrees
    private val sliceGap = sliceLength / 2  // in degrees
    private val origin = 90                 // in degrees
    private var end = origin - 360          // in degrees
    private var baseColor = hsb(0.0, 0.85, 1.0)
    private val colorFactor = sliceLength / 2.0
    private var outerRadius = 0.0
    private var innerRadius = 0.0
    var title: String = ""

    init {
        val listener = InvalidationListener {
            outerRadius = min(width, height) / 2
            innerRadius = outerRadius * 0.66
            draw()
        }
        widthProperty().addListener(listener)
        heightProperty().addListener(listener)

        // addEventHandler(MouseEvent.MOUSE_CLICKED,event -> {});
    }

    override fun isResizable() = true

    private fun draw() {
        graphicsContext2D.clearRect(0.0, 0.0, width, height)
        drawBackgroundBar()
        var start = origin
        while (start - sliceLength - sliceGap >= end) {
            val hue = (origin - end) / (sliceLength + sliceGap) * colorFactor
            val color = baseColor.deriveColor(hue, 1.0, 1.0, 1.0)
            drawSector(start, color)
            start -= sliceLength + sliceGap
        }
        // for (double start = origin, i = 0; start > end + sliceGap ; start -= sliceLength + sliceGap, i++) {
        //     Color color = baseColor.deriveColor(i * colorFactor, 1, 1, 1);
        //     drawSector(gc, start, color);
        // }
    }

    private fun drawBackgroundBar() {
        graphicsContext2D.lineWidth = outerRadius - innerRadius
        graphicsContext2D.stroke = Color.gray(0.5, 0.2)
        graphicsContext2D.beginPath()
        graphicsContext2D.arc(width / 2, height / 2, (innerRadius + outerRadius) / 2,
                (innerRadius + outerRadius) / 2, 0.0, 360.0)
        graphicsContext2D.closePath()
        graphicsContext2D.stroke()
        // gc.applyEffect(new DropShadow(2, 1, 1, Color.gray(0.2)));
    }

    private fun drawSector(startAngle: Int, color: Color) {
        fillLinearGradient(startAngle, color)
        graphicsContext2D.beginPath()
        // gc.moveTo(100, 100);
        graphicsContext2D.arc(width / 2, height / 2, outerRadius, outerRadius,
                startAngle.toDouble(), -sliceLength.toDouble())
        graphicsContext2D.arc(width / 2, height / 2, innerRadius, innerRadius,
                (startAngle - sliceLength).toDouble(), sliceLength.toDouble())
        graphicsContext2D.closePath()
        graphicsContext2D.fill()
    }

    private fun fillLinearGradient(startAngle: Int, color: Color) {
        val angle = startAngle + (sliceLength / 2.0)

        val startX = width / 2 + cos(angle.toRadian()) * innerRadius
        val startY = height / 2 + sin(angle.toRadian()) * -innerRadius
        val endX = width / 2 + cos(angle.toRadian()) * outerRadius
        val endY = height / 2 + sin(angle.toRadian()) * -outerRadius

        val startColor = Stop(0.0, color)
        val endColor = Stop(1.0, color.darker())

        graphicsContext2D.fill = LinearGradient(startX, startY, endX, endY,
                false, CycleMethod.NO_CYCLE, startColor, endColor)
    }

    private fun Double.toRadian() = Math.toRadians(this)

    fun tick(percent: Double, color: Color) {
        baseColor = color
        end = (-360 * percent).toInt() + origin
        draw()
    }
}
