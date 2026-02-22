package org.trivaris.tasks.controller

import java.sql.Connection
import java.sql.DriverManager

class ConnectionController(
    host: String,
    port: String,
    username: String,
    password: String,
): AutoCloseable {

    private val url: String = "jdbc:postgresql://${host}:${port}/tasks"
    private val connection: Connection = DriverManager.getConnection(url, username, password).apply { autoCommit = true }

    fun getConnection(): Connection = connection

    override fun close() {
        connection.close()
    }
}