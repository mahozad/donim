package com.pleon.chopchop.util

import com.pleon.chopchop.util.ThemeUtil.Theme.DARK
import com.pleon.chopchop.util.ThemeUtil.Theme.LIGHT
import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node

object ThemeUtil {

    private enum class Theme {
        DARK, LIGHT
    }

    private var theme = SimpleObjectProperty(DARK)

    fun setOnToggled(listener: InvalidationListener) {
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
