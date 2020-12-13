package ir.mahozad.donim.util

import com.registry.RegistryKey
import com.registry.RegistryWatcher
import ir.mahozad.donim.PersistentSettings
import ir.mahozad.donim.Settings
import ir.mahozad.donim.exception.SettingNotFoundException
import ir.mahozad.donim.util.DecorationUtil.Theme.*
import javafx.beans.Observable
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window

val DEFAULT_THEME = DARK
private val WINDOWS_THEME_REGISTRY_KEY = RegistryKey("Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize")

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

    enum class Theme(val title: String) {
        DARK("Dark Theme"),
        LIGHT("Light Theme"),
        AUTO("System Theme")
    }

    private var isThemeAuto = false
    private var theme = SimpleObjectProperty<Theme>()
    private val settings: Settings = PersistentSettings

    init {
        try {
            val themeName = settings.get("theme")
            theme.value = Theme.valueOf(themeName)
        } catch (e: SettingNotFoundException) {
            theme.value = DEFAULT_THEME
        }
        isThemeAuto = (theme.value == AUTO)
        if (isThemeAuto) applySystemTheme()
        listenForSystemThemeChanges()
    }

    private fun listenForSystemThemeChanges() {
        RegistryWatcher.addRegistryListener { registryEvent ->
            val changedKey = registryEvent.key
            if (changedKey == WINDOWS_THEME_REGISTRY_KEY)
                if (isThemeAuto) applySystemTheme()
        }
        RegistryWatcher.watchKey(WINDOWS_THEME_REGISTRY_KEY)
    }

    private fun applySystemTheme() {
        val value = WINDOWS_THEME_REGISTRY_KEY.getValue("AppsUseLightTheme")
        val actualValue = if (value == null) 1 else value.byteData[0] // 1 (Light) is default value in Windows
        val isWindowsDark = actualValue.toInt() == 0
        theme.value = if (isWindowsDark) DARK else LIGHT
    }

    fun setThemeChangedListener(listener: (Observable) -> Unit) = theme.addListener(listener)

    fun toggleTheme(): Theme {
        if (isThemeAuto) {
            isThemeAuto = false
            theme.value = DARK
        } else if (theme.value == DARK) {
            isThemeAuto = false
            theme.value = LIGHT
        } else if (theme.value == LIGHT) {
            isThemeAuto = true
            applySystemTheme()
        }
        settings.set("theme", if (isThemeAuto) AUTO.name else theme.value.name)
        return if (isThemeAuto) AUTO else theme.value
    }

    fun applyThemeTo(node: Node) {
        if (theme.value == LIGHT) {
            node.styleClass.removeAll("dark")
        } else {
            node.styleClass.add("dark")
        }
    }

    fun Stage.showCentered() {
        show() // Should be called before centering (otherwise sometimes does not work)
        val screenBounds = Screen.getPrimary().visualBounds
        x = (screenBounds.width - width) / 2
        y = (screenBounds.height - height) / 2
    }

    fun createTransparentStage() = Stage().apply { initStyle(StageStyle.TRANSPARENT) }
}
