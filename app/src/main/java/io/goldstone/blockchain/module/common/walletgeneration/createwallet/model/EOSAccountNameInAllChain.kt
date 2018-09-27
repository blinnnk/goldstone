package io.goldstone.blockchain.module.common.walletgeneration.createwallet.model

import android.arch.persistence.room.TypeConverter
import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.common.sharedpreference.SharedChain
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
		val currentChainID = SharedChain.getEOSCurrent()
		return when {
			currentChainID.isEOSMain() -> main
			currentChainID.isEOSTest() -> jungle
			else -> main
		}
	}

	fun getUnEmptyValue(): String {
		return listOf(main, jungle).firstOrNull { it.isNotEmpty() } ?: ""
	}

	fun updateCurrent(name: String): EOSDefaultAllChainName {
		val currentChainID = SharedChain.getEOSCurrent()
		return apply {
			when {
				currentChainID.isEOSMain() -> main = name
				currentChainID.isEOSTest() -> jungle = name
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