import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URL

plugins {
    kotlin("jvm") version "1.4.10"
    id("application")
    id("org.openjfx.javafxplugin") version "0.0.9"
    // Official kotlin tool to generate kdoc, javadoc, markdown and other types of documentation.
    // Note that dokka includes a javadoc task as well so there's no need to configure or use the
    // traditional javadoc task in gradle.
    id("org.jetbrains.dokka") version "1.4.0"
    // A gradle plugin to create fat (uber) jars. The "jar" task only creates a jar of the application
    // itself without embedding the dependency jars in the resulting file. This plugin, on the other hand,
    // creates an uber jar file with the task named "shadowJar".
    id ("com.github.johnrengelman.shadow") version "6.0.0"
}

group = "com.pleon"
version = "1.0.0-alpha"

repositories {
    jcenter()
    mavenCentral()
}

application {
    mainClassName = "com.pleon.donim.Main"

    // JVM arguments; To prevent errors from javafxsvg library
    applicationDefaultJvmArgs = listOf(
            "--add-exports=javafx.graphics/com.sun.javafx.iio=ALL-UNNAMED",
            "--add-exports=javafx.graphics/com.sun.javafx.iio.common=ALL-UNNAMED"
    )
}

// Configure some settings of dokka. This is totally optional and can be removed.
tasks.dokkaHtml.configure {
    dokkaSourceSets {
        configureEach {
            // List of files or directories containing sample code (referenced with @sample tags)
            samples.from("sample/basic.kt", "sample/advanced.kt")
            // Specifies the location of the project source code on the Web.
            // If provided, Dokka generates "source" links for each declaration.
            sourceLink {
                // Unix-based directory relative path to the root of the project (where you execute gradle respectively).
                localDirectory.set(file("src/$name/kotlin"))
                // URL showing where the source code can be accessed through the web browser
                remoteUrl.set(URL("https://github.com/mahozad/donim/blob/master/src/$name/kotlin"))
                // Suffix which is used to append the line number to the URL. Use #L for GitHub
                remoteLineSuffix.set("#L")
            }
        }
    }
}

javafx {
    version = "15"
    modules(
            "javafx.base", "javafx.controls", "javafx.fxml",
            "javafx.graphics", "javafx.media", "javafx.web", "javafx.swing"
    )
}

tasks.withType<Wrapper> {
    // Add a gradle wrapper script to your source folders (by running the wrapper task).
    // The wrapper script when invoked, downloads the defined gradle version, and executes it.
    // By distributing the wrapper with your project, anyone can work with it without needing to install Gradle beforehand
    gradleVersion = "6.6"
}

tasks.withType<Jar> {
    // Define main class in the manifest of output jar file when generating one
    manifest.attributes("Main-Class" to "com.pleon.donim.MainKt")
}

tasks.withType<KotlinCompile> {
    // Target version of the generated JVM bytecode (1.6, 1.8, 9, 10, 11 or 12), default is 1.6
    kotlinOptions.jvmTarget = "14"
}

dependencies {
    // implementation(kotlin("stdlib-jdk8")) // Automatically added as of Kotlin 1.4
    implementation("de.codecentric.centerdevice:javafxsvg:1.3.0")
    implementation("com.jhlabs:filters:2.0.235-1")
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.5.0")
    testImplementation("org.assertj:assertj-core:3.13.2")
    testImplementation("io.mockk:mockk:1.9.3")
}
