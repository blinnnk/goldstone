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
			HoneyLanguage.English.code -> "RAM occupy amount ${value}GB"
			HoneyLanguage.Chinese.code -> "RAM occupy amount ${value}GB"
			HoneyLanguage.Japanese.code -> ""
			HoneyLanguage.Korean.code -> ""
			HoneyLanguage.Russian.code -> ""
			HoneyLanguage.TraditionalChinese.code -> ""
			else -> ""
		}
	}
	
	val ramTotalAmount:(value: String) -> String = { value ->
		when(currentLanguage) {
			HoneyLanguage.English.code -> "RAM total amount ${value}GB"
			HoneyLanguage.Chinese.code -> "RAM total amount ${value}GB"
			HoneyLanguage.Japanese.code -> ""
			HoneyLanguage.Korean.code -> ""
			HoneyLanguage.Russian.code -> ""
			HoneyLanguage.TraditionalChinese.code -> ""
			else -> ""
		}
	}
	
	val bigOrder = when(currentLanguage) {
		HoneyLanguage.English.code -> "big"
		HoneyLanguage.Chinese.code -> "big"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	
	val middleOrder = when(currentLanguage) {
		HoneyLanguage.English.code -> "middle"
		HoneyLanguage.Chinese.code -> "middle"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	
	val smallOrder = when(currentLanguage) {
		HoneyLanguage.English.code -> "small"
		HoneyLanguage.Chinese.code -> "small"
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
	
	val sell: (value: String) -> String = { value ->
		when(currentLanguage) {
			HoneyLanguage.English.code -> "sell$value"
			HoneyLanguage.Chinese.code -> "sell$value"
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
		HoneyLanguage.English.code -> "RAM exchange"
		HoneyLanguage.Chinese.code -> "RAM exchange"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	
	val ram = when(currentLanguage) {
		HoneyLanguage.English.code -> "RAM"
		HoneyLanguage.Chinese.code -> "内存"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	
	val eos = when(currentLanguage) {
			HoneyLanguage.English.code -> "EOS"
		HoneyLanguage.Chinese.code -> "EOS"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	
	val enterCountHint = when(currentLanguage) {
		HoneyLanguage.English.code -> "please input your count"
		HoneyLanguage.Chinese.code -> "please input your count"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	val transactionHistory = when(currentLanguage) {
		HoneyLanguage.English.code -> "transaction history"
		HoneyLanguage.Chinese.code -> "transaction history"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	val ramBalanceDescription: (count: String) -> String = {
		when(currentLanguage) {
			HoneyLanguage.English.code -> "balance $it KB"
			HoneyLanguage.Chinese.code -> "balance $it KB"
			HoneyLanguage.Japanese.code -> ""
			HoneyLanguage.Korean.code -> ""
			HoneyLanguage.Russian.code -> ""
			HoneyLanguage.TraditionalChinese.code -> ""
			else -> ""
		}
	}
	val eosBalanceDescription: (count: String) -> String = {
		when(currentLanguage) {
			HoneyLanguage.English.code -> "balance $it EOS"
			HoneyLanguage.Chinese.code -> "balance $it EOS"
			HoneyLanguage.Japanese.code -> ""
			HoneyLanguage.Korean.code -> ""
			HoneyLanguage.Russian.code -> ""
			HoneyLanguage.TraditionalChinese.code -> ""
			else -> ""
		}
	}
	
	val confirmToTrade = when(currentLanguage) {
		HoneyLanguage.English.code -> "confirm"
		HoneyLanguage.Chinese.code -> "confirm"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	
	val transactionAccount = when(currentLanguage) {
		HoneyLanguage.English.code -> "account/timing"
		HoneyLanguage.Chinese.code -> "account/timing"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	
	val transactionAmount =  when(currentLanguage) {
		HoneyLanguage.English.code -> "transactionAmount"
		HoneyLanguage.Chinese.code -> "transactionAmount"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	
	val totalRAM: (amount: String) -> String = {
		when(currentLanguage) {
			HoneyLanguage.English.code -> "$it TOTAL"
			HoneyLanguage.Chinese.code -> "$it total"
			HoneyLanguage.Japanese.code -> ""
			HoneyLanguage.Korean.code -> ""
			HoneyLanguage.Russian.code -> ""
			HoneyLanguage.TraditionalChinese.code -> ""
			else -> ""
		}
	}
	
	val ramAvailable: (amount: String) -> String = {
		when(currentLanguage) {
			HoneyLanguage.English.code -> "$it AVAILABLE"
			HoneyLanguage.Chinese.code -> "$it available"
			HoneyLanguage.Japanese.code -> ""
			HoneyLanguage.Korean.code -> ""
			HoneyLanguage.Russian.code -> ""
			HoneyLanguage.TraditionalChinese.code -> ""
			else -> ""
		}
	}
	
	val chainRAMBalance = when(currentLanguage) {
		HoneyLanguage.English.code -> "chain RAM balance"
		HoneyLanguage.Chinese.code -> "chain RAM balance"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	val chainRAMTotal = when(currentLanguage) {
		HoneyLanguage.English.code -> "chain RAM total"
		HoneyLanguage.Chinese.code -> "chain RAM total"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	
	
	val eosForRAM = when(currentLanguage) {
		HoneyLanguage.English.code -> "EOS for RAM"
		HoneyLanguage.Chinese.code -> "EOS for RAM"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	
	val chainRAMData = when(currentLanguage) {
		HoneyLanguage.English.code -> "chain RAM data"
		HoneyLanguage.Chinese.code -> "chain RAM data"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	
	val bigTransactions = when(currentLanguage) {
		HoneyLanguage.English.code -> "big transactions"
		HoneyLanguage.Chinese.code -> "big transactions"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	val occupyBig = when(currentLanguage) {
		HoneyLanguage.English.code -> "occupy big"
		HoneyLanguage.Chinese.code -> "occupy big"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	val ramStatistics = when(currentLanguage) {
		HoneyLanguage.English.code -> "ram statistics"
		HoneyLanguage.Chinese.code -> "ram statistics"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	val tradeStruct = when(currentLanguage) {
		HoneyLanguage.English.code -> "trade struct"
		HoneyLanguage.Chinese.code -> "trade struct"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	
	val timing = when(currentLanguage) {
		HoneyLanguage.English.code -> "timing"
		HoneyLanguage.Chinese.code -> "timing"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	
	val user = when(currentLanguage) {
		HoneyLanguage.English.code -> "user"
		HoneyLanguage.Chinese.code -> "user"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	
	val tradingAmount = when(currentLanguage) {
		HoneyLanguage.English.code -> "trading amount"
		HoneyLanguage.Chinese.code -> "trading amount"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	
}