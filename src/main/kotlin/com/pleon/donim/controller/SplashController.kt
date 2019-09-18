package com.pleon.donim.controller

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

class SplashController : BaseController() {

    @FXML private lateinit var brand: Node

    private val nextScene = FXMLLoader.load<Parent>(javaClass.getResource("/fxml/scene-main.fxml"))
    private var stageOpacity = 1.0

    override fun initialize() {
        super.initialize()
        nextScene.opacity = 0.0
        rotateLogo()
        fadeOut()
    }

    private fun rotateLogo() {
        val rotate = RotateTransition(Duration.millis(2000.0), brand)
        rotate.interpolator = Interpolator.EASE_BOTH
        rotate.delay = Duration.millis(500.0)
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
        timeline.delay = Duration.millis(2000.0)
        timeline.setOnFinished { root.scene.root = nextScene }
        timeline.play()

        val timeline2 = Timeline()
        timeline2.keyFrames.add(KeyFrame(Duration.millis(2.0), EventHandler<ActionEvent> {
            stageOpacity = (stageOpacity + 0.01).coerceAtMost(1.0)
            nextScene.opacity = stageOpacity
        }))
        timeline2.cycleCount = 100
        timeline2.delay = Duration.millis(2500.0)
        timeline2.play()
    }
}
