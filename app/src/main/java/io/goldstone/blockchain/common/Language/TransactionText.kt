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
	val confirmations = when (currentLanguage) {
		HoneyLanguage.English.code -> "CONFIRMED BLOCKS"
		HoneyLanguage.Chinese.code -> "已确认区块"
		HoneyLanguage.Japanese.code -> "確認済みのブロック"
		HoneyLanguage.Korean.code -> "확인 된 블록"
		HoneyLanguage.Russian.code -> "ПОДТВЕРЖДЕННЫЕ БЛОКИ"
		HoneyLanguage.TraditionalChinese.code -> "已確認區塊"
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
	val transactionWeb = when (currentLanguage) {
		HoneyLanguage.English.code -> "Transaction Info"
		HoneyLanguage.Chinese.code -> "账单信息"
		HoneyLanguage.Japanese.code -> "転送情報"
		HoneyLanguage.Korean.code -> "정보 이전"
		HoneyLanguage.Russian.code -> "Сделка"
		HoneyLanguage.TraditionalChinese.code -> "賬單信息"
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
	val confirmTransactionTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Transfer Token"
		HoneyLanguage.Chinese.code -> "转账确认"
		HoneyLanguage.Japanese.code -> "転送トークン"
		HoneyLanguage.Korean.code -> "전송 토큰"
		HoneyLanguage.Russian.code -> "Переносить токен"
		HoneyLanguage.TraditionalChinese.code -> "轉賬確認"
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
		HoneyLanguage.English.code -> "MINER FEE"
		HoneyLanguage.Chinese.code -> "矿工费"
		HoneyLanguage.Japanese.code -> "マイニング費"
		HoneyLanguage.Korean.code -> "채굴수수료"
		HoneyLanguage.Russian.code -> "Плата майнера"
		HoneyLanguage.TraditionalChinese.code -> "礦工費"
		else -> ""
	}
	@JvmField
	val memo = when (currentLanguage) {
		HoneyLanguage.English.code -> "MEMO"
		HoneyLanguage.Chinese.code -> "备注"
		HoneyLanguage.Japanese.code -> "備考"
		HoneyLanguage.Korean.code -> "비고"
		HoneyLanguage.Russian.code -> "Примечание"
		HoneyLanguage.TraditionalChinese.code -> "메모"
		else -> ""
	}
	@JvmField
	val transactionHash = when (currentLanguage) {
		HoneyLanguage.English.code -> "TRANSACTION HASH"
		HoneyLanguage.Chinese.code -> "交易编号(Hash)"
		HoneyLanguage.Japanese.code -> "取引番号(Hash)"
		HoneyLanguage.Korean.code -> "거래Hash"
		HoneyLanguage.Russian.code -> "Номер операции (Hash)"
		HoneyLanguage.TraditionalChinese.code -> "交易編號(Hash)"
		else -> ""
	}
	@JvmField
	val blockNumber = when (currentLanguage) {
		HoneyLanguage.English.code -> "BLOCK HEIGHT"
		HoneyLanguage.Chinese.code -> "区块高度"
		HoneyLanguage.Japanese.code -> "ブロック高さ"
		HoneyLanguage.Korean.code -> "블록 높이"
		HoneyLanguage.Russian.code -> "Высота блока"
		HoneyLanguage.TraditionalChinese.code -> "區塊高度"
		else -> ""
	}


	@JvmField
	val pendingBlockConfirmation = when (currentLanguage) {
		// 当区块还没产生时，账单详情页区块高度的显示文案
		HoneyLanguage.English.code -> "Pending confirmation"
		HoneyLanguage.Chinese.code -> "等待区块确认"
		HoneyLanguage.Japanese.code -> "確認を待っています"
		HoneyLanguage.Korean.code -> "대기 블록 확인"
		HoneyLanguage.Russian.code -> "Ожидание подтверждения"
		HoneyLanguage.TraditionalChinese.code -> "等待區塊確認"
		else -> ""
	}


	@JvmField
	val process = when (currentLanguage) {
		HoneyLanguage.English.code -> "CONFIRMATION PROCESS"
		HoneyLanguage.Chinese.code -> "CONFIRMATION PROCESS"
		HoneyLanguage.Japanese.code -> "CONFIRMATION PROCESS"
		HoneyLanguage.Korean.code -> "CONFIRMATION PROCESS"
		HoneyLanguage.Russian.code -> "CONFIRMATION PROCESS"
		HoneyLanguage.TraditionalChinese.code -> "CONFIRMATION PROCESS"
		else -> ""
	}

	@JvmField
	val transactionDate = when (currentLanguage) {
		HoneyLanguage.English.code -> "TRANSACTION DATE"
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
		HoneyLanguage.Russian.code -> "Лредел"
		HoneyLanguage.TraditionalChinese.code -> "燃氣費上限"
		else -> ""
	}
	@JvmField
	val gasPrice = when (currentLanguage) {
		HoneyLanguage.English.code -> "Gas Price"
		HoneyLanguage.Chinese.code -> "单价"
		HoneyLanguage.Japanese.code -> "ガス料金単価"
		HoneyLanguage.Korean.code -> "가스단가"
		HoneyLanguage.Russian.code -> "Цена"
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
	val irreversible = when (currentLanguage) {
		HoneyLanguage.English.code -> "Irreversible"
		HoneyLanguage.Chinese.code -> "Irreversible"
		HoneyLanguage.Japanese.code -> "Irreversible"
		HoneyLanguage.Korean.code -> "Irreversible"
		HoneyLanguage.Russian.code -> "Irreversible"
		HoneyLanguage.TraditionalChinese.code -> "Irreversible"
		else -> ""
	}
}