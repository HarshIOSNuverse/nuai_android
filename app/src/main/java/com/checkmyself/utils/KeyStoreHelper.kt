package com.checkmyself.utils

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object KeyStoreHelper {
    private const val secretKey: String = "Sp!hNx@app"
    private const val salt: String = "CHeckMySElf"

    fun encrypt(strToEncrypt: String?): String? {
        if (!strToEncrypt.isNullOrEmpty()) {
            try {
                val iv = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
                val ivSpec = IvParameterSpec(iv)
                val factory =
                    SecretKeyFactory.getInstance(/*"PBKDF2WithHmacSHA1"*/"PBKDF2WithHmacSHA256")
                val spec = PBEKeySpec(secretKey.toCharArray(), salt.toByteArray(), 65536, 256)
                val tmp = factory.generateSecret(spec)
                val secretKey = SecretKeySpec(tmp.encoded, "AES")
                val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
                return Base64.encodeToString(
                    cipher.doFinal(strToEncrypt.toByteArray()),
                    Base64.DEFAULT
                )

            } catch (e: Exception) {
                println("Error while encrypting: $e")
            }
        }
        return null
    }

    fun decrypt(strToDecrypt: String?): String? {
        if (strToDecrypt != null) {
            try {
                val iv = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
                val ivSpec = IvParameterSpec(iv)
                val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                val spec = PBEKeySpec(secretKey.toCharArray(), salt.toByteArray(), 65536, 256)
                val tmp = factory.generateSecret(spec)
                val secretKey = SecretKeySpec(tmp.encoded, "AES")

                val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
                cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
                return String(cipher.doFinal(Base64.decode(strToDecrypt, Base64.DEFAULT)))
            } catch (e: Exception) {
                println("Error while decrypting: $e")
            }
        }
        return null
    }
}