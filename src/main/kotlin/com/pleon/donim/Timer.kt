package com.pleon.donim

import javafx.animation.Animation.Status.RUNNING
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.util.Duration

class Timer(duration: Duration, updateRate: Duration) {

    val isStarted get() = timeline.status == RUNNING
    private var remainingTimeProperty = ReadOnlyObjectWrapper(duration)
    private val timeline = Timeline()

    init {
        timeline.keyFrames.add(KeyFrame(updateRate, {
            remainingTimeProperty.set(timeline.totalDuration - timeline.currentTime)
            println(remainingTimeProperty.value)
        }))
    }

    fun remainingTimeProperty() = remainingTimeProperty.readOnlyProperty

    fun start() {
        timeline.play()
    }

    fun stop() {
        timeline.pause()
    }

    fun reset() {
        timeline.playFromStart()
    }
}

operator fun Duration.minus(other: Duration): Duration = subtract(other)
operator fun Duration.plus(other: Duration): Duration = add(other)
