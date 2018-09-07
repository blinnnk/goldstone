package io.goldstone.blockchain.crypto.multichain


/**
 * @author KaySaith
 * @date  2018/09/07
 */
enum class PrivateKeyType(val content: String) {
	ETHERCAndETC("ETH, ERC20 And ETC"),
	BTCEOSAndBCH("BTC, EOS And BCH"),
	BTC("BTC"),
	BCH("BCH"),
	AllBTCSeriesTest("BTC Test"),
	EOS("EOS"),
	LTC("LTC");

	companion object {
		fun getTypeByContent(content: String): PrivateKeyType {
			return when (content) {
				ETHERCAndETC.content -> ETHERCAndETC
				LTC.content -> LTC
				BCH.content -> BCH
				BTC.content -> BTC
				EOS.content -> EOS
				BTCEOSAndBCH.content -> BTCEOSAndBCH
				else -> AllBTCSeriesTest
			}
		}
	}
}