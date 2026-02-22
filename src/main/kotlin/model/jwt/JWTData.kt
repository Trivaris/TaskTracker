package org.trivaris.tasks.model.jwt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JWTData(
    @SerialName("user_id")
    val userId: Int,
)
