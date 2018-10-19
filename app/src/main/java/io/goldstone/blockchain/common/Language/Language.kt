@file:Suppress("DEPRECATION")

package io.goldstone.blockchain.common.language

import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.value.CountryCode

/**
 * @date 21/03/2018 7:34 PM
 * @author KaySaith
 */
var currentLanguage = when {
	SharedWallet.getCurrentLanguageCode() == 100 -> HoneyLanguage.getCodeBySymbol(CountryCode.currentLanguageSymbol)
	HoneyLanguage.currentLanguageIsSupported() -> SharedWallet.getCurrentLanguageCode()
	else -> HoneyLanguage.English.code
}

object WatchOnlyText {
	@JvmField
	val enterDescription = when(currentLanguage) {
		HoneyLanguage.English.code -> "Enter the address of the wallet to be observed"
		HoneyLanguage.Chinese.code -> "请输入想观察的钱包地址"
		HoneyLanguage.Japanese.code -> "確認したいウォレットのアドレスを入力して下さい"
		HoneyLanguage.Korean.code -> "관찰하고 싶은 지갑 주소를 입력하세요."
		HoneyLanguage.Russian.code -> "Пожалуйста, введите адрес кошелька, который Вы хотите просмотреть"
		HoneyLanguage.TraditionalChinese.code -> "輸入要觀察的錢包地址"
		else -> ""
	}
	@JvmField
	val intro = when(currentLanguage) {
		HoneyLanguage.English.code -> "You are convinced your brain is working at peak efficiency today, yet you wonder why you continue to run into obstacles that you"
		HoneyLanguage.Chinese.code -> "使用观察钱包, 您无需导入私钥，只需要输入钱包地址, 就能查看该地址下所有余额与转账信息。"
		HoneyLanguage.Japanese.code -> "ウォレットの確認を使用する際、プライベートキーのインポートをする必要はありません。ウォレットのアドレスを入力するだけで、当該アドレスの全ての残高と振込情報が確認できます。"
		HoneyLanguage.Korean.code -> "시계 월렛을 사용하면 비공개 키를 가져올 필요가 없습니다. 지갑 주소를 입력하면 잔액을 확인하고 해당 주소로 정보를 전송하면됩니다."
		HoneyLanguage.Russian.code -> "При просмотре кошелька не требуется импорт закрытого ключа, введите только адрес кошелька и Вы сможете сможете просмотреть всю информацию баланса и переводов данного адреса."
		HoneyLanguage.TraditionalChinese.code -> "使用觀察錢包, 您無需導入私鑰，只需要輸入錢包地址, 就能查看該地址下所有餘額與轉賬信息。"
		else -> ""
	}
}

object NotificationText {
	@JvmField
	val notification = when(currentLanguage) {
		HoneyLanguage.English.code -> "Notifications"
		HoneyLanguage.Chinese.code -> "通知中心"
		HoneyLanguage.Japanese.code -> "メッセージセンター"
		HoneyLanguage.Korean.code -> "알림센터"
		HoneyLanguage.Russian.code -> "Центр уведомлений"
		HoneyLanguage.TraditionalChinese.code -> "通知中心"
		else -> ""
	}
}

object TokenManagementText {
	@JvmField
	val addToken = when(currentLanguage) {
		HoneyLanguage.English.code -> "Add Token"
		HoneyLanguage.Chinese.code -> "添加其他币种"
		HoneyLanguage.Japanese.code -> "他の貨幣を追加する"
		HoneyLanguage.Korean.code -> "토큰 추가"
		HoneyLanguage.Russian.code -> "Добавить другие валюты"
		HoneyLanguage.TraditionalChinese.code -> "添加其他幣種"
		else -> ""
	}
}

object Alert {
	@JvmField
	val selectCurrency = when(currentLanguage) {
		HoneyLanguage.English.code -> "Once you've selected this, you'll need to wait a moment while we restart the app. Are you sure you'd like to switch currency settings?"
		HoneyLanguage.Chinese.code -> "一旦你选择切换货币，应用程序将被重新启动，并等待几秒钟。 你确定要切换货币设置吗？"
		HoneyLanguage.Japanese.code -> "貨幣の切り替えを選択すると、アプリケーションが再起動され、数秒待つことになります貨幣設定の切り替えをしますか？"
		HoneyLanguage.Korean.code -> "일단 화폐 교체를 선택하면, 응용 프로그램은 다시 시작후 몇초동안 기다려야 합니다. 화폐 설정을 교체할까요？"
		HoneyLanguage.Russian.code -> "При смене валюты программа будет перезапущена, ожидание составит несколько секунд. Вы уверены, что хотите изменить настройки валюты?"
		HoneyLanguage.TraditionalChinese.code -> "一旦你選擇切換貨幣，應用程序將被重新啟動，並等待幾秒鐘。你確定要切換貨幣設置嗎？"
		else -> ""
	}
}

object PincodeText {
	@JvmField
	val needToVerifyYourIdentity = when(currentLanguage) {
		HoneyLanguage.English.code -> "Verify your identity"
		HoneyLanguage.Chinese.code -> "Verify your identity"
		HoneyLanguage.Japanese.code -> "Verify your identity"
		HoneyLanguage.Korean.code -> "Verify your identity"
		HoneyLanguage.Russian.code -> "Verify your identity"
		HoneyLanguage.TraditionalChinese.code -> "Verify your identity"
		else -> ""
	}
	@JvmField
	val pleaseEnterYourCurrentNumericPassword = when(currentLanguage) {
		HoneyLanguage.English.code -> "Please enter your current numeric password"
		HoneyLanguage.Chinese.code -> "Please enter your current numeric password"
		HoneyLanguage.Japanese.code -> "Please enter your current numeric password"
		HoneyLanguage.Korean.code -> "Please enter your current numeric password"
		HoneyLanguage.Russian.code -> "Please enter your current numeric password"
		HoneyLanguage.TraditionalChinese.code -> "Please enter your current numeric password"
		else -> ""
	}
	@JvmField
	val setFourDigitPassword = when(currentLanguage) {
		HoneyLanguage.English.code -> "Set a four-digit password"
		HoneyLanguage.Chinese.code -> "Set a four-digit password"
		HoneyLanguage.Japanese.code -> "Set a four-digit password"
		HoneyLanguage.Korean.code -> "Set a four-digit password"
		HoneyLanguage.Russian.code -> "Set a four-digit password"
		HoneyLanguage.TraditionalChinese.code -> "Set a four-digit password"
		else -> ""
	}
	@JvmField
	val resetTheFour_digitPassword = when(currentLanguage) {
		HoneyLanguage.English.code -> "Reset four-digit password"
		HoneyLanguage.Chinese.code -> "Reset four-digit password"
		HoneyLanguage.Japanese.code -> "Reset four-digit password"
		HoneyLanguage.Korean.code -> "Reset four-digit password"
		HoneyLanguage.Russian.code -> "Reset four-digit password"
		HoneyLanguage.TraditionalChinese.code -> "Reset four-digit password"
		else -> ""
	}
	@JvmField
	val ifThePasswordInputIsInconsistentPleaseRe_enter = when(currentLanguage) {
		HoneyLanguage.English.code -> "If the password input is inconsistent, please re-enter"
		HoneyLanguage.Chinese.code -> "If the password input is inconsistent, please re-enter"
		HoneyLanguage.Japanese.code -> "If the password input is inconsistent, please re-enter"
		HoneyLanguage.Korean.code -> "If the password input is inconsistent, please re-enter"
		HoneyLanguage.Russian.code -> "If the password input is inconsistent, please re-enter"
		HoneyLanguage.TraditionalChinese.code -> "If the password input is inconsistent, please re-enter"
		else -> ""
	}

	@JvmField
	val failedAttention: (retryTimes: Int) -> String = {
		when(currentLanguage) {
			HoneyLanguage.English.code -> "incorrect passcode $it retry times left"
			HoneyLanguage.Chinese.code -> "incorrect passcode $it retry times left"
			HoneyLanguage.Japanese.code -> "incorrect passcode $it retry times left"
			HoneyLanguage.Korean.code -> "incorrect passcode $it retry times left"
			HoneyLanguage.Russian.code -> "incorrect passcode $it retry times left"
			HoneyLanguage.TraditionalChinese.code -> "incorrect passcode $it retry times left"
			else -> ""
		}
	}
	@JvmField
	val remainingFrozenTime: (currentFrozenTime: Long) -> String = {
		when(currentLanguage) {
			HoneyLanguage.English.code -> "you have to wait ${it / 1000} seconds"
			HoneyLanguage.Chinese.code -> "you have to wait ${it / 1000} seconds"
			HoneyLanguage.Japanese.code -> "you have to wait ${it / 1000} seconds"
			HoneyLanguage.Korean.code -> "you have to wait ${it / 1000} seconds"
			HoneyLanguage.Russian.code -> "you have to wait ${it / 1000} seconds"
			HoneyLanguage.TraditionalChinese.code -> "you have to wait ${it / 1000} seconds"
			else -> ""
		}
	}

	@JvmField
	val show = when(currentLanguage) {
		HoneyLanguage.English.code -> "Show PIN"
		HoneyLanguage.Chinese.code -> "显示PIN码"
		HoneyLanguage.Japanese.code -> "PINコードを表示します"
		HoneyLanguage.Korean.code -> "PIN 코드 표시"
		HoneyLanguage.Russian.code -> "Показать PIN-код"
		HoneyLanguage.TraditionalChinese.code -> "顯示PIN碼"
		else -> ""
	}
	val setPinCode = when(currentLanguage) {
		HoneyLanguage.English.code -> "Digital password PIN setting"
		HoneyLanguage.Chinese.code -> "Digital password PIN setting"
		HoneyLanguage.Japanese.code -> "Digital password PIN setting"
		HoneyLanguage.Korean.code -> "Digital password PIN setting"
		HoneyLanguage.Russian.code -> "Digital password PIN setting"
		HoneyLanguage.TraditionalChinese.code -> "Digital password PIN setting"
		else -> ""
	}
	val changePinCode = when(currentLanguage) {
		HoneyLanguage.English.code -> "Change the numeric PIN code"
		HoneyLanguage.Chinese.code -> "Change the numeric PIN code"
		HoneyLanguage.Japanese.code -> "Change the numeric PIN code"
		HoneyLanguage.Korean.code -> "Change the numeric PIN code"
		HoneyLanguage.Russian.code -> "Change the numeric PIN code"
		HoneyLanguage.TraditionalChinese.code -> "Change the numeric PIN code"
		else -> ""
	}
	val setTheDigitalLock = when(currentLanguage) {
		HoneyLanguage.English.code -> "Set the digital lock"
		HoneyLanguage.Chinese.code -> "Set the digital lock"
		HoneyLanguage.Japanese.code -> "Set the digital lock"
		HoneyLanguage.Korean.code -> "Set the digital lock"
		HoneyLanguage.Russian.code -> "Set the digital lock"
		HoneyLanguage.TraditionalChinese.code -> "Set the digital lock"
		else -> ""
	}
	val goToSetPinCode = when(currentLanguage) {
		HoneyLanguage.English.code -> "To set a numeric password"
		HoneyLanguage.Chinese.code -> "To set a numeric password"
		HoneyLanguage.Japanese.code -> "To set a numeric password"
		HoneyLanguage.Korean.code -> "To set a numeric password"
		HoneyLanguage.Russian.code -> "To set a numeric password"
		HoneyLanguage.TraditionalChinese.code -> "To set a numeric password"
		else -> ""
	}
}

object FingerprintUnlockText {
	val fingerprintUnlock = when(currentLanguage) {
		HoneyLanguage.English.code -> "Fingerprint unlock"
		HoneyLanguage.Chinese.code -> "Fingerprint unlock"
		HoneyLanguage.Japanese.code -> "Fingerprint unlock"
		HoneyLanguage.Korean.code -> "Fingerprint unlock"
		HoneyLanguage.Russian.code -> "Fingerprint unlock"
		HoneyLanguage.TraditionalChinese.code -> "Fingerprint unlock"
		else -> ""
	}
	val attention = when(currentLanguage) {
		HoneyLanguage.English.code -> "Once you have set up your wallet, you need to unlock it to view your wallet. Wallet locks can better protect your privacy."
		HoneyLanguage.Chinese.code -> "Once you have set up your wallet, you need to unlock it to view your wallet. Wallet locks can better protect your privacy."
		HoneyLanguage.Japanese.code -> "Once you have set up your wallet, you need to unlock it to view your wallet. Wallet locks can better protect your privacy."
		HoneyLanguage.Korean.code -> "Once you have set up your wallet, you need to unlock it to view your wallet. Wallet locks can better protect your privacy."
		HoneyLanguage.Russian.code -> "Once you have set up your wallet, you need to unlock it to view your wallet. Wallet locks can better protect your privacy."
		HoneyLanguage.TraditionalChinese.code -> "Once you have set up your wallet, you need to unlock it to view your wallet. Wallet locks can better protect your privacy."
		else -> ""
	}
	val fingerprintIsOn = when(currentLanguage) {
		HoneyLanguage.English.code -> "Fingerprint is on"
		HoneyLanguage.Chinese.code -> "Fingerprint is on"
		HoneyLanguage.Japanese.code -> "Fingerprint is on"
		HoneyLanguage.Korean.code -> "Fingerprint is on"
		HoneyLanguage.Russian.code -> "Fingerprint is on"
		HoneyLanguage.TraditionalChinese.code -> "Fingerprint is on"
		else -> ""
	}
	val checkFingerprint = when(currentLanguage) {
		HoneyLanguage.English.code -> "Check fingerprint"
		HoneyLanguage.Chinese.code -> "Check fingerprint"
		HoneyLanguage.Japanese.code -> "Check fingerprint"
		HoneyLanguage.Korean.code -> "Check fingerprint"
		HoneyLanguage.Russian.code -> "Check fingerprint"
		HoneyLanguage.TraditionalChinese.code -> "Check fingerprint"
		else -> ""
	}
	val tryAgain = when(currentLanguage) {
		HoneyLanguage.English.code -> "try again"
		HoneyLanguage.Chinese.code -> "try again"
		HoneyLanguage.Japanese.code -> "try again"
		HoneyLanguage.Korean.code -> "try again"
		HoneyLanguage.Russian.code -> "try again"
		HoneyLanguage.TraditionalChinese.code -> "try again"
		else -> ""
	}
	val fingerprintOpeningPrompt = when(currentLanguage) {
		HoneyLanguage.English.code -> "We recommend that you turn on your digital password at the same time.\n" + "This way, when your fingerprint is not recognized correctly,\n" + "You can also unlock your wallet with a password."
		HoneyLanguage.Chinese.code -> "We recommend that you turn on your digital password at the same time.\n" + "This way, when your fingerprint is not recognized correctly,\n" + "You can also unlock your wallet with a password."
		HoneyLanguage.Japanese.code -> "We recommend that you turn on your digital password at the same time.\n" + "This way, when your fingerprint is not recognized correctly,\n" + "You can also unlock your wallet with a password."
		HoneyLanguage.Korean.code -> "We recommend that you turn on your digital password at the same time.\n" + "This way, when your fingerprint is not recognized correctly,\n" + "You can also unlock your wallet with a password."
		HoneyLanguage.Russian.code -> "We recommend that you turn on your digital password at the same time.\n" + "This way, when your fingerprint is not recognized correctly,\n" + "You can also unlock your wallet with a password."
		HoneyLanguage.TraditionalChinese.code -> "We recommend that you turn on your digital password at the same time.\n" + "This way, when your fingerprint is not recognized correctly,\n" + "You can also unlock your wallet with a password."
		else -> ""
	}
	val goToSetFingerprint = when(currentLanguage) {
		HoneyLanguage.English.code -> "Go to set the fingerprint"
		HoneyLanguage.Chinese.code -> "Go to set the fingerprint"
		HoneyLanguage.Japanese.code -> "Go to set the fingerprint"
		HoneyLanguage.Korean.code -> "Go to set the fingerprint"
		HoneyLanguage.Russian.code -> "Go to set the fingerprint"
		HoneyLanguage.TraditionalChinese.code -> "Go to set the fingerprint"
		else -> ""
	}
	val yourDeviceHasNotSetAFingerprintYet = when(currentLanguage) {
		HoneyLanguage.English.code -> "Your device has not set a fingerprint yet"
		HoneyLanguage.Chinese.code -> "Your device has not set a fingerprint yet"
		HoneyLanguage.Japanese.code -> "Your device has not set a fingerprint yet"
		HoneyLanguage.Korean.code -> "Your device has not set a fingerprint yet"
		HoneyLanguage.Russian.code -> "Your device has not set a fingerprint yet"
		HoneyLanguage.TraditionalChinese.code -> "Your device has not set a fingerprint yet"
		else -> ""
	}
	val fingerprintNotSetPrompt = when(currentLanguage) {
		HoneyLanguage.English.code -> "We detected that you are not currently set.Over fingerprint. After entering the fingerprint in the system settings.Let's turn on fingerprint recognition."
		HoneyLanguage.Chinese.code -> "We detected that you are not currently set.Over fingerprint. After entering the fingerprint in the system settings.Let's turn on fingerprint recognition."
		HoneyLanguage.Japanese.code -> "We detected that you are not currently set.Over fingerprint. After entering the fingerprint in the system settings.Let's turn on fingerprint recognition."
		HoneyLanguage.Korean.code -> "We detected that you are not currently set.Over fingerprint. After entering the fingerprint in the system settings.Let's turn on fingerprint recognition."
		HoneyLanguage.Russian.code -> "We detected that you are not currently set.Over fingerprint. After entering the fingerprint in the system settings.Let's turn on fingerprint recognition."
		HoneyLanguage.TraditionalChinese.code -> "We detected that you are not currently set.Over fingerprint. After entering the fingerprint in the system settings.Let's turn on fingerprint recognition."
		else -> ""
	}
	val unregisteredFingerprint = when(currentLanguage) {
		HoneyLanguage.English.code -> "The device does not record fingerprints. Please go to System -> Settings to add fingerprints."
		HoneyLanguage.Chinese.code -> "The device does not record fingerprints. Please go to System -> Settings to add fingerprints."
		HoneyLanguage.Japanese.code -> "The device does not record fingerprints. Please go to System -> Settings to add fingerprints."
		HoneyLanguage.Korean.code -> "The device does not record fingerprints. Please go to System -> Settings to add fingerprints."
		HoneyLanguage.Russian.code -> "The device does not record fingerprints. Please go to System -> Settings to add fingerprints."
		HoneyLanguage.TraditionalChinese.code -> "The device does not record fingerprints. Please go to System -> Settings to add fingerprints."
		else -> ""
	}
	val hardwareDoesNotSupportFingerprints = when(currentLanguage) {
		HoneyLanguage.English.code -> "The device has not detected the fingerprint hardware"
		HoneyLanguage.Chinese.code -> "The device has not detected the fingerprint hardware"
		HoneyLanguage.Japanese.code -> "The device has not detected the fingerprint hardware"
		HoneyLanguage.Korean.code -> "The device has not detected the fingerprint hardware"
		HoneyLanguage.Russian.code -> "The device has not detected the fingerprint hardware"
		HoneyLanguage.TraditionalChinese.code -> "The device has not detected the fingerprint hardware"
		else -> ""
	}
}

object ContactText {
	@JvmField
	val emptyNameAlert = when(currentLanguage) {
		HoneyLanguage.English.code -> "Please enter a contact name"
		HoneyLanguage.Chinese.code -> "请填写联系人名称"
		HoneyLanguage.Japanese.code -> "連絡先の名称を記入して下さい"
		HoneyLanguage.Korean.code -> "연락처 명칭을 입력하십시오"
		HoneyLanguage.Russian.code -> "Пожалуйста, заполните имя контакта"
		HoneyLanguage.TraditionalChinese.code -> "請填寫聯繫人名稱"
		else -> ""
	}
	@JvmField
	val emptyAddressAlert = when(currentLanguage) {
		HoneyLanguage.English.code -> "Please enter a wallet address"
		HoneyLanguage.Chinese.code -> "请填写联系人钱包地址"
		HoneyLanguage.Japanese.code -> "連絡先のウォレットアドレスを記入して下さい"
		HoneyLanguage.Korean.code -> "연락처 지갑주소를 입력하십시오"
		HoneyLanguage.Russian.code -> "Пожалуйста, заполните адрес кошелька контакта"
		HoneyLanguage.TraditionalChinese.code -> "請填寫聯繫人錢包地址"
		else -> ""
	}
	@JvmField
	val wrongAddressFormat: (symbol: String) -> String = {
		when(currentLanguage) {
			HoneyLanguage.English.code -> "Incorrect $it wallet address format"
			HoneyLanguage.Chinese.code -> "$it 钱包地址格式错误"
			HoneyLanguage.Japanese.code -> "$it ウォレットアドレス形式が間違っています"
			HoneyLanguage.Korean.code -> "$it 지갑주소 포맷 오류"
			HoneyLanguage.Russian.code -> "$it Неправильный формат адреса кошелька"
			HoneyLanguage.TraditionalChinese.code -> "$it 錢包地址格式有誤"
			else -> ""
		}
	}
	@JvmField
	val contactName = when(currentLanguage) {
		HoneyLanguage.English.code -> "Contact Name"
		HoneyLanguage.Chinese.code -> "联系人名称"
		HoneyLanguage.Japanese.code -> "連絡先名称"
		HoneyLanguage.Korean.code -> "연락처 명칭"
		HoneyLanguage.Russian.code -> "Имя контакта"
		HoneyLanguage.TraditionalChinese.code -> "聯繫人名稱"
		else -> ""
	}
	@JvmField
	val ethERCAndETHint = when(currentLanguage) {
		HoneyLanguage.English.code -> "Enter Ethereum, ERC20 or Ethereum Classic address that you want to store"
		HoneyLanguage.Chinese.code -> "输入您要存储的以太坊，ERC20或以太坊经典地址"
		HoneyLanguage.Japanese.code -> "保存したいEthereum、ERC20またはEthereumクラシックアドレスを入力してください"
		HoneyLanguage.Korean.code -> "저장할 Ethereum, ERC20 또는 Ethereum 기본 주소를 입력하십시오."
		HoneyLanguage.Russian.code -> "Введите классический адрес Ethereum, ERC20 или Ethereum, который вы хотите сохранить"
		HoneyLanguage.TraditionalChinese.code -> "輸入您要存儲的以太坊，ERC20或以太坊經典地址"
		else -> ""
	}

	@JvmField
	val eosHint = when(currentLanguage) {
		HoneyLanguage.English.code -> "Enter EOS Mainnet account name that you want to store"
		HoneyLanguage.Chinese.code -> "输入您要存储的EOS主网账户名"
		HoneyLanguage.Japanese.code -> "保存したいEOSアカウント名を入力してください"
		HoneyLanguage.Korean.code -> "저장하려는 EOS 기본 네트워크 계정 이름을 입력하십시오"
		HoneyLanguage.Russian.code -> "Введите имя главной сетевой учетной записи EOS, которое вы хотите сохранить"
		HoneyLanguage.TraditionalChinese.code -> "輸入您要存儲的EOS主網賬戶名"
		else -> ""
	}

	@JvmField
	val eosJungleHint = when(currentLanguage) {
		HoneyLanguage.English.code -> "Enter Bitcoin Mainnet address that you want to store"
		HoneyLanguage.Chinese.code -> "輸入您要存儲的EOS主網賬戶名"
		HoneyLanguage.Japanese.code -> "保存したいEOS Jungleテストネットワークアカウント名を入力してください"
		HoneyLanguage.Korean.code -> "저장하려는 EOS Jungle 테스트 네트워크 계정 이름 입력"
		HoneyLanguage.Russian.code -> "Введите имя учетной записи тестовой сети EOS Jungle, которую вы хотите сохранить"
		HoneyLanguage.TraditionalChinese.code -> "輸入您要存儲的EOS Jungle測試網網賬戶名"
		else -> ""
	}

	@JvmField
	val btcMainnetAddress = when(currentLanguage) {
		HoneyLanguage.English.code -> "Enter Bitcoin Mainnet address that you want to store"
		HoneyLanguage.Chinese.code -> "输入您要存储的比特币主网地址"
		HoneyLanguage.Japanese.code -> "保存するビットコムホームネットワークアドレスを入力してください"
		HoneyLanguage.Korean.code -> "저장하려는 비트 코인 홈 네트워크 주소를 입력하십시오."
		HoneyLanguage.Russian.code -> "Введите адрес домашней сети биткойна, который вы хотите сохранить"
		HoneyLanguage.TraditionalChinese.code -> "輸入您要存儲的比特幣主網地址"
		else -> ""
	}

	@JvmField
	val bchAddress = when(currentLanguage) {
		HoneyLanguage.English.code -> "Enter Bitcoin Cash Mainnet address that you want to store"
		HoneyLanguage.Chinese.code -> "Enter Bitcoin Cash Mainnet address that you want to store"
		HoneyLanguage.Japanese.code -> "Enter Bitcoin Cash Mainnet address that you want to store"
		HoneyLanguage.Korean.code -> "Enter Bitcoin Cash Mainnet address that you want to store"
		HoneyLanguage.Russian.code -> "Enter Bitcoin Cash Mainnet address that you want to store"
		HoneyLanguage.TraditionalChinese.code -> "Enter Bitcoin Cash Mainnet address that you want to store"
		else -> ""
	}

	@JvmField
	val ltcAddress = when(currentLanguage) {
		HoneyLanguage.English.code -> "Enter Litecoin Mainnet address that you want to store"
		HoneyLanguage.Chinese.code -> "输入您要存储的莱特币主网地址"
		HoneyLanguage.Japanese.code -> "Enter Litecoin Mainnet address that you want to store"
		HoneyLanguage.Korean.code -> "Enter Litecoin Mainnet address that you want to store"
		HoneyLanguage.Russian.code -> "Enter Litecoin Mainnet address that you want to store"
		HoneyLanguage.TraditionalChinese.code -> "Enter Litecoin Mainnet address that you want to store"
		else -> ""
	}

	@JvmField
	val btcTestnetAddress = when(currentLanguage) {
		HoneyLanguage.English.code -> "Enter Bitcoin Testnet address that you want to store"
		HoneyLanguage.Chinese.code -> "输入要存储的比特币Testnet地址"
		HoneyLanguage.Japanese.code -> "保存するBitcoin Testnetアドレスを入力してください"
		HoneyLanguage.Korean.code -> "저장할 Bitcoin Testnet 주소를 입력하십시오."
		HoneyLanguage.Russian.code -> "Введите адрес тестовой сети Bitcoin для хранения"
		HoneyLanguage.TraditionalChinese.code -> "輸入要存儲的比特幣Testnet地址"
		else -> ""
	}
}

object QRText {
	@JvmField
	val savedAttention = when(currentLanguage) {
		HoneyLanguage.English.code -> "QR code has been saved to album"
		HoneyLanguage.Chinese.code -> "二维码已保存至相册"
		HoneyLanguage.Japanese.code -> "QRコードをアルバムに保存しました"
		HoneyLanguage.Korean.code -> "QR 코드를 앨범에 저장 완료"
		HoneyLanguage.Russian.code -> "QR-код был сохранен в альбом"
		HoneyLanguage.TraditionalChinese.code -> "二維碼已保存至手機相冊"
		else -> ""
	}
	@JvmField
	val shareQRTitle = when(currentLanguage) {
		HoneyLanguage.English.code -> "SHARE QR IMAGE"
		HoneyLanguage.Chinese.code -> "分享二维码"
		HoneyLanguage.Japanese.code -> "QRコードをシェアする"
		HoneyLanguage.Korean.code -> "QR 코드 공유"
		HoneyLanguage.Russian.code -> "Поделиться QR-кодом"
		HoneyLanguage.TraditionalChinese.code -> "分享二維碼"
		else -> ""
	}
	@JvmField
	val screenText = when(currentLanguage) {
		HoneyLanguage.English.code -> "Scan GoldStone QR Code"
		HoneyLanguage.Chinese.code -> "扫描GoldStone的二维码"
		HoneyLanguage.Japanese.code -> "GoldStoneのQRコードをスキャンします"
		HoneyLanguage.Korean.code -> "GoldStone 의 QR 코드 스캔"
		HoneyLanguage.Russian.code -> "Сканирование QR-кода в GoldStone"
		HoneyLanguage.TraditionalChinese.code -> "掃描GoldStone的二維碼"
		else -> ""
	}
	@JvmField
	val invalidQRCodeAlert = when(currentLanguage) {
		HoneyLanguage.English.code -> "Invalid QR code"
		HoneyLanguage.Chinese.code -> "未识别到有效的二维码图片"
		HoneyLanguage.Japanese.code -> "有効的なQRコード画像を識別できません"
		HoneyLanguage.Korean.code -> "미식별 유효 QR 코드 이미지"
		HoneyLanguage.Russian.code -> "Неверный QR-код"
		HoneyLanguage.TraditionalChinese.code -> "未識別到有效的二維碼圖片"
		else -> ""
	}
	@JvmField
	val invalidContract = when(currentLanguage) {
		HoneyLanguage.English.code -> "Inconsistent currency. The QR code scanned is not that of the current token, please change the transfer token, or change the scanned QR code."
		HoneyLanguage.Chinese.code -> "货币不一致。您所扫描的不是当前Token的二维码，请您更换token进行转账，或者更换扫描的二维码。"
		HoneyLanguage.Japanese.code -> "貨幣が一致していません。現在のTokenのQRコードがスキャンされていません。Tokenを変更して振込するか、スキャンするQRコードを変更して下さい。"
		HoneyLanguage.Korean.code -> "화폐가 불일치합니다. 귀하께서 스캐스 한 것은 현재 Token 의 QR 코드가 아닙니다, 귀하께서 token 을 교체하여 이체하거나, 스캐너용 QR 코드를 교체하십시오. "
		HoneyLanguage.Russian.code -> "Несоответствующая валюта. Отсканированное содержание не является QR-кодом текущего токена, пожалуйста, поменяйте токен для перевода или измените отсканированный QR-код."
		HoneyLanguage.TraditionalChinese.code -> "貨幣不一致。您所掃描的不是當前Token的二維碼，請您更換token進行轉賬，或者更換掃描的二維碼。"
		else -> ""
	}

	@JvmField
	val selectQRCodeFromAlbum = when(currentLanguage) {
		HoneyLanguage.English.code -> "Select QR code from the album"
		HoneyLanguage.Chinese.code -> "Select QR code from the album"
		HoneyLanguage.Japanese.code -> "Select QR code from the album"
		HoneyLanguage.Korean.code -> "Select QR code from the album"
		HoneyLanguage.Russian.code -> "Select QR code from the album"
		HoneyLanguage.TraditionalChinese.code -> "Select QR code from the album"
		else -> ""
	}
}

object QAText {
	@JvmField
	val whatIsMnemonic = when(currentLanguage) {
		HoneyLanguage.English.code -> "What are mnemonics?"
		HoneyLanguage.Chinese.code -> "什么是助记词？"
		HoneyLanguage.Japanese.code -> "ニーモニックとはなんですか？"
		HoneyLanguage.Korean.code -> "니모닉이란 무엇인가요？"
		HoneyLanguage.Russian.code -> "Что такое мнемоническая запись?"
		HoneyLanguage.TraditionalChinese.code -> "什麼是助憶口令?"
		else -> ""
	}
	@JvmField
	val whatIsGas = when(currentLanguage) {
		HoneyLanguage.English.code -> "What is gas?"
		HoneyLanguage.Chinese.code -> "什么是GAS？"
		HoneyLanguage.Japanese.code -> "GASとはなんですか？"
		HoneyLanguage.Korean.code -> "니모닉이란 가스요금？"
		HoneyLanguage.Russian.code -> "Что такое GAS?"
		HoneyLanguage.TraditionalChinese.code -> "什麼是GAS?"
		else -> ""
	}
	@JvmField
	val whatIsKeystore = when(currentLanguage) {
		HoneyLanguage.English.code -> "What is a keystore?"
		HoneyLanguage.Chinese.code -> "什么是 keystore?"
		HoneyLanguage.Japanese.code -> "Keystoreとはなんですか？"
		HoneyLanguage.Korean.code -> "keystore 란？"
		HoneyLanguage.Russian.code -> "Что такое keystore?"
		HoneyLanguage.TraditionalChinese.code -> "什麼是 keystore?"
		else -> ""
	}
	@JvmField
	val whatIsWatchOnlyWallet = when(currentLanguage) {
		HoneyLanguage.English.code -> "What is a watch-only wallet?"
		HoneyLanguage.Chinese.code -> "什么是观察钱包？"
		HoneyLanguage.Japanese.code -> "観察ウォレットとはなんですか？"
		HoneyLanguage.Korean.code -> "관찰지갑이란？"
		HoneyLanguage.Russian.code -> "Что такое кошелек только для просмотра?"
		HoneyLanguage.TraditionalChinese.code -> "什麼是觀察錢包？"
		else -> ""
	}
	@JvmField
	val whatIsPrivateKey = when(currentLanguage) {
		HoneyLanguage.English.code -> "What is a private key?"
		HoneyLanguage.Chinese.code -> "什么是私钥？"
		HoneyLanguage.Japanese.code -> "プライベートキーとはなんですか？"
		HoneyLanguage.Korean.code -> "개인키란？"
		HoneyLanguage.Russian.code -> "Что такое закрытый ключ?"
		HoneyLanguage.TraditionalChinese.code -> "什麼是私鑰？"
		else -> ""
	}
}

object ImportMethodText {
	@JvmField
	val mnemonic = when(currentLanguage) {
		HoneyLanguage.English.code -> "Mnemonic"
		HoneyLanguage.Chinese.code -> "助记词"
		HoneyLanguage.Japanese.code -> "ニーモニック"
		HoneyLanguage.Korean.code -> "니모닉"
		HoneyLanguage.Russian.code -> "Мнемоническая запись"
		HoneyLanguage.TraditionalChinese.code -> "助憶口令"
		else -> ""
	}
	@JvmField
	val keystore = when(currentLanguage) {
		HoneyLanguage.English.code -> "Keystore"
		HoneyLanguage.Chinese.code -> "Keystore"
		HoneyLanguage.Japanese.code -> "Keystore"
		HoneyLanguage.Korean.code -> "Keystore"
		HoneyLanguage.Russian.code -> "Keystore"
		HoneyLanguage.TraditionalChinese.code -> "Keystore"
		else -> ""
	}
	@JvmField
	val privateKey = when(currentLanguage) {
		HoneyLanguage.English.code -> "Private Key"
		HoneyLanguage.Chinese.code -> "私钥"
		HoneyLanguage.Japanese.code -> "プライベートキー"
		HoneyLanguage.Korean.code -> "개인키(Private Key)"
		HoneyLanguage.Russian.code -> "Закрытый ключ"
		HoneyLanguage.TraditionalChinese.code -> "私鑰"
		else -> ""
	}
	@JvmField
	val watchOnly = when(currentLanguage) {
		HoneyLanguage.English.code -> "Watch-Only Wallet"
		HoneyLanguage.Chinese.code -> "观察钱包"
		HoneyLanguage.Japanese.code -> "観察ウォレット"
		HoneyLanguage.Korean.code -> "지갑 관찰"
		HoneyLanguage.Russian.code -> "Наблюдательный кошелек"
		HoneyLanguage.TraditionalChinese.code -> "觀察錢包"
		else -> ""
	}
}

object SplashText {
	@JvmField
	val slogan = when(currentLanguage) {
		HoneyLanguage.English.code -> "The safest, most useful wallet in the world"
		HoneyLanguage.Chinese.code -> "好用又安全的区块链钱包"
		HoneyLanguage.Japanese.code -> "使いやすくセキュリティのしっかりしたブロックチェーンウォレット"
		HoneyLanguage.Korean.code -> "사용하기 편리하고 안전한 블록체인 지갑"
		HoneyLanguage.Russian.code -> "Самый безопасный и полезный кошелек блокчейн в мире"
		HoneyLanguage.TraditionalChinese.code -> "好用又安全的區塊鏈錢包"
		else -> ""
	}
	@JvmField
	val goldStone = when(currentLanguage) {
		HoneyLanguage.English.code -> "GOLD STONE"
		HoneyLanguage.Chinese.code -> "GOLD STONE"
		HoneyLanguage.Japanese.code -> "GOLD STONE"
		HoneyLanguage.Korean.code -> "GOLD STONE"
		HoneyLanguage.Russian.code -> "GOLD STONE"
		HoneyLanguage.TraditionalChinese.code -> "GOLD STONE"
		else -> ""
	}
}