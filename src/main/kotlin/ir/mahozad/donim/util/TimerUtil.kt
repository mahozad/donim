package ir.mahozad.donim.util

import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.beans.property.SimpleDoubleProperty
import javafx.util.Duration

fun createTimer(duration:Duration, updatableProperty: SimpleDoubleProperty, onEnd: () -> Unit): Timeline {
    val startKeyFrame = KeyFrame(Duration.ZERO, KeyValue(updatableProperty, 0))
    val endKeyFrame = KeyFrame(duration, "end", { onEnd() }, KeyValue(updatableProperty, 1))
    val timer = Timeline(startKeyFrame, endKeyFrame)
    timer.cycleCount = Animation.INDEFINITE
    return timer
}
