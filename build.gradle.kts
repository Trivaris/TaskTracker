import java.util.Properties

plugins {
    kotlin("jvm") version "2.3.0"
    application
}

group = "org.trivaris.tasks"
version = "1.0-SNAPSHOT"

val dotenv = Properties()
val envFile = project.file(".env")
if (envFile.exists())
    envFile.inputStream().use { dotenv.load(it) }


repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.postgresql:postgresql:42.7.7")
    implementation("com.github.ajalt.mordant:mordant:2.4.0")
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass = "org.trivaris.tasks.TaskTrackerKt"
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaExec> {
    dotenv.forEach { (key, value) ->
        environment(key.toString(), value.toString())
    }
}