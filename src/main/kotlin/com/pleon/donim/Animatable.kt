package com.pleon.donim

import javafx.scene.paint.Color
import javafx.util.Duration

interface Animatable {

    enum class AnimationDirection { FORWARD, BACKWARD }

    data class AnimationProperties(
            val duration: Duration,
            val direction: AnimationDirection,
            val startColor: Color = APP_BASE_COLOR,
            val endColor: Color = APP_BASE_COLOR,
            val pauseColor: Color = APP_BASE_COLOR,
            val initialProgress: Double = 0.0
    )

    fun startAnimation(properties: AnimationProperties)

    fun startAnimation()

    fun pauseAnimation()

    fun resetAnimation(properties: AnimationProperties)

    fun endAnimation(onEnd: () -> Unit = {},
                     graceful: Boolean = true,
                     graceDuration: Duration = Duration.seconds(2.0))
}
