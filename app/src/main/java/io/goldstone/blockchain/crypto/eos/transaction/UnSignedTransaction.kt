package io.goldstone.blockchain.crypto.eos.transaction

import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.eos.base.EOSModel
import java.io.Serializable

/**
 * @author KaySaith
 * @date 2018/09/03
 */

data class UnSignedTransaction(
	val availableKeys: String,
	val actionObjects: String,
	val contextFreeActions: String,
	val contextFreeData: String,
	val delaySec: Long,
	val expiration: String,
	val maxKCpuUsage: Long,
	val maxNetUsageWords: Long,
	val refBlockNum: Int,
	val refBlockPrefix: Int,
	val signatures: String
) : Serializable, EOSModel {
	override fun createObject(): String {
		return "{\"available_keys\": $availableKeys,\"transaction\":{\"actions\":$actionObjects,\"context_free_actions\":[],\"context_free_data\":[],\"delay_sec\":$delaySec,\"expiration\":\"$expiration\",\"max_kcpu_usage\":$maxKCpuUsage,\"max_net_usage_words\":$maxNetUsageWords,\"ref_block_num\":$refBlockNum,\"ref_block_prefix\":$refBlockPrefix,\"signatures\":[]}}"
	}

	override fun serialize(): String {
		return ""
	}

	companion object {
		@Throws
		fun prepareAvailableKeys(vararg eosPublicKeys: String): String {
			var keys = ""
			eosPublicKeys.forEach {
				if (!EOSWalletUtils.isValidAddress(it)) throw Exception("Invalid EOS PublicKeys")
				keys += "\"$it\","
			}
			return "[${keys.substringBeforeLast(",")}]"
		}
	}
}