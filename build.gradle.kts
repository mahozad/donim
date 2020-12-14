import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URL

plugins {
    kotlin("jvm") version "1.4.20"
    id("application")
    id("org.openjfx.javafxplugin") version "0.0.9"
    // Official kotlin tool to generate kdoc, javadoc, markdown and other types of documentation.
    // Note that dokka includes a javadoc task as well so there's no need to configure or use the
    // traditional javadoc task in gradle.
    id("org.jetbrains.dokka") version "1.4.20"
    // A gradle plugin to create fat (uber) jars. The "jar" task only creates a jar of the application
    // itself without embedding the dependency jars in the resulting file. This plugin, on the other hand,
    // creates an uber jar file with the task named "shadowJar".
    id("com.github.johnrengelman.shadow") version "6.0.0"
}

group = "ir.mahozad"
version = "1.0.0-beta"

// If your build script needs to use external libraries, you can add them to the script’s classpath in the build script
// itself. You do this using the buildscript() method, passing in a block which declares the build script classpath.
buildscript {
    repositories {
        jcenter()
        google()
        mavenCentral()
    }
    dependencies {
        // For using proguard gradle task
        "classpath"("com.guardsquare:proguard-gradle:7.0.1")
    }
}

task("myProguardTask", proguard.gradle.ProGuardTask::class) {

}

repositories {
    jcenter()
    mavenCentral()
}

application {
    // Either Main or MainKt classes are acceptable
    mainClassName = "ir.mahozad.donim.Main"
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
    // The javafx modules that contain features (classes, functions etc) that we are using in our application
    modules(
            "javafx.base",
            "javafx.controls",
            "javafx.graphics",
            "javafx.media",
            "javafx.fxml"
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
    manifest.attributes("Main-Class" to "ir.mahozad.donim.MainKt")
}

tasks.withType<KotlinCompile> {
    // Target version of the generated JVM bytecode (1.6, 1.8, 9, 10, 11 or 12), default is 1.6
    kotlinOptions.jvmTarget = "11"
}

dependencies {
    implementation(files("lib/jregistry-1.8.3.jar"))
    implementation("com.1stleg:jnativehook:2.1.0")
    // implementation(kotlin("stdlib-jdk8")) // Automatically added as of Kotlin 1.4
    // See https://kotlinlang.org/api/latest/kotlin.test/ for info about the following 2 dependencies
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    // Aggregator artifact that transitively pulls in dependencies on junit-jupiter-api,
    // junit-jupiter-params, and junit-jupiter-engine for simplified dependency management
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
    testImplementation("org.testfx:testfx-core:4.0.16-alpha")
    testImplementation("org.testfx:testfx-junit5:4.0.16-alpha")
    testImplementation("org.assertj:assertj-core:3.17.2")
    testImplementation("io.mockk:mockk:1.10.0")
}
