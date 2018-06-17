package io.goldstone.blockchain.crypto.kecca

import io.goldstone.blockchain.crypto.utils.hexToByteArray
import org.spongycastle.jcajce.provider.digest.Keccak

fun String.keccak() = hexToByteArray().keccak()
fun ByteArray.keccak() = Keccak.Digest256().let {
	it.update(this)
	it.digest()
}!!
