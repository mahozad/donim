package com.pleon.chopchop.model

import javafx.scene.paint.Color
import javafx.scene.paint.Color.hsb
import java.awt.TrayIcon.MessageType

enum class Type(val length: Int,
                val baseColor: Color,
                val notification: String,
                val notificationType: MessageType) {

    WORK(25 * 60,
            hsb(0.0, 0.85, 1.0),
            "You can now use the system",
            MessageType.INFO),

    BREAK(5 * 60,
            hsb(145.0, 0.85, 1.0),
            "Please take a break and return later",
            MessageType.WARNING)

}
