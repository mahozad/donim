package com.pleon.donim.controller

import com.pleon.donim.exception.SettingNotFoundException
import com.pleon.donim.model.DEFAULT_BREAK_DURATION
import com.pleon.donim.model.DEFAULT_FOCUS_DURATION
import com.pleon.donim.util.DecorationUtil
import com.pleon.donim.util.PersistentSettings
import javafx.beans.value.ObservableStringValue
import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.scene.control.ToggleButton

class SettingsController : BaseController() {

    @FXML private lateinit var focusDuration: TextField
    @FXML private lateinit var breakDuration: TextField
    @FXML private lateinit var toggleTheme: ToggleButton

    override fun initialize() {
        super.initialize()
        focusDuration.setOnMouseClicked { focusDuration.selectAll() }
        breakDuration.setOnMouseClicked { breakDuration.selectAll() }
        setInitialSettingValues()
        setupListeners()
    }

    private fun setInitialSettingValues() {
        focusDuration.promptText = DEFAULT_FOCUS_DURATION.toMinutes().toInt().toString()
        breakDuration.promptText = DEFAULT_BREAK_DURATION.toMinutes().toInt().toString()
        try {
            focusDuration.text = PersistentSettings.get("focus-duration")
            breakDuration.text = PersistentSettings.get("break-duration")
        } catch (e: SettingNotFoundException) {
            // That's fine, do nothing
        }
        try {
            if (PersistentSettings.get("theme") == DecorationUtil.Theme.DARK.name) {
                toggleTheme.isSelected = true
                toggleTheme.text = "Switch to light theme"
            } else {
                toggleTheme.isSelected = false
                toggleTheme.text = "Switch to dark theme"
            }
        } catch (e: SettingNotFoundException) {
            // That's fine, do nothing
        }
    }

    private fun setupListeners() {
        toggleTheme.selectedProperty().addListener { observable, oldValue, newValue ->
            if (newValue == true) {
                toggleTheme.text = "Switch to light theme"
            } else {
                toggleTheme.text = "Switch to dark theme"
            }
        }
        focusDuration.textProperty().addListener { _, _, newValue ->
            PersistentSettings.set("focus-duration", newValue)
        }
        breakDuration.textProperty().addListener { _, _, newValue ->
            PersistentSettings.set("break-duration", newValue)
        }
    }

    fun close() = closeWindow(false)
    fun toggleTheme() = DecorationUtil.toggleTheme()

    fun getObservableFocusDuration(): ObservableStringValue {
        return focusDuration.textProperty()
    }

    fun getObservableBreakDuration(): ObservableStringValue {
        return breakDuration.textProperty()
    }
}
