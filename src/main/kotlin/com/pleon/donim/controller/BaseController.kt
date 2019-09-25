package com.pleon.donim.controller

import com.pleon.donim.util.DecorationUtil
import javafx.fxml.FXML
import javafx.scene.Node

open class BaseController {

    @FXML protected lateinit var root: Node

    private var windowOffsetX = 0.0
    private var windowOffsetY = 0.0

    @FXML protected open fun initialize() {
        DecorationUtil.applyThemeTo(root)
        DecorationUtil.setOnToggled { DecorationUtil.applyThemeTo(root) }
    }

    protected fun makeWindowMovable() {
        root.setOnMousePressed {
            windowOffsetX = it.sceneX
            windowOffsetY = it.sceneY
        }
        root.setOnMouseDragged {
            root.scene.window.x = it.screenX - windowOffsetX
            root.scene.window.y = it.screenY - windowOffsetY
        }
    }
}
