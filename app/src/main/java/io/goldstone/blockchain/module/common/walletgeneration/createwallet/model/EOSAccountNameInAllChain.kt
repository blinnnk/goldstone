package io.goldstone.blockchain.module.common.walletgeneration.createwallet.model

import android.arch.persistence.room.TypeConverter
import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.kernel.databaseinterface.RoomModel
import org.json.JSONObject


/**
 * @author KaySaith
 * @date  2018/09/13
 */

data class EOSDefaultAllChainName(
	var main: String,
	var jungle: String
) : RoomModel {
	constructor(data: JSONObject) : this(
		data.safeGet("main"),
		data.safeGet("jungle")
	)

	override fun getObject(): String {
		return "{\"main\":\"$main\",\"jungle\":\"$jungle\"}"
	}

	fun getCurrent(): String {
		return when (Config.getEOSCurrentChain()) {
			ChainID.eosMain -> main
			ChainID.eosTest -> jungle
			else -> main
		}
	}

	fun updateCurrent(name: String): EOSDefaultAllChainName {
		return apply {
			when (Config.getEOSCurrentChain()) {
				ChainID.eosMain -> main = name
				ChainID.eosTest -> jungle = name
				else -> main = name
			}
		}
	}
}

class EOSDefaultAllChainNameConverter {
	@TypeConverter
	fun convertToModel(content: String): EOSDefaultAllChainName {
		val data = JSONObject(content)
		return EOSDefaultAllChainName(data)
	}

	@TypeConverter
	fun convertToString(model: EOSDefaultAllChainName): String {
		return model.getObject()
	}
}