package com.pleon.donim

import javafx.scene.paint.Color
import javafx.util.Duration

interface ColorAnimatable {
    fun startAnimation(duration: Duration, startColor: Color, endColor: Color)
    fun stopAnimation(graceful: Boolean = true)
    fun resetAnimation(graceful: Boolean = true)
}
