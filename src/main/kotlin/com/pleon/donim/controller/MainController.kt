package com.pleon.donim.controller

import com.pleon.donim.model.Period.BREAK
import com.pleon.donim.model.Period.WORK
import com.pleon.donim.node.CircularProgressBar
import com.pleon.donim.util.AnimationUtil.FadeMode.OUT
import com.pleon.donim.util.AnimationUtil.MoveDirection.BOTTOM_RIGHT
import com.pleon.donim.util.AnimationUtil.fade
import com.pleon.donim.util.AnimationUtil.move
import com.pleon.donim.util.DecorationUtil
import com.pleon.donim.util.DecorationUtil.centerOnScreen
import com.pleon.donim.util.ImageUtil.rotateImage
import com.pleon.donim.util.ImageUtil.tintImage
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
import java.awt.image.BufferedImage
import java.nio.file.Files
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.system.exitProcess

class MainController : BaseController() {

    @FXML private lateinit var progressBar: CircularProgressBar
    @FXML private lateinit var restart: Button
    @FXML private lateinit var skip: Button
    @FXML lateinit var playIcon: SVGPath

    private var period = WORK
    private lateinit var trayIcon: TrayIcon
    private lateinit var trayImage: BufferedImage
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
        setupTimeline()
    }

    private fun createTrayIcon() {
        root.sceneProperty().addListener { _, oldScene, newScene ->
            if (!SystemTray.isSupported() || oldScene != null) return@addListener
            val stage = newScene.window as Stage
            trayImage = ImageIO.read(javaClass.getResource("/tray.png"))
            trayIcon = TrayIcon(trayImage, "Donim", makePopupMenu(stage))
            trayIcon.addActionListener { Platform.runLater { stage.show().also { centerOnScreen(stage) } } }
            SystemTray.getSystemTray().add(trayIcon)
        }
    }

    private fun makePopupMenu(stage: Stage): PopupMenu {
        val popup = PopupMenu()
        popup.add(newMenuItem("Show Window") { Platform.runLater { stage.show().also { centerOnScreen(stage) } } })
        popup.add(newMenuItem("About") { Platform.runLater { showAbout() } })
        popup.add(newMenuItem("Exit") { exitProcess(0) })
        return popup
    }

    private fun newMenuItem(title: String, listener: (java.awt.event.ActionEvent) -> Unit): MenuItem {
        return MenuItem(title).apply { addActionListener(listener) }
    }

    private fun makeTrayIconAnimatable() {
        val angles = Files.readAllLines(Path.of(javaClass.getResource("/angles.txt").toURI())).map { it.toDouble() }
        trayAnimation = Timeline()
        trayAnimation.cycleCount = Timeline.INDEFINITE
        trayAnimation.keyFrames.add(KeyFrame(Duration.millis(50.0), object : EventHandler<ActionEvent> {
            private var frame = 0
            private var angleIndex = 0
            private val firstFrameDuration = 100
            override fun handle(event: ActionEvent) {
                val hueFactor = if (period == WORK) fraction() * 0.3 + 0.4 else -fraction() * 0.3 + 0.7
                if (frame < angles.size) {
                    trayIcon.image = tintImage(rotateImage(trayImage, angles[angleIndex]), hueFactor)
                    angleIndex = (angleIndex + 1) % angles.size
                    frame++
                } else {
                    if (paused) {
                        trayAnimation.pause()
                        trayIcon.image = tintImage(trayImage, 0.0)
                    } else {
                        trayIcon.image = tintImage(trayImage, hueFactor)
                    }
                    frame = (frame + 1) % (angles.size + firstFrameDuration)
                }
            }
        }))
    }

    private fun fraction() = remainingTime.toMillis() / period.length.toMillis()

    private fun setupTimeline() {
        timeline.keyFrames.add(KeyFrame(Duration.seconds(1.0), EventHandler {
            setRemainingTimeString(format(remainingTime))
            progressBar.tick(fraction(), period.baseColor)
            remainingTime = remainingTime.subtract(Duration.seconds(1.0))
        }))

        timeline.setOnFinished {
            period = if (period == WORK) BREAK else WORK
            startTimer(shouldNotify = true, shouldResetTimer = true)
        }
    }

    private fun startTimer(shouldNotify: Boolean, shouldResetTimer: Boolean) {
        playIcon.content = "m 8,18.1815 c 1.1,0 2,-0.794764 2,-1.766143 V 7.5846429 C 10,6.6132643 9.1,5.8185 8,5.8185 6.9,5.8185 6,6.6132643 6,7.5846429 V 16.415357 C 6,17.386736 6.9,18.1815 8,18.1815 Z M 14,7.5846429 v 8.8307141 c 0,0.971379 0.9,1.766143 2,1.766143 1.1,0 2,-0.794764 2,-1.766143 V 7.5846429 C 18,6.6132643 17.1,5.8185 16,5.8185 c -1.1,0 -2,0.7947643 -2,1.7661429 z"
        if (shouldResetTimer) remainingTime = period.length
        timeline.cycleCount = remainingTime.toSeconds().toInt()
        timeline.play()
        trayAnimation.play()
        trayIcon.toolTip = "Donim: $period"
        if (shouldNotify) {
            trayIcon.displayMessage(period.toString(), period.notification, period.notificationType)
            beep.play()
        }
    }

    fun close() = closeWindow(true)

    fun minimize() {
        val delay = Duration.millis(0.0)
        val duration = Duration.millis(100.0)
        fade(OUT, root, delay, duration, EventHandler {
            root.opacity = 1.0 // Make it opaque again, so it'll reappear properly
            (root.scene.window as Stage).hide()
        })
        move(BOTTOM_RIGHT, root.scene.window, delay, duration)
    }

    fun restart() {
        playIcon.content = "m 8,18.1815 c 1.1,0 2,-0.794764 2,-1.766143 V 7.5846429 C 10,6.6132643 9.1,5.8185 8,5.8185 6.9,5.8185 6,6.6132643 6,7.5846429 V 16.415357 C 6,17.386736 6.9,18.1815 8,18.1815 Z M 14,7.5846429 v 8.8307141 c 0,0.971379 0.9,1.766143 2,1.766143 1.1,0 2,-0.794764 2,-1.766143 V 7.5846429 C 18,6.6132643 17.1,5.8185 16,5.8185 c -1.1,0 -2,0.7947643 -2,1.7661429 z"
        remainingTime = period.length
        paused = false
        trayAnimation.play()
        timeline.playFrom(Duration.ZERO)
    }

    fun pauseResume() {
        paused = !paused
        if (paused) {
            // tray animation is paused in its keyframe event handler
            timeline.pause()
            playIcon.content = "M8 6.82v10.36c0 .79.87 1.27 1.54.84l8.14-5.18c.62-.39.62-1.29 0-1.69L9.54 5.98C8.87 5.55 8 6.03 8 6.82z"
        } else {
            skip.isDisable = false
            restart.isDisable = false
            startTimer(shouldNotify = false, shouldResetTimer = false)
        }
    }

    private fun format(duration: Duration): String {
        return String.format("%02d:%02d",
                duration.toMinutes().toInt(),
                duration.toSeconds().toInt() % 60)
    }

    fun skip() {
        period = if (period == WORK) BREAK else WORK
        timeline.stop()
        paused = false
        trayAnimation.play()
        startTimer(shouldNotify = false, shouldResetTimer = true)
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

}
