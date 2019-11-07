package com.pleon.donim

import com.pleon.donim.util.DecorationUtil.centerOnScreen
import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory
import javafx.application.Application
import javafx.application.HostServices
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle

const val APP_NAME = "Donim"
lateinit var hostServicesInstance: HostServices

fun main(args: Array<String>) {
    Application.launch(Main::class.java, *args)
}

// To use spring for dependency injection see
// [http://www.greggbolinger.com/let-spring-be-your-javafx-controller-factory/]

class Main : Application() {

    override fun start(primaryStage: Stage) {
        SvgImageLoaderFactory.install() // Enable svg wherever other formats are applicable
        Platform.setImplicitExit(false) // For minimize to tray to work correctly
        hostServicesInstance = this.hostServices

        val root: Parent = FXMLLoader.load(javaClass.getResource("/fxml/scene-splash.fxml"))
        primaryStage.scene = Scene(root).apply { fill = Color.TRANSPARENT }
        primaryStage.isResizable = false
        primaryStage.title = APP_NAME
        primaryStage.icons.add(Image("/img/logo.svg"))
        primaryStage.initStyle(StageStyle.TRANSPARENT)
        primaryStage.show()
        centerOnScreen(primaryStage)
    }
}
