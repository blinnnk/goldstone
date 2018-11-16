package io.goldstone.blockchain.common.language

/**
 * @date 2018/8/8 2:27 AM
 * @author KaySaith
 */

object TokenDetailText {
	@JvmField
	val address = when (currentLanguage) {
		HoneyLanguage.English.code -> "Recipient Address"
		HoneyLanguage.Chinese.code -> "接收地址"
		HoneyLanguage.Japanese.code -> "受入アドレス"
		HoneyLanguage.Korean.code -> "접수주소"
		HoneyLanguage.Russian.code -> "Адрес получения"
		HoneyLanguage.TraditionalChinese.code -> "接收地址"
		else -> ""
	}
	@JvmField
	val deposit = when (currentLanguage) {
		HoneyLanguage.English.code -> "Deposit"
		HoneyLanguage.Chinese.code -> "接收"
		HoneyLanguage.Japanese.code -> "受入"
		HoneyLanguage.Korean.code -> "접수"
		HoneyLanguage.Russian.code -> "Получить"
		HoneyLanguage.TraditionalChinese.code -> "接收"
		else -> ""
	}
	@JvmField
	val customGas = when (currentLanguage) {
		HoneyLanguage.English.code -> "Gas Editor"
		HoneyLanguage.Chinese.code -> "自定义燃气费"
		HoneyLanguage.Japanese.code -> "ガス料金のカスタマイズ"
		HoneyLanguage.Korean.code -> "자체정의 가스요금"
		HoneyLanguage.Russian.code -> "Пользовательская плата за газ"
		HoneyLanguage.TraditionalChinese.code -> "自定義燃氣費"
		else -> ""
	}
	@JvmField
	val paymentValue = when (currentLanguage) {
		HoneyLanguage.English.code -> "Payment Value"
		HoneyLanguage.Chinese.code -> "实际价值"
		HoneyLanguage.Japanese.code -> "実際価格"
		HoneyLanguage.Korean.code -> "실제가치"
		HoneyLanguage.Russian.code -> "Реальная стоимость"
		HoneyLanguage.TraditionalChinese.code -> "實際價值"
		else -> ""
	}
	@JvmField
	val transferDetail = when (currentLanguage) {
		HoneyLanguage.English.code -> "Transfer Detail"
		HoneyLanguage.Chinese.code -> "交易详情"
		HoneyLanguage.Japanese.code -> "取引詳細"
		HoneyLanguage.Korean.code -> "거래 구체상황"
		HoneyLanguage.Russian.code -> "Подробности операций"
		HoneyLanguage.TraditionalChinese.code -> "交易詳情"
		else -> ""
	}
	@JvmField
	val customMiner = when (currentLanguage) {
		HoneyLanguage.English.code -> "Custom miner fee"
		HoneyLanguage.Chinese.code -> "自定义矿工费"
		HoneyLanguage.Japanese.code -> "マイニング費のカスタマイズ"
		HoneyLanguage.Korean.code -> "자체정의 채굴수수료"
		HoneyLanguage.Russian.code -> "Пользовательская плата майнера"
		HoneyLanguage.TraditionalChinese.code -> "自定義礦工費"
		else -> ""
	}
	@JvmField
	val tokenDetail = when (currentLanguage) {
		HoneyLanguage.English.code -> "Token Detail"
		HoneyLanguage.Chinese.code -> "代币详情"
		HoneyLanguage.Japanese.code -> "トークン詳細"
		HoneyLanguage.Korean.code -> "Token 소개"
		HoneyLanguage.Russian.code -> "Подробности токена"
		HoneyLanguage.TraditionalChinese.code -> "代幣詳情"
		else -> ""
	}
	@JvmField
	val transferToLocalWalletAlertDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "This will transfer value to an address already in one of your wallets. Are you sure?"
		HoneyLanguage.Chinese.code -> "这个地址也在GoldStone钱包中。你确定要给这个地址转账吗？"
		HoneyLanguage.Japanese.code -> "このアドレスもGoldStoneウォレットの中に存在しています。このアドレスに振込を行いますか？"
		HoneyLanguage.Korean.code -> "이 주소 역시 GoldStone 지갑에 포함되어 있습니다. 이 주소로 이체할까요？"
		HoneyLanguage.Russian.code -> "Этот адрес в кошельке GoldStone Вы уверены в осуществлении перевода на данный адрес?"
		HoneyLanguage.TraditionalChinese.code -> "這個地址也在GoldStone錢包中，你確定要給自己轉賬嗎？"
		else -> ""
	}
	@JvmField
	val transferToLocalWalletAlertTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Transaction Alert"
		HoneyLanguage.Chinese.code -> "转账提示"
		HoneyLanguage.Japanese.code -> "振込アドバイス"
		HoneyLanguage.Korean.code -> "거래 알림"
		HoneyLanguage.Russian.code -> "Подсказки перевода"
		HoneyLanguage.TraditionalChinese.code -> "轉賬提示"
		else -> ""
	}

	//	Token/资产信息
	//	tab标题
	@JvmField
	val assets = when (currentLanguage) {
		HoneyLanguage.English.code -> "Assets"
		HoneyLanguage.Chinese.code -> "资产"
		HoneyLanguage.Japanese.code -> "資産"
		HoneyLanguage.Korean.code -> "자산"
		HoneyLanguage.Russian.code -> "Активы"
		HoneyLanguage.TraditionalChinese.code -> "資產"
		else -> ""
	}
	@JvmField
	val information = when (currentLanguage) {
		HoneyLanguage.English.code -> "Information"
		HoneyLanguage.Chinese.code -> "信息"
		HoneyLanguage.Japanese.code -> "情報"
		HoneyLanguage.Korean.code -> "통화 정보"
		HoneyLanguage.Russian.code -> "Информация"
		HoneyLanguage.TraditionalChinese.code -> "信息"
		else -> ""
	}
	@JvmField
	val transactionList = when (currentLanguage) {
		HoneyLanguage.English.code -> "Transaction List"
		HoneyLanguage.Chinese.code -> "账单记录"
		HoneyLanguage.Japanese.code -> "取引リスト"
		HoneyLanguage.Korean.code -> "결제 기록"
		HoneyLanguage.Russian.code -> "Список транзакций"
		HoneyLanguage.TraditionalChinese.code -> "賬單記錄"
		else -> ""
	}

	// token详情cell
	@JvmField
	val chainType = when (currentLanguage) {
		HoneyLanguage.English.code -> "CHAIN"
		HoneyLanguage.Chinese.code -> "链"
		HoneyLanguage.Japanese.code -> "チェーン"
		HoneyLanguage.Korean.code -> "사슬"
		HoneyLanguage.Russian.code -> "Сеть"
		HoneyLanguage.TraditionalChinese.code -> "鏈"
		else -> ""
	}

	@JvmField
	val latestActivationTime = when (currentLanguage) {
		HoneyLanguage.English.code -> "this account's latest active time is"
		HoneyLanguage.Chinese.code -> "当前账户的最近活跃时间是"
		HoneyLanguage.Japanese.code -> "このアカウントの最新のアクティブ時間があります"
		HoneyLanguage.Korean.code -> "이 계정의 최근 활성 시간"
		HoneyLanguage.Russian.code -> "Последнее активное время этой учетной записи"
		HoneyLanguage.TraditionalChinese.code -> "當前賬戶的最近活躍時間是"
		else -> ""
	}

	@JvmField
	val checkDetail = when (currentLanguage) {
		HoneyLanguage.English.code -> "CHECK DETAIL"
		HoneyLanguage.Chinese.code -> "查看详情"
		HoneyLanguage.Japanese.code -> "詳細を確認"
		HoneyLanguage.Korean.code -> "세부 정보 확인"
		HoneyLanguage.Russian.code -> "ПРОВЕРИТЬ ДЕТАЛИ"
		HoneyLanguage.TraditionalChinese.code -> "查看詳情"
		else -> ""
	}

	// token详情cell
	@JvmField
	val balance = when (currentLanguage) {
		HoneyLanguage.English.code -> "Balance"
		HoneyLanguage.Chinese.code -> "余额"
		HoneyLanguage.Japanese.code -> "残高"
		HoneyLanguage.Korean.code -> "균형"
		HoneyLanguage.Russian.code -> "баланс"
		HoneyLanguage.TraditionalChinese.code -> "餘額"
		else -> ""
	}

	@JvmField
	val transactionCount = when (currentLanguage) {
		HoneyLanguage.English.code -> "Transaction Count"
		HoneyLanguage.Chinese.code -> "转账次数"
		HoneyLanguage.Japanese.code -> "Transaction Count"
		HoneyLanguage.Korean.code -> "거래 수"
		HoneyLanguage.Russian.code -> "Количество транзакций"
		HoneyLanguage.TraditionalChinese.code -> "轉賬次數"
		else -> ""
	}

	// for btc/ltc/bch
	@JvmField
	val totalReceived = when (currentLanguage) {
		HoneyLanguage.English.code -> "Total Received"
		HoneyLanguage.Chinese.code -> "总接收"
		HoneyLanguage.Japanese.code -> "受信総数"
		HoneyLanguage.Korean.code -> "총 접수"
		HoneyLanguage.Russian.code -> "Всего получено"
		HoneyLanguage.TraditionalChinese.code -> "總接收"
		else -> ""
	}

	@JvmField
	val totalSent = when (currentLanguage) {
		HoneyLanguage.English.code -> "Total Sent"
		HoneyLanguage.Chinese.code -> "总发送"
		HoneyLanguage.Japanese.code -> "合計送信済み"
		HoneyLanguage.Korean.code -> "총 보낸"
		HoneyLanguage.Russian.code -> "Всего отправлено"
		HoneyLanguage.TraditionalChinese.code -> "總發送"
		else -> ""
	}

	@JvmField
	val accountInformation = when (currentLanguage) {
		HoneyLanguage.English.code -> "Account Information"
		HoneyLanguage.Chinese.code -> "账户信息"
		HoneyLanguage.Japanese.code -> "アカウント情報"
		HoneyLanguage.Korean.code -> "계정 정보"
		HoneyLanguage.Russian.code -> "Информация об учетной записи"
		HoneyLanguage.TraditionalChinese.code -> "賬戶信息"
		else -> ""
	}

	@JvmField
	val transaction = when (currentLanguage) {
		HoneyLanguage.English.code -> "Transaction"
		HoneyLanguage.Chinese.code -> "账单"
		HoneyLanguage.Japanese.code -> "取引"
		HoneyLanguage.Korean.code -> "거래"
		HoneyLanguage.Russian.code -> "Сделка"
		HoneyLanguage.TraditionalChinese.code -> "賬單"
		else -> ""
	}

	@JvmField
	val addressDetail = when (currentLanguage) {
		HoneyLanguage.English.code -> "Address Detail"
		HoneyLanguage.Chinese.code -> "地址详情"
		HoneyLanguage.Japanese.code -> "アドレス情報"
		HoneyLanguage.Korean.code -> "주소 세부 정보"
		HoneyLanguage.Russian.code -> "Подробное описание адреса"
		HoneyLanguage.TraditionalChinese.code -> "地址詳情"
		else -> ""
	}

	//	····················EOS账户····················
	@JvmField
	val accountManagement = when (currentLanguage) {
		HoneyLanguage.English.code -> "Account Manage"
		HoneyLanguage.Chinese.code -> "帐户管理"
		HoneyLanguage.Japanese.code -> "アカウント管理"
		HoneyLanguage.Korean.code -> "계정 관리"
		HoneyLanguage.Russian.code -> "Управление учетной записью"
		HoneyLanguage.TraditionalChinese.code -> "帳戶管理"
		else -> ""
	}

	//	EOS资源相关
	@JvmField
	val resources = when (currentLanguage) {
		HoneyLanguage.English.code -> "Resources"
		HoneyLanguage.Chinese.code -> "资源"
		HoneyLanguage.Japanese.code -> "リソース"
		HoneyLanguage.Korean.code -> "자원"
		HoneyLanguage.Russian.code -> "Ресурсы"
		HoneyLanguage.TraditionalChinese.code -> "資源"
		else -> ""
	}
	@JvmField
	val assetTools = when (currentLanguage) {
		HoneyLanguage.English.code -> "Resource Tools"
		HoneyLanguage.Chinese.code -> "资源工具"
		HoneyLanguage.Japanese.code -> "リソースツール"
		HoneyLanguage.Korean.code -> "리소스 도구"
		HoneyLanguage.Russian.code -> "Инструмент ресурса"
		HoneyLanguage.TraditionalChinese.code -> "資源工具"
		else -> ""
	}
	@JvmField
	val delegateTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "DELEGATE"
		HoneyLanguage.Chinese.code -> "代理"
		HoneyLanguage.Japanese.code -> "デリゲート"
		HoneyLanguage.Korean.code -> "대의원"
		HoneyLanguage.Russian.code -> "Делегат"
		HoneyLanguage.TraditionalChinese.code -> "代理"
		else -> ""
	}
	@JvmField
	val refundTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "REFUND"
		HoneyLanguage.Chinese.code -> "赎回"
		HoneyLanguage.Japanese.code -> "払い戻し"
		HoneyLanguage.Korean.code -> "환불"
		HoneyLanguage.Russian.code -> "Возврат"
		HoneyLanguage.TraditionalChinese.code -> "贖回"
		else -> ""
	}
	@JvmField
	val ram = when (currentLanguage) {
		HoneyLanguage.English.code -> "RAM (Bytes)"
		HoneyLanguage.Chinese.code -> "RAM (内存)"
		HoneyLanguage.Japanese.code -> "RAM (ラム)"
		HoneyLanguage.Korean.code -> "RAM (램)"
		HoneyLanguage.Russian.code -> "RAM (Bytes)"
		HoneyLanguage.TraditionalChinese.code -> "RAM (內存)"
		else -> ""
	}
	@JvmField
	val cpu = when (currentLanguage) {
		HoneyLanguage.English.code -> "CPU"
		HoneyLanguage.Chinese.code -> "CPU(计算)"
		HoneyLanguage.Japanese.code -> "CPU"
		HoneyLanguage.Korean.code -> "CPU"
		HoneyLanguage.Russian.code -> "CPU"
		HoneyLanguage.TraditionalChinese.code -> "CPU(計算)"
		else -> ""
	}
	@JvmField
	val net = when (currentLanguage) {
		HoneyLanguage.English.code -> "NET"
		HoneyLanguage.Chinese.code -> "NET(网络)"
		HoneyLanguage.Japanese.code -> "NET"
		HoneyLanguage.Korean.code -> "NET"
		HoneyLanguage.Russian.code -> "NET"
		HoneyLanguage.TraditionalChinese.code -> "NET(網絡)"
		else -> ""
	}

	@JvmField
	val buyRam = when (currentLanguage) {
		HoneyLanguage.English.code -> "BUY $ram"
		HoneyLanguage.Chinese.code -> "买入$ram"
		HoneyLanguage.Japanese.code -> "$ram を購入する"
		HoneyLanguage.Korean.code -> "$ram  구입"
		HoneyLanguage.Russian.code -> "Купить $ram"
		HoneyLanguage.TraditionalChinese.code -> "買入$ram"
		else -> ""
	}

	@JvmField
	val sellRam = when (currentLanguage) {
		HoneyLanguage.English.code -> "SELL $ram"
		HoneyLanguage.Chinese.code -> "卖出$ram"
		HoneyLanguage.Japanese.code -> "$ram を販売する"
		HoneyLanguage.Korean.code -> "$ram  판매"
		HoneyLanguage.Russian.code -> "Продать $ram"
		HoneyLanguage.TraditionalChinese.code -> "賣出$ram"
		else -> ""
	}

	@JvmField
	val available = when (currentLanguage) {
		HoneyLanguage.English.code -> "AVAILABLE"
		HoneyLanguage.Chinese.code -> "可用"
		HoneyLanguage.Japanese.code -> "利用可能"
		HoneyLanguage.Korean.code -> "가능합니다"
		HoneyLanguage.Russian.code -> "ДОСТУПНЫ"
		HoneyLanguage.TraditionalChinese.code -> "可用"
		else -> ""
	}

	@JvmField
	val total = when (currentLanguage) {
		HoneyLanguage.English.code -> "TOTAL"
		HoneyLanguage.Chinese.code -> "总计"
		HoneyLanguage.Japanese.code -> "合計"
		HoneyLanguage.Korean.code -> "총"
		HoneyLanguage.Russian.code -> "ВСЕГО"
		HoneyLanguage.TraditionalChinese.code -> "總計"
		else -> ""
	}

	@JvmField
	val inactivationAccount = when (currentLanguage) {
		HoneyLanguage.English.code -> "Inactivation Account"
		HoneyLanguage.Chinese.code -> "账户未激活"
		HoneyLanguage.Japanese.code -> "活性化していない"
		HoneyLanguage.Korean.code -> "활성화되지 않음"
		HoneyLanguage.Russian.code -> "Не активировано"
		HoneyLanguage.TraditionalChinese.code -> "賬戶未激活"
		else -> ""
	}

	//	EOS资产交易入口
	@JvmField
	val delegateCPU = when (currentLanguage) {
		HoneyLanguage.English.code -> "DELEGATE CPU\nREFUND CPU"
		HoneyLanguage.Chinese.code -> "代理CPU\n赎回CPU"
		HoneyLanguage.Japanese.code -> "デリゲートCPU\n返金CPU"
		HoneyLanguage.Korean.code -> "CPU 를 위임하십시오\nCPU 환불"
		HoneyLanguage.Russian.code -> "Делегат CPU\nВозврат CPU"
		HoneyLanguage.TraditionalChinese.code -> "代理CPU\n贖回CPU"
		else -> ""
	}

	@JvmField
	val delegateNET = when (currentLanguage) {
		HoneyLanguage.English.code -> "DELEGATE NET\nREFUND NET"
		HoneyLanguage.Chinese.code -> "代理NET\n赎回NET"
		HoneyLanguage.Japanese.code -> "デリゲートNET\n返金NET"
		HoneyLanguage.Korean.code -> "NET 를 위임하십시오\nNET 환불"
		HoneyLanguage.Russian.code -> "Делегат NET\nВозврат NET"
		HoneyLanguage.TraditionalChinese.code -> "代理NET\n贖回NET"
		else -> ""
	}

	@JvmField
	val refunds = when (currentLanguage) {
		HoneyLanguage.English.code -> "REFUND"
		HoneyLanguage.Chinese.code -> "赎回"
		HoneyLanguage.Japanese.code -> "払い戻し"
		HoneyLanguage.Korean.code -> "환불"
		HoneyLanguage.Russian.code -> "Возврат"
		HoneyLanguage.TraditionalChinese.code -> "贖回"
		else -> ""
	}

	@JvmField
	val buySellRAM = when (currentLanguage) {
		HoneyLanguage.English.code -> "BUY RAM\nSELL RAM"
		HoneyLanguage.Chinese.code -> "买入RAM\n卖出RAM"
		HoneyLanguage.Japanese.code -> "RAMを購入する\nRAMを販売する"
		HoneyLanguage.Korean.code -> "RAM 구입\nRAM  판매"
		HoneyLanguage.Russian.code -> "Купить RAM\nПродать RAM"
		HoneyLanguage.TraditionalChinese.code -> "買入RAM\n賣出RAM"
		else -> ""
	}

	//	CPU与NET租赁界面
	@JvmField
	val tradeForAccountTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Account"
		HoneyLanguage.Chinese.code -> "账户"
		HoneyLanguage.Japanese.code -> "アカウント"
		HoneyLanguage.Korean.code -> "계정"
		HoneyLanguage.Russian.code -> "Счет"
		HoneyLanguage.TraditionalChinese.code -> "賬戶"
		else -> ""
	}
	@JvmField
	val tradeForAccountPlaceholder = when (currentLanguage) {
		HoneyLanguage.English.code -> "Delegate for account"
		HoneyLanguage.Chinese.code -> "给谁代理"
		HoneyLanguage.Japanese.code -> "エージェントは誰ですか？"
		HoneyLanguage.Korean.code -> "에이전트는 누구입니까?"
		HoneyLanguage.Russian.code -> "Кто агент?"
		HoneyLanguage.TraditionalChinese.code -> "給誰代理"
		else -> ""
	}
	@JvmField
	val eosAmountTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "EOS Amount"
		HoneyLanguage.Chinese.code -> "EOS 数量"
		HoneyLanguage.Japanese.code -> "EOSの数"
		HoneyLanguage.Korean.code -> "EOS 의 수"
		HoneyLanguage.Russian.code -> "Количество EOS"
		HoneyLanguage.TraditionalChinese.code -> "EOS 數量"
		else -> ""
	}
	@JvmField
	val eosAmountPlaceholder = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter EOS amount"
		HoneyLanguage.Chinese.code -> "想要兑换的EOS数量"
		HoneyLanguage.Japanese.code -> "EOS額を入力"
		HoneyLanguage.Korean.code -> "EOS 금액 입력"
		HoneyLanguage.Russian.code -> "Введите сумму EOS"
		HoneyLanguage.TraditionalChinese.code -> "想要兌換的數量(EOS)"
		else -> ""
	}
	@JvmField
	val tradeRamByBytesTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Ram(Byte)"
		HoneyLanguage.Chinese.code -> "Ram(Byte)"
		HoneyLanguage.Japanese.code -> "Ram(Byte)"
		HoneyLanguage.Korean.code -> "Ram(Byte)"
		HoneyLanguage.Russian.code -> "Ram(Byte)"
		HoneyLanguage.TraditionalChinese.code -> "Ram(Byte)"
		else -> ""
	}
	@JvmField
	val tradeRamByBytesPlaceholder = when (currentLanguage) {
		HoneyLanguage.English.code -> "Sell bytes"
		HoneyLanguage.Chinese.code -> "卖出的RAM数量"
		HoneyLanguage.Japanese.code -> "購入したい番号"
		HoneyLanguage.Korean.code -> "구매하려는 번호"
		HoneyLanguage.Russian.code -> "Номер, который вы хотите купить"
		HoneyLanguage.TraditionalChinese.code -> "想要買的數量"
		else -> ""
	}

	// 两种代理类型
	@JvmField
	val delegateTypeTransfer = when (currentLanguage) {
		HoneyLanguage.English.code -> "Transfer"
		HoneyLanguage.Chinese.code -> "同时转让EOS"
		HoneyLanguage.Japanese.code -> "転送"
		HoneyLanguage.Korean.code -> "EOS를 동시에 전송"
		HoneyLanguage.Russian.code -> "Передача"
		HoneyLanguage.TraditionalChinese.code -> "同時轉讓EOS"
		else -> ""
	}
	@JvmField
	val delegateTypeRent = when (currentLanguage) {
		HoneyLanguage.English.code -> "Rent"
		HoneyLanguage.Chinese.code -> "仅租赁"
		HoneyLanguage.Japanese.code -> "レンタルのみ"
		HoneyLanguage.Korean.code -> "임대 전용"
		HoneyLanguage.Russian.code -> "Аренда только"
		HoneyLanguage.TraditionalChinese.code -> "僅租賃"
		else -> ""
	}

	// 资源交易的page title
	@JvmField
	val tradingCPU = when (currentLanguage) {  HoneyLanguage.English.code -> "Delegate / Refund CPU"
		HoneyLanguage.Chinese.code -> "代理/赎回CPU"
		HoneyLanguage.Japanese.code -> "デリゲート/返金CPU"
		HoneyLanguage.Korean.code -> "대의원 / 환불 CPU"
		HoneyLanguage.Russian.code -> "Торговое CPU"
		HoneyLanguage.TraditionalChinese.code -> "代理/贖回CPU"
		else -> ""
	}
	@JvmField
	val tradingNET = when (currentLanguage) {  HoneyLanguage.English.code -> "Delegate / Refund NET"
		HoneyLanguage.Chinese.code -> "代理/赎回NET"
		HoneyLanguage.Japanese.code -> "デリゲート/返金NET"
		HoneyLanguage.Korean.code -> "대의원 / 환불 NET"
		HoneyLanguage.Russian.code -> "Торговое NET"
		HoneyLanguage.TraditionalChinese.code -> "代理/贖回NET"
		else -> ""
	}
	@JvmField
	val tradingRAM = when (currentLanguage) {  HoneyLanguage.English.code -> "Buy / Sell RAM"
		HoneyLanguage.Chinese.code -> "买入/卖出RAM"
		HoneyLanguage.Japanese.code -> "購入/販売RAM"
		HoneyLanguage.Korean.code -> "구입 / 판매 CPU"
		HoneyLanguage.Russian.code -> "Купить / Продать RAM"
		HoneyLanguage.TraditionalChinese.code -> "買入/賣出RAM"
		else -> ""
	}
}

object EOSAccountText {

	@JvmField
	val pendingActivation = when (currentLanguage) {
		HoneyLanguage.English.code -> "Pending Activation"
		HoneyLanguage.Chinese.code -> "尚未激活"
		HoneyLanguage.Japanese.code -> "非アクティブ化"
		HoneyLanguage.Korean.code -> "비활성화 됨"
		HoneyLanguage.Russian.code -> "Ожидание активации"
		HoneyLanguage.TraditionalChinese.code -> "尚未激活"
		else -> ""
	}

	@JvmField
	val pendingConfirmation = when (currentLanguage) {
		HoneyLanguage.English.code -> "Pending Confirmation"
		HoneyLanguage.Chinese.code -> "等待确认账户"
		HoneyLanguage.Japanese.code -> "確認勘定を待っている"
		HoneyLanguage.Korean.code -> "대기중인 확인"
		HoneyLanguage.Russian.code -> "Ожидание подтверждения"
		HoneyLanguage.TraditionalChinese.code -> "等待確認賬戶"
		else -> ""
	}

	@JvmField
	val publicKey = when (currentLanguage) {
		HoneyLanguage.English.code -> "Public key"
		HoneyLanguage.Chinese.code -> "公钥"
		HoneyLanguage.Japanese.code -> "公開鍵"
		HoneyLanguage.Korean.code -> "공개 키"
		HoneyLanguage.Russian.code -> "Открытый ключ"
		HoneyLanguage.TraditionalChinese.code -> "公鑰"
		else -> ""
	}

	//	权限管理
	@JvmField
	val authority = when (currentLanguage) {
		HoneyLanguage.English.code -> "Authority Management"
		HoneyLanguage.Chinese.code -> "权限管理"
		HoneyLanguage.Japanese.code -> "権限管理"
		HoneyLanguage.Korean.code -> "권한 관리"
		HoneyLanguage.Russian.code -> "Управление полномочиями"
		HoneyLanguage.TraditionalChinese.code -> "權限管理"
		else -> ""
	}


	@JvmField
	val accountNameSelection = when (currentLanguage) {
		HoneyLanguage.English.code -> "Account Manage"
		HoneyLanguage.Chinese.code -> "账户管理"
		HoneyLanguage.Japanese.code -> "アカウント管理"
		HoneyLanguage.Korean.code -> "계정 관리"
		HoneyLanguage.Russian.code -> "Счета"
		HoneyLanguage.TraditionalChinese.code -> "賬戶管理"
		else -> ""
	}

	@JvmField
	val activationMethod = when (currentLanguage) {
		HoneyLanguage.English.code -> "Inactivation Account"
		HoneyLanguage.Chinese.code -> "账户未激活"
		HoneyLanguage.Japanese.code -> "活性化していない"
		HoneyLanguage.Korean.code -> "활성화되지 않음"
		HoneyLanguage.Russian.code -> "Не активировано"
		HoneyLanguage.TraditionalChinese.code -> "賬戶未激活"
		else -> ""
	}

	// 三种激活方式
	@JvmField
	val activeByFriend = when (currentLanguage) {
		HoneyLanguage.English.code -> "Activate via an existing account"
		HoneyLanguage.Chinese.code -> "找好友为我激活"
		HoneyLanguage.Japanese.code -> "既存のアカウントで有効化"
		HoneyLanguage.Korean.code -> "나를 위해 활성화 할 친구 찾기"
		HoneyLanguage.Russian.code -> "Найти друга для активации для меня"
		HoneyLanguage.TraditionalChinese.code -> "找好友為我激活"
		else -> ""
	}
	@JvmField
	val activeByContract = when (currentLanguage) {
		HoneyLanguage.English.code -> "Activate via EOS transfer"
		HoneyLanguage.Chinese.code -> "通过EOS转账进行激活"
		HoneyLanguage.Japanese.code -> "EOS転送による有効化"
		HoneyLanguage.Korean.code -> "EOS 전송을 통해 활성화"
		HoneyLanguage.Russian.code -> "Активировать путем передачи"
		HoneyLanguage.TraditionalChinese.code -> "通過EOS轉賬進行激活"
		else -> ""
	}
	@JvmField
	val activeManually = when (currentLanguage) {
		HoneyLanguage.English.code -> "Copy public key"
		HoneyLanguage.Chinese.code -> "手动激活 点击复制公钥"
		HoneyLanguage.Japanese.code -> "公開鍵をコピーする"
		HoneyLanguage.Korean.code -> "수동 활성화, 공개 키 복사"
		HoneyLanguage.Russian.code -> "Копировать открытый ключ"
		HoneyLanguage.TraditionalChinese.code -> "手動激活 點擊複製公鑰"
		else -> ""
	}

	// 账户未激活的界面提示
	@JvmField
	val inactivationAccount = when (currentLanguage) {
		HoneyLanguage.English.code -> "Account is not activated"
		HoneyLanguage.Chinese.code -> "账户尚未激活"
		HoneyLanguage.Japanese.code -> "アカウントは有効"
		HoneyLanguage.Korean.code -> "계정이 활성화되지 않았습니다."
		HoneyLanguage.Russian.code -> "Аккаунт не активирован"
		HoneyLanguage.TraditionalChinese.code -> "賬戶尚未激活"
		else -> ""
	}
	@JvmField
	val inactivationAccountHint = when (currentLanguage) {
		HoneyLanguage.English.code -> "According to the rules of the EOS network, a certain amount of Resource(RAM/NET/CPU) needs to be paid when a new account is generated. Because new users don't have resource yet, they need to rely on someone who already has EOS account to help to pay for resources. \n\nIf you already have an EOS account, or if you have a trusted friend who is willing to help you to create an account, you can choose '$activeByFriend' \n\n Or you can choose '$activeByContract'. In this option, you can transfer a certain number of EOS from your own exchange or wallet to the contract, and the contract will automatically perform account activation for you. \n\nPlease do not reveal your private key to anyone."
		HoneyLanguage.Chinese.code -> "根据EOS网络的规则，新的账号生成时需要消耗一定数量的资源（RAM/NET/CPU）。因为新用户还没有这些资源，所以需要依赖已经有EOS的人帮助你来支付。\n\n如果你已经拥有一个EOS账号，或是有一个可信赖的并愿意帮助你创建账号的朋友，你可以选择「$activeByFriend」\n\n你也可以选择「$activeByContract 」。在此选项中，你可以从自己的交易所或钱包转出一定数目的EOS到合约中，合约将自动为您执行账户激活。\n\n请不要向任何人泄露您的私钥。"
		HoneyLanguage.Japanese.code -> "EOSネットワークのルールによれば、新しいアカウントは一定量のリソース（RAM / NET / CPU）を消費する必要があります。 新規ユーザーはまだこれらのリソースを持っていないため、お支払いに役立つEOSを既に持っている人に頼る必要があります。 \n\n既にEOSアカウントをお持ちの場合、またはアカウントの作成をお手伝いする信頼できる友人がいる場合は、「$activeByFriend」\n\n「${activeByContract}」のオプションでは、自分の交換やウォレットから一定数のEOSを契約に転送することができ、契約によって自動的にアカウントの有効化が行われます。 \n\n秘密鍵を誰にも公開しないでください。"
		HoneyLanguage.Korean.code -> "EOS 네트워크의 규칙에 따르면 새로운 계정은 일정량의 리소스 (RAM / NET / CPU)를 소비해야합니다. 신규 사용자는 아직 이러한 리소스가 없으므로 이미 EOS를 보유한 사람이 비용을 지불해야합니다. \n\n이미 EOS 계정을 가지고 있거나 친구를 사귈 수있는 신뢰할 수있는 친구가있는 경우 친구, \"$activeByFriend\"n을 선택할 수 있습니다. \n\n\"$activeByContract\"를 선택할 수도 있습니다.이 옵션을 사용하면 교환 또는 지갑에서 계약 금액으로 EOS를 전송할 수 있으며 계약이 자동으로 활성화됩니다. 귀하의 계정. \n\n다른 사람에게 비공개 키를 공개하지 마십시오."
		HoneyLanguage.Russian.code -> "Согласно правилам сети EOS, новая учетная запись должна потреблять определенный объем ресурсов (RAM / NET / CPU). \n\nЕсли у вас уже есть учетная запись EOS или у вас есть доверенный друг, который хочет помочь вам создать учетную запись, вы можете выбрать 'activeByFriend' \n\nили вы можете выбрать '$activeByContract', / Передача кошелька активирована. \" В этом случае вы можете перенести определенное количество EOS с вашей собственной биржи или кошелька на контракт, и контракт автоматически выполнит активацию учетной записи для вас. \n\nПожалуйста, не раскрывайте свой личный ключ никому."
		HoneyLanguage.TraditionalChinese.code -> "根據EOS網絡的規則，新的賬號生成時需要消耗一定數量的資源（RAM/NET/CPU）。因為新用戶還沒有這些資源，所以需要依賴已經有EOS的人幫助你來支付。 \\n\\n如果你已經擁有一個EOS賬號，或是有一個可信賴的並願意幫助你創建賬號的朋友，你可以選擇「$activeByFriend」\\n\\n你也可以選擇「$activeByContract」。在此選項中，你可以從自己的交易所或錢包轉出一定數目的EOS到合約中，合約將自動為您執行賬戶激活。 \n\n請不要向任何人洩露您的私鑰。"
		else -> ""
	}

	//	好友激活
	@JvmField
	val activeByFriendHint = when (currentLanguage) {
		HoneyLanguage.English.code -> "The steps for a friend to help activate the EOS account are as follows: \n\n1. Set a account name (12-letter, consisting of the number 1~5 or English characters). Check whether it is available on chain. 2. Copy the public key below. Give Your account name and public key to a trusted friend who already have an EOS account\n3. Friends in the Settings > EOS Account to register \n4. After registration is completed, re-enter the EOS asset interface, GoldStone will update your status"
		HoneyLanguage.Chinese.code -> "好友帮忙激活EOS账号步骤如下: \n\n1. 起一个用户名(12位字母，由数字1~5或英文字符组成)，在本页面检测是否可用\n2. 复制下方公钥，将你的用户名和公钥给已拥有EOS账号的好友\n3. 好友在「设置>EOS账号」中按步骤进行注册\n4. 好友注册完成后，重新进入EOS资产界面，GoldStone会为您更状态"
		HoneyLanguage.Japanese.code -> "\n \n1.ユーザー名（1〜5文字または英字で構成された12文字）を開始するこのページで\n2. 利用可能かどうかを確認する下記の公開キーをコピーし、あなたのユーザー名と公開鍵は、既にEOSアカウントを持っている友達に与えられます。\n3. 設定> EOSアカウントの友達は、ステップごとに\n4登録してください。あなたのステータス"
		HoneyLanguage.Korean.code -> "친구 계정 활성화 EOS 단계 도움 : 사용자 이름에서 \n \n1 (12 글자, 한 5 영어 문자에 숫자를),이 페이지는 것이다 검출 가능합니다 \n2 아래의 공개 키를 복사합니다. 단계별로 사용자 이름과 공개 키가 이미 등록에서 설정> EOS 계정에 EOS 친구 \n 3. 의 계정. 친구가 \n4 단계. 친구 등록이 완료되면 EOS 자산 인터페이스를 다시 입력, 골드 스톤은 제공 할 것입니다 내 상태"
		HoneyLanguage.Russian.code -> "Шаги для друга, чтобы помочь активировать учетную запись EOS, следующие: \n \n1. Начните имя пользователя (12-буквенное, состоящее из цифр 1 ~ 5 или английских символов). Проверьте, доступен ли \n2 на этой странице. Скопируйте открытый ключ ниже, Ваше имя пользователя и открытый ключ предоставляются друзьям, у которых уже есть учетная запись EOS \n3. Друзья в «Настройки> EOS-учетной записи» шаг за шагом регистрируют \n4. После того, как регистрация друга завершена, повторно введите интерфейс EOS-активов, GoldStone будет Ваш статус"
		HoneyLanguage.TraditionalChinese.code -> "好友幫忙激活EOS賬號步驟如下: \n\n1. 起一個用戶名(12位字母，由數字1~5或英文字符組成)，在本頁面檢測是否可用\n2. 複製下方公鑰，將你的用戶名和公鑰給已擁有EOS賬號的好友\n3. 好友在「設置>EOS賬號」中按步驟進行註冊\n4. 好友註冊完成後，重新進入EOS資產界面，GoldStone會為您更狀態"
		else -> ""
	}
	@JvmField
	val copyPublicKey = when (currentLanguage) {
		HoneyLanguage.English.code -> "Click to copy public key"
		HoneyLanguage.Chinese.code -> "点击下方复制公钥"
		HoneyLanguage.Japanese.code -> "公開鍵をコピーするには、以下をクリックしてください"
		HoneyLanguage.Korean.code -> "공개 키를 복사하려면 아래를 클릭하십시오."
		HoneyLanguage.Russian.code -> "Нажмите ниже, чтобы скопировать открытый ключ"
		HoneyLanguage.TraditionalChinese.code -> "點擊下方復制公鑰"
		else -> ""
	}
	@JvmField
	val checkNameAvailability = when (currentLanguage) {
		HoneyLanguage.English.code -> "Check if account name is available"
		HoneyLanguage.Chinese.code -> "检测用户名是否在链上可用"
		HoneyLanguage.Japanese.code -> "チェーンでユーザー名の可用性の検出"
		HoneyLanguage.Korean.code -> "사용자 이름을 사용할 수 있는지 확인하십시오."
		HoneyLanguage.Russian.code -> "Проверьте, доступно ли имя пользователя"
		HoneyLanguage.TraditionalChinese.code -> "查看用戶名是否可用"
		else -> ""
	}
	@JvmField
	val checkNameResultAvailable = when (currentLanguage) {
		HoneyLanguage.English.code -> "Congratulations, this name has not yet been registered by others. If you have a friend using GoldStone Wallet, he/she could go to \"Settings > Register EOS Accounts\" to register with the following information."
		HoneyLanguage.Chinese.code -> "恭喜，此名称尚未被其他人注册。 如果您有朋友使用GoldStone钱包，他/她可以转到设置界面的注册EOS帐户模块，并注册以下信息。"
		HoneyLanguage.Japanese.code -> "おめでとう、この名前はまだ誰かに登録されていません。 あなたは、Goldstone Walletを使用している友人がいる場合、[設定 > EOSアカウントの登録]にアクセスして、次の情報を登録できます。"
		HoneyLanguage.Korean.code -> "축하합니다.이 이름은 아직 다른 사람이 등록하지 않았습니다. 골드 스톤 월렛을 사용하는 친구가있는 경우 설정 화면의 EOS 계정 등록 모듈로 이동하여 다음 정보를 등록 할 수 있습니다."
		HoneyLanguage.Russian.code -> "Поздравляю, это имя еще не зарегистрировано кем-то другим. Если у вас есть друг с помощью GoldStone Wallet, он может перейти в модуль учетных записей EOS на экране настроек и зарегистрировать следующую информацию."
		HoneyLanguage.TraditionalChinese.code -> "恭喜，此名稱尚未被其他人註冊。如果您有朋友使用GoldStone錢包，他/她可以轉到設置界面的註冊EOS帳戶模塊，並註冊以下信息。"
		else -> ""
	}
	@JvmField
	val checkNameResultValid = when (currentLanguage) {
		HoneyLanguage.English.code -> "Correct format"
		HoneyLanguage.Chinese.code -> "格式正确"
		HoneyLanguage.Japanese.code -> "正しい形式"
		HoneyLanguage.Korean.code -> "올바른 형식"
		HoneyLanguage.Russian.code -> "Правильный формат"
		HoneyLanguage.TraditionalChinese.code -> "格式正確"
		else -> ""
	}
	@JvmField
	val checkNameResultUnavailable = when (currentLanguage) {
		HoneyLanguage.English.code -> "The account name has already been registered by someone else, change it."
		HoneyLanguage.Chinese.code -> "该用户名已经被别人注册，换一个吧"
		HoneyLanguage.Japanese.code -> "ユーザー名は既にバーのために、他の人が登録されています"
		HoneyLanguage.Korean.code -> "사용자 이름이 이미 다른 사람이 등록한 경우 변경하십시오."
		HoneyLanguage.Russian.code -> "Имя пользователя уже зарегистрировано кем-то другим, измените его."
		HoneyLanguage.TraditionalChinese.code -> "該用戶名已經被別人註冊，換一個吧"
		else -> ""
	}
	@JvmField
	val checkNameResultEmpty = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please input account name"
		HoneyLanguage.Chinese.code -> "请输入账户名"
		HoneyLanguage.Japanese.code -> "アカウント名を入力してください"
		HoneyLanguage.Korean.code -> "계정 이름을 입력하십시오."
		HoneyLanguage.Russian.code -> "Введите имя учетной записи"
		HoneyLanguage.TraditionalChinese.code -> "請輸入賬戶名"
		else -> ""
	}
	@JvmField
	val checkNameResultInvalid = when (currentLanguage) {
		HoneyLanguage.English.code -> "Invalid account name. Only 1~5 and English letters can be used, and only 12 characters"
		HoneyLanguage.Chinese.code -> "用户名格式不合规，只能用1~5与英文字母，且只能12字符"
		HoneyLanguage.Japanese.code -> "アカウント名が無効です.1〜5文字と英字のみが使用でき、12文字しか使用できません"
		HoneyLanguage.Korean.code -> "계정 이름이 잘못되었습니다. 1 ~ 5 자 및 영어 글자 만 사용할 수 있으며 12 자만 사용할 수 있습니다."
		HoneyLanguage.Russian.code -> "Формат имени пользователя несовместим, только 1 ~ 5 и английские буквы могут использоваться, и только 12 символов"
		HoneyLanguage.TraditionalChinese.code -> "用戶名格式不合規，只能用1~5與英文字母，且只能12字符"
		else -> ""
	}

	//	合约激活
	@JvmField
	val activeByContractHint = when (currentLanguage) {
		HoneyLanguage.English.code -> "Contract registration was created using a smart contract deployed by the GoldStone team, with no manual action during the creation process. Once property damage is caused by improper operation, it will not be recovered."
		HoneyLanguage.Chinese.code -> "合约注册是使用 GoldStone 团队部署的智能合约进行创建的，在创建过程中没有任何人工操作。一旦因不当操作导致财产损失，将无法追回。"
		HoneyLanguage.Japanese.code -> "契約登録は、GoldStoneチームによって導入されたスマート契約を使用して作成されました。 物的損害が不適切な操作によって引き起こされた場合、それは回復されません。"
		HoneyLanguage.Korean.code -> "계약 등록은 GoldStone 팀이 배포 한 스마트 계약을 사용하여 생성되었으며 생성 프로세스 중에는 수동 작업이 필요하지 않습니다. 재산상의 손해는 부적절한 조작으로 인해 야기 된 후에는 복구되지 않습니다."
		HoneyLanguage.Russian.code -> "Регистрация контракта была создана с использованием интеллектуального контракта, развернутого командой GoldStone, без ручного действия в процессе создания. Если повреждение имущества вызвано неправильной работой, оно не будет восстановлено."
		HoneyLanguage.TraditionalChinese.code -> "合約註冊是使用 GoldStone 團隊部署的智能合約進行創建的，在創建過程中沒有任何人工操作。一旦因不當操作導致財產損失，將無法追回。"
		else -> ""
	}
	@JvmField
	val activeByContractSpendDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "Among the EOS you sent to contract, 0.1EOS is used to mortgage the CPU, 0.1EOS is used to mortgage NET, and the remaining EOS will be used to purchase memory. All those resources are owned by your account, both contract and GoldStone do not charge any fees. \nPlease confirm that your exchange can make ordinary transfers with notes. It has been confirmed that OTCBTC, Gate.io, Coin Security, and Bitfinex support this method."
		HoneyLanguage.Chinese.code -> "你转入的EOS中 0.1EOS用于抵押CPU，0.1EOS用于抵押NET，剩余EOS将用于购买内存。这三种资源都为你的账户所有，合约和GoldStone不收取任何费用。\n请确认你的交易所可以进行带备注的普通转账，目前已确认OTCBTC、Gate.io、币安、Bitfinex支持此种方式。"
		HoneyLanguage.Japanese.code -> "0.1EOSに転送するEOSではCPUを抵当に、0.1EOSでNETを、残りのEOSでメモリを購入します。 3つのリソースはすべてお客様のアカウントで所有されており、契約とGoldStoneは料金を請求しません。 \n OTCBTC、Gate.io、Coin Security、Bitfinexがこの方法をサポートしていることが確認されています。"
		HoneyLanguage.Korean.code -> "EOS에서 0.1EOS로 이전하면 CPU를 모기지로 사용하고, 0.1EOS는 NET을 모기지로 사용하고 나머지 EOS는 메모리를 구매하는 데 사용됩니다. 세 가지 리소스는 모두 귀하의 계정에 의해 소유되며 계약과 GoldStone은 수수료를 부과하지 않습니다. \n 귀하의 거래소가 메모로 일반 이체를 할 수 있는지 확인하십시오 .OTCBTC, Gate.io, Coin Security 및 Bitfinex가이 방법을 지원함이 확인되었습니다."
		HoneyLanguage.Russian.code -> "В EOS вы переходите на 0.1EOS, чтобы закладывать CPU, 0.1EOS используется для ипотеки NET, а оставшаяся EOS будет использоваться для покупки памяти. Все три ресурса принадлежат вашей учетной записи, а контракт и GoldStone не взимают никаких комиссий. \nПожалуйста, подтвердите, что ваш обмен может делать обычные переводы с заметками. Было подтверждено, что OTCBTC, Gate.io, Coin Security и Bitfinex поддерживают этот метод."
		HoneyLanguage.TraditionalChinese.code -> "你轉入的EOS中 0.1EOS用於抵押CPU，0.1EOS用於抵押NET，剩余EOS將用於購買內存。這三種資源都為你的賬戶所有，合約和GoldStone不收取任何費用。\n請確認你的交易所可以進行帶備註的普通轉賬，目前已確認OTCBTC、Gate.io、幣安、Bitfinex支持此種方式。"
		else -> ""
	}
	@JvmField
	val estimatedSpentOfActiveAccount = when (currentLanguage) {
		HoneyLanguage.English.code -> "Estimated cost"
		HoneyLanguage.Chinese.code -> "预估花费"
		HoneyLanguage.Japanese.code -> "見積もり原価"
		HoneyLanguage.Korean.code -> "예상 비용"
		HoneyLanguage.Russian.code -> "Ориентировочная стоимость"
		HoneyLanguage.TraditionalChinese.code -> "預估花費"
		else -> ""
	}
	@JvmField
	val activeByContractMethod: (contract: String) -> String = { contract ->
		when (currentLanguage) {
			HoneyLanguage.English.code -> "Transfer at least 2.0 EOS to the contract account ($contract) from your exchange/ wallet, with memo."
			HoneyLanguage.Chinese.code -> "通过交易所提币并备注/从钱包转账并备注的方式向合约账号($contract)转入至少2.0 EOS"
			HoneyLanguage.Japanese.code -> "取引所のコインとノート/送金とウォレットからのメモを使用して、少なくとも2.0 EOSを契約口座に転送する（$contract）"
			HoneyLanguage.Korean.code -> "거래소의 동전과 노트 / 송금 및 지갑에서 노트를 통해 계약 계정 ($contract)으로 최소한 2.0 EOS를 이체하십시오."
			HoneyLanguage.Russian.code -> "Передайте по крайней мере 2,0 EOS на контрактную учетную запись ($contract) с помощью монеты обмена и банкноты / банкноты и записку из кошелька"
			HoneyLanguage.TraditionalChinese.code -> "通過交易所提幣並備註/從錢包轉賬並備註的方式向合約賬號($contract)轉入至少2.0 EOS"
			else -> ""
		}
	}
	@JvmField
	val smartContract = when (currentLanguage) {
		HoneyLanguage.English.code -> "Smart contract code address"
		HoneyLanguage.Chinese.code -> "智能合约代码开源地址"
		HoneyLanguage.Japanese.code -> "スマート契約コードオープンソースアドレス"
		HoneyLanguage.Korean.code -> "스마트 계약 코드 오픈 소스 주소"
		HoneyLanguage.Russian.code -> "Смарт-код контракта с открытым исходным кодом"
		HoneyLanguage.TraditionalChinese.code -> "智能合約代碼開源地址"
		else -> ""
	}
	@JvmField
	val transferTo = when (currentLanguage) {
		HoneyLanguage.English.code -> "Transfer to account"
		HoneyLanguage.Chinese.code -> "转入账户"
		HoneyLanguage.Japanese.code -> "アカウントに転送"
		HoneyLanguage.Korean.code -> "계정으로 이전"
		HoneyLanguage.Russian.code -> "Перевод на счет"
		HoneyLanguage.TraditionalChinese.code -> "轉入賬戶"
		else -> ""
	}
	@JvmField
	val receiver = when (currentLanguage) {
		HoneyLanguage.English.code -> "Receiver"
		HoneyLanguage.Chinese.code -> "接收方"
		HoneyLanguage.Japanese.code -> "受信者"
		HoneyLanguage.Korean.code -> "수신자"
		HoneyLanguage.Russian.code -> "Получатели"
		HoneyLanguage.TraditionalChinese.code -> "接收方"
		else -> ""
	}
	@JvmField
	val memoInfo = when (currentLanguage) {
		HoneyLanguage.English.code -> "Memo Information"
		HoneyLanguage.Chinese.code -> "备注信息"
		HoneyLanguage.Japanese.code -> "備考情報"
		HoneyLanguage.Korean.code -> "비고"
		HoneyLanguage.Russian.code -> "Замечания информация"
		HoneyLanguage.TraditionalChinese.code -> "備註信息"
		else -> ""
	}
	@JvmField
	val copyResult = when (currentLanguage) {
		HoneyLanguage.English.code -> "copy memo information"
		HoneyLanguage.Chinese.code -> "复制备注信息"
		HoneyLanguage.Japanese.code -> "備考情報をコピー"
		HoneyLanguage.Korean.code -> "준비 메모"
		HoneyLanguage.Russian.code -> "Замечание по подготовке"
		HoneyLanguage.TraditionalChinese.code -> "復制備註信息"
		else -> ""
	}

	// 为好友注册功能
	@JvmField
	val activateForFriendHint = when (currentLanguage) {
		HoneyLanguage.English.code -> "Account name must be 12 characters. It can only be composed of number 1~5 or English letters."
		HoneyLanguage.Chinese.code -> "账户名必须为12个字符。且只能用1~5或英文字母组成。"
		HoneyLanguage.Japanese.code -> "アカウント名は12文字でなければなりません。 1〜5文字または英字のみで構成することができます。"
		HoneyLanguage.Korean.code -> "계정 이름은 12 자 여야합니다. 1 ~ 5 자 또는 영문자로만 구성 할 수 있습니다."
		HoneyLanguage.Russian.code -> "Имя учетной записи должно быть 12 символов. Он может состоять только из 1 ~ 5 или английских букв."
		HoneyLanguage.TraditionalChinese.code -> "賬戶名必須為12個字符。且只能用1~5或英文字母組成"
		else -> ""
	}
	@JvmField
	val advancedSettings = when (currentLanguage) {
		HoneyLanguage.English.code -> "Advanced Settings"
		HoneyLanguage.Chinese.code -> "高级设置"
		HoneyLanguage.Japanese.code -> "詳細設定"
		HoneyLanguage.Korean.code -> "고급 설정"
		HoneyLanguage.Russian.code -> "Расширенные настройки"
		HoneyLanguage.TraditionalChinese.code -> "高級設置"
		else -> ""
	}
	@JvmField
	val customizeResource = when (currentLanguage) {
		HoneyLanguage.English.code -> "Custom resource"
		HoneyLanguage.Chinese.code -> "自定义资源分配"
		HoneyLanguage.Japanese.code -> "カスタムリソース"
		HoneyLanguage.Korean.code -> "맞춤 리소스"
		HoneyLanguage.Russian.code -> "Пользовательский ресурс"
		HoneyLanguage.TraditionalChinese.code -> "自定義資源"
		else -> ""
	}

	//	EOS账户权限
	@JvmField
	val multipleAccountHint = when (currentLanguage) {
		HoneyLanguage.English.code -> "Find multiple EOS accounts with this EOS public key, please select an account as your default account."
		HoneyLanguage.Chinese.code -> "在此EOS公钥下找到多个EOS帐户，请选择一个帐户作为您的默认帐户。"
		HoneyLanguage.Japanese.code -> "このEOS公開鍵で複数のEOSアカウントを検索するには、デフォルトアカウントとしてアカウントを選択してください。"
		HoneyLanguage.Korean.code -> "이 EOS 공개 키로 여러 개의 EOS 계정을 찾으려면 계정을 기본 계정으로 선택하십시오."
		HoneyLanguage.Russian.code -> "Чтобы найти несколько учетных записей EOS под этим открытым ключом EOS, выберите учетную запись в качестве учетной записи по умолчанию."
		HoneyLanguage.TraditionalChinese.code -> "在此EOS公鑰下找到多個EOS帳戶，請選擇一個帳戶作為您的默認帳戶。"
		else -> ""
	}

	@JvmField
	val loadingAccountInfo = when (currentLanguage) {
		HoneyLanguage.English.code -> "Loading account information from chain."
		HoneyLanguage.Chinese.code -> "正在从链上加载账户信息"
		HoneyLanguage.Japanese.code -> "チェーンからのアカウント情報の読み込み"
		HoneyLanguage.Korean.code -> "체인에서 계정 정보로드"
		HoneyLanguage.Russian.code -> "Загрузка информации о счете из сети"
		HoneyLanguage.TraditionalChinese.code -> "正在從鏈上加載賬戶信息"
		else -> ""
	}

	@JvmField
	val permissionActive = when (currentLanguage) {
		HoneyLanguage.English.code -> "Active"
		HoneyLanguage.Chinese.code -> "Active(管理者)"
		HoneyLanguage.Japanese.code -> "Active(管理者)"
		HoneyLanguage.Korean.code -> "Active(활성)"
		HoneyLanguage.Russian.code -> "Active(Активный)"
		HoneyLanguage.TraditionalChinese.code -> "Active(管理者)"
		else -> ""
	}
	@JvmField
	val permissionOwner = when (currentLanguage) {
		HoneyLanguage.English.code -> "Owner"
		HoneyLanguage.Chinese.code -> "Owner(所有者)"
		HoneyLanguage.Japanese.code -> "所有者（所有者）"
		HoneyLanguage.Korean.code -> "Owner (소유자)"
		HoneyLanguage.Russian.code -> "Owner(Владелец)"
		HoneyLanguage.TraditionalChinese.code -> "Owner(所有者)"
		else -> ""
	}
	@JvmField
	val authorization = when (currentLanguage) {
		HoneyLanguage.English.code -> "Permission"
		HoneyLanguage.Chinese.code -> "权限"
		HoneyLanguage.Japanese.code -> "許可"
		HoneyLanguage.Korean.code -> "허가"
		HoneyLanguage.Russian.code -> "компетентность"
		HoneyLanguage.TraditionalChinese.code -> "權限"
		else -> ""
	}

}