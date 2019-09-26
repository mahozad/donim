package com.pleon.donim.controller

import com.pleon.donim.util.HostServicesUtil
import javafx.fxml.FXML
import javafx.scene.control.Hyperlink

class AboutController : BaseController() {

    @FXML private lateinit var link: Hyperlink

    override fun initialize() {
        super.initialize()
        link.setOnAction { HostServicesUtil.hostServices.showDocument(link.text) }
    }

    fun close() = closeWindow(false)
}
