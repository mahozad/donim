import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.50"
    id("application")
    id("org.openjfx.javafxplugin") version "0.0.8"
}

group = "com.pleon"
version = "0.9.2"

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

javafx {
    version = "13"
    modules(
            "javafx.base", "javafx.controls", "javafx.fxml",
            "javafx.graphics", "javafx.media", "javafx.web", "javafx.swing"
    )
}

tasks.withType<Wrapper> {
    // Add a gradle wrapper script to your source folders (by running the wrapper task).
    // The wrapper script when invoked, downloads the defined gradle version, and executes it.
    // By distributing the wrapper with your project, anyone can work with it without needing to install Gradle beforehand
    gradleVersion = "5.5"
}

tasks.withType<Jar> {
    // Define main class in the manifest of output jar file when generating one
    manifest.attributes("Main-Class" to "com.pleon.donim.MainKt")
}

tasks.withType<KotlinCompile> {
    // Target version of the generated JVM bytecode (1.6, 1.8, 9, 10, 11 or 12), default is 1.6
    kotlinOptions.jvmTarget = "12"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("de.codecentric.centerdevice:javafxsvg:1.3.0")
    implementation("com.jhlabs:filters:2.0.235-1")
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.5.0")
    testImplementation("org.assertj:assertj-core:3.13.2")
    testImplementation("io.mockk:mockk:1.9.3")
}
