package io.goldstone.blockchain.common.Language

import io.goldstone.blockchain.common.language.HoneyLanguage
import io.goldstone.blockchain.common.language.currentLanguage

/**
 * @date: 2018/9/27.
 * @author: yanglihai
 * @description:
 */
object EOSRAMText {
	
	val currentPrice = when(currentLanguage) {
		HoneyLanguage.English.code -> ""
		HoneyLanguage.Chinese.code -> "当前价"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	
	val openPrice:(value: String) -> String = { value ->
		when(currentLanguage) {
			HoneyLanguage.English.code -> ""
			HoneyLanguage.Chinese.code -> "开盘价：$value"
			HoneyLanguage.Japanese.code -> ""
			HoneyLanguage.Korean.code -> ""
			HoneyLanguage.Russian.code -> ""
			HoneyLanguage.TraditionalChinese.code -> ""
			else -> ""
		}
	}
	
	val highPrice:(value: String) -> String = { value ->
		when(currentLanguage) {
			HoneyLanguage.English.code -> ""
			HoneyLanguage.Chinese.code -> "最高：$value"
			HoneyLanguage.Japanese.code -> ""
			HoneyLanguage.Korean.code -> ""
			HoneyLanguage.Russian.code -> ""
			HoneyLanguage.TraditionalChinese.code -> ""
			else -> ""
		}
	}
	
	val lowPrice:(value: String) -> String = { value ->
		when(currentLanguage) {
			HoneyLanguage.English.code -> ""
			HoneyLanguage.Chinese.code -> "最低：$value"
			HoneyLanguage.Japanese.code -> ""
			HoneyLanguage.Korean.code -> ""
			HoneyLanguage.Russian.code -> ""
			HoneyLanguage.TraditionalChinese.code -> ""
			else -> ""
		}
	}
	
	val ramUtilization = when(currentLanguage) {
		HoneyLanguage.English.code -> ""
		HoneyLanguage.Chinese.code -> "内存占用率"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	
	val ramAccupyAmount:(value: String) -> String = { value ->
		when(currentLanguage) {
			HoneyLanguage.English.code -> ""
			HoneyLanguage.Chinese.code -> "占用${value}GB"
			HoneyLanguage.Japanese.code -> ""
			HoneyLanguage.Korean.code -> ""
			HoneyLanguage.Russian.code -> ""
			HoneyLanguage.TraditionalChinese.code -> ""
			else -> ""
		}
	}
	
	val ramTotalAmount:(value: String) -> String = { value ->
		when(currentLanguage) {
			HoneyLanguage.English.code -> ""
			HoneyLanguage.Chinese.code -> "总量${value}GB"
			HoneyLanguage.Japanese.code -> ""
			HoneyLanguage.Korean.code -> ""
			HoneyLanguage.Russian.code -> ""
			HoneyLanguage.TraditionalChinese.code -> ""
			else -> ""
		}
	}
	
	val bigOrder = when(currentLanguage) {
		HoneyLanguage.English.code -> ""
		HoneyLanguage.Chinese.code -> "大单"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	
	val middleOrder = when(currentLanguage) {
		HoneyLanguage.English.code -> ""
		HoneyLanguage.Chinese.code -> "中单"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	
	val smallOrder = when(currentLanguage) {
		HoneyLanguage.English.code -> ""
		HoneyLanguage.Chinese.code -> "小单"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	
	val buying:(value: String) -> String = { value ->
		when(currentLanguage) {
			HoneyLanguage.English.code -> ""
			HoneyLanguage.Chinese.code -> "买入$value"
			HoneyLanguage.Japanese.code -> ""
			HoneyLanguage.Korean.code -> ""
			HoneyLanguage.Russian.code -> ""
			HoneyLanguage.TraditionalChinese.code -> ""
			else -> ""
		}
	}
	
	val saling:(value: String) -> String = { value ->
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
		HoneyLanguage.English.code -> ""
		HoneyLanguage.Chinese.code -> "大单 >2000EOS  中单 500~2000EOS 小单 <500EOS"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	
	val tradeDistribute = when(currentLanguage) {
		HoneyLanguage.English.code -> ""
		HoneyLanguage.Chinese.code -> "成交分布"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	
	val ramTradeRoom = when(currentLanguage) {
		HoneyLanguage.English.code -> ""
		HoneyLanguage.Chinese.code -> "内存交易所"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	
}