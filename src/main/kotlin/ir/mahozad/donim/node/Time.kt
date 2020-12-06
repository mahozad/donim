package ir.mahozad.donim.node

import ir.mahozad.donim.extension.div
import ir.mahozad.donim.extension.plus
import ir.mahozad.donim.extension.times
import ir.mahozad.donim.node.Animatable.AnimationDirection.FORWARD
import ir.mahozad.donim.node.Animatable.AnimationProperties
import ir.mahozad.donim.util.createTimer
import javafx.animation.Timeline
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.text.Text
import javafx.util.Duration

class Time : Text(), Animatable {

    private lateinit var animationProperties: AnimationProperties
    private var fraction = SimpleDoubleProperty(0.0)
    private var timer = Timeline()

    init { fraction.addListener { _, _, _ -> text = format() } }

    private fun format(): String {
        var duration = if (animationProperties.direction == FORWARD) animationProperties.duration * fraction.value else animationProperties.duration * (1 - fraction.value)
        duration += Duration.millis(500.0) // To display the initial state long enough
        return String.format("%02d:%02d", duration.toMinutes().toInt(), duration.toSeconds().toInt() % 60)
    }

    fun transitionToHoldState() {
        styleClass.remove("time")
        styleClass.add("time-text")
        text = "On Hold"
    }

    fun transitionToTimeState() {
        styleClass.remove("time-text")
        styleClass.add("time")
    }

    override fun resetAnimation(properties: AnimationProperties) {
        animationProperties = properties
        timer.stop()
        timer = createTimer(animationProperties.duration, fraction) { timer.stop() }
        fraction.value = 0.0
        text = format()
    }

    override fun startAnimation() = timer.play()

    override fun pauseAnimation() = timer.pause()

    override fun endAnimation(isGraceful: Boolean, graceDuration: Duration) {
        timer.rate = when {
            isGraceful -> animationProperties.duration * (1 - fraction.value) / graceDuration
            else -> animationProperties.duration * (1 - fraction.value) / Duration.ONE
        }
        timer.play() // for when the ending is called while paused
    }
}
