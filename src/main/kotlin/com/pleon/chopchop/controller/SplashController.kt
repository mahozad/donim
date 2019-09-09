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
import javafx.scene.Parent
import javafx.util.Duration

class SplashController {

    @FXML
    private lateinit var root: Node
    @FXML
    private lateinit var brand: Node

    fun initialize() {
        ThemeUtil.applyTheme(root)
        rotateLogo()
        fadeOut()
    }

    private fun rotateLogo() {
        val rotate = RotateTransition(Duration.millis(2000.0), brand)
        rotate.byAngle = 360.0
        rotate.interpolator = Interpolator.EASE_BOTH
        rotate.delay = Duration.millis(1200.0)
        rotate.play()
    }

    private fun fadeOut() {
        val timeline = Timeline()
        timeline.keyFrames.add(KeyFrame(Duration.millis(2.0), object : EventHandler<ActionEvent?> {
            private var opacity = 1.0
            override fun handle(event: ActionEvent?) {
                opacity = (opacity - 0.01).coerceAtLeast(0.0)
                root.opacity = opacity
            }
        }))
        timeline.cycleCount = 100
        timeline.delay = Duration.millis(2500.0)
        val mainSceneRoot: Parent = FXMLLoader.load(javaClass
                .getResource("/fxml/scene-main.fxml"))
        timeline.setOnFinished { root.scene.root = mainSceneRoot }
        timeline.play()
    }
}
