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

    @Start
    private fun start(stage: Stage) {
        // val layout: Parent = FXMLLoader.load(javaClass.getResource("/fxml/scene-splash.fxml"))
        // stage.scene = Scene(layout)
        // stage.show()
    }

    @Test
    fun `a new timer should have "started" field as false`() {
        val timer = Timer(Duration.ZERO)

        assertThat(timer.isStarted).isEqualTo(false)
    }

    @Test
    fun `start the timer`() {
        val duration = Duration.ofSeconds(60)
        val timer = Timer(duration)

        timer.start()

        assertThat(timer.isStarted).isEqualTo(true)
    }

    @Test
    fun `start and then stop the timer`() {
        val duration = Duration.ofSeconds(60)
        val timer = Timer(duration)

        timer.start()
        timer.stop()

        assertThat(timer.isStarted).isEqualTo(false)
    }

    @Test
    fun `after start, the remaining time should decrease`(robot: FxRobot) {
        val duration = Duration.ofSeconds(60)
        val timer = Timer(duration)

        timer.start()
        robot.sleep(50)

        assertThat(timer.remainingTimeProperty.value).isNotEqualTo(duration)
    }

    @Test
    fun `after stop, the remaining time should not change`(robot: FxRobot) {
        val duration = Duration.ofSeconds(60)
        val timer = Timer(duration)

        timer.start()
        timer.stop()
        val firstDuration = timer.remainingTimeProperty.value
        robot.sleep(50)
        val secondDuration = timer.remainingTimeProperty.value

        assertThat(firstDuration).isEqualTo(secondDuration)
    }
}
