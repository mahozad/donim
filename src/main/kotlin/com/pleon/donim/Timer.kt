package com.pleon.donim

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.util.Duration

class Timer(private val duration: Duration) {

    var isStarted = false
        private set
    private var remainingTimeProperty = ReadOnlyObjectWrapper<Duration>()
    private val timeline = Timeline()
    private var remainingTime = duration

    fun remainingTimeProperty() = remainingTimeProperty.readOnlyProperty

    fun start() {
        timeline.keyFrames.add(KeyFrame(Duration.seconds(1.0), {
            remainingTime = remainingTime.subtract(Duration.seconds(1.0))
            remainingTimeProperty.set(remainingTime)
        }))
        timeline.play()
        isStarted = true
    }

    fun stop() {
        timeline.pause()
        isStarted = false
    }

    fun reset() {
        remainingTime = duration
        remainingTimeProperty.set(remainingTime)
        timeline.playFromStart()
    }
}
