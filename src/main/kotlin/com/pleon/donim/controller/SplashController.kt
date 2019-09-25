package com.pleon.donim.controller

import com.pleon.donim.util.AnimationUtil.FadeMode.IN
import com.pleon.donim.util.AnimationUtil.FadeMode.OUT
import com.pleon.donim.util.AnimationUtil.MoveDirection
import com.pleon.donim.util.AnimationUtil.fade
import com.pleon.donim.util.AnimationUtil.rotate
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.Parent
import javafx.util.Duration

class SplashController : BaseController() {

    @FXML private lateinit var brand: Node

    private val nextRoot = FXMLLoader.load<Parent>(javaClass.getResource("/fxml/scene-main.fxml"))

    override fun initialize() {
        super.initialize()
        nextRoot.opacity = 0.0
        rotate(brand, byAngle = 360.0, durationMillis = 2000, delayMillis = 500)
        transitionToNextScene()
    }

    private fun transitionToNextScene() {
        fade(OUT, root, MoveDirection.NONE,
                duration = Duration.millis(200.0),
                delay = Duration.millis(2000.0),
                onFinished = EventHandler { root.scene.root = nextRoot })

        fade(IN, nextRoot, MoveDirection.NONE,
                duration = Duration.millis(200.0),
                delay = Duration.millis(2500.0),
                onFinished = EventHandler { /* Do nothing */ })
    }
}
