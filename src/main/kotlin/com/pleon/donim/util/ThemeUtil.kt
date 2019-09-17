package com.pleon.donim.util

import com.pleon.donim.exception.SettingNotFoundException
import com.pleon.donim.util.ThemeUtil.Theme.DARK
import com.pleon.donim.util.ThemeUtil.Theme.LIGHT
import javafx.beans.Observable
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node

private val DEFAULT_THEME = DARK

object ThemeUtil {

    enum class Theme { DARK, LIGHT }

    private var theme = SimpleObjectProperty<Theme>()

    init {
        try {
            val themeName = SettingsUtil.load("theme")
            theme.value = Theme.valueOf(themeName)
        } catch (e: SettingNotFoundException) {
            theme.value = DEFAULT_THEME
        }
    }

    fun setOnToggled(listener: (Observable) -> Unit) = theme.addListener(listener)

    fun toggleTheme() {
        theme.value = if (theme.value == DARK) LIGHT else DARK
        SettingsUtil.store("theme", theme.value.name)
    }

    fun applyThemeTo(node: Node) {
        if (theme.value == LIGHT) {
            node.styleClass.removeAll("dark")
        } else {
            node.styleClass.add("dark")
        }
    }
}
