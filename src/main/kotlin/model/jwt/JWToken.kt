package org.trivaris.tasks.model.jwt

import io.ktor.http.ContentType
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.TextContent
import io.ktor.http.withCharsetIfNeeded
import io.ktor.serialization.ContentConverter
import io.ktor.util.reflect.TypeInfo
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.charsets.Charset
import io.ktor.utils.io.core.readText
import io.ktor.utils.io.core.toByteArray
import io.ktor.utils.io.readRemaining
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.util.Base64

@Serializable
data class JWToken(
    val header: JWTHeader,
    val data: JWTData,
    val signature: ByteArray,
    @Transient
    val originalSignatureInput: String? = null
) {
    companion object {
        private val json = Json { encodeDefaults = true }

        fun sign(privateKey: PrivateKey, header: JWTHeader, data: JWTData): JWToken {
            val payload = getSigningInput(header, data)
            val signer = Signature.getInstance(header.javaAlgorithm)

            signer.initSign(privateKey)
            signer.update(payload.toByteArray())

            return JWToken(header, data, signer.sign(), payload)
        }

        fun getSigningInput(header: JWTHeader, data: JWTData): String {
            val jsonHeader = json.encodeToString(header)
            val jsonData = json.encodeToString(data)
            val encodedHeader = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonHeader.toByteArray())
            val encodedData = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonData.toByteArray())
            return "$encodedHeader.$encodedData"
        }

        fun ofTokenString(tokenString: String) : JWToken? {
            val parts = tokenString.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (parts.size != 3) return null
            return try {
                val headerJson = decodeBase64Url(parts[0])
                val dataJson = decodeBase64Url(parts[1])
                val signature = decodeBase64Url(parts[2])

                val header = json.decodeFromString<JWTHeader>(String(headerJson))
                val data = json.decodeFromString<JWTData>(String(dataJson))

                JWToken(header, data, signature, "${parts[0]}.${parts[1]}")
            } catch (_: Exception) { null }
        }

        private fun decodeBase64Url(input: String): ByteArray {
            var padded = input
            while (padded.length % 4 != 0)
                padded += "="
            return Base64.getUrlDecoder().decode(padded)
        }

    }

    fun isValidSignature(publicKey: PublicKey): Boolean {
        val payload = originalSignatureInput ?: return false
        return try {
            val verifier = Signature.getInstance(header.javaAlgorithm)
            verifier.initVerify(publicKey)
            verifier.update(payload.toByteArray())

            verifier.verify(signature)
        } catch (_: Exception) {
            false
        }
    }

    fun toTokenString(): String {
        val payload = originalSignatureInput ?: getSigningInput(header, data)
        val signatureBase64 = Base64.getUrlEncoder().withoutPadding().encodeToString(signature)

        return "$payload.$signatureBase64"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as JWToken
        if (header != other.header) return false
        if (data != other.data) return false
        return signature.contentEquals(other.signature)
    }

    override fun hashCode(): Int {
        var result = header.hashCode()
        result = 31 * result + data.hashCode()
        result = 31 * result + signature.contentHashCode()
        return result
    }

}


class JWTConverter(private val json: Json = Json.Default) : ContentConverter {

    override suspend fun serialize(
        contentType: ContentType,
        charset: Charset,
        typeInfo: TypeInfo,
        value: Any?
    ): OutgoingContent? {
        if (value !is JWToken) return null

        return TextContent(value.toTokenString(), contentType.withCharsetIfNeeded(charset))
    }

    override suspend fun deserialize(
        charset: Charset,
        typeInfo: TypeInfo,
        content: ByteReadChannel
    ): Any? {
        if (typeInfo.type != JWToken::class) return null

        val base64 = content.readRemaining().readText(charset)
        return JWToken.ofTokenString(base64)
    }


}