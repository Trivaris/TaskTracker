package org.trivaris.tasks.controller

import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

class Keystore(
    privateKeyContent: String,
    publicKeyContent: String,
) {
    val privateKey: PrivateKey = loadPrivateKey(privateKeyContent)
    val publicKey: PublicKey = loadPublicKey(publicKeyContent);

    private fun loadPrivateKey(pemContent: String): PrivateKey {
        val privateKeyPEM = pemContent
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\s".toRegex(), "")

        val encoded = Base64.getDecoder().decode(privateKeyPEM)
        val keySpec = PKCS8EncodedKeySpec(encoded)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePrivate(keySpec)
    }

    private fun loadPublicKey(pemContent: String): PublicKey {
        val cleanPem = pemContent
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\\s".toRegex(), "")

        val encoded = Base64.getDecoder().decode(cleanPem)

        val keySpec = X509EncodedKeySpec(encoded)

        val keyFactory = KeyFactory.getInstance(privateKey.algorithm)
        return keyFactory.generatePublic(keySpec)
    }
}