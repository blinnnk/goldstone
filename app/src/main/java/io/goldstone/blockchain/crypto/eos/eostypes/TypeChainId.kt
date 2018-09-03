package io.goldstone.blockchain.crypto.eos.eostypes

import org.bitcoinj.core.Sha256Hash

class TypeChainId {
	private val chainID: Sha256Hash

	val bytes: ByteArray
		get() = chainID.bytes

	constructor() {
		chainID = Sha256Hash.ZERO_HASH
	}

	private fun getSha256FromHexStr(content: String): ByteArray {
		val len = content.length
		val bytes = ByteArray(32)
		var index = 0
		while (index < len) {
			val strIte = content.substring(index, index + 2)
			val n = Integer.parseInt(strIte, 16) and 0xFF
			bytes[index / 2] = n.toByte()
			index += 2
		}
		return bytes
	}

	constructor(content: String) {
		chainID = Sha256Hash.of(getSha256FromHexStr(content))
	}
}