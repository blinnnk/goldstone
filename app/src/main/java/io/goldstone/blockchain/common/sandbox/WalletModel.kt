package io.goldstone.blockchain.common.sandbox

import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.*

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
	val isUsing: Boolean,
	val hint: String? = null,
	val isWatchOnly: Boolean = false,
	val encryptMnemonic: String? = null,
	val encryptFingerPrinterKey: String? = null,
	val currentEOSAccountName: EOSDefaultAllChainName,
	val eosAccountNames: List<EOSAccountInfo>,
	var currentETHSeriesAddress: String = "",
	var currentETCAddress: String = "",
	var currentBTCAddress: String = "",
	var currentBTCSeriesTestAddress: String = "",
	var currentLTCAddress: String = "",
	var currentBCHAddress: String = "",
	var currentEOSAddress: String = ""
) {
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
		walletTable.isUsing,
		walletTable.hint,
		walletTable.isWatchOnly,
		walletTable.encryptMnemonic,
		walletTable.encryptFingerPrinterKey,
		walletTable.currentEOSAccountName,
		walletTable.eosAccountNames
	)
}