If Intellij does not open an fxml file in Scene Builder tab of IDEA and shows error,
it might be because of URLs used in images or something similar.
Try to remove the URLs and then restart the IDEA to fix the problem.

---

[Cross-platform tray icon support](https://github.com/dorkbox/SystemTray)

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

See [this](https://stackoverflow.com/q/26325403),
[this](https://stackoverflow.com/q/20107463)
and [this](https://stackoverflow.com/q/44124202) for more info.

---

For making the layout right-to-left, set the orientation of the scene and parent node
like this:

    node.nodeOrientation = NodeOrientation.RIGHT_TO_LEFT
    
See [docs](https://wiki.openjdk.java.net/display/OpenJFX/Node+Orientation+in+JavaFX) for more info.

---

See [this post](https://stackoverflow.com/a/49833163) for how to add custom nodes (and enable working with scenes that
contain them) in scene builder

---

See javafx.beans.binding.Bindings class for creating useful bindings with its static methods.

---

To get the controller of a scene do like this:

    val fxmlLoader = FXMLLoader("/fxml/scene-settings.fxml".toURL())
    val settingsController: SettingsController = fxmlLoader.getController()
    val root: Parent = fxmlLoader.load()

---

For how javafx timeline works see [this](https://stackoverflow.com/a/36366805/8583692)

---

// To use spring for dependency injection see [this page](http://www.greggbolinger.com/let-spring-be-your-javafx-controller-factory/)

---

Add shortcut of the app .exe file to `C:\Users\X\AppData\Roaming\Microsoft\Windows\Start Menu\Programs\Startup`
for the app to automatically start on Windows login

---

To make the app follow windows theme refer to these:
 https://stackoverflow.com/q/62289/ and https://stackoverflow.com/q/60837862
 and https://jregistry.sourceforge.io/ and https://stackoverflow.com/q/60321706
 and https://stackoverflow.com/q/38734615/

---

The jRegistry library which is used to acess windows registry values (system theme etc.)
requires two dll files (provided along with the library jar file) to work: reg.dll and reg_x64.dll  
These two files are placed in the `/resources/` directory to be accessed by classpath loader
and also to be available in the Application jar file (because everything in resources is copied into
the jar file; see the exe guide for more info).
At application startup we check to see if these two files are available in the current directory,
and if not, copy them from the classpath (or jar) to the current directory.  
See [here](https://stackoverflow.com/q/1611357) and [here](https://stackoverflow.com/q/2546665)
and [here](https://stackoverflow.com/q/4691095) and [here](https://stackoverflow.com/q/4764347)
and [here](https://stackoverflow.com/q/9006127) and [here](https://stackoverflow.com/a/24738004)
and [here](https://stackoverflow.com/q/2937406) and [here](https://github.com/adamheinrich/native-utils)
and [here](http://www.jdotsoft.com/JarClassLoader.php)
