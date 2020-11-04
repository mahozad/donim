package com.pleon.donim.controller

import com.pleon.donim.APP_SOURCE_PAGE_URI
import com.pleon.donim.hostServicesInstance

class AboutController : BaseController() {

    fun viewSourceCode() = hostServicesInstance.showDocument(APP_SOURCE_PAGE_URI)

    fun close() = closeWindow(false)
}
