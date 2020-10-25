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

    private var reseted = false
    private var paused = true
    private var ended = false
    private var trayImage = ImageIO.read(javaClass.getResource("/img/logo-tray.png"))
    private var trayIcon = TrayIcon(trayImage, APP_NAME, makePopupMenu(stage))
    private lateinit var timer: Timer
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

    private fun createTimer() {
        timer = Timer(animationProperties.duration, FRAME_DURATION)
        timer.elapsedTimeProperty().addListener { _, _, elapsedTime ->
            // Because the icon has the base color by default
            val distanceBetweenStartAndBaseColor = animationProperties.startColor.hue - APP_BASE_COLOR.hue
            val colorRange = animationProperties.endColor.hue - animationProperties.startColor.hue
            val periodFraction = elapsedTime / animationProperties.duration
            val hueShift = if (paused) 0.0 else distanceBetweenStartAndBaseColor + colorRange * periodFraction
            val animationElapsedMove = (elapsedTime.toMillis() % TOTAL_ANIMATION_DURATION.toMillis()).coerceAtMost(TOTAL_MOVEMENT_DURATION.toMillis())
            val animationFraction = animationElapsedMove / TOTAL_MOVEMENT_DURATION.toMillis()
            val angle = interpolate(0, 180, animationFraction)
            trayIcon.image = trayImage.rotate(angle).tint(hueShift)

            if (angle == 0.0 || angle == 180.0) {
                if (reseted) runReset()
                if (paused) runPause()
                if (ended) runEnd()
            }
        }
    }

    private fun runReset() {
        timer.stop()
        createTimer()
        timer.start()
        reseted = false
    }

    private fun runPause() = timer.stop()

    private fun runEnd() {
        timer.stop()
        onEnd()
        ended = false
    }

    override fun startAnimation() {
        if (!this::timer.isInitialized) createTimer()
        paused = false
        timer.start()
    }

    override fun pauseAnimation() {
        paused = true
        // To ensure the icon movement is complete, the animation is stopped in the keyframe
    }

    override fun resetAnimation(properties: AnimationProperties) {
        if (this::animationProperties.isInitialized) reseted = true
        animationProperties = properties
        // To ensure the icon movement is complete, the animation is stopped in the keyframe
    }

    override fun endAnimation(onEnd: () -> Unit, graceful: Boolean, graceDuration: Duration) {
        this.ended = true
        this.onEnd = onEnd
        // To ensure the icon movement is complete, the animation is stopped in the keyframe
    }
}
