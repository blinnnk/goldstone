package io.goldstone.blockchain.common.Language

import io.goldstone.blockchain.common.language.HoneyLanguage
import io.goldstone.blockchain.common.language.currentLanguage

/**
 * @date: 2018/9/27.
 * @author: yanglihai
 * @description: eos 内存交易所所需字段
 */
object EOSRAMExchangeText {
	val currentPrice = when(currentLanguage) {
		HoneyLanguage.English.code -> "current price"
		HoneyLanguage.Chinese.code -> "current price"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	
	val openPrice:(value: String) -> String = { value ->
		when(currentLanguage) {
			HoneyLanguage.English.code -> "open price:$value"
			HoneyLanguage.Chinese.code -> "open price:$value"
			HoneyLanguage.Japanese.code -> ""
			HoneyLanguage.Korean.code -> ""
			HoneyLanguage.Russian.code -> ""
			HoneyLanguage.TraditionalChinese.code -> ""
			else -> ""
		}
	}
	
	val highPrice:(value: String) -> String = { value ->
		when(currentLanguage) {
			HoneyLanguage.English.code -> "high price:$value"
			HoneyLanguage.Chinese.code -> "high price:$value"
			HoneyLanguage.Japanese.code -> ""
			HoneyLanguage.Korean.code -> ""
			HoneyLanguage.Russian.code -> ""
			HoneyLanguage.TraditionalChinese.code -> ""
			else -> ""
		}
	}
	
	val lowPrice:(value: String) -> String = { value ->
		when(currentLanguage) {
			HoneyLanguage.English.code -> "low price:$value"
			HoneyLanguage.Chinese.code -> "low price:$value"
			HoneyLanguage.Japanese.code -> ""
			HoneyLanguage.Korean.code -> ""
			HoneyLanguage.Russian.code -> ""
			HoneyLanguage.TraditionalChinese.code -> ""
			else -> ""
		}
	}
	
	val ramOccupyRate = when(currentLanguage) {
		HoneyLanguage.English.code -> " RAM occupy rate"
		HoneyLanguage.Chinese.code -> "RAM occupy rate"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	
	val ramOccupyAmount:(value: String) -> String = { value ->
		when(currentLanguage) {
			HoneyLanguage.English.code -> "ram occupy amount ${value}GB"
			HoneyLanguage.Chinese.code -> "ram occupy amount ${value}GB"
			HoneyLanguage.Japanese.code -> ""
			HoneyLanguage.Korean.code -> ""
			HoneyLanguage.Russian.code -> ""
			HoneyLanguage.TraditionalChinese.code -> ""
			else -> ""
		}
	}
	
	val ramTotalAmount:(value: String) -> String = { value ->
		when(currentLanguage) {
			HoneyLanguage.English.code -> "ram total amount ${value}GB"
			HoneyLanguage.Chinese.code -> "ram total amount ${value}GB"
			HoneyLanguage.Japanese.code -> ""
			HoneyLanguage.Korean.code -> ""
			HoneyLanguage.Russian.code -> ""
			HoneyLanguage.TraditionalChinese.code -> ""
			else -> ""
		}
	}
	
	val bigOrder = when(currentLanguage) {
		HoneyLanguage.English.code -> "big order"
		HoneyLanguage.Chinese.code -> "big order"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	
	val middleOrder = when(currentLanguage) {
		HoneyLanguage.English.code -> "middle order"
		HoneyLanguage.Chinese.code -> "middle order"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	
	val smallOrder = when(currentLanguage) {
		HoneyLanguage.English.code -> "small order"
		HoneyLanguage.Chinese.code -> "small order"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	
	val buy: (value: String) -> String = { value ->
		when(currentLanguage) {
			HoneyLanguage.English.code -> "buy$value"
			HoneyLanguage.Chinese.code -> "buy$value"
			HoneyLanguage.Japanese.code -> ""
			HoneyLanguage.Korean.code -> ""
			HoneyLanguage.Russian.code -> ""
			HoneyLanguage.TraditionalChinese.code -> ""
			else -> ""
		}
	}
	
	val sall: (value: String) -> String = { value ->
		when(currentLanguage) {
			HoneyLanguage.English.code -> ""
			HoneyLanguage.Chinese.code -> "卖出$value"
			HoneyLanguage.Japanese.code -> ""
			HoneyLanguage.Korean.code -> ""
			HoneyLanguage.Russian.code -> ""
			HoneyLanguage.TraditionalChinese.code -> ""
			else -> ""
		}
	}
	
	val ramOrderRules = when(currentLanguage) {
		HoneyLanguage.English.code -> "big order >2000EOS  middle order 500~2000EOS small order <500EOS"
		HoneyLanguage.Chinese.code -> "big order >2000EOS  middle order 500~2000EOS small order <500EOS"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	
	val transactionDistribute = when(currentLanguage) {
		HoneyLanguage.English.code -> "transaction distribute"
		HoneyLanguage.Chinese.code -> "transaction distribute"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	
	val ramExchange = when(currentLanguage) {
		HoneyLanguage.English.code -> "ram exchange"
		HoneyLanguage.Chinese.code -> "ram exchange"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	
}