package com.pleon.donim.util

import com.pleon.donim.util.AnimationUtil.FadeMode.IN
import javafx.animation.Interpolator
import javafx.animation.KeyFrame
import javafx.animation.RotateTransition
import javafx.animation.Timeline
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.util.Duration

object AnimationUtil {

    private const val STEP = 0.01

    enum class MoveDirection {
        NONE, BOTTOM, BOTTOM_RIGHT
    }

    enum class FadeMode {
        IN, OUT
    }

    fun fade(fadeMode: FadeMode, node: Node, moveDirection: MoveDirection,
             duration: Duration, delay: Duration, onFinished: EventHandler<ActionEvent>) {

        val timeline = Timeline()
        val frameDuration = duration.multiply(STEP)
        timeline.keyFrames.add(KeyFrame(frameDuration, object : EventHandler<ActionEvent?> {
            private var opacity = if (fadeMode == IN) 0.0 else 1.0
            override fun handle(event: ActionEvent?) {
                opacity = if (fadeMode == IN) {
                    (opacity + STEP).coerceAtMost(1.0)
                } else {
                    (opacity - STEP).coerceAtLeast(0.0)
                }
                node.opacity = opacity
                if (moveDirection == MoveDirection.BOTTOM) {
                    node.scene.window.y += 0.15
                } else if (moveDirection == MoveDirection.BOTTOM_RIGHT) {
                    node.scene.window.y += 0.5
                    node.scene.window.x += 0.5
                }
            }
        }))
        timeline.delay = delay
        timeline.cycleCount = (1 / STEP).toInt()
        timeline.onFinished = onFinished
        timeline.play()
    }

    fun rotate(node: Node, byAngle: Double, durationMillis: Int, delayMillis: Int) {
        val rotate = RotateTransition(Duration.millis(durationMillis.toDouble()), node)
        rotate.interpolator = Interpolator.EASE_BOTH
        rotate.delay = Duration.millis(delayMillis.toDouble())
        rotate.byAngle = byAngle
        rotate.play()
    }
}
