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
import javax.crypto.Cipher.*

class JavaKeystoreUtil(
	private val keyStoreAlias: String = "skipBackUp"
) {

	private val keyStore: KeyStore

	private val keystoreProvider = "AndroidKeyStore"
	private val rsaCipher = "RSA/ECB/PKCS1Padding"

	init {
		keyStore = KeyStore.getInstance(keystoreProvider)
		keyStore.load(null)
		if (!keyStore.containsAlias(keyStoreAlias)) createNewKey()
	}

	private fun createNewKey() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			createNewKeyM()
		}
	}

	@TargetApi(Build.VERSION_CODES.M)
	private fun createNewKeyM() {
		val generator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, keystoreProvider)
		generator.initialize(
			KeyGenParameterSpec.Builder(
				keyStoreAlias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
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
			val encryptKey = keyStore.getCertificate(keyStoreAlias).publicKey
			val cipher = getInstance(rsaCipher)
			cipher.init(ENCRYPT_MODE, encryptKey)
			val result = cipher.doFinal(content.toByteArray())
			Base64.encodeToString(result, Base64.DEFAULT)
		}
	}

	fun decryptData(encryptContent: String): String {
		return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			AesCrypto.decrypt(encryptContent).orEmpty()
		} else {
			val decryptKey = keyStore.getKey(keyStoreAlias, null) as PrivateKey
			val cipher = getInstance(rsaCipher)
			cipher.init(DECRYPT_MODE, decryptKey)
			val result = cipher.doFinal(Base64.decode(encryptContent, Base64.DEFAULT))
			String(result)
		}

	}
}