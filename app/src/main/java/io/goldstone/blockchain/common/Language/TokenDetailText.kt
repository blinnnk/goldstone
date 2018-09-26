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
		HoneyLanguage.Chinese.code -> "资源"
		HoneyLanguage.Japanese.code -> "リソース"
		HoneyLanguage.Korean.code -> "자원"
		HoneyLanguage.Russian.code -> "Ресурсы"
		HoneyLanguage.TraditionalChinese.code -> "資源"
		else -> ""
	}
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
		HoneyLanguage.English.code -> "RAM"
		HoneyLanguage.Chinese.code -> "RAM(内存)"
		HoneyLanguage.Japanese.code -> "RAM(ラム)"
		HoneyLanguage.Korean.code -> "RAM(램)"
		HoneyLanguage.Russian.code -> "RAM"
		HoneyLanguage.TraditionalChinese.code -> "RAM(內存)"
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
	val delegateCPU = when (currentLanguage) {
		HoneyLanguage.English.code -> "DELEGATE CPU\nREFUND CPU"
		HoneyLanguage.Chinese.code -> "代理CPU\n赎回CPU"
		HoneyLanguage.Japanese.code -> "CPUを委任する\nCPUを払い戻す"
		HoneyLanguage.Korean.code -> "CPU 를 위임하십시오\nCPU 환불"
		HoneyLanguage.Russian.code -> "Делегат CPU\nВозврат CPU"
		HoneyLanguage.TraditionalChinese.code -> "代理CPU\n贖回CPU"
		else -> ""
	}
	@JvmField
	val delegateNET = when (currentLanguage) {
		HoneyLanguage.English.code -> "DELEGATE NET\nREFUND NET"
		HoneyLanguage.Chinese.code -> "代理NET\n赎回NET"
		HoneyLanguage.Japanese.code -> "NETを委任する\nNETを払い戻す"
		HoneyLanguage.Korean.code -> "NET 를 위임하십시오\nNET 환불"
		HoneyLanguage.Russian.code -> "Делегат NET\nВозврат NET"
		HoneyLanguage.TraditionalChinese.code -> "代理NET\n贖回NET"
		else -> ""
	}
	@JvmField
	val tradeRAM = when (currentLanguage) {
		HoneyLanguage.English.code -> "BUY RAM\nSELL RAM"
		HoneyLanguage.Chinese.code -> "买入RAM\n卖出RAM"
		HoneyLanguage.Japanese.code -> "RAMを購入する\nRAMを販売する"
		HoneyLanguage.Korean.code -> "RAM 구입\nRAM  판매"
		HoneyLanguage.Russian.code -> "Купить RAM\nПродать RAM"
		HoneyLanguage.TraditionalChinese.code -> "買入RAM\n賣出RAM"
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
	val chainType = when (currentLanguage) {
		HoneyLanguage.English.code -> "CHAIN TYPE"
		HoneyLanguage.Chinese.code -> "链类型"
		HoneyLanguage.Japanese.code -> "チェーンタイプ"
		HoneyLanguage.Korean.code -> "세부 정보 확인"
		HoneyLanguage.Russian.code -> "ТИП ЦЕПЕЙ"
		HoneyLanguage.TraditionalChinese.code -> "鏈類型"
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
		HoneyLanguage.Chinese.code -> "账户未激活"
		HoneyLanguage.Japanese.code -> "活性化していない"
		HoneyLanguage.Korean.code -> "활성화되지 않음"
		HoneyLanguage.Russian.code -> "Не активировано"
		HoneyLanguage.TraditionalChinese.code -> "賬戶未激活"
		else -> ""
	}

	@JvmField
	val tradingCPU = when (currentLanguage) {
		HoneyLanguage.English.code -> "Trading CPU"
		HoneyLanguage.Chinese.code -> "交易CPU"
		HoneyLanguage.Japanese.code -> "トランザクション CPU"
		HoneyLanguage.Korean.code -> "트레이딩 CPU"
		HoneyLanguage.Russian.code -> "Торговое CPU"
		HoneyLanguage.TraditionalChinese.code -> "交易CPU"
		else -> ""
	}

	@JvmField
	val tradingNET = when (currentLanguage) {
		HoneyLanguage.English.code -> "Trading NET"
		HoneyLanguage.Chinese.code -> "交易NET"
		HoneyLanguage.Japanese.code -> "トランザクション NET"
		HoneyLanguage.Korean.code -> "트레이딩 NET"
		HoneyLanguage.Russian.code -> "Торговое NET"
		HoneyLanguage.TraditionalChinese.code -> "交易NET"
		else -> ""
	}

	@JvmField
	val tradingRAM = when (currentLanguage) {
		HoneyLanguage.English.code -> "Trading RAM"
		HoneyLanguage.Chinese.code -> "交易RAM"
		HoneyLanguage.Japanese.code -> "トランザクション RAM"
		HoneyLanguage.Korean.code -> "트레이딩 RAM"
		HoneyLanguage.Russian.code -> "Торговое RAM"
		HoneyLanguage.TraditionalChinese.code -> "交易RAM"
		else -> ""
	}

}