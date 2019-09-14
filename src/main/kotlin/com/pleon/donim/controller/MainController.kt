package com.pleon.donim.controller

import com.pleon.donim.model.Period.BREAK
import com.pleon.donim.model.Period.WORK
import com.pleon.donim.node.CircularProgressBar
import com.pleon.donim.util.AnimationUtil.fadeOut
import com.pleon.donim.util.ThemeUtil
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.media.AudioClip
import javafx.scene.shape.SVGPath
import javafx.stage.Stage
import javafx.util.Duration
import java.awt.SystemTray
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
    private lateinit var trayAnimation: Timeline
    private lateinit var beep: AudioClip
    private val remainingTimeString = SimpleStringProperty(format(period.length))
    private var remainingTime = period.length
    private val timeline = Timeline()
    private var paused = true

    override fun initialize() {
        super.initialize()

        val trayZip = ZipFile(javaClass.getResource("/tray-animated.zip").path)
        val trayImages = trayZip.entries().toList().sortedBy { it.name }.map {
            ImageIO.read(trayZip.getInputStream(it))
        }

        trayAnimation = Timeline()
        trayAnimation.cycleCount = Timeline.INDEFINITE
        trayAnimation.keyFrames.add(KeyFrame(Duration.millis(50.0),
                object : EventHandler<ActionEvent> {
                    val firstFrameDelay = 100
                    var i = 0
                    override fun handle(event: ActionEvent) {
                        val trayIcon = SystemTray.getSystemTray().trayIcons[0]
                        trayIcon.image = if (i <= firstFrameDelay) trayImages[0] else trayImages[i - firstFrameDelay]
                        i = (i + 1) % (trayImages.size + firstFrameDelay)
                    }
                }
        ))

        beep = AudioClip(javaClass.getResource("/sound/beep.wav").toExternalForm())

        // Make window movable
        root.setOnMousePressed {
            xOffset = it.sceneX
            yOffset = it.sceneY
        }
        root.setOnMouseDragged {
            root.scene.window.x = it.screenX - xOffset
            root.scene.window.y = it.screenY - yOffset
        }
    }

    fun getRemainingTimeString(): String {
        return remainingTimeString.get()
    }

    fun remainingTimeStringProperty(): StringProperty {
        return remainingTimeString
    }

    fun setRemainingTimeString(remainingTimeString: String) {
        this.remainingTimeString.set(remainingTimeString)
    }

    private fun startTimer(shouldNotify: Boolean) {
        playIcon.content = "m 8,18.1815 c 1.1,0 2,-0.794764 2,-1.766143 V 7.5846429 C 10,6.6132643 9.1,5.8185 8,5.8185 6.9,5.8185 6,6.6132643 6,7.5846429 V 16.415357 C 6,17.386736 6.9,18.1815 8,18.1815 Z M 14,7.5846429 v 8.8307141 c 0,0.971379 0.9,1.766143 2,1.766143 1.1,0 2,-0.794764 2,-1.766143 V 7.5846429 C 18,6.6132643 17.1,5.8185 16,5.8185 c -1.1,0 -2,0.7947643 -2,1.7661429 z"
        trayAnimation.play()
        remainingTime = period.length
        timeline.cycleCount = remainingTime.toSeconds().toInt()
        val trayIcon = SystemTray.getSystemTray().trayIcons[0]
        if (shouldNotify) {
            trayIcon.displayMessage(period.toString(), period.notification, period.notificationType)
            beep.play()
        }
        trayIcon.toolTip = "Donim: $period"
        timeline.play()
    }

    fun close() {
        fadeOut(root.scene.window as Stage, EventHandler { exitProcess(0) })
    }

    fun minimize() {
        fadeOut(root.scene.window as Stage, EventHandler {
            // make it opaque again, so it'll reappear properly if they click the taskbar
            (root.scene.window as Stage).opacity = 1.0
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

    fun toggleTheme() = ThemeUtil.toggleTheme()

}
