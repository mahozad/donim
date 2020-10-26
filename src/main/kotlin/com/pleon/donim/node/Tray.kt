package com.pleon.donim.node

import com.pleon.donim.*
import com.pleon.donim.Animatable.AnimationProperties
import com.pleon.donim.util.AnimationUtil.interpolate
import com.pleon.donim.util.DecorationUtil
import com.pleon.donim.util.ImageUtil.rotate
import com.pleon.donim.util.ImageUtil.tint
import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.application.Platform
import javafx.beans.property.SimpleDoubleProperty
import javafx.event.ActionEvent
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

class Tray(stage: Stage) : Animatable {

    private var hueShift = 0.0
    private var angle = 0.0
    private var trayImage = ImageIO.read(javaClass.getResource("/img/logo-tray.png"))
    private var trayIcon = TrayIcon(trayImage, APP_NAME, makePopupMenu(stage))
    private var timer = Timeline()
    private var endFunction: () -> Unit = {}
    private val fraction = SimpleDoubleProperty(0.0)
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

    override fun setupAnimation(properties: AnimationProperties, onEnd: () -> Unit) {
        animationProperties = properties
        endFunction = onEnd
        timer.stop()
        createTimer()
    }

    override fun startAnimation() {
        timer.play()
    }

    override fun pauseAnimation() {
        timer.pause()
        hueShift = 0.0
        trayIcon.image = trayImage.rotate(angle).tint(hueShift)
    }

    override fun resetAnimation() {
        // TODO: Also remove listeners from timer properties to avoid memory leak
        timer.stop()
        createTimer()
    }

    override fun endAnimation(isGraceful: Boolean, graceDuration: Duration) {
        if (isGraceful) {
            timer.rate = animationProperties.duration * (1 - fraction.value) / graceDuration
            timer.play() // for when the ending is called while paused
        } else {
            timer.jumpTo(animationProperties.duration)
            endFunction() // maybe not needed?
        }
    }

    private fun createTimer() {
        val startKeyFrame = KeyFrame(Duration.ZERO, KeyValue(fraction, 0))
        val endKeyFrame = KeyFrame(animationProperties.duration, onAnimationEnd(), KeyValue(fraction, 1))
        timer = Timeline(startKeyFrame, endKeyFrame)
        fraction.addListener { _, _, _ -> tick() }
        timer.cycleCount = Animation.INDEFINITE
    }

    private fun onAnimationEnd(): (event: ActionEvent) -> Unit = {
        timer.stop() // to prevent blinking
        endFunction()
    }

    private fun tick() {
        // Because the icon has the base color by default
        val distanceBetweenStartAndBaseColor = animationProperties.startColor.hue - APP_BASE_COLOR.hue
        val colorRange = animationProperties.endColor.hue - animationProperties.startColor.hue
        hueShift = distanceBetweenStartAndBaseColor + colorRange * fraction.value
        angle = interpolate(START_ANGLE, END_ANGLE, fraction.value)
        trayIcon.image = trayImage.rotate(angle).tint(hueShift)
    }
}
