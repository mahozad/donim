package com.pleon.donim

import com.pleon.donim.util.HostServicesUtil
import com.pleon.donim.util.ImageUtil.getImage
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
import java.awt.event.ActionListener
import java.io.IOException
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    Application.launch(Main::class.java, *args)
}

class Main : Application() {

    override fun start(primaryStage: Stage) {
        SvgImageLoaderFactory.install() // enable svg wherever other formats are applicable

        HostServicesUtil.hostServices = hostServices

        // Parent root = FXMLLoader.load(getClass().getResource("/fxml/scene-main.fxml"));
        val root = FXMLLoader.load<Parent>(javaClass.getResource("/fxml/scene-splash.fxml"))
        createTrayIcon(primaryStage)
        primaryStage.initStyle(StageStyle.TRANSPARENT)
        primaryStage.title = "Donim"
        primaryStage.icons.add(Image("/svg/logo.svg"))
        primaryStage.isAlwaysOnTop = false
        primaryStage.isResizable = false
        Platform.setImplicitExit(false) // for minimize to tray to work correctly
        // primaryStage.setX(0 - 10); // dou to padding and inset in .root{} in css we subtract 10
        // primaryStage.setY(0 - 10);

        val scene = Scene(root)
        scene.fill = Color.TRANSPARENT // for drop shadow to show correctly
        primaryStage.scene = scene
        primaryStage.show()
    }

    @Throws(AWTException::class)
    private fun createTrayIcon(stage: Stage) {
        if (SystemTray.isSupported()) {
            val showListener = ActionListener { Platform.runLater { stage.show() } }

            val popup = PopupMenu()

            val showItem = MenuItem("Show Window")
            showItem.addActionListener(showListener)
            popup.add(showItem)

            val aboutItem = MenuItem("About")
            aboutItem.addActionListener {
                Platform.runLater {
                    try {
                        val root = FXMLLoader.load<Parent>(javaClass.getResource("/fxml/scene-about.fxml"))
                        val stageAbout = Stage()
                        stageAbout.title = "About"
                        stageAbout.initStyle(StageStyle.TRANSPARENT)
                        val scene = Scene(root)
                        scene.fill = Color.TRANSPARENT // for drop shadow to show correctly
                        stageAbout.scene = scene
                        stageAbout.icons.add(Image("/svg/logo.svg"))
                        stageAbout.isResizable = false
                        stageAbout.toFront()
                        stageAbout.show()
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                    }
                }
            }
            popup.add(aboutItem)

            val closeItem = MenuItem("Exit")
            closeItem.addActionListener { exitProcess(0) }
            popup.add(closeItem)

            val trayImage = Toolkit.getDefaultToolkit().createImage(getImage("/tray/1.png"))
            val trayIcon = TrayIcon(trayImage, "Donim", popup)
            trayIcon.addActionListener(showListener)
            SystemTray.getSystemTray().add(trayIcon)
        }
    }

}
