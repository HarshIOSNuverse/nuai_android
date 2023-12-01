package com.checkmyself.utils

import android.util.Base64
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object DarKnight {

    private const val ALGORITHM = "AES"
    private val SALT = "tHeApAcHe6410111".toByteArray() // THE KEY MUST BE SAME
    private val X = DarKnight::class.java.simpleName

    fun getEncrypted(plainText: String?): String? {
        if (plainText == null) {
            return null
        }
        val salt = salt
        try {
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, salt)
            val encodedValue = cipher.doFinal(plainText.toByteArray())
            return Base64.encode(encodedValue, 0).toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        throw IllegalArgumentException("Failed to encrypt data")
    }

    fun getDecrypted(encodedText: String?): String? {
        if (encodedText == null) {
            return null
        }
        val salt = salt
        try {
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, salt)
            val decodedValue: ByteArray = Base64.decode(encodedText, 0)
            val decValue = cipher.doFinal(decodedValue)
            return decValue.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    val salt: Key
        get() = SecretKeySpec(SALT, ALGORITHM)
}