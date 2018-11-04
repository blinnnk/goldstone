package io.goldstone.blockchain.common.language

/**
 * @date 2018/8/8 2:25 AM
 * @author KaySaith
 */

object LoadingText {
	@JvmField
	val gettingData = when (currentLanguage) {
		HoneyLanguage.English.code -> "Getting Data.."
		HoneyLanguage.Chinese.code -> "Getting Data.."
		HoneyLanguage.Japanese.code -> "Getting Data.."
		HoneyLanguage.Korean.code -> "Getting Data.."
		HoneyLanguage.Russian.code -> "Getting Data.."
		HoneyLanguage.TraditionalChinese.code -> "Getting Data.."
		else -> ""
	}
	@JvmField
	val calculateGas = when (currentLanguage) {
		HoneyLanguage.English.code -> "Estimating gas costs..."
		HoneyLanguage.Chinese.code -> "正在估算燃气费用，马上就好"
		HoneyLanguage.Japanese.code -> "現在ガス代金を計算しています。すぐに終わります"
		HoneyLanguage.Korean.code -> "바로 가스 비용 견적..."
		HoneyLanguage.Russian.code -> "Выполняется оценка стоимости газа..."
		HoneyLanguage.TraditionalChinese.code -> "正在估算燃氣費用，馬上就好"
		else -> ""
	}
	@JvmField
	val calculating = when (currentLanguage) {
		HoneyLanguage.English.code -> "Calculating..."
		HoneyLanguage.Chinese.code -> "正在计算..."
		HoneyLanguage.Japanese.code -> "現在計算中..."
		HoneyLanguage.Korean.code -> "계산 중..."
		HoneyLanguage.Russian.code -> "Расчет..."
		HoneyLanguage.TraditionalChinese.code -> "正在計算..."
		else -> ""
	}

}