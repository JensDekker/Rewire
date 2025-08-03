plugins {
    application
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}

application {
    mainClass.set("com.example.rewire.cli.MainKt")
}

dependencies {
    implementation(project(":core"))
    // ...other dependencies...
}
