package com.pleon.donim.node

import com.pleon.donim.APP_BASE_COLOR
import com.pleon.donim.APP_NAME
import com.pleon.donim.extension.div
import com.pleon.donim.extension.times
import com.pleon.donim.node.Animatable.AnimationProperties
import com.pleon.donim.util.*
import javafx.animation.Timeline
import javafx.beans.property.SimpleDoubleProperty
import javafx.util.Duration
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.event.ActionListener
import javax.imageio.ImageIO

private const val START_ANGLE = 0
private const val END_ANGLE = 180
private val ICON_BASE_COLOR = APP_BASE_COLOR

class Tray : Animatable {

    private val popupMenu = PopupMenu()
    private val trayImage = ImageIO.read("/img/logo-tray.png".toURL())
    private val trayIcon = TrayIcon(trayImage, APP_NAME, popupMenu)
    private var hueShift = 0.0
    private var angle = 0.0
    private var timer = Timeline()
    private val fraction = SimpleDoubleProperty(0.0)
    private lateinit var animationProperties: AnimationProperties

    init { fraction.addListener { _, _, _ -> tick() } }

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

    override fun resetAnimation(properties: AnimationProperties) {
        animationProperties = properties
        timer.stop()
        timer = createTimer(animationProperties.duration, fraction) { timer.stop() /* Also prevents blinking */ }
    }

    override fun startAnimation() = timer.play()

    override fun pauseAnimation() {
        timer.pause()
        hueShift = animationProperties.pauseColor.hue - ICON_BASE_COLOR.hue
        trayIcon.image = trayImage.rotate(angle).tint(hueShift)
    }

    override fun endAnimation(isGraceful: Boolean, graceDuration: Duration) {
        timer.rate = when {
            isGraceful -> animationProperties.duration * (1 - fraction.value) / graceDuration
            else -> animationProperties.duration * (1 - fraction.value) / Duration.ONE
        }
        timer.play() // for when the ending is called while paused
    }

    private fun tick() {
        val distanceBetweenStartAndBaseColor = animationProperties.startColor.hue - ICON_BASE_COLOR.hue
        val colorRange = animationProperties.endColor.hue - animationProperties.startColor.hue
        hueShift = distanceBetweenStartAndBaseColor + colorRange * fraction.value
        angle = interpolate(START_ANGLE, END_ANGLE, fraction.value)
        trayIcon.image = trayImage.rotate(angle).tint(hueShift)
    }
}
