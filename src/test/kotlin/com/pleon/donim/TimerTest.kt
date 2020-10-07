package com.pleon.donim

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration

class TimerTest {

    lateinit var timer: Timer
    lateinit var duration: Duration

    @BeforeEach
    fun setup() {
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
    fun `after start, the remaining time should decrease`() {
        timer.start()
        Thread.sleep(50)

        assertThat(timer.remainingTimeProperty().value).isNotEqualTo(duration)
    }

    @Test
    fun `after stop, the remaining time should not change`() {
        timer.start()
        timer.stop()
        val firstSample = timer.remainingTimeProperty().value
        Thread.sleep(50)
        val secondSample = timer.remainingTimeProperty().value

        assertThat(firstSample).isEqualTo(secondSample)
    }

    @Test
    fun `reset the timer - "isStarted" should be true`() {
        timer.start()
        Thread.sleep(50)
        timer.reset()

        assertThat(timer.isStarted).isEqualTo(true)
    }

    @Test
    fun `reset the timer - the remaining time should reset`() {
        timer.start()
        Thread.sleep(50)
        timer.reset()
        val remainingTime = timer.remainingTimeProperty().value

        assertThat(remainingTime)
                .isGreaterThanOrEqualTo(duration.minusMillis(1))
                .isLessThanOrEqualTo(duration)
    }

    @Test
    fun `reset the timer - the remaining time should decrease`() {
        timer.start()
        Thread.sleep(50)
        timer.reset()
        Thread.sleep(50)
        val remainingTime = timer.remainingTimeProperty().value

        assertThat(remainingTime).isLessThan(duration)
    }
}
