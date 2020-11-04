package com.pleon.donim.controller

import com.pleon.donim.util.buildTransparentScene
import com.pleon.donim.util.toURL
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.Parent
import javafx.stage.Stage
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.assertions.api.Assertions.assertThat
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start

@ExtendWith(ApplicationExtension::class)
class SplashControllerTest {

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
        stage.scene = buildTransparentScene(layout)
        stage.show()
    }

    @Test
    fun `scene should have transparent fill`() {
        assertThat(logo.scene.fill.isOpaque).isFalse()
    }

    /**
     * @param robot will be injected by the test runner.
     */
    @Test
    fun `logo should be visible`(robot: FxRobot) {
        assertThat(logo).isVisible()
    }

    @Test
    fun `logo should be centered in the scene`(robot: FxRobot) {
        assertThat(robot.bounds(logo).query().centerX).isEqualTo(robot.bounds(logo.scene).query().centerX)
        assertThat(robot.bounds(logo).query().centerY).isEqualTo(robot.bounds(logo.scene).query().centerY)
    }

    @Disabled @Test
    fun `get all application windows`(robot: FxRobot) {
        robot.robotContext().windowFinder.listWindows()
    }
}
