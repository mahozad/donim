package com.pleon.donim

import javafx.util.Duration

val Duration.length: Int get() = toMillis().toInt()
operator fun Duration.plus(other: Duration): Duration = add(other)
operator fun Duration.minus(other: Duration): Duration = subtract(other)
operator fun Duration.times(num: Int): Duration = multiply(num.toDouble())
operator fun Duration.times(num: Double): Duration = multiply(num)
operator fun Duration.div(num: Int): Duration = divide(num.toDouble())
operator fun Duration.div(num: Double): Duration = divide(num)
