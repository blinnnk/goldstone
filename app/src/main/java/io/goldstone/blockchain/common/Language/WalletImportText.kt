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
		HoneyLanguage.Russian.code -> "ДОРОЖКА (PATH)"
		HoneyLanguage.TraditionalChinese.code -> "路徑 (Path)"
		else -> ""
	}
	@JvmField
	val walletType = when (currentLanguage) {
		HoneyLanguage.English.code -> "TYPE"
		HoneyLanguage.Chinese.code -> "类型"
		HoneyLanguage.Japanese.code -> "タイプ"
		HoneyLanguage.Korean.code -> "종류"
		HoneyLanguage.Russian.code -> "ТИП"
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
		HoneyLanguage.Chinese.code -> "自定义LTC(莱特币)路径"
		HoneyLanguage.Japanese.code -> "カスタムLTC(リテコイン)パス"
		HoneyLanguage.Korean.code -> "맞춤 LTC 경로"
		HoneyLanguage.Russian.code -> "Пользовательский путь LTC"
		HoneyLanguage.TraditionalChinese.code -> "自定義LTC(莱特幣)路徑"
		else -> ""
	}
	@JvmField
	val customBCHPath = when (currentLanguage) {
		HoneyLanguage.English.code -> "Custom BCH Path"
		HoneyLanguage.Chinese.code -> "自定义BCH(比特币现金)路径"
		HoneyLanguage.Japanese.code -> "カスタムBCHパス"
		HoneyLanguage.Korean.code -> "맞춤 BCH 경로"
		HoneyLanguage.Russian.code -> "Пользовательский путь BCH"
		HoneyLanguage.TraditionalChinese.code -> "自定義BCH(比特幣現金)路徑"
		else -> ""
	}
	@JvmField
	val customEOSPath = when (currentLanguage) {
		HoneyLanguage.English.code -> "Custom EOS Path"
		HoneyLanguage.Chinese.code -> "自定义EOS路径"
		HoneyLanguage.Japanese.code -> "カスタムEOSパス"
		HoneyLanguage.Korean.code -> "맞춤 EOS 경로"
		HoneyLanguage.Russian.code -> "Пользовательский путь EOS"
		HoneyLanguage.TraditionalChinese.code -> "自定義EOS路徑"
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
		HoneyLanguage.English.code -> "Chains Supported"
		HoneyLanguage.Chinese.code -> "支持的链"
		HoneyLanguage.Japanese.code -> "サポートされるチェーン"
		HoneyLanguage.Korean.code -> "지원되는 체인"
		HoneyLanguage.Russian.code -> "Поддерживаемые цепочки"
		HoneyLanguage.TraditionalChinese.code -> "支持的鏈"
		else -> ""
	}
	@JvmField
	val importWatchWallet = when (currentLanguage) {
		HoneyLanguage.English.code -> "Import Watch Wallet"
		HoneyLanguage.Chinese.code -> "导入观察钱包"
		HoneyLanguage.Japanese.code -> "観測ウォレットをインポートする"
		HoneyLanguage.Korean.code -> "수입 관측 지갑"
		HoneyLanguage.Russian.code -> "Импорт только кошелька"
		HoneyLanguage.TraditionalChinese.code -> "導入觀察錢包"
		else -> ""
	}
	@JvmField
	val mnemonicHint = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter your mnemonics, split with spaces"
		HoneyLanguage.Chinese.code -> "按顺序输入助记词，使用空格间隔"
		HoneyLanguage.Japanese.code -> "スペースで間隔を空けて順番にニーモニックを入力して下さい"
		HoneyLanguage.Korean.code -> "순서대로 니모닉 프레이즈(Mnemonics Phrase)를 입력하고 스페이스로 분리시키십시오"
		HoneyLanguage.Russian.code -> "Введите мнемоническую запись по порядку, для интервалов используйте пробел"
		HoneyLanguage.TraditionalChinese.code -> "請按順序輸入助記詞，詞間使用空格符間隔"
		else -> ""
	}
	@JvmField
	val registerEOSPublicKey = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter the public key you want to register."
		HoneyLanguage.Chinese.code -> "填写你想绑定在这个账号上的EOS公钥"
		HoneyLanguage.Japanese.code -> "アカウントにバインドするEOS公開鍵を入力します"
		HoneyLanguage.Korean.code -> "계정에 바인딩하려는 EOS 공개 키를 입력하십시오."
		HoneyLanguage.Russian.code -> "Заполните открытый ключ EOS, который вы хотите привязать к своей учетной записи."
		HoneyLanguage.TraditionalChinese.code -> "填寫你想綁定在这個賬號上的EOS公鑰"
		else -> ""
	}
	@JvmField
	val eosAccountName = when (currentLanguage) {
		HoneyLanguage.English.code -> "Account Name"
		HoneyLanguage.Chinese.code -> "用户名"
		HoneyLanguage.Japanese.code -> "アカウント名"
		HoneyLanguage.Korean.code -> "계정 이름"
		HoneyLanguage.Russian.code -> "Имя пользователя"
		HoneyLanguage.TraditionalChinese.code -> "用戶名"
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
		HoneyLanguage.English.code -> "Import mnemonics, you will get a multi-chain wallet based on BIP44 standard. When you import a private key or keystore file of a single chain account, GoldStone will generate a multi-chain wallet for you, containing the previous single-chain assets. "
		HoneyLanguage.Chinese.code -> "导入助记词，您将获得一个基于BIP44标准的分层确定性(HD)多链钱包。如果你选择导入某个单链的私钥或是keystore文件，那么GoldStone会帮你创建一个包含之前单链资产的多链账户。"
		HoneyLanguage.Japanese.code -> "ニーモニックをインポートして、BIP44標準に基づいたマルチチェーンウォレットを取得します。シングルチェーンの秘密キーまたはキーストアファイルをインポートする場合、GoldStoneは以前のシングルチェーンアセットを使用したマルチチェーンアカウントを作成するのに役立ちます。。"
		HoneyLanguage.Korean.code -> "니모닉 가져 오기를 사용하면 BIP44 표준을 기반으로 다중 체인 지갑을 구할 수 있습니다. 단일 체인 개인 키 또는 키 저장소 파일을 가져 오도록 선택한 경우 GoldStone 은 이전 단일 체인 자산으로 다중 체인 계정을 만드는 데 도움을줍니다."
		HoneyLanguage.Russian.code -> "we supported that import multi-chain wallet by any a single mnemonic, private key or keystore file"
		HoneyLanguage.TraditionalChinese.code -> "Импортировать мнемонику для получения многоцелевого кошелька на основе стандарта BIP44. Если вы решите импортировать одноцелевой частный ключ или файл хранилища ключей, GoldStone поможет вам создать многоцелевую учетную запись с предыдущим одноцелевым активом."
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
	@JvmField
	val importPrivateKeyHint = when (currentLanguage) {
		HoneyLanguage.English.code -> "Clear text private key of any of the above chains"
		HoneyLanguage.Chinese.code -> "以上任一链的明文私钥"
		HoneyLanguage.Japanese.code -> "上記のチェーンのいずれかのクリアテキストの秘密鍵"
		HoneyLanguage.Korean.code -> "위 체인 중 하나의 일반 텍스트 개인 키"
		HoneyLanguage.Russian.code -> "Открытый текстовый закрытый ключ любой из вышеперечисленных цепочек"
		HoneyLanguage.TraditionalChinese.code -> "以上任一鏈的明文私鑰"
		else -> ""
	}
	@JvmField
	val importMnemonicsHint = when (currentLanguage) {
		HoneyLanguage.English.code -> "A set of clear text keys consisting of 12 words"
		HoneyLanguage.Chinese.code -> "一组由12个单词组成的明文秘钥"
		HoneyLanguage.Japanese.code -> "12語からなる平文キーのセット"
		HoneyLanguage.Korean.code -> "12 단어로 구성된 평문 키 세트"
		HoneyLanguage.Russian.code -> "набор ключей открытого текста, состоящий из 12 слов"
		HoneyLanguage.TraditionalChinese.code -> "一組由12個單詞組成的明文秘鑰"
		else -> ""
	}
	@JvmField
	val importKeystoreHint = when (currentLanguage) {
		HoneyLanguage.English.code -> "A string encrypted with a user password"
		HoneyLanguage.Chinese.code -> "一种使用用户密码加密的字符串秘钥"
		HoneyLanguage.Japanese.code -> "ユーザーパスワードで暗号化された文字列キー"
		HoneyLanguage.Korean.code -> "사용자 암호로 암호화 된 문자열 키"
		HoneyLanguage.Russian.code -> "Строковый ключ, зашифрованный паролем пользователя"
		HoneyLanguage.TraditionalChinese.code -> "一種使用用戶密碼加密的字符串秘鑰"
		else -> ""
	}
}