package io.goldstone.blockchain.common.language

/**
 * @date 2018/8/8 2:25 AM
 * @author KaySaith
 */

object EmptyText {
	@JvmField
	val transferToAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter an wallet address or select a contact below"
		HoneyLanguage.Chinese.code -> "输入钱包地址或选择一个联系人"
		HoneyLanguage.Japanese.code -> "ウォレットアドレスを入力するか、連絡先を選んで下さい"
		HoneyLanguage.Korean.code -> "지갑 주소를 입력하거나 아래 연락처를 선택하십시오."
		HoneyLanguage.Russian.code -> "Введите адрес кошелька или выберите контакт"
		HoneyLanguage.TraditionalChinese.code -> "輸入錢包地址或選擇一個聯繫人"
		else -> ""
	}
	@JvmField
	val searchInput = when (currentLanguage) {
		HoneyLanguage.English.code -> "Token name or contract address"
		HoneyLanguage.Chinese.code -> "搜索Token名称或合约地址"
		HoneyLanguage.Japanese.code -> "Token名称もしくは契約アドレスを検索する"
		HoneyLanguage.Korean.code -> "토큰 이름 또는 계약서 주소 검색"
		HoneyLanguage.Russian.code -> "Поиск названия Token или адреса соглашения"
		HoneyLanguage.TraditionalChinese.code -> "搜索Token名稱或合約地址"
		else -> ""
	}
	@JvmField
	val tokenDetailTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "No token transactions found"
		HoneyLanguage.Chinese.code -> "还没有任何交易记录"
		HoneyLanguage.Japanese.code -> "何の取引記録もありません"
		HoneyLanguage.Korean.code -> "임의의 거래기록이 없습니다"
		HoneyLanguage.Russian.code -> "Нет истории транзакций"
		HoneyLanguage.TraditionalChinese.code -> "還沒有任何交易記錄"
		else -> ""
	}
	@JvmField
	val tokenDetailSubtitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "No transaction history for this token"
		HoneyLanguage.Chinese.code -> "在区块链上没有交易，所以您没有图表和记录信息"
		HoneyLanguage.Japanese.code -> "ブロックチェーン上に取引がありません。お客様にチャートや記録情報が表示できません"
		HoneyLanguage.Korean.code -> "블록체인에서 거래가 없으므로, 귀하한테 도표와 기록정보가 없습니다 "
		HoneyLanguage.Russian.code -> "В блокчейне нет операций, поэтому отсутствуют диаграммы и история"
		HoneyLanguage.TraditionalChinese.code -> "區塊鏈中沒有交易，所以您沒有圖表和記錄"
		else -> ""
	}
	@JvmField
	val notificationListTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Empty Here"
		HoneyLanguage.Chinese.code -> "没有新消息"
		HoneyLanguage.Japanese.code -> "もありません"
		HoneyLanguage.Korean.code -> "새 알림 메시지가 없습니다"
		HoneyLanguage.Russian.code -> "Нет нового уведомления"
		HoneyLanguage.TraditionalChinese.code -> "還沒有任何交易記錄"
		else -> ""
	}
	@JvmField
	val notificationListSubtitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "We will remind you when there are new transfers or other notifications."
		HoneyLanguage.Chinese.code -> "当有新的转账或其他通知消息时我们会在这里提醒您"
		HoneyLanguage.Japanese.code -> "新しい転送やその他の通知があった場合にお知らせします。"
		HoneyLanguage.Korean.code -> "새로운 전송이나 기타 알림이있을 때 알려드립니다."
		HoneyLanguage.Russian.code -> "Напоминаем, когда появляются новые переводы или другие уведомления."
		HoneyLanguage.TraditionalChinese.code -> "當有新的轉賬或其他通知消息時我們會在這裡提醒您"
		else -> ""
	}
	@JvmField
	val searchTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Token not found"
		HoneyLanguage.Chinese.code -> "没有找到这个 Token"
		HoneyLanguage.Japanese.code -> "この Token が見つかりません"
		HoneyLanguage.Korean.code -> "이 Token 을 찾지 못하였습니다"
		HoneyLanguage.Russian.code -> "Данный Token не найден"
		HoneyLanguage.TraditionalChinese.code -> "沒有找到這個 Token"
		else -> ""
	}
	@JvmField
	val searchSubtitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "No result for your search, please change and search again."
		HoneyLanguage.Chinese.code -> "没有搜索结果，请更改并再次搜索。"
		HoneyLanguage.Japanese.code -> "検索結果はありません。変更して再度検索してください。"
		HoneyLanguage.Korean.code -> "검색 결과가 없으므로 다시 변경하고 검색하십시오."
		HoneyLanguage.Russian.code -> "Нет результатов для вашего поиска, пожалуйста, измените и повторите поиск."
		HoneyLanguage.TraditionalChinese.code -> "沒有搜索結果，請更改並再次搜索。"
		else -> ""
	}
	@JvmField
	val quotationManagementTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "No Selected Tokens"
		HoneyLanguage.Chinese.code -> "还没有添加自选"
		HoneyLanguage.Japanese.code -> "トークンはありません"
		HoneyLanguage.Korean.code -> "선택을 추가하지 않았습니다"
		HoneyLanguage.Russian.code -> "Не добавил свой выбор"
		HoneyLanguage.TraditionalChinese.code -> "還沒有添加自選"
		else -> ""
	}
	@JvmField
	val quotationManagementSubtitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Search for and add tokens in the market by clicking the search icon"
		HoneyLanguage.Chinese.code -> "点击界面左上角的图标，搜索并添加市场里的 Token"
		HoneyLanguage.Japanese.code -> "左上隅にあるアイコンをクリックして、市場のトークンを検索して追加します"
		HoneyLanguage.Korean.code -> "인터페이스의 왼쪽 상단 모서리에있는 아이콘을 클릭하여 시장에서 토큰을 검색하고 추가하십시오."
		HoneyLanguage.Russian.code -> "Найдите и добавьте маркеры на рынок, щелкнув значок в верхнем левом углу интерфейса."
		HoneyLanguage.TraditionalChinese.code -> "點擊界面左上角的圖標, 搜索並添加市場裡的 Token"
		else -> ""
	}
	@JvmField
	val contractTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Empty Contact List"
		HoneyLanguage.Chinese.code -> "通讯录里空空的"
		HoneyLanguage.Japanese.code -> "連絡先リストに何も入力されていません"
		HoneyLanguage.Korean.code -> "전화번호부가 비어 있습니다"
		HoneyLanguage.Russian.code -> "Контакты пусты"
		HoneyLanguage.TraditionalChinese.code -> "通訊簿里沒有記錄"
		else -> ""
	}
	@JvmField
	val contractSubtitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Click the plus sign in the upper-left corner to add a contact address"
		HoneyLanguage.Chinese.code -> "点击左上角加号，可以添加常用联系人的地址"
		HoneyLanguage.Japanese.code -> "左上隅の「+」記号をクリックして、普段使われる連絡先のアドレスを追加することが出来ます"
		HoneyLanguage.Korean.code -> "좌측 상단 플러스 부호를 클릭하면 상용 연락처 주소를 추가할 수 있습니다"
		HoneyLanguage.Russian.code -> "Нажмите на плюс в левом верхнем углу, чтобы добавить адреса часто используемых контактов"
		HoneyLanguage.TraditionalChinese.code -> "點擊左上角加號，可以添加常用聯繫人的地址"
		else -> ""
	}
	@JvmField
	val currency = when (currentLanguage) {
		HoneyLanguage.English.code -> "Currency"
		HoneyLanguage.Chinese.code -> "货币设置"
		HoneyLanguage.Japanese.code -> "貨幣設定"
		HoneyLanguage.Korean.code -> "통화 설정"
		HoneyLanguage.Russian.code -> "Настройки валюты"
		HoneyLanguage.TraditionalChinese.code -> "貨幣設置"
		else -> ""
	}
	@JvmField
	val language = when (currentLanguage) {
		HoneyLanguage.English.code -> "Language"
		HoneyLanguage.Chinese.code -> "语言"
		HoneyLanguage.Japanese.code -> "言語"
		HoneyLanguage.Korean.code -> "언어"
		HoneyLanguage.Russian.code -> "Язык"
		HoneyLanguage.TraditionalChinese.code -> "語言"
		else -> ""
	}

}