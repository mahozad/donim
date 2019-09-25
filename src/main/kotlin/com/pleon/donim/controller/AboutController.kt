package com.pleon.donim.controller

import com.pleon.donim.util.AnimationUtil.FadeMode.OUT
import com.pleon.donim.util.AnimationUtil.MoveDirection
import com.pleon.donim.util.AnimationUtil.fade
import com.pleon.donim.util.HostServicesUtil
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.Hyperlink
import javafx.stage.Stage
import javafx.util.Duration

class AboutController : BaseController() {

    @FXML private lateinit var link: Hyperlink

    override fun initialize() {
        super.initialize()
        link.setOnAction { HostServicesUtil.hostServices.showDocument(link.text) }
        makeWindowMovable()
    }

    fun closeWindow() {
        val stage = root.scene.window as Stage
        fade(OUT, root, MoveDirection.BOTTOM,
                duration = Duration.millis(100.0),
                delay = Duration.millis(0.0),
                onFinished = EventHandler { stage.close() })
    }
}
