package com.pleon.donim.controller

import com.pleon.donim.util.AnimationUtil.fadeOut
import com.pleon.donim.util.HostServicesUtil
import com.pleon.donim.util.ThemeUtil
import javafx.beans.InvalidationListener
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Hyperlink
import javafx.stage.Stage

class AboutController {

    @FXML private lateinit var root: Node
    @FXML private lateinit var link: Hyperlink

    private var xOffset = 0.0
    private var yOffset = 0.0

    fun initialize() {
        ThemeUtil.applyTheme(root)
        ThemeUtil.setOnToggled(InvalidationListener { ThemeUtil.applyTheme(root) })

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
        fadeOut(stage, EventHandler { stage.close() })
    }
}
