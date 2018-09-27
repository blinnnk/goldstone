package io.goldstone.blockchain.crypto.multichain


/**
 * @author KaySaith
 * @date  2018/09/18
 */

enum class AddressType(val value: String, val symbol: String) {
	ETHSeries("ETH/ERC20/ETC", CoinSymbol.eth),
	BTC("BTC", CoinSymbol.pureBTCSymbol),
	BCH("BCH", CoinSymbol.bch),
	BTCSeriesTest("BTC Test", CoinSymbol.pureBTCSymbol),
	LTC("LTC", CoinSymbol.ltc),
	EOS("EOS", CoinSymbol.eos),
	EOSJungle("EOS JUNGLE", CoinSymbol.eos),
	EOSAccountName("EOS Account Name", CoinSymbol.eos)
}