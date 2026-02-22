package org.trivaris.tasks.controller

import de.mkammerer.argon2.Argon2
import de.mkammerer.argon2.Argon2Factory
import io.ktor.util.toCharArray
import org.trivaris.tasks.controller.Keystore
import org.trivaris.tasks.model.jwt.JWTData
import org.trivaris.tasks.model.jwt.JWToken
import org.trivaris.tasks.model.User
import org.trivaris.tasks.model.jwt.JWTHeader
import kotlin.text.isEmpty

class AuthController(
    private val keyStore: Keystore,
    private val dbManager: DatabaseController,
    val argon: Argon2 = Argon2Factory.create()
) {
    private lateinit var _user: User
    private var loginEmail: String = ""
    private var loginPassword: String = ""
    private var loginName: String = ""

    private var _loginResult: LoginResult = LoginResult.NO_INPUT
    val loginResult: LoginResult
        get() = _loginResult

    val user: User
        get() = _user

    fun login() {
        val email = loginEmail
        val password = loginPassword.toCharArray()
        if (email.isEmpty() || password.isEmpty()) {
            _loginResult = LoginResult.NO_INPUT
            return
        }

        val dbUser: User? = dbManager.getUserByEmail(email)
        val success = argon.verify(dbUser?.password ?: "", password)

        val result = when {
            dbUser == null -> LoginResult.NO_SUCH_USER
            !success -> LoginResult.PASSWORD_DOES_NOT_MATCH
            else -> LoginResult.OK
        }

        dbUser?.let { if (result.success) _user = it }
        _loginResult = result
        argon.wipeArray(password)
    }

    fun register() {
        val email = loginEmail
        val username = loginName
        val password = loginPassword.toCharArray()

        println("Registering with $email, $username, $password")

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            _loginResult = LoginResult.NO_INPUT
            return
        }

        // iterations, memory (KB), parallelism, password
        val hashedPassword = argon.hash(10, 65536, 1, password)

        val userId = dbManager.insertUser(username, hashedPassword, email)
        if (userId == -1) {
            _loginResult = LoginResult.NO_SUCH_USER
            return
        }
        _user =  User(
            userId,
            username,
            hashedPassword,
            email,
        )
        _loginResult = LoginResult.OK
        argon.wipeArray(password)
    }

    fun loginWithJWT(token: JWToken) {
        if (!token.isValidSignature(keyStore.publicKey)) {
            _loginResult = LoginResult.INVALID_SIGNATURE
            return
        }
        val userId = token.data.userId
        val dbUser = dbManager.getUserById(userId)
        if (dbUser == null) {
            _loginResult = LoginResult.NO_INPUT
            return
        }

        _user = dbUser
        _loginResult = LoginResult.OK
    }

    fun getJWT(): JWToken? {
        if (!loginResult.success) return null
        val userId = _user?.id ?: return null
        val header = JWTHeader.getDefault()
        val data = JWTData(userId)
        return JWToken.sign(keyStore.privateKey, header, data)
    }

    fun setPassword(password: String) { loginPassword = password }
    fun setEmail(email: String) { loginEmail = email }
    fun setUsername(username: String) { loginName = username }

    enum class LoginResult(val description: String, val success: Boolean) {
        OK("Login was successful", true),
        NO_SUCH_USER("Email or password is incorrect", false),
        PASSWORD_DOES_NOT_MATCH("Email or password is incorrect", false),
        NO_INPUT("Login...", false),
        INVALID_SIGNATURE("Invalid signature", false),
    }

}