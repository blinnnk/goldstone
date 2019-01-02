@file:Suppress("DEPRECATION")

package io.goldstone.blockchain.crypto.utils

import android.annotation.TargetApi
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import io.goldstone.blockchain.common.utils.AesCrypto
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import javax.crypto.Cipher
import javax.crypto.Cipher.*

data class KeystoreInfo(val alias: String, val cipher: Cipher) {
	companion object {
		fun isMnemonic(): KeystoreInfo {
			val cipher = getInstance("RSA/ECB/PKCS1Padding")
			return KeystoreInfo("skipBackUp", cipher)
		}

		fun isFingerPrinter(cipher: Cipher): KeystoreInfo {
			return KeystoreInfo("fingerPrinter", cipher)
		}
	}
}

class JavaKeystoreUtil(private val keystoreInfo: KeystoreInfo) {
	private val keyStore: KeyStore
	private val keystoreProvider = "AndroidKeyStore"
	init {
		keyStore = KeyStore.getInstance(keystoreProvider)
		keyStore.load(null)
		if (!keyStore.containsAlias(keystoreInfo.alias)) createNewKey()
	}

	private fun createNewKey() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			createNewKeyM()
		}
	}

	@TargetApi(Build.VERSION_CODES.M)
	private fun createNewKeyM() {
		val generator =
			KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, keystoreProvider)
		generator.initialize(
			KeyGenParameterSpec.Builder(
				keystoreInfo.alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
			).setBlockModes(KeyProperties.BLOCK_MODE_ECB).setEncryptionPaddings(
				KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1
			).build()
		)
		generator.generateKeyPair()
	}

	fun encryptData(content: String): String {
		return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			AesCrypto.encrypt(content).orEmpty()
		} else {
			val encryptKey = keyStore.getCertificate(keystoreInfo.alias).publicKey
			keystoreInfo.cipher.init(ENCRYPT_MODE, encryptKey)
			val result = keystoreInfo.cipher.doFinal(content.toByteArray())
			Base64.encodeToString(result, Base64.DEFAULT)
		}
	}

	fun decryptData(encryptContent: String): String {
		return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			AesCrypto.decrypt(encryptContent).orEmpty()
		} else {
			val decryptKey = keyStore.getKey(keystoreInfo.alias, null) as PrivateKey
			keystoreInfo.cipher.init(DECRYPT_MODE, decryptKey)
			val result = keystoreInfo.cipher.doFinal(Base64.decode(encryptContent, Base64.DEFAULT))
			String(result)
		}
	}
}