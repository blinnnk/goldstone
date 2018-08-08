package io.goldstone.blockchain.common.language

/**
 * @date 2018/8/8 2:17 AM
 * @author KaySaith
 */

object ChainText {
	@JvmField
	val nodeSelection = when (currentLanguage) {
		HoneyLanguage.English.code -> "Node Selection"
		HoneyLanguage.Chinese.code -> "节点选择"
		HoneyLanguage.Japanese.code -> "ノード選択"
		HoneyLanguage.Korean.code -> "노드 선택"
		HoneyLanguage.Russian.code -> "Выбор узла"
		HoneyLanguage.TraditionalChinese.code -> "節點選擇"
		else -> ""
	}
	@JvmField
	val isUsing = when (currentLanguage) {
		HoneyLanguage.English.code -> "Using Now"
		HoneyLanguage.Chinese.code -> "立即使用"
		HoneyLanguage.Japanese.code -> "直ちに使用する"
		HoneyLanguage.Korean.code -> "지금 사용하기"
		HoneyLanguage.Russian.code -> "Использовать сейчас"
		HoneyLanguage.TraditionalChinese.code -> "立即使用"
		else -> ""
	}
	@JvmField
	val mainnet = when (currentLanguage) {
		HoneyLanguage.English.code -> "Mainnet"
		HoneyLanguage.Chinese.code -> "主网"
		HoneyLanguage.Japanese.code -> "Mainnet"
		HoneyLanguage.Korean.code -> "메인 넷"
		HoneyLanguage.Russian.code -> "Mainnet"
		HoneyLanguage.TraditionalChinese.code -> "主網"
		else -> ""
	}
	@JvmField
	val mainnetDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "The formal production environment \nwhere the assets are of real value"
		HoneyLanguage.Chinese.code -> "资产具有真正价值的正式生产环境"
		HoneyLanguage.Japanese.code -> "資産が持っている本当の価値を正式に生産する環境を備えています"
		HoneyLanguage.Korean.code -> "공식 생산 환경 \n 자산이 실제 가치를 지닌 곳"
		HoneyLanguage.Russian.code -> "Официальная производственная среда с активами, имеющими истинную ценность"
		HoneyLanguage.TraditionalChinese.code -> "資產具有真正價值的正式生產環境"
		else -> ""
	}
	@JvmField
	val testnet = when (currentLanguage) {
		HoneyLanguage.English.code -> "Testnet"
		HoneyLanguage.Chinese.code -> "测试网络"
		HoneyLanguage.Japanese.code -> "テスト"
		HoneyLanguage.Korean.code -> "테스트 넷"
		HoneyLanguage.Russian.code -> "Testnet"
		HoneyLanguage.TraditionalChinese.code -> "測試網絡"
		else -> ""
	}
	@JvmField
	val testnetDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "The chain used for testing, the assets \nhere are usually worthless."
		HoneyLanguage.Chinese.code -> "测试链上的资产通常是没有价值的。"
		HoneyLanguage.Japanese.code -> "テストリンク上の資産は一般的に価値を持っていません。"
		HoneyLanguage.Korean.code -> "테스트를 위해 사용 된 체인은 일반적으로 무용지물입니다."
		HoneyLanguage.Russian.code -> "Активы, цепи тестирования, как правило, не имеют ценности."
		HoneyLanguage.TraditionalChinese.code -> "測試鏈上的資產通常是沒有價值的。"
		else -> ""
	}
	@JvmField
	val goldStoneMain = when (currentLanguage) {
		HoneyLanguage.English.code -> "Main (Gold Stone)"
		HoneyLanguage.Chinese.code -> "主 (GoldStone)"
		HoneyLanguage.Japanese.code -> "主（ゴールドストーン）"
		HoneyLanguage.Korean.code -> "주님 (골드 스톤)"
		HoneyLanguage.Russian.code -> "Лорд (Голдстоун)"
		HoneyLanguage.TraditionalChinese.code -> "主 (GoldStone)"
		else -> ""
	}
	@JvmField
	val infuraMain = when (currentLanguage) {
		HoneyLanguage.English.code -> "Main (Infura)"
		HoneyLanguage.Chinese.code -> "主 (Infura)"
		HoneyLanguage.Japanese.code -> "主（Infura）"
		HoneyLanguage.Korean.code -> "주님 (Infura)"
		HoneyLanguage.Russian.code -> "Господь (Инфура)"
		HoneyLanguage.TraditionalChinese.code -> "主 (Infura)"
		else -> ""
	}
	@JvmField
	val ropsten = when (currentLanguage) {
		HoneyLanguage.English.code -> "Ropsten (GoldStone)"
		HoneyLanguage.Chinese.code -> "Ropsten测试网（GoldStone）"
		HoneyLanguage.Japanese.code -> "Ropstenテストネットワーク（GoldStone）"
		HoneyLanguage.Korean.code -> "Ropsten 테스트 네트워크 (GoldStone)"
		HoneyLanguage.Russian.code -> "Тест-сеть Ropsten (GoldStone)"
		HoneyLanguage.TraditionalChinese.code -> "Ropsten測試網（GoldStone）"
		else -> ""
	}
	@JvmField
	val infuraRopsten = when (currentLanguage) {
		HoneyLanguage.English.code -> "Ropsten (Infura)"
		HoneyLanguage.Chinese.code -> "Ropsten测试网（Infura）"
		HoneyLanguage.Japanese.code -> "Ropsten試験ネットワーク（Infura）"
		HoneyLanguage.Korean.code -> "Ropsten 테스트 네트워크 (Infura)"
		HoneyLanguage.Russian.code -> "Тест-сеть Ropsten (Infura)"
		HoneyLanguage.TraditionalChinese.code -> "Ropsten測試網（Infura）"
		else -> ""
	}
	@JvmField
	val infuraKovan = when (currentLanguage) {
		HoneyLanguage.English.code -> "Kovan (Infura)"
		HoneyLanguage.Chinese.code -> "Kovan测试网（Infura）"
		HoneyLanguage.Japanese.code -> "Kovanテストネットワーク（Infura）"
		HoneyLanguage.Korean.code -> "Kovan 테스트 네트워크 (Infura)"
		HoneyLanguage.Russian.code -> "Сеть тестирования Кована (Infura)"
		HoneyLanguage.TraditionalChinese.code -> "Kovan測試網（Infura）"
		else -> ""
	}
	@JvmField
	val infuraRinkeby = when (currentLanguage) {
		HoneyLanguage.English.code -> "Rinkeby (Infura)"
		HoneyLanguage.Chinese.code -> "Rinkeby测试网（Infura）"
		HoneyLanguage.Japanese.code -> "Rinkebyテストネットワーク（Infura）"
		HoneyLanguage.Korean.code -> "Rinkeby 테스트 네트워크 (Infura)"
		HoneyLanguage.Russian.code -> "Испытательная сеть Rinkeby (Infura)"
		HoneyLanguage.TraditionalChinese.code -> "Rinkeby測試網（Infura）"
		else -> ""
	}
	@JvmField
	val kovan = when (currentLanguage) {
		HoneyLanguage.English.code -> "Kovan (GoldStone)"
		HoneyLanguage.Chinese.code -> "Kovan测试网（GoldStone）"
		HoneyLanguage.Japanese.code -> "Kovanテストネットワーク（GoldStone）"
		HoneyLanguage.Korean.code -> "Kovan 테스트 네트워크 (GoldStone)"
		HoneyLanguage.Russian.code -> "Сеть тестирования Кована (GoldStone)"
		HoneyLanguage.TraditionalChinese.code -> "Kovan測試網（GoldStone）"
		else -> ""
	}
	@JvmField
	val rinkeby = when (currentLanguage) {
		HoneyLanguage.English.code -> "Rinkeby (GoldStone)"
		HoneyLanguage.Chinese.code -> "Rinkeby测试网（GoldStone）"
		HoneyLanguage.Japanese.code -> "Rinkebyテストネットワーク（GoldStone）"
		HoneyLanguage.Korean.code -> "Rinkeby 테스트 네트워크 (GoldStone)"
		HoneyLanguage.Russian.code -> "Сеть тестирования Rinkeby (GoldStone)"
		HoneyLanguage.TraditionalChinese.code -> "Rinkeby測試網（GoldStone）"
		else -> ""
	}
	@JvmField
	val etcMorden = when (currentLanguage) {
		HoneyLanguage.English.code -> "Morden (GasTracker)"
		HoneyLanguage.Chinese.code -> "Morden测试网（GasTracker）"
		HoneyLanguage.Japanese.code -> "モルデン試験ネットワーク（GasTracker）"
		HoneyLanguage.Korean.code -> "Morden 테스트 네트워크 (GasTracker)"
		HoneyLanguage.Russian.code -> "Сеть тестирования Мордена (GasTracker)"
		HoneyLanguage.TraditionalChinese.code -> "Morden測試網（GasTracker）"
		else -> ""
	}
	@JvmField
	val goldStoneEtcMain = when (currentLanguage) {
		HoneyLanguage.English.code -> "ETC Mainnet (GoldStone)"
		HoneyLanguage.Chinese.code -> "ETC Mainnet（GoldStone）"
		HoneyLanguage.Japanese.code -> "ETCメインネット（ゴールドストーン）"
		HoneyLanguage.Korean.code -> "ETC Mainnet (골드 스톤)"
		HoneyLanguage.Russian.code -> "ETC Mainnet (GoldStone)"
		HoneyLanguage.TraditionalChinese.code -> "ETC Mainnet（GoldStone）"
		else -> ""
	}
	@JvmField
	val goldStoneEtcMorderTest = when (currentLanguage) {
		HoneyLanguage.English.code -> "Morden (GoldStone)"
		HoneyLanguage.Chinese.code -> "Morden测试网（GoldStone）"
		HoneyLanguage.Japanese.code -> "Mordenテストネットワーク（GoldStone）"
		HoneyLanguage.Korean.code -> "Morden 테스트 네트워크 (GoldStone)"
		HoneyLanguage.Russian.code -> "Сеть тестирования Morden (GoldStone)"
		HoneyLanguage.TraditionalChinese.code -> "Morden測試網（GoldStone）"
		else -> ""
	}
	@JvmField
	val etcMainGasTracker = when (currentLanguage) {
		HoneyLanguage.English.code -> "ETC Mainnet (GasTracker)"
		HoneyLanguage.Chinese.code -> "ETC主网（GasTracker）"
		HoneyLanguage.Japanese.code -> "ETCメインネットワーク（GasTracker）"
		HoneyLanguage.Korean.code -> "기타 주요 네트워크 (GasTracker)"
		HoneyLanguage.Russian.code -> "Основная сеть ETC (GasTracker)"
		HoneyLanguage.TraditionalChinese.code -> "ETC主網（GasTracker）"
		else -> ""
	}
	@JvmField
	val btcMain = when (currentLanguage) {
		HoneyLanguage.English.code -> "BTC Mainnet (GoldStone)"
		HoneyLanguage.Chinese.code -> "BTC主网（GoldStone）"
		HoneyLanguage.Japanese.code -> "BTCメインネットワーク（GoldStone）"
		HoneyLanguage.Korean.code -> "BTC 주요 네트워크 (GoldStone)"
		HoneyLanguage.Russian.code -> "Основная сеть BTC (GoldStone)"
		HoneyLanguage.TraditionalChinese.code -> "BTC主網（GoldStone）"
		else -> ""
	}
	@JvmField
	val btcTest = when (currentLanguage) {
		HoneyLanguage.English.code -> "BTC Testnet (GoldStone)"
		HoneyLanguage.Chinese.code -> "BTC 测试网（GoldStone）"
		HoneyLanguage.Japanese.code -> "BTCテストネットワーク（GoldStone）"
		HoneyLanguage.Korean.code -> "BTC 테스트 네트워크 (GoldStone)"
		HoneyLanguage.Russian.code -> "Сеть тестирования BTC (GoldStone)"
		HoneyLanguage.TraditionalChinese.code -> "BTC測試網（GoldStone）"
		else -> ""
	}
}