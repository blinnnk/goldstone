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
		HoneyLanguage.Chinese.code -> "正式环境，资产具有真正价值"
		HoneyLanguage.Japanese.code -> "正式な環境\n資産は本当の価値がある"
		HoneyLanguage.Korean.code -> "공식 생산 환경 \n자산이 실제 가치를 지닌 곳"
		HoneyLanguage.Russian.code -> "Формальная среда \nактивы имеют реальную ценность"
		HoneyLanguage.TraditionalChinese.code -> "正式環境，資產具有真正價值"
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
		HoneyLanguage.Chinese.code -> "测试环境测试资产是没有价值的"
		HoneyLanguage.Japanese.code -> "テスト環境資産をテストする価値がない"
		HoneyLanguage.Korean.code -> "테스트 환경, 자산 테스트는 쓸모가 없다."
		HoneyLanguage.Russian.code -> "Условия тестирования, тестирование активов бесполезно"
		HoneyLanguage.TraditionalChinese.code -> "測試鏈上的資產通常是沒有價值的。"
		else -> ""
	}
}