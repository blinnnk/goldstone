package io.goldstone.blockchain.crypto.bitcoin

import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.crypto.CryptoValue
import org.bitcoinj.core.DumpedPrivateKey
import org.bitcoinj.core.NetworkParameters

/**
 * @date 2018/7/19 2:15 PM
 * @author KaySaith
 */
object BitCoinUtils {
	
	fun isValidTestnetPrivateKey(privateKey: String): Boolean {
		if (privateKey.length != CryptoValue.bitcoinPrivateKeyLength) return false
		val testNetID = NetworkParameters.fromID(NetworkParameters.ID_TESTNET)
		return try {
			DumpedPrivateKey.fromBase58(testNetID, privateKey).key
			true
		} catch (error: Exception) {
			LogUtil.error("isValidTestnetPrivateKey", error)
			false
		}
	}
	
	fun isValidMainnetPrivateKey(privateKey: String): Boolean {
		if (privateKey.length != CryptoValue.bitcoinPrivateKeyLength) return false
		val mainNetID = NetworkParameters.fromID(NetworkParameters.ID_MAINNET)
		return try {
			DumpedPrivateKey.fromBase58(mainNetID, privateKey).key
			true
		} catch (error: Exception) {
			LogUtil.error("isValidMainnetPrivateKey", error)
			false
		}
	}
	
	fun isValidMainnetAddress(address: String): Boolean {
		// 没有找到具体的方法，目前的规律是目测出来的，如果遇到问题需要更改。
		return if (address.isEmpty() || address.length != CryptoValue.bitcoinAddressLength) false
		else !address.substring(0, 1).toIntOrNull().isNull()
	}
	
	fun isValidTestnetAddress(address: String): Boolean {
		// 没有找到具体的方法，目前的规律是目测出来的，如果遇到问题需要更改。
		return if (address.isEmpty() || address.length != CryptoValue.bitcoinAddressLength) false
		else address.substring(0, 1).toIntOrNull().isNull()
	}
}