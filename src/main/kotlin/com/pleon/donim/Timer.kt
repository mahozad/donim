package com.pleon.donim

import javafx.animation.Animation.Status.RUNNING
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.util.Duration

class Timer(private val duration: Duration, private val updateRate: Duration) {

    val isStarted get() = timeline.status == RUNNING
    private var remainingTimeProperty = ReadOnlyObjectWrapper(duration)
    private val timeline = Timeline()

    init {
        timeline.keyFrames.add(KeyFrame(updateRate, {
            remainingTimeProperty.set(remainingTimeProperty.value - updateRate)
        }))
    }

    fun remainingTimeProperty() = remainingTimeProperty.readOnlyProperty

    fun start() {
        timeline.cycleCount = duration.toMillis().toInt() / updateRate.toMillis().toInt()
        timeline.play()
    }

    fun stop() {
        timeline.pause()
    }

    fun reset() {
        remainingTimeProperty.set(duration)
        timeline.playFromStart()
    }
}

operator fun Duration.plus(other: Duration): Duration = add(other)
operator fun Duration.minus(other: Duration): Duration = subtract(other)
operator fun Duration.times(num: Int): Duration = multiply(num.toDouble())
operator fun Duration.times(num: Double): Duration = multiply(num)
operator fun Duration.div(num: Int): Duration = divide(num.toDouble())
operator fun Duration.div(num: Double): Duration = divide(num)
