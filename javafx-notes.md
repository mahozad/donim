If Intellij does not open an fxml file in Scene Builder tab of IDEA and shows error,
it might be because of URLs used in images or something similar.
Try to remove the URLs and then restart the IDEA to fix the problem.

---

To localize strings in the app, the main class should load the resource bundle like this:

    val resources: ResourceBundle = ResourceBundle.getBundle("messages")

Then whenever loading a new fxml file -in main class or in a controller class-
the variable should be passed around like this:

    FXMLLoader.load(javaClass.getResource("scene.fxml"), resources)

In the controller of the target fxml file declare a resource bundle field like this
and it will be injected automatically by the FXMLLoader:

    @FXML private lateinit var resources: ResourceBundle

Note that name of the field should be "resources".

see [this](https://stackoverflow.com/q/26325403),
[this](https://stackoverflow.com/q/20107463)
and [this](https://stackoverflow.com/q/44124202) for more info.

---

For making the layout right-to-left, set the orientation of the scene and parent node
like this:

    node.nodeOrientation = NodeOrientation.RIGHT_TO_LEFT
    
see [docs](https://wiki.openjdk.java.net/display/OpenJFX/Node+Orientation+in+JavaFX) for more info.