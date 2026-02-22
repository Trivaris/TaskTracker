package org.trivaris.tasks

import org.trivaris.tasks.model.jwt.JWTData
import org.trivaris.tasks.model.jwt.JWTHeader
import org.trivaris.tasks.model.jwt.JWToken
import java.security.KeyPairGenerator
import java.security.Signature
import java.util.Base64
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JWTTest {

    @Test
    fun `test header serialization and encoding`() {
        val header = JWTHeader.getDefault()

        // 1. Check JSON format
        assertEquals("""{"alg":"RS256","typ":"JWT"}""", header.toJSON())

        // 2. Check Base64Url (Standard result for this JSON)
        assertEquals("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9", header.base64())
    }

    @Test
    fun `test data serialization`() {
        val data = JWTData(userId = "user_123")

        // 1. Check JSON
        assertEquals("""{"user_id":"user_123"}""", data.toJSON())

        // 2. Check Base64Url
        assertEquals("eyJ1c2VyX2lkIjoidXNlcl8xMjMifQ", data.base64())
    }

    @Test
    fun `test full token generation and signature verification`() {
        // 1. Generate a temporary RSA KeyPair
        val kpg = KeyPairGenerator.getInstance("RSA")
        kpg.initialize(2048)
        val keyPair = kpg.generateKeyPair()

        val header = JWTHeader.getDefault()
        val data = JWTData(userId = "test_user")

        // 2. Sign the token using your implementation
        // NOTE: Ensure your JWToken.sign method uses 'javaAlg' for Signature.getInstance!
        val token = JWToken.sign(keyPair.private, header, data)
        val jwtString = token.toJWT()

        // 3. Verify structure (Header.Payload.Signature)
        val parts = jwtString.split(".")
        assertEquals(3, parts.size, "JWT should have 3 parts separated by dots")

        // 4. Verify the Signature manually
        // This confirms that your generated signature is mathematically valid
        val verifier = Signature.getInstance("SHA256withRSA")
        verifier.initVerify(keyPair.public)

        // The signature validates the "Header.Payload" string
        val contentToVerify = "${parts[0]}.${parts[1]}"
        verifier.update(contentToVerify.toByteArray())

        // Decode the signature from the token
        val sigBytes = Base64.getUrlDecoder().decode(parts[2])

        assertTrue(verifier.verify(sigBytes), "The signature must be valid for the public key")
    }
}