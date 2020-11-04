package ir.mahozad.donim.util

import ir.mahozad.donim.Main
import java.net.URL

fun String.toURL(): URL = Main::class.java.getResource(this)

fun String.toStringURL(): String = toURL().toExternalForm()
