package com.pleon.donim.node

import com.pleon.donim.Animatable
import com.pleon.donim.Animatable.AnimationDirection.FORWARD
import com.pleon.donim.Animatable.AnimationProperties
import com.pleon.donim.div
import com.pleon.donim.plus
import com.pleon.donim.times
import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.beans.property.SimpleDoubleProperty
import javafx.event.ActionEvent
import javafx.scene.text.Text
import javafx.util.Duration

class Time : Text(), Animatable {

    private lateinit var animationProperties: AnimationProperties
    private var timer = Timeline()
    private var endFunction: () -> Unit = {}
    private var fraction = SimpleDoubleProperty(0.0)

    private fun createTimer() {
        val startKeyFrame = KeyFrame(Duration.ZERO, KeyValue(fraction, 0))
        val endKeyFrame = KeyFrame(animationProperties.duration, onAnimationEnd(), KeyValue(fraction, 1))
        timer = Timeline(startKeyFrame, endKeyFrame)
        fraction.addListener { _, _, _ -> text = format() }
        timer.cycleCount = Animation.INDEFINITE
    }

    private fun format(): String {
        var duration = if (animationProperties.direction == FORWARD) animationProperties.duration * fraction.value else animationProperties.duration * (1 - fraction.value)
        duration += Duration.millis(500.0) // To display the initial state long enough
        return String.format("%02d:%02d", duration.toMinutes().toInt(), duration.toSeconds().toInt() % 60)
    }

    fun isFresh() = timer.status == Animation.Status.STOPPED

    override fun setupAnimation(properties: AnimationProperties, onEnd: () -> Unit) {
        animationProperties = properties
        endFunction = onEnd
        timer.stop()
        createTimer()
        fraction.value = 0.0
        text = format()
    }

    override fun startAnimation() {
        timer.play()
    }

    override fun pauseAnimation() {
        timer.pause()
    }

    override fun resetAnimation() {
        timer.stop()
        fraction.value = 0.0
        text = format()
    }

    override fun endAnimation(isGraceful: Boolean, graceDuration: Duration) {
        if (isGraceful) {
            timer.rate = animationProperties.duration * (1 - fraction.value) / graceDuration
            timer.play() // for when the ending is called while paused
        } else {
            timer.jumpTo(animationProperties.duration)
            endFunction() // maybe not needed?
        }
    }

    private fun onAnimationEnd(): (event: ActionEvent) -> Unit = {
        timer.stop()
        endFunction()
    }
}
