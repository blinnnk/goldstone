package io.goldstone.blockchain.common.language


/**
 * @author KaySaith
 * @date  2018/09/14
 */
object PrepareTransferText {
	@JvmField
	val invalidEOSMemoSize = when (currentLanguage) {
		HoneyLanguage.English.code -> "EOS memo content size is only allowed less than 256 characters"
		HoneyLanguage.Chinese.code -> "EOS memo content size is only allowed less than 256 characters"
		HoneyLanguage.Japanese.code -> "EOS memo content size is only allowed less than 256 characters"
		HoneyLanguage.Korean.code -> "EOS memo content size is only allowed less than 256 characters"
		HoneyLanguage.Russian.code -> "EOS memo content size is only allowed less than 256 characters"
		HoneyLanguage.TraditionalChinese.code -> "EOS memo content size is only allowed less than 256 characters"
		else -> ""
	}
	@JvmField
	val sendAmountSuffix = when (currentLanguage) {
		HoneyLanguage.English.code -> "Amount"
		HoneyLanguage.Chinese.code -> "数量"
		HoneyLanguage.Japanese.code -> "数量"
		HoneyLanguage.Korean.code -> "수량"
		HoneyLanguage.Russian.code -> "сумму"
		HoneyLanguage.TraditionalChinese.code -> "數量"
		else -> ""
	}
	@JvmField
	val memoInformation = when (currentLanguage) {
		HoneyLanguage.English.code -> "Memo Information"
		HoneyLanguage.Chinese.code -> "备注信息"
		HoneyLanguage.Japanese.code -> "備考情報"
		HoneyLanguage.Korean.code -> "비고정보"
		HoneyLanguage.Russian.code -> "Информация о примечании"
		HoneyLanguage.TraditionalChinese.code -> "備註信息"
		else -> ""
	}
	@JvmField
	val memo = when (currentLanguage) {
		HoneyLanguage.English.code -> "Memo"
		HoneyLanguage.Chinese.code -> "备注"
		HoneyLanguage.Japanese.code -> "備考"
		HoneyLanguage.Korean.code -> "비고"
		HoneyLanguage.Russian.code -> "Примечание"
		HoneyLanguage.TraditionalChinese.code -> "備註"
		else -> ""
	}
	@JvmField
	val customChangeAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "Custom Change Address"
		HoneyLanguage.Chinese.code -> "设置找零地址"
		HoneyLanguage.Japanese.code -> "変更アドレスを設定する"
		HoneyLanguage.Korean.code -> "변경 주소 설정"
		HoneyLanguage.Russian.code -> "Задайте адрес изменения"
		HoneyLanguage.TraditionalChinese.code -> "設置找零地址"
		else -> ""
	}
	@JvmField
	val changeAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "Change Address"
		HoneyLanguage.Chinese.code -> "更换地址"
		HoneyLanguage.Japanese.code -> "置換アドレス"
		HoneyLanguage.Korean.code -> "대체 주소"
		HoneyLanguage.Russian.code -> "Адрес для замещения"
		HoneyLanguage.TraditionalChinese.code -> "更換地址"
		else -> ""
	}
	@JvmField
	val addAMemo = when (currentLanguage) {
		HoneyLanguage.English.code -> "Add a Memo"
		HoneyLanguage.Chinese.code -> "添加备注"
		HoneyLanguage.Japanese.code -> "備考を追加する"
		HoneyLanguage.Korean.code -> "비고 추가"
		HoneyLanguage.Russian.code -> "Добавить примечание"
		HoneyLanguage.TraditionalChinese.code -> "添加備註"
		else -> ""
	}
	@JvmField
	val price = when (currentLanguage) {
		HoneyLanguage.English.code -> "UNIT PRICE"
		HoneyLanguage.Chinese.code -> "单价"
		HoneyLanguage.Japanese.code -> "単価"
		HoneyLanguage.Korean.code -> "단가"
		HoneyLanguage.Russian.code -> "Цена за единицу"
		HoneyLanguage.TraditionalChinese.code -> "單價"
		else -> ""
	}
	@JvmField
	val currentPrice = when (currentLanguage) {
		HoneyLanguage.English.code -> "Current Price"
		HoneyLanguage.Chinese.code -> "当前价"
		HoneyLanguage.Japanese.code -> "現在の価格"
		HoneyLanguage.Korean.code -> "현재가격"
		HoneyLanguage.Russian.code -> "Текущая цена"
		HoneyLanguage.TraditionalChinese.code -> "時價"
		else -> ""
	}
	@JvmField
	val accountInfo = when (currentLanguage) {
		HoneyLanguage.English.code -> "Account Information"
		HoneyLanguage.Chinese.code -> "账户信息"
		HoneyLanguage.Japanese.code -> "アカウント情報"
		HoneyLanguage.Korean.code -> "계정 정보"
		HoneyLanguage.Russian.code -> "Информация о счете"
		HoneyLanguage.TraditionalChinese.code -> "帳戶信息"
		else -> ""
	}
	@JvmField
	val willSpending = when (currentLanguage) {
		HoneyLanguage.English.code -> "WILL SPEND"
		HoneyLanguage.Chinese.code -> "预计花费"
		HoneyLanguage.Japanese.code -> "見積価格"
		HoneyLanguage.Korean.code -> "예상 소비"
		HoneyLanguage.Russian.code -> "Предварительные расходы"
		HoneyLanguage.TraditionalChinese.code -> "預計花費"
		else -> ""
	}
	@JvmField
	val send = when (currentLanguage) {
		HoneyLanguage.English.code -> "SEND TO"
		HoneyLanguage.Chinese.code -> "发送至"
		HoneyLanguage.Japanese.code -> "発送先"
		HoneyLanguage.Korean.code -> "수신자"
		HoneyLanguage.Russian.code -> "Отправить до"
		HoneyLanguage.TraditionalChinese.code -> "發送至"
		else -> ""
	}
	@JvmField
	val from = when (currentLanguage) {
		HoneyLanguage.English.code -> "FROM"
		HoneyLanguage.Chinese.code -> "发送者"
		HoneyLanguage.Japanese.code -> "発送者"
		HoneyLanguage.Korean.code -> "발신자"
		HoneyLanguage.Russian.code -> "Отправитель"
		HoneyLanguage.TraditionalChinese.code -> "發送者"
		else -> ""
	}
	@JvmField
	val recommend = when (currentLanguage) {
		HoneyLanguage.English.code -> "Recommend"
		HoneyLanguage.Chinese.code -> "推荐"
		HoneyLanguage.Japanese.code -> "お勧め"
		HoneyLanguage.Korean.code -> "추천"
		HoneyLanguage.Russian.code -> "Рекомендуемое"
		HoneyLanguage.TraditionalChinese.code -> "推薦"
		else -> ""
	}
	@JvmField
	val cheap = when (currentLanguage) {
		HoneyLanguage.English.code -> "Cheap"
		HoneyLanguage.Chinese.code -> "便宜"
		HoneyLanguage.Japanese.code -> "安い"
		HoneyLanguage.Korean.code -> "발신자"
		HoneyLanguage.Russian.code -> "Дешево"
		HoneyLanguage.TraditionalChinese.code -> "便宜"
		else -> ""
	}
	@JvmField
	val fast = when (currentLanguage) {
		HoneyLanguage.English.code -> "Fast"
		HoneyLanguage.Chinese.code -> "快速"
		HoneyLanguage.Japanese.code -> "速い"
		HoneyLanguage.Korean.code -> "빠른"
		HoneyLanguage.Russian.code -> "Быстро"
		HoneyLanguage.TraditionalChinese.code -> "快速"
		else -> ""
	}
	@JvmField
	val customize = when (currentLanguage) {
		HoneyLanguage.English.code -> "Customize"
		HoneyLanguage.Chinese.code -> "自定义"
		HoneyLanguage.Japanese.code -> "カスタマイズ"
		HoneyLanguage.Korean.code -> "사용자 정의"
		HoneyLanguage.Russian.code -> "Самоопред"
		HoneyLanguage.TraditionalChinese.code -> "自定義"
		else -> ""
	}
}