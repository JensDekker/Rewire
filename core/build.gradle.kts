
plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.kapt")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"
}

group = "com.example.rewire.core"
version = "1.0"


dependencies {
    // Add any dependencies needed for shared logic here
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
}

