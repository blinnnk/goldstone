package io.goldstone.blockchain.crypto.bitcoin

/**
 * @date 2018/8/6 1:57 AM
 * @author KaySaith
 */
data class MultiChainPath(
	val ethPath: String,
	val etcPath: String,
	val btcPath: String,
	val testPath: String,
	val ltcPath: String,
	val bchPath: String
) {
	
	constructor() : this(
		"",
		"",
		"",
		"",
		"",
		""
	)
}

data class MultiChainAddresses(
	val ethAddress: String,
	val etcAddress: String,
	val btcAddress: String,
	val btcSeriesTestAddress: String,
	val ltcAddress: String,
	val bchAddress: String
) {
	
	constructor() : this(
		"",
		"",
		"",
		"",
		"",
		""
	)
}