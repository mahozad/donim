package com.pleon.donim.controller

import com.pleon.donim.util.DecorationUtil
import com.pleon.donim.util.PersistentSettings
import javafx.beans.value.ObservableStringValue
import javafx.fxml.FXML
import javafx.scene.control.TextField

class SettingsController : BaseController() {

    @FXML private lateinit var focusDuration: TextField
    @FXML private lateinit var breakDuration: TextField

    override fun initialize() {
        super.initialize()
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
