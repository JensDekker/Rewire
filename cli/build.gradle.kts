plugins {
    application
    kotlin("jvm")
}

application {
    mainClass.set("com.example.rewire.cli.MainKt") // Use the fully qualified name if needed
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Main-Class"] = "com.example.rewire.cli.MainKt"
    }
    // Optional: include dependencies in the jar (fat jar)
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}

dependencies {
    implementation(project(":core"))
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}
