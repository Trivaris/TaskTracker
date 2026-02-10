package org.trivaris.tasks.model

import java.sql.ResultSet
import java.sql.SQLException

data class User(
    val id: Int,
    val name: String,
    val password: String,
    val email: String,
) {
    companion object {
        fun fromResultSet(rs: ResultSet): User? {
            try {
                return User(
                    id = rs.getInt("id"),
                    name = rs.getString("name"),
                    password = rs.getString("password"),
                    email = rs.getString("email")
                )
            } catch (e: SQLException) {
                println("SQL Error: ${e.message}")
                return null
            }
        }
    }
}
