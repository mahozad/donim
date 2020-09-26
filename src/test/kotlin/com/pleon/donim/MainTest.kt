package com.pleon.donim

import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.assertions.api.Assertions
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start

@ExtendWith(ApplicationExtension::class)
class MainTest {

    private lateinit var button: Button

    /**
     * Will be called with `@Before` semantics, i. e. before each test method.
     *
     * @param stage Will be injected by the test runner.
     */
    @Start
    private fun start(stage: Stage) {
        button = Button("click me!")
        button.setId("myButton")
        button.setOnAction { actionEvent -> button.setText("clicked!") }
        stage.setScene(Scene(StackPane(button), 100.0, 100.0))
        stage.show()
    }

    /**
     * @param robot Will be injected by the test runner.
     */
    @Test
    fun should_contain_button_with_text(robot: FxRobot) {
        Assertions.assertThat(button).hasText("click me!")
        // or (lookup by css id):
        Assertions.assertThat(robot.lookup("#myButton").queryAs(Button::class.java)).hasText("click me!")
        // or (lookup by css class):
        Assertions.assertThat(robot.lookup(".button").queryAs(Button::class.java)).hasText("click me!")
        // or (query specific type):
        Assertions.assertThat(robot.lookup(".button").queryButton()).hasText("click me!")
    }

    /**
     * @param robot Will be injected by the test runner.
     */
    @Disabled @Test
    fun `when button is clicked text changes` (robot: FxRobot) {
        // when:
        robot.clickOn(".button")

        // then:
        Assertions.assertThat(button).hasText("clicked!")
        // or (lookup by css id):
        Assertions.assertThat(robot.lookup("#myButton").queryAs(Button::class.java)).hasText("clicked!")
        // or (lookup by css class):
        Assertions.assertThat(robot.lookup(".button").queryAs(Button::class.java)).hasText("clicked!")
        // or (query specific type)
        Assertions.assertThat(robot.lookup(".button").queryButton()).hasText("clicked!")
    }
}
