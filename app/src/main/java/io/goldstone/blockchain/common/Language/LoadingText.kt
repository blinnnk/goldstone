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
	@JvmField
	val searchingQuotation = when (currentLanguage) {
		HoneyLanguage.English.code -> "Searching token information..."
		HoneyLanguage.Chinese.code -> "正在搜索Token信息..."
		HoneyLanguage.Japanese.code -> "現在Token情報の検索中..."
		HoneyLanguage.Korean.code -> "Token시세 검색..."
		HoneyLanguage.Russian.code -> "Поиск информации о токене..."
		HoneyLanguage.TraditionalChinese.code -> "正在檢索Token信息..."
		else -> ""
	}
	@JvmField
	val searchingToken = when (currentLanguage) {
		HoneyLanguage.English.code -> "Searching token information..."
		HoneyLanguage.Chinese.code -> "正在搜索Token信息..."
		HoneyLanguage.Japanese.code -> "現在Token情報の検索中..."
		HoneyLanguage.Korean.code -> "Token시세 검색..."
		HoneyLanguage.Russian.code -> "Поиск информации о токене..."
		HoneyLanguage.TraditionalChinese.code -> "正在檢索Token信息..."
		else -> ""
	}
	@JvmField
	val transactionData = when (currentLanguage) {
		HoneyLanguage.English.code -> "Loading transaction data..."
		HoneyLanguage.Chinese.code -> "正在加载转账记录..."
		HoneyLanguage.Japanese.code -> "現在振込記録の読み込み中..."
		HoneyLanguage.Korean.code -> "이전 기록 로드중..."
		HoneyLanguage.Russian.code -> "Загрузка данных о переводе..."
		HoneyLanguage.TraditionalChinese.code -> "正在加載轉賬記錄..."
		else -> ""
	}
	@JvmField
	val tokenData = when (currentLanguage) {
		HoneyLanguage.English.code -> "Loading token data..."
		HoneyLanguage.Chinese.code -> "正在加载token信息..."
		HoneyLanguage.Japanese.code -> "現在Token情報の読み込み中..."
		HoneyLanguage.Korean.code -> "Token정보 로드중..."
		HoneyLanguage.Russian.code -> "Загрузка информации о токене..."
		HoneyLanguage.TraditionalChinese.code -> "正在加載token信息..."
		else -> ""
	}
	@JvmField
	val notificationData = when (currentLanguage) {
		HoneyLanguage.English.code -> "Loading notifications..."
		HoneyLanguage.Chinese.code -> "正在加载通知信息..."
		HoneyLanguage.Japanese.code -> "現在アラームメッセージの読み込み中..."
		HoneyLanguage.Korean.code -> "알림 정보 로드중..."
		HoneyLanguage.Russian.code -> "Загрузка информации о уведомлениях..."
		HoneyLanguage.TraditionalChinese.code -> "正在加載通知信息..."
		else -> ""
	}
	@JvmField
	val loadingDataFromChain = when (currentLanguage) {
		HoneyLanguage.English.code -> "Loading data from chain ..."
		HoneyLanguage.Chinese.code -> "正在从链获取信息..."
		HoneyLanguage.Japanese.code -> "現在チェーンから情報の取得中..."
		HoneyLanguage.Korean.code -> "체인에서 정보를 얻는 중 ..."
		HoneyLanguage.Russian.code -> "Загрузка данных из цепи ..."
		HoneyLanguage.TraditionalChinese.code -> "正在從鏈獲取信息..."
		else -> ""
	}
}