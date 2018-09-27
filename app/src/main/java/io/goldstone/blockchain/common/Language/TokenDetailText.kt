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
	@JvmField
	val resources = when (currentLanguage) {
		HoneyLanguage.English.code -> "Resources"
		HoneyLanguage.Chinese.code -> "Resources"
		HoneyLanguage.Japanese.code -> "Resources"
		HoneyLanguage.Korean.code -> "Resources"
		HoneyLanguage.Russian.code -> "Resources"
		HoneyLanguage.TraditionalChinese.code -> "Resources"
		else -> ""
	}
	@JvmField
	val accountManagement = when (currentLanguage) {
		HoneyLanguage.English.code -> "Account Management"
		HoneyLanguage.Chinese.code -> "Account Management"
		HoneyLanguage.Japanese.code -> "Account Management"
		HoneyLanguage.Korean.code -> "Account Management"
		HoneyLanguage.Russian.code -> "Account Management"
		HoneyLanguage.TraditionalChinese.code -> "Account Management"
		else -> ""
	}
	@JvmField
	val assetTools = when (currentLanguage) {
		HoneyLanguage.English.code -> "Asset Tools"
		HoneyLanguage.Chinese.code -> "Asset Tools"
		HoneyLanguage.Japanese.code -> "Asset Tools"
		HoneyLanguage.Korean.code -> "Asset Tools"
		HoneyLanguage.Russian.code -> "Asset Tools"
		HoneyLanguage.TraditionalChinese.code -> "Asset Tools"
		else -> ""
	}
	@JvmField
	val delegateTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "DELEGATE"
		HoneyLanguage.Chinese.code -> "DELEGATE"
		HoneyLanguage.Japanese.code -> "DELEGATE"
		HoneyLanguage.Korean.code -> "DELEGATE"
		HoneyLanguage.Russian.code -> "DELEGATE"
		HoneyLanguage.TraditionalChinese.code -> "DELEGATE"
		else -> ""
	}
	@JvmField
	val refundTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "REFUND"
		HoneyLanguage.Chinese.code -> "REFUND"
		HoneyLanguage.Japanese.code -> "REFUND"
		HoneyLanguage.Korean.code -> "REFUND"
		HoneyLanguage.Russian.code -> "REFUND"
		HoneyLanguage.TraditionalChinese.code -> "REFUND"
		else -> ""
	}
	@JvmField
	val ram = when (currentLanguage) {
		HoneyLanguage.English.code -> "RAM"
		HoneyLanguage.Chinese.code -> "RAM"
		HoneyLanguage.Japanese.code -> "RAM"
		HoneyLanguage.Korean.code -> "RAM"
		HoneyLanguage.Russian.code -> "RAM"
		HoneyLanguage.TraditionalChinese.code -> "RAM"
		else -> ""
	}
	@JvmField
	val cpu = when (currentLanguage) {
		HoneyLanguage.English.code -> "CPU"
		HoneyLanguage.Chinese.code -> "CPU"
		HoneyLanguage.Japanese.code -> "CPU"
		HoneyLanguage.Korean.code -> "CPU"
		HoneyLanguage.Russian.code -> "CPU"
		HoneyLanguage.TraditionalChinese.code -> "CPU"
		else -> ""
	}
	@JvmField
	val net = when (currentLanguage) {
		HoneyLanguage.English.code -> "NET"
		HoneyLanguage.Chinese.code -> "NET"
		HoneyLanguage.Japanese.code -> "NET"
		HoneyLanguage.Korean.code -> "NET"
		HoneyLanguage.Russian.code -> "NET"
		HoneyLanguage.TraditionalChinese.code -> "NET"
		else -> ""
	}
	@JvmField
	val authority = when (currentLanguage) {
		HoneyLanguage.English.code -> "Authority Management"
		HoneyLanguage.Chinese.code -> "Authority Management"
		HoneyLanguage.Japanese.code -> "Authority Management"
		HoneyLanguage.Korean.code -> "Authority Management"
		HoneyLanguage.Russian.code -> "Authority Management"
		HoneyLanguage.TraditionalChinese.code -> "Authority Management"
		else -> ""
	}
	@JvmField
	val delegateCPU = when (currentLanguage) {
		HoneyLanguage.English.code -> "DELEGATE CPU\nREFUND CPU"
		HoneyLanguage.Chinese.code -> "DELEGATE CPU\nREFUND CPU"
		HoneyLanguage.Japanese.code -> "DELEGATE CPU\nREFUND CPU"
		HoneyLanguage.Korean.code -> "DELEGATE CPU\nREFUND CPU"
		HoneyLanguage.Russian.code -> "DELEGATE CPU\nREFUND CPU"
		HoneyLanguage.TraditionalChinese.code -> "DELEGATE CPU\nREFUND CPU"
		else -> ""
	}
	@JvmField
	val delegateNET = when (currentLanguage) {
		HoneyLanguage.English.code -> "DELEGATE NET\nREFUND NET"
		HoneyLanguage.Chinese.code -> "DELEGATE NET\nREFUND NET"
		HoneyLanguage.Japanese.code -> "DELEGATE NET\nREFUND NET"
		HoneyLanguage.Korean.code -> "DELEGATE NET\nREFUND NET"
		HoneyLanguage.Russian.code -> "DELEGATE NET\nREFUND NET"
		HoneyLanguage.TraditionalChinese.code -> "DELEGATE NET\nREFUND NET"
		else -> ""
	}
	@JvmField
	val tradeRAM = when (currentLanguage) {
		HoneyLanguage.English.code -> "BUY RAM\nSELL RAM"
		HoneyLanguage.Chinese.code -> "BUY RAM\nSELL RAM"
		HoneyLanguage.Japanese.code -> "BUY RAM\nSELL RAM"
		HoneyLanguage.Korean.code -> "BUY RAM\nSELL RAM"
		HoneyLanguage.Russian.code -> "BUY RAM\nSELL RAM"
		HoneyLanguage.TraditionalChinese.code -> "BUY RAM\nSELL RAM"
		else -> ""
	}

	@JvmField
	val available = when (currentLanguage) {
		HoneyLanguage.English.code -> "AVAILABLE"
		HoneyLanguage.Chinese.code -> "AVAILABLE"
		HoneyLanguage.Japanese.code -> "AVAILABLE"
		HoneyLanguage.Korean.code -> "AVAILABLE"
		HoneyLanguage.Russian.code -> "AVAILABLE"
		HoneyLanguage.TraditionalChinese.code -> "AVAILABLE"
		else -> ""
	}

	@JvmField
	val total = when (currentLanguage) {
		HoneyLanguage.English.code -> "TOTAL"
		HoneyLanguage.Chinese.code -> "TOTAL"
		HoneyLanguage.Japanese.code -> "TOTAL"
		HoneyLanguage.Korean.code -> "TOTAL"
		HoneyLanguage.Russian.code -> "TOTAL"
		HoneyLanguage.TraditionalChinese.code -> "TOTAL"
		else -> ""
	}

	@JvmField
	val chainType = when (currentLanguage) {
		HoneyLanguage.English.code -> "CHAIN TYPE"
		HoneyLanguage.Chinese.code -> "CHAIN TYPE"
		HoneyLanguage.Japanese.code -> "CHAIN TYPE"
		HoneyLanguage.Korean.code -> "CHAIN TYPE"
		HoneyLanguage.Russian.code -> "CHAIN TYPE"
		HoneyLanguage.TraditionalChinese.code -> "CHAIN TYPE"
		else -> ""
	}

	@JvmField
	val checkDetail = when (currentLanguage) {
		HoneyLanguage.English.code -> "CHECK DETAIL"
		HoneyLanguage.Chinese.code -> "CHECK DETAIL"
		HoneyLanguage.Japanese.code -> "CHECK DETAIL"
		HoneyLanguage.Korean.code -> "CHECK DETAIL"
		HoneyLanguage.Russian.code -> "CHECK DETAIL"
		HoneyLanguage.TraditionalChinese.code -> "CHECK DETAIL"
		else -> ""
	}

	@JvmField
	val latestActivationTime = when (currentLanguage) {
		HoneyLanguage.English.code -> "this account's latest active time is"
		HoneyLanguage.Chinese.code -> "this account's latest active time is"
		HoneyLanguage.Japanese.code -> "this account's latest active time is"
		HoneyLanguage.Korean.code -> "this account's latest active time is"
		HoneyLanguage.Russian.code -> "this account's latest active time is"
		HoneyLanguage.TraditionalChinese.code -> "this account's latest active time is"
		else -> ""
	}

	@JvmField
	val balance = when (currentLanguage) {
		HoneyLanguage.English.code -> "Balance"
		HoneyLanguage.Chinese.code -> "Balance"
		HoneyLanguage.Japanese.code -> "Balance"
		HoneyLanguage.Korean.code -> "Balance"
		HoneyLanguage.Russian.code -> "Balance"
		HoneyLanguage.TraditionalChinese.code -> "Balance"
		else -> ""
	}

	@JvmField
	val refunds = when (currentLanguage) {
		HoneyLanguage.English.code -> "Refunds"
		HoneyLanguage.Chinese.code -> "Refunds"
		HoneyLanguage.Japanese.code -> "Refunds"
		HoneyLanguage.Korean.code -> "Refunds"
		HoneyLanguage.Russian.code -> "Refunds"
		HoneyLanguage.TraditionalChinese.code -> "Refunds"
		else -> ""
	}

	@JvmField
	val transactionCount = when (currentLanguage) {
		HoneyLanguage.English.code -> "Transaction Count"
		HoneyLanguage.Chinese.code -> "Transaction Count"
		HoneyLanguage.Japanese.code -> "Transaction Count"
		HoneyLanguage.Korean.code -> "Transaction Count"
		HoneyLanguage.Russian.code -> "Transaction Count"
		HoneyLanguage.TraditionalChinese.code -> "Transaction Count"
		else -> ""
	}

	@JvmField
	val totalReceived = when (currentLanguage) {
		HoneyLanguage.English.code -> "Total Received"
		HoneyLanguage.Chinese.code -> "Total Received"
		HoneyLanguage.Japanese.code -> "Total Received"
		HoneyLanguage.Korean.code -> "Total Received"
		HoneyLanguage.Russian.code -> "Total Received"
		HoneyLanguage.TraditionalChinese.code -> "Total Received"
		else -> ""
	}

	@JvmField
	val totalSent = when (currentLanguage) {
		HoneyLanguage.English.code -> "Total Sent"
		HoneyLanguage.Chinese.code -> "Total Sent"
		HoneyLanguage.Japanese.code -> "Total Sent"
		HoneyLanguage.Korean.code -> "Total Sent"
		HoneyLanguage.Russian.code -> "Total Sent"
		HoneyLanguage.TraditionalChinese.code -> "Total Sent"
		else -> ""
	}

	@JvmField
	val accountInformation = when (currentLanguage) {
		HoneyLanguage.English.code -> "Account Information"
		HoneyLanguage.Chinese.code -> "Account Information"
		HoneyLanguage.Japanese.code -> "Account Information"
		HoneyLanguage.Korean.code -> "Account Information"
		HoneyLanguage.Russian.code -> "Account Information"
		HoneyLanguage.TraditionalChinese.code -> "Account Information"
		else -> ""
	}

	@JvmField
	val transaction = when (currentLanguage) {
		HoneyLanguage.English.code -> "Transaction"
		HoneyLanguage.Chinese.code -> "Transaction"
		HoneyLanguage.Japanese.code -> "Transaction"
		HoneyLanguage.Korean.code -> "Transaction"
		HoneyLanguage.Russian.code -> "Transaction"
		HoneyLanguage.TraditionalChinese.code -> "Transaction"
		else -> ""
	}

	@JvmField
	val addressDetail = when (currentLanguage) {
		HoneyLanguage.English.code -> "Address Detail"
		HoneyLanguage.Chinese.code -> "Address Detail"
		HoneyLanguage.Japanese.code -> "Address Detail"
		HoneyLanguage.Korean.code -> "Address Detail"
		HoneyLanguage.Russian.code -> "Address Detail"
		HoneyLanguage.TraditionalChinese.code -> "Address Detail"
		else -> ""
	}

	@JvmField
	val inactivationAccount = when (currentLanguage) {
		HoneyLanguage.English.code -> "Inactivation Account"
		HoneyLanguage.Chinese.code -> "Inactivation Account"
		HoneyLanguage.Japanese.code -> "Inactivation Account"
		HoneyLanguage.Korean.code -> "Inactivation Account"
		HoneyLanguage.Russian.code -> "Inactivation Account"
		HoneyLanguage.TraditionalChinese.code -> "Inactivation Account"
		else -> ""
	}

	@JvmField
	val accountNameSelection = when (currentLanguage) {
		HoneyLanguage.English.code -> "Account Name Selection"
		HoneyLanguage.Chinese.code -> "Account Name Selection"
		HoneyLanguage.Japanese.code -> "Account Name Selection"
		HoneyLanguage.Korean.code -> "Account Name Selection"
		HoneyLanguage.Russian.code -> "Account Name Selection"
		HoneyLanguage.TraditionalChinese.code -> "Account Name Selection"
		else -> ""
	}

	@JvmField
	val activationMethod = when (currentLanguage) {
		HoneyLanguage.English.code -> "Inactivation Account"
		HoneyLanguage.Chinese.code -> "Inactivation Account"
		HoneyLanguage.Japanese.code -> "Inactivation Account"
		HoneyLanguage.Korean.code -> "Inactivation Account"
		HoneyLanguage.Russian.code -> "Inactivation Account"
		HoneyLanguage.TraditionalChinese.code -> "Inactivation Account"
		else -> ""
	}

	@JvmField
	val tradingCPU = when (currentLanguage) {
		HoneyLanguage.English.code -> "Trading CPU"
		HoneyLanguage.Chinese.code -> "Trading CPU"
		HoneyLanguage.Japanese.code -> "Trading CPU"
		HoneyLanguage.Korean.code -> "Trading CPU"
		HoneyLanguage.Russian.code -> "Trading CPU"
		HoneyLanguage.TraditionalChinese.code -> "Trading CPU"
		else -> ""
	}

	@JvmField
	val tradingNET = when (currentLanguage) {
		HoneyLanguage.English.code -> "Trading NET"
		HoneyLanguage.Chinese.code -> "Trading NET"
		HoneyLanguage.Japanese.code -> "Trading NET"
		HoneyLanguage.Korean.code -> "Trading NET"
		HoneyLanguage.Russian.code -> "Trading NET"
		HoneyLanguage.TraditionalChinese.code -> "Trading NET"
		else -> ""
	}

	@JvmField
	val tradingRAM = when (currentLanguage) {
		HoneyLanguage.English.code -> "Trading RAM"
		HoneyLanguage.Chinese.code -> "Trading RAM"
		HoneyLanguage.Japanese.code -> "Trading RAM"
		HoneyLanguage.Korean.code -> "Trading RAM"
		HoneyLanguage.Russian.code -> "Trading RAM"
		HoneyLanguage.TraditionalChinese.code -> "Trading RAM"
		else -> ""
	}

}