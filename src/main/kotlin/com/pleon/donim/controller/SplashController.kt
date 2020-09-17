package com.pleon.donim.controller

import com.pleon.donim.util.AnimationUtil.FadeMode.IN
import com.pleon.donim.util.AnimationUtil.FadeMode.OUT
import com.pleon.donim.util.AnimationUtil.fade
import com.pleon.donim.util.AnimationUtil.rotate
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.Parent
import javafx.util.Duration

class SplashController : BaseController() {

    // TODO: Set splash screen to be always on top

    @FXML private lateinit var brand: Node
    private lateinit var nextRoot: Parent

    override fun initialize() {
        super.initialize()
        nextRoot = FXMLLoader.load(javaClass.getResource("/fxml/scene-main.fxml"))
        nextRoot.opacity = 0.0
        val delay = Duration.millis(500.0)
        val duration = Duration.millis(3000.0)
        rotate(brand, byAngle = 360.0, delay, duration)
        transitionToNextScene()
    }

    private fun transitionToNextScene() {
        fade(root, OUT,
                delay = Duration.millis(3000.0),
                duration = Duration.millis(200.0),
                onFinished = EventHandler { root.scene.root = nextRoot })

        fade(nextRoot, IN,
                delay = Duration.millis(3500.0),
                duration = Duration.millis(200.0))
    }
}
