package com.pleon.donim

import javafx.stage.Stage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import java.time.Duration

@ExtendWith(ApplicationExtension::class)
class TimerTest {

    lateinit var timer: Timer
    lateinit var duration: Duration

    @Start
    fun start(stage: Stage) {
        duration = Duration.ofSeconds(60)
        timer = Timer(duration)
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
        robot.sleep(50)

        assertThat(timer.remainingTimeProperty.value).isNotEqualTo(duration)
    }

    @Test
    fun `after stop, the remaining time should not change`(robot: FxRobot) {
        timer.start()
        timer.stop()
        val firstSample = timer.remainingTimeProperty.value
        robot.sleep(50)
        val secondSample = timer.remainingTimeProperty.value

        assertThat(firstSample).isEqualTo(secondSample)
    }

        assertThat(firstDuration).isEqualTo(secondDuration)
    }
}
