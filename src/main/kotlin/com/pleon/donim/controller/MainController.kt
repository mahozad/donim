package com.pleon.donim.controller

import com.pleon.donim.Animatable.AnimationDirection.BACKWARD
import com.pleon.donim.Animatable.AnimationProperties
import com.pleon.donim.exception.SettingNotFoundException
import com.pleon.donim.model.DEFAULT_BREAK_DURATION
import com.pleon.donim.model.DEFAULT_FOCUS_DURATION
import com.pleon.donim.model.Period.BREAK
import com.pleon.donim.model.Period.WORK
import com.pleon.donim.node.CircularProgressBar
import com.pleon.donim.node.Time
import com.pleon.donim.node.Tray
import com.pleon.donim.util.AnimationUtil.FadeMode.OUT
import com.pleon.donim.util.AnimationUtil.MoveDirection.BOTTOM_RIGHT
import com.pleon.donim.util.AnimationUtil.fade
import com.pleon.donim.util.AnimationUtil.move
import com.pleon.donim.util.PersistentSettings
import com.pleon.donim.util.SnapSide
import com.pleon.donim.util.snapTo
import javafx.collections.MapChangeListener
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
import java.awt.SystemTray

class MainController : BaseController() {

    // For how javafx timeline works see [https://stackoverflow.com/a/36366805/8583692]

    @FXML private lateinit var progressBar: CircularProgressBar
    @FXML private lateinit var restart: Button
    @FXML private lateinit var play: Button
    @FXML private lateinit var skip: Button
    @FXML private lateinit var time: Time
    @FXML lateinit var playIcon: SVGPath

    private lateinit var tray: Tray
    private var period = WORK
    private var isMuted = false
    private var beep = AudioClip(javaClass.getResource("/sound/beep.mp3").toExternalForm())
    private var paused = true
    private var aboutStage = Stage().apply { initStyle(StageStyle.TRANSPARENT) }
    private var settingsStage = Stage().apply { initStyle(StageStyle.TRANSPARENT) }

    override fun initialize() {
        super.initialize()
        super.makeWindowMovable()
        createTrayIcon()
        applyUserPreferences()
        listenForSettingsChanges()
        WORK.nextPeriod = BREAK
        BREAK.nextPeriod = WORK
        progressBar.resetAnimation(AnimationProperties(period.duration, BACKWARD, period.baseColor, period.nextPeriod.baseColor))
        time.resetAnimation(AnimationProperties(period.duration, BACKWARD))
    }

    private fun applyUserPreferences() {
        try {
            WORK.setDuration(PersistentSettings.get("focus-duration"))
        } catch (e: SettingNotFoundException) {
            WORK.setDuration(DEFAULT_FOCUS_DURATION.toMinutes().toInt().toString())
        }
        try {
            BREAK.setDuration(PersistentSettings.get("break-duration"))
        } catch (e: SettingNotFoundException) {
            BREAK.setDuration(DEFAULT_BREAK_DURATION.toMinutes().toInt().toString())
        }
    }

    private fun listenForSettingsChanges() {
        PersistentSettings.getObservableProperties().addListener(MapChangeListener {
            if (it.key == "focus-duration") {
                WORK.setDuration(it.valueAdded)
            } else if (it.key == "break-duration") {
                BREAK.setDuration(it.valueAdded)
            }

            if (paused && time.isFresh()) {
                if (it.key == "focus-duration" && period == WORK || it.key == "break-duration" && period == BREAK) {
                    try {
                        progressBar.resetAnimation(AnimationProperties(period.duration, BACKWARD))
                        time.resetAnimation(AnimationProperties(period.duration, BACKWARD))
                    } catch (e: Exception) {
                        progressBar.resetAnimation(AnimationProperties(period.duration, BACKWARD))
                        time.resetAnimation(AnimationProperties(period.duration, BACKWARD))
                    }
                }
            }
        })
    }

    private fun createTrayIcon() {
        root.sceneProperty().addListener { _, oldScene, newScene ->
            if (!SystemTray.isSupported() || oldScene != null) return@addListener
            tray = Tray(newScene.window as Stage)
            tray.resetAnimation(AnimationProperties(period.duration, BACKWARD, period.baseColor, period.nextPeriod.baseColor))
        }
    }

    private fun startAllThings(shouldNotify: Boolean) {
        playIcon.content = "m 8,18.1815 c 1.1,0 2,-0.794764 2,-1.766143 V 7.5846429 C 10,6.6132643 9.1,5.8185 8,5.8185 6.9,5.8185 6,6.6132643 6,7.5846429 V 16.415357 C 6,17.386736 6.9,18.1815 8,18.1815 Z M 14,7.5846429 v 8.8307141 c 0,0.971379 0.9,1.766143 2,1.766143 1.1,0 2,-0.794764 2,-1.766143 V 7.5846429 C 18,6.6132643 17.1,5.8185 16,5.8185 c -1.1,0 -2,0.7947643 -2,1.7661429 z"
        paused = false

        // TODO:
        // trayIcon.toolTip = "$APP_NAME: $period"

        if (!isMuted && shouldNotify) {

            // TODO:
            // trayIcon.displayMessage(period.toString(), period.notification, period.notificationType)

            beep.play()
        }
    }

    fun close() {
        // Not needed if you do not care about other stages to be closed after process is finished
        if (aboutStage.isShowing) aboutStage.close()
        if (settingsStage.isShowing) settingsStage.close()

        closeWindow(true)
    }

    fun minimize() {
        // Close other stages
        if (aboutStage.isShowing) aboutStage.close()
        if (settingsStage.isShowing) settingsStage.close()

        val delay = Duration.millis(0.0)
        val duration = Duration.millis(100.0)
        fade(root, OUT, delay, duration, EventHandler {
            root.opacity = 1.0 // Make it opaque again, so it'll reappear properly
            root.scene.window.hide()
        })
        move(root.scene.window, BOTTOM_RIGHT, delay, duration)
    }

    fun restart() {
        progressBar.resetAnimation(AnimationProperties(period.duration, BACKWARD, period.baseColor, period.nextPeriod.baseColor))
        progressBar.startAnimation()
        tray.resetAnimation(AnimationProperties(period.duration, BACKWARD, period.baseColor, period.nextPeriod.baseColor))
        tray.startAnimation()
        time.resetAnimation(AnimationProperties(period.duration, BACKWARD))
        time.startAnimation()
        startAllThings(false)
    }

    fun pauseResume() {
        paused = !paused
        if (paused) {



            // TODO:
            // tray animation is paused in its keyframe event handler
            // if (trayFrameNumber == 0) trayIcon.image = trayImage.tint(0.0)


            playIcon.content = "M8 6.82v10.36c0 .79.87 1.27 1.54.84l8.14-5.18c.62-.39.62-1.29 0-1.69L9.54 5.98C8.87 5.55 8 6.03 8 6.82z"
            time.pauseAnimation()
            tray.pauseAnimation()
            progressBar.pauseAnimation()
        } else {
            skip.isDisable = false
            restart.isDisable = false
            startAllThings(shouldNotify = false)
            time.startAnimation()
            tray.startAnimation()
            progressBar.startAnimation()
        }
    }

    fun skip() {
        play.isDisable = true
        restart.isDisable = true
        skip.isDisable = true

        period = if (period == WORK) BREAK else WORK

        tray.endAnimation(onEnd = {
            tray.resetAnimation(AnimationProperties(period.duration, BACKWARD, period.baseColor, period.nextPeriod.baseColor))
            tray.startAnimation()
        })
        time.endAnimation({
            time.resetAnimation(AnimationProperties(period.duration, BACKWARD))
            time.startAnimation()
        })
        progressBar.endAnimation({
            startAllThings(shouldNotify = false)
            progressBar.resetAnimation(AnimationProperties(period.duration, BACKWARD, period.baseColor, period.nextPeriod.baseColor))
            progressBar.startAnimation()
            play.isDisable = false
            restart.isDisable = false
            skip.isDisable = false
        })
    }

    fun showSettings() {
        if (settingsStage.isShowing) return

        val fxmlLoader = FXMLLoader(javaClass.getResource("/fxml/scene-settings.fxml"))
        // val settingsController: SettingsController = fxmlLoader.getController()
        val root: Parent = fxmlLoader.load()

        settingsStage.isResizable = false
        settingsStage.title = "Settings"
        settingsStage.scene = Scene(root).apply { fill = Color.TRANSPARENT }
        settingsStage.icons.add(Image("/img/logo.png"))
        settingsStage.show()
        settingsStage.snapTo(this.root.scene.window, SnapSide.LEFT)
    }

    fun showAbout() {
        if (aboutStage.isShowing) return

        val root = FXMLLoader.load<Parent>(javaClass.getResource("/fxml/scene-about.fxml"))
        aboutStage.isResizable = false
        aboutStage.title = "About"
        aboutStage.scene = Scene(root).apply { fill = Color.TRANSPARENT }
        aboutStage.icons.add(Image("/img/logo.png"))
        aboutStage.show()
        aboutStage.snapTo(this.root.scene.window, SnapSide.RIGHT)
    }
}
