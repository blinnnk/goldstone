package io.goldstone.blockchain.common.sandbox

import com.blinnnk.extension.safeGet
import com.google.gson.Gson
import com.google.gson.InstanceCreator
import com.google.gson.reflect.TypeToken
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.Type

/**
 * @date: 2018-12-24.
 * @author: yangLiHai
 * @description:
 */
class WalletModel(
	val id: Int,
	val avatarID: Int,
	val name: String,
	val ethPath: String,
	val etcPath: String,
	val btcPath: String,
	val btcTestPath: String,
	val ltcPath: String,
	val bchPath: String,
	val eosPath: String,
	val hint: String? = null,
	val isWatchOnly: Boolean = false,
	val encryptMnemonic: String? = null,
	val encryptFingerPrinterKey: String? = null,
	val currentEOSAccountName: EOSDefaultAllChainName,
	val hasBackUpMnemonic: Boolean,
	var currentETHSeriesAddress: String = "",
	var currentETCAddress: String = "",
	var currentBTCAddress: String = "",
	var currentBTCSeriesTestAddress: String = "",
	var currentLTCAddress: String = "",
	var currentBCHAddress: String = "",
	var currentEOSAddress: String = ""
) {
	constructor(jsonObject: JSONObject): this(
		jsonObject.safeGet("id").toInt(),
		jsonObject.safeGet("avatarID").toInt(),
		jsonObject.safeGet("name"),
		jsonObject.safeGet("ethPath"),
		jsonObject.safeGet("etcPath"),
		jsonObject.safeGet("btcPath"),
		jsonObject.safeGet("btcTestPath"),
		jsonObject.safeGet("ltcPath"),
		jsonObject.safeGet("bchPath"),
		jsonObject.safeGet("eosPath"),
		jsonObject.safeGet("hint"),
		jsonObject.safeGet("isWatchOnly").toBoolean(),
		jsonObject.safeGet("encryptMnemonic"),
		jsonObject.safeGet("encryptFingerPrinterKey"),
		currentEOSAccountName = Gson().fromJson(jsonObject.safeGet("currentEOSAccountName"), object : TypeToken<EOSDefaultAllChainName>() {}.type),
		hasBackUpMnemonic = jsonObject.safeGet("hasBackUpMnemonic").toBoolean(),
		currentETHSeriesAddress = jsonObject.safeGet("currentETHSeriesAddress"),
		currentETCAddress = jsonObject.safeGet("currentETCAddress"),
		currentBTCAddress = jsonObject.safeGet("currentBTCAddress"),
		currentBTCSeriesTestAddress = jsonObject.safeGet("currentBTCSeriesTestAddress"),
		currentLTCAddress = jsonObject.safeGet("currentLTCAddress"),
		currentBCHAddress = jsonObject.safeGet("currentBCHAddress"),
		currentEOSAddress = jsonObject.safeGet("currentEOSAddress")
		
	)
	constructor(walletTable: WalletTable): this(
		walletTable.id,
		walletTable.avatarID,
		walletTable.name,
		walletTable.ethPath,
		walletTable.etcPath,
		walletTable.btcPath,
		walletTable.btcTestPath,
		walletTable.ltcPath,
		walletTable.bchPath,
		walletTable.eosPath,
		walletTable.hint,
		walletTable.isWatchOnly,
		walletTable.encryptMnemonic,
		walletTable.encryptFingerPrinterKey,
		walletTable.currentEOSAccountName,
		walletTable.hasBackUpMnemonic,
		currentETHSeriesAddress = walletTable.currentETHSeriesAddress,
		currentETCAddress = walletTable.currentETCAddress,
		currentBTCAddress = walletTable.currentBTCAddress,
		currentLTCAddress = walletTable.currentLTCAddress,
		currentBCHAddress = walletTable.currentBCHAddress,
		currentEOSAddress = walletTable.currentEOSAddress
	)
}
