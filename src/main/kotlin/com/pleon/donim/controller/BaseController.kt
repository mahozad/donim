package com.pleon.donim.controller

import com.pleon.donim.util.ThemeUtil
import javafx.fxml.FXML
import javafx.scene.Node

open class BaseController {

    @FXML protected lateinit var root: Node

    @FXML protected open fun initialize() {
        ThemeUtil.applyTheme(root)
        ThemeUtil.setOnToggled { ThemeUtil.applyTheme(root) }
    }
}
