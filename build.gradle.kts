plugins {
    alias(libs.plugins.kotlin.jvm)
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

    //Ktor Server
    implementation(libs.ktor.server.di)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.ktor.server.htmx)
    implementation(libs.ktor.server.html.builder)

    //Other
    implementation(libs.postgres)
    implementation(libs.mordant)
    implementation(libs.logback)

    //Test
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
    testImplementation("io.ktor:ktor-server-test-host-jvm:3.4.0")
}

tasks.test {
    useJUnitPlatform()
}
