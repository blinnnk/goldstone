@file:Suppress("DEPRECATION")

package io.goldstone.blinnnk.crypto.bitcoin

import io.goldstone.blinnnk.crypto.bip32.generateKey
import io.goldstone.blinnnk.crypto.bip39.Mnemonic
import io.goldstone.blinnnk.crypto.multichain.ChainPath
import io.goldstone.blinnnk.crypto.multichain.ChainType
import org.bitcoinj.core.*

/**
 * @date 2018/7/13 12:13 PM
 * @author KaySaith
 */
object BTCWalletUtils {

	fun getBitcoinWalletByMnemonic(
		mnemonicCode: String,
		path: String,
		hold: (address: String, secret: String) -> Unit
	) {
		val seed = Mnemonic.mnemonicToSeed(mnemonicCode, "")
		val keyPair =
			ECKey.fromPrivate(generateKey(seed, path).keyPair.privateKey, true)
		val isTest = ChainType.isBTCTest(ChainPath.pathToChainType(path))
		val testNetID = NetworkParameters.fromID(NetworkParameters.ID_TESTNET)
		val mainNetID = NetworkParameters.fromID(NetworkParameters.ID_MAINNET)
		val currentID = if (isTest) testNetID else mainNetID
		val address = Address(currentID, Utils.sha256hash160(keyPair.pubKey)).toBase58()
		val secret = keyPair.getPrivateKeyEncoded(currentID).toString()
		hold(address, secret)
	}

	private fun getKeyPairFromBase58PrivateKey(
		privateKey: String,
		isTest: Boolean
	): ECKey {
		val testNetID = NetworkParameters.fromID(NetworkParameters.ID_TESTNET)
		val mainNetID = NetworkParameters.fromID(NetworkParameters.ID_MAINNET)
		val currentID = if (isTest) testNetID else mainNetID
		return DumpedPrivateKey.fromBase58(currentID, privateKey).key
	}

	fun getPublicKeyFromBase58PrivateKey(
		privateKey: String,
		isTest: Boolean
	): String {
		val testNetID = NetworkParameters.fromID(NetworkParameters.ID_TESTNET)
		val mainNetID = NetworkParameters.fromID(NetworkParameters.ID_MAINNET)
		val chainID = if (isTest) testNetID else mainNetID
		return Address(chainID, Utils.sha256hash160(getKeyPairFromBase58PrivateKey(privateKey, isTest).pubKey)).toBase58()
	}
}