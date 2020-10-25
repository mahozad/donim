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

private val TOTAL_ANIMATION_DURATION = Duration.seconds(8.0)
private val TOTAL_MOVEMENT_DURATION = Duration.seconds(3.0)
private val FRAME_DURATION = Duration.millis(30.0)

class Tray(stage: Stage) : Animatable {

    private var paused = true
    private var shouldEnd = false
    private var shouldReset = false
    private var gracing = false
    private var hueShift = 0.0
    private var trayImage = ImageIO.read(javaClass.getResource("/img/logo-tray.png"))
    private var trayIcon = TrayIcon(trayImage, APP_NAME, makePopupMenu(stage))
    private lateinit var movementTimer: Timer
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
        createHueTimer()
        createMovementTimer()
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

    private fun createMovementTimer() {
        movementTimer = Timer(animationProperties.duration, FRAME_DURATION)
        movementTimer.elapsedTimeProperty().addListener { _, _, elapsedTime ->
            val animationElapsedMove = (elapsedTime % TOTAL_ANIMATION_DURATION).coerceAtMost(TOTAL_MOVEMENT_DURATION.toMillis())
            val animationFraction = animationElapsedMove / TOTAL_MOVEMENT_DURATION.toMillis()
            val angle = interpolate(0, 180, animationFraction)
            trayIcon.image = trayImage.rotate(angle).tint(hueShift)
            if (angle == 0.0 || angle == 180.0) {
                if (paused) pause()
                if (shouldReset) reset()
                if (shouldEnd && !gracing) end()
            }
        }
    }

    private fun reset() {
        movementTimer.stop()
        createMovementTimer()
        movementTimer.start()
        shouldReset = false
    }

    private fun pause() = movementTimer.stop()

    private fun end() {
        movementTimer.stop()
        onEnd()
        shouldEnd = false
    }

    override fun startAnimation() {
        if (!this::movementTimer.isInitialized || !this::hueTimer.isInitialized) createTimers()
        paused = false
        // to not restart the movement again immediately (for when startAnimation is called immediately after a reset)
        shouldReset = false
        hueTimer.start()
        movementTimer.start()
    }

    override fun pauseAnimation() {
        hueShift = 0.0
        paused = true
        hueTimer.stop()
        // To ensure the icon movement is complete, it is stopped in the keyframe
    }

    override fun resetAnimation(properties: AnimationProperties) {
        if (this::animationProperties.isInitialized) shouldReset = true
        animationProperties = properties
        if (this::hueTimer.isInitialized) {
            hueTimer.stop()
            createHueTimer()
        }
        // To ensure the icon movement is complete, it is stopped in the keyframe
    }

    override fun endAnimation(onEnd: () -> Unit, graceful: Boolean, graceDuration: Duration) {
        if (graceful) {
            this.onEnd = onEnd
            shouldEnd = true
            gracing = true
            hueTimer.stop()
            val startHue = hueShift
            hueTimer = Timer(graceDuration, FRAME_DURATION, onEnd = { gracing = false })
            hueTimer.elapsedTimeProperty().addListener { _, _, elapsedTime ->
                val graceColorRange = (animationProperties.endColor.hue - APP_BASE_COLOR.hue) - startHue
                val graceFraction = elapsedTime / graceDuration
                hueShift = startHue + graceFraction * graceColorRange
            }
            hueTimer.start()
            // To ensure the icon movement is complete, it is stopped in the keyframe
        } else {
            shouldEnd = true
            hueTimer.reset()
            hueTimer.stop()
            movementTimer.reset()
            movementTimer.stop()
            onEnd()
        }
    }
}
