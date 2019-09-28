import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.50"
    id("application")
    id("org.openjfx.javafxplugin") version "0.0.8"
}

group = "com.pleon"
version = "0.1"

sourceSets {
    getByName("main").java.srcDirs("src/main/kotlin")
    getByName("test").java.srcDirs("src/test/kotlin")
}

repositories {
    jcenter()
    mavenCentral()
}

javafx {
    version = "13"
    modules(
            "javafx.base", "javafx.controls", "javafx.fxml",
            "javafx.graphics", "javafx.media", "javafx.web",
            "javafx.swing"
    )
}

application {
    mainClassName = "com.pleon.donim.Main"
}

/**
 * This block is to tell gradle to use its native junit platform. We can also specify some useful
 * things such as excluded tags, etc. here. Currently seems to be not needed.
 */
/*tasks.named<Test>("test") {
    useJUnitPlatform()
}*/

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("de.codecentric.centerdevice:javafxsvg:1.3.0")
    implementation("net.java.dev.jna:jna:5.4.0")
    implementation("net.java.dev.jna:jna-platform:5.4.0")
    implementation("com.jhlabs:filters:2.0.235-1")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.3.40")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.5.0")
    testImplementation("org.assertj:assertj-core:3.12.2")
    testImplementation("io.mockk:mockk:1.9.3")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "12"
}
//configure<JavaPluginConvention> {
//    sourceCompatibility = JavaVersion.VERSION_12
//}
