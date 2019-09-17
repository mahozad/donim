package com.pleon.donim.util

import com.pleon.donim.exception.SettingNotFoundException
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

object SettingsUtil {

    private val properties = Properties()
    private const val fileName = "app-settings.properties"

    init {
        try {
            properties.load(FileInputStream(fileName))
        } catch (e: FileNotFoundException) {
            Files.createFile(Path.of(fileName))
        } catch (e: Exception) {
            print(e)
        }
    }

    fun store(name: String, value: String) = try {
        val outputStream = FileOutputStream(fileName)
        properties.setProperty(name, value)
        properties.store(outputStream, null)
        outputStream.close()
    } catch (e: Exception) {
        print(e)
    }

    fun load(name: String): String {
        return properties.getProperty(name) ?: throw SettingNotFoundException()
    }
}
