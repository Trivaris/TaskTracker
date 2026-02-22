package org.trivaris.tasks

import io.ktor.server.plugins.di.annotations.Property
import org.trivaris.tasks.controller.ConnectionController
import org.trivaris.tasks.controller.DatabaseController
import org.trivaris.tasks.controller.Keystore
import java.io.File

fun provideConnectionManager(
    @Property("database.host") host: String,
    @Property("database.port") port: String,
    @Property("database.username") username: String,
    @Property("database.password") password: String,
): ConnectionController = ConnectionController(host, port, username, password)

fun provideDataBaseManager(connectionController: ConnectionController): DatabaseController = DatabaseController(connectionController)


fun provideKeystore(
    @Property("keystore.private_key") privateKeyPath: String,
    @Property("keystore.public_key") publicKeyPath: String
): Keystore {
    val privateKeyContent = File(privateKeyPath).readText()
    val publicKeyContent = File(publicKeyPath).readText()

    return Keystore(privateKeyContent, publicKeyContent)
}