package org.trivaris.tasks

import org.trivaris.tasks.model.Task
import org.trivaris.tasks.model.User

class UserDataManager(
    private val user: User,
    private val dbManager: DatabaseManager
) {
    fun getTasks(): List<Task> {
        return dbManager.getTasksOfUser(user.id)
    }
}