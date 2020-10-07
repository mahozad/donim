package com.pleon.donim

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.util.Duration

class Timer(private val duration: Duration) {

    var isStarted = false
        private set
    private var remainingTimeProperty = ReadOnlyObjectWrapper(duration)
    private val timeline = Timeline()

    fun remainingTimeProperty() = remainingTimeProperty.readOnlyProperty

    fun start() {
        timeline.keyFrames.add(KeyFrame(Duration.seconds(1.0), {
            val newTime = remainingTimeProperty.value.subtract(Duration.seconds(1.0))
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
