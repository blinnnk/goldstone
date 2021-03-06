package io.goldstone.blinnnk.common.language

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
		HoneyLanguage.English.code -> "CONFIRMATION STATUS"
		HoneyLanguage.Chinese.code -> "确认状态"
		HoneyLanguage.Japanese.code -> "確認ステータス"
		HoneyLanguage.Korean.code -> "확인 상태"
		HoneyLanguage.Russian.code -> "ПОДТВЕРЖДЕНИЕ СТАТУСА"
		HoneyLanguage.TraditionalChinese.code -> "確認狀態"
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
	val signData = when (currentLanguage) {
		HoneyLanguage.English.code -> "Sign Data"
		HoneyLanguage.Chinese.code -> "数据签名"
		HoneyLanguage.Japanese.code -> "データ署名"
		HoneyLanguage.Korean.code -> "데이터 서명"
		HoneyLanguage.Russian.code -> "Подпись данных"
		HoneyLanguage.TraditionalChinese.code -> "數據簽名"
		else -> ""
	}

	@JvmField
	val signDataDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "Sign to chain to authenticate for authentication, please confirm.."
		HoneyLanguage.Chinese.code -> "向链发起签名以进行身份验证，请确认。"
		HoneyLanguage.Japanese.code -> "認証のためにチェーンに署名したいと考えています。確認してください。"
		HoneyLanguage.Korean.code -> "인증을 위해 체인에 서명하여 확인하십시오."
		HoneyLanguage.Russian.code -> "Подпишите цепочку для аутентификации для аутентификации, пожалуйста, подтвердите."
		HoneyLanguage.TraditionalChinese.code -> "向鏈發起簽名以進行身份驗證，請確認。"
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
		HoneyLanguage.Chinese.code -> "区块确认"
		HoneyLanguage.Japanese.code -> "ブロック確認"
		HoneyLanguage.Korean.code -> "블록 확인"
		HoneyLanguage.Russian.code -> "ПОДТВЕРЖДЕНИЕ ПРОЦЕССА"
		HoneyLanguage.TraditionalChinese.code -> "區塊確認"
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
		HoneyLanguage.TraditionalChinese.code -> " 至"
		else -> ""
	}

	@JvmField
	val irreversible = when (currentLanguage) {
		HoneyLanguage.English.code -> "Irreversible"
		HoneyLanguage.Chinese.code -> "不可逆的"
		HoneyLanguage.Japanese.code -> "不可逆"
		HoneyLanguage.Korean.code -> "뒤집을 수 없는"
		HoneyLanguage.Russian.code -> "Необратимые"
		HoneyLanguage.TraditionalChinese.code -> "不可逆的"
		else -> ""
	}
	@JvmField
	val confirmed = when (currentLanguage) {
		HoneyLanguage.English.code -> " Blocks Confirmed"
		HoneyLanguage.Chinese.code -> " 个区块已确认"
		HoneyLanguage.Japanese.code -> " ブロック確認済み"
		HoneyLanguage.Korean.code -> " 블록이 확인되었습니다"
		HoneyLanguage.Russian.code -> " блока подтверждены"
		HoneyLanguage.TraditionalChinese.code -> " 個區塊已確認"
		else -> ""
	}

	@JvmField
	val netUsage = when (currentLanguage) {
		HoneyLanguage.English.code -> "NET Usage"
		HoneyLanguage.Chinese.code -> "NET 用量"
		HoneyLanguage.Japanese.code -> "NET 消費"
		HoneyLanguage.Korean.code -> "NET 소비"
		HoneyLanguage.Russian.code -> "Использование NET"
		HoneyLanguage.TraditionalChinese.code -> "NET 用量"
		else -> ""
	}
	@JvmField
	val cpuUsage = when (currentLanguage) {
		HoneyLanguage.English.code -> "CPU Usage"
		HoneyLanguage.Chinese.code -> "CPU 用量"
		HoneyLanguage.Japanese.code -> "CPU 消費"
		HoneyLanguage.Korean.code -> "CPU 소비"
		HoneyLanguage.Russian.code -> "Использование CPU"
		HoneyLanguage.TraditionalChinese.code -> "CPU 用量"
		else -> ""
	}

	@JvmField
	val noActionsFound = when (currentLanguage) {
		HoneyLanguage.English.code -> "No corresponding record found"
		HoneyLanguage.Chinese.code -> "未找到相应记录"
		HoneyLanguage.Japanese.code -> "対応するレコードが見つかりません"
		HoneyLanguage.Korean.code -> "해당 레코드가 없습니다."
		HoneyLanguage.Russian.code -> "Соответствующая запись не найдена"
		HoneyLanguage.TraditionalChinese.code -> "未找到相應記錄"
		else -> ""
	}
	@JvmField
	val filterDataResource: (dataSize: Int) -> String = {
		when (currentLanguage) {
			HoneyLanguage.English.code -> {
				if (it < 2) {
					"(Data retrieved: $it Transaction)"
				} else {
					"(Data retrieved: $it Transactions)"
				}
			}
			HoneyLanguage.Chinese.code -> "(已检索数据: $it 条账单)"
			HoneyLanguage.Japanese.code -> "（取り出されたデータ：$it 取引）"
			HoneyLanguage.Korean.code -> "(검색된 데이터 : $it 거래)"
			HoneyLanguage.English.code -> {
				if (it < 2) {
					"(Полученные данные: $it транзакция)"
				} else {
					"(Полученные данные: $it транзакции)"
				}
			}
			HoneyLanguage.TraditionalChinese.code -> "(已檢索數據: $it 條賬單)"
			else -> ""
		}
	}
	@JvmField
	val filterFoundNoItem = when (currentLanguage) {
		HoneyLanguage.English.code -> "No specified type of transaction was found in the currently retrieved data"
		HoneyLanguage.Chinese.code -> "在目前检索到的数据中未发现指定类型的账单"
		HoneyLanguage.Japanese.code -> "現在検索されているデータに特定の種類の請求書が見つかりませんでした"
		HoneyLanguage.Korean.code -> "현재 검색된 데이터에서 지정된 유형의 청구서를 찾을 수 없습니다."
		HoneyLanguage.Russian.code -> "Указанный тип транзакции не был найден в полученных данных."
		HoneyLanguage.TraditionalChinese.code -> "在目前檢索到的數據中未發現指定類型的賬單"
		else -> ""
	}

	@JvmField
	val loadMore = when (currentLanguage) {
		HoneyLanguage.English.code -> "Load more"
		HoneyLanguage.Chinese.code -> "加载更多"
		HoneyLanguage.Japanese.code -> "もっと読み込む"
		HoneyLanguage.Korean.code -> "더 많은로드"
		HoneyLanguage.Russian.code -> "Загрузить больше"
		HoneyLanguage.TraditionalChinese.code -> "加載更多"
		else -> ""
	}
}