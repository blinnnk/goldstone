package io.goldstone.blockchain.crypto.bitcoin

/**
 * @date 2018/8/6 1:57 AM
 * @author KaySaith
 */
data class MultiChainPath(
	val ethPath: String,
	val etcPath: String,
	val btcPath: String,
	val btcTestPath: String
) {
	
	constructor() : this(
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
	val btcTestAddress: String
) {
	
	constructor() : this(
		"",
		"",
		"",
		""
	)
}