package com.pleon.donim

import javafx.animation.Animation.Status.RUNNING
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.util.Duration
import javafx.util.Duration.ZERO

/**
 * To change the duration or the updateRate create a new instance of this class.
 */
class Timer(private val totalDuration: Duration,
            private val stepDuration: Duration) {

    val isRunning get() = timeline.status == RUNNING
    private val remainingTimeProperty = ReadOnlyObjectWrapper(totalDuration)
    private val elapsedTimeProperty = ReadOnlyObjectWrapper(ZERO)
    private val timeline = Timeline()

    init {
        timeline.keyFrames.add(KeyFrame(stepDuration, {
            remainingTimeProperty.value -= stepDuration
            elapsedTimeProperty.value += stepDuration
        }))
    }

    fun remainingTimeProperty(): ReadOnlyObjectProperty<Duration> {
        return remainingTimeProperty.readOnlyProperty
    }

    fun elapsedTimeProperty(): ReadOnlyObjectProperty<Duration> {
        return elapsedTimeProperty.readOnlyProperty
    }

    fun start() {
        timeline.cycleCount = totalDuration.length / stepDuration.length
        timeline.play()
    }

    fun stop() {
        timeline.pause()
    }

    fun reset() {
        remainingTimeProperty.value = totalDuration
        elapsedTimeProperty.value = ZERO
        timeline.playFromStart()
    }
}
