package com.pleon.donim.util

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.stage.Stage
import javafx.util.Duration

object AnimationUtil {

    private const val STEP = 0.01

    enum class MoveDirection {
        NONE, BOTTOM, BOTTOM_RIGHT
    }

    fun fadeOut(stage: Stage, moveDirection: MoveDirection, onFinished: EventHandler<ActionEvent>) {
        val timeline = Timeline()
        timeline.keyFrames.add(KeyFrame(Duration.millis(1.0), object : EventHandler<ActionEvent?> {
            private var opacity = 1.0
            override fun handle(event: ActionEvent?) {
                opacity = (opacity - STEP).coerceAtLeast(0.0)
                stage.opacity = opacity
                if (moveDirection == MoveDirection.BOTTOM) {
                    stage.y += 0.15
                } else if (moveDirection == MoveDirection.BOTTOM_RIGHT) {
                    stage.y += 0.5
                    stage.x += 0.5
                }
            }
        }))
        timeline.cycleCount = (1 / STEP).toInt()
        timeline.onFinished = onFinished
        timeline.play()
    }
}
