package com.pleon.donim

import com.pleon.donim.util.buildTransparentScene
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.stage.Stage
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.assertions.api.Assertions.assertThat
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start

@ExtendWith(ApplicationExtension::class)
class SceneBuilderTest {

    lateinit var stage: Stage;
    lateinit var layout: Parent;

    @Start
    fun start(stage: Stage) {
        this.layout = FXMLLoader.load(javaClass.getResource("/fxml/scene-splash.fxml"))
        this.stage = stage
    }

    @Test
    fun `scene fill should be transparent`() {
        val scene = buildTransparentScene(layout)

        assertThat(scene.fill.isOpaque).isFalse()
    }
}
