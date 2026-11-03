package com.example.termux

import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPublicKey

data class SSHKeyPair(
    val privateKeyPem: String,
    val publicKeyAuthorizedFormat: String
)

object SSHKeyGenerator {

    fun generateKeyPair(comment: String = "shen@termutoxic"): SSHKeyPair {
        return try {
            val keyPairGen = KeyPairGenerator.getInstance("RSA")
            keyPairGen.initialize(2048)
            val keyPair = keyPairGen.generateKeyPair()

            val privateKey = keyPair.private
            val publicKey = keyPair.public as RSAPublicKey

            // Encode Private Key to PEM format
            val privateKeyBytes = privateKey.encoded
            val privateKeyB64 = Base64.encodeToString(privateKeyBytes, Base64.NO_WRAP)
            val privateKeyPem = "-----BEGIN PRIVATE KEY-----\n$privateKeyB64\n-----END PRIVATE KEY-----"

            // Encode Public Key to OpenSSH authorized_keys format (ssh-rsa AAAAB3N...)
            val byteStream = ByteArrayOutputStream()
            val dataStream = DataOutputStream(byteStream)

            val sshRsaBytes = "ssh-rsa".toByteArray(Charsets.UTF_8)
            dataStream.writeInt(sshRsaBytes.size)
            dataStream.write(sshRsaBytes)

            val eBytes = publicKey.publicExponent.toByteArray()
            dataStream.writeInt(eBytes.size)
            dataStream.write(eBytes)

            val nBytes = publicKey.modulus.toByteArray()
            dataStream.writeInt(nBytes.size)
            dataStream.write(nBytes)

            val publicRsaB64 = Base64.encodeToString(byteStream.toByteArray(), Base64.NO_WRAP)
            val publicKeyAuthorizedFormat = "ssh-rsa $publicRsaB64 $comment"

            SSHKeyPair(
                privateKeyPem = privateKeyPem,
                publicKeyAuthorizedFormat = publicKeyAuthorizedFormat
            )
        } catch (e: Exception) {
            e.printStackTrace()
            SSHKeyPair(
                privateKeyPem = "",
                publicKeyAuthorizedFormat = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQ0... shen@termutoxic"
            )
        }
    }
}
