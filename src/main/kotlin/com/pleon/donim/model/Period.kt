package com.pleon.donim.model

import javafx.scene.paint.Color
import javafx.scene.paint.Color.hsb
import javafx.util.Duration
import java.awt.TrayIcon.MessageType

val DEFAULT_FOCUS_DURATION = Duration.minutes(25.0)
val DEFAULT_BREAK_DURATION = Duration.minutes(2.0)

enum class Period(var length: Duration,
                  val baseColor: Color,
                  val notification: String,
                  val notificationType: MessageType,
                  val defaultLength: Duration) {

    WORK(DEFAULT_FOCUS_DURATION,
            hsb(0.0, 0.85, 1.0),
            "You can now use the system",
            MessageType.INFO,
            DEFAULT_FOCUS_DURATION),

    BREAK(DEFAULT_BREAK_DURATION,
            hsb(100.0, 0.85, 1.0),
            "Please take a break and return later",
            MessageType.WARNING,
            DEFAULT_BREAK_DURATION);

    fun setLength(length: String) {
        if (length.isNotBlank()) {
            this.length = Duration.minutes(length.toDouble())
        } else {
            this.length = defaultLength
        }
    }
}
