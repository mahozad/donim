package com.pleon.donim

import javafx.animation.Animation.Status.RUNNING
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.util.Duration

class Timer(private val duration: Duration, private val updateRate: Duration) {

    val isRunning get() = timeline.status == RUNNING
    private val remainingTimeProperty = ReadOnlyObjectWrapper(duration)
    private val timeline = Timeline()

    init {
        timeline.keyFrames.add(KeyFrame(updateRate, { remainingTimeProperty.value -= updateRate }))
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
