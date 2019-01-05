package io.goldstone.blinnnk.module.common.tokendetail.eosactivation.accountselection.model

import android.arch.persistence.room.TypeConverter
import com.blinnnk.extension.safeGet
import com.blinnnk.extension.toIntOrZero
import com.blinnnk.extension.toList
import com.blinnnk.extension.toLongOrZero
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/09/12
 */

/**
 * @example
 * {"is_proxy":0,"last_vote_weight":"0.00000000000000000","owner":"hopehopehope","producers":[],"proxied_vote_weight":"0.00000000000000000","proxy":"","staked":370000}
 */
data class VoterInfo(
	val proxyName: String, // PublicKey or AccountName
	val producers: List<String>,
	val staked: Long, // EOS In 4 decimal
	val lastVoteWeight: String,
	val proxiedVoteWeight: String,
	val isProxy: Int,
	val owner: String
) : Serializable {
	constructor(data: JSONObject) : this(
		data.safeGet("proxy"),
		JSONArray(data.safeGet("producers")).toList(),
		data.safeGet("staked").toLongOrZero(),
		data.safeGet("proxy"),
		data.safeGet("last_vote_weight"),
		data.safeGet("is_proxy").toIntOrZero(),
		data.safeGet("owner")
	)
}

class VoterInfoConverter {
	@TypeConverter
	fun revertJSONObject(content: String): VoterInfo {
		val data = JSONObject(content)
		return VoterInfo(
			data.safeGet("proxy"),
			JSONArray(data.safeGet("producers")).toList(),
			data.safeGet("staked").toLongOrZero(),
			data.safeGet("proxy"),
			data.safeGet("last_vote_weight"),
			data.safeGet("is_proxy").toIntOrZero(),
			data.safeGet("owner")
		)
	}

	@TypeConverter
	fun convertToString(voterInfo: VoterInfo): String {
		return "{\"is_proxy\":${voterInfo.isProxy},\"last_vote_weight\":\"${voterInfo.lastVoteWeight}\",\"owner\":\"${voterInfo.owner}\",\"producers\":${voterInfo.producers},\"proxied_vote_weight\":\"${voterInfo.proxiedVoteWeight}\",\"proxy\":\"${voterInfo.proxyName}\",\"staked\":${voterInfo.staked}}"
	}
}