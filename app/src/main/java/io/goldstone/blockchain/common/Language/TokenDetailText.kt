package io.goldstone.blockchain.common.language

/**
 * @date 2018/8/8 2:27 AM
 * @author KaySaith
 */

object TokenDetailText {

	//	····················转账记录相关····················
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

	//	····················Token/资产信息····················
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

	//	····················EOS资源相关····················
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

	//	····················EOS资产交易入口····················

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

	//	····················CPU与NET租赁界面····················
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
		HoneyLanguage.Korean.code -> "EOS의 수"
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
		HoneyLanguage.English.code -> "How much bytes you want"
		HoneyLanguage.Chinese.code -> "想要卖出的RAM数量(Byte)"
		HoneyLanguage.Japanese.code -> "購入したい番号（バイト）"
		HoneyLanguage.Korean.code -> "구매하려는 번호 (바이트)"
		HoneyLanguage.Russian.code -> "Номер, который вы хотите купить (байт)"
		HoneyLanguage.TraditionalChinese.code -> "想要買的數量(Byte)"
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
	val publicKey = when (currentLanguage) {HoneyLanguage.English.code -> "Public key"
		HoneyLanguage.Chinese.code -> "公钥"
		HoneyLanguage.Japanese.code -> "公開鍵"
		HoneyLanguage.Korean.code -> "공개 키"
		HoneyLanguage.Russian.code -> "Открытый ключ"
		HoneyLanguage.TraditionalChinese.code -> "公鑰"
		else -> ""
	}
}