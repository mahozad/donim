package ir.mahozad.donim.util

import ir.mahozad.donim.Main
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path

fun extractResource(resourceName: String, destination: String = ".") {
    val input = Main::class.java.getResourceAsStream("/$resourceName")
    val file = Files.createFile(Path.of("$destination/$resourceName"))
    val output = FileOutputStream(file.toFile())
    Files.setAttribute(file, "dos:hidden", true)

    val buffer = ByteArray(1024)
    var data = input.read(buffer)
    while (data != -1) {
        output.write(buffer, 0, data)
        data = input.read(buffer)
    }
    output.close()
    input.close()
}
