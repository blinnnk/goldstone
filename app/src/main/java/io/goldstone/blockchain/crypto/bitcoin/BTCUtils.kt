package io.goldstone.blockchain.crypto.bitcoin

import com.blinnnk.extension.isNull
import io.goldstone.blockchain.crypto.Address
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.crypto.isValid
import org.bitcoinj.core.DumpedPrivateKey
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.params.TestNet3Params
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

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
			address.length != CryptoValue.bitcoinAddressLength -> false
			!address.matches("^[1-9A-HJ-NP-Za-z]+$".toRegex()) -> false
			address.substring(0, 1).toIntOrNull().isNull() -> false
			else -> true
		}
	}
	
	fun isValidTestnetAddress(address: String): Boolean {
		return when {
			address.isEmpty() -> false
			address.length != CryptoValue.bitcoinAddressLength -> false
			!address.matches("^[1-9A-HJ-NP-Za-z]+$".toRegex()) -> false
			!address.substring(0, 1).toIntOrNull().isNull() -> false
			else -> true
		}
	}
	
	fun isBTCAddress(address: String): Boolean {
		return isValidTestnetAddress(address) || isValidMainnetAddress(address)
	}
	
	fun isValidMultiChainAddress(address: String): AddressType? {
		return when {
			Address(address).isValid() -> AddressType.ETHERCOrETC
			isValidMainnetAddress(address) -> AddressType.BTC
			isValidTestnetAddress(address) -> AddressType.BTCTest
			else -> null
		}
	}
}

enum class AddressType {
	ETHERCOrETC, BTC, BTCTest
}

object DoubleSHA256 {
	
	fun gen(input: ByteArray): ByteArray {
		var digester: MessageDigest? = null
		try {
			digester = MessageDigest.getInstance("SHA-256")
		} catch (e: NoSuchAlgorithmException) {
			// TODO Auto-generated catch block
			e.printStackTrace()
		}
		
		return digester!!.digest(digester.digest(input))
	}
}

fun String.toLittleEndian(): String? {
	return if (length % 2 == 0) {
		var endianHash = ""
		(0 until length / 2).forEach {
			endianHash += substring(length - (it + 1) * 2, length - it * 2)
		}
		endianHash
	} else {
		null
	}
}