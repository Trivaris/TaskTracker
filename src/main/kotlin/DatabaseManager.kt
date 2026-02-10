package org.trivaris.tasks

import org.trivaris.tasks.model.Task
import org.trivaris.tasks.model.User

class DatabaseManager(
    val connectionManager: ConnectionManager
) {
    fun getUserById(userId: Int): User? {
        val sql = "SELECT * FROM users WHERE id=?"
        val conn = connectionManager.getConnection()
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
        val conn = connectionManager.getConnection()
        return conn.prepareStatement(sql).use { statement ->
            statement.setString(1, email)
            statement.executeQuery().use { rs ->
                rs.next()
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
        val conn = connectionManager.getConnection()

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
        val conn = connectionManager.getConnection()
        conn.autoCommit = false

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

    fun insertTask(completed: Boolean, name: String, content: String, user: User) {
        val sql = "INSERT INTO tasks (completed, name, content, user_id) VALUES (?, ?, ?, ?)"
        val conn = connectionManager.getConnection()

        return conn.prepareStatement(sql).use { statement ->
            statement.setBoolean(1, completed)
            statement.setString(2, name)
            statement.setString(3, content)
            statement.setInt(4, user.id)
            statement.executeUpdate()
        }
    }

    fun insertUser(name: String, password: String, email: String) {
        val sql = "INSERT INTO users (name, password, email) VALUES (?, ?, ?)"
        val conn = connectionManager.getConnection()

        return conn.prepareStatement(sql).use { statement ->
            statement.setString(1, name)
            statement.setString(2, password)
            statement.setString(3, email)
            statement.executeUpdate()
        }
    }
}