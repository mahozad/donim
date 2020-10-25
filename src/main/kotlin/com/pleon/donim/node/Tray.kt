package com.pleon.donim.node

import com.pleon.donim.*
import com.pleon.donim.Animatable.AnimationProperties
import com.pleon.donim.util.AnimationUtil.interpolate
import com.pleon.donim.util.DecorationUtil
import com.pleon.donim.util.ImageUtil.rotate
import com.pleon.donim.util.ImageUtil.tint
import javafx.application.Platform
import javafx.stage.Stage
import javafx.util.Duration
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.TrayIcon
import javax.imageio.ImageIO
import kotlin.system.exitProcess

private const val START_ANGLE = 0
private const val END_ANGLE = 180
private val TOTAL_ANIMATION_DURATION = Duration.seconds(8.0)
private val TOTAL_MOVEMENT_DURATION = Duration.seconds(3.0)
private val FRAME_DURATION = Duration.millis(30.0)

class Tray(stage: Stage) : Animatable {


    // FIXME: End (skip) the animation multiple times successively to see a bug in color not changing


    private var paused = true
    private var shouldEnd = false // For graceful ending
    private var shouldReset = false // For graceful resetting
    private var isGracing = false
    private var hueShift = 0.0
    private var angle = 0.0
    private var trayImage = ImageIO.read(javaClass.getResource("/img/logo-tray.png"))
    private var trayIcon = TrayIcon(trayImage, APP_NAME, makePopupMenu(stage))
    private lateinit var mainTimer: Timer
    private lateinit var moveTimer: Timer
    private lateinit var hueTimer: Timer
    private lateinit var onEnd: () -> Unit
    private lateinit var animationProperties: AnimationProperties

    init {
        trayIcon.addActionListener { Platform.runLater { stage.show().also { DecorationUtil.centerOnScreen(stage) } } }
        SystemTray.getSystemTray().add(trayIcon)
    }

    private fun makePopupMenu(stage: Stage): PopupMenu {
        val popup = PopupMenu()
        popup.add(newMenuItem("Show Window") { Platform.runLater { stage.show().also { DecorationUtil.centerOnScreen(stage) } } })
        val muteMenuItem = newMenuItem("Mute") { }
        muteMenuItem.addActionListener {
            // TODO:
            // isMuted = !isMuted
            muteMenuItem.label = if (false/*isMuted*/) "Unmute" else "Mute"
        }
        popup.add(muteMenuItem)
        popup.add(newMenuItem("Exit") { exitProcess(0) })
        return popup
    }

    private fun newMenuItem(title: String, listener: (java.awt.event.ActionEvent) -> Unit): MenuItem {
        return MenuItem(title).apply { addActionListener(listener) }
    }

    private fun createTimers() {
        createMainTimer()
        createMoveTimer()
        createHueTimer()
    }

    private fun createMainTimer() {
        mainTimer = Timer(animationProperties.duration, FRAME_DURATION)
        mainTimer.elapsedTimeProperty().addListener { _, _, _ ->
            trayIcon.image = trayImage.rotate(angle).tint(hueShift)
            if (angle.toInt() == START_ANGLE || angle.toInt() == END_ANGLE) {
                if (paused) pause()
                if (shouldEnd) end()
                if (shouldReset) reset()
            }
        }
    }

    private fun createMoveTimer() {
        // The movement animation is static and does not depend on the properties of the animation
        // such as its duration, direction or colors. So this animation runs infinitely unless
        // otherwise specified via flags that it should stop, reset, etc.
        // This makes it run seamlessly when paused/reset and started again immediately.
        // This also prevents early termination when paused multiple times in between.
        moveTimer = Timer(Duration.INDEFINITE, FRAME_DURATION)
        moveTimer.elapsedTimeProperty().addListener { _, _, elapsedTime ->
            val animationElapsedMove = (elapsedTime % TOTAL_ANIMATION_DURATION).coerceAtMost(TOTAL_MOVEMENT_DURATION.toMillis())
            val animationFraction = animationElapsedMove / TOTAL_MOVEMENT_DURATION.toMillis()
            angle = interpolate(START_ANGLE, END_ANGLE, animationFraction)
        }
    }

    private fun createHueTimer() {
        hueTimer = Timer(animationProperties.duration, FRAME_DURATION)
        hueTimer.elapsedTimeProperty().addListener { _, _, elapsedTime ->
            // Because the icon has the base color by default
            val distanceBetweenStartAndBaseColor = animationProperties.startColor.hue - APP_BASE_COLOR.hue
            val colorRange = animationProperties.endColor.hue - animationProperties.startColor.hue
            val periodFraction = elapsedTime / animationProperties.duration
            hueShift = if (paused) 0.0 else distanceBetweenStartAndBaseColor + colorRange * periodFraction
        }
    }

    private fun reset() {
        mainTimer.stop()
        moveTimer.stop()
        // No need to recreate the moveTimer
        createMainTimer()
        shouldReset = false
    }

    private fun pause() {
        moveTimer.stop()
        mainTimer.stop()
    }

    private fun end() {
        moveTimer.stop()
        if (!isGracing) {
            mainTimer.stop()
            onEnd()
            shouldEnd = false
        }
    }

    override fun startAnimation() {
        if (!this::mainTimer.isInitialized) createTimers()
        paused = false
        // to not restart the movement again immediately (for when startAnimation is called immediately after a reset)
        shouldReset = false
        mainTimer.start()
        moveTimer.start()
        hueTimer.start()
    }

    override fun pauseAnimation() {
        hueTimer.stop()
        hueShift = 0.0
        paused = true
    }

    override fun resetAnimation(properties: AnimationProperties) {
        animationProperties = properties
        if (this::mainTimer.isInitialized) shouldReset = true
        if (this::hueTimer.isInitialized) {
            hueTimer.stop()
            createHueTimer()
        }
    }

    override fun endAnimation(onEnd: () -> Unit, graceful: Boolean, graceDuration: Duration) {
        if (graceful) {
            this.onEnd = onEnd
            paused = false
            shouldEnd = true
            isGracing = true
            hueTimer.stop()
            createGraceHueTimer(graceDuration)
            hueTimer.start()
            mainTimer.start()
        } else {
            shouldEnd = true
            hueTimer.reset()
            hueTimer.stop()
            moveTimer.reset()
            moveTimer.stop()
            onEnd()
        }
    }

    private fun createGraceHueTimer(graceDuration: Duration) {
        val startHue = hueShift
        hueTimer = Timer(graceDuration, FRAME_DURATION, onEnd = { isGracing = false })
        hueTimer.elapsedTimeProperty().addListener { _, _, elapsedTime ->
            val graceColorRange = (animationProperties.endColor.hue - APP_BASE_COLOR.hue) - startHue
            val graceFraction = elapsedTime / graceDuration
            hueShift = startHue + graceFraction * graceColorRange
        }
    }
}
