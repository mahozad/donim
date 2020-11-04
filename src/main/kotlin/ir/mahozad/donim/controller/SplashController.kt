package ir.mahozad.donim.controller

import ir.mahozad.donim.util.FadeMode.IN
import ir.mahozad.donim.util.FadeMode.OUT
import ir.mahozad.donim.util.fade
import ir.mahozad.donim.util.rotate
import ir.mahozad.donim.util.toURL
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.Parent
import javafx.util.Duration

class SplashController : BaseController() {

    // TODO: Set splash screen to be always on top

    @FXML private lateinit var brand: Node
    private lateinit var nextRoot: Parent

    override fun initialize() {
        super.initialize()
        nextRoot = FXMLLoader.load("/fxml/scene-main.fxml".toURL())
        nextRoot.opacity = 0.0
        val delay = Duration.millis(500.0)
        val duration = Duration.millis(3000.0)
        rotate(brand, byAngle = 360.0, delay, duration)
        transitionToNextScene()
    }

    private fun transitionToNextScene() {
        val delay = Duration.millis(3000.0)
        val duration = Duration.millis(200.0)
        fade(root, OUT, duration, delay) {
            root.scene.root = nextRoot
            fade(nextRoot, IN, duration)
        }
    }
}
