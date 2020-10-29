package com.pleon.donim.controller

import com.pleon.donim.APP_NAME
import com.pleon.donim.Animatable
import com.pleon.donim.Animatable.AnimationDirection.BACKWARD
import com.pleon.donim.Animatable.AnimationProperties
import com.pleon.donim.div
import com.pleon.donim.exception.SettingNotFoundException
import com.pleon.donim.model.DEFAULT_BREAK_DURATION
import com.pleon.donim.model.DEFAULT_FOCUS_DURATION
import com.pleon.donim.model.Period.BREAK
import com.pleon.donim.model.Period.WORK
import com.pleon.donim.node.CircularProgressBar
import com.pleon.donim.node.Time
import com.pleon.donim.node.Tray
import com.pleon.donim.times
import com.pleon.donim.util.AnimationUtil.FadeMode.OUT
import com.pleon.donim.util.AnimationUtil.MoveDirection.BOTTOM_RIGHT
import com.pleon.donim.util.AnimationUtil.fade
import com.pleon.donim.util.AnimationUtil.move
import com.pleon.donim.util.DecorationUtil.createTransparentStage
import com.pleon.donim.util.DecorationUtil.showCentered
import com.pleon.donim.util.PersistentSettings
import com.pleon.donim.util.SnapSide
import com.pleon.donim.util.createTimer
import com.pleon.donim.util.snapTo
import javafx.animation.Animation
import javafx.animation.Timeline
import javafx.beans.property.SimpleDoubleProperty
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
import javafx.util.Duration
import java.awt.MenuItem
import java.awt.SystemTray
import kotlin.system.exitProcess
import javafx.application.Platform.runLater as runOnJavaFXThread

val GRACE_DURATION: Duration = Duration.seconds(2.0)
private val ANIMATION_DIRECTION = BACKWARD
private const val SHOULD_GRACE_ENDING = true
private const val ICON_PLAY = "m 8,18.1815 c 1.1,0 2,-0.794764 2,-1.766143 V 7.5846429 C 10,6.6132643 9.1,5.8185 8,5.8185 6.9,5.8185 6,6.6132643 6,7.5846429 V 16.415357 C 6,17.386736 6.9,18.1815 8,18.1815 Z M 14,7.5846429 v 8.8307141 c 0,0.971379 0.9,1.766143 2,1.766143 1.1,0 2,-0.794764 2,-1.766143 V 7.5846429 C 18,6.6132643 17.1,5.8185 16,5.8185 c -1.1,0 -2,0.7947643 -2,1.7661429 z"
private const val ICON_PAUSE = "M8 6.82v10.36c0 .79.87 1.27 1.54.84l8.14-5.18c.62-.39.62-1.29 0-1.69L9.54 5.98C8.87 5.55 8 6.03 8 6.82z"

class MainController : BaseController() {

    // For how javafx timeline works see [https://stackoverflow.com/a/36366805/8583692]
    @FXML private lateinit var progressBar: CircularProgressBar
    @FXML private lateinit var playIcon: SVGPath
    @FXML private lateinit var restart: Button
    @FXML private lateinit var play: Button
    @FXML private lateinit var skip: Button
    @FXML private lateinit var time: Time

    private lateinit var animatables: Array<Animatable>
    private val beep = AudioClip(javaClass.getResource("/sound/beep.mp3").toExternalForm())
    private val settingsStage = createTransparentStage()
    private val aboutStage = createTransparentStage()
    private val tray = Tray()
    private val progress = SimpleDoubleProperty(0.0)
    private var mainTimer = Timeline()
    private var period = WORK
    private var isMuted = false
    private var shouldNotify = true

    override fun initialize() {
        super.initialize()
        super.makeWindowMovable()
        setupTrayIcon()
        applyUserPreferences()
        listenForSettingsChanges()
        WORK.nextPeriod = BREAK
        BREAK.nextPeriod = WORK
        animatables = arrayOf(progressBar, time, tray)
        for (animatable in animatables) animatable.resetAnimation(createProperties())
        setupMainTimer()
    }

    private fun createProperties(): AnimationProperties {
        val duration = period.duration
        val startColor = period.baseColor
        val endColor = period.nextPeriod.baseColor
        return AnimationProperties(duration, ANIMATION_DIRECTION, startColor, endColor)
    }

    private fun setupMainTimer() {
        mainTimer.stop()
        mainTimer = createTimer(period.duration, progress, this::endFunction)
    }

    private fun endFunction() {
        period = period.nextPeriod
        setupMainTimer()
        if (shouldNotify && !isMuted) {
            tray.showNotification(period.toString(), period.notification, period.notificationType)
            beep.play()
        }
        setButtonsStatus(disable = false)
        for (animatable in animatables) animatable.resetAnimation(createProperties())
        for (animatable in animatables) animatable.startAnimation()
        startMainTimer()
    }

    private fun setButtonsStatus(disable: Boolean) {
        play.isDisable = disable
        restart.isDisable = disable
        skip.isDisable = disable
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

            if (mainTimer.status == Animation.Status.STOPPED) {
                if (it.key == "focus-duration" && period == WORK || it.key == "break-duration" && period == BREAK) {
                    for (animatable in animatables) animatable.resetAnimation(createProperties())
                    setupMainTimer()
                }
            }
        })
    }

    private fun setupTrayIcon() {
        root.sceneProperty().addListener { _, oldScene, newScene ->
            if (!SystemTray.isSupported() || oldScene != null) return@addListener
            val stage = newScene.window as Stage
            tray.addActionListener { runOnJavaFXThread { stage.showCentered() } }
            tray.addMenuItem("Show Window") { runOnJavaFXThread { stage.showCentered() } }
            tray.addMenuItem("Mute") { toggleMute(it.source as MenuItem) }
            tray.addMenuItem("Exit") { exitProcess(0) }
            tray.show()
        }
    }

    private fun toggleMute(menuItem: MenuItem) {
        isMuted = !isMuted
        menuItem.label = if (isMuted) "Unmute" else "Mute"
    }

    private fun startMainTimer() {
        playIcon.content = ICON_PLAY
        tray.setTooltip("$APP_NAME: $period")
        shouldNotify = true
        mainTimer.play()
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
        // call setup instead of reset in case the user changed settings
        for (animatable in animatables) animatable.resetAnimation(createProperties())
        for (animatable in animatables) animatable.startAnimation()
        setupMainTimer()
        startMainTimer()
    }

    fun pauseResume() {
        if (mainTimer.status == Animation.Status.RUNNING) {
            playIcon.content = ICON_PAUSE
            for (animatable in animatables) animatable.pauseAnimation()
            mainTimer.pause()
        } else {
            setButtonsStatus(disable = false)
            for (animatable in animatables) animatable.startAnimation()
            startMainTimer()
        }
    }

    fun skip() {
        setButtonsStatus(disable = true)
        for (animatable in animatables) animatable.endAnimation(SHOULD_GRACE_ENDING, GRACE_DURATION)
        shouldNotify = false
        mainTimer.rate = when {
            SHOULD_GRACE_ENDING -> mainTimer.cuePoints["end"]!! * (1 - progress.value) / GRACE_DURATION
            else -> mainTimer.cuePoints["end"]!! * (1 - progress.value) / Duration.ONE
        }
        mainTimer.play()
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
