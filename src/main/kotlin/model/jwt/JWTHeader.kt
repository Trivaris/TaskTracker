package org.trivaris.tasks.model.jwt

import kotlinx.serialization.Serializable

@Serializable
data class JWTHeader(
    val algorithm: String,
    val typ: String,
) {
    val javaAlgorithm: String
        get() = if (algorithm == "RS256") "SHA256withRSA" else algorithm

    companion object {
        fun getDefault(): JWTHeader {
            val algorithm = "RS256"
            val typ = "JWT"
            return JWTHeader(algorithm, typ)
        }
    }
}
