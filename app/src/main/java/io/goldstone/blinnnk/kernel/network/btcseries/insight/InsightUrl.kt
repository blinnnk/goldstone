package io.goldstone.blinnnk.kernel.network.btcseries.insight

import io.goldstone.blinnnk.common.sharedpreference.SharedValue
import io.goldstone.blinnnk.common.value.WebUrl
import io.goldstone.blinnnk.crypto.multichain.*

/**
 * @date 2018/8/13 12:08 PM
 * @author KaySaith
 */

object InsightUrl {

	val getBalance: (chainType: ChainType, address: String) -> String = { type, address ->
		"${getHeader(type)}/addr/$address/balance"
	}

	val getUnspentInfo: (chainType: ChainType, address: String) -> String = { type, address ->
		"${getHeader(type)}/addr/$address/utxo"
	}

	val getTransactions: (chainType: ChainType, address: String, from: Int, to: Int) -> String = { type, address, from, to ->
		"${getHeader(type)}/addrs/$address/txs?from=$from&to=$to"
	}

	val getTransactionByHash: (chaiID: ChainID, hash: String) -> String = { chainID, hash ->
		"${getHeader(chainID)}/tx/$hash"
	}

	private var ltcHeader: () -> String = {
		if (SharedValue.isTestEnvironment()) WebUrl.ltcTestGoldStone else WebUrl.ltcGoldStone
	}

	private var bchHeader: () -> String = {
		if (SharedValue.isTestEnvironment()) WebUrl.bchTest else WebUrl.bchMain
	}

	private var btcHeader: () -> String = {
		if (SharedValue.isTestEnvironment()) WebUrl.btcTestGoldStone else WebUrl.btcGoldStone
	}

	@Throws
	fun getHeader(chainType: ChainType): String {
		return when {
			chainType.isLTC() -> ltcHeader()
			chainType.isBCH() -> bchHeader()
			chainType.isBTC() -> btcHeader()
			else -> throw Throwable("Wrong Insight API Header")
		}
	}

	@Throws
	fun getHeader(chaiID: ChainID): String {
		return when {
			chaiID.isBCHMain() -> WebUrl.bchMain
			chaiID.isBCHTest() -> WebUrl.bchTest
			chaiID.isBTCMain() -> WebUrl.btcGoldStone
			chaiID.isBTCTest() -> WebUrl.btcTestGoldStone
			chaiID.isLTCMain() -> WebUrl.ltcGoldStone
			chaiID.isLTCTest() -> WebUrl.ltcTestGoldStone
			else -> throw Throwable("Wrong Insight API Header")
		}
	}
}