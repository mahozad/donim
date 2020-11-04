package com.pleon.donim.node

import com.pleon.donim.APP_BASE_COLOR
import com.pleon.donim.extension.div
import com.pleon.donim.extension.times
import com.pleon.donim.node.Animatable.AnimationDirection.FORWARD
import com.pleon.donim.node.Animatable.AnimationProperties
import com.pleon.donim.util.createTimer
import javafx.animation.Animation.Status.RUNNING
import javafx.animation.Timeline
import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import javafx.util.Duration
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class CircularProgressBar : Animatable, Canvas() {

    // https://www.youtube.com/watch?v=V2yXGdC98jM
    // https://stackoverflow.com/q/24533556/8583692
    // https://stackoverflow.com/a/38773373/8583692

    private lateinit var animationProperties: AnimationProperties
    private val fraction = SimpleDoubleProperty(0.0)
    private var timer = Timeline()
    private val sliceLength = 4             // in degrees
    private val sliceGap = sliceLength / 2  // in degrees
    private val arcStart = 90               // in degrees
    private var arcEnd = arcStart - 360     // in degrees
    private var color = APP_BASE_COLOR
    private var outerRadius = 0.0
    private var innerRadius = 0.0
    var title: String = ""

    init {
        fraction.addListener { _, _, _ -> tick() }
        val listener = InvalidationListener {
            outerRadius = min(width, height) / 2
            innerRadius = outerRadius * 0.66
            draw()
        }
        widthProperty().addListener(listener)
        heightProperty().addListener(listener)
    }

    override fun isResizable() = true

    private fun draw() {
        graphicsContext2D.clearRect(0.0, 0.0, width, height)
        drawBackgroundBar()
        var sliceStart = arcStart
        while (sliceStart - sliceGap >= arcEnd) {
            drawSlice(sliceStart, color)
            sliceStart -= (sliceLength + sliceGap)
        }
    }

    private fun drawBackgroundBar() {
        graphicsContext2D.lineWidth = outerRadius - innerRadius
        graphicsContext2D.stroke = Color.gray(0.5, 0.2)
        graphicsContext2D.beginPath()
        graphicsContext2D.arc(width / 2, height / 2, (innerRadius + outerRadius) / 2,
                (innerRadius + outerRadius) / 2, 0.0, 360.0)
        graphicsContext2D.closePath()
        graphicsContext2D.stroke()
    }

    private fun drawSlice(startAngle: Int, color: Color) {
        fillLinearGradient(startAngle, color)
        graphicsContext2D.beginPath()
        graphicsContext2D.arc(width / 2, height / 2, outerRadius, outerRadius,
                startAngle.toDouble(), -sliceLength.toDouble())
        graphicsContext2D.arc(width / 2, height / 2, innerRadius, innerRadius,
                (startAngle - sliceLength).toDouble(), sliceLength.toDouble())
        graphicsContext2D.closePath()
        graphicsContext2D.fill()
    }

    private fun fillLinearGradient(startAngle: Int, color: Color) {
        val angle = startAngle + (sliceLength / 2.0)

        fun Double.toRadian() = Math.toRadians(this)
        val startX = width / 2 + cos(angle.toRadian()) * innerRadius
        val startY = height / 2 + sin(angle.toRadian()) * -innerRadius
        val endX = width / 2 + cos(angle.toRadian()) * outerRadius
        val endY = height / 2 + sin(angle.toRadian()) * -outerRadius

        val startColor = Stop(0.0, color)
        val endColor = Stop(1.0, color.darker())

        graphicsContext2D.fill = LinearGradient(startX, startY, endX, endY,
                false, CycleMethod.NO_CYCLE, startColor, endColor)
    }

    override fun resetAnimation(properties: AnimationProperties) {
        animationProperties = properties
        timer.stop()
        timer = createTimer(animationProperties.duration, fraction) { timer.stop() }
        tick()
    }

    override fun startAnimation() {
        timer.play()
    }

    override fun pauseAnimation() {
        timer.pause()
        color = animationProperties.pauseColor
        draw()
    }

    override fun endAnimation(isGraceful: Boolean, graceDuration: Duration) {
        timer.rate = when {
            isGraceful -> animationProperties.duration * (1 - fraction.value) / graceDuration
            else -> animationProperties.duration * (1 - fraction.value) / Duration.ONE
        }
        timer.play() // for when the ending is called while paused
    }

    private fun tick() {
        val hueRange = animationProperties.endColor.hue - animationProperties.startColor.hue
        val hueShift = fraction.value * hueRange
        val backwardEnd = arcStart - 360 + fraction.value * 360
        val forwardEnd = arcStart - fraction.value * 360
        color = if (timer.status != RUNNING) animationProperties.pauseColor else animationProperties.startColor.deriveColor(hueShift, 1.0, 1.0, 1.0)
        arcEnd = if (animationProperties.direction == FORWARD) forwardEnd.toInt() else backwardEnd.toInt()
        draw()
    }
}
