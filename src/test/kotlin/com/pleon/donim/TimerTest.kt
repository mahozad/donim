package com.pleon.donim

import javafx.stage.Stage
import javafx.util.Duration
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
    fun `a new timer should have "started" field as false`() {
        assertThat(timer.isStarted).isEqualTo(false)
    }

    @Test
    fun `start the timer`() {
        timer.start()

        assertThat(timer.isStarted).isEqualTo(true)
    }

    @Test
    fun `start and then stop the timer`() {
        timer.start()
        timer.stop()

        assertThat(timer.isStarted).isEqualTo(false)
    }

    @Test
    fun `after start, the remaining time should decrease`(robot: FxRobot) {
        timer.start()
        robot.sleep(updateRate.multiply(2.0).toMillis().toLong())

        assertThat(timer.remainingTimeProperty().value).isNotEqualTo(duration)
    }

    @Test
    fun `after stop, the remaining time should not change`(robot: FxRobot) {
        timer.start()
        timer.stop()
        val firstSample = timer.remainingTimeProperty().value
        robot.sleep(50)
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
    fun `reset the timer - the remaining time should reset`(robot: FxRobot) {
        timer.start()
        robot.sleep(50)
        timer.reset()
        val remainingTime = timer.remainingTimeProperty().value

        assertThat(remainingTime).isGreaterThanOrEqualTo(duration.subtract(Duration.millis(1.0)))
        assertThat(remainingTime).isLessThanOrEqualTo(duration)
    }

    @Test
    fun `reset the timer - the remaining time should decrease`(robot: FxRobot) {
        timer.start()
        robot.sleep(50)
        timer.reset()
        robot.sleep(updateRate.multiply(2.0).toMillis().toLong())
        val remainingTime = timer.remainingTimeProperty().value

        assertThat(remainingTime).isLessThan(duration)
    }

    @Test
    fun `resume the timer - remaining time should not reset`(robot: FxRobot) {
        val sleepTime = updateRate.multiply(2.0)

        timer.start()
        robot.sleep(sleepTime.toMillis().toLong())
        timer.stop()
        timer.start()
        val remainingTime = timer.remainingTimeProperty().value

        assertThat(remainingTime).isLessThan(duration.subtract(sleepTime))
    }
}
