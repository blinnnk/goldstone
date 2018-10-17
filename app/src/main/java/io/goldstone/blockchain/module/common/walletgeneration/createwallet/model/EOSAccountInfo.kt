package io.goldstone.blockchain.module.common.walletgeneration.createwallet.model

import android.arch.persistence.room.TypeConverter
import com.blinnnk.extension.isNull
import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.kernel.databaseinterface.RoomModel
import org.json.JSONArray
import org.json.JSONObject


/**
 * @author KaySaith
 * @date  2018/09/13
 */

data class EOSAccountInfo(
	val name: String,
	val chainID: String,
	val publicKey: String
) : RoomModel {
	constructor(data: JSONObject) : this(
		data.safeGet("name"),
		data.safeGet("chainID"),
		data.safeGet("publicKey")
	)

	constructor(accountName: String, chainID: String) : this(
		accountName,
		chainID,
		""
	)

	fun hasActivated(): Boolean {
		return if (SharedWallet.isWatchOnlyWallet() && name.isNotEmpty()) ChainID(chainID).isCurrent()
		else {
			ChainID(chainID).isCurrent() && publicKey.equals(SharedAddress.getCurrentEOS(), true)
		}
	}

	fun isActivatedOrWatchOnlyEOSAccount(): Boolean {
		return ChainID(chainID).isCurrent() && name.equals(SharedAddress.getCurrentEOSAccount().accountName, true)
	}

	override fun getObject(): String {
		return "{\"name\":\"$name\",\"chainID\":\"$chainID\",\"publicKey\":\"$publicKey\"}"
	}
}

fun List<EOSAccountInfo>.currentPublicKeyHasActivated(): Boolean {
	return !find { it.hasActivated() }.isNull()
}

fun List<EOSAccountInfo>.hasActivatedOrWatchOnlyEOSAccount(): Boolean {
	return !find { it.isActivatedOrWatchOnlyEOSAccount() }.isNull()
}

fun List<EOSAccountInfo>.getTargetKeyName(key: String): String? {
	return find { it.publicKey.equals(key, true) }?.name
}

class EOSAccountInfoConverter {
	@TypeConverter
	fun convertToModel(content: String): List<EOSAccountInfo> {
		val data = JSONArray(content)
		var accounts = listOf<EOSAccountInfo>()
		(0 until data.length()).forEach {
			accounts += EOSAccountInfo(JSONObject(data[it].toString()))
		}
		return accounts
	}

	@TypeConverter
	fun convertToString(models: List<EOSAccountInfo>): String {
		var data = ""
		models.forEach {
			data += it.getObject() + ","
		}
		data = data.substringBeforeLast(",")
		return "[$data]"
	}
}