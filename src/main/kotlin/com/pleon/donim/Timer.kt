package com.pleon.donim

import javafx.beans.property.ReadOnlyObjectWrapper
import java.time.Duration

class Timer(private val duration: Duration) {

    var isStarted = false
        private set
    private var remainingTimeProperty = ReadOnlyObjectWrapper<Duration>()
    private var remainingTime = duration

    fun remainingTimeProperty() = remainingTimeProperty.readOnlyProperty

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
