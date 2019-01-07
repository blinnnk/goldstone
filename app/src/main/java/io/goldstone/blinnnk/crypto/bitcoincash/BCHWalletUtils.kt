@file:Suppress("DEPRECATION")

package io.goldstone.blinnnk.crypto.bitcoincash

import io.goldstone.blinnnk.crypto.bip32.generateKey
import io.goldstone.blinnnk.crypto.bip39.Mnemonic
import io.goldstone.blinnnk.crypto.bitcoin.BTCUtils
import io.goldstone.blinnnk.crypto.bitcoin.BTCWalletUtils
import io.goldstone.blinnnk.crypto.litecoin.BaseKeyPair
import io.goldstone.blinnnk.crypto.litecoin.ChainPrefix
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

	// Mainnet Checker
	// Only support Mainnet Address
	fun isValidAddress(address: String, isTestNet: Boolean = false): Boolean {
		val net =
			if (isTestNet) TestNet3Params.get() else MainNetParams.get()
		return if (isNewCashAddress(address)) try {
			formattedToLegacy(address, net)
			true
		} catch (error: Exception) {
			false
		} else {
			if (BTCUtils.isValidTestnetAddress(address) && !isTestNet) false
			else try {
				BCHUtil.instance.encodeCashAddressByLegacy(address)
				true
			} catch (error: Exception) {
				false
			}
		}
	}

	fun isNewCashAddress(address: String): Boolean {
		return if (address.isEmpty()) false
		else address.contains(":") || address.substring(0, 1).equals("q", true)
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