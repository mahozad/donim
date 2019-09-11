package com.pleon.donim.controller

import com.pleon.donim.model.Period.BREAK
import com.pleon.donim.model.Period.WORK
import com.pleon.donim.node.CircularProgressBar
import com.pleon.donim.util.AnimationUtil.fadeOut
import com.pleon.donim.util.FileUtil.readFile
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
import javafx.stage.Stage
import javafx.util.Duration
import java.awt.SystemTray
import java.awt.Toolkit
import java.util.zip.ZipFile
import kotlin.system.exitProcess

class HomeController : BaseController() {

    @FXML private lateinit var progressBar: CircularProgressBar
    @FXML private lateinit var restart: Button
    @FXML private lateinit var skip: Button

    private var xOffset = 0.0
    private var yOffset = 0.0
    private var period = WORK
    private lateinit var trayAnimation: Timeline
    private lateinit var beep: AudioClip
    private val remainingTimeString = SimpleStringProperty(format(period.length))
    private val pauseString = SimpleStringProperty("Start")
    private var remainingTime = period.length
    private val timeline = Timeline()
    private var paused = true

    // @FXML // required if method is not public
    override fun initialize() {
        super.initialize()

        val trayZip = ZipFile(javaClass.getResource("/tray-animated.zip").path)
        val trayImages = trayZip.entries().toList().map { entry ->
            val imageBytes = readFile(trayZip.getInputStream(entry))
            Toolkit.getDefaultToolkit().createImage(imageBytes)
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

    fun getPauseString(): String {
        return pauseString.get()
    }

    fun pauseStringProperty(): StringProperty {
        return pauseString
    }

    fun setPauseString(pauseString: String) {
        this.pauseString.set(pauseString)
    }

    private fun startTimer(shouldNotify: Boolean) {
        setPauseString("Pause")
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

    fun onExitClick() {
        /*if (shouldMinimizeToTray) {
            ((Stage) root.getScene().getWindow()).hide();
        } else {
            fadeOut((Stage) root.getScene().getWindow(), event -> System.exit(0));
        }*/

        fadeOut(root.scene.window as Stage, EventHandler { exitProcess(0) })
    }

    fun minimizeApp() {
        /*Stage.getWindows().filtered(Window::isShowing).forEach(window ->
                ((Stage) window).setIconified(true)
        );*/

        fadeOut(root.scene.window as Stage, EventHandler {
            // make it opaque again, so it'll reappear properly if they click the taskbar
            (root.scene.window as Stage).opacity = 1.0
            (root.scene.window as Stage).hide()
        })
    }

    fun restart() = startTimer(false)

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
            setPauseString("Pause")
        } else {
            timeline.pause()
            trayAnimation.pause()
            setPauseString("Resume")
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
