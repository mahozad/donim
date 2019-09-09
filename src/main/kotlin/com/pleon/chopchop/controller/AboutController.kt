package com.pleon.chopchop.controller

import com.pleon.chopchop.HostServicesProvider.getHostServices
import com.pleon.chopchop.ThemeUtil
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
        ThemeUtil.onThemeChanged(InvalidationListener { ThemeUtil.applyTheme(root) })

        link.setOnAction { getHostServices().showDocument(link.text) }

        // Make window movable
        root.onMousePressed = EventHandler {
            xOffset = it.sceneX
            yOffset = it.sceneY
        }
        root.onMouseDragged = EventHandler {
            root.scene.window.x = it.screenX - xOffset
            root.scene.window.y = it.screenY - yOffset
        }
    }

    fun closeWindow() {
        // TODO: Fade out the stage
        (root.scene.window as Stage).close()
    }
}
