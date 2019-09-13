package com.pleon.donim

import com.pleon.donim.util.FileUtil.readFile
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
import java.awt.*
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    Application.launch(Main::class.java, *args)
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
    }

    @Throws(AWTException::class)
    private fun createTrayIcon(stage: Stage) {
        if (!SystemTray.isSupported()) return

        val showItem = MenuItem("Show Window")
        showItem.addActionListener { Platform.runLater { stage.show() } }

        val aboutItem = MenuItem("About")
        aboutItem.addActionListener { Platform.runLater { showAbout() } }

        val closeItem = MenuItem("Exit")
        closeItem.addActionListener { exitProcess(0) }

        val popup = PopupMenu()
        popup.add(showItem)
        popup.add(aboutItem)
        popup.add(closeItem)

        val resourceAsStream = javaClass.getResourceAsStream("/tray.png")
        val trayImage = Toolkit.getDefaultToolkit().createImage(readFile(resourceAsStream))
        val trayIcon = TrayIcon(trayImage, "Donim", popup)
        trayIcon.addActionListener { Platform.runLater { stage.show() } }
        SystemTray.getSystemTray().add(trayIcon)
    }

    private fun showAbout() {
        val root = FXMLLoader.load<Parent>(javaClass.getResource("/fxml/scene-about.fxml"))
        Stage().apply {
            isResizable = false
            title = "About"
            scene = Scene(root).apply { fill = Color.TRANSPARENT }
            icons.add(Image("/svg/logo.svg"))
            initStyle(StageStyle.TRANSPARENT)
            toFront()
            show()
        }
    }
}
