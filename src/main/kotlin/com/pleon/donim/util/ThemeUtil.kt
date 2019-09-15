package com.pleon.donim.util

import com.pleon.donim.util.ThemeUtil.Theme.DARK
import com.pleon.donim.util.ThemeUtil.Theme.LIGHT
import javafx.beans.Observable
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node

const val THEME_FILE_PATH = "theme.dat"

object ThemeUtil {

    enum class Theme {
        DARK, LIGHT
    }

    private var theme = SimpleObjectProperty(DARK)

    fun getTheme(): Theme = theme.get()

    fun setTheme(theme: Theme) = this.theme.set(theme)

    fun setOnToggled(listener: (Observable) -> Unit) = theme.addListener(listener)

    fun toggleTheme() = theme.setValue(if (theme.value == DARK) LIGHT else DARK)

    fun applyTheme(node: Node) {
        if (theme.value == LIGHT) {
            node.styleClass.removeAll("dark")
        } else {
            node.styleClass.add("dark")
        }
    }
}
