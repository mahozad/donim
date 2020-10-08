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
        duration = Duration.millis(300.0)
        updateRate = Duration.millis(50.0)
        timer = Timer(duration, updateRate)
    }

    @Test
    fun `create new timer - running field should be false`() {
        assertThat(timer.isRunning).isEqualTo(false)
    }

    @Test
    fun `create new timer - remaining time should be equal to provided duration`() {
        assertThat(timer.remainingTimeProperty().value).isEqualTo(duration)
    }

    @Test
    fun `start the timer - started should be true`() {
        timer.start()

        assertThat(timer.isRunning).isEqualTo(true)
    }

    @Test
    fun `start the timer - remaining time should decrease`(robot: FxRobot) {
        timer.start()
        robot.sleep((updateRate * 2.5).toMillis().toLong())
        val remainingTime = timer.remainingTimeProperty().value
        assertThat(remainingTime).isBetween(duration - updateRate * 3, duration - updateRate - ONE)
    }

    @Test
    fun `start then stop the timer - running field should be false`() {
        timer.start()
        timer.stop()

        assertThat(timer.isRunning).isEqualTo(false)
    }

    @Test
    fun `stop the timer - remaining time should not change`(robot: FxRobot) {
        timer.start()
        timer.stop()
        val firstSample = timer.remainingTimeProperty().value
        robot.sleep((updateRate * 3).toMillis().toLong())
        val secondSample = timer.remainingTimeProperty().value

        assertThat(firstSample).isEqualTo(secondSample)
    }

    @Test
    fun `resume the timer - remaining time should not reset`(robot: FxRobot) {
        timer.start()
        robot.sleep((updateRate * 4).toMillis().toLong())
        timer.stop()
        timer.start()
        val remainingTime = timer.remainingTimeProperty().value

        assertThat(remainingTime).isBetween(duration - updateRate * 5, duration - updateRate * 3)
    }

    @Test
    fun `reset the timer - running field should be true`(robot: FxRobot) {
        timer.start()
        robot.sleep((updateRate * 3).toMillis().toLong())
        timer.reset()

        assertThat(timer.isRunning).isEqualTo(true)
    }

    @Test
    fun `reset the timer - remaining time should reset`(robot: FxRobot) {
        timer.start()
        robot.sleep((updateRate * 3).toMillis().toLong())
        timer.reset()
        val remainingTime = timer.remainingTimeProperty().value

        assertThat(remainingTime).isEqualTo(duration)
    }

    @Test
    fun `reset the timer - remaining time should decrease`(robot: FxRobot) {
        timer.start()
        robot.sleep((updateRate * 2).toMillis().toLong())
        timer.reset()
        robot.sleep((updateRate * 4).toMillis().toLong())
        val remainingTime = timer.remainingTimeProperty().value

        assertThat(remainingTime).isBetween(duration - updateRate * 4, duration - updateRate * 2)
    }

    @Test
    fun `start the timer - after finish the remaining time should be 0`(robot: FxRobot) {
        timer.start()
        robot.sleep((duration + updateRate * 4).toMillis().toLong())
        val remainingTime = timer.remainingTimeProperty().value

        assertThat(remainingTime).isEqualTo(Duration.ZERO)
    }

    @Test
    fun `start the timer - after finish the running field should be false`(robot: FxRobot) {
        timer.start()
        robot.sleep((duration + updateRate * 4).toMillis().toLong())

        assertThat(timer.isRunning).isEqualTo(false)
    }
}
