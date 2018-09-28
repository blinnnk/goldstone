package io.goldstone.blockchain.kernel.network.eos.model

import com.google.gson.annotations.SerializedName

/**
 * @date: 2018/9/20.
 * @author: yanglihai
 * @description: eos global 信息model
 */

class EOSGolbalModel {
	@SerializedName("max_block_net_usage")
	var maxBlockNetUsage: Long = 0
	@SerializedName("target_block_net_usage_pct")
	var targetBlockNetUsagePct: Long = 0
	@SerializedName("max_transaction_net_usage")
	var maxTransactionNetUsage: Long = 0
	@SerializedName("base_per_transaction_net_usage")
	var basePerTransactionNetUsage: Long = 0
	@SerializedName("net_usage_leeway")
	var netUsageLeeway: Long = 0
	@SerializedName("context_free_discount_net_usage_num")
	var contextFreeDiscountNetUsageNum: Long = 0
	@SerializedName("context_free_discount_net_usage_den")
	var contextFreeDiscountNetUsageDen: Long = 0
	@SerializedName("max_block_cpu_usage")
	var maxBlockCpuUsage: Long = 0
	@SerializedName("target_block_cpu_usage_pct")
	var targetBlockCpuUsagePct: Long = 0
	@SerializedName("max_transaction_cpu_usage")
	var maxTransactionCpuUsage: Long = 0
	@SerializedName("min_transaction_cpu_usage")
	var mLongransactionCpuUsage: Long = 0
	@SerializedName("max_transaction_lifetime")
	var maxTransactionLifetime: Long = 0
	@SerializedName("deferred_trx_expiration_window")
	var deferredTrxExpirationWindow: Long = 0
	@SerializedName("max_transaction_delay")
	var maxTransactionDelay: Long = 0
	@SerializedName("max_inline_action_size")
	var maxInlineActionSize: Long = 0
	@SerializedName("max_inline_action_depth")
	var maxInlineActionDepth: Long = 0
	@SerializedName("max_authority_depth")
	var maxAuthorityDepth: Long = 0
	@SerializedName("max_ram_size")
	var maxRamSize: String? = null
	@SerializedName("total_ram_bytes_reserved")
	var totalRamBytesReserved: String? = null
	@SerializedName("total_ram_stake")
	var totalRamStake: String? = null
	@SerializedName("last_producer_schedule_update")
	var lastProducerScheduleUpdate: String? = null
	@SerializedName("last_pervote_bucket_fill")
	var lastPervoteBucketFill: String? = null
	@SerializedName("pervote_bucket")
	var pervoteBucket: Long = 0
	@SerializedName("perblock_bucket")
	var perblockBucket: Long = 0
	@SerializedName("total_unpaid_blocks")
	var totalUnpaidBlocks: Long = 0
	@SerializedName("total_activated_stake")
	var totalActivatedStake: String? = null
	@SerializedName("thresh_activated_stake_time")
	var threshActivatedStakeTime: String? = null
	@SerializedName("last_producer_schedule_size")
	var lastProducerScheduleSize: Long = 0
	@SerializedName("total_producer_vote_weight")
	var totalProducerVoteWeight: String? = null
	@SerializedName("last_name_close")
	var lastNameClose: String? = null
	
}