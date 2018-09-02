package io.goldstone.blockchain.crypto.bitcoin

import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.Address
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.crypto.bitcoincash.BCHWalletUtils
import io.goldstone.blockchain.crypto.isValid
import io.goldstone.blockchain.crypto.litecoin.LTCWalletUtils
import org.bitcoinj.core.DumpedPrivateKey
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.params.TestNet3Params

/**
 * @date 2018/7/19 2:15 PM
 * @author KaySaith
 */
object BTCUtils {

	fun isValidTestnetPrivateKey(privateKey: String): Boolean {
		val dpk = DumpedPrivateKey.fromBase58(null, privateKey)
		val key = dpk.key
		// checking our key object
		val testnet3 = TestNet3Params.get()
		val check = key.getPrivateKeyAsWiF(testnet3)
		return privateKey == check
	}

	fun isValidMainnetPrivateKey(privateKey: String): Boolean {
		val dpk = DumpedPrivateKey.fromBase58(null, privateKey)
		val key = dpk.key
		// checking our key object
		val main = MainNetParams.get()
		val check = key.getPrivateKeyAsWiF(main)
		return privateKey == check
	}

	fun isValidMainnetAddress(address: String): Boolean {
		return when {
			address.isEmpty() -> false
			!CryptoValue.isBitcoinAddressLength(address) -> false
			!address.matches("^[1-9A-HJ-NP-Za-z]+$".toRegex()) -> false
			address.substring(0, 1).toIntOrNull().isNull() -> false
			else -> true
		}
	}

	fun isValidTestnetAddress(address: String): Boolean {
		return when {
			address.isEmpty() -> false
			!CryptoValue.isBitcoinAddressLength(address)  -> false
			!address.matches("^[1-9A-HJ-NP-Za-z]+$".toRegex()) -> false
			address.substring(0, 1) != "m" && address.substring(0, 1) != "n" -> false
			else -> true
		}
	}

	fun isValidMultiChainAddress(address: String, symbol: String): AddressType? {
		return when {
			Address(address).isValid() -> AddressType.ETHERCOrETC
			isValidMainnetAddress(address)
				&& symbol.equals(CryptoSymbol.btc(), true) -> AddressType.BTC
			isValidTestnetAddress(address) -> {
				when {
					symbol.equals(CryptoSymbol.bch, true) -> {
						if (Config.isTestEnvironment()) AddressType.BCH
						else null
					}
					symbol.equals(CryptoSymbol.ltc, true) -> {
						if (Config.isTestEnvironment()) AddressType.LTC
						else null
					}
					else -> AddressType.BTCSeriesTest
				}
			}
			LTCWalletUtils.isValidAddress(address) -> AddressType.LTC
			BCHWalletUtils.isValidAddress(address) -> AddressType.BCH
			else -> null
		}
	}
}

enum class AddressType(val value: String, val symbol: String) {
	ETHERCOrETC("ethERCOrETC", CryptoSymbol.eth),
	BTC("btc", CryptoSymbol.pureBTCSymbol),
	BCH("bch", CryptoSymbol.bch),
	BTCSeriesTest("btcTest", CryptoSymbol.pureBTCSymbol),
	LTC("ltc", CryptoSymbol.ltc)
}
