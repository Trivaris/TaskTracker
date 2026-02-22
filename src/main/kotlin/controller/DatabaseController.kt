package org.trivaris.tasks.controller

import org.trivaris.tasks.model.Task
import org.trivaris.tasks.model.User
import java.sql.Statement

class DatabaseController(
    private val connectionController: ConnectionController
) {
    fun getUserById(userId: Int): User? {
        val sql = "SELECT * FROM users WHERE id=?"
        val conn = connectionController.getConnection()
        return conn.prepareStatement(sql).use { statement ->
            statement.setInt(1, userId)
            statement.executeQuery().use { rs ->
                rs.next()
                User.fromResultSet(rs)
            }
        }
    }

    fun  getUserByEmail(email: String): User? {
        val sql = "SELECT * FROM users WHERE email=?"
        val conn = connectionController.getConnection()
        return conn.prepareStatement(sql).use { statement ->
            statement.setString(1, email)
            statement.executeQuery().use { rs ->
                if (!rs.next()) return null
                User.fromResultSet(rs)
            }
        }
    }

    fun getTaskById(taskId: Int): Task? {
        val sql = """
            SELECT t.id as id, t.completed as completed, t.name as name, t.content as content, t.user_id as user_id,
                   u.name as user_name, u.password as user_password, u.email as user_email
            FROM tasks t
            JOIN users u ON t.user_id = u.id
            WHERE t.id = ?
        """.trimIndent()
        val conn = connectionController.getConnection()

        return conn.prepareStatement(sql).use { statement ->
            statement.setInt(1, taskId)
            statement.executeQuery().use { rs ->
                rs.next()
                Task.fromResultset(rs)
            }
        }
    }

    fun getTasksOfUser(userId: Int): List<Task> {
        val sql = """
            SELECT t.id as id, t.completed as completed, t.name as name, t.content as content, t.user_id as user_id,
                   u.name as user_name, u.password as user_password, u.email as user_email
            FROM tasks t
            JOIN users u ON t.user_id = u.id
            WHERE u.id = ?
        """.trimIndent()
        val conn = connectionController.getConnection()

        return conn.prepareStatement(sql)
            .apply { fetchSize = 100 }
            .use { statement ->
            statement.setInt(1, userId)
            statement.executeQuery().use { rs ->
                val tasks = mutableListOf<Task>()
                while (rs.next())
                    Task.fromResultset(rs)?.let { tasks.add(it) }
                tasks
            }
        }
    }

    fun insertTask(completed: Boolean, name: String, content: String, user: User): Int {
        val sql = "INSERT INTO tasks (completed, name, content, user_id) VALUES (?, ?, ?, ?)"
        val conn = connectionController.getConnection()

        return conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { statement ->
            statement.setBoolean(1, completed)
            statement.setString(2, name)
            statement.setString(3, content)
            statement.setInt(4, user.id)
            statement.executeUpdate()

            val rs = statement.generatedKeys
            if (rs.next()) rs.getInt(1) else -1
        }
    }

    fun insertUser(name: String, password: String, email: String): Int {
        val sql = "INSERT INTO users (name, password, email) VALUES (?, ?, ?)"
        val conn = connectionController.getConnection()

        return conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { statement ->
            statement.setString(1, name)
            statement.setString(2, password)
            statement.setString(3, email)
            statement.executeUpdate()

            val rs = statement.generatedKeys
            if (rs.next()) rs.getInt(1) else -1
        }
    }

    fun toggleTask(taskId: Int) {
        val sql = "UPDATE tasks SET completed = NOT completed WHERE id = ?"
        val conn = connectionController.getConnection()

        return conn.prepareStatement(sql).use { statement ->
            statement.setInt(1, taskId)
            statement.executeUpdate()
        }
    }

    fun removeTask(taskId: Int): Boolean {
        val sql = "DELETE FROM tasks WHERE id = ?"
        val conn = connectionController.getConnection()
        return conn.prepareStatement(sql).use { statement ->
            statement.setInt(1, taskId)
            // Affected Rows > 0
            statement.executeUpdate() > 0
        }
    }

    fun renameTask(taskId: Int, name: String) {
        val sql = "UPDATE tasks SET name = ? WHERE id = ?"
        val conn = connectionController.getConnection()
        return conn.prepareStatement(sql).use { statement ->
            statement.setInt(1, taskId)
            statement.setString(2, name)
            statement.executeUpdate()
        }
    }
}