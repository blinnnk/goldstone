package io.goldstone.blockchain.common.language

/**
 * @date 2018/8/8 2:13 AM
 * @author KaySaith
 */
object AlertText {

	@JvmField
	val mainnetOnly = when (currentLanguage) {
		HoneyLanguage.English.code -> "This is a wallet dedicated to the main network. You can not switch to the test network."
		HoneyLanguage.Chinese.code -> "这是一个仅限主网使用的钱包。您无法切换到测试网。"
		HoneyLanguage.Japanese.code -> "これはメインネットワーク専用のウォレットです。 テストネットワークに切り替えることはできません。"
		HoneyLanguage.Korean.code -> "이것은 기본 네트워크 전용 지갑입니다. 테스트 네트워크로 전환 할 수 없습니다."
		HoneyLanguage.Russian.code -> "Это кошелек, посвященный основной сети. Вы не можете переключиться на тестовую сеть."
		HoneyLanguage.TraditionalChinese.code -> "這是一個僅限主網使用的錢包。您無法切換到測試網。"
		else -> ""
	}
	@JvmField
	val testnetOnly = when (currentLanguage) {
		HoneyLanguage.English.code -> "This is a wallet dedicated to the test network. You can not switch to the main network."
		HoneyLanguage.Chinese.code -> "这是一个仅限测试网使用的钱包。您无法切换到主网。"
		HoneyLanguage.Japanese.code -> "これは テストネットワーク専用のウォレットです。 メインネットワークに切り替えることはできません。"
		HoneyLanguage.Korean.code -> "이것은 테스트 네트워크 전용 지갑입니다. 주요 네트워크로 전환 할 수 없습니다."
		HoneyLanguage.Russian.code -> "Это кошелек, посвященный тестовой сети. Вы не можете переключиться на основную сеть."
		HoneyLanguage.TraditionalChinese.code -> "這是一個僅限測試網使用的錢包。您無法切換到主網。"
		else -> ""
	}
	@JvmField
	val btcWalletOnly = when (currentLanguage) {
		HoneyLanguage.English.code -> "The current wallet supports Bitcoin only and cannot search for other types of digital currency information asset management."
		HoneyLanguage.Chinese.code -> "目前的钱包仅支持比特币，无法搜索其他类型的数字货币进行资产管理。"
		HoneyLanguage.Japanese.code -> "現在のウォレットはBitcoinのみをサポートしており、資産管理のために他の種類のデジタル通貨を検索することはできません。"
		HoneyLanguage.Korean.code -> "현재 지갑은 Bitcoin 만 지원하며 자산 관리를 위해 다른 유형의 디지털 통화를 검색 할 수 없습니다."
		HoneyLanguage.Russian.code -> "Текущий кошелек поддерживает только биткойн и не может искать другие типы цифровой валюты для управления активами."
		HoneyLanguage.TraditionalChinese.code -> "目前的錢包僅支持比特幣，無法搜索其他類型的數字貨幣進行資產管理。"
		else -> ""
	}
	@JvmField
	val watchOnly = when (currentLanguage) {
		HoneyLanguage.English.code -> "Current wallet is watch-only. You cannot transfer or deposit"
		HoneyLanguage.Chinese.code -> "这是观察钱包，无法进行转账交易。"
		HoneyLanguage.Japanese.code -> "本ウォレットは観察用のため、振込取引をすることは出来ません"
		HoneyLanguage.Korean.code -> "이것은 관찰지갑으로, 이체 거래를 할수 없습니다. "
		HoneyLanguage.Russian.code -> "В данный момент можно только просмотреть кошелек, нельзя выполнить операции перевода."
		HoneyLanguage.TraditionalChinese.code -> "這是觀察錢包，無法進行轉賬交易。"
		else -> ""
	}

	@JvmField
	val transferWrongDecimal = when (currentLanguage) {
		HoneyLanguage.English.code -> "This decimal is not supported by this token. Please input a shorter decimal."
		HoneyLanguage.Chinese.code -> "当前的token不支持你输入的小数位数，请修改金额重新提交"
		HoneyLanguage.Japanese.code -> "現在のTokenはお客様の入力された小数点以下の桁数をサポートしていません。金額を変更してもう一度提出して下さい"
		HoneyLanguage.Korean.code -> "현재 token은 귀하께서 입력한 소수자리수를 지원하지 않습니다, 금액을 변경한후 다시 제출하십시오"
		HoneyLanguage.Russian.code -> "Текущий token не поддерживает количество введенных десятичных знаков, пожалуйста, исправьте сумму и отправьте ее заново."
		HoneyLanguage.TraditionalChinese.code -> "當前的token不支持您輸入的小數位數，請修改金額重新提交"
		else -> ""
	}

	@JvmField
	val gasEditorEmpty = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please set a gas price and gas limit"
		HoneyLanguage.Chinese.code -> "请设置燃气费的单价与上限"
		HoneyLanguage.Japanese.code -> "ガス費の単価と上限を設定して下さい"
		HoneyLanguage.Korean.code -> "마이너 비용 정보를 설정하지 않았습니다"
		HoneyLanguage.Russian.code -> "Установите цену и лимит платы за газ"
		HoneyLanguage.TraditionalChinese.code -> "請設置燃氣費的單價與上限"
		else -> ""
	}
	@JvmField
	val gasLimitValue = when (currentLanguage) {
		HoneyLanguage.English.code -> "Gas limit must bigger than estimate gas value"
		HoneyLanguage.Chinese.code -> "矿工费上限不能低于预估值"
		HoneyLanguage.Japanese.code -> "マイニング費の上限は見積もり価格を下回ることはできません"
		HoneyLanguage.Korean.code -> "가스 한도는 예상 가스 가치보다 커야합니다."
		HoneyLanguage.Russian.code -> "Верхний лимит платы майнера не может быть ниже предварительной стоимости"
		HoneyLanguage.TraditionalChinese.code -> "礦工費上限不能低於預估值"
		else -> ""
	}
	@JvmField
	val transferUnvalidInputFormat = when (currentLanguage) {
		HoneyLanguage.English.code -> "Incorrect value format, please check again"
		HoneyLanguage.Chinese.code -> "请输入正确的金额"
		HoneyLanguage.Japanese.code -> "正確な金額を入力して下さい"
		HoneyLanguage.Korean.code -> "귀하께서 입력한 금액 포맷 오류"
		HoneyLanguage.Russian.code -> "Пожалуйста, введите корректную сумму"
		HoneyLanguage.TraditionalChinese.code -> "請輸入正確的金額"
		else -> ""
	}
	@JvmField
	val switchLanguage = when (currentLanguage) {
		HoneyLanguage.English.code -> "Once you selected it, application will be rebooted and just wait several seconds. Are You Sure To Switch Language Settings?"
		HoneyLanguage.Chinese.code -> "一旦你选择切换语言，应用程序将被重新启动，并等待几秒钟。 你确定要切换货币设置吗？"
		HoneyLanguage.Japanese.code -> "言語の切り替えを選択すると、アプリケーションが再起動され、数秒待つことになります。貨幣設定の切り替えをしますか？"
		HoneyLanguage.Korean.code -> "언어를 전환하면 응용 프로그램이 다시 시작되고 몇 초 기다립니다. 통화 설정을 전환 하시겠습니까?"
		HoneyLanguage.Russian.code -> "При смене языка программа будет перезапущена, ожидание составит несколько секунд. Вы уверены, что хотите изменить настройки валюты?"
		HoneyLanguage.TraditionalChinese.code -> "一旦你選擇切換語言，應用程序將被重新啟動，並等待幾秒鐘。你確定要切換貨幣設置嗎？"
		else -> ""
	}
	@JvmField
	val switchLanguageConfirmText = when (currentLanguage) {
		HoneyLanguage.English.code -> "Change Language"
		HoneyLanguage.Chinese.code -> "切换语言"
		HoneyLanguage.Japanese.code -> "言語の切替"
		HoneyLanguage.Korean.code -> "언어 변경"
		HoneyLanguage.Russian.code -> "Сменить язык"
		HoneyLanguage.TraditionalChinese.code -> "切換語言"
		else -> ""
	}

	@JvmField
	val transferRequestTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Quick Payment"
		HoneyLanguage.Chinese.code -> "快捷支付"
		HoneyLanguage.Japanese.code -> "クイック支払い"
		HoneyLanguage.Korean.code -> "빠른 지불"
		HoneyLanguage.Russian.code -> "Быстрая оплата"
		HoneyLanguage.TraditionalChinese.code -> "快捷支付"
		else -> ""
	}
	@JvmField
	val transferRequestDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "Currently Dapp wants to initiate a transfer to the chain, please confirm."
		HoneyLanguage.Chinese.code -> "当前 Dapp 想要向链发起转账，请确认。"
		HoneyLanguage.Japanese.code -> "現在、Dappはチェーンへの転送を開始したいので、確認してください。"
		HoneyLanguage.Korean.code -> "현재 Dapp는 체인으로 이전을 원합니다. 확인하십시오."
		HoneyLanguage.Russian.code -> "В настоящее время Dapp хочет инициировать перевод в сеть, пожалуйста, подтвердите."
		HoneyLanguage.TraditionalChinese.code -> "當前 Dapp 想要向鏈發起轉賬，請確認。"
		else -> ""
	}
	@JvmField
	val signRequestTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Authentication"
		HoneyLanguage.Chinese.code -> "身份验证"
		HoneyLanguage.Japanese.code -> "認証"
		HoneyLanguage.Korean.code -> "인증"
		HoneyLanguage.Russian.code -> "идентификация"
		HoneyLanguage.TraditionalChinese.code -> "身份驗證"
		else -> ""
	}
	@JvmField
	val signRequestDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "Currently Dapp wants to sign the chain for authentication, please confirm."
		HoneyLanguage.Chinese.code -> "当前 Dapp 想要向链发起签名以进行身份验证，请确认。"
		HoneyLanguage.Japanese.code -> "現在、Dappは認証のためにチェーンに署名したいと考えています。確認してください。"
		HoneyLanguage.Korean.code -> "현재 Dapp는 인증을 위해 체인에 서명하려고합니다. 확인하십시오."
		HoneyLanguage.Russian.code -> "В настоящее время Dapp хочет подписать цепочку для аутентификации, пожалуйста, подтвердите."
		HoneyLanguage.TraditionalChinese.code -> "當前 Dapp 想要向鏈發起簽名以進行身份驗證，請確認。"
		else -> ""
	}
	@JvmField
	val signDataRequestTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Sign Data"
		HoneyLanguage.Chinese.code -> "数据签名"
		HoneyLanguage.Japanese.code -> "データ署名"
		HoneyLanguage.Korean.code -> "데이터 서명"
		HoneyLanguage.Russian.code -> "Подпись данных"
		HoneyLanguage.TraditionalChinese.code -> "數據簽名"
		else -> ""
	}
	@JvmField
	val signDataRequestDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "Currently Dapp wants to initiate a custom data signature to the chain, please confirm the data content is correct, and then confirm the transfer."
		HoneyLanguage.Chinese.code -> "当前 Dapp 想要向链发起自定义数据签名，请确认数据内容无误，再确认转账。"
		HoneyLanguage.Japanese.code -> "現在、Dappはチェーンへのカスタムデータ署名を開始したいので、データの内容が正しいことを確認してから転送を確認してください。"
		HoneyLanguage.Korean.code -> "현재 Dapp는 체인에 맞춤 데이터 서명을 시작하고 데이터 내용이 올바른지 확인한 다음 전송을 확인하려고합니다."
		HoneyLanguage.Russian.code -> "В настоящее время Dapp хочет инициировать пользовательскую подпись данных в цепочке, пожалуйста, подтвердите правильность содержимого данных и затем подтвердите передачу."
		HoneyLanguage.TraditionalChinese.code -> "當前 Dapp 想要向鏈發起自定義數據簽名，請確認數據內容無誤，再確認轉賬。"
		else -> ""
	}
}