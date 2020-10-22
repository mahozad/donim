package com.pleon.donim

import com.pleon.donim.util.DecorationUtil.centerOnScreen
import com.pleon.donim.util.buildTransparentScene
import javafx.application.Application
import javafx.application.HostServices
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle

const val APP_NAME = "Donim"

/** This is the same as the brighter color-stop in the gradient of the logo */
val APP_BASE_COLOR = Color.hsb(212.0, 0.9, 1.0)!!

lateinit var hostServicesInstance: HostServices

// TODO: Rename WORK period to FOCUS
// TODO: Add an option to the app to make its theme follow windows theme.
//  see https://stackoverflow.com/q/62289/ and https://stackoverflow.com/q/60837862

fun main(args: Array<String>) {
    Application.launch(Main::class.java, *args)
}

// To use spring for dependency injection see
// [http://www.greggbolinger.com/let-spring-be-your-javafx-controller-factory/]

/**
 * The main class of the application.
 *
 * In JavaFX the main class should extend from [Application] and override its [start][Application.start] method.
 * For help about code samples refer [here](https://github.com/JetBrains/kotlin/blob/master/libraries/stdlib/samples/ReadMe.md).
 *
 * @constructor the primary constructor takes no parameters
 * @sample sample.basic.addTwoNumbers
 * @see Application
 */
class Main : Application() {

    override fun start(primaryStage: Stage) {
        Platform.setImplicitExit(false) // For minimize to tray to work correctly
        hostServicesInstance = this.hostServices

        val root: Parent = FXMLLoader.load(javaClass.getResource("/fxml/scene-splash.fxml"))
        primaryStage.scene = buildTransparentScene(root)
        primaryStage.isResizable = false
        primaryStage.title = APP_NAME
        primaryStage.icons.add(Image("/img/logo.png"))
        primaryStage.initStyle(StageStyle.TRANSPARENT)
        primaryStage.show()
        primaryStage.toFront()
        centerOnScreen(primaryStage)
    }
}
