plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktor)
    application
}

group = "org.trivaris.tasks"
version = "1.0-SNAPSHOT"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    //Ktor
    implementation(libs.ktor.htmx.html)
    implementation(libs.ktor.htmx)
    implementation(libs.ktor.serialization.json)

    //Ktor Server
    implementation(libs.ktor.server.di)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.ktor.server.htmx)
    implementation(libs.ktor.server.html.builder)
    implementation(libs.ktor.server.negotiation)

    //Other
    implementation(libs.postgres)
    implementation(libs.mordant)
    implementation(libs.logback)
    implementation(libs.argon2kt)

    //Test
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.platform.launcher)
}

tasks.test {
    useJUnitPlatform()
}
