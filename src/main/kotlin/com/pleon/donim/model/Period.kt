package com.pleon.donim.model

import com.pleon.donim.util.PersistentSettings
import javafx.scene.paint.Color
import javafx.scene.paint.Color.hsb
import javafx.util.Duration
import java.awt.TrayIcon.MessageType

enum class Period(var length: Duration,
                  val baseColor: Color,
                  val notification: String,
                  val notificationType: MessageType) {

    // FIXME: Handle empty or not existent cases
    WORK(Duration.minutes(PersistentSettings.get("focus-duration").toDouble()),
            hsb(0.0, 0.85, 1.0),
            "You can now use the system",
            MessageType.INFO),

    // FIXME: Handle empty or not existent cases
    BREAK(Duration.minutes(PersistentSettings.get("break-duration").toDouble()),
            hsb(100.0, 0.85, 1.0),
            "Please take a break and return later",
            MessageType.WARNING)
}
