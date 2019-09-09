package com.pleon.chopchop.controller

import com.pleon.chopchop.model.Period.BREAK
import com.pleon.chopchop.model.Period.WORK
import com.pleon.chopchop.node.CircularProgressBar
import com.pleon.chopchop.util.AnimationUtil.fadeOut
import com.pleon.chopchop.util.ImageUtil.getImage
import com.pleon.chopchop.util.ThemeUtil
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.stage.Stage
import javafx.util.Duration
import java.awt.SystemTray
import java.awt.Toolkit
import kotlin.system.exitProcess

class MainController {

    @FXML private lateinit var progressBar: CircularProgressBar
    @FXML private lateinit var restart: Button
    @FXML private lateinit var skip: Button
    @FXML private lateinit var root: Node

    private var xOffset = 0.0
    private var yOffset = 0.0
    private var period = WORK
    private val remainingTimeString = SimpleStringProperty(String.format("%02d:%02d",
            period.length / 60, period.length % 60))
    private val pauseString = SimpleStringProperty("Start")
    private var remainingTime = period.length
    private val timeline = Timeline()
    private var paused = true
    private lateinit var trayAnimation: Timeline

    // @FXML // required if method is not public
    fun initialize() {
        ThemeUtil.applyTheme(root)
        ThemeUtil.setOnToggled(InvalidationListener { ThemeUtil.applyTheme(root) })

        val trayImages = arrayOfNulls<java.awt.Image>(53)
        for (i in trayImages.indices) {
            val path = "/tray/${i + 1}.png"
            trayImages[i] = Toolkit.getDefaultToolkit().createImage(getImage(path))
        }

        trayAnimation = Timeline()
        trayAnimation.cycleCount = Timeline.INDEFINITE
        trayAnimation.keyFrames.add(KeyFrame(Duration.millis(50.0),
                object : EventHandler<ActionEvent> {
                    var firstFrameDelay = 0
                    var i = 0

                    override fun handle(event: ActionEvent) {
                        val trayIcon = SystemTray.getSystemTray().trayIcons[0]
                        trayIcon.image = trayImages[i]
                        if (i == 0 && firstFrameDelay < 100) {
                            firstFrameDelay++
                            if (firstFrameDelay == 100) {
                                firstFrameDelay = 0
                                i++
                            }
                        } else {
                            i = (i + 1) % trayImages.size
                        }
                    }
                }))

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

    private fun startTimer(shouldShowNotification: Boolean) {
        setPauseString("Pause")
        trayAnimation.play()
        remainingTime = period.length
        timeline.cycleCount = remainingTime
        val trayIcon = SystemTray.getSystemTray().trayIcons[0]
        if (shouldShowNotification) {
            trayIcon.displayMessage(period.toString(), period.notification, period.notificationType)
        }
        trayIcon.toolTip = "Chop chop: $period"
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
                setRemainingTimeString(String.format("%02d:%02d", remainingTime / 60, remainingTime % 60))
                progressBar.tick(remainingTime.toDouble() / period.length, period.baseColor)
                remainingTime--
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

    fun skip() {
        period = if (period == WORK) BREAK else WORK
        timeline.stop()
        startTimer(false)
    }

    fun toggleTheme() = ThemeUtil.toggleTheme()

}
