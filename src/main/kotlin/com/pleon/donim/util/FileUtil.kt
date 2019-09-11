package com.pleon.donim.util

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

object FileUtil {

    fun readFile(inputStream: InputStream): ByteArray {
        val buffer = ByteArrayOutputStream()
        val data = ByteArray(16384)
        try {
            var bytes = inputStream.read(data, 0, data.size)
            while (bytes != -1) {
                buffer.write(data, 0, bytes)
                bytes = inputStream.read(data, 0, data.size)
            }
            buffer.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return buffer.toByteArray()
    }
}
