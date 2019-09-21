package com.pleon.donim.util

import com.pleon.donim.exception.SettingNotFoundException
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class PersistentSettings : Settings {

    private val properties = Properties()
    private val fileName = "app-settings.properties"

    init {
        try {
            properties.load(FileInputStream(fileName))
        } catch (e: FileNotFoundException) {
            Files.createFile(Path.of(fileName))
        } catch (e: Exception) {
            print(e)
        }
    }

    override fun set(name: String, value: String) = try {
        val outputStream = FileOutputStream(fileName)
        properties.setProperty(name, value)
        properties.store(outputStream, null)
        outputStream.close()
    } catch (e: Exception) {
        print(e)
    }

    override fun get(name: String): String {
        return properties.getProperty(name) ?: throw SettingNotFoundException()
    }
}
