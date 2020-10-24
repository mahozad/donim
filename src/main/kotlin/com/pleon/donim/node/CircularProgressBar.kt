package com.pleon.donim.node

import com.pleon.donim.APP_BASE_COLOR
import com.pleon.donim.Animatable
import com.pleon.donim.Animatable.AnimationDirection.FORWARD
import com.pleon.donim.Animatable.AnimationProperties
import com.pleon.donim.Timer
import com.pleon.donim.div
import javafx.beans.InvalidationListener
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

    private lateinit var timer: Timer
    private lateinit var animationProperties: AnimationProperties
    private val sliceLength = 4             // in degrees
    private val sliceGap = sliceLength / 2  // in degrees
    private val arcStart = 90               // in degrees
    private var arcEnd = arcStart - 360     // in degrees
    private var color = APP_BASE_COLOR
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

    override fun startAnimation() {
        if (!this::timer.isInitialized) createTimer()
        timer.start()
    }

    override fun pauseAnimation() {
        if (this::timer.isInitialized) timer.stop()
        color = animationProperties.pauseColor
        draw()
    }

    override fun resetAnimation(properties: AnimationProperties) {
        animationProperties = properties
        // TODO: Also remove listeners from timer properties to avoid memory leak
        if (this::timer.isInitialized) timer.stop()
        createTimer()
        tick(Duration.ZERO)
    }

    override fun endAnimation(onEnd: () -> Unit, graceful: Boolean, graceDuration: Duration) {
        val wasRunning = timer.isRunning
        // TODO: Also remove listeners from timer properties to avoid memory leak
        timer.stop()
        runGraceAnimation(graceDuration, wasRunning, onEnd)
    }

    private fun tick(elapsedTime: Duration) {
        val fraction = elapsedTime / animationProperties.duration
        val fractionOfRemaining = fraction * (1 - animationProperties.initialProgress)
        val hueRange = animationProperties.endColor.hue - animationProperties.startColor.hue
        val hueShift = (animationProperties.initialProgress + fractionOfRemaining) * hueRange
        color = animationProperties.startColor.deriveColor(hueShift, 1.0, 1.0, 1.0)
        val backwardEnd = arcStart - ((1 - animationProperties.initialProgress) * 360) + fractionOfRemaining * 360
        val forwardEnd = arcStart - (animationProperties.initialProgress * 360) - fractionOfRemaining * 360
        arcEnd = if (animationProperties.direction == FORWARD) forwardEnd.toInt() else backwardEnd.toInt()
        draw()
    }

    private fun createTimer(onEnd: () -> Unit = {}) {
        timer = Timer(animationProperties.duration, Duration.millis(30.0), onEnd)
        timer.elapsedTimeProperty().addListener { _, _, elapsedTime -> tick(elapsedTime) }
    }

    private fun runGraceAnimation(graceDuration: Duration, wasRunning: Boolean, onEnd: () -> Unit) {
        val startColor = if (wasRunning) animationProperties.startColor else APP_BASE_COLOR
        val endColor = if (wasRunning) animationProperties.endColor else APP_BASE_COLOR
        val progress = timer.elapsedTimeProperty().value / animationProperties.duration
        animationProperties = AnimationProperties(
                graceDuration,
                animationProperties.direction,
                startColor, endColor,
                initialProgress = progress)
        createTimer(onEnd)
        timer.start()
    }
}
