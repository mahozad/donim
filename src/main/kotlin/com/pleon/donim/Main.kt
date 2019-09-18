package com.pleon.donim

import com.pleon.donim.controller.MainController
import com.pleon.donim.util.DecorationUtil.centerWindowOnScreen
import com.pleon.donim.util.HostServicesUtil
import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory
import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.event.ActionEvent
import javax.imageio.ImageIO
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    Application.launch(Main::class.java, *args)
}

fun showAbout() {
    val root = FXMLLoader.load<Parent>(MainController::class.java
            .getResource("/fxml/scene-about.fxml"))
    val stage = Stage()
    stage.isResizable = false
    stage.title = "About"
    stage.scene = Scene(root).apply { fill = Color.TRANSPARENT }
    stage.icons.add(Image("/svg/logo.svg"))
    stage.initStyle(StageStyle.TRANSPARENT)
    stage.show()
    centerWindowOnScreen(stage)
}

class Main : Application() {

    override fun start(primaryStage: Stage) {
        SvgImageLoaderFactory.install() // Enable svg wherever other formats are applicable
        Platform.setImplicitExit(false) // For minimize to tray to work correctly
        HostServicesUtil.hostServices = hostServices

        val root = FXMLLoader.load<Parent>(javaClass.getResource("/fxml/scene-splash.fxml"))
        createTrayIcon(primaryStage)
        primaryStage.initStyle(StageStyle.TRANSPARENT)
        primaryStage.title = "Donim"
        primaryStage.icons.add(Image("/svg/logo.svg"))
        primaryStage.isResizable = false
        primaryStage.scene = Scene(root).apply { fill = Color.TRANSPARENT }
        primaryStage.show()
        centerWindowOnScreen(primaryStage)
    }

    private fun createTrayIcon(stage: Stage) {
        if (!SystemTray.isSupported()) return

        val popup = PopupMenu()
        popup.add(newMenuItem("Show Window") { Platform.runLater { stage.show() } })
        popup.add(newMenuItem("About") { Platform.runLater { showAbout() } })
        popup.add(newMenuItem("Exit") { exitProcess(0) })

        val trayImage = ImageIO.read(javaClass.getResource("/tray.png"))
        val trayIcon = TrayIcon(trayImage, "Donim", popup)
        trayIcon.addActionListener {
            Platform.runLater { stage.show().also { centerWindowOnScreen(stage) } }
        }
        SystemTray.getSystemTray().add(trayIcon)
    }

    private fun newMenuItem(title: String, listener: (ActionEvent) -> Unit): MenuItem {
        return MenuItem(title).apply { addActionListener(listener) }
    }
}
