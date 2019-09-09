package com.pleon.donim.model

import javafx.scene.paint.Color
import javafx.scene.paint.Color.hsb
import javafx.util.Duration
import java.awt.TrayIcon.MessageType

enum class Period(val length: Duration,
                  val baseColor: Color,
                  val notification: String,
                  val notificationType: MessageType) {

    WORK(Duration.minutes(25.0),
            hsb(0.0, 0.85, 1.0),
            "You can now use the system",
            MessageType.INFO),

    BREAK(Duration.minutes(2.0),
            hsb(145.0, 0.85, 1.0),
            "Please take a break and return later",
            MessageType.WARNING)

}
