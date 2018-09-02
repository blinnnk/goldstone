package io.goldstone.blockchain.common.value

import java.io.Serializable

/**
 * @date 2018/7/19 11:23 AM
 * @author KaySaith
 */
enum class WalletType(val content: String) {

	BTCOnly("btcOnly"),
	ETHERCAndETCOnly("ethERCAndEtc"),
	BTCTestOnly("btctestOnly"),
	LTCOnly("ltcOnly"),
	BCHOnly("bchOnly"),
	EOSOnly("eosOnly"),
	MultiChain("multiChain");

	companion object {
		fun isBTCSeriesType(type: String): Boolean {
			return type == BTCOnly.content ||
				type == BTCTestOnly.content ||
				type == LTCOnly.content ||
				type == BCHOnly.content
		}
	}
}

object DataValue {
	const val pageCount = 50
	const val quotationDataCount = 10
	const val candleChartCount = 100
}

data class PageInfo(val from: Int, val to: Int, val maxDataIndex: Int) : Serializable