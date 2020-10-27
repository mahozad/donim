package com.pleon.donim.node

import com.pleon.donim.*
import com.pleon.donim.Animatable.AnimationProperties
import com.pleon.donim.util.AnimationUtil.interpolate
import com.pleon.donim.util.ImageUtil.rotate
import com.pleon.donim.util.ImageUtil.tint
import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.beans.property.SimpleDoubleProperty
import javafx.event.ActionEvent
import javafx.util.Duration
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.event.ActionListener
import javax.imageio.ImageIO

private const val START_ANGLE = 0
private const val END_ANGLE = 180

class Tray : Animatable {

    private val popupMenu = PopupMenu()
    private val trayImage = ImageIO.read(javaClass.getResource("/img/logo-tray.png"))
    private val trayIcon = TrayIcon(trayImage, APP_NAME, popupMenu)
    private var hueShift = 0.0
    private var angle = 0.0
    private var timer = Timeline()
    private var endFunction: () -> Unit = {}
    private val fraction = SimpleDoubleProperty(0.0)
    private lateinit var animationProperties: AnimationProperties

    fun addActionListener(listener: ActionListener) = trayIcon.addActionListener(listener)

    fun show() = SystemTray.getSystemTray().add(trayIcon)

    fun setTooltip(tooltip: String) = trayIcon.setToolTip(tooltip)

    fun showNotification(title: String, text: String, messageType: TrayIcon.MessageType) {
        trayIcon.displayMessage(title, text, messageType)
    }

    fun addMenuItem(label: String, listener: (java.awt.event.ActionEvent) -> Unit) {
        val menuItem = MenuItem(label)
        menuItem.addActionListener(listener)
        popupMenu.add(menuItem)
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

    override fun resetAnimation() = timer.stop()

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
