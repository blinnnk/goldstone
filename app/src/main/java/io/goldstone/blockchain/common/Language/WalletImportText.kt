package io.goldstone.blockchain.common.language

/**
 * @date 2018/8/8 2:10 AM
 * @author KaySaith
 */

object ImportWalletText {
	@JvmField
	val path = when (currentLanguage) {
		HoneyLanguage.English.code -> "PATH"
		HoneyLanguage.Chinese.code -> "路径 (Path)"
		HoneyLanguage.Japanese.code -> "パス"
		HoneyLanguage.Korean.code -> "경로"
		HoneyLanguage.Russian.code -> "Путь (Path)"
		HoneyLanguage.TraditionalChinese.code -> "路徑 (Path)"
		else -> ""
	}
	@JvmField
	val walletType = when (currentLanguage) {
		HoneyLanguage.English.code -> "TYPE"
		HoneyLanguage.Chinese.code -> "类型"
		HoneyLanguage.Japanese.code -> "タイプ"
		HoneyLanguage.Korean.code -> "종류"
		HoneyLanguage.Russian.code -> "тип"
		HoneyLanguage.TraditionalChinese.code -> "類型"
		else -> ""
	}
	@JvmField
	val customBitcoinPath: (isYingYongBao: Boolean) -> String = {
		when (currentLanguage) {
			HoneyLanguage.English.code -> "Custom ${HoneyLanguage.bitcoinPrefix(it)} Path"
			HoneyLanguage.Chinese.code -> "自定义 ${HoneyLanguage.bitcoinPrefix(it)} 路径"
			HoneyLanguage.Japanese.code -> "カスタム ${HoneyLanguage.bitcoinPrefix(it)} パス"
			HoneyLanguage.Korean.code -> "사용자 정의 ${HoneyLanguage.bitcoinPrefix(it)} 경로"
			HoneyLanguage.Russian.code -> "Пользовательский путь ${HoneyLanguage.bitcoinPrefix(it)}"
			HoneyLanguage.TraditionalChinese.code -> "自定義 ${HoneyLanguage.bitcoinPrefix(it)} 路徑"
			else -> ""
		}
	}
	@JvmField
	val customEthereumPath = when (currentLanguage) {
		HoneyLanguage.English.code -> "Custom Ethereum Path"
		HoneyLanguage.Chinese.code -> "自定义ETH路径"
		HoneyLanguage.Japanese.code -> "カスタムETHパス"
		HoneyLanguage.Korean.code -> "사용자 정의 ETH 경로"
		HoneyLanguage.Russian.code -> "Пользовательский путь ETH"
		HoneyLanguage.TraditionalChinese.code -> "自定義ETH路徑"
		else -> ""
	}
	@JvmField
	val customBTCTestPath: (isYingYongBao: Boolean) -> String = {
		when (currentLanguage) {
			HoneyLanguage.English.code -> "Custom ${HoneyLanguage.bitcoinPrefix(it)} Test Path"
			HoneyLanguage.Chinese.code -> "自定义 ${HoneyLanguage.bitcoinPrefix(it)} 测试路径"
			HoneyLanguage.Japanese.code -> "カスタム ${HoneyLanguage.bitcoinPrefix(it)} テストパス"
			HoneyLanguage.Korean.code -> "사용자 지정 ${HoneyLanguage.bitcoinPrefix(it)} 테스트 경로"
			HoneyLanguage.Russian.code -> "Пользовательский тестовый путь ${HoneyLanguage.bitcoinPrefix(it)}"
			HoneyLanguage.TraditionalChinese.code -> "自定義 ${HoneyLanguage.bitcoinPrefix(it)} 測試路徑"
			else -> ""
		}
	}
	@JvmField
	val customEthereumClassicPath = when (currentLanguage) {
		HoneyLanguage.English.code -> "Custom Ethereum Classic Path"
		HoneyLanguage.Chinese.code -> "自定义ETC路径"
		HoneyLanguage.Japanese.code -> "カスタムETCパス"
		HoneyLanguage.Korean.code -> "맞춤 ETC 경로"
		HoneyLanguage.Russian.code -> "Пользовательский путь ETC"
		HoneyLanguage.TraditionalChinese.code -> "自定義ETC路徑"
		else -> ""
	}
	@JvmField
	val customLitecoinPath = when (currentLanguage) {
		HoneyLanguage.English.code -> "Custom Litecoin Path"
		HoneyLanguage.Chinese.code -> "Custom Litecoin Path"
		HoneyLanguage.Japanese.code -> "Custom Litecoin Path"
		HoneyLanguage.Korean.code -> "Custom Litecoin Path"
		HoneyLanguage.Russian.code -> "Custom Litecoin Path"
		HoneyLanguage.TraditionalChinese.code -> "Custom Litecoin Path"
		else -> ""
	}
	@JvmField
	val customBCHPath = when (currentLanguage) {
		HoneyLanguage.English.code -> "Custom Bitcoin Cash Path"
		HoneyLanguage.Chinese.code -> "Custom Bitcoin Cash Path"
		HoneyLanguage.Japanese.code -> "Custom Bitcoin Cash Path"
		HoneyLanguage.Korean.code -> "Custom Bitcoin Cash Path"
		HoneyLanguage.Russian.code -> "Custom Bitcoin Cash Path"
		HoneyLanguage.TraditionalChinese.code -> "Custom Bitcoin Cash Path"
		else -> ""
	}
	@JvmField
	val customEOSPath = when (currentLanguage) {
		HoneyLanguage.English.code -> "Custom EOS Path"
		HoneyLanguage.Chinese.code -> "Custom EOS Path"
		HoneyLanguage.Japanese.code -> "Custom EOS Path"
		HoneyLanguage.Korean.code -> "Custom EOS Path"
		HoneyLanguage.Russian.code -> "Custom EOS Path"
		HoneyLanguage.TraditionalChinese.code -> "Custom EOS Path"
		else -> ""
	}
	@JvmField
	val defaultPath = when (currentLanguage) {
		HoneyLanguage.English.code -> "Default Path"
		HoneyLanguage.Chinese.code -> "默认路径"
		HoneyLanguage.Japanese.code -> "デフォルトパス"
		HoneyLanguage.Korean.code -> "기본 경로"
		HoneyLanguage.Russian.code -> "Путь по умолчанию"
		HoneyLanguage.TraditionalChinese.code -> "默認路徑"
		else -> ""
	}
	@JvmField
	val importWallet = when (currentLanguage) {
		HoneyLanguage.English.code -> "Import Wallet"
		HoneyLanguage.Chinese.code -> "导入钱包"
		HoneyLanguage.Japanese.code -> "ウォレットをインポート"
		HoneyLanguage.Korean.code -> "지갑 도입"
		HoneyLanguage.Russian.code -> "Импорт кошелька"
		HoneyLanguage.TraditionalChinese.code -> "導入錢包"
		else -> ""
	}
	@JvmField
	val supportedChain = when (currentLanguage) {
		HoneyLanguage.English.code -> "Supported Chain Type"
		HoneyLanguage.Chinese.code -> "Supported Chain Type"
		HoneyLanguage.Japanese.code -> "Supported Chain Type"
		HoneyLanguage.Korean.code -> "Supported Chain Type"
		HoneyLanguage.Russian.code -> "Supported Chain Type"
		HoneyLanguage.TraditionalChinese.code -> "Supported Chain Type"
		else -> ""
	}
	@JvmField
	val importWatchWallet = when (currentLanguage) {
		HoneyLanguage.English.code -> "Import Watch Wallet"
		HoneyLanguage.Chinese.code -> "Import Watch Wallet"
		HoneyLanguage.Japanese.code -> "Import Watch Wallet"
		HoneyLanguage.Korean.code -> "Import Watch Wallet"
		HoneyLanguage.Russian.code -> "Import Watch Wallet"
		HoneyLanguage.TraditionalChinese.code -> "Import Watch Wallet"
		else -> ""
	}
	@JvmField
	val mnemonicHint = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter your mnemonic, split with spaces"
		HoneyLanguage.Chinese.code -> "按顺序输入助记词，使用空格间隔"
		HoneyLanguage.Japanese.code -> "スペースで間隔を空けて順番にニーモニックを入力して下さい"
		HoneyLanguage.Korean.code -> "순서대로 니모닉 프레이즈(Mnemonic Phrase)를 입력하고 스페이스로 분리시키십시오"
		HoneyLanguage.Russian.code -> "Введите мнемоническую запись по порядку, для интервалов используйте пробел"
		HoneyLanguage.TraditionalChinese.code -> "請按順序輸入助記詞，詞間使用空格符間隔"
		else -> ""
	}
	@JvmField
	val registerEOSPublicKey = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter the public key address corresponding to the account you want to register."
		HoneyLanguage.Chinese.code -> "Enter the public key address corresponding to the account you want to register."
		HoneyLanguage.Japanese.code -> "Enter the public key address corresponding to the account you want to register."
		HoneyLanguage.Korean.code -> "Enter the public key address corresponding to the account you want to register."
		HoneyLanguage.Russian.code -> "Enter the public key address corresponding to the account you want to register."
		HoneyLanguage.TraditionalChinese.code -> "Enter the public key address corresponding to the account you want to register."
		else -> ""
	}
	@JvmField
	val eosAccountName = when (currentLanguage) {
		HoneyLanguage.English.code -> "Account Name"
		HoneyLanguage.Chinese.code -> "Account Name"
		HoneyLanguage.Japanese.code -> "Account Name"
		HoneyLanguage.Korean.code -> "Account Name"
		HoneyLanguage.Russian.code -> "Account Name"
		HoneyLanguage.TraditionalChinese.code -> "Account Name"
		else -> ""
	}
	@JvmField
	val keystoreIntro = when (currentLanguage) {
		HoneyLanguage.English.code -> "Keystore is a JSON encrypted private key file. You need to enter the wallet password corresponding to the keystore. You can change your password at any time after importing."
		HoneyLanguage.Chinese.code -> "Keystore是一种JSON格式的加密私钥文件。您需要输入获得Keystore时对应的钱包密码。您可以在导入后随时修改密码。"
		HoneyLanguage.Japanese.code -> "KeystoreはJSON形式で暗号化されたプライベートキーファイルです。Keystoreに対応するウォレット・パスワードを入力する必要があります。インポート後いつでもパスワードを変更することが可能です。"
		HoneyLanguage.Korean.code -> "Keystore 는 JSON 으로 암호화 된 개인 키 파일입니다. 키 스토어에 해당하는 지갑 암호를 입력해야합니다. 가져온 후에는 언제든지 비밀번호를 변경할 수 있습니다."
		HoneyLanguage.Russian.code -> "Keystore - это зашифрованный файл с закрытым ключом в формате JSON. Вам нужно ввести соответствующий пароль кошелька для получения Keystore. После импорта, Вы можете изменять пароль в любое время."
		HoneyLanguage.TraditionalChinese.code -> "Keystore是一種JSON格式的加密私鑰文件。您需要輸入獲得Keystore時對應的錢包密碼。您可以在導入後隨時修改密碼。"
		else -> ""
	}
	@JvmField
	val keystoreHint = when (currentLanguage) {
		HoneyLanguage.English.code -> "Paste your keystore here"
		HoneyLanguage.Chinese.code -> "在此输入您的keystore"
		HoneyLanguage.Japanese.code -> "ここにお客様のKeystoneを入力して下さい"
		HoneyLanguage.Korean.code -> "이곳에 귀하의 keystore를 입력하십시오"
		HoneyLanguage.Russian.code -> "Введите сюда Ваш keystore"
		HoneyLanguage.TraditionalChinese.code -> "在此輸入您的keystore密鑰庫"
		else -> ""
	}
	@JvmField
	val privateKeyHint = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter your private key here"
		HoneyLanguage.Chinese.code -> "在此输入您的私钥"
		HoneyLanguage.Japanese.code -> "ここにお客様のプライベートキーを入力して下さい"
		HoneyLanguage.Korean.code -> "이곳에 귀하의 개인키를 입력하십시오"
		HoneyLanguage.Russian.code -> "Введите сюда Ваш закрытый ключ"
		HoneyLanguage.TraditionalChinese.code -> "在此輸入您的私鑰"
		else -> ""
	}
	@JvmField
	val address = when (currentLanguage) {
		HoneyLanguage.English.code -> "Address"
		HoneyLanguage.Chinese.code -> "钱包地址"
		HoneyLanguage.Japanese.code -> "ウォレットアドレス"
		HoneyLanguage.Korean.code -> "지갑주소"
		HoneyLanguage.Russian.code -> "Адрес кошелька"
		HoneyLanguage.TraditionalChinese.code -> "錢包地址"
		else -> ""
	}
	@JvmField
	val invalidPrivateKey = when (currentLanguage) {
		HoneyLanguage.English.code -> "Invalid private key"
		HoneyLanguage.Chinese.code -> "这不是正确格式的私钥"
		HoneyLanguage.Japanese.code -> "これは正確な形式のプライベートキーではありません"
		HoneyLanguage.Korean.code -> "이것은 정확한 포맷의 개인키가 아닙니다"
		HoneyLanguage.Russian.code -> "Неправильный формат закрытого ключа"
		HoneyLanguage.TraditionalChinese.code -> "這不是正確格式的私鑰"
		else -> ""
	}
	@JvmField
	val unvalidTestnetBTCPrivateKey = when (currentLanguage) {
		HoneyLanguage.English.code -> "Incorrect Bitcoin testnet private key address format"
		HoneyLanguage.Chinese.code -> "不是正确的比特币测试网私钥或地址格式"
		HoneyLanguage.Japanese.code -> "正しいbitcoinテストネットワークの秘密鍵またはアドレス形式ではありません"
		HoneyLanguage.Korean.code -> "올바른 비트 코인 테스트 네트워크 개인 키 또는 주소 형식이 아닙니다."
		HoneyLanguage.Russian.code -> "Не правильный личный ключ или формат адреса биткойн-теста"
		HoneyLanguage.TraditionalChinese.code -> "不是正確的比特幣測試網私鑰或地址格式"
		else -> ""
	}
	@JvmField
	val invalidLTCPrivateKey = when (currentLanguage) {
		HoneyLanguage.English.code -> "Incorrect Litecoin private key address format"
		HoneyLanguage.Chinese.code -> "Incorrect Litecoin private key address format"
		HoneyLanguage.Japanese.code -> "Incorrect Litecoin private key address format"
		HoneyLanguage.Korean.code -> "Incorrect Litecoin private key address format"
		HoneyLanguage.Russian.code -> "Incorrect Litecoin private key address format"
		HoneyLanguage.TraditionalChinese.code -> "Incorrect Litecoin private key address format"
		else -> ""
	}
	@JvmField
	val invalidEOSPrivateKey = when (currentLanguage) {
		HoneyLanguage.English.code -> "Incorrect EOS private key address format"
		HoneyLanguage.Chinese.code -> "Incorrect EOS private key address format"
		HoneyLanguage.Japanese.code -> "Incorrect EOS private key address format"
		HoneyLanguage.Korean.code -> "Incorrect EOS private key address format"
		HoneyLanguage.Russian.code -> "Incorrect EOS private key address format"
		HoneyLanguage.TraditionalChinese.code -> "Incorrect EOS private key address format"
		else -> ""
	}
	@JvmField
	val invalidMainnetBTCPrivateKey = when (currentLanguage) {
		HoneyLanguage.English.code -> "Incorrect Bitcoin main network private key address format"
		HoneyLanguage.Chinese.code -> "不是正确的比特币私钥或地址格式"
		HoneyLanguage.Japanese.code -> "正しいbitcoin秘密鍵またはアドレス形式ではありません。"
		HoneyLanguage.Korean.code -> "올바른 비트 코인 개인 키 또는 주소 형식이 아닙니다."
		HoneyLanguage.Russian.code -> "Не правильный личный ключ или адресный биткойн"
		HoneyLanguage.TraditionalChinese.code -> "不是正確的比特幣私鑰或地址格式"
		else -> ""
	}
	@JvmField
	val existAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "This address has already been imported"
		HoneyLanguage.Chinese.code -> "这个地址已经导入过了"
		HoneyLanguage.Japanese.code -> "このアドレスは既にインポートされています"
		HoneyLanguage.Korean.code -> "이 주소는 이미 도입되어 있습니다"
		HoneyLanguage.Russian.code -> "Этот адрес уже был импортирован"
		HoneyLanguage.TraditionalChinese.code -> "這個地址已經導入過了"
		else -> ""
	}
	@JvmField
	val notBip44WalletAttention = when (currentLanguage) {
		HoneyLanguage.English.code -> "Wallets imported using keystore or privateKey alone do not support the bip44 format, so the current wallet has only a unique address. If you want to use the bip44 wallet, please use mnemonic to import or create a new wallet directly."
		HoneyLanguage.Chinese.code -> "仅使用keystore或privateKey导入的钱包不支持bip44格式，因此当前钱包只有一个唯一的地址。如果您想使用bip44钱包，请使用助记符直接导入或创建新钱包。"
		HoneyLanguage.Japanese.code -> "キーストアまたはprivateKeyのみを使用してインポートされたウォレットは、bip44形式をサポートしていないため、現在のウォレットには一意のアドレスが1つしかありません。 bip44ウォレットを使用する場合は、ニーモニックを使用して新しいウォレットを直接インポートまたは作成します。"
		HoneyLanguage.Korean.code -> "키 스토어 또는 privateKey 만 사용하여 가져온 지갑은 bip44 형식을 지원하지 않으므로 현재 지갑에는 고유 주소가 하나만 있습니다. bip44 지갑을 사용하려면 니모닉을 사용하여 새 지갑을 직접 가져 오거나 작성하십시오."
		HoneyLanguage.Russian.code -> "Кошельки, импортированные с использованием только keystore или privateKey, не поддерживают формат bip44, поэтому текущий кошелек имеет только один уникальный адрес. Если вы хотите использовать кошелек bip44, используйте мнемонику для импорта или создания нового кошелька напрямую."
		HoneyLanguage.TraditionalChinese.code -> "僅使用keystore或privateKey導入的錢包不支持bip44格式，因此當前錢包只有一個唯一的地址。如果您想使用bip44錢包，請使用助記符直接導入或創建新錢包。"
		else -> ""
	}
	@JvmField
	val exportKeystore = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter password and then click confirm to get your keystore"
		HoneyLanguage.Chinese.code -> "输入密码，然后单击确认按钮以获取keystore"
		HoneyLanguage.Japanese.code -> "パスワードを入力し、[OK]ボタンをクリックしてKeystoreを取得します"
		HoneyLanguage.Korean.code -> "비밀번호를 입력한후 확인 버튼을 클릭하여 keystore를 획득하십시오"
		HoneyLanguage.Russian.code -> "Введите пароль и нажмите на кнопку подтверждения для получения keystore"
		HoneyLanguage.TraditionalChinese.code -> "輸入密碼，然後單擊確認按鈕以獲取keystore"
		else -> ""
	}
	@JvmField
	val exportPrivateKey = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter password and then click confirm to get your private key"
		HoneyLanguage.Chinese.code -> "输入密码，然后点击确认按钮获得私钥"
		HoneyLanguage.Japanese.code -> "パスワードを入力し、[OK]ボタンをクリックしてプライベートキーを取得します"
		HoneyLanguage.Korean.code -> "비밀번호를 입력한후 확인 버튼을 클릭하여 개인키를 획득하십시오"
		HoneyLanguage.Russian.code -> "Введите пароль и нажмите на кнопку подтверждения для получения закрытого ключа"
		HoneyLanguage.TraditionalChinese.code -> "輸入密碼，然後點擊確認按鈕獲得私鑰"
		else -> ""
	}
	@JvmField
	val importWalletDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "we supported that import multi-chain wallet by any single mnemonic, private key or keystore file"
		HoneyLanguage.Chinese.code -> "we supported that import multi-chain wallet by any a single mnemonic, private key or keystore file"
		HoneyLanguage.Japanese.code -> "we supported that import multi-chain wallet by any a single mnemonic, private key or keystore file"
		HoneyLanguage.Korean.code -> "we supported that import multi-chain wallet by any a single mnemonic, private key or keystore file"
		HoneyLanguage.Russian.code -> "we supported that import multi-chain wallet by any a single mnemonic, private key or keystore file"
		HoneyLanguage.TraditionalChinese.code -> "we supported that import multi-chain wallet by any a single mnemonic, private key or keystore file"
		else -> ""
	}
	@JvmField
	val exportWrongPassword = when (currentLanguage) {
		HoneyLanguage.English.code -> "Incorrect password, try again"
		HoneyLanguage.Chinese.code -> "请输入正确的密码"
		HoneyLanguage.Japanese.code -> "正確なパスワードを入力して下さい"
		HoneyLanguage.Korean.code -> "정확한 비밀번호를 입력하십시오"
		HoneyLanguage.Russian.code -> "Пожалуйста, введите правильный пароль"
		HoneyLanguage.TraditionalChinese.code -> "請輸入正確的密碼"
		else -> ""
	}
	@JvmField
	val privateKeyAlert = when (currentLanguage) {
		HoneyLanguage.English.code -> "Incorrect private key"
		HoneyLanguage.Chinese.code -> "私钥格式不正确"
		HoneyLanguage.Japanese.code -> "プライベートキー形式が正しくありません"
		HoneyLanguage.Korean.code -> "개인키 오류"
		HoneyLanguage.Russian.code -> "Неправильный формат закрытого ключа"
		HoneyLanguage.TraditionalChinese.code -> "私鑰格式不正確"
		else -> ""
	}
	@JvmField
	val mnemonicAlert = when (currentLanguage) {
		HoneyLanguage.English.code -> "Incorrect mnemonic format"
		HoneyLanguage.Chinese.code -> "助记词格式不正确"
		HoneyLanguage.Japanese.code -> "ニーモニック形式が正しくありません"
		HoneyLanguage.Korean.code -> "니모닉 포맷 오류"
		HoneyLanguage.Russian.code -> "Неправильный формат мнемонической записи"
		HoneyLanguage.TraditionalChinese.code -> "助憶口令格式不對"
		else -> ""
	}
	@JvmField
	val pathAlert = when (currentLanguage) {
		HoneyLanguage.English.code -> "Invalid path"
		HoneyLanguage.Chinese.code -> "路径格式不正确"
		HoneyLanguage.Japanese.code -> "パス形式が正しくありません"
		HoneyLanguage.Korean.code -> "로트 포맷 오류"
		HoneyLanguage.Russian.code -> "Неправильный формат пути"
		HoneyLanguage.TraditionalChinese.code -> "路徑格式不正確"
		else -> ""
	}
	@JvmField
	val addressFormatAlert = when (currentLanguage) {
		HoneyLanguage.English.code -> "Invalid address"
		HoneyLanguage.Chinese.code -> "地址格式不对"
		HoneyLanguage.Japanese.code -> "アドレス形式が違っています"
		HoneyLanguage.Korean.code -> "주소 무효"
		HoneyLanguage.Russian.code -> "Неправильный формат адреса"
		HoneyLanguage.TraditionalChinese.code -> "地址格式不正確"
		else -> ""
	}
}