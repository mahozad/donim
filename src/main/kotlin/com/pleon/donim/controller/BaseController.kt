package com.pleon.donim.controller

import com.pleon.donim.util.AnimationUtil.FadeMode.OUT
import com.pleon.donim.util.AnimationUtil.MoveDirection.BOTTOM
import com.pleon.donim.util.AnimationUtil.fade
import com.pleon.donim.util.AnimationUtil.move
import com.pleon.donim.util.DecorationUtil
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.stage.Stage
import javafx.util.Duration
import kotlin.system.exitProcess

open class BaseController {

    @FXML protected lateinit var root: Node

    private var windowOffsetX = 0.0
    private var windowOffsetY = 0.0

    @FXML protected open fun initialize() {
        DecorationUtil.applyThemeTo(root)
        DecorationUtil.setThemeChangedListener { DecorationUtil.applyThemeTo(root) }
    }

    fun makeWindowMovable() {
        root.setOnMousePressed {
            windowOffsetX = it.sceneX
            windowOffsetY = it.sceneY
        }
        root.setOnMouseDragged {
            root.scene.window.x = it.screenX - windowOffsetX
            root.scene.window.y = it.screenY - windowOffsetY
        }
    }

    protected fun closeWindow(shouldFinishApp: Boolean) {
        val delay = Duration.millis(0.0)
        val duration = Duration.millis(100.0)
        fade(OUT, root, delay, duration, EventHandler {
            (root.scene.window as Stage).close()
            if (shouldFinishApp) exitProcess(0)
        })
        move(BOTTOM, root.scene.window, delay, duration)
    }
}
