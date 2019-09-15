package com.pleon.donim.util

import com.pleon.donim.util.ThemeUtil.Theme.DARK
import com.pleon.donim.util.ThemeUtil.Theme.LIGHT
import javafx.beans.Observable
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import java.nio.file.Files
import java.nio.file.Path

private val STORE_PATH = Path.of("theme.dat")
private val DEFAULT_THEME = DARK

object ThemeUtil {

    enum class Theme { DARK, LIGHT }
    private var theme = SimpleObjectProperty<Theme>()

    init {
        try {
            val themeName = Files.readString(STORE_PATH)
            theme.value = Theme.valueOf(themeName)
        } catch (e: Exception) {
            theme.value = DEFAULT_THEME
        }
    }

    fun setOnToggled(listener: (Observable) -> Unit) = theme.addListener(listener)

    fun toggleTheme() {
        theme.value = if (theme.value == DARK) LIGHT else DARK
        Files.writeString(STORE_PATH, theme.value.name)
    }

    fun applyThemeTo(node: Node) {
        if (theme.value == LIGHT) {
            node.styleClass.removeAll("dark")
        } else {
            node.styleClass.add("dark")
        }
    }
}
