package org.trivaris.tasks

import java.sql.Connection
import java.sql.DriverManager

class ConnectionManager(
    host: String,
    port: String,
    username: String,
    password: String,
): AutoCloseable {

    private val url: String = "jdbc:postgresql://${host}:${port}/tasks"
    private val connection: Connection = DriverManager.getConnection(url, username, password)

    fun getConnection(): Connection = connection

    override fun close() {
        connection.close()
    }
}