package com.checkmyself.utils

import android.util.Base64
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object KeyStoreHelper {
    private const val secretKey: String = "b14ca5898a4e4133bbce2ea2315a1916"

    fun encrypt(strToEncrypt: String?): String? {
        if (!strToEncrypt.isNullOrEmpty()) {
            try {
                val iv = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
                val paramSpec: AlgorithmParameterSpec = IvParameterSpec(iv)
                val secretKey = SecretKeySpec(secretKey.toByteArray(), "AES")
                val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec)
                return Base64.encodeToString(
                    cipher.doFinal(strToEncrypt.toByteArray()),
                    Base64.DEFAULT
                ).replace("\n", "")

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
                val secretKey = SecretKeySpec(secretKey.toByteArray(), "AES")
                val paramSpec: AlgorithmParameterSpec = IvParameterSpec(iv)
                val cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING")
                cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec)
                return String(cipher.doFinal(Base64.decode(strToDecrypt, Base64.DEFAULT)))
            } catch (e: Exception) {
                println("Error while decrypting: $e")
            }
        }
        return null
    }
}