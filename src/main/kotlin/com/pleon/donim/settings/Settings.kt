package com.pleon.donim.settings

import javafx.collections.ObservableMap

interface Settings {

    fun get(name: String): String

    fun set(name: String, value: String)

    fun getObservableProperties(): ObservableMap<String, String>
}
