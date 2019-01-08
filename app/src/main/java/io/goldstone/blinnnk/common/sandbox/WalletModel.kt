
package io.goldstone.blinnnk.common.sandbox

import com.blinnnk.extension.safeGet
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.goldstone.blinnnk.crypto.eos.EOSWalletUtils
import io.goldstone.blinnnk.crypto.multichain.WalletType
import io.goldstone.blinnnk.module.common.walletgeneration.createwallet.model.*
import org.json.JSONObject

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
	
	fun getWalletType(): WalletType {
		val types = listOf(
			Pair(WalletType.btcOnly, currentBTCAddress),
			Pair(WalletType.btcTestOnly, currentBTCSeriesTestAddress),
			Pair(WalletType.ethSeries, currentETHSeriesAddress),
			Pair(WalletType.ltcOnly, currentLTCAddress),
			Pair(WalletType.bchOnly, currentBCHAddress),
			Pair(WalletType.eosOnly, currentEOSAddress),
			Pair(WalletType.eosMainnetOnly, currentEOSAccountName.main),
			Pair(WalletType.eosJungleOnly, currentEOSAccountName.jungle),
			Pair(WalletType.eosKylinOnly, currentEOSAccountName.kylin)
		).filter {
			it.second.isNotEmpty() && if (it.first == WalletType.eosOnly) EOSWalletUtils.isValidAddress(currentEOSAddress) else true
		}
		return when {
			// 减 `2` 是去除掉 `EOS` 的两个网络状态的计数, 此计数并不影响判断是否是全链钱包
			// 通过私钥导入的多链钱包没有 Path 值所以通过这个来判断是否是
			// BIP44 钱包还是单纯的多链钱包
			types.size > 6 -> if (ethPath.isNotEmpty()) WalletType.BIP44 else WalletType.MultiChain
			else -> WalletType(types.firstOrNull()?.first)
		}
	}
}