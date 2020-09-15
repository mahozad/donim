package com.pleon.donim.util

import com.pleon.donim.exception.SettingNotFoundException
import com.pleon.donim.util.DecorationUtil.Theme.DARK
import com.pleon.donim.util.DecorationUtil.Theme.LIGHT
import javafx.beans.Observable
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.Window

private val DEFAULT_THEME = DARK

enum class SnapSide { LEFT, RIGHT }

fun Stage.snapTo(other: Window, side: SnapSide) {
    if (side == SnapSide.LEFT) {
        this.x = other.x - other.width + 16
    } else {
        this.x = other.x + other.width - 16
    }
    this.y = other.y - 3
    other.xProperty().addListener { _, _, newValue ->
        if (side == SnapSide.LEFT) {
            this.x = newValue.toDouble() - other.width + 16
        } else {
            this.x = newValue.toDouble() + other.width - 16
        }
    }
    other.yProperty().addListener { _, _, newValue ->
        this.y = newValue.toDouble()
    }
}

object DecorationUtil {

    enum class Theme { DARK, LIGHT }

    private var theme = SimpleObjectProperty<Theme>()
    private val settings: Settings = PersistentSettings

    init {
        try {
            val themeName = settings.get("theme")
            theme.value = Theme.valueOf(themeName)
        } catch (e: SettingNotFoundException) {
            theme.value = DEFAULT_THEME
        }
    }

    fun setThemeChangedListener(listener: (Observable) -> Unit) = theme.addListener(listener)

    fun toggleTheme() {
        theme.value = if (theme.value == DARK) LIGHT else DARK
        settings.set("theme", theme.value.name)
    }

    fun applyThemeTo(node: Node) {
        if (theme.value == LIGHT) {
            node.styleClass.removeAll("dark")
        } else {
            node.styleClass.add("dark")
        }
    }

    fun centerOnScreen(stage: Stage) {
        val screenBounds = Screen.getPrimary().visualBounds
        stage.x = (screenBounds.width - stage.width) / 2
        stage.y = (screenBounds.height - stage.height) / 2
    }
}
