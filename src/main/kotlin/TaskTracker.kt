package org.trivaris.tasks

import org.trivaris.tasks.model.Task
import java.util.Scanner
import kotlin.system.exitProcess

fun main() {
    val connectionManager = ConnectionManager()
    val databaseManager = DatabaseManager(connectionManager)
    val authManager = AuthManager(databaseManager)
    val display = DisplayManager()

    val scanner = Scanner(System.`in`)

    println("Enter email address: ")
    val email = scanner.next()

    println("Enter password: ")
    val password = scanner.next()

    val loginResult = authManager.login(email, password)
    if (loginResult != AuthManager.LoginResult.OK) {
        println("Login failed")
        exitProcess(0)
    }

    val userId = authManager.getId() ?: throw IllegalStateException("User ID is not set")
    val tasks: List<Task> = databaseManager.getTasksOfUser(userId)

    display.displayTasks(tasks)

    connectionManager.closeConnection()
}
