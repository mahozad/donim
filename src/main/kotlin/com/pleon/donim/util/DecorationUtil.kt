package com.pleon.donim.util

import javafx.stage.Screen
import javafx.stage.Stage

object DecorationUtil {

    fun centerWindowOnScreen(stage: Stage) {
        val screenBounds = Screen.getPrimary().visualBounds
        stage.x = (screenBounds.width - stage.width) / 2
        stage.y = (screenBounds.height - stage.height) / 2
    }

}
