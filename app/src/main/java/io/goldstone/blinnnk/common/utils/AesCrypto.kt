package io.goldstone.blinnnk.common.utils

/**
 * @date 2018/5/9 12:40 PM
 * @author KaySaith
 */

import android.util.Base64
import io.goldstone.blinnnk.common.jni.JniManager
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AesCrypto {

	private var key = ""
	private const val iv = "Ydm0uvZh3IJxQOS8"
	private const val algo = "AES"
	private const val algoMode = "AES/CBC/NoPadding"

	fun encrypt(Data: String): String? {
		key = if (key.isEmpty()) JniManager.getDecryptKey() + "dyEM6Q4<U" else key
		try {
			val cipher = Cipher.getInstance(algoMode)
			val blockSize = cipher.blockSize
			val dataBytes = Data.toByteArray()
			var plaintextLength = dataBytes.size
			if (plaintextLength % blockSize != 0) {
				plaintextLength += (blockSize - plaintextLength % blockSize)
			}
			val plaintext = ByteArray(plaintextLength)
			System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.size)

			val keyspec = SecretKeySpec(key.toByteArray(charset("utf-8")), algo)
			val ivspec = IvParameterSpec(iv.toByteArray(charset("utf-8")))
			cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec)
			val encrypted = cipher.doFinal(plaintext)
			return String(Base64.encode(encrypted, Base64.DEFAULT))
		} catch (e: Exception) {
			e.printStackTrace()
			return null
		}
	}

	fun decrypt(encryptedData: String): String? {
		key = if (key.isEmpty()) JniManager.getDecryptKey() + "dyEM6Q4<U" else key
		return try {
			val encrypted1 = Base64.decode(encryptedData, Base64.DEFAULT)

			val cipher = Cipher.getInstance(algoMode)
			val keyspec = SecretKeySpec(key.toByteArray(charset("utf-8")), algo)
			val ivspec = IvParameterSpec(iv.toByteArray(charset("utf-8")))
			cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec)
			val original = cipher.doFinal(encrypted1)
			val originalString = String(original)
			originalString.trim { it <= ' ' }
		} catch (e: Exception) {
			e.printStackTrace()
			null
		}
	}
}
