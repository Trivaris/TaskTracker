package org.trivaris.tasks

import io.ktor.server.plugins.di.annotations.Property

fun provideConnectionManager(
    @Property("database.host") host: String,
    @Property("database.port") port: String,
    @Property("database.username") username: String,
    @Property("database.password") password: String,
): ConnectionManager = ConnectionManager(host, port, username, password)

fun provideDataBaseManager(connectionManager: ConnectionManager): DatabaseManager = DatabaseManager(connectionManager)
