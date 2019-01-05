package io.goldstone.blinnnk.crypto.multichain

import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/09/07
 */
enum class PrivateKeyType(val content: String) : Serializable {
	ETHSeries("ETH, ERC20 And ETC"),
	BTCEOSAndBCH("BTC, EOS And BCH"),
	BTC("BTC"),
	BCH("BCH"),
	AllBTCSeriesTest("BTC Test"),
	EOS("EOS"),
	LTC("LTC");
}