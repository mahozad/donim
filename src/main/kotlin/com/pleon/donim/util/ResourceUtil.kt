package com.pleon.donim.util

import com.pleon.donim.Main
import java.net.URL

fun String.toURL(): URL = Main::class.java.getResource(this)

fun String.toStringURL(): String = toURL().toExternalForm()
