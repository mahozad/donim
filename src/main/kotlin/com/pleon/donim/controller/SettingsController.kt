package com.pleon.donim.controller

import com.pleon.donim.util.DecorationUtil

class SettingsController : BaseController() {

    fun close() = closeWindow(false)
    fun toggleTheme() = DecorationUtil.toggleTheme()
}
