package com.pleon.chopchop.util

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.stage.Stage
import javafx.util.Duration

object AnimationUtil {

    private const val STEP = 0.01

    fun fadeOut(stage: Stage, onFinished: EventHandler<ActionEvent>) {
        val timeline = Timeline()
        timeline.keyFrames.add(KeyFrame(Duration.millis(1.0), object : EventHandler<ActionEvent?> {
            private var opacity = 1.0
            override fun handle(event: ActionEvent?) {
                opacity = (opacity - STEP).coerceAtLeast(0.0)
                stage.opacity = opacity
            }
        }))
        timeline.cycleCount = (1 / STEP).toInt()
        timeline.onFinished = onFinished
        timeline.play()
    }
}
