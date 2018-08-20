package io.goldstone.blockchain.common.value

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