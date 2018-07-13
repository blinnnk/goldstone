package io.goldstone.blockchain.crypto.bitcoin

import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.crypto.DefaultPath
import io.goldstone.blockchain.crypto.bip32.generateKey
import io.goldstone.blockchain.crypto.bip39.Mnemonic
import org.bitcoinj.core.*

/**
 * @date 2018/7/13 12:13 PM
 * @author KaySaith
 */
object BTCUtils {
	
	fun getBitcoinWalletByMnemonic(
		mnemonicCode: String,
		path: String = DefaultPath.btcPath,
		hold: (address: String, secret: String) -> Unit
	) {
		val seed = Mnemonic.mnemonicToSeed(mnemonicCode, "")
		val keyPair =
			ECKey.fromPrivate(generateKey(seed, path).keyPair.privateKey, true)
		val isTest = CryptoValue.isBtcTest(CryptoValue.chainType(path))
		val testNetID = NetworkParameters.fromID(NetworkParameters.ID_TESTNET)
		val mainNetID = NetworkParameters.fromID(NetworkParameters.ID_MAINNET)
		val currentID = if (isTest) testNetID else mainNetID
		val address = Address(currentID, Utils.sha256hash160(keyPair.pubKey)).toString()
		val secret = keyPair.getPrivateKeyEncoded(currentID).toString()
		hold(address, secret)
	}
	
	fun getKeyPairFromBase58PrivateKey(
		privateKey: String,
		isTest: Boolean,
		hold: (ECKey) -> Unit
	) {
		val testNetID = NetworkParameters.fromID(NetworkParameters.ID_TESTNET)
		val mainNetID = NetworkParameters.fromID(NetworkParameters.ID_MAINNET)
		val currentID = if (isTest) testNetID else mainNetID
		return hold(
			DumpedPrivateKey.fromBase58(currentID, privateKey).key
		)
	}
	
	fun getPublicKeyFromBase58PrivateKey(
		privateKey: String,
		isTest: Boolean,
		hold: (publicKey: String) -> Unit
	) {
		val testNetID = NetworkParameters.fromID(NetworkParameters.ID_TESTNET)
		val mainNetID = NetworkParameters.fromID(NetworkParameters.ID_MAINNET)
		val chainID = if (isTest) testNetID else mainNetID
		getKeyPairFromBase58PrivateKey(privateKey, isTest) {
			hold(Address(chainID, Utils.sha256hash160(it.pubKey)).toString())
		}
	}
}