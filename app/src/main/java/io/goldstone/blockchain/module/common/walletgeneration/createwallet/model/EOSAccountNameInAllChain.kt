package io.goldstone.blockchain.module.common.walletgeneration.createwallet.model

import android.arch.persistence.room.TypeConverter
import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.kernel.databaseinterface.RoomModel
import org.json.JSONObject


/**
 * @author KaySaith
 * @date  2018/09/13
 */

data class EOSDefaultAllChainName(
	var main: String,
	var jungle: String,
	var kylin: String
) : RoomModel {
	constructor(data: JSONObject) : this(
		data.safeGet("main"),
		data.safeGet("jungle"),
		data.safeGet("kylin")
	)

	override fun getObject(): String {
		return "{\"main\":\"$main\",\"jungle\":\"$jungle\",\"kylin\":\"$kylin\"}"
	}

	fun getCurrent(): String {
		return getTarget(SharedChain.getEOSCurrent().chainID)
	}

	fun getTarget(chaiID: ChainID): String {
		return when {
			chaiID.isEOSMain() -> main
			chaiID.isEOSJungle() -> jungle
			chaiID.isEOSKylin() -> kylin
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
				currentChainID.chainID.isEOSMain() -> main = name
				currentChainID.chainID.isEOSJungle() -> jungle = name
				currentChainID.chainID.isEOSKylin() -> kylin = name
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