package com.pleon.donim.util

interface Settings {

    fun get(name: String): String

    fun set(name: String, value: String)
}
