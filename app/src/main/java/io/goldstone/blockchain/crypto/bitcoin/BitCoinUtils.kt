package io.goldstone.blockchain.crypto.bitcoin

import io.goldstone.blockchain.common.utils.LogUtil
import org.bitcoinj.core.DumpedPrivateKey
import org.bitcoinj.core.NetworkParameters

/**
 * @date 2018/7/19 2:15 PM
 * @author KaySaith
 */
object BitCoinUtils {
	
	fun isValidTestnetPrivateKey(privateKey: String): Boolean {
		val testNetID = NetworkParameters.fromID(NetworkParameters.ID_TESTNET)
		return try {
			DumpedPrivateKey.fromBase58(testNetID, privateKey).key
			true
		} catch (error: Exception) {
			LogUtil.error("isValidTestnetPrivateKey", error)
			false
		}
	}
}