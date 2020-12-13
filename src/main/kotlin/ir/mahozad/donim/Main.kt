package ir.mahozad.donim

import ir.mahozad.donim.util.DecorationUtil.showCentered
import ir.mahozad.donim.util.buildTransparentScene
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
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path

const val APP_NAME = "Donim"

/** This is the same as the brighter color-stop in the gradient of the logo */
val APP_BASE_COLOR = Color.hsb(212.0, 0.9, 1.0)!!
const val APP_SOURCE_PAGE_URI = "https://github.com/mahozad/donim"

lateinit var hostServicesInstance: HostServices

// TODO: Show application version in about screen

fun main(args: Array<String>) {
    extractRequiredDll("reg.dll")
    extractRequiredDll("reg_x64.dll")
    Application.launch(Main::class.java, *args)
}

fun extractRequiredDll(fileName: String) {
    if (Files.exists(Path.of(fileName))) return

    val input = Main::class.java.getResourceAsStream("/$fileName")
    val buffer = ByteArray(1024)

    val path = Files.createFile(Path.of(fileName))
    val output = FileOutputStream(File(path.toUri()))
    Files.setAttribute(path, "dos:hidden", true)

    var data = input.read(buffer)
    while (data != -1) {
        output.write(buffer, 0, data)
        data = input.read(buffer)
    }
    output.close()
    input.close()
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
