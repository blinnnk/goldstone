package io.goldstone.blockchain.common.language

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
	val addToken = when (currentLanguage) {
		HoneyLanguage.English.code -> "Add More Tokens"
		HoneyLanguage.Chinese.code -> "添加Token"
		HoneyLanguage.Japanese.code -> "Tokenの追加"
		HoneyLanguage.Korean.code -> "기타 Token 추가"
		HoneyLanguage.Russian.code -> "Добавить Token"
		HoneyLanguage.TraditionalChinese.code -> "添加其他Token"
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
	val historyButton = when (currentLanguage) {
		HoneyLanguage.English.code -> "History"
		HoneyLanguage.Chinese.code -> "记录"
		HoneyLanguage.Japanese.code -> "記録"
		HoneyLanguage.Korean.code -> "기록"
		HoneyLanguage.Russian.code -> "История"
		HoneyLanguage.TraditionalChinese.code -> "記錄"
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
	val tokenDetailHeaderText = when (currentLanguage) {
		HoneyLanguage.English.code -> "MY"
		HoneyLanguage.Chinese.code -> "我的"
		HoneyLanguage.Japanese.code -> "私のもの"
		HoneyLanguage.Korean.code -> "내"
		HoneyLanguage.Russian.code -> "Мое"
		HoneyLanguage.TraditionalChinese.code -> "我的"
		else -> ""
	}
	@JvmField
	val setDefaultAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "Set Default Address"
		HoneyLanguage.Chinese.code -> "设置默认地址"
		HoneyLanguage.Japanese.code -> "デフォルトアドレスを設定する"
		HoneyLanguage.Korean.code -> "기본 주소 설정"
		HoneyLanguage.Russian.code -> "Установить адрес по умолчанию"
		HoneyLanguage.TraditionalChinese.code -> "設置默認地址"
		else -> ""
	}
	@JvmField
	val getBCHLegacyAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "Get Legacy Address"
		HoneyLanguage.Chinese.code -> "获取Legacy格式地址"
		HoneyLanguage.Japanese.code -> "Legacy形式のアドレス"
		HoneyLanguage.Korean.code -> "Legacy 형식 주소 가져 오기"
		HoneyLanguage.Russian.code -> "Получить адрес Legacy"
		HoneyLanguage.TraditionalChinese.code -> "獲取Legacy格式地址"
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
	val multiChain = when (currentLanguage) {
		HoneyLanguage.English.code -> "MULTI-CHAIN"
		HoneyLanguage.Chinese.code -> "支持多链"
		HoneyLanguage.Japanese.code -> "マルチチェーン"
		HoneyLanguage.Korean.code -> "다중 체인"
		HoneyLanguage.Russian.code -> "Multi-цепи"
		HoneyLanguage.TraditionalChinese.code -> "支持多鏈"
		else -> ""
	}
	@JvmField
	val ethERCAndETC = when (currentLanguage) {
		HoneyLanguage.English.code -> "ETH/ERC20 Token/ETC"
		HoneyLanguage.Chinese.code -> "ETH/ERC20代币/ETC"
		HoneyLanguage.Japanese.code -> "ETH/ERC20トークン/ETC"
		HoneyLanguage.Korean.code -> "ETH/ERC20 토큰/ETC"
		HoneyLanguage.Russian.code -> "ETH/ERC20 Token/ETC"
		HoneyLanguage.TraditionalChinese.code -> "ETH/ERC20代幣/ETC"
		else -> ""
	}
	@JvmField
	val ltcMainnet = when (currentLanguage) {
		HoneyLanguage.English.code -> "LITECOIN MAINNET"
		HoneyLanguage.Chinese.code -> "LITECOIN MAINNET"
		HoneyLanguage.Japanese.code -> "LITECOIN MAINNET"
		HoneyLanguage.Korean.code -> "LITECOIN MAINNET"
		HoneyLanguage.Russian.code -> "LITECOIN MAINNET"
		HoneyLanguage.TraditionalChinese.code -> "LITECOIN MAINNET"
		else -> ""
	}

	@JvmField
	val eosWallet = when (currentLanguage) {
		HoneyLanguage.English.code -> "EOS WALLET"
		HoneyLanguage.Chinese.code -> "EOS WALLET"
		HoneyLanguage.Japanese.code -> "EOS WALLET"
		HoneyLanguage.Korean.code -> "EOS WALLET"
		HoneyLanguage.Russian.code -> "EOS WALLET"
		HoneyLanguage.TraditionalChinese.code -> "EOS WALLET"
		else -> ""
	}

	@JvmField
	val bchMainnet = when (currentLanguage) {
		HoneyLanguage.English.code -> "BCH MAINNET"
		HoneyLanguage.Chinese.code -> "BCH MAINNET"
		HoneyLanguage.Japanese.code -> "BCH MAINNET"
		HoneyLanguage.Korean.code -> "BCH MAINNET"
		HoneyLanguage.Russian.code -> "BCH MAINNET"
		HoneyLanguage.TraditionalChinese.code -> "BCH MAINNET"
		else -> ""
	}
	@JvmField
	val btcMainnet = when (currentLanguage) {
		HoneyLanguage.English.code -> "BITCOIN MAINNET"
		HoneyLanguage.Chinese.code -> "BITCOIN 主网"
		HoneyLanguage.Japanese.code -> "BITCOIN メインネット"
		HoneyLanguage.Korean.code -> "BITCOIN  메인 넷"
		HoneyLanguage.Russian.code -> "BITCOIN МАЙНЕТ"
		HoneyLanguage.TraditionalChinese.code -> "BITCOIN MAINNET"
		else -> ""
	}
	@JvmField
	val bitcoinTestnet = when (currentLanguage) {
		HoneyLanguage.English.code -> "BITCOIN TESTNET"
		HoneyLanguage.Chinese.code -> "BITCOIN 测试网络"
		HoneyLanguage.Japanese.code -> "BITCOIN テストネット"
		HoneyLanguage.Korean.code -> "BITCOIN 테스트 넷"
		HoneyLanguage.Russian.code -> "BITCOIN TESTNET"
		HoneyLanguage.TraditionalChinese.code -> "BITCOIN 測試網絡"
		else -> ""
	}
	@JvmField
	val watchOnly = when (currentLanguage) {
		HoneyLanguage.English.code -> "WATCH ONLY"
		HoneyLanguage.Chinese.code -> "观察钱包"
		HoneyLanguage.Japanese.code -> "観察ウォレット"
		HoneyLanguage.Korean.code -> "관측 지갑"
		HoneyLanguage.Russian.code -> "Наблюдательный кошелек"
		HoneyLanguage.TraditionalChinese.code -> "觀察錢包"
		else -> ""
	}
	@JvmField
	val baseBip44 = when (currentLanguage) {
		HoneyLanguage.English.code -> "BASED ON BIP44"
		HoneyLanguage.Chinese.code -> "钱包基于 BIP44标准"
		HoneyLanguage.Japanese.code -> "BIP44規格に基づく"
		HoneyLanguage.Korean.code -> "BIP44 표준 기반 월렛"
		HoneyLanguage.Russian.code -> "Основано на bip44"
		HoneyLanguage.TraditionalChinese.code -> "錢包基於 BIP44標準"
		else -> ""
	}
	@JvmField
	val showQRCode = when (currentLanguage) {
		HoneyLanguage.English.code -> "QR Code"
		HoneyLanguage.Chinese.code -> "二维码"
		HoneyLanguage.Japanese.code -> "QRコード"
		HoneyLanguage.Korean.code -> "QR 코드"
		HoneyLanguage.Russian.code -> "QR-код"
		HoneyLanguage.TraditionalChinese.code -> "二維碼"
		else -> ""
	}
}

object WalletNameText {
	@JvmField
	val Owl = when (currentLanguage) {
		HoneyLanguage.English.code -> "Owl"
		HoneyLanguage.Chinese.code -> "猫头鹰"
		HoneyLanguage.Japanese.code -> "フクロウ"
		HoneyLanguage.Korean.code -> "올빼미"
		HoneyLanguage.Russian.code -> "Сова"
		HoneyLanguage.TraditionalChinese.code -> "貓頭鷹"
		else -> ""
	}
	@JvmField
	val Cat = when (currentLanguage) {
		HoneyLanguage.English.code -> "Cat"
		HoneyLanguage.Chinese.code -> "招财猫"
		HoneyLanguage.Japanese.code -> "ラッキーな猫"
		HoneyLanguage.Korean.code -> "럭키 고양이"
		HoneyLanguage.Russian.code -> "Счастливая кошка"
		HoneyLanguage.TraditionalChinese.code -> "招財貓"
		else -> ""
	}
	@JvmField
	val Elephant = when (currentLanguage) {
		HoneyLanguage.English.code -> "Elephant"
		HoneyLanguage.Chinese.code -> "象先生"
		HoneyLanguage.Japanese.code -> "象"
		HoneyLanguage.Korean.code -> "코끼리"
		HoneyLanguage.Russian.code -> "слон"
		HoneyLanguage.TraditionalChinese.code -> "象先生"
		else -> ""
	}
	@JvmField
	val Rhinoceros = when (currentLanguage) {
		HoneyLanguage.English.code -> "Rhinoceros"
		HoneyLanguage.Chinese.code -> "红犀牛"
		HoneyLanguage.Japanese.code -> "Rhinoceros"
		HoneyLanguage.Korean.code -> "코뿔소"
		HoneyLanguage.Russian.code -> "Носорог"
		HoneyLanguage.TraditionalChinese.code -> "红犀牛"
		else -> ""
	}
	@JvmField
	val Frog = when (currentLanguage) {
		HoneyLanguage.English.code -> "Frog"
		HoneyLanguage.Chinese.code -> "青蛙"
		HoneyLanguage.Japanese.code -> "カエル"
		HoneyLanguage.Korean.code -> "개구리"
		HoneyLanguage.Russian.code -> "Лягушка"
		HoneyLanguage.TraditionalChinese.code -> "青蛙"
		else -> ""
	}
	@JvmField
	val Koala = when (currentLanguage) {
		HoneyLanguage.English.code -> "Koala"
		HoneyLanguage.Chinese.code -> "考拉宝宝"
		HoneyLanguage.Japanese.code -> "コアラ"
		HoneyLanguage.Korean.code -> "코알라"
		HoneyLanguage.Russian.code -> "Коала"
		HoneyLanguage.TraditionalChinese.code -> "考拉寶寶"
		else -> ""
	}
	@JvmField
	val Fox = when (currentLanguage) {
		HoneyLanguage.English.code -> "Fox"
		HoneyLanguage.Chinese.code -> "火狐狸"
		HoneyLanguage.Japanese.code -> "狐"
		HoneyLanguage.Korean.code -> "여우"
		HoneyLanguage.Russian.code -> "Лиса"
		HoneyLanguage.TraditionalChinese.code -> "火狐狸"
		else -> ""
	}
	@JvmField
	val Monkey = when (currentLanguage) {
		HoneyLanguage.English.code -> "Monkey"
		HoneyLanguage.Chinese.code -> "猕猴"
		HoneyLanguage.Japanese.code -> "モンキー"
		HoneyLanguage.Korean.code -> "원숭이"
		HoneyLanguage.Russian.code -> "Обезьяна"
		HoneyLanguage.TraditionalChinese.code -> "獼猴"
		else -> ""
	}
	@JvmField
	val Giraffle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Giraffle"
		HoneyLanguage.Chinese.code -> "长颈鹿"
		HoneyLanguage.Japanese.code -> "キリン"
		HoneyLanguage.Korean.code -> "기린"
		HoneyLanguage.Russian.code -> "Жирафа"
		HoneyLanguage.TraditionalChinese.code -> "長頸鹿"
		else -> ""
	}
	@JvmField
	val Penguin = when (currentLanguage) {
		HoneyLanguage.English.code -> "Penguin"
		HoneyLanguage.Chinese.code -> "企鹅"
		HoneyLanguage.Japanese.code -> "ペンギン"
		HoneyLanguage.Korean.code -> "펭귄"
		HoneyLanguage.Russian.code -> "пингвин"
		HoneyLanguage.TraditionalChinese.code -> "企鵝"
		else -> ""
	}
	@JvmField
	val Wolf = when (currentLanguage) {
		HoneyLanguage.English.code -> "Wolf"
		HoneyLanguage.Chinese.code -> "小灰狼"
		HoneyLanguage.Japanese.code -> "狼"
		HoneyLanguage.Korean.code -> "늑대"
		HoneyLanguage.Russian.code -> "волк"
		HoneyLanguage.TraditionalChinese.code -> "小灰狼"
		else -> ""
	}
	@JvmField
	val Bull = when (currentLanguage) {
		HoneyLanguage.English.code -> "Bull"
		HoneyLanguage.Chinese.code -> "牛魔王"
		HoneyLanguage.Japanese.code -> "ブル"
		HoneyLanguage.Korean.code -> "황소"
		HoneyLanguage.Russian.code -> "бык"
		HoneyLanguage.TraditionalChinese.code -> "牛魔王"
		else -> ""
	}
	@JvmField
	val Leopard = when (currentLanguage) {
		HoneyLanguage.English.code -> "Leopard"
		HoneyLanguage.Chinese.code -> "黑豹"
		HoneyLanguage.Japanese.code -> "ヒョウ"
		HoneyLanguage.Korean.code -> "표범"
		HoneyLanguage.Russian.code -> "леопард"
		HoneyLanguage.TraditionalChinese.code -> "黑豹"
		else -> ""
	}
	@JvmField
	val Deer = when (currentLanguage) {
		HoneyLanguage.English.code -> "Deer"
		HoneyLanguage.Chinese.code -> "梅花鹿"
		HoneyLanguage.Japanese.code -> "鹿"
		HoneyLanguage.Korean.code -> "사슴"
		HoneyLanguage.Russian.code -> "олень"
		HoneyLanguage.TraditionalChinese.code -> "梅花鹿"
		else -> ""
	}
	@JvmField
	val Raccoon = when (currentLanguage) {
		HoneyLanguage.English.code -> "Raccoon"
		HoneyLanguage.Chinese.code -> "小浣熊"
		HoneyLanguage.Japanese.code -> "ラクーン"
		HoneyLanguage.Korean.code -> "너구리"
		HoneyLanguage.Russian.code -> "енот"
		HoneyLanguage.TraditionalChinese.code -> "小浣熊"
		else -> ""
	}
	@JvmField
	val Lion = when (currentLanguage) {
		HoneyLanguage.English.code -> "Lion"
		HoneyLanguage.Chinese.code -> "狮子王"
		HoneyLanguage.Japanese.code -> "ライオン"
		HoneyLanguage.Korean.code -> "사자"
		HoneyLanguage.Russian.code -> "лев"
		HoneyLanguage.TraditionalChinese.code -> "獅子王"
		else -> ""
	}
	@JvmField
	val Hippo = when (currentLanguage) {
		HoneyLanguage.English.code -> "Hippo"
		HoneyLanguage.Chinese.code -> "河马"
		HoneyLanguage.Japanese.code -> "カバ"
		HoneyLanguage.Korean.code -> "하마"
		HoneyLanguage.Russian.code -> "Бегемот"
		HoneyLanguage.TraditionalChinese.code -> "河馬君"
		else -> ""
	}
}
