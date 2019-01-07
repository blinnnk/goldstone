package io.goldstone.blinnnk.common.language

/**
 * @date 2018/8/8 2:11 AM
 * @author KaySaith
 */

object WalletText {
	@JvmField
	val totalAssets = when (currentLanguage) {
		HoneyLanguage.English.code -> "Total Assets"
		HoneyLanguage.Chinese.code -> "钱包所有财产"
		HoneyLanguage.Japanese.code -> "総資産"
		HoneyLanguage.Korean.code -> "지갑내 모든 재산"
		HoneyLanguage.Russian.code -> "Итого активы"
		HoneyLanguage.TraditionalChinese.code -> "總資產"
		else -> ""
	}
	@JvmField
	val section = when (currentLanguage) {
		HoneyLanguage.English.code -> "My Tokens:"
		HoneyLanguage.Chinese.code -> "我的资产:"
		HoneyLanguage.Japanese.code -> "私の資産："
		HoneyLanguage.Korean.code -> "나의자산:"
		HoneyLanguage.Russian.code -> "Мой токен:"
		HoneyLanguage.TraditionalChinese.code -> "我的資產:"
		else -> ""
	}

	@JvmField
	val wallet = when (currentLanguage) {
		HoneyLanguage.English.code -> "Wallet"
		HoneyLanguage.Chinese.code -> "钱包"
		HoneyLanguage.Japanese.code -> "ウォレット"
		HoneyLanguage.Korean.code -> "지갑"
		HoneyLanguage.Russian.code -> "Кошелек"
		HoneyLanguage.TraditionalChinese.code -> "錢包"
		else -> ""
	}

	@JvmField
	val isUsing = when (currentLanguage) {
		HoneyLanguage.English.code -> "Using Now"
		HoneyLanguage.Chinese.code -> "正在使用"
		HoneyLanguage.Japanese.code -> "使用中"
		HoneyLanguage.Korean.code -> "사용중"
		HoneyLanguage.Russian.code -> "При использовании"
		HoneyLanguage.TraditionalChinese.code -> "正在使用"
		else -> ""
	}

	@JvmField
	val notifyButton = when (currentLanguage) {
		HoneyLanguage.English.code -> "Notifications"
		HoneyLanguage.Chinese.code -> "通知"
		HoneyLanguage.Japanese.code -> "通知"
		HoneyLanguage.Korean.code -> "알림"
		HoneyLanguage.Russian.code -> "Уведомления"
		HoneyLanguage.TraditionalChinese.code -> "通知"
		else -> ""
	}

	@JvmField
	val addToken = when (currentLanguage) {
		HoneyLanguage.English.code -> "Add Token"
		HoneyLanguage.Chinese.code -> "Add Token"
		HoneyLanguage.Japanese.code -> "Add Token"
		HoneyLanguage.Korean.code -> "Add Token"
		HoneyLanguage.Russian.code -> "Add Token"
		HoneyLanguage.TraditionalChinese.code -> "Add Token"
		else -> ""
	}

	@JvmField
	val setDefaultAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "Set as the default address"
		HoneyLanguage.Chinese.code -> "设为默认地址"
		HoneyLanguage.Japanese.code -> "デフォルトアドレスとして設定"
		HoneyLanguage.Korean.code -> "기본 주소로 설정"
		HoneyLanguage.Russian.code -> "Установить как адрес по умолчанию"
		HoneyLanguage.TraditionalChinese.code -> "設為默認地址"
		else -> ""
	}
	@JvmField
	val createNewAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "Add a new subaddress"
		HoneyLanguage.Chinese.code -> "添加新的子地址"
		HoneyLanguage.Japanese.code -> "新しいサブアドレスを追加する"
		HoneyLanguage.Korean.code -> "새 하위 주소 추가"
		HoneyLanguage.Russian.code -> "Добавить новый субадрес"
		HoneyLanguage.TraditionalChinese.code -> "添加新的子地址"
		else -> ""
	}
	@JvmField
	val moreOperations = when (currentLanguage) {
		HoneyLanguage.English.code -> "More operations"
		HoneyLanguage.Chinese.code -> "更多操作"
		HoneyLanguage.Japanese.code -> "その他の操作"
		HoneyLanguage.Korean.code -> "추가 작업"
		HoneyLanguage.Russian.code -> "Дополнительные операции"
		HoneyLanguage.TraditionalChinese.code -> "更多操作"
		else -> ""
	}
	@JvmField
	val getBCHLegacyAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "Get Legacy Address"
		HoneyLanguage.Chinese.code -> "获取 Legacy 格式地址"
		HoneyLanguage.Japanese.code -> "Legacy形式のアドレス"
		HoneyLanguage.Korean.code -> "Legacy 형식 주소 가져 오기"
		HoneyLanguage.Russian.code -> "Получить адрес Legacy"
		HoneyLanguage.TraditionalChinese.code -> "獲取 Legacy 格式地址"
		else -> ""
	}
	@JvmField
	val multiChainWallet = when (currentLanguage) {
		HoneyLanguage.English.code -> "This is a multi-chain wallet"
		HoneyLanguage.Chinese.code -> "这是多链钱包"
		HoneyLanguage.Japanese.code -> "これはマルチチェーンウォレットです"
		HoneyLanguage.Korean.code -> "이것은 다중 체인 지갑입니다."
		HoneyLanguage.Russian.code -> "Это многоцелевой кошелек"
		HoneyLanguage.TraditionalChinese.code -> "這是多鏈錢包"
		else -> ""
	}
	@JvmField
	val bip44MultiChain = when (currentLanguage) {
		HoneyLanguage.English.code -> "Multiple Chain Based On BIP44"
		HoneyLanguage.Chinese.code -> "基于 BIP44 的多链钱包"
		HoneyLanguage.Japanese.code -> "マルチチェーン (BIP44)"
		HoneyLanguage.Korean.code -> "BIP44 기반의 다중 체인 지갑"
		HoneyLanguage.Russian.code -> "Multi-цепи (BIP44)"
		HoneyLanguage.TraditionalChinese.code -> "基於 BIP44 的多鏈錢包"
		else -> ""
	}
	@JvmField
	val multiChain = when (currentLanguage) {
		HoneyLanguage.English.code -> "Multiple Chain"
		HoneyLanguage.Chinese.code -> "支持多链"
		HoneyLanguage.Japanese.code -> "マルチチェーン"
		HoneyLanguage.Korean.code -> "다중 체인"
		HoneyLanguage.Russian.code -> "Multi-цепи"
		HoneyLanguage.TraditionalChinese.code -> "支持多鏈"
		else -> ""
	}
	@JvmField
	val ethERCAndETC = when (currentLanguage) {
		HoneyLanguage.English.code -> "ETH / ERC20/ETC"
		HoneyLanguage.Chinese.code -> "ETH/ERC20代币/ETC"
		HoneyLanguage.Japanese.code -> "ETH/ERC20トークン/ETC"
		HoneyLanguage.Korean.code -> "ETH/ERC20 토큰/ETC"
		HoneyLanguage.Russian.code -> "ETH/ERC20 Token/ETC"
		HoneyLanguage.TraditionalChinese.code -> "ETH/ERC20代幣/ETC"
		else -> ""
	}
	@JvmField
	val ltcMainnet = when (currentLanguage) {
		HoneyLanguage.English.code -> "Litecoin Mainnet"
		HoneyLanguage.Chinese.code -> "Litecoin (莱特币) 主网"
		HoneyLanguage.Japanese.code -> "Litecoin メインネット"
		HoneyLanguage.Korean.code -> "Litecoin 메인 넷"
		HoneyLanguage.Russian.code -> "Litecoin Основная"
		HoneyLanguage.TraditionalChinese.code -> "Litecoin (萊特幣) 主网"
		else -> ""
	}

	@JvmField
	val eosMainnet = when (currentLanguage) {
		HoneyLanguage.English.code -> "EOS Mainnet"
		HoneyLanguage.Chinese.code -> "EOS 主网"
		HoneyLanguage.Japanese.code -> "EOS メインネット"
		HoneyLanguage.Korean.code -> "EOS 메인 넷"
		HoneyLanguage.Russian.code -> "EOS Испытательная"
		HoneyLanguage.TraditionalChinese.code -> "EOS 主網"
		else -> ""
	}

	@JvmField
	val eosWallet = when (currentLanguage) {
		HoneyLanguage.English.code -> "EOS Wallet"
		HoneyLanguage.Chinese.code -> "EOS 钱包"
		HoneyLanguage.Japanese.code -> "EOS ウォレット"
		HoneyLanguage.Korean.code -> "EOS Wallet"
		HoneyLanguage.Russian.code -> "EOS Wallet"
		HoneyLanguage.TraditionalChinese.code -> "EOS 錢包"
		else -> ""
	}

	@JvmField
	val eosJungle = when (currentLanguage) {
		HoneyLanguage.English.code -> "EOS JUNGLE"
		HoneyLanguage.Chinese.code -> "EOS JUNGLE 测试网"
		HoneyLanguage.Japanese.code -> "EOS JUNGLE"
		HoneyLanguage.Korean.code -> "EOS JUNGLE"
		HoneyLanguage.Russian.code -> "EOS JUNGLE"
		HoneyLanguage.TraditionalChinese.code -> "EOS JUNGLE 測試網"
		else -> ""
	}

	val eosKylin = when (currentLanguage) {
		HoneyLanguage.English.code -> "EOS KYLIN"
		HoneyLanguage.Chinese.code -> "EOS KYLIN 测试网"
		HoneyLanguage.Japanese.code -> "EOS KYLIN"
		HoneyLanguage.Korean.code -> "EOS KYLIN"
		HoneyLanguage.Russian.code -> "EOS KYLIN"
		HoneyLanguage.TraditionalChinese.code -> "EOS KYLIN 測試網"
		else -> ""
	}


	@JvmField
	val bchMainnet = when (currentLanguage) {
		HoneyLanguage.English.code -> "BCH Mainnet"
		HoneyLanguage.Chinese.code -> "BCH (比特币现金) 主网"
		HoneyLanguage.Japanese.code -> "BCH メインネット"
		HoneyLanguage.Korean.code -> "BCH 메인 넷"
		HoneyLanguage.Russian.code -> "BCH Испытательная"
		HoneyLanguage.TraditionalChinese.code -> "BCH (比特幣現金) 主網"
		else -> ""
	}
	@JvmField
	val btcMainnet = when (currentLanguage) {
		HoneyLanguage.English.code -> "Bitcoin Mainnet"
		HoneyLanguage.Chinese.code -> "Bitcoin (比特币) 主网"
		HoneyLanguage.Japanese.code -> "Bitcoin(ビットコイン)メインネット"
		HoneyLanguage.Korean.code -> "Bitcoin  메인 넷"
		HoneyLanguage.Russian.code -> "Bitcoin Испытательная"
		HoneyLanguage.TraditionalChinese.code -> "Bitcoin (比特幣) 主網"
		else -> ""
	}
	@JvmField
	val btcTestnet = when (currentLanguage) {
		HoneyLanguage.English.code -> "Bitcoin Testnet"
		HoneyLanguage.Chinese.code -> "Bitcoin (比特币) 测试网"
		HoneyLanguage.Japanese.code -> "Bitcoin(ビットコイン)テストネット"
		HoneyLanguage.Korean.code -> "Bitcoin 테스트 넷"
		HoneyLanguage.Russian.code -> "Bitcoin Testnet"
		HoneyLanguage.TraditionalChinese.code -> "Bitcoin (比特幣) 測試網"
		else -> ""
	}
	@JvmField
	val watchOnly = when (currentLanguage) {
		HoneyLanguage.English.code -> "Watch Only"
		HoneyLanguage.Chinese.code -> "观察钱包"
		HoneyLanguage.Japanese.code -> "観察ウォレット"
		HoneyLanguage.Korean.code -> "관측 지갑"
		HoneyLanguage.Russian.code -> "Наблюдать"
		HoneyLanguage.TraditionalChinese.code -> "觀察錢包"
		else -> ""
	}

	@JvmField
	val qrCode = when (currentLanguage) {
		HoneyLanguage.English.code -> "QR Code"
		HoneyLanguage.Chinese.code -> "二维码"
		HoneyLanguage.Japanese.code -> "QR コード"
		HoneyLanguage.Korean.code -> "QR 코드"
		HoneyLanguage.Russian.code -> "QR-код"
		HoneyLanguage.TraditionalChinese.code -> "二維碼"
		else -> ""
	}
}