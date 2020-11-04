package ir.mahozad.donim.controller

import ir.mahozad.donim.APP_SOURCE_PAGE_URI
import ir.mahozad.donim.hostServicesInstance

class AboutController : BaseController() {

    fun viewSourceCode() = hostServicesInstance.showDocument(APP_SOURCE_PAGE_URI)

    fun close() = closeWindow(false)
}
