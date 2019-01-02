package io.goldstone.blockchain.crypto.bitcoin

import com.blinnnk.extension.isNull
import io.goldstone.blockchain.crypto.multichain.CryptoValue
import org.bitcoinj.core.DumpedPrivateKey
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.params.TestNet3Params

/**
 * @date 2018/7/19 2:15 PM
 * @author KaySaith
 */
object BTCUtils {

	fun isValidTestnetPrivateKey(privateKey: String): Boolean {
		return try {
			val dpk = DumpedPrivateKey.fromBase58(null, privateKey)
			val key = dpk.key
			// checking our key object
			val testnet3 = TestNet3Params.get()
			val check = key.getPrivateKeyAsWiF(testnet3)
			privateKey == check
		} catch (error: Exception) {
			false
		}
	}

	fun isValidMainnetPrivateKey(privateKey: String): Boolean {
		return try {
			val dpk = DumpedPrivateKey.fromBase58(null, privateKey)
			val key = dpk.key
			// checking our key object
			val main = MainNetParams.get()
			val check = key.getPrivateKeyAsWiF(main)
			privateKey == check
		} catch (error: Exception) {
			false
		}
	}

	fun isValidMainnetAddress(address: String): Boolean {
		return when {
			address.isEmpty() -> false
			!CryptoValue.isBitcoinAddressLength(address) -> false
			address.substring(0, 1).toIntOrNull().isNull() -> false
			else -> true
		}
	}

	fun isValidTestnetAddress(address: String): Boolean {
		return when {
			address.isEmpty() -> false
			!CryptoValue.isBitcoinAddressLength(address) -> false
			!address.matches("^[1-9A-HJ-NP-Za-z]+$".toRegex()) -> false
			address.substring(0, 1) != "m" && address.substring(0, 1) != "n" -> false
			else -> true
		}
	}
}
