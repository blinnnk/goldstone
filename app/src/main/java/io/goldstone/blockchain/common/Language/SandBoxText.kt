package io.goldstone.blockchain.common.Language

import io.goldstone.blockchain.common.language.HoneyLanguage
import io.goldstone.blockchain.common.language.currentLanguage

/**
 * @date: 2018-12-28.
 * @author: yangLiHai
 * @description:
 */
object SandBoxText {
	@JvmField
	val recoveryData = when (currentLanguage) {
		HoneyLanguage.English.code -> "recovery data"
		HoneyLanguage.Chinese.code -> "recovery data"
		HoneyLanguage.Japanese.code -> "recovery data"
		HoneyLanguage.Korean.code -> "recovery data"
		HoneyLanguage.Russian.code -> "recovery data"
		HoneyLanguage.TraditionalChinese.code -> "recovery data"
		else -> ""
	}
	
	@JvmField
	val recoveryDataTip = when (currentLanguage) {
		HoneyLanguage.English.code -> "do you want recovery the data?"
		HoneyLanguage.Chinese.code -> "do you want recovery the data?"
		HoneyLanguage.Japanese.code -> "do you want recovery the data?"
		HoneyLanguage.Korean.code -> "do you want recovery the data?"
		HoneyLanguage.Russian.code -> "do you want recovery the data?"
		HoneyLanguage.TraditionalChinese.code -> "do you want recovery the data?"
		else -> ""
	}
	
	@JvmField
	val walletPasswordInputTip: (wallet: String) -> String = {
		when (currentLanguage) {
			HoneyLanguage.English.code -> ""
			HoneyLanguage.Chinese.code -> "请输入$it 的密码"
			HoneyLanguage.Japanese.code -> ""
			HoneyLanguage.Korean.code -> ""
			HoneyLanguage.Russian.code -> ""
			HoneyLanguage.TraditionalChinese.code -> ""
			else -> ""
		}
	}
	
}