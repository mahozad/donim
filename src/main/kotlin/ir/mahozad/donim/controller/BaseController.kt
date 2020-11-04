package ir.mahozad.donim.controller

import ir.mahozad.donim.util.DecorationUtil
import ir.mahozad.donim.util.FadeMode.OUT
import ir.mahozad.donim.util.MoveDirection.BOTTOM
import ir.mahozad.donim.util.fade
import ir.mahozad.donim.util.move
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.stage.Stage
import javafx.util.Duration
import kotlin.system.exitProcess

open class BaseController {

    @FXML protected lateinit var root: Node

    private var windowOffsetX = 0.0
    private var windowOffsetY = 0.0

    @FXML
    protected open fun initialize() {
        DecorationUtil.applyThemeTo(root)
        DecorationUtil.setThemeChangedListener { DecorationUtil.applyThemeTo(root) }
    }

    fun makeWindowMovable() {
        root.lookup("#title_bar")?.setOnMousePressed {
            windowOffsetX = it.sceneX
            windowOffsetY = it.sceneY
        }
        root.lookup("#title_bar")?.setOnMouseDragged {
            root.scene.window.x = it.screenX - windowOffsetX
            root.scene.window.y = it.screenY - windowOffsetY
        }
    }

    protected fun closeWindow(shouldFinishApp: Boolean) {
        val duration = Duration.millis(100.0)
        fade(root, OUT, duration) {
            (root.scene.window as Stage).close()
            if (shouldFinishApp) exitProcess(0)
        }
        move(root.scene.window, BOTTOM, duration)
    }
}
