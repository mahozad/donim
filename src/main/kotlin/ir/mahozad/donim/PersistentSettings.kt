package ir.mahozad.donim

import ir.mahozad.donim.exception.SettingNotFoundException
import javafx.collections.FXCollections
import javafx.collections.ObservableMap
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

object PersistentSettings : Settings {

    private const val FILE_NAME = "preferences.ini"
    private val fileDirectory = getOsSpecificDirectory()
    private val properties = Properties()
    private val observableProperties = FXCollections.observableMap(properties)

    init {
        try {
            properties.load(FileInputStream("$fileDirectory$FILE_NAME"))
        } catch (e: FileNotFoundException) {
            Files.createDirectories(Path.of(fileDirectory))
            Files.createFile(Path.of(fileDirectory, FILE_NAME))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * See https://stackoverflow.com/q/1198911/
     */
    private fun getOsSpecificDirectory(): String {
        val osName = System.getProperty("os.name")
        return if (osName.contains("windows", ignoreCase = true)) {
            System.getenv("AppData") + "/Donim/"
        } else {
            System.getProperty("user.home") + "/Donim/"
        }
    }

    override fun set(name: String, value: String) = try {
        observableProperties[name] = value
        properties[name] = value
        val outputStream = FileOutputStream("$fileDirectory$FILE_NAME")
        properties.store(outputStream, null)
        outputStream.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    override fun get(name: String): String {
        return properties.getProperty(name) ?: throw SettingNotFoundException()
    }

    override fun getObservableProperties(): ObservableMap<String, String> {
        return observableProperties as ObservableMap<String, String>
    }
}
