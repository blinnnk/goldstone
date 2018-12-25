package io.goldstone.blockchain.common.language

/**
 * @date 2018/8/8 2:15 AM
 * @author KaySaith
 */

object ProfileText {
	@JvmField
	val settings = when (currentLanguage) {
		HoneyLanguage.English.code -> "Settings"
		HoneyLanguage.Chinese.code -> "设置"
		HoneyLanguage.Japanese.code -> "設定"
		HoneyLanguage.Korean.code -> "설정"
		HoneyLanguage.Russian.code -> "Настройки"
		HoneyLanguage.TraditionalChinese.code -> "設置"
		else -> ""
	}
	@JvmField
	val dappCenter = when (currentLanguage) {
		HoneyLanguage.English.code -> "DAPP Center"
		HoneyLanguage.Chinese.code -> "DAPP 中心"
		HoneyLanguage.Japanese.code -> "DAPP センター"
		HoneyLanguage.Korean.code -> "DAPP 센터"
		HoneyLanguage.Russian.code -> "DAPP Центр"
		HoneyLanguage.TraditionalChinese.code -> "DAPP 中心"
		else -> ""
	}
	@JvmField
	val contacts = when (currentLanguage) {
		HoneyLanguage.English.code -> "Contacts"
		HoneyLanguage.Chinese.code -> "通讯录"
		HoneyLanguage.Japanese.code -> "アドレス帳"
		HoneyLanguage.Korean.code -> "전화번호부"
		HoneyLanguage.Russian.code -> "Контакты"
		HoneyLanguage.TraditionalChinese.code -> "通訊簿"
		else -> ""
	}
	@JvmField
	val contactsInput = when (currentLanguage) {
		HoneyLanguage.English.code -> "Add Contact"
		HoneyLanguage.Chinese.code -> "添加联系人"
		HoneyLanguage.Japanese.code -> "連絡先を追加する"
		HoneyLanguage.Korean.code -> "연락처 추가"
		HoneyLanguage.Russian.code -> "Добавить контакт"
		HoneyLanguage.TraditionalChinese.code -> "添加聯繫人"
		else -> ""
	}
	@JvmField
	val currency = when (currentLanguage) {
		HoneyLanguage.English.code -> "Currency Settings"
		HoneyLanguage.Chinese.code -> "货币"
		HoneyLanguage.Japanese.code -> "貨幣"
		HoneyLanguage.Korean.code -> "화폐"
		HoneyLanguage.Russian.code -> "Валюта"
		HoneyLanguage.TraditionalChinese.code -> "貨幣"
		else -> ""
	}
	@JvmField
	val hint = when (currentLanguage) {
		HoneyLanguage.English.code -> "Password Hint"
		HoneyLanguage.Chinese.code -> "密码提示"
		HoneyLanguage.Japanese.code -> "パスワードヒント"
		HoneyLanguage.Korean.code -> "비밀번호 제시"
		HoneyLanguage.Russian.code -> "Подсказки пароля"
		HoneyLanguage.TraditionalChinese.code -> "密碼提示"
		else -> ""
	}
	@JvmField
	val language = when (currentLanguage) {
		HoneyLanguage.English.code -> "Language"
		HoneyLanguage.Chinese.code -> "语言"
		HoneyLanguage.Japanese.code -> "言語"
		HoneyLanguage.Korean.code -> "언어"
		HoneyLanguage.Russian.code -> "Язык"
		HoneyLanguage.TraditionalChinese.code -> "語言"
		else -> ""
	}
	@JvmField
	val aboutUs = when (currentLanguage) {
		HoneyLanguage.English.code -> "About Us"
		HoneyLanguage.Chinese.code -> "关于我们"
		HoneyLanguage.Japanese.code -> "弊社について"
		HoneyLanguage.Korean.code -> "당사소개"
		HoneyLanguage.Russian.code -> "О нас"
		HoneyLanguage.TraditionalChinese.code -> "關於我們"
		else -> ""
	}
	@JvmField
	val support = when (currentLanguage) {
		HoneyLanguage.English.code -> "Contact Us"
		HoneyLanguage.Chinese.code -> "联系我们"
		HoneyLanguage.Japanese.code -> "弊社とコンタクトする"
		HoneyLanguage.Korean.code -> "문의하기"
		HoneyLanguage.Russian.code -> "Связаться с нами"
		HoneyLanguage.TraditionalChinese.code -> "聯繫我們"
		else -> ""
	}
	@JvmField
	val helpCenter = when (currentLanguage) {
		HoneyLanguage.English.code -> "Help Center"
		HoneyLanguage.Chinese.code -> "帮助中心"
		HoneyLanguage.Japanese.code -> "ヘルプセンター"
		HoneyLanguage.Korean.code -> "도움말 센터"
		HoneyLanguage.Russian.code -> "Центр поддержки"
		HoneyLanguage.TraditionalChinese.code -> "幫助中心"
		else -> ""
	}
	@JvmField
	val privacy = when (currentLanguage) {
		HoneyLanguage.English.code -> "Privacy Policy"
		HoneyLanguage.Chinese.code -> "隐私条款"
		HoneyLanguage.Japanese.code -> "プライバシーポリシー"
		HoneyLanguage.Korean.code -> "프라이버시 조항"
		HoneyLanguage.Russian.code -> "Личные положения"
		HoneyLanguage.TraditionalChinese.code -> "隱私政策"
		else -> ""
	}
	@JvmField
	val terms = when (currentLanguage) {
		HoneyLanguage.English.code -> "User Agreement"
		HoneyLanguage.Chinese.code -> "用户协议"
		HoneyLanguage.Japanese.code -> "ユーザー契約書"
		HoneyLanguage.Korean.code -> "유저협의서"
		HoneyLanguage.Russian.code -> "Консультация"
		HoneyLanguage.TraditionalChinese.code -> "用戶協議"
		else -> ""
	}
	@JvmField
	val version = when (currentLanguage) {
		HoneyLanguage.English.code -> "Version"
		HoneyLanguage.Chinese.code -> "软件版本"
		HoneyLanguage.Japanese.code -> "バージョン"
		HoneyLanguage.Korean.code -> "소프트웨어 버전"
		HoneyLanguage.Russian.code -> "Информация о версии"
		HoneyLanguage.TraditionalChinese.code -> "Version"
		else -> ""
	}
	@JvmField
	val shareApp = when (currentLanguage) {
		HoneyLanguage.English.code -> "Share GoldStone"
		HoneyLanguage.Chinese.code -> "分享 GoldStone"
		HoneyLanguage.Japanese.code -> "GoldStoneをシェアする"
		HoneyLanguage.Korean.code -> "공유 GoldStone"
		HoneyLanguage.Russian.code -> "Поделиться GoldStone"
		HoneyLanguage.TraditionalChinese.code -> "分享 GoldStone"
		else -> ""
	}
	@JvmField
	val pinCode = when (currentLanguage) {
		HoneyLanguage.English.code -> "PIN"
		HoneyLanguage.Chinese.code -> "PIN码"
		HoneyLanguage.Japanese.code -> "PINコード"
		HoneyLanguage.Korean.code -> "PIN"
		HoneyLanguage.Russian.code -> "PIN-код"
		HoneyLanguage.TraditionalChinese.code -> "PIN碼"
		else -> ""
	}
	@JvmField
	val eosAccountRegister = when (currentLanguage) {
		HoneyLanguage.English.code -> "Create New EOS Account"
		HoneyLanguage.Chinese.code -> "创建新 EOS 账号"
		HoneyLanguage.Japanese.code -> "新しいEOSアカウント"
		HoneyLanguage.Korean.code -> "새 EOS 계정 활성화"
		HoneyLanguage.Russian.code -> "Создать учетную запись EOS"
		HoneyLanguage.TraditionalChinese.code -> "創建新 EOS 賬號"
		else -> ""
	}
	@JvmField
	val chain = when (currentLanguage) {
		HoneyLanguage.English.code -> "Chain Node"
		HoneyLanguage.Chinese.code -> "选择节点"
		HoneyLanguage.Japanese.code -> "ノード選択"
		HoneyLanguage.Korean.code -> "노드선택"
		HoneyLanguage.Russian.code -> "Выбор узла"
		HoneyLanguage.TraditionalChinese.code -> "選擇節點"
		else -> ""
	}


	@JvmField
	val walletManager = when (currentLanguage) {
		HoneyLanguage.English.code -> "Wallet Manage"
		HoneyLanguage.Chinese.code -> "钱包管理"
		HoneyLanguage.Japanese.code -> "ウォレット管理"
		HoneyLanguage.Korean.code -> "월렛 관리"
		HoneyLanguage.Russian.code -> "Все кошельки"
		HoneyLanguage.TraditionalChinese.code -> "錢包管理"
		else -> ""
	}
	@JvmField
	val shareContent = when (currentLanguage) {
		HoneyLanguage.English.code -> "GoldStone\ncrypto digital wallet the safest one for you\nhttps://www.goldstone.io"
		HoneyLanguage.Chinese.code -> "GoldStone\n安全，易用，快捷\nhttps://www.goldstone.io"
		HoneyLanguage.Japanese.code -> "GoldStone/nは安全で使いやすくて便利\nhttps://www.goldstone.io"
		HoneyLanguage.Korean.code -> "GoldStone\n안전하고 사용하기 쉽고 빠름\nhttps://www.goldstone.io"
		HoneyLanguage.Russian.code -> "GoldStone\n цифровой кошелек самый безопасный для вас\nhttps://www.goldstone.io"
		HoneyLanguage.TraditionalChinese.code -> "GoldStone\n安全，易用，快捷\nhttps://www.goldstone.io"
		else -> ""
	}
	@JvmField
	val deleteContactAlertTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "DELETE CONTACT"
		HoneyLanguage.Chinese.code -> "删除联系人"
		HoneyLanguage.Japanese.code -> "連絡先の削除"
		HoneyLanguage.Korean.code -> "연락처 삭제"
		HoneyLanguage.Russian.code -> "УДАЛИТЬ КОНТАКТ"
		HoneyLanguage.TraditionalChinese.code -> "刪除聯繫人"
		else -> ""
	}
	@JvmField
	val deleteContactAlertDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "Are you sure you want to delete this contact and the corresponding address?"
		HoneyLanguage.Chinese.code -> "你确定要删除这个联系人和其对应的地址么?"
		HoneyLanguage.Japanese.code -> "この連絡先と関連するアドレスを削除しますか？"
		HoneyLanguage.Korean.code -> "이 연락처와 해당 주소를 삭제 하시겠습니까?"
		HoneyLanguage.Russian.code -> "Вы уверены, что хотите удалить данный контакт и его соответствующий адрес?"
		HoneyLanguage.TraditionalChinese.code -> "你確定要刪除這個聯繫人和對應的地址嗎？"
		else -> ""
	}
	@JvmField
	val walletAdvanced = when (currentLanguage) {
		HoneyLanguage.English.code -> "Wallet Advanced Settings"
		HoneyLanguage.Chinese.code -> "钱包设置"
		HoneyLanguage.Japanese.code -> "ウォレットの設定"
		HoneyLanguage.Korean.code -> "월렛 설정"
		HoneyLanguage.Russian.code -> "Настройки кошелька"
		HoneyLanguage.TraditionalChinese.code -> "錢包設置"

		else -> ""
	}
	@JvmField
	val generalPreference = when (currentLanguage) {
		HoneyLanguage.English.code -> "General Preference"
		HoneyLanguage.Chinese.code -> "通用设置"
		HoneyLanguage.Japanese.code -> "一般的な設定"
		HoneyLanguage.Korean.code -> "일반 특혜"
		HoneyLanguage.Russian.code -> "Общие предпочтения"
		HoneyLanguage.TraditionalChinese.code -> "通用設置"

		else -> ""
	}
	@JvmField
	val aboutGoldStone = when (currentLanguage) {
		HoneyLanguage.English.code -> "About GoldStone"
		HoneyLanguage.Chinese.code -> "关于 GoldStone"
		HoneyLanguage.Japanese.code -> "GoldStone について"
		HoneyLanguage.Korean.code -> "GoldStone 정보"
		HoneyLanguage.Russian.code -> "О компании GoldStone"
		HoneyLanguage.TraditionalChinese.code -> "關於 GoldStone"
		else -> ""
	}

	@JvmField
	val editContactAlertTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "EDIT CONTACT"
		HoneyLanguage.Chinese.code -> "编辑联系人"
		HoneyLanguage.Japanese.code -> "連絡先を編集"
		HoneyLanguage.Korean.code -> "연락처 수정"
		HoneyLanguage.Russian.code -> "Изменить контакт"
		HoneyLanguage.TraditionalChinese.code -> "編輯聯繫人"
		else -> ""
	}
	@JvmField
	val editContactAlertDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "Do you want to edit this contact?"
		HoneyLanguage.Chinese.code -> "要编辑此联系人吗?"
		HoneyLanguage.Japanese.code -> "この連絡先を編集しますか?"
		HoneyLanguage.Korean.code -> "이 연락처를 편집 하시겠습니까?"
		HoneyLanguage.Russian.code -> "Вы хотите отредактировать этот контакт?"
		HoneyLanguage.TraditionalChinese.code -> "要編輯此聯繫人嗎?"
		else -> ""
	}

	@JvmField
	val fingerprintSettings = when (currentLanguage) {
		HoneyLanguage.English.code -> "Fingerprint payment"
		HoneyLanguage.Chinese.code -> "指纹支付"
		HoneyLanguage.Japanese.code -> "指紋支払い"
		HoneyLanguage.Korean.code -> "지문 지불"
		HoneyLanguage.Russian.code -> "Оплата отпечатков пальцев"
		HoneyLanguage.TraditionalChinese.code -> "指紋支付"
		else -> ""
	}

	@JvmField
	val developerOptions = when (currentLanguage) {
		HoneyLanguage.English.code -> "Developer Options"
		HoneyLanguage.Chinese.code -> "GoldStone 开发者选项"
		HoneyLanguage.Japanese.code -> "GoldStone デベロッパー"
		HoneyLanguage.Korean.code -> "GoldStone 개발자 옵션"
		HoneyLanguage.Russian.code -> "Возможности разработчика GoldStone"
		HoneyLanguage.TraditionalChinese.code -> "GoldStone Разработчик"
		else -> ""
	}

	@JvmField
	val apkChannel = when (currentLanguage) {
		HoneyLanguage.English.code -> "Apk Channel"
		HoneyLanguage.Chinese.code -> "Apk 渠道"
		HoneyLanguage.Japanese.code -> "チャネル"
		HoneyLanguage.Korean.code -> "채널"
		HoneyLanguage.Russian.code -> "Канал"
		HoneyLanguage.TraditionalChinese.code -> "渠道"
		else -> ""
	}
	@JvmField
	val versionCode = when (currentLanguage) {
		HoneyLanguage.English.code -> "Version Code"
		HoneyLanguage.Chinese.code -> "版本号"
		HoneyLanguage.Japanese.code -> "バージョンコード"
		HoneyLanguage.Korean.code -> "버전 코드"
		HoneyLanguage.Russian.code -> "Код Версии"
		HoneyLanguage.TraditionalChinese.code -> "版本號"
		else -> ""
	}
}

object FingerprintPaymentText {

	@JvmField
	val featureDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "After you open the fingerprint payment, you can confirm the transfer and signature by fingerprint. At the same time, you can still transfer or sign using the previously set password."
		HoneyLanguage.Chinese.code -> "开启指纹支付后，你可以通过指纹来确认转账及签名。同时您仍旧可以使用之前设置的密码来转账或签名。"
		HoneyLanguage.Japanese.code -> "指紋支払いを開いた後、指紋で転送と署名を確認することができます。 同時に、以前に設定したパスワードを使用して転送または署名することもできます。"
		HoneyLanguage.Korean.code -> "지문 지불을 시작하면 지문을 통해 전송 및 서명을 확인할 수 있습니다. 동시에 이전에 설정된 비밀번호를 사용하여 이전하거나 서명 할 수 있습니다."
		HoneyLanguage.Russian.code -> "После открытия оплаты по отпечатку пальца вы можете подтвердить перевод и подпись по отпечатку пальца. В то же время вы можете передавать или подписывать, используя ранее установленный пароль."
		HoneyLanguage.TraditionalChinese.code -> "開啟指紋支付後，你可以通過指紋來確認轉賬及簽名。同時您仍舊可以使用之前設置的密碼來轉賬或簽名。"
		else -> ""
	}
	@JvmField
	val unsupported = when (currentLanguage) {
		HoneyLanguage.English.code -> "Device does not support"
		HoneyLanguage.Chinese.code -> "您当前的设备不支持本功能"
		HoneyLanguage.Japanese.code -> "デバイスはサポートしていません"
		HoneyLanguage.Korean.code -> "기기가 지원하지 않습니다"
		HoneyLanguage.Russian.code -> "Устройство не поддерживает"
		HoneyLanguage.TraditionalChinese.code -> "您當前的設備不支持本功能"
		else -> ""
	}
	@JvmField
	val unsupportedDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "Your current device does not support fingerprint encryption and therefore cannot use fingerprint payment."
		HoneyLanguage.Chinese.code -> "您当前的设备不支持指纹加密，因而无法使用指纹支付。"
		HoneyLanguage.Japanese.code -> "現在のデバイスは指紋認証をサポートしていないため、指紋認証は使用できません。"
		HoneyLanguage.Korean.code -> "현재 사용중인 장치는 지문 암호화를 지원하지 않으므로 지문 인식을 사용할 수 없습니다."
		HoneyLanguage.Russian.code -> "Ваше текущее устройство не поддерживает шифрование отпечатков пальцев и поэтому не может использовать оплату отпечатков пальцев."
		HoneyLanguage.TraditionalChinese.code -> "您當前的設備不支持指紋加密，因而無法使用指紋支付。"
		else -> ""
	}

	val buttonStatusUnsupport = when (currentLanguage) {
		HoneyLanguage.English.code -> "DEVICE DOES NOT SUPPORT"
		HoneyLanguage.Chinese.code -> "您当前的设备不支持本功能"
		HoneyLanguage.Japanese.code -> "デバイスはサポートしていません"
		HoneyLanguage.Korean.code -> "기기가 지원하지 않습니다"
		HoneyLanguage.Russian.code -> "Устройство не поддерживает"
		HoneyLanguage.TraditionalChinese.code -> "您當前的設備不支持本功能"
		else -> ""
	}
	@JvmField
	val buttonStatusEnabled = when (currentLanguage) {
		HoneyLanguage.English.code -> "FINGERPRINT PAYMENT ENABLED"
		HoneyLanguage.Chinese.code -> "指纹支付已开启"
		HoneyLanguage.Japanese.code -> "指紋の支払いが有効になっています"
		HoneyLanguage.Korean.code -> "지문 지불이 사용 설정 됨"
		HoneyLanguage.Russian.code -> "ОКАЗАЛОСЬ"
		HoneyLanguage.TraditionalChinese.code -> "指紋支付已開啟"
		else -> ""
	}
	@JvmField

	val turnOffAlertTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Turn off fingerprint payment"
		HoneyLanguage.Chinese.code -> "关闭指纹支付"
		HoneyLanguage.Japanese.code -> "指紋の支払いを閉じる"
		HoneyLanguage.Korean.code -> "가까운 지문 지불"
		HoneyLanguage.Russian.code -> "Отключить оплату отпечатков пальцев"
		HoneyLanguage.TraditionalChinese.code -> "關閉指紋支付"
		else -> ""
	}
	@JvmField
	val turnOffAlertDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "Are you sure you want to turn off fingerprint payment?"
		HoneyLanguage.Chinese.code -> "确认要关闭指纹支付功能吗？重新开启需要再次验证密码。"
		HoneyLanguage.Japanese.code -> "あなたは指紋の支払いを止めてもよろしいですか？ 再開するにはパスワードの再確認が必要です。"
		HoneyLanguage.Korean.code -> "지문 지불을 해제 하시겠습니까? 다시 열려면 암호 확인이 필요합니다."
		HoneyLanguage.Russian.code -> "Вы уверены, что хотите отключить оплату по отпечаткам пальцев?"
		HoneyLanguage.TraditionalChinese.code -> "確認要關閉指紋支付功能嗎？重新開啟需要再次驗證密碼。"
		else -> ""
	}

	@JvmField
	val goToSetFingerprint = when (currentLanguage) {
		HoneyLanguage.English.code -> "Go to set the fingerprint"
		HoneyLanguage.Chinese.code -> "去设置指纹"
		HoneyLanguage.Japanese.code -> "指紋を設定する"
		HoneyLanguage.Korean.code -> "지문 설정으로 이동하십시오."
		HoneyLanguage.Russian.code -> "Перейти, чтобы установить отпечаток пальца"
		HoneyLanguage.TraditionalChinese.code -> "去設置指紋"
		else -> ""
	}

	@JvmField
	val authenticationAlertTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Fingerprint Identification"
		HoneyLanguage.Chinese.code -> "识别指纹"
		HoneyLanguage.Japanese.code -> "識別フィンガープリント"
		HoneyLanguage.Korean.code -> "식별 지문"
		HoneyLanguage.Russian.code -> "Идентификация отпечатков пальцев"
		HoneyLanguage.TraditionalChinese.code -> "識別指紋"
		else -> ""
	}
	@JvmField
	val authenticationAlertDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please identify the fingerprint to verify your identity."
		HoneyLanguage.Chinese.code -> "请识别指纹以验证您的身份。"
		HoneyLanguage.Japanese.code -> "身元を確認するには指紋を確認してください。"
		HoneyLanguage.Korean.code -> "신원을 확인하려면 지문을 확인하십시오."
		HoneyLanguage.Russian.code -> "Пожалуйста, идентифицируйте отпечаток пальца, чтобы подтвердить вашу личность."
		HoneyLanguage.TraditionalChinese.code -> "請識別指紋以驗證您的身份。"
		else -> ""
	}
	@JvmField
	val usePassword = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter password"
		HoneyLanguage.Chinese.code -> "输入密码"
		HoneyLanguage.Japanese.code -> "パスワードを入力"
		HoneyLanguage.Korean.code -> "비밀번호 입력"
		HoneyLanguage.Russian.code -> "Введите пароль"
		HoneyLanguage.TraditionalChinese.code -> "輸入密碼"
		else -> ""
	}
	@JvmField
	val buttonStatusUnset = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enable fingerprint payment"
		HoneyLanguage.Chinese.code -> "开启指纹支付"
		HoneyLanguage.Japanese.code -> "指紋の支払いを開く"
		HoneyLanguage.Korean.code -> "열린 지문 지불"
		HoneyLanguage.Russian.code -> "Включить оплату по отпечатку пальца"
		HoneyLanguage.TraditionalChinese.code -> "開啟指紋支付"
		else -> ""
	}
	@JvmField
	val permissionVerifyAlertTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Authentication"
		HoneyLanguage.Chinese.code -> "身份验证"
		HoneyLanguage.Japanese.code -> "認証"
		HoneyLanguage.Korean.code -> "인증"
		HoneyLanguage.Russian.code -> "Аутентификация"
		HoneyLanguage.TraditionalChinese.code -> "身份驗證"
		else -> ""
	}
	@JvmField
	val permissionVerifyAlertDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "A password is required to verify your identity to enable fingerprint payment."
		HoneyLanguage.Chinese.code -> "需要输入密码来验证您的身份，以开启指纹支付功能。"
		HoneyLanguage.Japanese.code -> "指紋認証を有効にするには、身元を確認するためのパスワードが必要です。"
		HoneyLanguage.Korean.code -> "지문 지불을 위해 신원을 확인하려면 암호가 필요합니다."
		HoneyLanguage.Russian.code -> "Пароль необходим для подтверждения вашей личности, чтобы включить оплату отпечатков пальцев."
		HoneyLanguage.TraditionalChinese.code -> "需要輸入密碼來驗證您的身份，以開啟指紋支付功能。"
		else -> ""
	}
	@JvmField
	val detecting = when (currentLanguage) {
		HoneyLanguage.English.code->"Identifying fingerprints"
		HoneyLanguage.Chinese.code->"正在鉴别指纹"
		HoneyLanguage.Japanese.code->"指紋の識別"
		HoneyLanguage.Korean.code->"지문 식별"
		HoneyLanguage.Russian.code->"Идентификация отпечатков пальцев"
		HoneyLanguage.TraditionalChinese.code->"正在鑑別指紋"
		else -> ""
	}

}


