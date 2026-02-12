package org.trivaris.tasks

import org.trivaris.tasks.model.JWTData
import org.trivaris.tasks.model.JWToken
import org.trivaris.tasks.model.User
import java.nio.ByteBuffer
import java.security.PrivateKey
import java.security.Signature
import kotlin.io.encoding.Base64

class AuthManager(
    private val dbManager: DatabaseManager,
) {
    private var user: User? = null
    private var loginEmail: String? = null
    private var loginPassword: String? = null

    private var _loginResult: LoginResult = LoginResult.NO_INPUT
    val loginResult: LoginResult
        get() = _loginResult

    fun login() {
        val email = loginEmail ?: return
        val password = loginPassword ?: return

        val hashedPassword = password.hashCode().toString()
        val dbUser: User? = dbManager.getUserByEmail(email)

        val result = when {
            dbUser == null -> LoginResult.NO_SUCH_USER
            hashedPassword != dbUser.password -> LoginResult.PASSWORD_DOES_NOT_MATCH
            else -> LoginResult.OK
        }

        if (result.success) user = dbUser
        _loginResult = result
    }

    fun getJWT(privateKey: PrivateKey): JWToken? {
        if (!loginResult.success) return null
        val userId = user?.id ?: return null
        val algorithm = "HS256"
        val data = JWTData(algorithm, "JWT", userId)
        val signer = Signature.getInstance(algorithm)

        val bytes = ByteBuffer.allocate(4).putInt(data.hashCode()).array()

        signer.initSign(privateKey)
        signer.update(bytes)

        val signature: String = Base64.encode(signer.sign())

        return JWToken(data, signature)
    }

    fun setPassword(password: String?) { loginPassword = password }
    fun setEmail(email: String?) { loginEmail = email }

    enum class LoginResult(val description: String, val success: Boolean) {
        OK("Login was successful", true),
        NO_SUCH_USER("Email or password is incorrect", false),
        PASSWORD_DOES_NOT_MATCH("Email or password is incorrect", false),
        NO_INPUT("Login...", false)
    }
}