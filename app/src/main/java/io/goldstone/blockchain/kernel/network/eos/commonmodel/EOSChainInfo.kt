package io.goldstone.blockchain.kernel.network.eos.commonmodel

import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.common.utils.toIntOrZero
import org.json.JSONObject
import java.io.Serializable

data class EOSChainInfo(
	val serverVersion: String,
	val headBlockNumber: Int,
	val lastIrreversibleBlockNumber: Int,
	val headBlockId: String,
	val headBlockTime: String,
	val headBlockProducer: String,
	val recentSlots: String,
	val participationRate: String
) : Serializable {
	constructor(data: JSONObject) : this(
		data.safeGet("server_version"),
		data.safeGet("head_block_num").toIntOrZero(),
		data.safeGet("last_irreversible_block_num").toIntOrZero(),
		data.safeGet("head_block_id"),
		data.safeGet("head_block_time"),
		data.safeGet("head_block_producer"),
		data.safeGet("recent_slots"),
		data.safeGet("participation_rate")
	)
}