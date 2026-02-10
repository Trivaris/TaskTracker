package org.trivaris.tasks.model

import org.trivaris.tasks.DatabaseManager
import java.sql.ResultSet
import java.sql.SQLException

data class Task(
    val id: Int,
    val completed: Boolean,
    val name: String,
    val content: String?,
    val user: User,
) {
    companion object {
        fun fromResultset(rs: ResultSet): Task? {
            try {
                val user = User(
                    id = rs.getInt("user_id"),
                    name = rs.getString("user_name"),
                    password = rs.getString("user_password"),
                    email = rs.getString("user_email")
                )

                return Task(
                    id = rs.getInt("id"),
                    completed = rs.getBoolean("completed"),
                    name = rs.getString("name"),
                    content = rs.getString("content"),
                    user = user
                )
            } catch (e: SQLException) {
                println("SQL Exception when trying to fetch Task: ${e.message}")
                return null
            }
        }
    }
}