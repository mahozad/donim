package com.pleon.chopchop

import com.pleon.chopchop.ThemeUtil.Theme.DARK
import com.pleon.chopchop.ThemeUtil.Theme.LIGHT
import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node

object ThemeUtil {

    private enum class Theme {
        DARK, LIGHT
    }

    private var theme = SimpleObjectProperty(DARK)

    fun onThemeChanged(listener: InvalidationListener) {
        theme.addListener(listener)
    }

    fun toggleTheme() {
        theme.value = if (theme.value == DARK) LIGHT else DARK
    }

    fun applyTheme(node: Node) {
        if (theme.value == LIGHT) {
            node.styleClass.removeAll("dark")
        } else {
            node.styleClass.add("dark")
        }
    }
}
