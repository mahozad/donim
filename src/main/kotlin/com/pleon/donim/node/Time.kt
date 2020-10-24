package com.pleon.donim.node

import com.pleon.donim.Animatable
import com.pleon.donim.Timer
import javafx.scene.text.Text
import javafx.util.Duration

class Time : Text(), Animatable {

    private lateinit var timer: Timer
    private lateinit var animationProperties: Animatable.AnimationProperties

    private fun createTimer(onEnd: () -> Unit = {}) {
        timer = Timer(animationProperties.duration, Duration.seconds(1.0), onEnd)
        timer.remainingTimeProperty().addListener { _, _, remainingTime -> }
        // timer.elapsedTimeProperty().addListener { _, _, elapsedTime -> tick(elapsedTime) }
    }

    override fun startAnimation(properties: Animatable.AnimationProperties) {

    }

    override fun startAnimation() {
        TODO("Not yet implemented")
    }

    override fun pauseAnimation() {
        TODO("Not yet implemented")
    }

    override fun resetAnimation(properties: Animatable.AnimationProperties) {
        TODO("Not yet implemented")
    }

    override fun endAnimation(onEnd: () -> Unit, graceful: Boolean, graceDuration: Duration) {
        TODO("Not yet implemented")
    }
}
