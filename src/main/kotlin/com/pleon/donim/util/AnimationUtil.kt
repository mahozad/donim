package com.pleon.donim.util

import com.pleon.donim.util.FadeMode.IN
import javafx.animation.*
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.stage.Window
import javafx.util.Duration
import javafx.util.Duration.ZERO

enum class MoveDirection { BOTTOM, BOTTOM_RIGHT }

enum class FadeMode { IN, OUT }

private val interpolator: Interpolator = Interpolator.SPLINE(0.4, 0.2, 0.25, 1.0)

fun fade(node: Node,
         mode: FadeMode,
         duration: Duration,
         delay: Duration = ZERO,
         onFinish: () -> Unit = {}) {

    val fade = FadeTransition(duration, node)
    fade.delay = delay
    fade.fromValue = if (mode == IN) 0.0 else 1.0
    fade.toValue = if (mode == IN) 1.0 else 0.0
    fade.onFinished = EventHandler { onFinish() }
    fade.play()
}

fun move(window: Window,
         direction: MoveDirection,
         duration: Duration,
         delay: Duration = ZERO,
         onFinish: () -> Unit = {}) {

    val step = 0.01
    val timeline = Timeline()
    val frameDuration = duration.multiply(step)
    timeline.keyFrames.add(KeyFrame(frameDuration, {
        if (direction == MoveDirection.BOTTOM) {
            window.y += step.times(15)
        } else if (direction == MoveDirection.BOTTOM_RIGHT) {
            window.y += step.times(50)
            window.x += step.times(50)
        }
    }))
    timeline.delay = delay
    timeline.cycleCount = (1 / step).toInt()
    timeline.onFinished = EventHandler { onFinish() }
    timeline.play()
}

fun rotate(node: Node, byAngle: Double, delay: Duration, duration: Duration) {
    val rotate = RotateTransition(duration, node)
    rotate.interpolator = Interpolator.SPLINE(0.4, 0.25, 0.16, 1.0)
    rotate.delay = delay
    rotate.byAngle = byAngle
    rotate.play()
}

fun interpolate(startValue: Int, endValue: Int, fraction: Double): Double {
    return interpolator.interpolate(startValue.toDouble(), endValue.toDouble(), fraction)
}
