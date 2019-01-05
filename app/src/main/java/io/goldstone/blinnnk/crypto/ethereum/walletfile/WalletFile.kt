package io.goldstone.blinnnk.crypto.ethereum.walletfile

import java.io.Serializable

/**
 * @date 2018/6/17 9:13 PM
 * @author KaySaith
 */

const val AES_128_CTR = "pbkdf2"
const val SCRYPT = "scrypt"

data class CipherParams(var iv: String)

data class WalletCrypto(
	var cipher: String,
	var ciphertext: String,
	var cipherparams: CipherParams,
	var kdf: String,
	var kdfparams: KdfParams,
	var mac: String
): Serializable

data class WalletCryptoForImport(
	var cipher: String,
	var ciphertext: String,
	var cipherparams: CipherParams,
	var kdf: String,
	var kdfparams: Map<String, String>,
	var mac: String
): Serializable

data class Wallet(
	val address: String?,
	val crypto: WalletCrypto,
	val id: String,
	val version: Int
): Serializable