package org.trivaris.tasks.controller

import org.trivaris.tasks.model.Task
import org.trivaris.tasks.model.User

class UserDataController(
    private val user: User,
    private val dbManager: DatabaseController
) {
    val tasks: MutableMap<Int, Task> = loadTasks()

    private fun loadTasks(): MutableMap<Int, Task> {
        return dbManager.getTasksOfUser(user.id).associateBy { task -> task.id }.toMutableMap()
    }

    fun addTask(completed: Boolean, name: String, content: String?): Task? {
        val id = dbManager.insertTask(completed, name, content ?: "", user)
        if (id == -1) return null
        val task = Task(id, completed, name, content, user)
        tasks[id] = task
        return task
    }

    fun toggleTask(taskId: Int): TaskCompletionType {
        if (!tasks.keys.contains(taskId)) return TaskCompletionType.BAD_REQUEST
        dbManager.toggleTask(taskId)
        return TaskCompletionType.OK
    }

    fun removeTask(taskId: Int): TaskCompletionType {
        if (!tasks.keys.contains(taskId)) return TaskCompletionType.BAD_REQUEST
        val success = dbManager.removeTask(taskId).apply { if (this) tasks.remove(taskId) }
        return if (success) TaskCompletionType.OK else TaskCompletionType.SERVER_ERROR
    }

    fun renameTask(taskId: Int, name: String): TaskCompletionType {
        if (!tasks.keys.contains(taskId)) return TaskCompletionType.BAD_REQUEST
        dbManager.renameTask(taskId, name)
        return TaskCompletionType.OK
    }

    enum class TaskCompletionType(val success: Boolean) {
        OK(true),
        BAD_REQUEST(false),
        SERVER_ERROR(false),
        UNAUTHORIZED(false)
    }
}