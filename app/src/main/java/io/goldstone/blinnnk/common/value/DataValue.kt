package io.goldstone.blinnnk.common.value

import io.goldstone.blinnnk.common.language.CommonText
import io.goldstone.blinnnk.common.language.HoneyLanguage
import io.goldstone.blinnnk.common.language.currentLanguage
import java.io.Serializable
import java.math.BigDecimal

/**
 * @date 2018/7/19 11:23 AM
 * @author KaySaith
 */
object DataValue {
	const val pageCount = 10
	const val quotationDataCount = 10
	const val candleChartCount = 100
	const val dappPageCount = 10
}

object Count {
	const val pinCode = 4
	const val retry = 5
}

object OS {
	const val android = 0
}

data class PageInfo(val from: Int, val to: Int, val maxDataIndex: Int, val total: Int) : Serializable

enum class NumberUnit(val value: BigDecimal, private val chineseSymbol: String, private val englishSymbol: String) {
	Thousand(BigDecimal(Math.pow(10.0, 3.0)), "千", "T"),
	Million(BigDecimal(Math.pow(10.0, 6.0)), "百万", "M"),
	Billion(BigDecimal(Math.pow(10.0, 9.0)), "十亿", "B"),
	TenThousand(BigDecimal(Math.pow(10.0, 4.0)), CommonText.amount10Thousand, "W"),
	HundredMillion(BigDecimal(Math.pow(10.0, 8.0)), CommonText.amount100Million, "Y");

	private fun getUnit(): String {
		return when (currentLanguage) {
			HoneyLanguage.English.code, HoneyLanguage.Russian.code -> this.englishSymbol
			else -> this.chineseSymbol
		}
	}

	fun calculate(volume: BigDecimal): String {
		val result = volume.divide(value, 3, BigDecimal.ROUND_HALF_UP)
		return result.toPlainString() + getUnit()
	}
}

