@file:JvmName("BIP32")

package io.goldstone.blinnnk.crypto.bip32

import io.goldstone.blinnnk.crypto.bip44.BIP44

fun generateKey(seed: ByteArray, path: String): ExtendedKey {
	val master = ExtendedKey.createFromSeed(seed)
	var child = master
	BIP44.fromPath(path).toIntList().forEach {
		child = child.generateChildKey(it)
	}
	
	return child
}

