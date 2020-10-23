package com.pleon.donim.controller

import com.pleon.donim.APP_BASE_COLOR
import com.pleon.donim.APP_NAME
import com.pleon.donim.Animatable
import com.pleon.donim.div
import com.pleon.donim.exception.SettingNotFoundException
import com.pleon.donim.model.DEFAULT_BREAK_DURATION
import com.pleon.donim.model.DEFAULT_FOCUS_DURATION
import com.pleon.donim.model.Period.BREAK
import com.pleon.donim.model.Period.WORK
import com.pleon.donim.node.CircularProgressBar
import com.pleon.donim.util.AnimationUtil.FadeMode.OUT
import com.pleon.donim.util.AnimationUtil.MoveDirection.BOTTOM_RIGHT
import com.pleon.donim.util.AnimationUtil.fade
import com.pleon.donim.util.AnimationUtil.interpolate
import com.pleon.donim.util.AnimationUtil.move
import com.pleon.donim.util.DecorationUtil.centerOnScreen
import com.pleon.donim.util.ImageUtil.rotate
import com.pleon.donim.util.ImageUtil.tint
import com.pleon.donim.util.PersistentSettings
import com.pleon.donim.util.SnapSide
import com.pleon.donim.util.snapTo
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
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
import javafx.scene.text.Text
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.util.Duration
import javafx.util.Duration.INDEFINITE
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.image.BufferedImage
import java.util.*
import javax.imageio.ImageIO
import kotlin.concurrent.timerTask
import kotlin.system.exitProcess

class MainController : BaseController() {

    // For how javafx timeline works see [https://stackoverflow.com/a/36366805/8583692]

    @FXML private lateinit var progressBar: CircularProgressBar
    @FXML private lateinit var restart: Button
    @FXML private lateinit var play: Button
    @FXML private lateinit var skip: Button
    @FXML private lateinit var time: Text
    @FXML lateinit var playIcon: SVGPath

    private var period = WORK
    // NOTE: Could not use period.duration because if the period duration is changed in
    //  settings while the app is running, it will cause bugs in the progress bar
    private var currentPeriodLength = INDEFINITE
    private var isMuted = false
    private var trayFrameNumber = 0
    private var trayAnimation = Timeline()
    private lateinit var trayIcon: TrayIcon
    private lateinit var trayImage: BufferedImage
    private var beep = AudioClip(javaClass.getResource("/sound/beep.mp3").toExternalForm())
    private var remainingTime = SimpleObjectProperty<Duration>()
    private val timeline = Timeline()
    private var paused = true
    private var aboutStage = Stage().apply { initStyle(StageStyle.TRANSPARENT) }
    private var settingsStage = Stage().apply { initStyle(StageStyle.TRANSPARENT) }

    override fun initialize() {
        super.initialize()
        super.makeWindowMovable()
        remainingTime.addListener { _, _, newValue -> time.text = format(newValue) }
        createTrayIcon()
        setupTrayIconAnimation()
        setupMainTimeline()
        applyUserPreferences()
        listenForSettingsChanges()
        WORK.nextPeriod = BREAK
        BREAK.nextPeriod = WORK
        progressBar.resetAnimation(Animatable.AnimationProperties(period.duration, Animatable.AnimationDirection.BACKWARD, period.baseColor, period.nextPeriod.baseColor))
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
        remainingTime.set(period.duration)
        currentPeriodLength = remainingTime.value
    }

    private fun listenForSettingsChanges() {
        PersistentSettings.getObservableProperties().addListener(MapChangeListener {
            if (paused && remainingTime.get() == period.duration) {
                if (it.key == "focus-duration" && period == WORK || it.key == "break-duration" && period == BREAK) {
                    try {
                        remainingTime.set(Duration.minutes(it.valueAdded.toDouble()))
                    } catch (e: Exception) {
                        remainingTime.set(period.defaultDuration)
                    }
                    currentPeriodLength = remainingTime.value
                }
            }

            if (it.key == "focus-duration") {
                WORK.setDuration(it.valueAdded)
            } else if (it.key == "break-duration") {
                BREAK.setDuration(it.valueAdded)
            }
        })
    }

    private fun createTrayIcon() {
        root.sceneProperty().addListener { _, oldScene, newScene ->
            if (!SystemTray.isSupported() || oldScene != null) return@addListener
            val stage = newScene.window as Stage
            trayImage = ImageIO.read(javaClass.getResource("/img/logo-tray.png"))
            trayIcon = TrayIcon(trayImage, APP_NAME, makePopupMenu(stage))
            trayIcon.addActionListener { Platform.runLater { stage.show().also { centerOnScreen(stage) } } }
            SystemTray.getSystemTray().add(trayIcon)
        }
    }

    private fun makePopupMenu(stage: Stage): PopupMenu {
        val popup = PopupMenu()
        popup.add(newMenuItem("Show Window") { Platform.runLater { stage.show().also { centerOnScreen(stage) } } })
        val muteMenuItem = newMenuItem("Mute") { }
        muteMenuItem.addActionListener {
            isMuted = !isMuted
            muteMenuItem.label = if (isMuted) "Unmute" else "Mute"
        }
        popup.add(muteMenuItem)
        popup.add(newMenuItem("Exit") { exitProcess(0) })
        return popup
    }

    private fun newMenuItem(title: String, listener: (java.awt.event.ActionEvent) -> Unit): MenuItem {
        return MenuItem(title).apply { addActionListener(listener) }
    }

    private fun setupTrayIconAnimation() {
        Timer().scheduleAtFixedRate(timerTask { if (!paused) trayAnimation.play() }, 0, 8000)
        val totalFrameNumbers = 3000/*ms*/ / 50/*ms*/
        trayAnimation.cycleCount = totalFrameNumbers
        val periodsColorRange = WORK.baseColor.hue - BREAK.baseColor.hue // FIXME: Duplicated
        trayAnimation.keyFrames.add(KeyFrame(Duration.millis(50.0), {
            // Because the icon has the default base color
            val distanceBetweenPeriodAndBaseColor = period.baseColor.hue - APP_BASE_COLOR.hue
            val hueShift = when {
                paused -> 0.0
                period == WORK -> (distanceBetweenPeriodAndBaseColor - periodsColorRange * (1 - fraction())) / 360
                else           -> (distanceBetweenPeriodAndBaseColor + periodsColorRange * (1 - fraction())) / 360
            }
            val angle = interpolate(0, 180, trayFrameNumber / totalFrameNumbers.toDouble())
            trayIcon.image = trayImage.rotate(angle).tint(hueShift)
            trayFrameNumber = (trayFrameNumber + 1) % totalFrameNumbers
        }))
    }

    private fun fraction() = remainingTime.get().toMillis() / currentPeriodLength.toMillis()

    private fun setupMainTimeline() {
        timeline.keyFrames.add(KeyFrame(Duration.seconds(1.0), {
            remainingTime.set(remainingTime.get().subtract(Duration.seconds(1.0)))
        }))
        timeline.setOnFinished {
            period = if (period == WORK) BREAK else WORK
            startTimer(shouldNotify = true, shouldResetTimer = true)
            progressBar.startAnimation(Animatable.AnimationProperties(period.duration, Animatable.AnimationDirection.BACKWARD, period.baseColor, period.nextPeriod.baseColor))
        }
    }

    private fun startTimer(shouldNotify: Boolean, shouldResetTimer: Boolean) {
        playIcon.content = "m 8,18.1815 c 1.1,0 2,-0.794764 2,-1.766143 V 7.5846429 C 10,6.6132643 9.1,5.8185 8,5.8185 6.9,5.8185 6,6.6132643 6,7.5846429 V 16.415357 C 6,17.386736 6.9,18.1815 8,18.1815 Z M 14,7.5846429 v 8.8307141 c 0,0.971379 0.9,1.766143 2,1.766143 1.1,0 2,-0.794764 2,-1.766143 V 7.5846429 C 18,6.6132643 17.1,5.8185 16,5.8185 c -1.1,0 -2,0.7947643 -2,1.7661429 z"
        if (shouldResetTimer) {
            remainingTime.set(period.duration)
            currentPeriodLength = remainingTime.value
        }
        timeline.cycleCount = (currentPeriodLength / timeline.cycleDuration).toInt()
        timeline.play()
        trayAnimation.play()
        paused = false
        trayIcon.toolTip = "$APP_NAME: $period"
        if (!isMuted && shouldNotify) {
            trayIcon.displayMessage(period.toString(), period.notification, period.notificationType)
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
        timeline.stop() // required so the period won't finish early
        startTimer(shouldNotify = false, shouldResetTimer = true)
        progressBar.startAnimation(Animatable.AnimationProperties(period.duration, Animatable.AnimationDirection.BACKWARD, period.baseColor, period.nextPeriod.baseColor))
    }

    fun pauseResume() {
        paused = !paused
        if (paused) {
            // tray animation is paused in its keyframe event handler
            if (trayFrameNumber == 0) trayIcon.image = trayImage.tint(0.0)
            playIcon.content = "M8 6.82v10.36c0 .79.87 1.27 1.54.84l8.14-5.18c.62-.39.62-1.29 0-1.69L9.54 5.98C8.87 5.55 8 6.03 8 6.82z"
            timeline.pause()
            progressBar.pauseAnimation()
        } else {
            skip.isDisable = false
            restart.isDisable = false
            startTimer(shouldNotify = false, shouldResetTimer = false)
            progressBar.startAnimation()
        }
    }

    fun skip() {
        time.text = "\u23E9" // Fast-forward character
        play.isDisable = true
        restart.isDisable = true
        skip.isDisable = true

        timeline.stop() // required so the counter won't go negative
        period = if (period == WORK) BREAK else WORK
        progressBar.endAnimation({
            startTimer(shouldNotify = false, shouldResetTimer = true)
            progressBar.startAnimation(Animatable.AnimationProperties(period.duration, Animatable.AnimationDirection.BACKWARD, period.baseColor, period.nextPeriod.baseColor))
            play.isDisable = false
            restart.isDisable = false
            skip.isDisable = false
        })
    }

    private fun format(duration: Duration) = String.format("%02d:%02d",
            duration.toMinutes().toInt(), duration.toSeconds().toInt() % 60)

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
