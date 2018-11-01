package io.goldstone.blockchain.common.language

/**
 * @date 2018/8/8 2:26 AM
 * @author KaySaith
 */

object QuotationText {
	@JvmField
	val market = when (currentLanguage) {
		HoneyLanguage.English.code -> "Markets"
		HoneyLanguage.Chinese.code -> "市场行情"
		HoneyLanguage.Japanese.code -> "市場状況"
		HoneyLanguage.Korean.code -> "시장시세"
		HoneyLanguage.Russian.code -> "РЫНКИ"
		HoneyLanguage.TraditionalChinese.code -> "市場行情"
		else -> ""
	}
	@JvmField
	val emptyDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "There isn't description of this token"
		HoneyLanguage.Chinese.code -> "这个Token没有简介"
		HoneyLanguage.Japanese.code -> "このTokenには概略がありません"
		HoneyLanguage.Korean.code -> "이 토큰에 대한 설명이 없습니다."
		HoneyLanguage.Russian.code -> "Данный Token не имеет описания"
		HoneyLanguage.TraditionalChinese.code -> "這個Token沒有簡介"
		else -> ""
	}
	@JvmField
	val management = when (currentLanguage) {
		HoneyLanguage.English.code -> "My Markets"
		HoneyLanguage.Chinese.code -> "自选管理"
		HoneyLanguage.Japanese.code -> "マーケット"
		HoneyLanguage.Korean.code -> "셀프관리"
		HoneyLanguage.Russian.code -> "Самостоятельное управление"
		HoneyLanguage.TraditionalChinese.code -> "自選管理"
		else -> ""
	}
	@JvmField
	val tokenDetail = when (currentLanguage) {
		HoneyLanguage.English.code -> "Market Token"
		HoneyLanguage.Chinese.code -> "市场交易对"
		HoneyLanguage.Japanese.code -> "マーケットの取引ペア"
		HoneyLanguage.Korean.code -> "시장 거래 쌍"
		HoneyLanguage.Russian.code -> "Пара рыночных операций"
		HoneyLanguage.TraditionalChinese.code -> "市場交易對"
		else -> ""
	}
	@JvmField
	val search = when (currentLanguage) {
		HoneyLanguage.English.code -> "Search pairs"
		HoneyLanguage.Chinese.code -> "搜索"
		HoneyLanguage.Japanese.code -> "検索"
		HoneyLanguage.Korean.code -> "검색"
		HoneyLanguage.Russian.code -> "Поиск"
		HoneyLanguage.TraditionalChinese.code -> "搜索"
		else -> ""
	}
	@JvmField
	val addToken = when (currentLanguage) {
		HoneyLanguage.English.code -> "Manage"
		HoneyLanguage.Chinese.code -> "管理"
		HoneyLanguage.Japanese.code -> "管理"
		HoneyLanguage.Korean.code -> "검색"
		HoneyLanguage.Russian.code -> "Управление"
		HoneyLanguage.TraditionalChinese.code -> "管理"
		else -> ""
	}

	@JvmField
	val quotationInfo = when (currentLanguage) {
		HoneyLanguage.English.code -> "Quotation Info"
		HoneyLanguage.Chinese.code -> "Quotation Info"
		HoneyLanguage.Japanese.code -> "Quotation Info"
		HoneyLanguage.Korean.code -> "Quotation Info"
		HoneyLanguage.Russian.code -> "Quotation Info"
		HoneyLanguage.TraditionalChinese.code -> "Quotation Info"
		else -> ""
	}

	@JvmField
	val currentPrice = when (currentLanguage) {
		HoneyLanguage.English.code -> "Current Price"
		HoneyLanguage.Chinese.code -> "当前价格"
		HoneyLanguage.Japanese.code -> "現在の価格"
		HoneyLanguage.Korean.code -> "현재가격"
		HoneyLanguage.Russian.code -> "Текущая цена"
		HoneyLanguage.TraditionalChinese.code -> "時價"
		else -> ""
	}
	@JvmField
	val priceHistory = when (currentLanguage) {
		HoneyLanguage.English.code -> "Price History"
		HoneyLanguage.Chinese.code -> "价格历史"
		HoneyLanguage.Japanese.code -> "価格履歴"
		HoneyLanguage.Korean.code -> "가격역사"
		HoneyLanguage.Russian.code -> "История цены"
		HoneyLanguage.TraditionalChinese.code -> "價格歷史"
		else -> ""
	}
	@JvmField
	val tokenDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "Token Description"
		HoneyLanguage.Chinese.code -> "Token 简介"
		HoneyLanguage.Japanese.code -> "Token概略"
		HoneyLanguage.Korean.code -> "Token 소개"
		HoneyLanguage.Russian.code -> "Описание Token"
		HoneyLanguage.TraditionalChinese.code -> "Token 簡介"
		else -> ""
	}
	@JvmField
	val exchangeList = when (currentLanguage) {
		HoneyLanguage.English.code -> "Select An Exchange"
		HoneyLanguage.Chinese.code -> "Select An Exchange"
		HoneyLanguage.Japanese.code -> "Select An Exchange"
		HoneyLanguage.Korean.code -> "Select An Exchange"
		HoneyLanguage.Russian.code -> "Select An Exchange"
		HoneyLanguage.TraditionalChinese.code -> "Select An Exchange"
		else -> ""
	}
	@JvmField
	val tokenInformation = when (currentLanguage) {
		HoneyLanguage.English.code -> "Token Information"
		HoneyLanguage.Chinese.code -> "Token 信息"
		HoneyLanguage.Japanese.code -> "Token情報"
		HoneyLanguage.Korean.code -> "Token 정보"
		HoneyLanguage.Russian.code -> "Информация Token"
		HoneyLanguage.TraditionalChinese.code -> "Token 信息"
		else -> ""
	}
	@JvmField
	val tokenInfoLink = when (currentLanguage) {
		HoneyLanguage.English.code -> "Token Link"
		HoneyLanguage.Chinese.code -> "Token 链接"
		HoneyLanguage.Japanese.code -> "Tokenリンク"
		HoneyLanguage.Korean.code -> "토큰 링크"
		HoneyLanguage.Russian.code -> "Ссылка Token"
		HoneyLanguage.TraditionalChinese.code -> "Token 鏈接"
		else -> ""
	}
	@JvmField
	val socimalMedia = when (currentLanguage) {
		HoneyLanguage.English.code -> "Social Media"
		HoneyLanguage.Chinese.code -> "社交媒体"
		HoneyLanguage.Japanese.code -> "ソーシャルメディア"
		HoneyLanguage.Korean.code -> "소셜 미디어"
		HoneyLanguage.Russian.code -> "Социальные медиа"
		HoneyLanguage.TraditionalChinese.code -> "社交媒體"
		else -> ""
	}
	@JvmField
	val tokenDescriptionPlaceHolder = when (currentLanguage) {
		HoneyLanguage.English.code -> "Token Description Content"
		HoneyLanguage.Chinese.code -> "Token 信息内容"
		HoneyLanguage.Japanese.code -> "Token情報内容"
		HoneyLanguage.Korean.code -> "Token 정보내용"
		HoneyLanguage.Russian.code -> "Содержание информации Token"
		HoneyLanguage.TraditionalChinese.code -> "Token 信息內容"
		else -> ""
	}
	@JvmField
	val highAndLow = when (currentLanguage) {
		HoneyLanguage.English.code -> "HIGH / LOW"
		HoneyLanguage.Chinese.code -> "最高/最低"
		HoneyLanguage.Japanese.code -> "最高/最低"
		HoneyLanguage.Korean.code -> "최고 / 최저"
		HoneyLanguage.Russian.code -> "Макс./мин."
		HoneyLanguage.TraditionalChinese.code -> "最高 / 最低"
		else -> ""
	}
	@JvmField
	val rank = when (currentLanguage) {
		HoneyLanguage.English.code -> "Rank"
		HoneyLanguage.Chinese.code -> "排名"
		HoneyLanguage.Japanese.code -> "ランキング"
		HoneyLanguage.Korean.code -> "순위"
		HoneyLanguage.Russian.code -> "Рейтинг"
		HoneyLanguage.TraditionalChinese.code -> "排名"
		else -> ""
	}
	@JvmField
	val website = when (currentLanguage) {
		HoneyLanguage.English.code -> "Website"
		HoneyLanguage.Chinese.code -> "网站"
		HoneyLanguage.Japanese.code -> "ウェブサイト"
		HoneyLanguage.Korean.code -> "웹 사이트"
		HoneyLanguage.Russian.code -> "Веб-сайт"
		HoneyLanguage.TraditionalChinese.code -> "網站"
		else -> ""
	}
	@JvmField
	val whitePaper = when (currentLanguage) {
		HoneyLanguage.English.code -> "White Paper"
		HoneyLanguage.Chinese.code -> "白皮书"
		HoneyLanguage.Japanese.code -> "ホワイトペーパー"
		HoneyLanguage.Korean.code -> "White Paper"
		HoneyLanguage.Russian.code -> "Белая книга"
		HoneyLanguage.TraditionalChinese.code -> "White Paper"
		else -> ""
	}
	@JvmField
	val startDate = when (currentLanguage) {
		HoneyLanguage.English.code -> "Start Date"
		HoneyLanguage.Chinese.code -> "开始日期"
		HoneyLanguage.Japanese.code -> "開始日"
		HoneyLanguage.Korean.code -> "시작 날짜"
		HoneyLanguage.Russian.code -> "Дата начала"
		HoneyLanguage.TraditionalChinese.code -> "開始日期"
		else -> ""
	}
	@JvmField
	val totalSupply = when (currentLanguage) {
		HoneyLanguage.English.code -> "Total Supply"
		HoneyLanguage.Chinese.code -> "总供给量"
		HoneyLanguage.Japanese.code -> "総合サプライ"
		HoneyLanguage.Korean.code -> "총 공급량"
		HoneyLanguage.Russian.code -> "Общее предложение"
		HoneyLanguage.TraditionalChinese.code -> "總供給量"
		else -> ""
	}
	@JvmField
	val marketCap = when (currentLanguage) {
		HoneyLanguage.English.code -> "Market Cap"
		HoneyLanguage.Chinese.code -> "市值"
		HoneyLanguage.Japanese.code -> "時価総額"
		HoneyLanguage.Korean.code -> "시가 총액"
		HoneyLanguage.Russian.code -> "Рыночная капитализация"
		HoneyLanguage.TraditionalChinese.code -> "市值"
		else -> ""
	}
	@JvmField
	val addQuotationChartPlaceholderTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "ADD QUOTATION CHART"
		HoneyLanguage.Chinese.code -> "添加你感兴趣的Token行情"
		HoneyLanguage.Japanese.code -> "カスタム見積もりを追加する"
		HoneyLanguage.Korean.code -> "관심있는 Token 따옴표 추가"
		HoneyLanguage.Russian.code -> "Добавить рынок"
		HoneyLanguage.TraditionalChinese.code -> "添加你感興趣的Token行情"
		else -> ""
	}
	@JvmField
	val addQuotationChartPlaceholderSubtitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Search and add a real-time\n token pricing chart from\n exchanges."
		HoneyLanguage.Chinese.code -> "从各大市场的交易对中\n搜索并添加你关心的token，\n你可以看到实时价格走势"
		HoneyLanguage.Japanese.code -> "各マーケットの取引の中から\nを検索して、お客様の注目している\nTokenを追加します。"
		HoneyLanguage.Korean.code -> "주요 시장의 거래 쌍에서 관심있는\n 토큰을 검색하고 추가하면 실시간\n 가격 동향을 볼 수 있습니다."
		HoneyLanguage.Russian.code -> "Найдите и добавьте диаграмму\n цен на токены в\n реальном времени из бирж."
		HoneyLanguage.TraditionalChinese.code -> "從各大市場的交易對中\n搜索並添加你關心的token，\n你可以看到實時價格走勢"
		else -> ""
	}
	@JvmField
	val searchExchangesNames: (exchangeNames: String) -> String = {
		when(currentLanguage) {
			HoneyLanguage.English.code -> "$it etc."
			HoneyLanguage.Chinese.code -> "$it etc."
			HoneyLanguage.Japanese.code -> "$it etc."
			HoneyLanguage.Korean.code -> "$it etc."
			HoneyLanguage.Russian.code -> "$it etc."
			HoneyLanguage.TraditionalChinese.code -> "$it etc."
			else -> ""
		}
	}
	@JvmField
	val searchFilterTextDescription: (exchangeNames: String) -> String = {
		when(currentLanguage) {
			HoneyLanguage.English.code -> "Show $it search result"
			HoneyLanguage.Chinese.code -> "Show $it search result"
			HoneyLanguage.Japanese.code -> "Show $it search result"
			HoneyLanguage.Korean.code -> "Show $it search result"
			HoneyLanguage.Russian.code -> "Show $it search result"
			HoneyLanguage.TraditionalChinese.code -> "Show $it search result"
			else -> ""
		}
	}
	@JvmField
	val selectAll = when (currentLanguage) {
		HoneyLanguage.English.code -> "Check All"
		HoneyLanguage.Chinese.code ->  "Check All"
		HoneyLanguage.Japanese.code ->  "Check All"
		HoneyLanguage.Korean.code ->  "Check All"
		HoneyLanguage.Russian.code -> "Check All"
		HoneyLanguage.TraditionalChinese.code ->  "Check All"
		else -> ""
	}
}