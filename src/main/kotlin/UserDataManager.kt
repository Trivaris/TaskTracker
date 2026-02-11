package org.trivaris.tasks

import org.trivaris.tasks.model.Task

class UserDataManager(
    private val dbManager: DatabaseManager,
    private val authManager: AuthManager
) {
    fun getTasks(): List<Task> {
        val userId = authManager.userId ?: return emptyList()
        return dbManager.getTasksOfUser(userId)
    }
}