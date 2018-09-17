package io.goldstone.blockchain.crypto.keystore

import org.json.JSONObject

/**
 * @date 05/04/2018 2:43 PM
 * @author KaySaith
 */
data class KeystoreModel(
	var address: String,
	var cipher: String,
	var ciphertext: String,
	var iv: String,
	var kdf: String,
	var dklen: Int,
	var n: Int,
	var p: Int,
	var r: Int,
	var salt: String,
	var mac: String,
	var id: String,
	var version: Int
)

fun String.toJsonObject(): JSONObject {
	return if (contains("{")) {
		JSONObject(substring(indexOf("{"), lastIndexOf("}") + 1))
	} else {
		JSONObject("{content:$this}")
	}
}

fun String.convertKeystoreToModel(): KeystoreModel {
	val address = toJsonObject()["address"].toString()
	val cipher = toJsonObject()["crypto"].toString().toJsonObject()["cipher"].toString()
	val ciphertext = toJsonObject()["crypto"].toString().toJsonObject()["ciphertext"].toString()
	val iv =
		toJsonObject()["crypto"].toString().toJsonObject()["cipherparams"].toString().toJsonObject()["iv"]
			.toString()
	val kdf = toJsonObject()["crypto"].toString().toJsonObject()["kdf"].toString()
	val dklen =
		toJsonObject()["crypto"].toString().toJsonObject()["kdfparams"].toString().toJsonObject()["dklen"]
			.toString().toInt()
	val n =
		toJsonObject()["crypto"].toString().toJsonObject()["kdfparams"].toString().toJsonObject()["n"]
			.toString().toInt()
	val p =
		toJsonObject()["crypto"].toString().toJsonObject()["kdfparams"].toString().toJsonObject()["p"]
			.toString().toInt()
	val r =
		toJsonObject()["crypto"].toString().toJsonObject()["kdfparams"].toString().toJsonObject()["r"]
			.toString().toInt()
	val salt =
		toJsonObject()["crypto"].toString().toJsonObject()["kdfparams"].toString().toJsonObject()["salt"]
			.toString()
	val mac = toJsonObject()["crypto"].toString().toJsonObject()["mac"].toString()
	val id = toJsonObject()["id"].toString()
	val version = toJsonObject()["version"].toString().toInt()
	return KeystoreModel(
		address,
		cipher,
		ciphertext,
		iv,
		kdf,
		dklen,
		n,
		p,
		r,
		salt,
		mac,
		id,
		version
	)
}