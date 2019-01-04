package io.goldstone.blockchain.crypto.multichain

import com.google.gson.annotations.SerializedName
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/10/26
 */
data class TokenIcon(
	@SerializedName("symbol")
	val symbol: String,
	@SerializedName("icon")
	val url: String,
	@SerializedName("address")
	val contract: String
) : Serializable {
	constructor() : this(
		"",
		"",
		""
	)
}

fun List<TokenIcon>.get(contract: TokenContract): TokenIcon? {
	return find {
		it.symbol.equals(contract.symbol, true) && it.contract.equals(contract.contract, true)
	}
}