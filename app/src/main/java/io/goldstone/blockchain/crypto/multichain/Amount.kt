package io.goldstone.blockchain.crypto.multichain

import io.goldstone.blockchain.common.error.TypeConvertError
import java.math.BigInteger


/**
 * @author KaySaith
 * @date  2018/09/23
 */
class Amount<T>(val value: T) {

	fun toBTC() = convertOrThrow(btcDecimal)
	fun toLTC() = convertOrThrow(ltcDecimal)
	fun toBCH() = convertOrThrow(bchDecimal)
	fun toETC() = convertOrThrow(etcDecimal)
	fun toETH() = convertOrThrow(ethDecimal)
	fun toEOS() = convertOrThrow(eosDecimal)

	val number: () -> BigInteger = {
		when (value) {
			is String -> BigInteger(value)
			is Long -> BigInteger.valueOf(value)
			is Int -> BigInteger.valueOf(value.toLong())
			else -> throw TypeConvertError.AmountToCount
		}
	}

	@Throws
	private fun convertOrThrow(decimal: Int): Double {
		return when (value) {
			is String -> BigInteger(value).toDouble() / Math.pow(10.0, decimal.toDouble())
			is Long -> BigInteger.valueOf(value).toDouble() / Math.pow(10.0, decimal.toDouble())
			is Int -> BigInteger.valueOf(value.toLong()).toDouble() / Math.pow(10.0, decimal.toDouble())
			else -> throw TypeConvertError.AmountToCount
		}
	}

	companion object {
		const val btcDecimal = 8
		const val ltcDecimal = 8
		const val bchDecimal = 8
		const val ethDecimal = 18
		const val eosDecimal = 4
		const val etcDecimal = 18
	}
}