package io.goldstone.blinnnk.crypto.ethereum.walletfile

import java.io.Serializable

/**
 * @date 2018/6/17 9:14 PM
 * @author KaySaith
 */


sealed class KdfParams {
	abstract var dklen: Int
	abstract var salt: String?
}


data class Aes128CtrKdfParams(
	var c: Int = 0,
	var prf: String? = null,
	override var dklen: Int = 0,
	override var salt: String? = null
) : KdfParams()


data class ScryptKdfParams(
	var n: Int = 0,
	var p: Int = 0,
	var r: Int = 0,
	override var dklen: Int = 0,
	override var salt: String? = null
) : Serializable, KdfParams()

fun WalletCryptoForImport.getTypedKdfParams() = if (kdf == SCRYPT) {
	ScryptKdfParams().apply {
		n = kdfparams["n"]!!.toInt()
		p = kdfparams["p"]!!.toInt()
		r = kdfparams["r"]!!.toInt()
	}
} else {
	Aes128CtrKdfParams().apply {
		c = kdfparams["c"]!!.toInt()
		prf = kdfparams["prf"]!!
	}
}.apply {
	salt = kdfparams["salt"]
	dklen = kdfparams["dklen"]!!.toInt()
}