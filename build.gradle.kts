plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")
    testImplementation("org.jetbrains.kotlin", "kotlin-test-junit5", "1.3.72")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test-jvm:1.6.0")

}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}