@file:Suppress("DEPRECATION")

package io.goldstone.blockchain.crypto.bitcoincash

import io.goldstone.blockchain.crypto.bip32.generateKey
import io.goldstone.blockchain.crypto.bip39.Mnemonic
import io.goldstone.blockchain.crypto.litecoin.BaseKeyPair
import org.bitcoinj.core.Address
import org.bitcoinj.core.ECKey
import org.bitcoinj.core.Utils
import org.bitcoinj.params.MainNetParams

/**
 * @date 2018/8/15 11:21 AM
 * @author KaySaith
 */

object BCHWalletUtils {
	fun generateBCHKeyPair(
		mnemonic: String,
		path: String
	): BaseKeyPair {
		val seed = Mnemonic.mnemonicToSeed(mnemonic)
		val ecKey = ECKey.fromPrivate(generateKey(seed, path).keyPair.privateKey)
		val net = MainNetParams.get()
		val legacyAddress = Address(net, Utils.sha256hash160(ecKey.pubKey)).toBase58()
		val address =
			BechCashUtil.instance.encodeCashAdrressByLegacy(legacyAddress)
		return BaseKeyPair(address, ecKey.getPrivateKeyAsWiF(net))
	}

	fun isValidAddress(address: String): Boolean {
		// TODO
		System.out.println(address)
		return true
	}
}