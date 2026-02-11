package org.trivaris.tasks

import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.binding.ObjectBinding
import javafx.beans.binding.StringBinding
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import org.trivaris.tasks.model.User

class AuthManager(
    private val dbManager: DatabaseManager,
) {
    private var user: User? = null
    private var loginEmail: String? = null
    private var loginPassword: String? = null
    val userId: Int?
        get() = user?.id
    val isLoggedIn: Boolean
        get() = user != null

    private val loginResultProperty: ObjectProperty<LoginResult> = SimpleObjectProperty(this, "status", LoginResult.NO_INPUT)

    fun loginResultProperty(): ObjectBinding<LoginResult> {
        return Bindings.createObjectBinding( {
            loginResultProperty.get()
        }, loginResultProperty)
    }

    fun loginDescriptionProperty(): StringBinding {
        return Bindings.createStringBinding({
            loginResultProperty.get().description
        }, loginResultProperty)
    }

    fun login() {
        val email: String? = loginEmail
        val password: String? = loginPassword

        if (email == null || password == null) {
            loginResultProperty.set(LoginResult.NO_INPUT)
            return
        }

        Thread {
            val hashedPassword = password.hashCode().toString()
            val user: User? = dbManager.getUserByEmail(email)

            val result = if (user == null)
                LoginResult.NO_SUCH_USER
            else if (hashedPassword != user.password)
                LoginResult.PASSWORD_DOES_NOT_MATCH
            else
                LoginResult.OK

            Platform.runLater { loginResultProperty.set(result) }
            if (result.success) this.user = user
        }.start()
    }

    fun setPassword(password: String) { loginPassword = password }
    fun setEmail(email: String) { loginEmail = email }

    enum class LoginResult(val description: String, val success: Boolean) {
        OK("Login was successful", true),
        NO_SUCH_USER("Email or password is incorrect", false),
        PASSWORD_DOES_NOT_MATCH("Email or password is incorrect", false),
        NO_INPUT("Login...", false)
    }
}