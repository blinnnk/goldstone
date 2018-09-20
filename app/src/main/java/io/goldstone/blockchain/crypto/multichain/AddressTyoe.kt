package io.goldstone.blockchain.crypto.multichain


/**
 * @author KaySaith
 * @date  2018/09/18
 */

enum class AddressType(val value: String, val symbol: String) {
	ETHSeries("ethERCOrETC", CoinSymbol.eth),
	BTC("btc", CoinSymbol.pureBTCSymbol),
	BCH("bch", CoinSymbol.bch),
	BTCSeriesTest("btcTest", CoinSymbol.pureBTCSymbol),
	LTC("ltc", CoinSymbol.ltc),
	EOS("eos", CoinSymbol.eos),
	EOSAccountName("eosAccountName", CoinSymbol.eos),
}