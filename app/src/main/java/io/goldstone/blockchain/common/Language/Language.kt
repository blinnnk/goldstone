@file:Suppress("DEPRECATION")

package io.goldstone.blockchain.common.language

import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.value.CountryCode

/**
 * @date 21/03/2018 7:34 PM
 * @author KaySaith
 */
var currentLanguage = when {
	SharedWallet.getCurrentLanguageCode() == 100 ->
		HoneyLanguage.getCodeBySymbol(CountryCode.currentLanguageSymbol)
	HoneyLanguage.currentLanguageIsSupported() -> SharedWallet.getCurrentLanguageCode()
	else -> HoneyLanguage.English.code
}

object WatchOnlyText {
	@JvmField
	val enterDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter the address or account name of the wallet to be observed"
		HoneyLanguage.Chinese.code -> "Enter the address or account name of the wallet to be observed"
		HoneyLanguage.Japanese.code -> "Enter the address or account name of the wallet to be observed"
		HoneyLanguage.Korean.code -> "Enter the address or account name of the wallet to be observed"
		HoneyLanguage.Russian.code -> "Enter the address or account name of the wallet to be observed"
		HoneyLanguage.TraditionalChinese.code -> "Enter the address or account name of the wallet to be observed"
		else -> ""
	}
	@JvmField
	val intro = when (currentLanguage) {
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
	val notification = when (currentLanguage) {
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
	val addToken = when (currentLanguage) {
		HoneyLanguage.English.code -> "Add Token"
		HoneyLanguage.Chinese.code -> "添加其他币种"
		HoneyLanguage.Japanese.code -> "他の貨幣を追加する"
		HoneyLanguage.Korean.code -> "토큰 추가"
		HoneyLanguage.Russian.code -> "Добавить другие валюты"
		HoneyLanguage.TraditionalChinese.code -> "添加其他幣種"
		else -> ""
	}
}

object PincodeText {
	@JvmField
	val pincode = when (currentLanguage) {
		HoneyLanguage.English.code -> "Pin Code"
		HoneyLanguage.Chinese.code -> "PIN 码"
		HoneyLanguage.Japanese.code -> "PIN コード"
		HoneyLanguage.Korean.code -> "PIN 코드"
		HoneyLanguage.Russian.code -> "PIN-код"
		HoneyLanguage.TraditionalChinese.code -> "PIN 碼"
		else -> ""
	}
	@JvmField
	val repeat = when (currentLanguage) {
		HoneyLanguage.English.code -> "Repeat PIN"
		HoneyLanguage.Chinese.code -> "再次輸入数字 PIN 码"
		HoneyLanguage.Japanese.code -> "もう一度PINを入力してください"
		HoneyLanguage.Korean.code -> "번호를 다시 입력하십시오"
		HoneyLanguage.Russian.code -> "Введите PIN-код снова"
		HoneyLanguage.TraditionalChinese.code -> "再次輸入數字 PIN 碼"
		else -> ""
	}
	@JvmField
	val description = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please set a 4-digit PIN"
		HoneyLanguage.Chinese.code -> "设置四位 PIN 码"
		HoneyLanguage.Japanese.code -> "4桁の PIN を設定する"
		HoneyLanguage.Korean.code -> "4 자리 PIN 설정"
		HoneyLanguage.Russian.code -> "Установите четырехзначный PIN-код"
		HoneyLanguage.TraditionalChinese.code -> "設置四位 PIN 碼"
		else -> ""
	}
	@JvmField
	val countAlert = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please enter a four-digit PIN"
		HoneyLanguage.Chinese.code -> "请输入四位 PIN 码"
		HoneyLanguage.Japanese.code -> "4桁の PIN を入力してください"
		HoneyLanguage.Korean.code -> "4 자리 PIN을 입력하십시오."
		HoneyLanguage.Russian.code -> "Введите четырехзначный PIN-код"
		HoneyLanguage.TraditionalChinese.code -> "請輸入四位 PIN 碼"
		else -> ""
	}
	@JvmField
	val verifyAlert = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please enter the PIN again to confirm"
		HoneyLanguage.Chinese.code -> "请再次输入一遍 PIN 码进行确认"
		HoneyLanguage.Japanese.code -> "確認のため PIN をもう一度入力してください"
		HoneyLanguage.Korean.code -> "PIN을 다시 입력하여 확인하십시오."
		HoneyLanguage.Russian.code -> "Введите PIN-код еще раз, чтобы подтвердить"
		HoneyLanguage.TraditionalChinese.code -> "請再次輸入一遍 PIN 碼進行確認"
		else -> ""
	}
	@JvmField
	val turnOnAttention = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please set your PIN first"
		HoneyLanguage.Chinese.code -> "请先设置 PIN 码"
		HoneyLanguage.Japanese.code -> "PIN を最初に設定してください"
		HoneyLanguage.Korean.code -> "PIN을 먼저 설정하십시오."
		HoneyLanguage.Russian.code -> "Сначала введите PIN-код"
		HoneyLanguage.TraditionalChinese.code -> "請先設置 PIN 碼"
		else -> ""
	}
	@JvmField
	val show = when (currentLanguage) {
		HoneyLanguage.English.code -> "Show PIN"
		HoneyLanguage.Chinese.code -> "显示 PIN 码"
		HoneyLanguage.Japanese.code -> "PIN を表示する"
		HoneyLanguage.Korean.code -> "PIN 표시"
		HoneyLanguage.Russian.code -> "Показать PIN-код"
		HoneyLanguage.TraditionalChinese.code -> "顯示 PIN 碼"
		else -> ""
	}
	@JvmField
	val enterPinCode = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter PIN"
		HoneyLanguage.Chinese.code -> "输入 PIN 码"
		HoneyLanguage.Japanese.code -> "PIN を入力"
		HoneyLanguage.Korean.code -> "PIN 입력"
		HoneyLanguage.Russian.code -> "Введите PIN-код"
		HoneyLanguage.TraditionalChinese.code -> "輸入 PIN 碼"
		else -> ""
	}
	@JvmField
	val setPincodeDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "Set the lock screen password to protect privacy. Once the lock screen password is enabled, you need to enter the lock screen password to view the wallet every time you open GoldStone."
		HoneyLanguage.Chinese.code -> "设置锁屏密码保护隐私，一旦开启锁屏密码，每次打开 GoldStone 时需要输入锁屏密码才能查看钱包"
		HoneyLanguage.Japanese.code -> "ロック画面のパスワードを設定すると、プライバシー保護のために画面がロックされます。ロック画面のパスワードが有効になったら、ゴールドストーンを開くたびにウォレットを表示するためにロック画面のパスワードを入力する必要があります。"
		HoneyLanguage.Korean.code -> "잠금 화면 암호를 설정하여 개인 정보를 보호하십시오. 화면 잠금 암호가 활성화되면 GoldStone을 열 때마다 지갑 화면을 보려면 잠금 화면 암호를 입력해야합니다."
		HoneyLanguage.Russian.code -> "Установите пароль для блокировки экрана для защиты конфиденциальности. После того как пароль заблокированного экрана включен, вам нужно ввести пароль блокировки для просмотра кошелька каждый раз, когда вы открываете GoldStone."
		HoneyLanguage.TraditionalChinese.code -> "設置鎖屏密碼保護隱私，一旦開啟鎖屏密碼，每次打開 GoldStone 時需要輸入鎖屏密碼才能查看錢包"
		else -> ""
	}
	@JvmField
	val enterPinCodeDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please enter the 4-digit PIN code"
		HoneyLanguage.Chinese.code -> "请输入设置的4位数字 PIN 码"
		HoneyLanguage.Japanese.code -> "設定した4桁の PIN コードを入力してください"
		HoneyLanguage.Korean.code -> "설정된 4 자리 PIN 코드를 입력하십시오."
		HoneyLanguage.Russian.code -> "Введите четырехзначный PIN-код"
		HoneyLanguage.TraditionalChinese.code -> "請輸入設置的4位數字 PIN 碼"
		else -> ""
	}
}

object ContactText {
	@JvmField
	val emptyNameAlert = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please enter a contact name"
		HoneyLanguage.Chinese.code -> "请填写联系人名称"
		HoneyLanguage.Japanese.code -> "連絡先の名称を記入して下さい"
		HoneyLanguage.Korean.code -> "연락처 명칭을 입력하십시오"
		HoneyLanguage.Russian.code -> "Пожалуйста, заполните имя контакта"
		HoneyLanguage.TraditionalChinese.code -> "請填寫聯繫人名稱"
		else -> ""
	}
	@JvmField
	val emptyAddressAlert = when (currentLanguage) {
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
		when (currentLanguage) {
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
	val contactName = when (currentLanguage) {
		HoneyLanguage.English.code -> "Contact Name"
		HoneyLanguage.Chinese.code -> "联系人名称"
		HoneyLanguage.Japanese.code -> "連絡先名称"
		HoneyLanguage.Korean.code -> "연락처 명칭"
		HoneyLanguage.Russian.code -> "Имя контакта"
		HoneyLanguage.TraditionalChinese.code -> "聯繫人名稱"
		else -> ""
	}

	//显示联系人有多少个地址的文案
	@JvmField
	val address: (count: Int) -> String = {
		when (currentLanguage) {
			HoneyLanguage.English.code -> if (it > 1) "Addresses" else "Address"
			HoneyLanguage.Chinese.code -> "个地址"
			HoneyLanguage.Japanese.code -> "アドレス"
			HoneyLanguage.Korean.code -> "주소"
			HoneyLanguage.Russian.code -> if (it > 1) "Адреса" else "Адрес"
			HoneyLanguage.TraditionalChinese.code -> "個地址"
			else -> ""
		}
	}

	@JvmField
	val ethERCAndETHint = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter Ethereum, ERC20 or Ethereum Classic address that you want to store"
		HoneyLanguage.Chinese.code -> "输入您要存储的以太坊,ERC20 代币或以太坊经典地址"
		HoneyLanguage.Japanese.code -> "保存したいETH、ERC20 トークン、またはETCアドレスを入力してください"
		HoneyLanguage.Korean.code -> "저장할 Ethereum, ERC20 또는 Ethereum 기본 주소를 입력하십시오."
		HoneyLanguage.Russian.code -> "Введите классический адрес Ethereum, ERC20 или адрес Ethereum, который вы хотите сохранить"
		HoneyLanguage.TraditionalChinese.code -> "輸入您要存儲的以太坊, ERC20 代幣或以太坊經典地址"
		else -> ""
	}

	@JvmField
	val eosHint = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter EOS Mainnet account name that you want to store"
		HoneyLanguage.Chinese.code -> "输入您要存储的 EOS 主网账户名"
		HoneyLanguage.Japanese.code -> "保存したい EOS メインネットワークアカウント名を入力してください"
		HoneyLanguage.Korean.code -> "저장할 EOS 기본 네트워크 계정 이름을 입력하십시오"
		HoneyLanguage.Russian.code -> "Введите имя главной сетевой учетной записи EOS, которое вы хотите сохранить."
		HoneyLanguage.TraditionalChinese.code -> "輸入您要存儲的 EOS 主網賬戶名"
		else -> ""
	}

	@JvmField
	val eosJungleHint = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter EOS Jungle address that you want to store"
		HoneyLanguage.Chinese.code -> "输入您要存储的 EOS Jungle 测试网账户名"
		HoneyLanguage.Japanese.code -> "保存したい EOS Jungleテストネットワークアカウント名を入力してください"
		HoneyLanguage.Korean.code -> "저장할 EOS Jungle 테스트 계정 이름을 입력하십시오"
		HoneyLanguage.Russian.code -> "Введите имя тестовой учетной записи EOS Jungle, которое вы хотите сохранить."
		HoneyLanguage.TraditionalChinese.code -> "輸入您要存儲的 EOS Jungle 測試網網賬戶名"
		else -> ""
	}

	@JvmField
	val eosKylinHint = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter EOS Kylin address that you want to store"
		HoneyLanguage.Chinese.code -> "输入您要存储的 EOS Kylin 测试网账户名"
		HoneyLanguage.Japanese.code -> "保存したい EOS Kylinテストネットワークアカウント名を入力してください"
		HoneyLanguage.Korean.code -> "저장할 EOS Kylin 테스트 계정 이름을 입력하십시오"
		HoneyLanguage.Russian.code -> "Введите имя тестовой учетной записи EOS Kylin, которое вы хотите сохранить."
		HoneyLanguage.TraditionalChinese.code -> "輸入您要存儲的 EOS Kylin 測試網網賬戶名"
		else -> ""
	}

	@JvmField
	val btcMainnetAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter Bitcoin Mainnet address that you want to store"
		HoneyLanguage.Chinese.code -> "输入您要存储的比特币主网地址"
		HoneyLanguage.Japanese.code -> "保存するビットコインメインネットワークアドレスを入力してください"
		HoneyLanguage.Korean.code -> "저장할 비트 코인을 입력하십시오"
		HoneyLanguage.Russian.code -> "Введите биткойн, который вы хотите сохранить."
		HoneyLanguage.TraditionalChinese.code -> "輸入您要存儲的比特幣主網地址"
		else -> ""
	}

	@JvmField
	val btcTestnetAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter Bitcoin, Litecoin and Bitcoin Cash Testnet address that you want to store"
		HoneyLanguage.Chinese.code -> "输入要存储的比特币(BTC)、莱特币(LTC)、比特币现金(BCH)测试网地址"
		HoneyLanguage.Japanese.code -> "ストレージビットコイン（BTC）、ライトコイン（LTC）、ビットコインの現金（BCH）テストアドレスを入力してください"
		HoneyLanguage.Korean.code -> "저장 비트 코인 (BTC), 라이트 코인 (LTC), 비트 코인 현금 (BCH) 테스트 주소를 입력"
		HoneyLanguage.Russian.code -> "Введите сохраненный сетевой адрес Биткойн (BTC), Litecoin (LTC), Биткойн наличными (BCH)"
		HoneyLanguage.TraditionalChinese.code -> "輸入要存儲的比特幣(BTC)、萊特幣(LTC)、比特幣現金(BCH)測試網地址"
		else -> ""
	}
	@JvmField
	val bchAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter Bitcoin Cash Mainnet address that you want to store"
		HoneyLanguage.Chinese.code -> "输入您要存储的比特币现金主网地址"
		HoneyLanguage.Japanese.code -> "保存するビットコイン現金メインネットワークアドレスを入力してください"
		HoneyLanguage.Korean.code -> "저장하려는 비트 코인 현금 홈 네트워크 주소를 입력하십시오."
		HoneyLanguage.Russian.code -> "Введите домашний адрес домашней сети биткойны, который вы хотите сохранить"
		HoneyLanguage.TraditionalChinese.code -> "輸入您要存儲的比特幣現金主網地址"
		else -> ""
	}

	@JvmField
	val ltcAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter Litecoin Mainnet address that you want to store"
		HoneyLanguage.Chinese.code -> "输入您要存储的莱特币主网地址"
		HoneyLanguage.Japanese.code -> "保存したいLitecoinメインネットワークのアドレスを入力してください"
		HoneyLanguage.Korean.code -> "저장하려는 주요 네트워크 주소 라이트 코인 입력"
		HoneyLanguage.Russian.code -> "Введите адрес основной сети Litecoin, которую вы хотите сохранить"
		HoneyLanguage.TraditionalChinese.code -> "輸入您要存儲的萊特幣主網地址"
		else -> ""
	}
}

object QRText {
	@JvmField
	val savedAttention = when (currentLanguage) {
		HoneyLanguage.English.code -> "QR code has been saved to album"
		HoneyLanguage.Chinese.code -> "二维码已保存至相册"
		HoneyLanguage.Japanese.code -> "QR コードをアルバムに保存しました"
		HoneyLanguage.Korean.code -> "QR 코드를 앨범에 저장 완료"
		HoneyLanguage.Russian.code -> "QR-код был сохранен в альбом"
		HoneyLanguage.TraditionalChinese.code -> "二維碼已保存至手機相冊"
		else -> ""
	}
	@JvmField
	val shareQRTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "SHARE QR IMAGE"
		HoneyLanguage.Chinese.code -> "分享二维码"
		HoneyLanguage.Japanese.code -> "QR コードをシェアする"
		HoneyLanguage.Korean.code -> "QR 코드 공유"
		HoneyLanguage.Russian.code -> "Поделиться QR-кодом"
		HoneyLanguage.TraditionalChinese.code -> "分享二維碼"
		else -> ""
	}
	@JvmField
	val screenText = when (currentLanguage) {
		HoneyLanguage.English.code -> "Scan GoldStone QR Code"
		HoneyLanguage.Chinese.code -> "扫描 GoldStone 二维码"
		HoneyLanguage.Japanese.code -> "GoldStoneのQRコードをスキャンします"
		HoneyLanguage.Korean.code -> "GoldStone 의 QR 코드 스캔"
		HoneyLanguage.Russian.code -> "Сканирование QR-кода в GoldStone"
		HoneyLanguage.TraditionalChinese.code -> "掃描 GoldStone 二維碼"
		else -> ""
	}
	@JvmField
	val invalidQRCodeAlert = when (currentLanguage) {
		HoneyLanguage.English.code -> "Invalid QR code"
		HoneyLanguage.Chinese.code -> "未识别到有效的二维码图片"
		HoneyLanguage.Japanese.code -> "有効的な QR コード画像を識別できません"
		HoneyLanguage.Korean.code -> "미식별 유효 QR 코드 이미지"
		HoneyLanguage.Russian.code -> "Неверный QR-код"
		HoneyLanguage.TraditionalChinese.code -> "未識別到有效的二維碼圖片"
		else -> ""
	}
	@JvmField
	val invalidContract = when (currentLanguage) {
		HoneyLanguage.English.code -> "Inconsistent currency. The QR code scanned is not that of the current token, please change the transfer token, or change the scanned QR code."
		HoneyLanguage.Chinese.code -> "货币不一致。您所扫描的不是当前 Token 的二维码，请您更换 Token 进行转账，或者更换扫描的二维码。"
		HoneyLanguage.Japanese.code -> "貨幣が一致していません。現在のTokenのQRコードがスキャンされていません。Tokenを変更して振込するか、スキャンするQRコードを変更して下さい。"
		HoneyLanguage.Korean.code -> "화폐가 불일치합니다. 귀하께서 스캐스 한 것은 현재 Token 의 QR 코드가 아닙니다, 귀하께서 token 을 교체하여 이체하거나, 스캐너용 QR 코드를 교체하십시오. "
		HoneyLanguage.Russian.code -> "Несоответствующая валюта. Отсканированное содержание не является QR-кодом текущего токена, пожалуйста, поменяйте токен для перевода или измените отсканированный QR-код."
		HoneyLanguage.TraditionalChinese.code -> "貨幣不一致。您所掃描的不是當前 Token 的二維碼，請您更換 Token 進行轉賬，或者更換掃描的二維碼。"
		else -> ""
	}

	@JvmField
	val selectQRCodeFromAlbum = when (currentLanguage) {
		HoneyLanguage.English.code -> "Select QR code from the album"
		HoneyLanguage.Chinese.code -> "从相册选择二维码"
		HoneyLanguage.Japanese.code -> "アルバムからQRコードを選択"
		HoneyLanguage.Korean.code -> "앨범에서 QR 코드를 선택하십시오."
		HoneyLanguage.Russian.code -> "Выберите QR-код из альбома"
		HoneyLanguage.TraditionalChinese.code -> "從相冊選擇二維碼"
		else -> ""
	}
}

object QAText {
	@JvmField
	val whatIsMnemonic = when (currentLanguage) {
		HoneyLanguage.English.code -> "What are mnemonics?"
		HoneyLanguage.Chinese.code -> "什么是助记词？"
		HoneyLanguage.Japanese.code -> "ニーモニックとはなんですか？"
		HoneyLanguage.Korean.code -> "니모닉이란 무엇인가요？"
		HoneyLanguage.Russian.code -> "Что такое мнемоническая запись?"
		HoneyLanguage.TraditionalChinese.code -> "什麼是助憶口令?"
		else -> ""
	}
	@JvmField
	val whatIsGas = when (currentLanguage) {
		HoneyLanguage.English.code -> "What is mining fee / gas?"
		HoneyLanguage.Chinese.code -> "什么是矿工费 / 燃气费(Gas)?"
		HoneyLanguage.Japanese.code -> "ガス(GAS) / 鉱夫料とは何ですか？"
		HoneyLanguage.Korean.code -> "가스 / 광업 요금 / 가스 란 무엇입니까?"
		HoneyLanguage.Russian.code -> "Что такое плата за газ / добыча?"
		HoneyLanguage.TraditionalChinese.code -> "什麼是礦工費 / 燃氣費(Gas)?"
		else -> ""
	}
	@JvmField
	val whatIsKeystore = when (currentLanguage) {
		HoneyLanguage.English.code -> "What is a keystore"
		HoneyLanguage.Chinese.code -> "什么是 keystore ?"
		HoneyLanguage.Japanese.code -> "キーストアとは何ですか"
		HoneyLanguage.Korean.code -> "키 저장소 란 무엇입니까?"
		HoneyLanguage.Russian.code -> "Что такое хранилище ключей"
		HoneyLanguage.TraditionalChinese.code -> "什麼是 keystore ?"
		else -> ""
	}
	@JvmField
	val whatIsWatchOnlyWallet = when (currentLanguage) {
		HoneyLanguage.English.code -> "What is a watch-only wallet?"
		HoneyLanguage.Chinese.code -> "什么是观察钱包？"
		HoneyLanguage.Japanese.code -> "観察ウォレットとはなんですか？"
		HoneyLanguage.Korean.code -> "관찰지갑이란？"
		HoneyLanguage.Russian.code -> "Что такое кошелек только для просмотра?"
		HoneyLanguage.TraditionalChinese.code -> "什麼是觀察錢包？"
		else -> ""
	}
	@JvmField
	val whatIsPrivateKey = when (currentLanguage) {
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
	val mnemonic = when (currentLanguage) {
		HoneyLanguage.English.code -> "Mnemonic"
		HoneyLanguage.Chinese.code -> "助记词"
		HoneyLanguage.Japanese.code -> "ニーモニック"
		HoneyLanguage.Korean.code -> "니모닉"
		HoneyLanguage.Russian.code -> "Мнемоническая запись"
		HoneyLanguage.TraditionalChinese.code -> "助憶口令"
		else -> ""
	}
	@JvmField
	val keystore = when (currentLanguage) {
		HoneyLanguage.English.code -> "Keystore"
		HoneyLanguage.Chinese.code -> "Keystore"
		HoneyLanguage.Japanese.code -> "Keystore"
		HoneyLanguage.Korean.code -> "Keystore"
		HoneyLanguage.Russian.code -> "Keystore"
		HoneyLanguage.TraditionalChinese.code -> "Keystore"
		else -> ""
	}
	@JvmField
	val privateKey = when (currentLanguage) {
		HoneyLanguage.English.code -> "Private Key"
		HoneyLanguage.Chinese.code -> "私钥"
		HoneyLanguage.Japanese.code -> "プライベートキー"
		HoneyLanguage.Korean.code -> "개인키 (Private Key)"
		HoneyLanguage.Russian.code -> "Закрытый ключ"
		HoneyLanguage.TraditionalChinese.code -> "私鑰"
		else -> ""
	}
	@JvmField
	val watchOnly = when (currentLanguage) {
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
	val slogan = when (currentLanguage) {
		HoneyLanguage.English.code -> "The safest, most useful multiple blockchain wallet"
		HoneyLanguage.Chinese.code -> "好用又安全的区块链钱包"
		HoneyLanguage.Japanese.code -> "使いやすくセキュリティのしっかりしたブロックチェーンウォレット"
		HoneyLanguage.Korean.code -> "사용하기 편리하고 안전한 블록체인 지갑"
		HoneyLanguage.Russian.code -> "Самый безопасный и полезный кошелек блокчейн в мире"
		HoneyLanguage.TraditionalChinese.code -> "好用又安全的區塊鏈錢包"
		else -> ""
	}
	@JvmField
	val goldStone = when (currentLanguage) {
		HoneyLanguage.English.code -> "GOLD STONE"
		HoneyLanguage.Chinese.code -> "GOLD STONE"
		HoneyLanguage.Japanese.code -> "GOLD STONE"
		HoneyLanguage.Korean.code -> "GOLD STONE"
		HoneyLanguage.Russian.code -> "GOLD STONE"
		HoneyLanguage.TraditionalChinese.code -> "GOLD STONE"
		else -> ""
	}
}