package com.pleon.donim

import javafx.beans.property.SimpleObjectProperty
import java.time.Duration

class Timer(private val duration: Duration) {

    var isStarted = false
    var remainingTimeProperty = SimpleObjectProperty<Duration>()
        private set
    private var remainingTime = duration

    fun start() {
        isStarted = true
        remainingTime = remainingTime.minusSeconds(1)
        remainingTimeProperty.set(remainingTime)
    }

    fun stop() {
        isStarted = false
    }

    fun reset() {
        remainingTimeProperty.set(duration.minusMillis(1))
    }
}
