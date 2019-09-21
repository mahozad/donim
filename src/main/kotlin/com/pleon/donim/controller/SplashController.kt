package com.pleon.donim.controller

import com.pleon.donim.util.AnimationUtil
import com.pleon.donim.util.AnimationUtil.FadeMode.IN
import com.pleon.donim.util.AnimationUtil.FadeMode.OUT
import com.pleon.donim.util.AnimationUtil.MoveDirection
import com.pleon.donim.util.AnimationUtil.rotate
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.Parent

class SplashController : BaseController() {

    @FXML private lateinit var brand: Node
    private val nextRoot = FXMLLoader.load<Parent>(javaClass.getResource("/fxml/scene-main.fxml"))

    override fun initialize() {
        super.initialize()
        nextRoot.opacity = 0.0
        rotate(brand, byAngle = 360.0, durationMillis = 2000, delayMillis = 500)
        transitionNextScene()
    }

    private fun transitionNextScene() {
        AnimationUtil.fade(OUT, root, MoveDirection.NONE, 2000, EventHandler { root.scene.root = nextRoot })
        AnimationUtil.fade(IN, nextRoot, MoveDirection.NONE, 2500, EventHandler { /* Do nothing */ })
    }
}
