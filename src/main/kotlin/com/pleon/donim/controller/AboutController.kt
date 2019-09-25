package com.pleon.donim.controller

import com.pleon.donim.util.AnimationUtil.FadeMode.OUT
import com.pleon.donim.util.AnimationUtil.MoveDirection
import com.pleon.donim.util.AnimationUtil.fade
import com.pleon.donim.util.HostServicesUtil
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.Hyperlink
import javafx.stage.Stage

class AboutController : BaseController() {

    @FXML private lateinit var link: Hyperlink

    private var xOffset = 0.0
    private var yOffset = 0.0

    override fun initialize() {
        super.initialize()

        link.setOnAction { HostServicesUtil.hostServices.showDocument(link.text) }

        // Make window movable
        root.setOnMousePressed {
            xOffset = it.sceneX
            yOffset = it.sceneY
        }
        root.setOnMouseDragged {
            root.scene.window.x = it.screenX - xOffset
            root.scene.window.y = it.screenY - yOffset
        }
    }

    fun closeWindow() {
        val stage = root.scene.window as Stage
        fade(OUT, root, MoveDirection.BOTTOM, 1, 0, EventHandler { stage.close() })
    }
}
