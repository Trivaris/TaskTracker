package org.trivaris.tasks.model

data class JWTData(
    val algorithm: String,
    val typ: String,
    val userId: Int
)

data class JWToken(
    val data: JWTData,
    val signature: String,
)