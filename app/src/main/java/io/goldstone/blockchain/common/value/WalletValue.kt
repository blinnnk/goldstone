package io.goldstone.blockchain.common.value

import java.io.Serializable

/**
 * @date 2018/7/19 11:23 AM
 * @author KaySaith
 */
object DataValue {
	const val pageCount = 100
	const val quotationDataCount = 10
	const val candleChartCount = 100
}

data class PageInfo(val from: Int, val to: Int, val maxDataIndex: Int) : Serializable