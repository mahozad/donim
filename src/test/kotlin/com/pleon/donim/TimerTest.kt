package com.pleon.donim

import javafx.stage.Stage
import javafx.util.Duration
import javafx.util.Duration.ONE
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start

@ExtendWith(ApplicationExtension::class)
class TimerTest {

    lateinit var timer: Timer
    lateinit var duration: Duration
    lateinit var updateRate: Duration

    @Start
    fun start(stage: Stage) {
        duration = Duration.seconds(60.0)
        updateRate = Duration.seconds(1.0)
        timer = Timer(duration, updateRate)
    }

    @Test
    fun `create new timer - "isStarted" should be false`() {
        assertThat(timer.isStarted).isEqualTo(false)
    }

    @Test
    fun `create new timer - remaining time should be equal to provided duration`() {
        assertThat(timer.remainingTimeProperty().value).isEqualTo(duration)
    }

    @Test
    fun `start the timer - "isStarted" should be true`() {
        timer.start()

        assertThat(timer.isStarted).isEqualTo(true)
    }

    @Test
    fun `start the timer - remaining time should decrease`(robot: FxRobot) {
        timer.start()
        robot.sleep(updateRate.multiply(2.5).toMillis().toLong())

        assertThat(timer.remainingTimeProperty().value)
                .isBetween(duration - updateRate.multiply(2.0), duration - updateRate - ONE)
    }

    @Test
    fun `start then stop the timer - "isStarted" should be false`() {
        timer.start()
        timer.stop()

        assertThat(timer.isStarted).isEqualTo(false)
    }

    @Test
    fun `stop the timer - remaining time should not change`(robot: FxRobot) {
        timer.start()
        timer.stop()
        val firstSample = timer.remainingTimeProperty().value
        robot.sleep(updateRate.multiply(2.5).toMillis().toLong())
        val secondSample = timer.remainingTimeProperty().value

        assertThat(firstSample).isEqualTo(secondSample)
    }

    @Test
    fun `reset the timer - "isStarted" should be true`(robot: FxRobot) {
        timer.start()
        robot.sleep(50)
        timer.reset()

        assertThat(timer.isStarted).isEqualTo(true)
    }

    @Test
    fun `reset the timer - remaining time should reset`(robot: FxRobot) {
        timer.start()
        robot.sleep(50)
        timer.reset()
        val remainingTime = timer.remainingTimeProperty().value

        assertThat(remainingTime).isBetween(duration - ONE, duration)
    }

    @Test
    fun `reset the timer - remaining time should decrease`(robot: FxRobot) {
        timer.start()
        robot.sleep(50)
        timer.reset()
        robot.sleep(updateRate.multiply(3.0).toMillis().toLong())
        val remainingTime = timer.remainingTimeProperty().value

        assertThat(remainingTime).isBetween(duration - updateRate.multiply(3.0), duration - updateRate.multiply(2.0))
    }

    @Test
    fun `resume the timer - remaining time should not reset`(robot: FxRobot) {
        val sleepTime = updateRate.multiply(2.0)

        timer.start()
        Thread.sleep(sleepTime.multiply(2.0).toMillis().toLong())
        timer.stop()
        timer.start()
        val remainingTime = timer.remainingTimeProperty().value

        assertThat(remainingTime).isBetween(duration - updateRate.multiply(4.0), duration - sleepTime)
    }
}
