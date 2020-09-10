package sample.basic

class Basic : Application() {

    fun start(primaryStage: Stage) {
        val root: Parent = FXMLLoader.load(javaClass.getResource("/fxml/scene.fxml"))
        primaryStage.scene = Scene(root)
        primaryStage.show()
    }
}
