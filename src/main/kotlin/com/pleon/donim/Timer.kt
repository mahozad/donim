package com.pleon.donim

import javafx.animation.Animation.Status.RUNNING
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.util.Duration

class Timer(val duration: Duration, val updateRate: Duration) {

    val isStarted get() = timeline.status == RUNNING
    private var remainingTimeProperty = ReadOnlyObjectWrapper(duration)
    private val timeline = Timeline()

    fun remainingTimeProperty() = remainingTimeProperty.readOnlyProperty

    fun start() {
        timeline.keyFrames.add(KeyFrame(updateRate, {
            val newTime = remainingTimeProperty.value.subtract(updateRate)
            remainingTimeProperty.set(newTime)
        }))
        timeline.play()
    }

    fun stop() {
        timeline.pause()
    }

    fun reset() {
        timeline.playFromStart()
    }

    operator fun Duration.minus(other: Duration): Duration = subtract(other)
}
