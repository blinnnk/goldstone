package io.goldstone.blinnnk.common.language

/**
 * @date 2018/8/8 2:25 AM
 * @author KaySaith
 */

object LoadingText {
	@JvmField
	val gettingData = when (currentLanguage) {
		HoneyLanguage.English.code -> "Getting Data Now ..."
		HoneyLanguage.Chinese.code -> "正在获取数据 ..."
		HoneyLanguage.Japanese.code -> "データを取得中 ..."
		HoneyLanguage.Korean.code -> "데이터 가져 오기 ..."
		HoneyLanguage.Russian.code -> "Получение данных ..."
		HoneyLanguage.TraditionalChinese.code -> "正在獲取數據 ..."
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

}