package org.trivaris.tasks

import java.sql.Connection
import java.sql.DriverManager

class ConnectionManager {
    private val username = System.getenv("DB_USERNAME") ?: "postgres"
    private val password = System.getenv("DB_PASSWORD").ifEmpty { throw IllegalArgumentException("Missing DB password") }
    private val host = System.getenv("DB_HOST") ?: "localhost"
    private val port = System.getenv("DB_PORT") ?: "5432"
    private val url = "jdbc:postgresql://${host}:${port}/tasks"

    private var connection: Connection = DriverManager.getConnection(url, username, password)

    fun getConnection(): Connection = connection

    fun closeConnection() = connection.close()
}