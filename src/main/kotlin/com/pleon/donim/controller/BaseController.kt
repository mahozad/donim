package com.pleon.donim.controller

import com.pleon.donim.util.THEME_FILE_PATH
import com.pleon.donim.util.ThemeUtil
import javafx.fxml.FXML
import javafx.scene.Node
import java.nio.file.Files
import java.nio.file.Path

open class BaseController {

    @FXML protected lateinit var root: Node

    @FXML protected open fun initialize() {
        setCurrentTheme()
        ThemeUtil.applyTheme(root)
        ThemeUtil.setOnToggled { ThemeUtil.applyTheme(root) }
    }

    private fun setCurrentTheme() = try {
        val theme = Files.readString(Path.of(THEME_FILE_PATH))
        ThemeUtil.setTheme(ThemeUtil.Theme.valueOf(theme))
    } catch (e: Exception) {
        // The default theme is used
    }
}
