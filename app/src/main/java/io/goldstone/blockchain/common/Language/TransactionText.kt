package io.goldstone.blockchain.common.language

/**
 * @date 2018/8/8 2:12 AM
 * @author KaySaith
 */

object TransactionText {
	@JvmField
	val transaction = when (currentLanguage) {
		HoneyLanguage.English.code -> "Transaction History"
		HoneyLanguage.Chinese.code -> "交易历史"
		HoneyLanguage.Japanese.code -> "取引履歴"
		HoneyLanguage.Korean.code -> "거래역사"
		HoneyLanguage.Russian.code -> "История операций"
		HoneyLanguage.TraditionalChinese.code -> "交易歷史"
		else -> ""
	}
	@JvmField
	val detail = when (currentLanguage) {
		HoneyLanguage.English.code -> "Transaction Details"
		HoneyLanguage.Chinese.code -> "交易详情"
		HoneyLanguage.Japanese.code -> "取引詳細"
		HoneyLanguage.Korean.code -> "거래 구체상황"
		HoneyLanguage.Russian.code -> "Подробности операций"
		HoneyLanguage.TraditionalChinese.code -> "交易明細"
		else -> ""
	}
	@JvmField
	val etherScanTransaction = when (currentLanguage) {
		HoneyLanguage.English.code -> "Etherscan Details"
		HoneyLanguage.Chinese.code -> "EtherScan 详情"
		HoneyLanguage.Japanese.code -> "EtherScan 詳細"
		HoneyLanguage.Korean.code -> "EtherScan 구체상황"
		HoneyLanguage.Russian.code -> "Подробности EtherScan"
		HoneyLanguage.TraditionalChinese.code -> "EtherScan 詳情"
		else -> ""
	}
	@JvmField
	val gasTracker = when (currentLanguage) {
		HoneyLanguage.English.code -> "GasTracker"
		HoneyLanguage.Chinese.code -> "GasTracker 详情"
		HoneyLanguage.Japanese.code -> "GasTracker 詳細"
		HoneyLanguage.Korean.code -> "GasTracker 구체상황"
		HoneyLanguage.Russian.code -> "Подробности GasTracker"
		HoneyLanguage.TraditionalChinese.code -> "GasTracker 詳情"
		else -> ""
	}
	@JvmField
	val insight = when (currentLanguage) {
		HoneyLanguage.English.code -> "Insigt Info"
		HoneyLanguage.Chinese.code -> "Insigt Info"
		HoneyLanguage.Japanese.code -> "Insigt Info"
		HoneyLanguage.Korean.code -> "Insigt Info"
		HoneyLanguage.Russian.code -> "Insigt Info"
		HoneyLanguage.TraditionalChinese.code -> "Insigt Info"
		else -> ""
	}
	@JvmField
	val url = when (currentLanguage) {
		HoneyLanguage.English.code -> "Open URL"
		HoneyLanguage.Chinese.code -> "从网址打开"
		HoneyLanguage.Japanese.code -> "ウェブサイトから開く"
		HoneyLanguage.Korean.code -> "웹에서 불러오기"
		HoneyLanguage.Russian.code -> "Открыть с сайта"
		HoneyLanguage.TraditionalChinese.code -> "從網址打開"
		else -> ""
	}
	@JvmField
	val confirmTransaction = when (currentLanguage) {
		HoneyLanguage.English.code -> "Confirm transaction with your password"
		HoneyLanguage.Chinese.code -> "输入您的密码以确认交易"
		HoneyLanguage.Japanese.code -> "お客様のパスワードを入力して取引を確認します"
		HoneyLanguage.Korean.code -> "귀하의 비밀번호를 입력하여 거래를 확인하십시오"
		HoneyLanguage.Russian.code -> "Введите свой пароль для подтверждения операции"
		HoneyLanguage.TraditionalChinese.code -> "輸入您的密碼以確認交易"
		else -> ""
	}
	@JvmField
	val minerFee = when (currentLanguage) {
		HoneyLanguage.English.code -> "Miner Fee"
		HoneyLanguage.Chinese.code -> "矿工费"
		HoneyLanguage.Japanese.code -> "マイニング費"
		HoneyLanguage.Korean.code -> "채굴수수료"
		HoneyLanguage.Russian.code -> "Плата майнера"
		HoneyLanguage.TraditionalChinese.code -> "礦工費"
		else -> ""
	}
	@JvmField
	val memo = when (currentLanguage) {
		HoneyLanguage.English.code -> "Memo"
		HoneyLanguage.Chinese.code -> "备注"
		HoneyLanguage.Japanese.code -> "備考"
		HoneyLanguage.Korean.code -> "비고"
		HoneyLanguage.Russian.code -> "Примечание"
		HoneyLanguage.TraditionalChinese.code -> "메모"
		else -> ""
	}
	@JvmField
	val transactionHash = when (currentLanguage) {
		HoneyLanguage.English.code -> "Transaction Hash"
		HoneyLanguage.Chinese.code -> "交易编号(Hash)"
		HoneyLanguage.Japanese.code -> "取引番号(Hash)"
		HoneyLanguage.Korean.code -> "거래Hash"
		HoneyLanguage.Russian.code -> "Номер операции (Hash)"
		HoneyLanguage.TraditionalChinese.code -> "交易編號(Hash)"
		else -> ""
	}
	@JvmField
	val blockNumber = when (currentLanguage) {
		HoneyLanguage.English.code -> "Block Height"
		HoneyLanguage.Chinese.code -> "区块高度"
		HoneyLanguage.Japanese.code -> "ブロック高さ"
		HoneyLanguage.Korean.code -> "블록 높이"
		HoneyLanguage.Russian.code -> "Высота блока"
		HoneyLanguage.TraditionalChinese.code -> "區塊高度"
		else -> ""
	}
	@JvmField
	val transactionDate = when (currentLanguage) {
		HoneyLanguage.English.code -> "Transaction Date"
		HoneyLanguage.Chinese.code -> "交易日期"
		HoneyLanguage.Japanese.code -> "取引期日"
		HoneyLanguage.Korean.code -> "거래일자"
		HoneyLanguage.Russian.code -> "Дата операции"
		HoneyLanguage.TraditionalChinese.code -> "交易日期"
		else -> ""
	}
	@JvmField
	val gasLimit = when (currentLanguage) {
		HoneyLanguage.English.code -> "Gas Limit"
		HoneyLanguage.Chinese.code -> "燃气费上限"
		HoneyLanguage.Japanese.code -> "ガス料金上限"
		HoneyLanguage.Korean.code -> "가스요금 상한"
		HoneyLanguage.Russian.code -> "Лимит платы за газ"
		HoneyLanguage.TraditionalChinese.code -> "燃氣費上限"
		else -> ""
	}
	@JvmField
	val gasPrice = when (currentLanguage) {
		HoneyLanguage.English.code -> "Gas Price (Gwei)"
		HoneyLanguage.Chinese.code -> "燃气单价"
		HoneyLanguage.Japanese.code -> "ガス料金単価"
		HoneyLanguage.Korean.code -> "가스단가"
		HoneyLanguage.Russian.code -> "Цена за газ"
		HoneyLanguage.TraditionalChinese.code -> "燃氣單價"
		else -> ""
	}
	@JvmField
	val satoshiValue = when (currentLanguage) {
		HoneyLanguage.English.code -> "Satoshi Value"
		HoneyLanguage.Chinese.code -> "交易手续费价值"
		HoneyLanguage.Japanese.code -> "取引手数料"
		HoneyLanguage.Korean.code -> "거래 수수료 값"
		HoneyLanguage.Russian.code -> "Стоимость транзакции"
		HoneyLanguage.TraditionalChinese.code -> "交易手續費價值"
		else -> ""
	}
	@JvmField
	val noMemo = when (currentLanguage) {
		HoneyLanguage.English.code -> "There isn't a memo."
		HoneyLanguage.Chinese.code -> "没有备注信息"
		HoneyLanguage.Japanese.code -> "備考情報がありません"
		HoneyLanguage.Korean.code -> "메모가 없습니다."
		HoneyLanguage.Russian.code -> "Информация примечания отсутствует"
		HoneyLanguage.TraditionalChinese.code -> "沒有備註信息。"
		else -> ""
	}
	@JvmField
	val tokenSelection = when (currentLanguage) {
		HoneyLanguage.English.code -> "Select a Token"
		HoneyLanguage.Chinese.code -> "选择一个Token"
		HoneyLanguage.Japanese.code -> "Tokenを一つ選んで下さい"
		HoneyLanguage.Korean.code -> "토큰 선택"
		HoneyLanguage.Russian.code -> "Выберите один Token"
		HoneyLanguage.TraditionalChinese.code -> "選擇一個Token"
		else -> ""
	}
	@JvmField
	val receivedFrom = when (currentLanguage) {
		HoneyLanguage.English.code -> " received from "
		HoneyLanguage.Chinese.code -> " 接受自 "
		HoneyLanguage.Japanese.code -> " 受入元 "
		HoneyLanguage.Korean.code -> " 로부터받은 "
		HoneyLanguage.Russian.code -> "Получить от"
		HoneyLanguage.TraditionalChinese.code -> " 接收自 "
		else -> ""
	}
	@JvmField
	val sentTo = when (currentLanguage) {
		HoneyLanguage.English.code -> " sent to "
		HoneyLanguage.Chinese.code -> " 发送至 "
		HoneyLanguage.Japanese.code -> " 発送先 "
		HoneyLanguage.Korean.code -> "　전송　"
		HoneyLanguage.Russian.code -> "Отправить до"
		HoneyLanguage.TraditionalChinese.code -> "  發送至 "
		else -> ""
	}
	@JvmField
	val transferResultReceived = when (currentLanguage) {
		HoneyLanguage.English.code -> "Received "
		HoneyLanguage.Chinese.code -> "入账 "
		HoneyLanguage.Japanese.code -> "記帳 "
		HoneyLanguage.Korean.code -> "받은 "
		HoneyLanguage.Russian.code -> "Зачисление на счет"
		HoneyLanguage.TraditionalChinese.code -> "入賬 "
		else -> ""
	}
	@JvmField
	val transferResultFrom = when (currentLanguage) {
		HoneyLanguage.English.code -> " from"
		HoneyLanguage.Chinese.code -> ", 来自"
		HoneyLanguage.Japanese.code -> "，受入元"
		HoneyLanguage.Korean.code -> " 발신자는입니다"
		HoneyLanguage.Russian.code -> ", из"
		HoneyLanguage.TraditionalChinese.code -> ", 來自"
		else -> ""
	}
	@JvmField
	val transferResultSent = when (currentLanguage) {
		HoneyLanguage.English.code -> " Sent"
		HoneyLanguage.Chinese.code -> "转出"
		HoneyLanguage.Japanese.code -> "振込"
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	@JvmField
	val transferResultTo = when (currentLanguage) {
		HoneyLanguage.English.code -> " to"
		HoneyLanguage.Chinese.code -> " 至"
		HoneyLanguage.Japanese.code -> " に"
		HoneyLanguage.Korean.code -> "　점 만점에, 받는 사람"
		HoneyLanguage.Russian.code -> "до"
		HoneyLanguage.TraditionalChinese.code -> "至"
		else -> ""
	}
	@JvmField
	val confirmedBlocks = when (currentLanguage) {
		HoneyLanguage.English.code -> "6 blocks have comfirmed"
		HoneyLanguage.Chinese.code -> "6 个区块已经确认了本次转账"
		HoneyLanguage.Japanese.code -> "6つのブロックが今回の振込を確認されました"
		HoneyLanguage.Korean.code -> "6 블록에서이 양도가 확인되었습니다"
		HoneyLanguage.Russian.code -> "6 блоков подтвердили данный перевод"
		HoneyLanguage.TraditionalChinese.code -> "6 個區塊已經確認了本次轉賬"
		else -> ""
	}
}

object PrepareTransferText {
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
		HoneyLanguage.Russian.code -> "Самоопред."
		HoneyLanguage.TraditionalChinese.code -> "自定義"
		else -> ""
	}
}