package com.pleon.donim.util

import com.pleon.donim.exception.SettingNotFoundException
import com.pleon.donim.util.DecorationUtil.Theme.DARK
import com.pleon.donim.util.DecorationUtil.Theme.LIGHT
import javafx.beans.Observable
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import javafx.stage.Screen
import javafx.stage.Stage

private val DEFAULT_THEME = DARK

object DecorationUtil {

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

    fun centerWindowOnScreen(stage: Stage) {
        val screenBounds = Screen.getPrimary().visualBounds
        stage.x = (screenBounds.width - stage.width) / 2
        stage.y = (screenBounds.height - stage.height) / 2
    }

}
