package io.goldstone.blockchain.crypto.multichain

import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/09/17
 */
data class ChainPath(
	val ethPath: String,
	val etcPath: String,
	val btcPath: String,
	val testPath: String,
	val ltcPath: String,
	val bchPath: String,
	val eosPath: String
) : Serializable {

	constructor() : this(
		"",
		"",
		"",
		"",
		"",
		"",
		""
	)

	companion object {
		@JvmStatic
		val pathToChainType: (path: String) -> Int = {
			it.replace("'", "").split("/")[2].toInt()
		}
	}
}

object DefaultPath {
	// Path
	const val ethPath = "m/44'/60'/0'/0/0"
	const val etcPath = "m/44'/61'/0'/0/0"
	const val btcPath = "m/44'/0'/0'/0/0"
	const val testPath = "m/44'/1'/0'/0/0"
	const val ltcPath = "m/44'/2'/0'/0/0"
	const val bchPath = "m/44'/145'/0'/0/0"
	const val eosPath = "m/44'/194'/0'/0/0"
	val allPaths: () -> List<String> = {
		listOf(
			ethPath,
			etcPath,
			btcPath,
			testPath,
			ltcPath,
			bchPath,
			eosPath
		)
	}
	// Header Value
	const val ethPathHeader = "m/44'/60'/"
	const val etcPathHeader = "m/44'/61'/"
	const val btcPathHeader = "m/44'/0'/"
	const val testPathHeader = "m/44'/1'/"
	const val ltcPathHeader = "m/44'/2'/"
	const val bchPathHeader = "m/44'/145'/"
	const val eosPathHeader = "m/44'/194'/"
	const val default = "0'/0/0"
}