package ir.mahozad.donim

import ir.mahozad.donim.util.DecorationUtil.showCentered
import ir.mahozad.donim.util.buildTransparentScene
import ir.mahozad.donim.util.extractResource
import ir.mahozad.donim.util.toURL
import javafx.application.Application
import javafx.application.HostServices
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.nio.file.FileAlreadyExistsException

const val APP_NAME = "Donim"

/** This is the same as the brighter color-stop in the gradient of the logo */
val APP_BASE_COLOR = Color.hsb(212.0, 0.9, 1.0)!!
const val APP_SOURCE_PAGE_URI = "https://github.com/mahozad/donim"

lateinit var hostServicesInstance: HostServices

// TODO: Show application version in about screen
// FIXME: If the app is paused/resumed (multiple times?), when the counter reaches 0 it shows "00:00" istead of "on hold"

fun main(args: Array<String>) {
    extractRequiredDllFiles()
    Application.launch(Main::class.java, *args)
}

fun extractRequiredDllFiles() {
    try {
        extractResource("reg.dll")
        extractResource("reg_x64.dll")
    } catch (e: FileAlreadyExistsException) {
        // That's fine, do nothing
    }
}

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

        val root: Parent = FXMLLoader.load("/fxml/scene-splash.fxml".toURL())
        primaryStage.scene = buildTransparentScene(root)
        primaryStage.isResizable = false
        primaryStage.title = APP_NAME
        primaryStage.icons.add(Image("/img/logo.png"))
        primaryStage.initStyle(StageStyle.TRANSPARENT)
        primaryStage.showCentered()
        primaryStage.toFront()
    }
}
