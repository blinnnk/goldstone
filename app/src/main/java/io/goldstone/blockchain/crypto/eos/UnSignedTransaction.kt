package io.goldstone.blockchain.crypto.eos

import java.io.Serializable

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
) : Serializable {
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

		fun createObject(transaction: UnSignedTransaction): String {
			return "{\"available_keys\": ${transaction.availableKeys},\"transaction\":{\"actions\":${transaction.actionObjects},\"context_free_actions\":[],\"context_free_data\":[],\"delay_sec\":${transaction.delaySec},\"expiration\":\"${transaction.expiration}\",\"max_kcpu_usage\":${transaction.maxKCpuUsage},\"max_net_usage_words\":${transaction.maxNetUsageWords},\"ref_block_num\":${transaction.refBlockNum},\"ref_block_prefix\":${transaction.refBlockPrefix},\"signatures\":[]}}"
		}
	}
}