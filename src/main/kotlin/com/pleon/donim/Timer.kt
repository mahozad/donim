package com.pleon.donim

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.util.Duration

class Timer(val duration: Duration, val updateRate: Duration) {

    var isStarted = false
        private set
    private var remainingTimeProperty = ReadOnlyObjectWrapper(duration)
    private val timeline = Timeline()

    fun remainingTimeProperty() = remainingTimeProperty.readOnlyProperty

    fun start() {
        timeline.keyFrames.add(KeyFrame(updateRate, {
            val newTime = remainingTimeProperty.value.subtract(updateRate)
            remainingTimeProperty.set(newTime)
        }))
        timeline.play()
        isStarted = true
    }

    fun stop() {
        timeline.pause()
        isStarted = false
    }

    fun reset() {
        remainingTimeProperty.set(duration)
        timeline.playFromStart()
    }
}
