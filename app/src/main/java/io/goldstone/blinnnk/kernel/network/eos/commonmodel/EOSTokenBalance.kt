package io.goldstone.blinnnk.kernel.network.eos.commonmodel

import com.blinnnk.extension.getDecimalCount
import com.google.gson.annotations.SerializedName
import io.goldstone.blinnnk.crypto.multichain.TokenContract
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/11/27
 */
data class EOSTokenBalance(
	@SerializedName("symbol")
	val symbol: String,
	@SerializedName("code")
	val codeName: String,
	@SerializedName("balance")
	val balance: String
) : Serializable {
	fun getDecimal(): Int? = balance.getDecimalCount()
}

infix fun TokenContract.isSameToken(tokenBalance: EOSTokenBalance): Boolean {
	return contract.equals(tokenBalance.codeName, true) &&
		symbol.equals(tokenBalance.symbol, true)
}