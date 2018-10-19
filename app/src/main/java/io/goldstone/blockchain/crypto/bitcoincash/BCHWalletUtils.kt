@file:Suppress("DEPRECATION")

package io.goldstone.blockchain.crypto.bitcoincash

import io.goldstone.blockchain.crypto.bip32.generateKey
import io.goldstone.blockchain.crypto.bip39.Mnemonic
import io.goldstone.blockchain.crypto.bitcoin.BTCWalletUtils
import io.goldstone.blockchain.crypto.litecoin.BaseKeyPair
import io.goldstone.blockchain.crypto.litecoin.ChainPrefix
import io.goldstone.blockchain.crypto.multichain.CryptoValue
import org.bitcoinj.core.Address
import org.bitcoinj.core.ECKey
import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.core.Utils
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.params.TestNet3Params
import java.math.BigInteger

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
			BCHUtil.instance.encodeCashAddressByLegacy(legacyAddress)
		return BaseKeyPair(address, ecKey.getPrivateKeyAsWiF(net))
	}

	fun getAddressByPrivateKey(privatekey: BigInteger): String {
		return BCHUtil.instance.encodeCashAddressByLegacy(
			ECKey.fromPrivate(privatekey).toAddress(MainNetParams.get()).toBase58()
		)
	}

	fun getAddressByWIFKey(privatekey: String): String {
		return BCHUtil.instance.encodeCashAddressByLegacy(
			BTCWalletUtils.getPublicKeyFromBase58PrivateKey(privatekey, false)
		)
	}

	fun isValidAddress(address: String): Boolean {
		return when {
			address.length < CryptoValue.bitcoinAddressClassicLength -> false
			address.contentEquals(":") &&
				!address.substringAfter(":").substring(0, 1).equals("q", true) -> false
			address.substring(0, 1).equals("q", true) &&
				address.length < CryptoValue.bchNewAddressMinLength -> false
			CryptoValue.isBitcoinAddressLength(address) &&
				address.substring(0, 1).equals("m", true) -> false
			CryptoValue.isBitcoinAddressLength(address) &&
				address.substring(0, 1).equals("n", true) -> false
			else -> true
		}
	}

	fun isNewCashAddress(address: String): Boolean {
		return address.contains(":") || address.substring(0, 1).equals("q", true)
	}

	fun formattedToLegacy(address: String, network: NetworkParameters): String {
		val prefix =
			if (network == TestNet3Params.get()) ChainPrefix.Testnet
			else ChainPrefix.BitcoinMainnet
		return when {
			address.contains(":") ->
				AddressConverter.toLegacyAddress(address.substringAfter(":"), prefix)
			address.substring(0, 1).equals("q", true) ->
				AddressConverter.toLegacyAddress(address, prefix)
			else -> address
		}
	}
}