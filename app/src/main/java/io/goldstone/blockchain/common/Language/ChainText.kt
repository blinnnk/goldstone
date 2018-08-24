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
		HoneyLanguage.Chinese.code -> "正在使用"
		HoneyLanguage.Japanese.code -> "使用中"
		HoneyLanguage.Korean.code -> "사용중"
		HoneyLanguage.Russian.code -> "При использовании"
		HoneyLanguage.TraditionalChinese.code -> "正在使用"
		else -> ""
	}
	@JvmField
	val mainnet = when (currentLanguage) {
		HoneyLanguage.English.code -> "Mainnet"
		HoneyLanguage.Chinese.code -> "主网"
		HoneyLanguage.Japanese.code -> "メインネット"
		HoneyLanguage.Korean.code -> "메인 넷"
		HoneyLanguage.Russian.code -> "Основная сеть"
		HoneyLanguage.TraditionalChinese.code -> "主網"
		else -> ""
	}
	@JvmField
	val mainnetDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "The formal production environment \nwhere the assets are of real value"
		HoneyLanguage.Chinese.code -> "正式环境\n资产具有真正价值"
		HoneyLanguage.Japanese.code -> "正式な環境\n資産は本当の価値がある"
		HoneyLanguage.Korean.code -> "공식 생산 환경 \n자산이 실제 가치를 지닌 곳"
		HoneyLanguage.Russian.code -> "Формальная среда \nактивы имеют реальную ценность"
		HoneyLanguage.TraditionalChinese.code -> "正式生產環境 \n資產具有真正價值"
		else -> ""
	}
	@JvmField
	val testnet = when (currentLanguage) {
		HoneyLanguage.English.code -> "Testnet"
		HoneyLanguage.Chinese.code -> "测试网络"
		HoneyLanguage.Japanese.code -> "テスト"
		HoneyLanguage.Korean.code -> "테스트 넷"
		HoneyLanguage.Russian.code -> "Тест"
		HoneyLanguage.TraditionalChinese.code -> "測試網絡"
		else -> ""
	}
	@JvmField
	val testnetDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "The chain used for testing, test assets \n are usually worthless."
		HoneyLanguage.Chinese.code -> "测试环境\n测试资产是没有价值的"
		HoneyLanguage.Japanese.code -> "テスト環境 \n資産をテストする価値がない"
		HoneyLanguage.Korean.code -> "테스트 환경, 자산 테스트는 쓸모가 없다."
		HoneyLanguage.Russian.code -> "Условия тестирования, тестирование активов бесполезно"
		HoneyLanguage.TraditionalChinese.code -> "測試鏈上的資產通常是沒有價值的。"
		else -> ""
	}
	@JvmField
	val goldStoneMain = when (currentLanguage) {
		HoneyLanguage.English.code -> "Ethereum (GoldStone)"
		HoneyLanguage.Chinese.code -> "Ethereum (GoldStone)"
		HoneyLanguage.Japanese.code -> "Ethereum (GoldStone)"
		HoneyLanguage.Korean.code -> "Ethereum (GoldStone)"
		HoneyLanguage.Russian.code -> "Ethereum (GoldStone)"
		HoneyLanguage.TraditionalChinese.code -> "Ethereum (GoldStone)"
		else -> ""
	}
	@JvmField
	val infuraMain = when (currentLanguage) {
		HoneyLanguage.English.code -> "Ethereum (Infura)"
		HoneyLanguage.Chinese.code -> "Ethereum (Infura)"
		HoneyLanguage.Japanese.code -> "Ethereum (Infura)"
		HoneyLanguage.Korean.code -> "Ethereum (Infura)"
		HoneyLanguage.Russian.code -> "Ethereum (Infura)"
		HoneyLanguage.TraditionalChinese.code -> "Ethereum (Infura)"
		else -> ""
	}
	@JvmField
	val ropsten = when (currentLanguage) {
		HoneyLanguage.English.code -> "Ropsten (GoldStone)"
		HoneyLanguage.Chinese.code -> "Ropsten (GoldStone)"
		HoneyLanguage.Japanese.code -> "Ropsten (GoldStone)"
		HoneyLanguage.Korean.code -> "Ropsten (GoldStone)"
		HoneyLanguage.Russian.code -> "Ropsten (GoldStone)"
		HoneyLanguage.TraditionalChinese.code -> "Ropsten (GoldStone)"
		else -> ""
	}
	@JvmField
	val infuraRopsten = when (currentLanguage) {
		HoneyLanguage.English.code -> "Ropsten (Infura)"
		HoneyLanguage.Chinese.code -> "Ropsten (Infura)"
		HoneyLanguage.Japanese.code -> "Ropsten (Infura)"
		HoneyLanguage.Korean.code -> "Ropsten (Infura)"
		HoneyLanguage.Russian.code -> "Ropsten (Infura)"
		HoneyLanguage.TraditionalChinese.code -> "Ropsten (Infura)"
		else -> ""
	}
	@JvmField
	val infuraKovan = when (currentLanguage) {
		HoneyLanguage.English.code -> "Kovan (Infura)"
		HoneyLanguage.Chinese.code -> "Kovan (Infura)"
		HoneyLanguage.Japanese.code -> "Kovan (Infura)"
		HoneyLanguage.Korean.code -> "Kovan (Infura)"
		HoneyLanguage.Russian.code -> "Kovan (Infura)"
		HoneyLanguage.TraditionalChinese.code -> "Kovan (Infura)"
		else -> ""
	}
	@JvmField
	val infuraRinkeby = when (currentLanguage) {
		HoneyLanguage.English.code -> "Rinkeby (Infura)"
		HoneyLanguage.Chinese.code -> "Rinkeby (Infura)"
		HoneyLanguage.Japanese.code -> "Rinkeby (Infura)"
		HoneyLanguage.Korean.code -> "Rinkeby (Infura)"
		HoneyLanguage.Russian.code -> "Rinkeby (Infura)"
		HoneyLanguage.TraditionalChinese.code -> "Rinkeby (Infura)"
		else -> ""
	}
	@JvmField
	val kovan = when (currentLanguage) {
		HoneyLanguage.English.code -> "Kovan (GoldStone)"
		HoneyLanguage.Chinese.code -> "Kovan（GoldStone）"
		HoneyLanguage.Japanese.code -> "Kovan（GoldStone）"
		HoneyLanguage.Korean.code -> "Kovan (GoldStone)"
		HoneyLanguage.Russian.code -> "Kovan (GoldStone)"
		HoneyLanguage.TraditionalChinese.code -> "Kovan（GoldStone）"
		else -> ""
	}
	@JvmField
	val rinkeby = when (currentLanguage) {
		HoneyLanguage.English.code -> "Rinkeby (GoldStone)"
		HoneyLanguage.Chinese.code -> "Rinkeby（GoldStone）"
		HoneyLanguage.Japanese.code -> "Rinkeby（GoldStone）"
		HoneyLanguage.Korean.code -> "Rinkeby (GoldStone)"
		HoneyLanguage.Russian.code -> "Rinkeby (GoldStone)"
		HoneyLanguage.TraditionalChinese.code -> "Rinkeby（GoldStone）"
		else -> ""
	}
	@JvmField
	val etcMorden = when (currentLanguage) {
		HoneyLanguage.English.code -> "Morden (GasTracker)"
		HoneyLanguage.Chinese.code -> "Morden（GasTracker）"
		HoneyLanguage.Japanese.code -> "Morden（GasTracker）"
		HoneyLanguage.Korean.code -> "Morden (GasTracker)"
		HoneyLanguage.Russian.code -> "Morden (GasTracker)"
		HoneyLanguage.TraditionalChinese.code -> "Morden（GasTracker）"
		else -> ""
	}
	@JvmField
	val goldStoneEtcMain = when (currentLanguage) {
		HoneyLanguage.English.code -> "ETC (GoldStone)"
		HoneyLanguage.Chinese.code -> "ETC (GoldStone)"
		HoneyLanguage.Japanese.code -> "ETC (GoldStone)"
		HoneyLanguage.Korean.code -> "ETC (GoldStone)"
		HoneyLanguage.Russian.code -> "ETC (GoldStone)"
		HoneyLanguage.TraditionalChinese.code -> "ETC (GoldStone)"
		else -> ""
	}
	@JvmField
	val goldStoneEtcMordenTest = when (currentLanguage) {
		HoneyLanguage.English.code -> "Morden (GoldStone)"
		HoneyLanguage.Chinese.code -> "Morden (GoldStone)"
		HoneyLanguage.Japanese.code -> "Morden (GoldStone)"
		HoneyLanguage.Korean.code -> "Morden (GoldStone)"
		HoneyLanguage.Russian.code -> "Morden (GoldStone)"
		HoneyLanguage.TraditionalChinese.code -> "Morden (GoldStone)"
		else -> ""
	}
	@JvmField
	val etcMainGasTracker = when (currentLanguage) {
		HoneyLanguage.English.code -> "ETC (GasTracker)"
		HoneyLanguage.Chinese.code -> "ETC (GasTracker)"
		HoneyLanguage.Japanese.code -> "ETC (GasTracker)"
		HoneyLanguage.Korean.code -> "ETC (GasTracker)"
		HoneyLanguage.Russian.code -> "ETC (GasTracker)"
		HoneyLanguage.TraditionalChinese.code -> "ETC (GasTracker)"
		else -> ""
	}
	@JvmField
	val btcMain = when (currentLanguage) {
		HoneyLanguage.English.code -> "BTC (GoldStone)"
		HoneyLanguage.Chinese.code -> "BTC (GoldStone)"
		HoneyLanguage.Japanese.code -> "BTC (GoldStone)"
		HoneyLanguage.Korean.code -> "BTC (GoldStone)"
		HoneyLanguage.Russian.code -> "BTC (GoldStone)"
		HoneyLanguage.TraditionalChinese.code -> "BTC (GoldStone)"
		else -> ""
	}
	@JvmField
	val ltcMain = when (currentLanguage) {
		HoneyLanguage.English.code -> "LTC (GoldStone)"
		HoneyLanguage.Chinese.code -> "LTC (GoldStone)"
		HoneyLanguage.Japanese.code -> "LTC (GoldStone)"
		HoneyLanguage.Korean.code -> "LTC (GoldStone)"
		HoneyLanguage.Russian.code -> "LTC (GoldStone)"
		HoneyLanguage.TraditionalChinese.code -> "LTC (GoldStone)"
		else -> ""
	}
	@JvmField
	val bchMain = when (currentLanguage) {
		HoneyLanguage.English.code -> "BCH (GoldStone)"
		HoneyLanguage.Chinese.code -> "BCH (GoldStone)"
		HoneyLanguage.Japanese.code -> "BCH (GoldStone)"
		HoneyLanguage.Korean.code -> "BCH (GoldStone)"
		HoneyLanguage.Russian.code -> "BCH (GoldStone)"
		HoneyLanguage.TraditionalChinese.code -> "BCH (GoldStone)"
		else -> ""
	}
	@JvmField
	val btcTest = when (currentLanguage) {
		HoneyLanguage.English.code -> "Testnet (GoldStone)"
		HoneyLanguage.Chinese.code -> "测试网（GoldStone）"
		HoneyLanguage.Japanese.code -> "テスト（GoldStone）"
		HoneyLanguage.Korean.code -> "테스트 (GoldStone)"
		HoneyLanguage.Russian.code -> "Тест (GoldStone)"
		HoneyLanguage.TraditionalChinese.code -> "測試網（GoldStone）"
		else -> ""
	}
	@JvmField
	val ltcTest = when (currentLanguage) {
		HoneyLanguage.English.code -> "Testnet (GoldStone)"
		HoneyLanguage.Chinese.code -> "测试网（GoldStone）"
		HoneyLanguage.Japanese.code -> "テスト（GoldStone）"
		HoneyLanguage.Korean.code -> "테스트 (GoldStone)"
		HoneyLanguage.Russian.code -> "Тест (GoldStone)"
		HoneyLanguage.TraditionalChinese.code -> "測試網（GoldStone）"
		else -> ""
	}
	@JvmField
	val bchTest = when (currentLanguage) {
		HoneyLanguage.English.code -> "Testnet (GoldStone)"
		HoneyLanguage.Chinese.code -> "测试网（GoldStone）"
		HoneyLanguage.Japanese.code -> "テスト（GoldStone）"
		HoneyLanguage.Korean.code -> "테스트 (GoldStone)"
		HoneyLanguage.Russian.code -> "Тест (GoldStone)"
		HoneyLanguage.TraditionalChinese.code -> "測試網（GoldStone）"
		else -> ""
	}
}