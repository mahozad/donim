package com.pleon.donim.util

import com.pleon.donim.exception.SettingNotFoundException
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

object PersistentSettings : Settings {

    private val properties = Properties()
    private const val FILE_NAME = "app-settings.properties"

    init {
        try {
            properties.load(FileInputStream(FILE_NAME))
        } catch (e: FileNotFoundException) {
            Files.createFile(Path.of(FILE_NAME))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun set(name: String, value: String) = try {
        val outputStream = FileOutputStream(FILE_NAME)
        properties.setProperty(name, value)
        properties.store(outputStream, null)
        outputStream.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    override fun get(name: String): String {
        return properties.getProperty(name) ?: throw SettingNotFoundException()
    }
}
