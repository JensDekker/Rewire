
plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.kapt")
}

group = "com.example.rewire.core"
version = "1.0"


dependencies {
    // Add any dependencies needed for shared logic here
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
}

