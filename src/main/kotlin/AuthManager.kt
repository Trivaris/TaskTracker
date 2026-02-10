package org.trivaris.tasks

import org.trivaris.tasks.model.User

class AuthManager(
    private val dbManager: DatabaseManager,
) {
    private var user: User? = null

    fun login(email: String, password: String): LoginResult {
        val user: User = dbManager.getUserByEmail(email) ?: return LoginResult.NO_SUCH_USER
        if (password != user.password) return LoginResult.PASSWORD_DOES_NOT_MATCH
        this.user = user
        return LoginResult.OK
    }

    fun getId(): Int? = user?.id

    enum class LoginResult {
        OK,
        NO_SUCH_USER,
        PASSWORD_DOES_NOT_MATCH,
    }
}