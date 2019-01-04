package io.goldstone.blockchain.crypto.multichain


/**
 * @author KaySaith
 * @date  2018/09/18
 */

enum class AddressType(val value: String, val symbol: String) {
	ETHSeries("ETH/ERC20/ETC", CoinSymbol.eth),
	BTC(CoinSymbol.btc, CoinSymbol.btc),
	BCH("BCH", CoinSymbol.bch),
	BTCSeriesTest("${CoinSymbol.btc} Test", CoinSymbol.btc),
	LTC("LTC", CoinSymbol.ltc),
	EOS("EOS", CoinSymbol.eos),
	EOSJungle("EOS Jungle", CoinSymbol.eos),
	EOSKylin("EOS Kylin", CoinSymbol.eos),
	EOSAccountName("EOS Account Name", CoinSymbol.eos)
}