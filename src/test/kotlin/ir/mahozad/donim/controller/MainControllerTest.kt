package ir.mahozad.donim.controller

import ir.mahozad.donim.util.toURL
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.assertions.api.Assertions
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start

@ExtendWith(ApplicationExtension::class)
class MainControllerTest{
    private lateinit var logo: Node

    /**
     * Will be called with `@BeforeEach` semantics, i.e. before each test method.
     *
     * @param stage will be injected by the test runner.
     */
    @Start
    private fun start(stage: Stage) {
        val layout: Parent = FXMLLoader.load("/fxml/scene-splash.fxml".toURL())
        logo = layout.lookup("#brand")
        stage.scene = Scene(layout)
        stage.show()
    }

    /**
     * @param robot will be injected by the test runner.
     */
    @Test
    fun `logo should be visible`(robot: FxRobot) {
        Assertions.assertThat(logo).isVisible()
    }

    @Test
    fun `logo should be centered in the scene`(robot: FxRobot) {
        Assertions.assertThat(robot.bounds(logo).query().centerX).isEqualTo(robot.bounds(logo.scene).query().centerX)
        Assertions.assertThat(robot.bounds(logo).query().centerY).isEqualTo(robot.bounds(logo.scene).query().centerY)
    }

    @Disabled @Test
    fun `get all application windows`(robot: FxRobot) {
        robot.robotContext().windowFinder.listWindows()
    }
}
