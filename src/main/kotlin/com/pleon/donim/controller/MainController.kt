package com.pleon.donim.controller

import com.pleon.donim.model.Period.BREAK
import com.pleon.donim.model.Period.WORK
import com.pleon.donim.node.CircularProgressBar
import com.pleon.donim.util.AnimationUtil.FadeMode.OUT
import com.pleon.donim.util.AnimationUtil.MoveDirection
import com.pleon.donim.util.AnimationUtil.fade
import com.pleon.donim.util.DecorationUtil
import com.pleon.donim.util.DecorationUtil.centerOnScreen
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.media.AudioClip
import javafx.scene.paint.Color
import javafx.scene.shape.SVGPath
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.util.Duration
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.TrayIcon
import java.util.zip.ZipFile
import javax.imageio.ImageIO
import kotlin.system.exitProcess

class MainController : BaseController() {

    @FXML private lateinit var progressBar: CircularProgressBar
    @FXML private lateinit var restart: Button
    @FXML private lateinit var skip: Button
    @FXML lateinit var playIcon: SVGPath

    private var xOffset = 0.0
    private var yOffset = 0.0
    private var period = WORK
    private lateinit var trayIcon: TrayIcon
    private lateinit var trayAnimation: Timeline
    private var beep: AudioClip = AudioClip(javaClass.getResource("/sound/beep.wav").toExternalForm())
    private val remainingTimeString = SimpleStringProperty(format(period.length))
    private var remainingTime = period.length
    private val timeline = Timeline()
    private var paused = true

    override fun initialize() {
        super.initialize()
        createTrayIcon()
        makeTrayIconAnimatable()
        makeWindowMovable()
    }

    private fun createTrayIcon() {
        if (!SystemTray.isSupported()) return

        root.sceneProperty().addListener { _, oldScene, newScene ->
            if (oldScene != null) return@addListener

            val stage = newScene.window as Stage
            val showWindow: (java.awt.event.ActionEvent) -> Unit = {
                Platform.runLater { stage.show().also { centerOnScreen(stage) } }
            }

            val popup = PopupMenu()
            popup.add(newMenuItem("Show Window", showWindow))
            popup.add(newMenuItem("About") { Platform.runLater { showAbout() } })
            popup.add(newMenuItem("Exit") { exitProcess(0) })

            val trayImage = ImageIO.read(javaClass.getResource("/tray.png"))
            trayIcon = TrayIcon(trayImage, "Donim", popup)
            trayIcon.addActionListener(showWindow)
            SystemTray.getSystemTray().add(trayIcon)
        }
    }

    private fun makeWindowMovable() {
        root.setOnMousePressed {
            xOffset = it.sceneX
            yOffset = it.sceneY
        }
        root.setOnMouseDragged {
            root.scene.window.x = it.screenX - xOffset
            root.scene.window.y = it.screenY - yOffset
        }
    }

    private fun makeTrayIconAnimatable() {
        val zip = ZipFile(javaClass.getResource("/tray-animated.zip").path)
        val trayImages = zip.entries().toList().sortedBy { it.name }.map {
            ImageIO.read(zip.getInputStream(it))
        }

        trayAnimation = Timeline()
        trayAnimation.cycleCount = Timeline.INDEFINITE
        trayAnimation.keyFrames.add(KeyFrame(Duration.millis(50.0),
                object : EventHandler<ActionEvent> {
                    val firstFrameDelay = 100
                    var i = 0
                    override fun handle(event: ActionEvent) {
                        trayIcon.image = if (i <= firstFrameDelay) trayImages[0] else trayImages[i - firstFrameDelay]
                        i = (i + 1) % (trayImages.size + firstFrameDelay)
                    }
                }
        ))
    }

    private fun newMenuItem(title: String, listener: (java.awt.event.ActionEvent) -> Unit): MenuItem {
        return MenuItem(title).apply { addActionListener(listener) }
    }

    fun getRemainingTimeString(): String {
        return remainingTimeString.get()
    }

    @Suppress("unused")
    fun remainingTimeStringProperty(): StringProperty {
        return remainingTimeString
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun setRemainingTimeString(remainingTimeString: String) {
        this.remainingTimeString.set(remainingTimeString)
    }

    private fun startTimer(shouldNotify: Boolean) {
        playIcon.content = "m 8,18.1815 c 1.1,0 2,-0.794764 2,-1.766143 V 7.5846429 C 10,6.6132643 9.1,5.8185 8,5.8185 6.9,5.8185 6,6.6132643 6,7.5846429 V 16.415357 C 6,17.386736 6.9,18.1815 8,18.1815 Z M 14,7.5846429 v 8.8307141 c 0,0.971379 0.9,1.766143 2,1.766143 1.1,0 2,-0.794764 2,-1.766143 V 7.5846429 C 18,6.6132643 17.1,5.8185 16,5.8185 c -1.1,0 -2,0.7947643 -2,1.7661429 z"
        trayAnimation.play()
        remainingTime = period.length
        timeline.cycleCount = remainingTime.toSeconds().toInt()
        if (shouldNotify) {
            trayIcon.displayMessage(period.toString(), period.notification, period.notificationType)
            beep.play()
        }
        trayIcon.toolTip = "Donim: $period"
        timeline.play()
    }

    fun close() {
        fade(OUT, root, MoveDirection.BOTTOM, 1, 0, EventHandler { exitProcess(0) })
    }

    fun minimize() {
        fade(OUT, root, MoveDirection.BOTTOM_RIGHT, 1, 0, EventHandler {
            // make it opaque again, so it'll reappear properly if they click the tray icon
            root.opacity = 1.0
            (root.scene.window as Stage).hide()
        })
    }

    fun restart() {
        playIcon.content = "m 8,18.1815 c 1.1,0 2,-0.794764 2,-1.766143 V 7.5846429 C 10,6.6132643 9.1,5.8185 8,5.8185 6.9,5.8185 6,6.6132643 6,7.5846429 V 16.415357 C 6,17.386736 6.9,18.1815 8,18.1815 Z M 14,7.5846429 v 8.8307141 c 0,0.971379 0.9,1.766143 2,1.766143 1.1,0 2,-0.794764 2,-1.766143 V 7.5846429 C 18,6.6132643 17.1,5.8185 16,5.8185 c -1.1,0 -2,0.7947643 -2,1.7661429 z"
        remainingTime = period.length
        timeline.playFrom(Duration.ZERO)
    }

    fun pauseResume() {
        if (timeline.keyFrames.isEmpty()) {
            timeline.keyFrames.add(KeyFrame(Duration.seconds(1.0), EventHandler {
                setRemainingTimeString(format(remainingTime))
                progressBar.tick(remainingTime.toMillis() / period.length.toMillis(), period.baseColor)
                remainingTime = remainingTime.subtract(Duration.seconds(1.0))
            }))

            timeline.setOnFinished {
                period = if (period == WORK) BREAK else WORK
                startTimer(true)
            }

            restart.isDisable = false
            skip.isDisable = false
            startTimer(false)
        }

        if (paused) {
            timeline.play()
            trayAnimation.play()
            playIcon.content = "m 8,18.1815 c 1.1,0 2,-0.794764 2,-1.766143 V 7.5846429 C 10,6.6132643 9.1,5.8185 8,5.8185 6.9,5.8185 6,6.6132643 6,7.5846429 V 16.415357 C 6,17.386736 6.9,18.1815 8,18.1815 Z M 14,7.5846429 v 8.8307141 c 0,0.971379 0.9,1.766143 2,1.766143 1.1,0 2,-0.794764 2,-1.766143 V 7.5846429 C 18,6.6132643 17.1,5.8185 16,5.8185 c -1.1,0 -2,0.7947643 -2,1.7661429 z"
        } else {
            timeline.pause()
            trayAnimation.pause()
            playIcon.content = "M8 6.82v10.36c0 .79.87 1.27 1.54.84l8.14-5.18c.62-.39.62-1.29 0-1.69L9.54 5.98C8.87 5.55 8 6.03 8 6.82z"
        }

        paused = !paused
    }

    private fun format(duration: Duration): String {
        return String.format("%02d:%02d",
                duration.toMinutes().toInt(),
                duration.toSeconds().toInt() % 60)
    }

    fun skip() {
        period = if (period == WORK) BREAK else WORK
        timeline.stop()
        startTimer(false)
    }

    fun toggleTheme() = DecorationUtil.toggleTheme()

    fun showAbout() {
        val root = FXMLLoader.load<Parent>(MainController::class.java.getResource("/fxml/scene-about.fxml"))
        val stage = Stage()
        stage.isResizable = false
        stage.title = "About"
        stage.scene = Scene(root).apply { fill = Color.TRANSPARENT }
        stage.icons.add(Image("/svg/logo.svg"))
        stage.initStyle(StageStyle.TRANSPARENT)
        stage.show()
        centerOnScreen(stage)
    }

}
