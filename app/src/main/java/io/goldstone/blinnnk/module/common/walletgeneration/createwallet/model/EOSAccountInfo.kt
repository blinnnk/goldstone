package io.goldstone.blinnnk.module.common.walletgeneration.createwallet.model

import android.arch.persistence.room.TypeConverter
import com.blinnnk.extension.isNull
import com.blinnnk.extension.safeGet
import io.goldstone.blinnnk.common.sharedpreference.SharedAddress
import io.goldstone.blinnnk.common.sharedpreference.SharedChain
import io.goldstone.blinnnk.common.sharedpreference.SharedWallet
import io.goldstone.blinnnk.kernel.databaseinterface.RoomModel
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

	private val isCurrent: () -> Boolean = {
		SharedChain.getEOSCurrent().chainID.id.equals(chainID, true)
	}

	fun hasActivated(): Boolean {
		return if (name.isNotEmpty() && SharedWallet.isWatchOnlyWallet()) isCurrent()
		else {
			isCurrent() && publicKey.equals(SharedAddress.getCurrentEOS(), true)
		}
	}

	fun isActivatedOrWatchOnlyEOSAccount(): Boolean {
		return isCurrent() && name.equals(SharedAddress.getCurrentEOSAccount().name, true)
	}

	override fun getObject(): String {
		return "{\"name\":\"$name\",\"chainID\":\"$chainID\",\"publicKey\":\"$publicKey\"}"
	}
}

fun List<EOSAccountInfo>.currentPublicKeyIsActivated(): Boolean {
	return !find { it.hasActivated() }.isNull()
}

fun List<EOSAccountInfo>.hasActivatedOrWatchOnly(): Boolean {
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