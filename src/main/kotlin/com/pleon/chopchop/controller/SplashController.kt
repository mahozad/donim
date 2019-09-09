package com.pleon.chopchop.controller

import com.pleon.chopchop.ThemeUtil
import javafx.animation.Interpolator
import javafx.animation.KeyFrame
import javafx.animation.RotateTransition
import javafx.animation.Timeline
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.util.Duration

class SplashController {

    @FXML private lateinit var root: Node
    @FXML private lateinit var brand: Node

    private var stageOpacity = 1.0

    fun initialize() {
        ThemeUtil.applyTheme(root)
        rotateLogo()
        fadeOut()
    }

    private fun rotateLogo() {
        val rotate = RotateTransition(Duration.millis(2000.0), brand)
        rotate.interpolator = Interpolator.EASE_BOTH
        rotate.delay = Duration.millis(1200.0)
        rotate.byAngle = 360.0
        rotate.play()
    }

    private fun fadeOut() {
        val timeline = Timeline()
        timeline.keyFrames.add(KeyFrame(Duration.millis(2.0), EventHandler<ActionEvent> {
            stageOpacity = (stageOpacity - 0.01).coerceAtLeast(0.0)
            root.opacity = stageOpacity
        }))
        timeline.cycleCount = 100
        timeline.delay = Duration.millis(2500.0)
        timeline.setOnFinished {
            root.scene.root = FXMLLoader.load(javaClass.getResource("/fxml/scene-main.fxml"))
        }
        timeline.play()
    }
}
