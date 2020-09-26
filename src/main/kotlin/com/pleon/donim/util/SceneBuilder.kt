package com.pleon.donim.util

import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.paint.Color

fun buildTransparentScene(layout: Parent): Scene {
    val scene = Scene(layout)
    scene.fill = Color.TRANSPARENT
    return scene
}
