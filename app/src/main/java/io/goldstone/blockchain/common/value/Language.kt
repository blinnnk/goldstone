@file:Suppress("DEPRECATION")

package io.goldstone.blockchain.common.value

import com.blinnnk.util.HoneyDateUtil

/**
 * @date 21/03/2018 7:34 PM
 * @author KaySaith
 */
var currentLanguage = when {
	Config.getCurrentLanguageCode() == 100 ->
		HoneyLanguage.getCodeBySymbol(CountryCode.currentLanguageSymbol)
	HoneyLanguage.currentLanguageIsSupported() -> Config.getCurrentLanguageCode()
	else -> HoneyLanguage.English.code
}

object CreateWalletText {
	
	@JvmField
	val attention = when (currentLanguage) {
		HoneyLanguage.English.code -> "Password strength is critical for the security of your wallet. We will be unable to recover your password, so make sure save it yourself, and in a very secure way!"
		HoneyLanguage.Chinese.code -> "越强的密码越安全，请尽量设置更复杂的密码。我们不会为您保管密码，请您谨慎保管。"
		HoneyLanguage.Japanese.code -> "強力なパスワードであればあるほど安全です。そのため、できるだけより複雑なパスワードを設定して下さい。弊社はお客様に代わってバスワードを保管することはありませんので、気をつけて保管するようにして下さい。"
		HoneyLanguage.Korean.code -> "복잡한 비밀번호일 수록 더 안전합니다, 최대한 복잡한 비밀번호를 설정하십시오. 당사는 귀하의 비밀번호를 보관하지 않으니 귀하께서 잘 보관하십시오. "
		HoneyLanguage.Russian.code -> "Чем сильнее пароль, тем он безопаснее, пожалуйста настройте более сложный пароль. Мы не будем хранить Ваш пароль, пожалуйста, храните его в безопасности."
		HoneyLanguage.TraditionalChinese.code -> "越強的密碼越安全，請盡量設置更複雜的密碼。我們不會為您保管密碼，請您謹慎保管。"
		else -> ""
	}
	@JvmField
	val create = when (currentLanguage) {
		HoneyLanguage.English.code -> "Start a New Wallet"
		HoneyLanguage.Chinese.code -> "创建钱包"
		HoneyLanguage.Japanese.code -> "ウォレットの新規作成"
		HoneyLanguage.Korean.code -> "지갑 만들기"
		HoneyLanguage.Russian.code -> "Создание нового Кошелька"
		HoneyLanguage.TraditionalChinese.code -> "產生錢包"
		else -> ""
	}
	@JvmField
	val passwordRules = when (currentLanguage) {
		HoneyLanguage.English.code -> "A secure passwords must contain both upper and lower case letters, at least one number, and a minimum of 8 characters"
		HoneyLanguage.Chinese.code -> "请设置更安全的密码，同时包含英文大小写和数字，并少于8位"
		HoneyLanguage.Japanese.code -> "アルファベット大文字・小文字・数字を組み合わせた形で、8桁以下のより安全なパスワードを設定して下さい"
		HoneyLanguage.Korean.code -> "더 안전한 비밀번호를 설정하고, 영문 대소문자와 숫자를 동시에 포함하되 최소 8자리 수여야 합니다"
		HoneyLanguage.Russian.code -> "Пожалуйста настройте более безопасный пароль, который содержит как заглавные, так и строчные буквы, и как минимум одну цифру, пароль должен быть не более 8 символов"
		HoneyLanguage.TraditionalChinese.code -> "越強的密碼越安全，請盡量設置更複雜的密碼。我們不會為您保管密碼，請您謹慎保管"
		else -> ""
	}
	@JvmField
	val mnemonicBackUp = when (currentLanguage) {
		HoneyLanguage.English.code -> "Mnemonic Backup"
		HoneyLanguage.Chinese.code -> "备份助记词"
		HoneyLanguage.Japanese.code -> "ニーモニックのバックアップ"
		HoneyLanguage.Korean.code -> "니모닉 백업"
		HoneyLanguage.Russian.code -> "Резервная копия мнемонической записи"
		HoneyLanguage.TraditionalChinese.code -> "備份助憶口令"
		else -> ""
	}
	@JvmField
	val agreement = when (currentLanguage) {
		HoneyLanguage.English.code -> "User Agreement"
		HoneyLanguage.Chinese.code -> "用户协议"
		HoneyLanguage.Japanese.code -> "ユーザー契約書"
		HoneyLanguage.Korean.code -> "유저협의서"
		HoneyLanguage.Russian.code -> "Пользовательское соглашение"
		HoneyLanguage.TraditionalChinese.code -> "用戶協議"
		else -> ""
	}
	@JvmField
	val agreementName = when (currentLanguage) {
		HoneyLanguage.English.code -> "GoldStone User Agreement"
		HoneyLanguage.Chinese.code -> "GoldStone 用户条款"
		HoneyLanguage.Japanese.code -> "＄ ユーザー規約"
		HoneyLanguage.Korean.code -> "GoldStone 이용 약관"
		HoneyLanguage.Russian.code -> "GoldStone Пользовательские условия"
		HoneyLanguage.TraditionalChinese.code -> "GoldStone 用戶條款與隱私協議"
		else -> ""
	}
	@JvmField
	val agreementPreString = when (currentLanguage) {
		HoneyLanguage.English.code -> "I have read and agreed to"
		HoneyLanguage.Chinese.code -> "我已阅读并同意"
		HoneyLanguage.Japanese.code -> "契約書・規約を読んで同意します"
		HoneyLanguage.Korean.code -> "나는"
		HoneyLanguage.Russian.code -> "Я прочитал и принимаю"
		HoneyLanguage.TraditionalChinese.code -> "我已閱讀并同意"
		else -> ""
	}
	val agreementPostString = when (currentLanguage) {
		HoneyLanguage.English.code -> ""
		HoneyLanguage.Chinese.code -> ""
		HoneyLanguage.Japanese.code -> "を読み、同意した"
		HoneyLanguage.Korean.code -> " 읽고 동의했다"
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	@JvmField
	val agreeRemind = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please read and agree to the terms"
		HoneyLanguage.Chinese.code -> "请阅读并同意用户协议"
		HoneyLanguage.Japanese.code -> "本ユーザー契約書を読んで同意するようにして下さい"
		HoneyLanguage.Korean.code -> "유저협의서를 열람 및 동의하십시오"
		HoneyLanguage.Russian.code -> "Пожалуйста, прочитайте и примите пользовательское соглашение"
		HoneyLanguage.TraditionalChinese.code -> "請閱讀並同意用戶協議"
		else -> ""
	}
	@JvmField
	val mnemonicBackupAttention = when (currentLanguage) {
		HoneyLanguage.English.code -> "We do not save any record of our users' mnemonics, so please take good care of them! To minimize risk, it's best not to save them digitally. Maybe write it down, you know like your grandmother used to do!"
		HoneyLanguage.Chinese.code -> "请将助记词抄写在安全的地方，不要保存到网络上也不要截屏以防被黑客盗走。"
		HoneyLanguage.Japanese.code -> "本ニーモニックは安全な所に書き写し、ハッカーに盗まれることを防ぐためにネット上に保存したり、スクリーンショットを取ったりしないようにして下さい。"
		HoneyLanguage.Korean.code -> "니모닉 단어를 확인하여 계정을 기억하십시오."
		HoneyLanguage.Russian.code -> "Пожалуйста, позаботьтесь о безопасном хранении мнемонической записи, не сохраняйте ее в Интернете и не делайте снимков экрана, чтобы минимизировать риск похищения хакерами."
		HoneyLanguage.TraditionalChinese.code -> "確認一遍助憶口令，以確保您的備份正確。"
		else -> ""
	}
	@JvmField
	val mnemonicConfirmationDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please click the mnemonic words in order. This makes sure that your backup is correct."
		HoneyLanguage.Chinese.code -> "按顺序点选助记词中的单词，以确保您的备份正确。"
		HoneyLanguage.Japanese.code -> "バックアップが正しいと確保するため、ニーモニックの単語を順番でクリックしてください。"
		HoneyLanguage.Korean.code -> "니모닉 단어를 확인하여 계정을 기억하십시오."
		HoneyLanguage.Russian.code -> "Выберите слова мнемонической записи по порядку, чтобы убедиться в правильности Вашей резервной копии."
		HoneyLanguage.TraditionalChinese.code -> "確認一遍助憶口令，以確保您的備份正確。"
		else -> ""
	}
	@JvmField
	val password = when (currentLanguage) {
		HoneyLanguage.English.code -> "Password"
		HoneyLanguage.Chinese.code -> "钱包密码"
		HoneyLanguage.Japanese.code -> "ウォレットのパスワード"
		HoneyLanguage.Korean.code -> "지갑 비밀번호"
		HoneyLanguage.Russian.code -> "Пароль кошелька"
		HoneyLanguage.TraditionalChinese.code -> "錢包密碼"
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
	val repeatPassword = when (currentLanguage) {
		HoneyLanguage.English.code -> "Repeat Password"
		HoneyLanguage.Chinese.code -> "确认密码"
		HoneyLanguage.Japanese.code -> "パスワードの確認"
		HoneyLanguage.Korean.code -> "비밀번호 확인"
		HoneyLanguage.Russian.code -> "Подтверждение пароля"
		HoneyLanguage.TraditionalChinese.code -> "再次輸入密碼"
		else -> ""
	}
	val illegalSymbol = when (currentLanguage) {
		HoneyLanguage.English.code -> "Passwords can only use numbers, letters, and punctuation except for spaces."
		HoneyLanguage.Chinese.code -> "密码仅可以使用数字、字母及除空格外的半角标点符号"
		HoneyLanguage.Japanese.code -> "パスワードは空白以外の数字、文字、半角の句読点のみを使用できます"
		HoneyLanguage.Korean.code -> "암호는 공백을 제외하고 숫자, 문자 및 반자체 구두점 만 사용할 수 있습니다."
		HoneyLanguage.Russian.code -> "Пароли могут использовать только числа, буквы и знаки препинания, за исключением пробелов."
		HoneyLanguage.TraditionalChinese.code -> "密碼僅可以使用數字、字母及除空格外的半角標點符號"
		else -> ""
	}
	@JvmField
	val bothNumberAndLetter = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please include both uppercase and lowercase letters and numbers in your password."
		HoneyLanguage.Chinese.code -> "请在密码中同时包含大小写字母与数字"
		HoneyLanguage.Japanese.code -> "パスワードに大文字と小文字の両方を入力してください。"
		HoneyLanguage.Korean.code -> "암호에 대문자와 소문자 및 숫자를 모두 포함하십시오."
		HoneyLanguage.Russian.code -> "В свой пароль укажите как прописные, так и строчные буквы и цифры."
		HoneyLanguage.TraditionalChinese.code -> "請在密碼中同時包含大小寫字母與數字"
		else -> ""
	}
	@JvmField
	val tooMuchSame = when (currentLanguage) {
		HoneyLanguage.English.code -> "There are too many duplicate characters in the password, which is not very safe."
		HoneyLanguage.Chinese.code -> "您设置的密码中重复字符过多，不太安全哟"
		HoneyLanguage.Japanese.code -> "設定したパスワードに重複した文字が多すぎます。これはあまり安全ではありません。"
		HoneyLanguage.Korean.code -> "설정 한 비밀번호에 중복 문자가 너무 많아 안전하지 않습니다."
		HoneyLanguage.Russian.code -> "В пароле слишком много повторяющихся символов, что не очень безопасно."
		HoneyLanguage.TraditionalChinese.code -> "您設置的密碼中重複字符過多，不太安全喲"
		else -> ""
	}
	@JvmField
	val passwordCount = when (currentLanguage) {
		HoneyLanguage.English.code -> "Password requires 8 characters at least."
		HoneyLanguage.Chinese.code -> "密码需要至少8位字符"
		HoneyLanguage.Japanese.code -> "パスワードは少なくとも8文字必要です"
		HoneyLanguage.Korean.code -> "비밀번호는 8 자 이상이어야합니다."
		HoneyLanguage.Russian.code -> "Пароль требует не менее 8 символов"
		HoneyLanguage.TraditionalChinese.code -> "密碼需要至少8位字符"
		else -> ""
	}
	@JvmField
	val passwordRepeatAlert = when (currentLanguage) {
		HoneyLanguage.English.code -> "The password entered twice is inconsistent"
		HoneyLanguage.Chinese.code -> "两次输入的密码不一致"
		HoneyLanguage.Japanese.code -> "2回の入力したパスワードが一致していません"
		HoneyLanguage.Korean.code -> "두 번 입력 한 암호가 일치하지 않습니다"
		HoneyLanguage.Russian.code -> "Введенные пароли не одинаковы"
		HoneyLanguage.TraditionalChinese.code -> "兩次輸入的密碼不一致"
		else -> ""
	}
	@JvmField
	val emptyRepeatPasswordAlert = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please repeat the password for confirmation"
		HoneyLanguage.Chinese.code -> "请重复输入密码以作确认"
		HoneyLanguage.Japanese.code -> "確認するためにパスワードの入力を繰り返して下さい"
		HoneyLanguage.Korean.code -> "확인을 위해 비밀번호를 반복하십시오"
		HoneyLanguage.Russian.code -> "Пожалуйста, введите пароль заново для подтверждения"
		HoneyLanguage.TraditionalChinese.code -> "請重複輸入密碼以作確認"
		else -> ""
	}
	@JvmField
	val name = when (currentLanguage) {
		HoneyLanguage.English.code -> "Wallet Name"
		HoneyLanguage.Chinese.code -> "钱包名称"
		HoneyLanguage.Japanese.code -> "ウォレットの名称"
		HoneyLanguage.Korean.code -> "지갑명칭"
		HoneyLanguage.Russian.code -> "Название кошелька"
		HoneyLanguage.TraditionalChinese.code -> "錢包名稱"
		else -> ""
	}
	@JvmField
	val mnemonicConfirmation = when (currentLanguage) {
		HoneyLanguage.English.code -> "Mnemonic Confirmation"
		HoneyLanguage.Chinese.code -> "确认助记词"
		HoneyLanguage.Japanese.code -> "ニーモニックの確認"
		HoneyLanguage.Korean.code -> "니모닉 확인"
		HoneyLanguage.Russian.code -> "Подтверждение мнемонической записи"
		HoneyLanguage.TraditionalChinese.code -> "確認助憶口令"
		else -> ""
	}
	@JvmField
	val safetyLevelWeak = when (currentLanguage) {
		HoneyLanguage.English.code -> "WEAK"
		HoneyLanguage.Chinese.code -> "弱"
		HoneyLanguage.Japanese.code -> "弱い"
		HoneyLanguage.Korean.code -> "약점"
		HoneyLanguage.Russian.code -> "Слабый"
		HoneyLanguage.TraditionalChinese.code -> "弱"
		else -> ""
	}
	@JvmField
	val safetyLevelNoraml = when (currentLanguage) {
		HoneyLanguage.English.code -> "NORMAL"
		HoneyLanguage.Chinese.code -> "一般"
		HoneyLanguage.Japanese.code -> "普通"
		HoneyLanguage.Korean.code -> "일반"
		HoneyLanguage.Russian.code -> "Средний"
		HoneyLanguage.TraditionalChinese.code -> "普通"
		else -> ""
	}
	@JvmField
	val safetyLevelHigh = when (currentLanguage) {
		HoneyLanguage.English.code -> "GOOD"
		HoneyLanguage.Chinese.code -> "高"
		HoneyLanguage.Japanese.code -> "高い"
		HoneyLanguage.Korean.code -> "고"
		HoneyLanguage.Russian.code -> "Надежный"
		HoneyLanguage.TraditionalChinese.code -> "高"
		else -> ""
	}
	@JvmField
	val safetyLevelStrong = when (currentLanguage) {
		HoneyLanguage.English.code -> "STRONG"
		HoneyLanguage.Chinese.code -> "很强"
		HoneyLanguage.Japanese.code -> "非常に高い"
		HoneyLanguage.Korean.code -> "강"
		HoneyLanguage.Russian.code -> "Очень надежный"
		HoneyLanguage.TraditionalChinese.code -> "很强"
		else -> ""
	}
}

object ImportWalletText {
	
	@JvmField
	val path = when (currentLanguage) {
		HoneyLanguage.English.code -> "PATH"
		HoneyLanguage.Chinese.code -> "PATH"
		HoneyLanguage.Japanese.code -> "PATH"
		HoneyLanguage.Korean.code -> "PATH"
		HoneyLanguage.Russian.code -> "PATH"
		HoneyLanguage.TraditionalChinese.code -> "PATH"
		else -> ""
	}
	@JvmField
	val walletType = when (currentLanguage) {
		HoneyLanguage.English.code -> "TYPE"
		HoneyLanguage.Chinese.code -> "TYPE"
		HoneyLanguage.Japanese.code -> "TYPE"
		HoneyLanguage.Korean.code -> "TYPE"
		HoneyLanguage.Russian.code -> "TYPE"
		HoneyLanguage.TraditionalChinese.code -> "TYPE"
		else -> ""
	}
	@JvmField
	val customBitcoinPath = when (currentLanguage) {
		HoneyLanguage.English.code -> "Custom Bitcoin Path"
		HoneyLanguage.Chinese.code -> "Custom Bitcoin Path"
		HoneyLanguage.Japanese.code -> "Custom Bitcoin Path"
		HoneyLanguage.Korean.code -> "Custom Bitcoin Path"
		HoneyLanguage.Russian.code -> "Custom Bitcoin Path"
		HoneyLanguage.TraditionalChinese.code -> "Custom Bitcoin Path"
		else -> ""
	}
	@JvmField
	val customEthereumPath = when (currentLanguage) {
		HoneyLanguage.English.code -> "Custom Ethereum Path"
		HoneyLanguage.Chinese.code -> "Custom Ethereum Path"
		HoneyLanguage.Japanese.code -> "Custom Ethereum Path"
		HoneyLanguage.Korean.code -> "Custom Ethereum Path"
		HoneyLanguage.Russian.code -> "Custom Ethereum Path"
		HoneyLanguage.TraditionalChinese.code -> "Custom Ethereum Path"
		else -> ""
	}
	@JvmField
	val customBTCTestPath = when (currentLanguage) {
		HoneyLanguage.English.code -> "Custom Bitcoin Test Path"
		HoneyLanguage.Chinese.code -> "Custom Bitcoin Test Path"
		HoneyLanguage.Japanese.code -> "Custom Bitcoin Test Path"
		HoneyLanguage.Korean.code -> "Custom Bitcoin Test Path"
		HoneyLanguage.Russian.code -> "Custom Bitcoin Test Path"
		HoneyLanguage.TraditionalChinese.code -> "Custom Bitcoin Test Path"
		else -> ""
	}
	@JvmField
	val customEthereumClassicPath = when (currentLanguage) {
		HoneyLanguage.English.code -> "Custom Ethereum Classic Path"
		HoneyLanguage.Chinese.code -> "Custom Ethereum Classic Path"
		HoneyLanguage.Japanese.code -> "Custom Ethereum Classic Path"
		HoneyLanguage.Korean.code -> "Custom Ethereum Classic Path"
		HoneyLanguage.Russian.code -> "Custom Ethereum Classic Path"
		HoneyLanguage.TraditionalChinese.code -> "Custom Classic Ethereum Path"
		else -> ""
	}
	@JvmField
	val defaultPath = when (currentLanguage) {
		HoneyLanguage.English.code -> "Default Path"
		HoneyLanguage.Chinese.code -> "Default Path"
		HoneyLanguage.Japanese.code -> "Default Path"
		HoneyLanguage.Korean.code -> "Default Path"
		HoneyLanguage.Russian.code -> "Default Path"
		HoneyLanguage.TraditionalChinese.code -> "Default Path"
		else -> ""
	}
	@JvmField
	val importWallet = when (currentLanguage) {
		HoneyLanguage.English.code -> "Import Wallet"
		HoneyLanguage.Chinese.code -> "导入钱包"
		HoneyLanguage.Japanese.code -> "ウォレットのインポート"
		HoneyLanguage.Korean.code -> "지갑 도입"
		HoneyLanguage.Russian.code -> "Импорт кошелька"
		HoneyLanguage.TraditionalChinese.code -> "導入錢包"
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
	val keystoreIntro = when (currentLanguage) {
		HoneyLanguage.English.code -> "Keystore is a JSON encrypted private key file. You need to enter the wallet password corresponding to the keystore. You can change your password at any time after importing."
		HoneyLanguage.Chinese.code -> "Keystore是一种JSON格式的加密私钥文件。您需要输入获得Keystore时对应的钱包密码。您可以在导入后随时修改密码。"
		HoneyLanguage.Japanese.code -> "KeystoreはJSON形式で暗号化されたプライベートキーファイルです。Keystoreに対応するウォレット・パスワードを入力する必要があります。インポート後いつでもパスワードを変更することが可能です。"
		HoneyLanguage.Korean.code -> "Keystore는 JSON으로 암호화 된 개인 키 파일입니다. 키 스토어에 해당하는 지갑 암호를 입력해야합니다. 가져온 후에는 언제든지 비밀번호를 변경할 수 있습니다."
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
	val unvalidPrivateKey = when (currentLanguage) {
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
		HoneyLanguage.English.code -> "Not the correct bitcoin test network private key address format"
		HoneyLanguage.Chinese.code -> "Not the correct bitcoin test network private key address format"
		HoneyLanguage.Japanese.code -> "Not the correct bitcoin test network private key address format"
		HoneyLanguage.Korean.code -> "Not the correct bitcoin test network private key address format"
		HoneyLanguage.Russian.code -> "Not the correct bitcoin test network private key address format"
		HoneyLanguage.TraditionalChinese.code -> "Not the correct bitcoin test network private key address format"
		else -> ""
	}
	@JvmField
	val unvalidMainnetBTCPrivateKey = when (currentLanguage) {
		HoneyLanguage.English.code -> "Not the correct bitcoin main network private key address format"
		HoneyLanguage.Chinese.code -> "Not the correct bitcoin main network private key address format"
		HoneyLanguage.Japanese.code -> "Not the correct bitcoin main network private key address format"
		HoneyLanguage.Korean.code -> "Not the correct bitcoin main network private key address format"
		HoneyLanguage.Russian.code -> "Not the correct bitcoin main network private key address format"
		HoneyLanguage.TraditionalChinese.code -> "Not the correct bitcoin main network private key address format"
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
	val keystoreEthOnly = when (currentLanguage) {
		HoneyLanguage.English.code -> "The account of the keystore type currently only supports the eth, erc and etc wallet. The wallet imported by this method can only generate the account corresponding to the keystore."
		HoneyLanguage.Chinese.code -> "The account of the keystore type currently only supports the eth, erc and etc wallet. The wallet imported by this method can only generate the account corresponding to the keystore."
		HoneyLanguage.Japanese.code -> "The account of the keystore type currently only supports the eth, erc and etc wallet. The wallet imported by this method can only generate the account corresponding to the keystore."
		HoneyLanguage.Korean.code -> "The account of the keystore type currently only supports the eth, erc and etc wallet. The wallet imported by this method can only generate the account corresponding to the keystore."
		HoneyLanguage.Russian.code -> "The account of the keystore type currently only supports the eth, erc and etc wallet. The wallet imported by this method can only generate the account corresponding to the keystore."
		HoneyLanguage.TraditionalChinese.code -> "The account of the keystore type currently only supports the eth, erc and etc wallet. The wallet imported by this method can only generate the account corresponding to the keystore."
		else -> ""
	}
	@JvmField
	val notBip44WalletAttention = when (currentLanguage) {
		HoneyLanguage.English.code -> "Wallets imported using keystore or privateKey alone do not support the bip44 format, so the current wallet has only a unique address. If you want to use the bip44 wallet, please use mnemonic to import or create a new wallet directly."
		HoneyLanguage.Chinese.code -> "Wallets imported using keystore or privateKey alone do not support the bip44 format, so the current wallet has only a unique address. If you want to use the bip44 wallet, please use mnemonic to import or create a new wallet directly."
		HoneyLanguage.Japanese.code -> "Wallets imported using keystore or privateKey alone do not support the bip44 format, so the current wallet has only a unique address. If you want to use the bip44 wallet, please use mnemonic to import or create a new wallet directly."
		HoneyLanguage.Korean.code -> "Wallets imported using keystore or privateKey alone do not support the bip44 format, so the current wallet has only a unique address. If you want to use the bip44 wallet, please use mnemonic to import or create a new wallet directly."
		HoneyLanguage.Russian.code -> "Wallets imported using keystore or privateKey alone do not support the bip44 format, so the current wallet has only a unique address. If you want to use the bip44 wallet, please use mnemonic to import or create a new wallet directly."
		HoneyLanguage.TraditionalChinese.code -> "Wallets imported using keystore or privateKey alone do not support the bip44 format, so the current wallet has only a unique address. If you want to use the bip44 wallet, please use mnemonic to import or create a new wallet directly."
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
	val addressFromatAlert = when (currentLanguage) {
		HoneyLanguage.English.code -> "Invalid address"
		HoneyLanguage.Chinese.code -> "地址格式不对"
		HoneyLanguage.Japanese.code -> "アドレス形式が違っています"
		HoneyLanguage.Korean.code -> "주소 무효"
		HoneyLanguage.Russian.code -> "Неправильный формат адреса"
		HoneyLanguage.TraditionalChinese.code -> "地址格式不正確"
		else -> ""
	}
}

object DialogText {
	
	
	@JvmField
	val backUpMnemonicSucceed = when (currentLanguage) {
		HoneyLanguage.English.code -> "You have backed up your Mnemonics backed up!"
		HoneyLanguage.Chinese.code -> "助记词备份成功！"
		HoneyLanguage.Japanese.code -> "ニーモニックのバックアップに成功しました。"
		HoneyLanguage.Korean.code -> "니모닉 백업이 성공적이었습니다!"
		HoneyLanguage.Russian.code -> "Резервная копия мнемонической записи завершена!"
		HoneyLanguage.TraditionalChinese.code -> "助憶口令備份成功！"
		else -> ""
	}
	@JvmField
	val backUpMnemonic = when (currentLanguage) {
		HoneyLanguage.English.code -> "Back Up Mnemonic"
		HoneyLanguage.Chinese.code -> "备份助记词"
		HoneyLanguage.Japanese.code -> "ニーモニックのバックアップ"
		HoneyLanguage.Korean.code -> "니모닉 백업"
		HoneyLanguage.Russian.code -> "Мнемонические записи резервной копии"
		HoneyLanguage.TraditionalChinese.code -> "備份助記詞"
		else -> ""
	}
	@JvmField
	val backUpMnemonicDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "You have not backed up your mnemonic yet. It is extremely important that take care of your mnemonic. If you lose it, you will lose your digital assets."
		HoneyLanguage.Chinese.code -> "你还没有备份您的钱包。GoldStone不会为您保存任何形式的私钥/助记词/keystore，一旦您忘记就无法找回。请您一定确保钱包妥善备份后再用这个钱包接收转账。"
		HoneyLanguage.Japanese.code -> "まだお客様のウォレットがバックアップされていません。GoldStoneどのような形式であれプライペートキー/ニーモニック/Keystoreは保存されません。忘れてしまった場合は探し出すことは出来ません。必ずウォレットが適切にバックアップされていることを確認してから、このウォレットを使用して振込を受け付けて下さい。"
		HoneyLanguage.Korean.code -> "귀하의 지갑을 백업하지 않았습니다.                                                       GoldStone는 귀하의 임의의 형식의 개인키/니모닉/keystore를 저장하지 않으며, 일단 귀하께서 분실할 경우 찾을수 없습니다. 지갑을 정확히 백업한후 이 지갑으로 이체금액을 수령하십시오. "
		HoneyLanguage.Russian.code -> "Вы еще не сделали резервную копию Вашего кошелька. Никакой формат закрытого ключа / мнемонической записи / keystore не будет сохранен, если Вы его забудете, Вы уже не сможете его восстановить. Пожалуйста, убедитесь, что кошелек имеет должную резервную копию, а затем получите перевод с этого кошелька."
		HoneyLanguage.TraditionalChinese.code -> "你還沒有備份您的錢包。GoldStone不會為您保存任何形式的私鑰/助記詞/密鑰庫，一旦您忘記就無法找回。“請您一定確保錢包妥善備份後再用這個錢包接收轉賬。"
		else -> ""
	}
	@JvmField
	val networkTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Network unavailable"
		HoneyLanguage.Chinese.code -> "未检测到网络"
		HoneyLanguage.Japanese.code -> "ネットワークが検出できません"
		HoneyLanguage.Korean.code -> "네트우크 무"
		HoneyLanguage.Russian.code -> "Интернет недоступен"
		HoneyLanguage.TraditionalChinese.code -> "未檢測到網絡"
		else -> ""
	}
	@JvmField
	val networkDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "The current state of the network is not good. Please check. You can try turning on and off airplane mode to try to recover."
		HoneyLanguage.Chinese.code -> "现在的网络状态不好，请检查。您可以尝试开启再关闭飞行模式来尝试恢复。"
		HoneyLanguage.Japanese.code -> "現在のネットワーク状態がよくありませんので、チェックするようにして下さい。飛行モードをオンにした後オフにして回復を試みて下さい。"
		HoneyLanguage.Korean.code -> "네트워크의 현재 상태가 좋지 않습니다. 확인하십시오. 비행기 모드를 켜고 끄고 복구를 시도 할 수 있습니다.An extensible dialog system I designed for the ItsON SaaS telecom solution for mobile Android devices at the OS level. Having dialogs easily identifiable as the brand of the phones service provider allows the context to be clearly understood"
		HoneyLanguage.Russian.code -> "Текущее состояние Интернета плохое, пожалуйста, проверьте. Вы можете попробовать включить и выключить режим в самолете, чтобы попытаться восстановить."
		HoneyLanguage.TraditionalChinese.code -> "現在的網絡狀態不好，請檢查。您可以嘗試開啟再關閉飛行模式來嘗試恢復。"
		else -> ""
	}
	@JvmField
	val goToBackUp = when (currentLanguage) {
		HoneyLanguage.English.code -> "BACK UP"
		HoneyLanguage.Chinese.code -> "立即备份"
		HoneyLanguage.Japanese.code -> "すぐにバックアップする"
		HoneyLanguage.Korean.code -> "백업"
		HoneyLanguage.Russian.code -> "Немедленное резервное копирование"
		HoneyLanguage.TraditionalChinese.code -> "立即備份"
		else -> ""
	}
	val serverBusyTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Network Busy"
		HoneyLanguage.Chinese.code -> "网络繁忙，无法连接"
		HoneyLanguage.Japanese.code -> "ネットワークビジーで接続できません"
		HoneyLanguage.Korean.code -> "네트워크가 사용 중입니다."
		HoneyLanguage.Russian.code -> "Сеть занята"
		HoneyLanguage.TraditionalChinese.code -> "網絡繁忙，無法連接"
		else -> ""
	}
	val serverBusyDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "Can't connect with service right now, sorry, please try again later."
		HoneyLanguage.Chinese.code -> "现在无法和服务连接，抱歉呀，请稍后再试。"
		HoneyLanguage.Japanese.code -> "現在サーバーと接続できません。大変申し訳ありませんが、しばらくしてから再度試して下さい。"
		HoneyLanguage.Korean.code -> "지금 서비스에 연결할 수 없습니다. 죄송합니다. 잠시 후 다시 시도하십시오."
		HoneyLanguage.Russian.code -> "Не удается подключиться к службе прямо сейчас, приносим свои извинения, повторите попытку позже."
		HoneyLanguage.TraditionalChinese.code -> "現在無法和服務連接，抱歉呀，請稍後再試。"
		else -> ""
	}
}

object WalletText {
	
	
	@JvmField
	val totalAssets = when (currentLanguage) {
		HoneyLanguage.English.code -> "Total Assets"
		HoneyLanguage.Chinese.code -> "钱包所有财产"
		HoneyLanguage.Japanese.code -> "ウォレット内の全ての財産"
		HoneyLanguage.Korean.code -> "지갑내 모든 재산"
		HoneyLanguage.Russian.code -> "Все средства Кошелька"
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
	val addWallet = when (currentLanguage) {
		HoneyLanguage.English.code -> "Add Wallet"
		HoneyLanguage.Chinese.code -> "添加钱包"
		HoneyLanguage.Japanese.code -> "ウォレットの追加"
		HoneyLanguage.Korean.code -> "지갑추가"
		HoneyLanguage.Russian.code -> "Добавить Кошелек"
		HoneyLanguage.TraditionalChinese.code -> "添加錢包"
		else -> ""
	}
	@JvmField
	val assetChart = when (currentLanguage) {
		HoneyLanguage.English.code -> "Assets Chart"
		HoneyLanguage.Chinese.code -> "资产图表"
		HoneyLanguage.Japanese.code -> "資産チャート"
		HoneyLanguage.Korean.code -> "자산 차트"
		HoneyLanguage.Russian.code -> "График Средств"
		HoneyLanguage.TraditionalChinese.code -> "資產圖表"
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
		HoneyLanguage.Chinese.code -> "Set Default Address"
		HoneyLanguage.Japanese.code -> "Set Default Address"
		HoneyLanguage.Korean.code -> "Set Default Address"
		HoneyLanguage.Russian.code -> "Set Default Address"
		HoneyLanguage.TraditionalChinese.code -> "Set Default Address"
		else -> ""
	}
	@JvmField
	val multiChainWallet = when (currentLanguage) {
		HoneyLanguage.English.code -> "this is multi-chain wallet"
		HoneyLanguage.Chinese.code -> "this is multi-chain wallet"
		HoneyLanguage.Japanese.code -> "this is multi-chain wallet"
		HoneyLanguage.Korean.code -> "this is multi-chain wallet"
		HoneyLanguage.Russian.code -> "this is multi-chain wallet"
		HoneyLanguage.TraditionalChinese.code -> "this is multi-chain wallet"
		else -> ""
	}
	@JvmField
	val showQRCode = when (currentLanguage) {
		HoneyLanguage.English.code -> "QR Code"
		HoneyLanguage.Chinese.code -> "QR Code"
		HoneyLanguage.Japanese.code -> "QR Code"
		HoneyLanguage.Korean.code -> "QR Code"
		HoneyLanguage.Russian.code -> "QR Code"
		HoneyLanguage.TraditionalChinese.code -> "QR Code"
		else -> ""
	}
}

object TransactionText {
	
	
	@JvmField
	val transaction = when (currentLanguage) {
		HoneyLanguage.English.code -> "Transaction History"
		HoneyLanguage.Chinese.code -> "交易历史"
		HoneyLanguage.Japanese.code -> "取引履歴"
		HoneyLanguage.Korean.code -> "거래역사"
		HoneyLanguage.Russian.code -> "История операций"
		HoneyLanguage.TraditionalChinese.code -> "交易歷史"
		else -> ""
	}
	@JvmField
	val detail = when (currentLanguage) {
		HoneyLanguage.English.code -> "Transaction Details"
		HoneyLanguage.Chinese.code -> "交易详情"
		HoneyLanguage.Japanese.code -> "取引詳細"
		HoneyLanguage.Korean.code -> "거래 구체상황"
		HoneyLanguage.Russian.code -> "Подробности операций"
		HoneyLanguage.TraditionalChinese.code -> "交易明細"
		else -> ""
	}
	@JvmField
	val etherScanTransaction = when (currentLanguage) {
		HoneyLanguage.English.code -> "Etherscan Details"
		HoneyLanguage.Chinese.code -> "EtherScan 详情"
		HoneyLanguage.Japanese.code -> "EtherScan 詳細"
		HoneyLanguage.Korean.code -> "EtherScan 구체상황"
		HoneyLanguage.Russian.code -> "Подробности EtherScan"
		HoneyLanguage.TraditionalChinese.code -> "EtherScan 詳情"
		else -> ""
	}
	@JvmField
	val gasTracker = when (currentLanguage) {
		HoneyLanguage.English.code -> "GasTracker"
		HoneyLanguage.Chinese.code -> "GasTracker 详情"
		HoneyLanguage.Japanese.code -> "GasTracker 詳細"
		HoneyLanguage.Korean.code -> "GasTracker 구체상황"
		HoneyLanguage.Russian.code -> "Подробности GasTracker"
		HoneyLanguage.TraditionalChinese.code -> "GasTracker 詳情"
		else -> ""
	}
	@JvmField
	val blockChainInfo = when (currentLanguage) {
		HoneyLanguage.English.code -> "BlockChain Info"
		HoneyLanguage.Chinese.code -> "BlockChain Info"
		HoneyLanguage.Japanese.code -> "BlockChain Info"
		HoneyLanguage.Korean.code -> "BlockChain Info"
		HoneyLanguage.Russian.code -> "BlockChain Info"
		HoneyLanguage.TraditionalChinese.code -> "BlockChain Info"
		else -> ""
	}
	@JvmField
	val url = when (currentLanguage) {
		HoneyLanguage.English.code -> "Open URL"
		HoneyLanguage.Chinese.code -> "从网址打开"
		HoneyLanguage.Japanese.code -> "ウェブサイトから開く"
		HoneyLanguage.Korean.code -> "웹에서 불러오기"
		HoneyLanguage.Russian.code -> "Открыть с сайта"
		HoneyLanguage.TraditionalChinese.code -> "從網址打開"
		else -> ""
	}
	@JvmField
	val confirmTransaction = when (currentLanguage) {
		HoneyLanguage.English.code -> "Confirm transaction with your password"
		HoneyLanguage.Chinese.code -> "输入您的密码以确认交易"
		HoneyLanguage.Japanese.code -> "お客様のパスワードを入力して取引を確認します"
		HoneyLanguage.Korean.code -> "귀하의 비밀번호를 입력하여 거래를 확인하십시오"
		HoneyLanguage.Russian.code -> "Введите свой пароль для подтверждения операции"
		HoneyLanguage.TraditionalChinese.code -> "輸入您的密碼以確認交易"
		else -> ""
	}
	@JvmField
	val minerFee = when (currentLanguage) {
		HoneyLanguage.English.code -> "Miner Fee"
		HoneyLanguage.Chinese.code -> "矿工费"
		HoneyLanguage.Japanese.code -> "マイニング費"
		HoneyLanguage.Korean.code -> "채굴수수료"
		HoneyLanguage.Russian.code -> "Плата майнера"
		HoneyLanguage.TraditionalChinese.code -> "礦工費"
		else -> ""
	}
	@JvmField
	val memo = when (currentLanguage) {
		HoneyLanguage.English.code -> "Memo"
		HoneyLanguage.Chinese.code -> "备注"
		HoneyLanguage.Japanese.code -> "備考"
		HoneyLanguage.Korean.code -> "비고"
		HoneyLanguage.Russian.code -> "Примечание"
		HoneyLanguage.TraditionalChinese.code -> "메모"
		else -> ""
	}
	@JvmField
	val transactionHash = when (currentLanguage) {
		HoneyLanguage.English.code -> "Transaction Hash"
		HoneyLanguage.Chinese.code -> "交易编号(Hash)"
		HoneyLanguage.Japanese.code -> "取引番号(Hash)"
		HoneyLanguage.Korean.code -> "거래Hash"
		HoneyLanguage.Russian.code -> "Номер операции (Hash)"
		HoneyLanguage.TraditionalChinese.code -> "交易編號(Hash)"
		else -> ""
	}
	@JvmField
	val blockNumber = when (currentLanguage) {
		HoneyLanguage.English.code -> "Block Height"
		HoneyLanguage.Chinese.code -> "区块高度"
		HoneyLanguage.Japanese.code -> "ブロック高さ"
		HoneyLanguage.Korean.code -> "블록 높이"
		HoneyLanguage.Russian.code -> "Высота блока"
		HoneyLanguage.TraditionalChinese.code -> "區塊高度"
		else -> ""
	}
	@JvmField
	val transactionDate = when (currentLanguage) {
		HoneyLanguage.English.code -> "Transaction Date"
		HoneyLanguage.Chinese.code -> "交易日期"
		HoneyLanguage.Japanese.code -> "取引期日"
		HoneyLanguage.Korean.code -> "거래일자"
		HoneyLanguage.Russian.code -> "Дата операции"
		HoneyLanguage.TraditionalChinese.code -> "交易日期"
		else -> ""
	}
	@JvmField
	val gasLimit = when (currentLanguage) {
		HoneyLanguage.English.code -> "Gas Limit"
		HoneyLanguage.Chinese.code -> "燃气费上限"
		HoneyLanguage.Japanese.code -> "ガス料金上限"
		HoneyLanguage.Korean.code -> "가스요금 상한"
		HoneyLanguage.Russian.code -> "Лимит платы за газ"
		HoneyLanguage.TraditionalChinese.code -> "燃氣費上限"
		else -> ""
	}
	@JvmField
	val gasPrice = when (currentLanguage) {
		HoneyLanguage.English.code -> "Gas Price (Gwei)"
		HoneyLanguage.Chinese.code -> "燃气单价"
		HoneyLanguage.Japanese.code -> "ガス料金単価"
		HoneyLanguage.Korean.code -> "가스단가"
		HoneyLanguage.Russian.code -> "Цена за газ"
		HoneyLanguage.TraditionalChinese.code -> "燃氣單價"
		else -> ""
	}
	@JvmField
	val satoshiValue = when (currentLanguage) {
		HoneyLanguage.English.code -> "Satoshi Value"
		HoneyLanguage.Chinese.code -> "Satoshi Value"
		HoneyLanguage.Japanese.code -> "Satoshi Value"
		HoneyLanguage.Korean.code -> "Satoshi Value"
		HoneyLanguage.Russian.code -> "Satoshi Value"
		HoneyLanguage.TraditionalChinese.code -> "Satoshi Value"
		else -> ""
	}
	@JvmField
	val noMemo = when (currentLanguage) {
		HoneyLanguage.English.code -> "There isn't a memo."
		HoneyLanguage.Chinese.code -> "没有备注信息"
		HoneyLanguage.Japanese.code -> "備考情報がありません"
		HoneyLanguage.Korean.code -> "메모가 없습니다."
		HoneyLanguage.Russian.code -> "Информация примечания отсутствует"
		HoneyLanguage.TraditionalChinese.code -> "沒有備註信息。"
		else -> ""
	}
	@JvmField
	val tokenSelection = when (currentLanguage) {
		HoneyLanguage.English.code -> "Select a Token"
		HoneyLanguage.Chinese.code -> "选择一个Token"
		HoneyLanguage.Japanese.code -> "Tokenを一つ選んで下さい"
		HoneyLanguage.Korean.code -> "토큰 선택"
		HoneyLanguage.Russian.code -> "Выберите один Token"
		HoneyLanguage.TraditionalChinese.code -> "選擇一個Token"
		else -> ""
	}
	@JvmField
	val receivedFrom = when (currentLanguage) {
		HoneyLanguage.English.code -> " received from "
		HoneyLanguage.Chinese.code -> " 接受自 "
		HoneyLanguage.Japanese.code -> " 受入元 "
		HoneyLanguage.Korean.code -> " 로부터받은 "
		HoneyLanguage.Russian.code -> "Получить от"
		HoneyLanguage.TraditionalChinese.code -> " 接收自 "
		else -> ""
	}
	@JvmField
	val sentTo = when (currentLanguage) {
		HoneyLanguage.English.code -> " sent to "
		HoneyLanguage.Chinese.code -> " 发送至 "
		HoneyLanguage.Japanese.code -> " 発送先 "
		HoneyLanguage.Korean.code -> "　전송　"
		HoneyLanguage.Russian.code -> "Отправить до"
		HoneyLanguage.TraditionalChinese.code -> "  發送至 "
		else -> ""
	}
	@JvmField
	val transferResultReceived = when (currentLanguage) {
		HoneyLanguage.English.code -> "Received "
		HoneyLanguage.Chinese.code -> "入账 "
		HoneyLanguage.Japanese.code -> "記帳 "
		HoneyLanguage.Korean.code -> "받은 "
		HoneyLanguage.Russian.code -> "Зачисление на счет"
		HoneyLanguage.TraditionalChinese.code -> "入賬 "
		else -> ""
	}
	@JvmField
	val transferResultFrom = when (currentLanguage) {
		HoneyLanguage.English.code -> " from"
		HoneyLanguage.Chinese.code -> ", 来自"
		HoneyLanguage.Japanese.code -> "，受入元"
		HoneyLanguage.Korean.code -> " 발신자는입니다"
		HoneyLanguage.Russian.code -> ", из"
		HoneyLanguage.TraditionalChinese.code -> ", 來自"
		else -> ""
	}
	@JvmField
	val transferResultSent = when (currentLanguage) {
		HoneyLanguage.English.code -> " Sent"
		HoneyLanguage.Chinese.code -> "转出"
		HoneyLanguage.Japanese.code -> "振込"
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> "перевести"
		HoneyLanguage.TraditionalChinese.code -> "轉出"
		else -> ""
	}
	@JvmField
	val transferResultTo = when (currentLanguage) {
		HoneyLanguage.English.code -> " to"
		HoneyLanguage.Chinese.code -> " 至"
		HoneyLanguage.Japanese.code -> " に"
		HoneyLanguage.Korean.code -> "　점 만점에, 받는 사람"
		HoneyLanguage.Russian.code -> "до"
		HoneyLanguage.TraditionalChinese.code -> "至"
		else -> ""
	}
	@JvmField
	val confirmedBlocks = when (currentLanguage) {
		HoneyLanguage.English.code -> "6 blocks have comfirmed"
		HoneyLanguage.Chinese.code -> "6 个区块已经确认了本次转账"
		HoneyLanguage.Japanese.code -> "6つのブロックが今回の振込を確認されました"
		HoneyLanguage.Korean.code -> "6 블록에서이 양도가 확인되었습니다"
		HoneyLanguage.Russian.code -> "6 блоков подтвердили данный перевод"
		HoneyLanguage.TraditionalChinese.code -> "6 個區塊已經確認了本次轉賬"
		else -> ""
	}
}

object TokenDetailText {
	
	
	@JvmField
	val address = when (currentLanguage) {
		HoneyLanguage.English.code -> "Recipient Address"
		HoneyLanguage.Chinese.code -> "接收地址"
		HoneyLanguage.Japanese.code -> "受入アドレス"
		HoneyLanguage.Korean.code -> "접수주소"
		HoneyLanguage.Russian.code -> "Адрес получения"
		HoneyLanguage.TraditionalChinese.code -> "接收地址"
		else -> ""
	}
	@JvmField
	val deposit = when (currentLanguage) {
		HoneyLanguage.English.code -> "Deposit"
		HoneyLanguage.Chinese.code -> "接收"
		HoneyLanguage.Japanese.code -> "受入"
		HoneyLanguage.Korean.code -> "접수"
		HoneyLanguage.Russian.code -> "Получить"
		HoneyLanguage.TraditionalChinese.code -> "接收"
		else -> ""
	}
	@JvmField
	val customGas = when (currentLanguage) {
		HoneyLanguage.English.code -> "Gas Editor"
		HoneyLanguage.Chinese.code -> "自定义燃气费"
		HoneyLanguage.Japanese.code -> "ガス料金のカスタマイズ"
		HoneyLanguage.Korean.code -> "자체정의 가스요금"
		HoneyLanguage.Russian.code -> "Пользовательская плата за газ"
		HoneyLanguage.TraditionalChinese.code -> "自定義燃氣費"
		else -> ""
	}
	@JvmField
	val paymentValue = when (currentLanguage) {
		HoneyLanguage.English.code -> "Payment Value"
		HoneyLanguage.Chinese.code -> "实际价值"
		HoneyLanguage.Japanese.code -> "実際価格"
		HoneyLanguage.Korean.code -> "실제가치"
		HoneyLanguage.Russian.code -> "Реальная стоимость"
		HoneyLanguage.TraditionalChinese.code -> "實際價值"
		else -> ""
	}
	@JvmField
	val transferDetail = when (currentLanguage) {
		HoneyLanguage.English.code -> "Transfer Detail"
		HoneyLanguage.Chinese.code -> "交易详情"
		HoneyLanguage.Japanese.code -> "取引詳細"
		HoneyLanguage.Korean.code -> "거래 구체상황"
		HoneyLanguage.Russian.code -> "Подробности операций"
		HoneyLanguage.TraditionalChinese.code -> "交易詳情"
		else -> ""
	}
	@JvmField
	val customMiner = when (currentLanguage) {
		HoneyLanguage.English.code -> "Custom miner fee"
		HoneyLanguage.Chinese.code -> "自定义矿工费"
		HoneyLanguage.Japanese.code -> "マイニング費のカスタマイズ"
		HoneyLanguage.Korean.code -> "자체정의 채굴수수료"
		HoneyLanguage.Russian.code -> "Пользовательская плата майнера"
		HoneyLanguage.TraditionalChinese.code -> "自定義礦工費"
		else -> ""
	}
	@JvmField
	val tokenDetail = when (currentLanguage) {
		HoneyLanguage.English.code -> "Token Detail"
		HoneyLanguage.Chinese.code -> "代币详情"
		HoneyLanguage.Japanese.code -> "トークン詳細"
		HoneyLanguage.Korean.code -> "Token 소개"
		HoneyLanguage.Russian.code -> "Подробности токена"
		HoneyLanguage.TraditionalChinese.code -> "代幣詳情"
		else -> ""
	}
	@JvmField
	val transferToLocalWalletAlertDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "This will transfer value to an address already in one of your wallets. Are you sure?"
		HoneyLanguage.Chinese.code -> "这个地址也在GoldStone钱包中。你确定要给这个地址转账吗？"
		HoneyLanguage.Japanese.code -> "このアドレスもGoldStoneウォレットの中に存在しています。このアドレスに振込を行いますか？"
		HoneyLanguage.Korean.code -> "이 주소 역시 GoldStone지갑에 포함되어 있습니다. 이 주소로 이체할까요？"
		HoneyLanguage.Russian.code -> "Этот адрес в кошельке GoldStone Вы уверены в осуществлении перевода на данный адрес?"
		HoneyLanguage.TraditionalChinese.code -> "這個地址也在GoldStone錢包中，你確定要給自己轉賬嗎？"
		else -> ""
	}
	@JvmField
	val transferToLocalWalletAlertTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Transaction Alert"
		HoneyLanguage.Chinese.code -> "转账提示"
		HoneyLanguage.Japanese.code -> "振込アドバイス"
		HoneyLanguage.Korean.code -> "거래 알림"
		HoneyLanguage.Russian.code -> "Подсказки перевода"
		HoneyLanguage.TraditionalChinese.code -> "轉賬提示"
		else -> ""
	}
}

object CommonText {
	
	@JvmField
	val confirm = when (currentLanguage) {
		HoneyLanguage.English.code -> "CONFIRM"
		HoneyLanguage.Chinese.code -> "确认"
		HoneyLanguage.Japanese.code -> "OK"
		HoneyLanguage.Korean.code -> "확인"
		HoneyLanguage.Russian.code -> "Подтвердить"
		HoneyLanguage.TraditionalChinese.code -> "確認"
		else -> ""
	}
	@JvmField
	val wrongPassword = when (currentLanguage) {
		HoneyLanguage.English.code -> "Wrong Password"
		HoneyLanguage.Chinese.code -> "密码错误"
		HoneyLanguage.Japanese.code -> "パスワードが間違っています"
		HoneyLanguage.Korean.code -> "잘못된 비밀번호"
		HoneyLanguage.Russian.code -> "Неверный пароль"
		HoneyLanguage.TraditionalChinese.code -> "密碼錯誤"
		else -> ""
	}
	@JvmField
	val succeed = when (currentLanguage) {
		HoneyLanguage.English.code -> "Success"
		HoneyLanguage.Chinese.code -> "成功"
		HoneyLanguage.Japanese.code -> "成功"
		HoneyLanguage.Korean.code -> "성공"
		HoneyLanguage.Russian.code -> "Завершено"
		HoneyLanguage.TraditionalChinese.code -> "成功"
		else -> ""
	}
	@JvmField
	val skip = when (currentLanguage) {
		HoneyLanguage.English.code -> "Back Up Later"
		HoneyLanguage.Chinese.code -> "稍后备份"
		HoneyLanguage.Japanese.code -> "後ほどバックアップをします"
		HoneyLanguage.Korean.code -> "잠시후 백업"
		HoneyLanguage.Russian.code -> "Сделайте резервную копию позже"
		HoneyLanguage.TraditionalChinese.code -> "稍後備份"
		else -> ""
	}
	@JvmField
	val create = when (currentLanguage) {
		HoneyLanguage.English.code -> "CREATE"
		HoneyLanguage.Chinese.code -> "添加"
		HoneyLanguage.Japanese.code -> "追加する"
		HoneyLanguage.Korean.code -> "만들기"
		HoneyLanguage.Russian.code -> "Добавить"
		HoneyLanguage.TraditionalChinese.code -> "添加"
		else -> ""
	}
	@JvmField
	val cancel = when (currentLanguage) {
		HoneyLanguage.English.code -> "CANCEL"
		HoneyLanguage.Chinese.code -> "取消"
		HoneyLanguage.Japanese.code -> "キャンセルする"
		HoneyLanguage.Korean.code -> "취소"
		HoneyLanguage.Russian.code -> "Отменить"
		HoneyLanguage.TraditionalChinese.code -> "取消"
		else -> ""
	}
	@JvmField
	val new = when (currentLanguage) {
		HoneyLanguage.English.code -> "NEW"
		HoneyLanguage.Chinese.code -> "新版本"
		HoneyLanguage.Japanese.code -> "新規バージョン"
		HoneyLanguage.Korean.code -> "NEW"
		HoneyLanguage.Russian.code -> "Новая версия"
		HoneyLanguage.TraditionalChinese.code -> "새 버전"
		else -> ""
	}
	@JvmField
	val next = when (currentLanguage) {
		HoneyLanguage.English.code -> "NEXT"
		HoneyLanguage.Chinese.code -> "下一步"
		HoneyLanguage.Japanese.code -> "次へ"
		HoneyLanguage.Korean.code -> "다음"
		HoneyLanguage.Russian.code -> "Следующий шаг"
		HoneyLanguage.TraditionalChinese.code -> "下一步"
		else -> ""
	}
	@JvmField
	val saveToAlbum = when (currentLanguage) {
		HoneyLanguage.English.code -> "Save To Album"
		HoneyLanguage.Chinese.code -> "保存到相册"
		HoneyLanguage.Japanese.code -> "アルバムに保存する"
		HoneyLanguage.Korean.code -> "앨범에 저장"
		HoneyLanguage.Russian.code -> "Сохранить в альбом"
		HoneyLanguage.TraditionalChinese.code -> "保存到相簿"
		else -> ""
	}
	@JvmField
	val shareQRImage = when (currentLanguage) {
		HoneyLanguage.English.code -> "Share QR Image"
		HoneyLanguage.Chinese.code -> "分享二维码"
		HoneyLanguage.Japanese.code -> "QRコードをシェアする"
		HoneyLanguage.Korean.code -> "QR코드 공유"
		HoneyLanguage.Russian.code -> "Поделиться QR-кодом"
		HoneyLanguage.TraditionalChinese.code -> "分享二維碼"
		else -> ""
	}
	@JvmField
	val copyAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "Click to Copy Address"
		HoneyLanguage.Chinese.code -> "点击复制地址"
		HoneyLanguage.Japanese.code -> "クリックしてアドレスをコピーします"
		HoneyLanguage.Korean.code -> "클릭하여 주소 복제"
		HoneyLanguage.Russian.code -> "Нажмите, чтобы скопировать адрес"
		HoneyLanguage.TraditionalChinese.code -> "點擊複製地址"
		else -> ""
	}
	@JvmField
	val startImporting = when (currentLanguage) {
		HoneyLanguage.English.code -> "START IMPORTING"
		HoneyLanguage.Chinese.code -> "开始导入"
		HoneyLanguage.Japanese.code -> "インポートを始めます"
		HoneyLanguage.Korean.code -> "도입개시"
		HoneyLanguage.Russian.code -> "НАЧАТЬ ИМПОРТ"
		HoneyLanguage.TraditionalChinese.code -> "開始導入"
		else -> ""
	}
	@JvmField
	val enterPassword = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter Password"
		HoneyLanguage.Chinese.code -> "输入钱包密码"
		HoneyLanguage.Japanese.code -> "ウォレットのパスワードを入力して下さい"
		HoneyLanguage.Korean.code -> "지갑 비밀번호 입력"
		HoneyLanguage.Russian.code -> "Введите пароль кошелька"
		HoneyLanguage.TraditionalChinese.code -> "輸入錢包密碼"
		else -> ""
	}
	@JvmField
	val delete = when (currentLanguage) {
		HoneyLanguage.English.code -> "Delete"
		HoneyLanguage.Chinese.code -> "删除"
		HoneyLanguage.Japanese.code -> "削除する"
		HoneyLanguage.Korean.code -> "삭제"
		HoneyLanguage.Russian.code -> "Удалить"
		HoneyLanguage.TraditionalChinese.code -> "刪除"
		else -> ""
	}
	@JvmField
	val slow = when (currentLanguage) {
		HoneyLanguage.English.code -> "SLOW"
		HoneyLanguage.Chinese.code -> "慢"
		HoneyLanguage.Japanese.code -> "遅い"
		HoneyLanguage.Korean.code -> "느림"
		HoneyLanguage.Russian.code -> "Медленно"
		HoneyLanguage.TraditionalChinese.code -> "慢"
		else -> ""
	}
	@JvmField
	val fast = when (currentLanguage) {
		HoneyLanguage.English.code -> "FAST"
		HoneyLanguage.Chinese.code -> "快速"
		HoneyLanguage.Japanese.code -> "速い"
		HoneyLanguage.Korean.code -> "빠름"
		HoneyLanguage.Russian.code -> "Быстро"
		HoneyLanguage.TraditionalChinese.code -> "快速"
		else -> ""
	}
	@JvmField
	val failed = when (currentLanguage) {
		HoneyLanguage.English.code -> "FAILED"
		HoneyLanguage.Chinese.code -> "失败"
		HoneyLanguage.Japanese.code -> "失敗"
		HoneyLanguage.Korean.code -> "실패한"
		HoneyLanguage.Russian.code -> "Ошибка"
		HoneyLanguage.TraditionalChinese.code -> "失敗"
		else -> ""
	}
	@JvmField
	val send = when (currentLanguage) {
		HoneyLanguage.English.code -> "SEND"
		HoneyLanguage.Chinese.code -> "转出"
		HoneyLanguage.Japanese.code -> "振込"
		HoneyLanguage.Korean.code -> "전송"
		HoneyLanguage.Russian.code -> "Перевести"
		HoneyLanguage.TraditionalChinese.code -> "轉出"
		else -> ""
	}
	@JvmField
	val deposit = when (currentLanguage) {
		HoneyLanguage.English.code -> "DEPOSIT"
		HoneyLanguage.Chinese.code -> "存入"
		HoneyLanguage.Japanese.code -> "預入"
		HoneyLanguage.Korean.code -> "보증금"
		HoneyLanguage.Russian.code -> "Вложить"
		HoneyLanguage.TraditionalChinese.code -> "存入"
		else -> ""
	}
	@JvmField
	val from = when (currentLanguage) {
		HoneyLanguage.English.code -> "From"
		HoneyLanguage.Chinese.code -> "发送者"
		HoneyLanguage.Japanese.code -> "発送者"
		HoneyLanguage.Korean.code -> "발신자"
		HoneyLanguage.Russian.code -> "Отправитель"
		HoneyLanguage.TraditionalChinese.code -> "發送者"
		else -> ""
	}
	@JvmField
	val to = when (currentLanguage) {
		HoneyLanguage.English.code -> "To"
		HoneyLanguage.Chinese.code -> "发送至"
		HoneyLanguage.Japanese.code -> "発送先"
		HoneyLanguage.Korean.code -> "수신자"
		HoneyLanguage.Russian.code -> "Отправить до"
		HoneyLanguage.TraditionalChinese.code -> "發送至"
		else -> ""
	}
	@JvmField
	val all = when (currentLanguage) {
		HoneyLanguage.English.code -> "ALL"
		HoneyLanguage.Chinese.code -> "所有"
		HoneyLanguage.Japanese.code -> "全て"
		HoneyLanguage.Korean.code -> "모든"
		HoneyLanguage.Russian.code -> "Все"
		HoneyLanguage.TraditionalChinese.code -> "所有"
		else -> ""
	}
	@JvmField
	val upgrade = when (currentLanguage) {
		HoneyLanguage.English.code -> "UPGRADE"
		HoneyLanguage.Chinese.code -> "升级版本"
		HoneyLanguage.Japanese.code -> "バージョンアップをする"
		HoneyLanguage.Korean.code -> "업그레이드"
		HoneyLanguage.Russian.code -> "Обновить версию"
		HoneyLanguage.TraditionalChinese.code -> "升級版本"
		else -> ""
	}
}

object AlertText {
	
	@JvmField
	val btcWalletOnly = when (currentLanguage) {
		HoneyLanguage.English.code -> "The current wallet tightly supports Bitcoin and cannot search for other types of digital currency information asset management."
		HoneyLanguage.Chinese.code -> "The current wallet tightly supports Bitcoin and cannot search for other types of digital currency information asset management."
		HoneyLanguage.Japanese.code -> "The current wallet tightly supports Bitcoin and cannot search for other types of digital currency information asset management."
		HoneyLanguage.Korean.code -> "The current wallet tightly supports Bitcoin and cannot search for other types of digital currency information asset management."
		HoneyLanguage.Russian.code -> "The current wallet tightly supports Bitcoin and cannot search for other types of digital currency information asset management."
		HoneyLanguage.TraditionalChinese.code -> "The current wallet tightly supports Bitcoin and cannot search for other types of digital currency information asset management."
		else -> ""
	}
	@JvmField
	val watchOnly = when (currentLanguage) {
		HoneyLanguage.English.code -> "Current wallet is watch-only. This operation is not allowed."
		HoneyLanguage.Chinese.code -> "这是观察钱包，无法进行转账交易。"
		HoneyLanguage.Japanese.code -> "本ウォレットは観察用のため、振込取引をすることは出来ません"
		HoneyLanguage.Korean.code -> "이것은 관찰지갑으로, 이체 거래를 할수 없습니다. "
		HoneyLanguage.Russian.code -> "В данный момент можно только просмотреть кошелек, нельзя выполнить операции перевода."
		HoneyLanguage.TraditionalChinese.code -> "這是觀察錢包，無法進行轉賬交易。"
		else -> ""
	}
	@JvmField
	val importWalletNetwork = when (currentLanguage) {
		HoneyLanguage.English.code -> "Network unavailable. Network is required to check the value of the importing wallet"
		HoneyLanguage.Chinese.code -> "没有检测到网络，导入钱包时需要网络环境查询您的货币余额"
		HoneyLanguage.Japanese.code -> "ネットワークが検出されていません。ウォレットをインポートする際は通貨残高を照会するためネットワーク環境が必要です"
		HoneyLanguage.Korean.code -> "인터넷을 검색하지 못하였습니다, 지갑 도입시 온라인 환경에서 귀하의 화폐 잔고를 조회할 수 있습니다"
		HoneyLanguage.Russian.code -> "Интернет недоступен, при импорте кошелька необходимо проверить денежный баланс в условиях Интернета"
		HoneyLanguage.TraditionalChinese.code -> "沒有檢測到網絡，導入錢包時需要網絡環境查詢您的貨幣餘額"
		else -> ""
	}
	@JvmField
	val balanceNotEnough = when (currentLanguage) {
		HoneyLanguage.English.code -> "Insufficient funds for transfer and gas fees"
		HoneyLanguage.Chinese.code -> "你的账户余额不足以支付转账金额与燃气费"
		HoneyLanguage.Japanese.code -> "お客様のアカウントの残高は不足しています。金銭の振込やガス費を支払うことは出来ません"
		HoneyLanguage.Korean.code -> "귀하의 계정 잔고 부족으로 이체 금액과 채굴수수료를 지불할 수 없습니다"
		HoneyLanguage.Russian.code -> "Баланса вашего счета недостаточно для оплаты суммы перевода и платы за газ."
		HoneyLanguage.TraditionalChinese.code -> "您的賬戶餘額不足以支付轉賬金額與燃氣費"
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
	val emptyTransferValue = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please add a transfer amount"
		HoneyLanguage.Chinese.code -> "你需要设置转账的金额"
		HoneyLanguage.Japanese.code -> "振り込む金額を設定する必要があります"
		HoneyLanguage.Korean.code -> "이체 금액을 설정해야 합니다"
		HoneyLanguage.Russian.code -> "Установите сумму для перевода"
		HoneyLanguage.TraditionalChinese.code -> "你需要设置转账的金额"
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
	val transferUnvalidInputFromat = when (currentLanguage) {
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
	val wrongKeyStorePassword = when (currentLanguage) {
		HoneyLanguage.English.code -> "Invaild keystore format or password"
		HoneyLanguage.Chinese.code -> "出错啦，请检查keystore或密码的格式是否正确"
		HoneyLanguage.Japanese.code -> "ミスが有りました。Keystoreやパスワードの形式が正しいかどうかとチェックして下さい"
		HoneyLanguage.Korean.code -> "잘못된 키 저장소 형식 또는 암호"
		HoneyLanguage.Russian.code -> "Возникала ошибка, пожалуйста, проверьте keystore или формат пароля"
		HoneyLanguage.TraditionalChinese.code -> "出錯啦，請檢查keystore或密碼的格式是否正確"
		else -> ""
	}
	@JvmField
	val getRateFromServerError = when (currentLanguage) {
		HoneyLanguage.English.code -> "Something wrong happened, cannot get currency rate now"
		HoneyLanguage.Chinese.code -> "出错了，暂时无法获取汇率信息"
		HoneyLanguage.Japanese.code -> "ミスが有りました。暫くの間為替相場情報を入手できません"
		HoneyLanguage.Korean.code -> "잘못된 일이 발생하여 환율을받지 못했습니다."
		HoneyLanguage.Russian.code -> "Произошла ошибка при получении записей переводов от "
		HoneyLanguage.TraditionalChinese.code -> "發生了錯誤，現在無法獲得貨幣匯率"
		else -> ""
	}
	@JvmField
	val getTransactionErrorPrefix = when (currentLanguage) {
		HoneyLanguage.English.code -> "An error occurred getting transaction records from Ethereum"
		HoneyLanguage.Chinese.code -> "从以太坊"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> "거래 알림"
		HoneyLanguage.Russian.code -> "Ethereum"
		HoneyLanguage.TraditionalChinese.code -> "從以太坊"
		else -> ""
	}
	@JvmField
	val getTransactionErrorSuffix = when (currentLanguage) {
		HoneyLanguage.English.code -> ""
		HoneyLanguage.Chinese.code -> "获取转账记录时发生了错误"
		HoneyLanguage.Japanese.code -> "から振り込む記録が発生した時のミスを取得しています"
		HoneyLanguage.Korean.code -> "에서 이전 기록을 가져 오는 중에 오류가 발생했습니다."
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> "獲取轉賬記錄時發生了錯誤"
		else -> ""
	}
}

object CurrentWalletText {
	@JvmField
	val Wallets = when (currentLanguage) {
		HoneyLanguage.English.code -> "Wallets"
		HoneyLanguage.Chinese.code -> "钱包列表"
		HoneyLanguage.Japanese.code -> "ウォレットリスト"
		HoneyLanguage.Korean.code -> "지갑리스트"
		HoneyLanguage.Russian.code -> "Список кошельков"
		HoneyLanguage.TraditionalChinese.code -> "錢包列表"
		else -> ""
	}
}

object WatchOnlyText {
	
	
	@JvmField
	val enterDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter the address of the wallet to be observed"
		HoneyLanguage.Chinese.code -> "请输入想观察的钱包地址"
		HoneyLanguage.Japanese.code -> "確認したいウォレットのアドレスを入力して下さい"
		HoneyLanguage.Korean.code -> "관찰하고 싶은 지갑 주소를 입력하세요."
		HoneyLanguage.Russian.code -> "Пожалуйста, введите адрес кошелька, который Вы хотите просмотреть"
		HoneyLanguage.TraditionalChinese.code -> "輸入要觀察的錢包地址"
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

object Alert {
	@JvmField
	val selectCurrency = when (currentLanguage) {
		HoneyLanguage.English.code -> "Once you've selected this, you'll need to wait a moment while we restart the app. Are you sure you'd like to switch currency settings?"
		HoneyLanguage.Chinese.code -> "一旦你选择切换货币，应用程序将被重新启动，并等待几秒钟。 你确定要切换货币设置吗？"
		HoneyLanguage.Japanese.code -> "貨幣の切り替えを選択すると、アプリケーションが再起動され、数秒待つことになります貨幣設定の切り替えをしますか？"
		HoneyLanguage.Korean.code -> "일단 화폐 교체를 선택하면, 응용 프로그램은 다시 시작후 몇초동안 기다려야 합니다. 화폐 설정을 교체할까요？"
		HoneyLanguage.Russian.code -> "При смене валюты программа будет перезапущена, ожидание составит несколько секунд. Вы уверены, что хотите изменить настройки валюты?"
		HoneyLanguage.TraditionalChinese.code -> "一旦你選擇切換貨幣，應用程序將被重新啟動，並等待幾秒鐘。你確定要切換貨幣設置嗎？"
		else -> ""
	}
}

object WalletSettingsText {
	
	@JvmField
	val copy = when (currentLanguage) {
		HoneyLanguage.English.code -> "Click to Copy Address"
		HoneyLanguage.Chinese.code -> "点击复制地址"
		HoneyLanguage.Japanese.code -> "クリックしてアドレスをコピーします"
		HoneyLanguage.Korean.code -> "클릭하여 주소 복제"
		HoneyLanguage.Russian.code -> "Нажмите, чтобы скопировать адрес"
		HoneyLanguage.TraditionalChinese.code -> "點擊複製地址"
		else -> ""
	}
	@JvmField
	val newETHAndERCAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "New ETH & ERC Address"
		HoneyLanguage.Chinese.code -> "New ETH & ERC Address"
		HoneyLanguage.Japanese.code -> "New ETH & ERC Address"
		HoneyLanguage.Korean.code -> "New ETH & ERC Address"
		HoneyLanguage.Russian.code -> "New ETH & ERC Address"
		HoneyLanguage.TraditionalChinese.code -> "New ETH & ERC Address"
		else -> ""
	}
	@JvmField
	val newETCAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "New ETC Address"
		HoneyLanguage.Chinese.code -> "New ETC Address"
		HoneyLanguage.Japanese.code -> "New ETC Address"
		HoneyLanguage.Korean.code -> "New ETC Address"
		HoneyLanguage.Russian.code -> "New ETC Address"
		HoneyLanguage.TraditionalChinese.code -> "New ETC Address"
		else -> ""
	}
	@JvmField
	val newBTCAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "New BTC Address"
		HoneyLanguage.Chinese.code -> "New BTC Address"
		HoneyLanguage.Japanese.code -> "New BTC Address"
		HoneyLanguage.Korean.code -> "New BTC Address"
		HoneyLanguage.Russian.code -> "New BTC Address"
		HoneyLanguage.TraditionalChinese.code -> "New BTC Address"
		else -> ""
	}
	@JvmField
	val viewAddresses = when (currentLanguage) {
		HoneyLanguage.English.code -> "View All Addresses"
		HoneyLanguage.Chinese.code -> "View All Addresses"
		HoneyLanguage.Japanese.code -> "View All Addresses"
		HoneyLanguage.Korean.code -> "View All Addresses"
		HoneyLanguage.Russian.code -> "View All Addresses"
		HoneyLanguage.TraditionalChinese.code -> "View All Addresses"
		else -> ""
	}
	@JvmField
	val allETHAndERCAddresses = when (currentLanguage) {
		HoneyLanguage.English.code -> "All ETH & ERC Addresses"
		HoneyLanguage.Chinese.code -> "All ETH & ERC Addresses"
		HoneyLanguage.Japanese.code -> "All ETH & ERC Addresses"
		HoneyLanguage.Korean.code -> "All ETH & ERC Addresses"
		HoneyLanguage.Russian.code -> "All ETH & ERC Addresses"
		HoneyLanguage.TraditionalChinese.code -> "All ETH & ERC Addresses"
		else -> ""
	}
	@JvmField
	val allETCAddresses = when (currentLanguage) {
		HoneyLanguage.English.code -> "All ETC Addresses"
		HoneyLanguage.Chinese.code -> "All ETC Addresses"
		HoneyLanguage.Japanese.code -> "All ETC Addresses"
		HoneyLanguage.Korean.code -> "All ETC Addresses"
		HoneyLanguage.Russian.code -> "All ETC Addresses"
		HoneyLanguage.TraditionalChinese.code -> "All ETC Addresses"
		else -> ""
	}
	@JvmField
	val allBtCAddresses = when (currentLanguage) {
		HoneyLanguage.English.code -> "All BTC Addresses"
		HoneyLanguage.Chinese.code -> "All BTC Addresses"
		HoneyLanguage.Japanese.code -> "All BTC Addresses"
		HoneyLanguage.Korean.code -> "All BTC Addresses"
		HoneyLanguage.Russian.code -> "All BTC Addresses"
		HoneyLanguage.TraditionalChinese.code -> "All BTC Addresses"
		else -> ""
	}
	@JvmField
	val allBtCTestAddresses = when (currentLanguage) {
		HoneyLanguage.English.code -> "All BTC Test Addresses"
		HoneyLanguage.Chinese.code -> "All BTC Test Addresses"
		HoneyLanguage.Japanese.code -> "All BTC Test Addresses"
		HoneyLanguage.Korean.code -> "All BTC Test Addresses"
		HoneyLanguage.Russian.code -> "All BTC Test Addresses"
		HoneyLanguage.TraditionalChinese.code -> "All BTC Test Addresses"
		else -> ""
	}
	@JvmField
	val balance = when (currentLanguage) {
		HoneyLanguage.English.code -> "Balance"
		HoneyLanguage.Chinese.code -> "余额"
		HoneyLanguage.Japanese.code -> "残高"
		HoneyLanguage.Korean.code -> "잔고"
		HoneyLanguage.Russian.code -> "Баланс"
		HoneyLanguage.TraditionalChinese.code -> "余额"
		else -> ""
	}
	@JvmField
	val walletName = when (currentLanguage) {
		HoneyLanguage.English.code -> "Wallet Name"
		HoneyLanguage.Chinese.code -> "钱包名称"
		HoneyLanguage.Japanese.code -> "ウォレットの名称"
		HoneyLanguage.Korean.code -> "지갑명칭"
		HoneyLanguage.Russian.code -> "Название кошелька"
		HoneyLanguage.TraditionalChinese.code -> "錢包名稱"
		else -> ""
	}
	@JvmField
	val walletNameSettings = when (currentLanguage) {
		HoneyLanguage.English.code -> "Name Your Wallet"
		HoneyLanguage.Chinese.code -> "钱包名称设置"
		HoneyLanguage.Japanese.code -> "ウォレットの名称設定"
		HoneyLanguage.Korean.code -> "지갑명칭 설정"
		HoneyLanguage.Russian.code -> "Настройка названия кошелька"
		HoneyLanguage.TraditionalChinese.code -> "錢包名稱設置"
		else -> ""
	}
	@JvmField
	val walletSettings = when (currentLanguage) {
		HoneyLanguage.English.code -> "Wallet Settings"
		HoneyLanguage.Chinese.code -> "钱包设置"
		HoneyLanguage.Japanese.code -> "ウォレットの設定"
		HoneyLanguage.Korean.code -> "월렛 설정"
		HoneyLanguage.Russian.code -> "Настройка кошелька"
		HoneyLanguage.TraditionalChinese.code -> "錢包設置"
		else -> ""
	}
	@JvmField
	val passwordSettings = when (currentLanguage) {
		HoneyLanguage.English.code -> "Change Password"
		HoneyLanguage.Chinese.code -> "修改密码"
		HoneyLanguage.Japanese.code -> "パスワードを変更する"
		HoneyLanguage.Korean.code -> "비밀번호 변경"
		HoneyLanguage.Russian.code -> "Изменить пароль"
		HoneyLanguage.TraditionalChinese.code -> "修改密碼"
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
	val hintAlert = when (currentLanguage) {
		HoneyLanguage.English.code -> "It is empty, please enter some word"
		HoneyLanguage.Chinese.code -> "写点什么"
		HoneyLanguage.Japanese.code -> "何を書きますか？"
		HoneyLanguage.Korean.code -> "비어 있습니다. 약간의 단어를 입력하십시오."
		HoneyLanguage.Russian.code -> "Напишите что-нибудь"
		HoneyLanguage.TraditionalChinese.code -> "寫點什麼"
		else -> ""
	}
	@JvmField
	val exportPrivateKey = when (currentLanguage) {
		HoneyLanguage.English.code -> "Export Private Key"
		HoneyLanguage.Chinese.code -> "导出私钥"
		HoneyLanguage.Japanese.code -> "プライベートキーをエクスポートする"
		HoneyLanguage.Korean.code -> "개인키 도출"
		HoneyLanguage.Russian.code -> "Экспортировать закрытый ключ"
		HoneyLanguage.TraditionalChinese.code -> "導出金鑰"
		else -> ""
	}
	@JvmField
	val exportKeystore = when (currentLanguage) {
		HoneyLanguage.English.code -> "Export Keystore"
		HoneyLanguage.Chinese.code -> "导出keystore"
		HoneyLanguage.Japanese.code -> "Keystoneをエクスポートする"
		HoneyLanguage.Korean.code -> "keystore 도출"
		HoneyLanguage.Russian.code -> "Экспортировать keystore"
		HoneyLanguage.TraditionalChinese.code -> "導出 Keystore"
		else -> ""
	}
	@JvmField
	val backUpMnemonic = when (currentLanguage) {
		HoneyLanguage.English.code -> "Back Up Your Mnemonics"
		HoneyLanguage.Chinese.code -> "请备份助记词"
		HoneyLanguage.Japanese.code -> "ニーモニックのバックアップをして下さい"
		HoneyLanguage.Korean.code -> "니모닉 백업"
		HoneyLanguage.Russian.code -> "Пожалуйста, сделайте резервную копию мнемонической записи"
		HoneyLanguage.TraditionalChinese.code -> "備份助憶口令"
		else -> ""
	}
	@JvmField
	val defaultAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "Default Address"
		HoneyLanguage.Chinese.code -> "Default Address"
		HoneyLanguage.Japanese.code -> "Default Address"
		HoneyLanguage.Korean.code -> "Default Address"
		HoneyLanguage.Russian.code -> "Default Address"
		HoneyLanguage.TraditionalChinese.code -> "Default Address"
		else -> ""
	}
	@JvmField
	val currentMultiChainAddresses = when (currentLanguage) {
		HoneyLanguage.English.code -> "Current Multi-Chain Addresses"
		HoneyLanguage.Chinese.code -> "Current Multi-Chain Addresses"
		HoneyLanguage.Japanese.code -> "Current Multi-Chain Addresses"
		HoneyLanguage.Korean.code -> "Current Multi-Chain Addresses"
		HoneyLanguage.Russian.code -> "Current Multi-Chain Addresses"
		HoneyLanguage.TraditionalChinese.code -> "Current Multi-Chain Addresses"
		else -> ""
	}
	@JvmField
	val ethereumSeriesAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "Ethereum Series Address"
		HoneyLanguage.Chinese.code -> "Ethereum Series Address"
		HoneyLanguage.Japanese.code -> "Ethereum Series Address"
		HoneyLanguage.Korean.code -> "Ethereum Series Address"
		HoneyLanguage.Russian.code -> "Ethereum Series Address"
		HoneyLanguage.TraditionalChinese.code -> "Ethereum Series Address"
		else -> ""
	}
	@JvmField
	val ethereumClassicAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "Ethereum Classic Address"
		HoneyLanguage.Chinese.code -> "Ethereum Classic Address"
		HoneyLanguage.Japanese.code -> "Ethereum Classic Address"
		HoneyLanguage.Korean.code -> "Ethereum Classic Address"
		HoneyLanguage.Russian.code -> "Ethereum Classic Address"
		HoneyLanguage.TraditionalChinese.code -> "Ethereum Classic Address"
		else -> ""
	}
	@JvmField
	val bitcoinAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "Bitcoin Address"
		HoneyLanguage.Chinese.code -> "Bitcoin Address"
		HoneyLanguage.Japanese.code -> "Bitcoin Address"
		HoneyLanguage.Korean.code -> "Bitcoin Address"
		HoneyLanguage.Russian.code -> "Bitcoin Address"
		HoneyLanguage.TraditionalChinese.code -> "Bitcoin Address"
		else -> ""
	}
	@JvmField
	val bitcoinTestAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "Bitcoin Test Address"
		HoneyLanguage.Chinese.code -> "Bitcoin Test Address"
		HoneyLanguage.Japanese.code -> "Bitcoin Test Address"
		HoneyLanguage.Korean.code -> "Bitcoin Test Address"
		HoneyLanguage.Russian.code -> "Bitcoin Test Address"
		HoneyLanguage.TraditionalChinese.code -> "Bitcoin Test Address"
		else -> ""
	}
	@JvmField
	val backUpMnemonicGotBefore = when (currentLanguage) {
		HoneyLanguage.English.code -> "Click the mnemonic words in order, so as to ensure that your backup is correct."
		HoneyLanguage.Chinese.code -> "按顺序点选助记词中的单词，以确保您的备份正确。"
		HoneyLanguage.Japanese.code -> "バックアップが正しいと確保するため、ニーモニックの単語を順番でクリックしてください。"
		HoneyLanguage.Korean.code -> "백업의 정확성를 확보하기 위해 순서대로 니모닉 프레이즈 중의 단어를 선택하십시오."
		HoneyLanguage.Russian.code -> "Выберите слова мнемонической записи по порядку, чтобы убедиться в правильности Вашей резервной копии."
		HoneyLanguage.TraditionalChinese.code -> "请按顺序点选助憶口令中的單詞，以確保您的備份正確。"
		else -> ""
	}
	@JvmField
	val safeAttention = when (currentLanguage) {
		HoneyLanguage.English.code -> "Safety Alert"
		HoneyLanguage.Chinese.code -> "安全提示"
		HoneyLanguage.Japanese.code -> "セキュリティヒント"
		HoneyLanguage.Korean.code -> "안전 제시"
		HoneyLanguage.Russian.code -> "Подсказки безопасности"
		HoneyLanguage.TraditionalChinese.code -> "安全提醒"
		else -> ""
	}
	@JvmField
	val delete = when (currentLanguage) {
		HoneyLanguage.English.code -> "Delete Wallet"
		HoneyLanguage.Chinese.code -> "删除钱包"
		HoneyLanguage.Japanese.code -> "ウォレットの削除"
		HoneyLanguage.Korean.code -> "지갑 삭제"
		HoneyLanguage.Russian.code -> "Удалить Кошелек"
		HoneyLanguage.TraditionalChinese.code -> "刪除錢包"
		else -> ""
	}
	@JvmField
	val deleteInfoTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Are you sure you want to delete the current wallet? Be sure you have backed it up!"
		HoneyLanguage.Chinese.code -> "确认要删除钱包吗？(删除前请确保已妥善备份)"
		HoneyLanguage.Japanese.code -> "ウォレットを削除しますか？(削除する前にバックアップを行って下さい)"
		HoneyLanguage.Korean.code -> "지갑을 삭제할까요?(삭제전 정확히 백업하였는지 확인하세요)"
		HoneyLanguage.Russian.code -> "Подтвердить удаление кошелька? (перед удалением сохраните надлежащую резервную копию)"
		HoneyLanguage.TraditionalChinese.code -> "確認要刪除錢包嗎？ (刪除前請確保已妥善備份)"
		else -> ""
	}
	@JvmField
	val deleteInfoSubtitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Before deleting your wallet, please back up its information (private key, keystore, mnemonics). We never save your data, so we won't be able to recover it."
		HoneyLanguage.Chinese.code -> "在删除您的钱包之前，请备份您的钱包信息，我们绝不会保存您的数据，因此我们无法恢复此操作"
		HoneyLanguage.Japanese.code -> "お客様のウォレットを削除する前、ウォレット情報をバックアップして下さい。弊社ではお客様のデータを保管しておらず、この操作をリカバリーすることはできません"
		HoneyLanguage.Korean.code -> "귀하의 지갑을 삭제하기전, 귀하의 지갑정보를 백업하십시오. 당사는 귀하의 데이터를 저장하지 않기 때문에 이번 조작을 복구할 수 없습니다"
		HoneyLanguage.Russian.code -> "Перед удалением кошелька, создайте резервную копию его информации, мы не будем сохранять Ваши данные, поэтому мы не сможем их восстановить."
		HoneyLanguage.TraditionalChinese.code -> "在刪除您的錢包之前，請備份您的錢包信息，我們絕不會保存您的數據，因此我們無法恢復此操作"
		else -> ""
	}
	@JvmField
	val oldPassword = when (currentLanguage) {
		HoneyLanguage.English.code -> "Old Password"
		HoneyLanguage.Chinese.code -> "旧密码"
		HoneyLanguage.Japanese.code -> "旧パスワード"
		HoneyLanguage.Korean.code -> "기존비밀번호"
		HoneyLanguage.Russian.code -> "Старый пароль"
		HoneyLanguage.TraditionalChinese.code -> "舊密碼"
		else -> ""
	}
	@JvmField
	val newPassword = when (currentLanguage) {
		HoneyLanguage.English.code -> "New Password"
		HoneyLanguage.Chinese.code -> "新密码"
		HoneyLanguage.Japanese.code -> "新パスワード"
		HoneyLanguage.Korean.code -> "새비밀번호"
		HoneyLanguage.Russian.code -> "Новый пароль"
		HoneyLanguage.TraditionalChinese.code -> "新密碼"
		else -> ""
	}
	@JvmField
	val emptyNameAleryt = when (currentLanguage) {
		HoneyLanguage.English.code -> "The wallet name is empty"
		HoneyLanguage.Chinese.code -> "还没有填写钱包名称"
		HoneyLanguage.Japanese.code -> "ウォレットの名称が記入されていません"
		HoneyLanguage.Korean.code -> "지갑 이름을 입력하십시오."
		HoneyLanguage.Russian.code -> "Не введено название кошелька"
		HoneyLanguage.TraditionalChinese.code -> "還沒有填寫錢包名稱"
		else -> ""
	}
}

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
	val profile = when (currentLanguage) {
		HoneyLanguage.English.code -> "Profile"
		HoneyLanguage.Chinese.code -> "个人主页"
		HoneyLanguage.Japanese.code -> "個人ページ"
		HoneyLanguage.Korean.code -> "개인 홈"
		HoneyLanguage.Russian.code -> "Личная страница"
		HoneyLanguage.TraditionalChinese.code -> "個人檔案"
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
		HoneyLanguage.Russian.code -> "Пользовательское соглашение"
		HoneyLanguage.TraditionalChinese.code -> "用戶協議"
		else -> ""
	}
	@JvmField
	val version = when (currentLanguage) {
		HoneyLanguage.English.code -> "Version"
		HoneyLanguage.Chinese.code -> "软件版本"
		HoneyLanguage.Japanese.code -> "ソフトウェアバージョン"
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
		HoneyLanguage.TraditionalChinese.code -> "分享GoldStone"
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
		HoneyLanguage.English.code -> "Wallet Manager"
		HoneyLanguage.Chinese.code -> "Wallet Manager"
		HoneyLanguage.Japanese.code -> "Wallet Manager"
		HoneyLanguage.Korean.code -> "Wallet Manager"
		HoneyLanguage.Russian.code -> "Wallet Manager"
		HoneyLanguage.TraditionalChinese.code -> "Wallet Manager"
		else -> ""
	}
	@JvmField
	val shareContent = when (currentLanguage) {
		HoneyLanguage.English.code -> "GoldStone\ncrypto digtal wallet the safest one for you\nhttps://GoldStone.io"
		HoneyLanguage.Chinese.code -> "GoldStone\n安全，易用，快捷\nhttps://GoldStone.io"
		HoneyLanguage.Japanese.code -> "GoldStone/nは安全で使いやすくて便利\nhttps://GoldStone.io"
		HoneyLanguage.Korean.code -> "GoldStone\n안전하고 사용하기 쉽고 빠름\nhttps://GoldStone.io"
		HoneyLanguage.Russian.code -> "GoldStone\n цифровой кошелек самый безопасный для вас\nhttps://GoldStone.io"
		HoneyLanguage.TraditionalChinese.code -> "GoldStone\n安全，易用，快捷\nhttps://GoldStone.io"
		else -> ""
	}
	@JvmField
	val deletContactAlertTitle = when (currentLanguage) {
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
}

object EmptyText {
	
	
	@JvmField
	val transferToAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter an wallet address or select a contact below"
		HoneyLanguage.Chinese.code -> "输入钱包地址或选择一个联系人"
		HoneyLanguage.Japanese.code -> "ウォレットアドレスを入力するか、連絡先を選んで下さい"
		HoneyLanguage.Korean.code -> "지갑 주소를 입력하거나 아래 연락처를 선택하십시오."
		HoneyLanguage.Russian.code -> "Введите адрес кошелька или выберите контакт"
		HoneyLanguage.TraditionalChinese.code -> "輸入錢包地址或選擇一個聯繫人"
		else -> ""
	}
	@JvmField
	val searchInput = when (currentLanguage) {
		HoneyLanguage.English.code -> "Token name or contract address"
		HoneyLanguage.Chinese.code -> "搜索Token名称或合约地址"
		HoneyLanguage.Japanese.code -> "Token名称もしくは契約アドレスを検索する"
		HoneyLanguage.Korean.code -> "토큰 이름 또는 계약서 주소 검색"
		HoneyLanguage.Russian.code -> "Поиск названия Token или адреса соглашения"
		HoneyLanguage.TraditionalChinese.code -> "搜索Token名稱或合約地址"
		else -> ""
	}
	@JvmField
	val tokenDetailTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "No token transactions found"
		HoneyLanguage.Chinese.code -> "还没有任何交易记录"
		HoneyLanguage.Japanese.code -> "何の取引記録もありません"
		HoneyLanguage.Korean.code -> "임의의 거래기록이 없습니다"
		HoneyLanguage.Russian.code -> "Отсутствует какая-либо история операций"
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
	@JvmField
	val tokenDetailSubtitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "No transaction history for this token"
		HoneyLanguage.Chinese.code -> "在区块链上没有交易，所以您没有图表和记录信息"
		HoneyLanguage.Japanese.code -> "ブロックチェーン上に取引がありません。お客様にチャートや記録情報が表示できません"
		HoneyLanguage.Korean.code -> "블록체인에서 거래가 없으므로, 귀하한테 도표와 기록정보가 없습니다 "
		HoneyLanguage.Russian.code -> "В блокчейне нет операций, поэтому отсутствуют диаграммы и история"
		HoneyLanguage.TraditionalChinese.code -> "區塊鏈中沒有交易，所以您沒有圖表和記錄"
		else -> ""
	}
	@JvmField
	val searchTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Token not found"
		HoneyLanguage.Chinese.code -> "没有找到这个Token"
		HoneyLanguage.Japanese.code -> "このTokenが見つかりません"
		HoneyLanguage.Korean.code -> "이 Token을 찾지 못하였습니다"
		HoneyLanguage.Russian.code -> "Данный Token не найден"
		HoneyLanguage.TraditionalChinese.code -> "沒有找到這個Token"
		else -> ""
	}
	@JvmField
	val searchSubtitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "You have not added any trading pairs yet. Please click on the upper left button to search for and add real-time"
		HoneyLanguage.Chinese.code -> "你还没有添加交易对，点击界面左上角添加市场里的token，可以看实时行情"
		HoneyLanguage.Japanese.code -> "取引ペアを追加していません。インタフェースの左上隅にあるマーケットのTokenをクリックすると、リアルタイム市況が表示されます"
		HoneyLanguage.Korean.code -> "아직 거래 내역이 없습니다"
		HoneyLanguage.Russian.code -> "Вы еще не добавили никаких пар операций, пожалуйста, нажмите на верхнюю левую кнопку, чтобы добавить городской token, также можно осуществлять в режиме реального времени"
		HoneyLanguage.TraditionalChinese.code -> "你还没有添加交易对，点击界面左上角添加市场里的token，可以看实时行情"
		else -> ""
	}
	@JvmField
	val contractTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Empty Contact List"
		HoneyLanguage.Chinese.code -> "通讯录里空空的"
		HoneyLanguage.Japanese.code -> "連絡先リストに何も入力されていません"
		HoneyLanguage.Korean.code -> "전화번호부가 비어 있습니다"
		HoneyLanguage.Russian.code -> "Контакты пусты"
		HoneyLanguage.TraditionalChinese.code -> "通訊簿里沒有記錄"
		else -> ""
	}
	@JvmField
	val contractSubtitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Click the plus sign in the upper-left corner to add a contact address"
		HoneyLanguage.Chinese.code -> "點擊左上角加號，可以添加常用聯繫人的地址"
		HoneyLanguage.Japanese.code -> "左上隅の「+」記号をクリックして、普段使われる連絡先のアドレスを追加することが出来ます"
		HoneyLanguage.Korean.code -> "좌측 상단 플러스 부호를 클릭하면 상용 연락처 주소를 추가할 수 있습니다"
		HoneyLanguage.Russian.code -> "Нажмите на плюс в левом верхнем углу, чтобы добавить адреса часто используемых контактов"
		HoneyLanguage.TraditionalChinese.code -> "點擊左上角加號，可以添加常用聯繫人的地址"
		else -> ""
	}
	@JvmField
	val currency = when (currentLanguage) {
		HoneyLanguage.English.code -> "Click the plus sign in the upper-left corner to add a contact address"
		HoneyLanguage.Chinese.code -> "货币设置"
		HoneyLanguage.Japanese.code -> "貨幣設定"
		HoneyLanguage.Korean.code -> "좌측 상단 플러스 부호를 클릭하면 상용 연락처 주소를 추가할 수 있습니다"
		HoneyLanguage.Russian.code -> "Настройки валюты"
		HoneyLanguage.TraditionalChinese.code -> "貨幣設置"
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
}

object QuotationText {
	
	@JvmField
	val market = when (currentLanguage) {
		HoneyLanguage.English.code -> "Markets"
		HoneyLanguage.Chinese.code -> "市场行情"
		HoneyLanguage.Japanese.code -> "リアルタイム市況"
		HoneyLanguage.Korean.code -> "시장시세"
		HoneyLanguage.Russian.code -> "Конъюнктура рынка"
		HoneyLanguage.TraditionalChinese.code -> "市場行情"
		else -> ""
	}
	@JvmField
	val emptyDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "There isn't description of this token"
		HoneyLanguage.Chinese.code -> "这个Token没有简介"
		HoneyLanguage.Japanese.code -> "このTokenには概略がありません"
		HoneyLanguage.Korean.code -> "이 토큰에 대한 설명이 없습니다."
		HoneyLanguage.Russian.code -> "Данный Token не имеет описания"
		HoneyLanguage.TraditionalChinese.code -> "這個Token沒有簡介"
		else -> ""
	}
	@JvmField
	val management = when (currentLanguage) {
		HoneyLanguage.English.code -> "My Markets"
		HoneyLanguage.Chinese.code -> "自选管理"
		HoneyLanguage.Japanese.code -> "マーケットの自動選択"
		HoneyLanguage.Korean.code -> "셀프관리"
		HoneyLanguage.Russian.code -> "Самостоятельное управление"
		HoneyLanguage.TraditionalChinese.code -> "自選管理"
		else -> ""
	}
	@JvmField
	val tokenDetail = when (currentLanguage) {
		HoneyLanguage.English.code -> "Market Token"
		HoneyLanguage.Chinese.code -> "市场交易对"
		HoneyLanguage.Japanese.code -> "マーケットの取引ペア"
		HoneyLanguage.Korean.code -> "시장 거래 쌍"
		HoneyLanguage.Russian.code -> "Пара рыночных операций"
		HoneyLanguage.TraditionalChinese.code -> "市場交易對"
		else -> ""
	}
	@JvmField
	val search = when (currentLanguage) {
		HoneyLanguage.English.code -> "Search pairs"
		HoneyLanguage.Chinese.code -> "搜索"
		HoneyLanguage.Japanese.code -> "検索"
		HoneyLanguage.Korean.code -> "검색"
		HoneyLanguage.Russian.code -> "Поиск"
		HoneyLanguage.TraditionalChinese.code -> "搜索"
		else -> ""
	}
	@JvmField
	val addToken = when (currentLanguage) {
		HoneyLanguage.English.code -> "Manage"
		HoneyLanguage.Chinese.code -> "管理"
		HoneyLanguage.Japanese.code -> "管理"
		HoneyLanguage.Korean.code -> "검색"
		HoneyLanguage.Russian.code -> "Управление"
		HoneyLanguage.TraditionalChinese.code -> "管理"
		else -> ""
	}
	@JvmField
	val alarm = when (currentLanguage) {
		HoneyLanguage.English.code -> "Alerts"
		HoneyLanguage.Chinese.code -> "价格提醒"
		HoneyLanguage.Japanese.code -> "価格のアドバイス"
		HoneyLanguage.Korean.code -> "가격알람"
		HoneyLanguage.Russian.code -> "Подсказки цены"
		HoneyLanguage.TraditionalChinese.code -> "價格提醒"
		else -> ""
	}
	@JvmField
	val currentPrice = when (currentLanguage) {
		HoneyLanguage.English.code -> "Current Price"
		HoneyLanguage.Chinese.code -> "当前价格"
		HoneyLanguage.Japanese.code -> "現在の価格"
		HoneyLanguage.Korean.code -> "현재가격"
		HoneyLanguage.Russian.code -> "Текущая цена"
		HoneyLanguage.TraditionalChinese.code -> "時價"
		else -> ""
	}
	@JvmField
	val priceHistory = when (currentLanguage) {
		HoneyLanguage.English.code -> "Price History"
		HoneyLanguage.Chinese.code -> "价格历史"
		HoneyLanguage.Japanese.code -> "価格履歴"
		HoneyLanguage.Korean.code -> "가격역사"
		HoneyLanguage.Russian.code -> "История цены"
		HoneyLanguage.TraditionalChinese.code -> "價格歷史"
		else -> ""
	}
	@JvmField
	val tokenDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "Token Description"
		HoneyLanguage.Chinese.code -> "Token 简介"
		HoneyLanguage.Japanese.code -> "Token概略"
		HoneyLanguage.Korean.code -> "Token 소개"
		HoneyLanguage.Russian.code -> "Описание Token"
		HoneyLanguage.TraditionalChinese.code -> "Token 簡介"
		else -> ""
	}
	@JvmField
	val tokenInformation = when (currentLanguage) {
		HoneyLanguage.English.code -> "Token Information"
		HoneyLanguage.Chinese.code -> "Token 信息"
		HoneyLanguage.Japanese.code -> "Token情報"
		HoneyLanguage.Korean.code -> "Token 정보"
		HoneyLanguage.Russian.code -> "Информация Token"
		HoneyLanguage.TraditionalChinese.code -> "Token 信息"
		else -> ""
	}
	@JvmField
	val tokenInfoLink = when (currentLanguage) {
		HoneyLanguage.English.code -> "Token Link"
		HoneyLanguage.Chinese.code -> "Token 链接"
		HoneyLanguage.Japanese.code -> "Tokenリンク"
		HoneyLanguage.Korean.code -> "토큰 링크"
		HoneyLanguage.Russian.code -> "Ссылка Token"
		HoneyLanguage.TraditionalChinese.code -> "Token 鏈接"
		else -> ""
	}
	@JvmField
	val socimalMedia = when (currentLanguage) {
		HoneyLanguage.English.code -> "Social Media"
		HoneyLanguage.Chinese.code -> "社交媒体"
		HoneyLanguage.Japanese.code -> "ソーシャルメディア"
		HoneyLanguage.Korean.code -> "소셜 미디어"
		HoneyLanguage.Russian.code -> "Социальные медиа"
		HoneyLanguage.TraditionalChinese.code -> "社交媒體"
		else -> ""
	}
	@JvmField
	val tokenDescriptionPlaceHolder = when (currentLanguage) {
		HoneyLanguage.English.code -> "Token Description Content"
		HoneyLanguage.Chinese.code -> "Token 信息内容"
		HoneyLanguage.Japanese.code -> "Token情報内容"
		HoneyLanguage.Korean.code -> "Token 정보내용"
		HoneyLanguage.Russian.code -> "Содержание информации Token"
		HoneyLanguage.TraditionalChinese.code -> "Token 信息內容"
		else -> ""
	}
	@JvmField
	val highAndLow = when (currentLanguage) {
		HoneyLanguage.English.code -> "HIGH / LOW"
		HoneyLanguage.Chinese.code -> "最高/最低"
		HoneyLanguage.Japanese.code -> "最高/最低"
		HoneyLanguage.Korean.code -> "최고 / 최저"
		HoneyLanguage.Russian.code -> "Макс./мин."
		HoneyLanguage.TraditionalChinese.code -> "最高 / 最低"
		else -> ""
	}
	@JvmField
	val rank = when (currentLanguage) {
		HoneyLanguage.English.code -> "Rank"
		HoneyLanguage.Chinese.code -> "排名"
		HoneyLanguage.Japanese.code -> "ランキング"
		HoneyLanguage.Korean.code -> "순위"
		HoneyLanguage.Russian.code -> "Рейтинг"
		HoneyLanguage.TraditionalChinese.code -> "排名"
		else -> ""
	}
	@JvmField
	val website = when (currentLanguage) {
		HoneyLanguage.English.code -> "Website"
		HoneyLanguage.Chinese.code -> "网站"
		HoneyLanguage.Japanese.code -> "ウェブサイト"
		HoneyLanguage.Korean.code -> "웹 사이트"
		HoneyLanguage.Russian.code -> "Веб-сайт"
		HoneyLanguage.TraditionalChinese.code -> "網站"
		else -> ""
	}
	@JvmField
	val whitePaper = when (currentLanguage) {
		HoneyLanguage.English.code -> "White Paper"
		HoneyLanguage.Chinese.code -> "白皮书"
		HoneyLanguage.Japanese.code -> "ホワイトペーパー"
		HoneyLanguage.Korean.code -> "White Paper"
		HoneyLanguage.Russian.code -> "Белая книга"
		HoneyLanguage.TraditionalChinese.code -> "White Paper"
		else -> ""
	}
	@JvmField
	val startDate = when (currentLanguage) {
		HoneyLanguage.English.code -> "Start Date"
		HoneyLanguage.Chinese.code -> "开始日期"
		HoneyLanguage.Japanese.code -> "開始日"
		HoneyLanguage.Korean.code -> "시작 날짜"
		HoneyLanguage.Russian.code -> "Дата начала"
		HoneyLanguage.TraditionalChinese.code -> "開始日期"
		else -> ""
	}
	@JvmField
	val totalSupply = when (currentLanguage) {
		HoneyLanguage.English.code -> "Total Supply"
		HoneyLanguage.Chinese.code -> "总供应量"
		HoneyLanguage.Japanese.code -> "総提供量"
		HoneyLanguage.Korean.code -> "총 공급"
		HoneyLanguage.Russian.code -> "Совокупное предложение"
		HoneyLanguage.TraditionalChinese.code -> "總供應量"
		else -> ""
	}
	@JvmField
	val marketCap = when (currentLanguage) {
		HoneyLanguage.English.code -> "Market Cap"
		HoneyLanguage.Chinese.code -> "流通市值"
		HoneyLanguage.Japanese.code -> "流通市場価値"
		HoneyLanguage.Korean.code -> "시가 총액"
		HoneyLanguage.Russian.code -> "Ликвидная рыночная капитализация"
		HoneyLanguage.TraditionalChinese.code -> "流通市值"
		else -> ""
	}
	@JvmField
	val addQuotationChartPlaceholderTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "ADD QUOTATION CHART"
		HoneyLanguage.Chinese.code -> "添加你感兴趣的Token行情"
		HoneyLanguage.Japanese.code -> "お客様の注目しているTokenの市況を追加する"
		HoneyLanguage.Korean.code -> "관심있는 Token 따옴표 추가"
		HoneyLanguage.Russian.code -> "Добавьте интересующую Вас конъюнктуру токена"
		HoneyLanguage.TraditionalChinese.code -> "添加你感興趣的Token行情"
		else -> ""
	}
	@JvmField
	val addQuotationChartPlaceholderSubtitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Search and add a real-time\n token pricing chart from\n exchanges."
		HoneyLanguage.Chinese.code -> "从各大市场的交易对中\n搜索并添加你关心的token，\n你可以看到实时价格走势"
		HoneyLanguage.Japanese.code -> "各マーケットの取引の中から\nを検索して、お客様の注目しているTokenを追加します。\nいつでもリアルタイムで価格動向を見ることが出来ます"
		HoneyLanguage.Korean.code -> "주요 시장의 거래 쌍에서 관심있는\n 토큰을 검색하고 추가하면 실시간\n 가격 동향을 볼 수 있습니다."
		HoneyLanguage.Russian.code -> "Поиск и добавление графика цен на \n токены в режиме реального времени, с \n конъюнктурой."
		HoneyLanguage.TraditionalChinese.code -> "從各大市場的交易對中\n搜索並添加你關心的token，\n你可以看到實時價格走勢"
		else -> ""
	}
}

object PincodeText {
	
	
	@JvmField
	val pincode = when (currentLanguage) {
		HoneyLanguage.English.code -> "Pin Code"
		HoneyLanguage.Chinese.code -> "PIN码"
		HoneyLanguage.Japanese.code -> "PINコード"
		HoneyLanguage.Korean.code -> "PIN 코드"
		HoneyLanguage.Russian.code -> "PIN-код"
		HoneyLanguage.TraditionalChinese.code -> "PIN碼"
		else -> ""
	}
	@JvmField
	val repeat = when (currentLanguage) {
		HoneyLanguage.English.code -> "Repeat PIN"
		HoneyLanguage.Chinese.code -> "重复PIN码"
		HoneyLanguage.Japanese.code -> "PINコードが重複しています"
		HoneyLanguage.Korean.code -> "중복PIN코드"
		HoneyLanguage.Russian.code -> "Повторите PIN-код"
		HoneyLanguage.TraditionalChinese.code -> "重複PIN碼"
		else -> ""
	}
	@JvmField
	val description = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please set a 4-digit PIN"
		HoneyLanguage.Chinese.code -> "输入四位密码"
		HoneyLanguage.Japanese.code -> "4桁のパスワードを入力します"
		HoneyLanguage.Korean.code -> "4자리 비밀번호 입력"
		HoneyLanguage.Russian.code -> "Пожалуйста, введите 4-значный пароль"
		HoneyLanguage.TraditionalChinese.code -> "輸入四位密碼密碼"
		else -> ""
	}
	@JvmField
	val countAlert = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please Enter four bit ciphers"
		HoneyLanguage.Chinese.code -> "请输入四位数字"
		HoneyLanguage.Japanese.code -> "4桁の数字を入力して下さい"
		HoneyLanguage.Korean.code -> "4 비트 암호를 입력하십시오."
		HoneyLanguage.Russian.code -> "Пожалуйста, введите 4-значное число"
		HoneyLanguage.TraditionalChinese.code -> "請輸入四位數字"
		else -> ""
	}
	@JvmField
	val verifyAlert = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please repeat the same PIN"
		HoneyLanguage.Chinese.code -> "请再次输入一遍PIN码进行确认"
		HoneyLanguage.Japanese.code -> "再度繰り返しPINコードを入力し確認して下さい"
		HoneyLanguage.Korean.code -> "같은 PIN을 중복하십시오"
		HoneyLanguage.Russian.code -> "Пожалуйста, введите PIN-код еще раз для подтверждения"
		HoneyLanguage.TraditionalChinese.code -> "請再次輸入PIN碼確認"
		else -> ""
	}
	@JvmField
	val turnOnAttention = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please set a PIN"
		HoneyLanguage.Chinese.code -> "请先设置PIN码"
		HoneyLanguage.Japanese.code -> "PINコードを先に設定して下さい"
		HoneyLanguage.Korean.code -> "PIN을 설정하십시오"
		HoneyLanguage.Russian.code -> "Пожалуйста, установите PIN-код"
		HoneyLanguage.TraditionalChinese.code -> "請先設置PIN碼"
		else -> ""
	}
	@JvmField
	val show = when (currentLanguage) {
		HoneyLanguage.English.code -> "Show PIN"
		HoneyLanguage.Chinese.code -> "显示PIN码"
		HoneyLanguage.Japanese.code -> "PINコードを表示します"
		HoneyLanguage.Korean.code -> "PIN코드 표시"
		HoneyLanguage.Russian.code -> "Показать PIN-код"
		HoneyLanguage.TraditionalChinese.code -> "顯示PIN碼"
		else -> ""
	}
	@JvmField
	val enterPincode = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter PIN"
		HoneyLanguage.Chinese.code -> "输入PIN码"
		HoneyLanguage.Japanese.code -> "PINコードを入力します"
		HoneyLanguage.Korean.code -> "Enter Passcode"
		HoneyLanguage.Russian.code -> "Введите PIN-код"
		HoneyLanguage.TraditionalChinese.code -> "輸入PIN碼"
		else -> ""
	}
	@JvmField
	val enterPincodeDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "Set the PIN to protect privacy. Once you open GoldStone, you need to enter PIN to see your wallet "
		HoneyLanguage.Chinese.code -> "设置锁屏密码保护隐私，一旦开启锁屏密码，每次打开 GoldStone 时需要输入锁屏密码才能查看钱包"
		HoneyLanguage.Japanese.code -> "ロック画面のパスワードを設定するとプライバシーを保護します。ロック画面のパスワードを設定すると、GoldStoneの画面を開くたびにロック画面のパスワードを入力することで、ウォレットを確認することができます"
		HoneyLanguage.Korean.code -> "잠금 화면 암호를 설정하여 개인 정보를 보호하십시오. 화면 잠금 암호가 켜지면 GoldStone을 열 때마다 지갑 화면을 보려면 잠금 화면 암호를 입력해야합니다."
		HoneyLanguage.Russian.code -> "Установите защиту паролем экрана блокировки для защиты конфиденциальности. Как только Вы запустите пароль экрана блокировки, Вам нужно ввести пароль экрана блокировки, чтобы просмотреть свой кошелек"
		HoneyLanguage.TraditionalChinese.code -> "設置鎖屏密碼保護隱私，一旦開啟鎖屏密碼，每次打開GoldStone時需要輸入鎖屏密碼才能查看錢包"
		else -> ""
	}
}

object PrepareTransferText {
	
	@JvmField
	val sendAmountSuffix = when (currentLanguage) {
		HoneyLanguage.English.code -> "Amount"
		HoneyLanguage.Chinese.code -> "转出数量"
		HoneyLanguage.Japanese.code -> "転送数量"
		HoneyLanguage.Korean.code -> "수량 전송"
		HoneyLanguage.Russian.code -> "сумму"
		HoneyLanguage.TraditionalChinese.code -> "轉出數量"
		else -> ""
	}
	@JvmField
	val memoInformation = when (currentLanguage) {
		HoneyLanguage.English.code -> "Memo Information"
		HoneyLanguage.Chinese.code -> "备注信息"
		HoneyLanguage.Japanese.code -> "備考情報"
		HoneyLanguage.Korean.code -> "비고정보"
		HoneyLanguage.Russian.code -> "Информация о примечании"
		HoneyLanguage.TraditionalChinese.code -> "備註信息"
		else -> ""
	}
	@JvmField
	val memo = when (currentLanguage) {
		HoneyLanguage.English.code -> "Memo"
		HoneyLanguage.Chinese.code -> "备注"
		HoneyLanguage.Japanese.code -> "備考"
		HoneyLanguage.Korean.code -> "비고"
		HoneyLanguage.Russian.code -> "Примечание"
		HoneyLanguage.TraditionalChinese.code -> "備註"
		else -> ""
	}
	@JvmField
	val customChangeAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "Custom Change Address"
		HoneyLanguage.Chinese.code -> "Custom Change Address"
		HoneyLanguage.Japanese.code -> "Custom Change Address"
		HoneyLanguage.Korean.code -> "Custom Change Address"
		HoneyLanguage.Russian.code -> "Custom Change Address"
		HoneyLanguage.TraditionalChinese.code -> "Custom Change Address"
		else -> ""
	}
	@JvmField
	val changeAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "Change Address"
		HoneyLanguage.Chinese.code -> "Change Address"
		HoneyLanguage.Japanese.code -> "Change Address"
		HoneyLanguage.Korean.code -> "Change Address"
		HoneyLanguage.Russian.code -> "Change Address"
		HoneyLanguage.TraditionalChinese.code -> "Change Address"
		else -> ""
	}
	@JvmField
	val addAMemo = when (currentLanguage) {
		HoneyLanguage.English.code -> "Add a Memo"
		HoneyLanguage.Chinese.code -> "添加备注"
		HoneyLanguage.Japanese.code -> "備考を追加する"
		HoneyLanguage.Korean.code -> "비고 추가"
		HoneyLanguage.Russian.code -> "Добавить примечание"
		HoneyLanguage.TraditionalChinese.code -> "添加備註"
		else -> ""
	}
	@JvmField
	val price = when (currentLanguage) {
		HoneyLanguage.English.code -> "UNIT PRICE"
		HoneyLanguage.Chinese.code -> "单价"
		HoneyLanguage.Japanese.code -> "単価"
		HoneyLanguage.Korean.code -> "단가"
		HoneyLanguage.Russian.code -> "Цена за единицу"
		HoneyLanguage.TraditionalChinese.code -> "單價"
		else -> ""
	}
	@JvmField
	val currentPrice = when (currentLanguage) {
		HoneyLanguage.English.code -> "Current Price"
		HoneyLanguage.Chinese.code -> "当前价"
		HoneyLanguage.Japanese.code -> "現在の価格"
		HoneyLanguage.Korean.code -> "현재가격"
		HoneyLanguage.Russian.code -> "Текущая цена"
		HoneyLanguage.TraditionalChinese.code -> "時價"
		else -> ""
	}
	@JvmField
	val accountInfo = when (currentLanguage) {
		HoneyLanguage.English.code -> "Account Information"
		HoneyLanguage.Chinese.code -> "账户信息"
		HoneyLanguage.Japanese.code -> "アカウント情報"
		HoneyLanguage.Korean.code -> "계정 정보"
		HoneyLanguage.Russian.code -> "Информация о счете"
		HoneyLanguage.TraditionalChinese.code -> "帳戶信息"
		else -> ""
	}
	@JvmField
	val willSpending = when (currentLanguage) {
		HoneyLanguage.English.code -> "WILL SPEND"
		HoneyLanguage.Chinese.code -> "预计花费"
		HoneyLanguage.Japanese.code -> "見積価格"
		HoneyLanguage.Korean.code -> "예상 소비"
		HoneyLanguage.Russian.code -> "Предварительные расходы"
		HoneyLanguage.TraditionalChinese.code -> "預計花費"
		else -> ""
	}
	@JvmField
	val send = when (currentLanguage) {
		HoneyLanguage.English.code -> "SEND TO"
		HoneyLanguage.Chinese.code -> "发送至"
		HoneyLanguage.Japanese.code -> "発送先"
		HoneyLanguage.Korean.code -> "수신자"
		HoneyLanguage.Russian.code -> "Отправить до"
		HoneyLanguage.TraditionalChinese.code -> "發送至"
		else -> ""
	}
	@JvmField
	val from = when (currentLanguage) {
		HoneyLanguage.English.code -> "FROM"
		HoneyLanguage.Chinese.code -> "发送者"
		HoneyLanguage.Japanese.code -> "発送者"
		HoneyLanguage.Korean.code -> "발신자"
		HoneyLanguage.Russian.code -> "Отправитель"
		HoneyLanguage.TraditionalChinese.code -> "發送者"
		else -> ""
	}
	@JvmField
	val recommend = when (currentLanguage) {
		HoneyLanguage.English.code -> "Recommend"
		HoneyLanguage.Chinese.code -> "推荐"
		HoneyLanguage.Japanese.code -> "お勧め"
		HoneyLanguage.Korean.code -> "추천"
		HoneyLanguage.Russian.code -> "Рекомендуемое"
		HoneyLanguage.TraditionalChinese.code -> "推薦"
		else -> ""
	}
	@JvmField
	val cheap = when (currentLanguage) {
		HoneyLanguage.English.code -> "Cheap"
		HoneyLanguage.Chinese.code -> "便宜"
		HoneyLanguage.Japanese.code -> "安い"
		HoneyLanguage.Korean.code -> "발신자"
		HoneyLanguage.Russian.code -> "Дешево"
		HoneyLanguage.TraditionalChinese.code -> "便宜"
		else -> ""
	}
	@JvmField
	val fast = when (currentLanguage) {
		HoneyLanguage.English.code -> "Fast"
		HoneyLanguage.Chinese.code -> "快速"
		HoneyLanguage.Japanese.code -> "速い"
		HoneyLanguage.Korean.code -> "빠른"
		HoneyLanguage.Russian.code -> "Быстро"
		HoneyLanguage.TraditionalChinese.code -> "快速"
		else -> ""
	}
	@JvmField
	val customize = when (currentLanguage) {
		HoneyLanguage.English.code -> "Customize"
		HoneyLanguage.Chinese.code -> "自定义"
		HoneyLanguage.Japanese.code -> "カスタマイズ"
		HoneyLanguage.Korean.code -> "사용자 정의"
		HoneyLanguage.Russian.code -> "Самоопред."
		HoneyLanguage.TraditionalChinese.code -> "自定義"
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
	val wrongAddressFormat = when (currentLanguage) {
		HoneyLanguage.English.code -> "Incorrect wallet address format"
		HoneyLanguage.Chinese.code -> "钱包地址格式错误"
		HoneyLanguage.Japanese.code -> "ウォレットアドレス形式が間違っています"
		HoneyLanguage.Korean.code -> "지갑주소 포맷 오류"
		HoneyLanguage.Russian.code -> "Неправильный формат адреса кошелька"
		HoneyLanguage.TraditionalChinese.code -> "錢包地址格式有誤"
		else -> ""
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
	val ethERCAndETChint = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter Ethereum, ERC20 or Ethereum Classic address that you want to store"
		HoneyLanguage.Chinese.code -> "Enter Ethereum, ERC20 or Ethereum Classic address that you want to store"
		HoneyLanguage.Japanese.code -> "Enter Ethereum, ERC20 or Ethereum Classic address that you want to store"
		HoneyLanguage.Korean.code -> "Enter Ethereum, ERC20 or Ethereum Classic address that you want to store"
		HoneyLanguage.Russian.code -> "Enter Ethereum, ERC20 or Ethereum Classic address that you want to store"
		HoneyLanguage.TraditionalChinese.code -> "Enter Ethereum, ERC20 or Ethereum Classic address that you want to store"
		else -> ""
	}
	val btcMainnetAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter Bitcoin Mainnet address that you want to store"
		HoneyLanguage.Chinese.code -> "Enter Bitcoin Mainnet address that you want to store"
		HoneyLanguage.Japanese.code -> "Enter Bitcoin Mainnet address that you want to store"
		HoneyLanguage.Korean.code -> "Enter Bitcoin Mainnet address that you want to store"
		HoneyLanguage.Russian.code -> "Enter Bitcoin Mainnet address that you want to store"
		HoneyLanguage.TraditionalChinese.code -> "Enter Bitcoin Mainnet address that you want to store"
		else -> ""
	}
	val btcTestnetAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter Bitcoin Testnet address that you want to store"
		HoneyLanguage.Chinese.code -> "Enter Bitcoin Testnet address that you want to store"
		HoneyLanguage.Japanese.code -> "Enter Bitcoin Testnet address that you want to store"
		HoneyLanguage.Korean.code -> "Enter Bitcoin Testnet address that you want to store"
		HoneyLanguage.Russian.code -> "Enter Bitcoin Testnet address that you want to store"
		HoneyLanguage.TraditionalChinese.code -> "Enter Bitcoin Testnet address that you want to store"
		else -> ""
	}
}

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
		HoneyLanguage.Chinese.code -> "Main (Gold Stone)"
		HoneyLanguage.Japanese.code -> "Main (Gold Stone)"
		HoneyLanguage.Korean.code -> "Main (Gold Stone)"
		HoneyLanguage.Russian.code -> "Main (Gold Stone)"
		HoneyLanguage.TraditionalChinese.code -> "Main (Gold Stone)"
		else -> ""
	}
	@JvmField
	val infuraMain = when (currentLanguage) {
		HoneyLanguage.English.code -> "Main (Infura)"
		HoneyLanguage.Chinese.code -> "Main (Infura)"
		HoneyLanguage.Japanese.code -> "Main (Infura)"
		HoneyLanguage.Korean.code -> "Main (Infura)"
		HoneyLanguage.Russian.code -> "Main (Infura)"
		HoneyLanguage.TraditionalChinese.code -> "Main (Infura)"
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
		HoneyLanguage.Chinese.code -> "Kovan (GoldStone)"
		HoneyLanguage.Japanese.code -> "Kovan (GoldStone)"
		HoneyLanguage.Korean.code -> "Kovan (GoldStone)"
		HoneyLanguage.Russian.code -> "Kovan (GoldStone)"
		HoneyLanguage.TraditionalChinese.code -> "Kovan (GoldStone)"
		else -> ""
	}
	@JvmField
	val rinkeby = when (currentLanguage) {
		HoneyLanguage.English.code -> "Rinkeby (GoldStone)"
		HoneyLanguage.Chinese.code -> "Rinkeby (GoldStone)"
		HoneyLanguage.Japanese.code -> "Rinkeby (GoldStone)"
		HoneyLanguage.Korean.code -> "Rinkeby (GoldStone)"
		HoneyLanguage.Russian.code -> "Rinkeby (GoldStone)"
		HoneyLanguage.TraditionalChinese.code -> "Rinkeby (GoldStone)"
		else -> ""
	}
	@JvmField
	val etcMorden = when (currentLanguage) {
		HoneyLanguage.English.code -> "Morden (GasTracker)"
		HoneyLanguage.Chinese.code -> "Morden (GasTracker)"
		HoneyLanguage.Japanese.code -> "Morden (GasTracker)"
		HoneyLanguage.Korean.code -> "Morden (GasTracker)"
		HoneyLanguage.Russian.code -> "Morden (GasTracker)"
		HoneyLanguage.TraditionalChinese.code -> "Morden (GasTracker)"
		else -> ""
	}
	@JvmField
	val goldStoneEtcMain = when (currentLanguage) {
		HoneyLanguage.English.code -> "ETC Mainnet (GoldStone)"
		HoneyLanguage.Chinese.code -> "ETC Mainnet (GoldStone)"
		HoneyLanguage.Japanese.code -> "ETC Mainnet (GoldStone)"
		HoneyLanguage.Korean.code -> "ETC Mainnet (GoldStone)"
		HoneyLanguage.Russian.code -> "ETC Mainnet (GoldStone)"
		HoneyLanguage.TraditionalChinese.code -> "ETC Mainnet (GoldStone)"
		else -> ""
	}
	@JvmField
	val goldStoneEtcMorderTest = when (currentLanguage) {
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
		HoneyLanguage.English.code -> "ETC Mainnet (GasTracker)"
		HoneyLanguage.Chinese.code -> "ETC Mainnet (GasTracker)"
		HoneyLanguage.Japanese.code -> "ETC Mainnet (GasTracker)"
		HoneyLanguage.Korean.code -> "ETC Mainnet (GasTracker)"
		HoneyLanguage.Russian.code -> "ETC Mainnet (GasTracker)"
		HoneyLanguage.TraditionalChinese.code -> "ETC Mainnet (GasTracker)"
		else -> ""
	}
	@JvmField
	val btcMain = when (currentLanguage) {
		HoneyLanguage.English.code -> "BTC Mainnet (GoldStone)"
		HoneyLanguage.Chinese.code -> "BTC Mainnet (GoldStone)"
		HoneyLanguage.Japanese.code -> "BTC Mainnet (GoldStone)"
		HoneyLanguage.Korean.code -> "BTC Mainnet (GoldStone)"
		HoneyLanguage.Russian.code -> "BTC Mainnet (GoldStone)"
		HoneyLanguage.TraditionalChinese.code -> "BTC Mainnet (GoldStone)"
		else -> ""
	}
	@JvmField
	val btcTest = when (currentLanguage) {
		HoneyLanguage.English.code -> "BTC Testnet (GoldStone)"
		HoneyLanguage.Chinese.code -> "BTC Testnet (GoldStone)"
		HoneyLanguage.Japanese.code -> "BTC Testnet (GoldStone)"
		HoneyLanguage.Korean.code -> "BTC Testnet (GoldStone)"
		HoneyLanguage.Russian.code -> "BTC Testnet (GoldStone)"
		HoneyLanguage.TraditionalChinese.code -> "BTC Testnet (GoldStone)"
		else -> ""
	}
}

object LoadingText {
	
	
	@JvmField
	val getTokenInformation = when (currentLanguage) {
		HoneyLanguage.English.code -> "Getting Token information from Ethereum..."
		HoneyLanguage.Chinese.code -> "正在从以太坊获取Token信息，马上就好"
		HoneyLanguage.Japanese.code -> "現在EthereumからToken情報を取得しています。すぐに終わります"
		HoneyLanguage.Korean.code -> "Ethereum에서 토큰 정보 얻기..."
		HoneyLanguage.Russian.code -> "Получение информации о токенах от Ethereum..."
		HoneyLanguage.TraditionalChinese.code -> "正在從以太坊獲取Token信息，馬上就好"
		else -> ""
	}
	@JvmField
	val calculateGas = when (currentLanguage) {
		HoneyLanguage.English.code -> "Estimating gas costs..."
		HoneyLanguage.Chinese.code -> "正在估算燃气费用，马上就好"
		HoneyLanguage.Japanese.code -> "現在ガス代金を計算しています。すぐに終わります"
		HoneyLanguage.Korean.code -> "바로 가스 비용 견적..."
		HoneyLanguage.Russian.code -> "Выполняется оценка стоимости газа..."
		HoneyLanguage.TraditionalChinese.code -> "正在估算燃氣費用，馬上就好"
		else -> ""
	}
	@JvmField
	val calculating = when (currentLanguage) {
		HoneyLanguage.English.code -> "Calculating..."
		HoneyLanguage.Chinese.code -> "正在计算..."
		HoneyLanguage.Japanese.code -> "現在計算中..."
		HoneyLanguage.Korean.code -> "계산 중..."
		HoneyLanguage.Russian.code -> "Расчет..."
		HoneyLanguage.TraditionalChinese.code -> "正在計算..."
		else -> ""
	}
	@JvmField
	val searchingQuotation = when (currentLanguage) {
		HoneyLanguage.English.code -> "Searching token information..."
		HoneyLanguage.Chinese.code -> "正在搜索Token信息..."
		HoneyLanguage.Japanese.code -> "現在Token情報の検索中..."
		HoneyLanguage.Korean.code -> "Token시세 검색..."
		HoneyLanguage.Russian.code -> "Поиск информации о токене..."
		HoneyLanguage.TraditionalChinese.code -> "正在檢索Token信息..."
		else -> ""
	}
	@JvmField
	val searchingToken = when (currentLanguage) {
		HoneyLanguage.English.code -> "Searching token information..."
		HoneyLanguage.Chinese.code -> "正在搜索Token信息..."
		HoneyLanguage.Japanese.code -> "現在Token情報の検索中..."
		HoneyLanguage.Korean.code -> "Token시세 검색..."
		HoneyLanguage.Russian.code -> "Поиск информации о токене..."
		HoneyLanguage.TraditionalChinese.code -> "正在檢索Token信息..."
		else -> ""
	}
	@JvmField
	val transactionData = when (currentLanguage) {
		HoneyLanguage.English.code -> "Loading transaction data..."
		HoneyLanguage.Chinese.code -> "正在加载转账记录..."
		HoneyLanguage.Japanese.code -> "現在振込記録の読み込み中..."
		HoneyLanguage.Korean.code -> "이전 기록 로드중..."
		HoneyLanguage.Russian.code -> "Загрузка данных о переводе..."
		HoneyLanguage.TraditionalChinese.code -> "正在加載轉賬記錄..."
		else -> ""
	}
	@JvmField
	val tokenData = when (currentLanguage) {
		HoneyLanguage.English.code -> "Loading token data..."
		HoneyLanguage.Chinese.code -> "正在加载token信息..."
		HoneyLanguage.Japanese.code -> "現在Token情報の読み込み中..."
		HoneyLanguage.Korean.code -> "Token정보 로드중..."
		HoneyLanguage.Russian.code -> "Загрузка информации о токене..."
		HoneyLanguage.TraditionalChinese.code -> "正在加載token信息..."
		else -> ""
	}
	@JvmField
	val notificationData = when (currentLanguage) {
		HoneyLanguage.English.code -> "Loading notifications..."
		HoneyLanguage.Chinese.code -> "正在加载通知信息..."
		HoneyLanguage.Japanese.code -> "現在アラームメッセージの読み込み中..."
		HoneyLanguage.Korean.code -> "알림 정보 로드중..."
		HoneyLanguage.Russian.code -> "Загрузка информации о уведомлениях..."
		HoneyLanguage.TraditionalChinese.code -> "正在加載通知信息..."
		else -> ""
	}
	@JvmField
	val loadingDataFromChain = when (currentLanguage) {
		HoneyLanguage.English.code -> "Loading data from chain ..."
		HoneyLanguage.Chinese.code -> "正在从链获取信息..."
		HoneyLanguage.Japanese.code -> "現在チェーンから情報の取得中..."
		HoneyLanguage.Korean.code -> "체인에서 정보를 얻는 중 ..."
		HoneyLanguage.Russian.code -> "Загрузка данных из цепи ..."
		HoneyLanguage.TraditionalChinese.code -> "正在從鏈獲取信息..."
		else -> ""
	}
}

object QRText {
	
	@JvmField
	val savedAttention = when (currentLanguage) {
		HoneyLanguage.English.code -> "QR code has been saved to album"
		HoneyLanguage.Chinese.code -> "二维码已保存至相册"
		HoneyLanguage.Japanese.code -> "QRコードをアルバムに保存しました"
		HoneyLanguage.Korean.code -> "QR코드를 앨범에 저장 완료"
		HoneyLanguage.Russian.code -> "QR-код был сохранен в альбом"
		HoneyLanguage.TraditionalChinese.code -> "二維碼已保存至手機相冊"
		else -> ""
	}
	@JvmField
	val shareQRTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "SHARE QR IMAGE"
		HoneyLanguage.Chinese.code -> "分享二维码"
		HoneyLanguage.Japanese.code -> "QRコードをシェアする"
		HoneyLanguage.Korean.code -> "QR코드 공유"
		HoneyLanguage.Russian.code -> "Поделиться QR-кодом"
		HoneyLanguage.TraditionalChinese.code -> "分享二維碼"
		else -> ""
	}
	@JvmField
	val screenText = when (currentLanguage) {
		HoneyLanguage.English.code -> "Scan GoldStone QR Code"
		HoneyLanguage.Chinese.code -> "扫描GoldStone的二维码"
		HoneyLanguage.Japanese.code -> "GoldStoneのQRコードをスキャンします"
		HoneyLanguage.Korean.code -> "GoldStone 의 QR 코드 스캔"
		HoneyLanguage.Russian.code -> "Сканирование QR-кода в GoldStone"
		HoneyLanguage.TraditionalChinese.code -> "掃描GoldStone的二維碼"
		else -> ""
	}
	@JvmField
	val unvalidQRCodeAlert = when (currentLanguage) {
		HoneyLanguage.English.code -> "Invalid QR code"
		HoneyLanguage.Chinese.code -> "未识别到有效的二维码图片"
		HoneyLanguage.Japanese.code -> "有効的なQRコード画像を識別できません"
		HoneyLanguage.Korean.code -> "미식별 유효 QR코드 이미지"
		HoneyLanguage.Russian.code -> "Неверный QR-код"
		HoneyLanguage.TraditionalChinese.code -> "未識別到有效的二維碼圖片"
		else -> ""
	}
	@JvmField
	val unvalidContract = when (currentLanguage) {
		HoneyLanguage.English.code -> "Inconsistent currency. The QR code scanned is not that of the current token, please change the transfer token, or change the scanned QR code."
		HoneyLanguage.Chinese.code -> "货币不一致。您所扫描的不是当前Token的二维码，请您更换token进行转账，或者更换扫描的二维码。"
		HoneyLanguage.Japanese.code -> "貨幣が一致していません。現在のTokenのQRコードがスキャンされていません。Tokenを変更して振込するか、スキャンするQRコードを変更して下さい。"
		HoneyLanguage.Korean.code -> "화폐가 불일치합니다. 귀하께서 스캐스 한 것은 현재 Token의 QR코드가 아닙니다, 귀하께서 token을 교체하여 이체하거나, 스캐너용 QR코드를 교체하십시오. "
		HoneyLanguage.Russian.code -> "Несоответствующая валюта. Отсканированное содержание не является QR-кодом текущего токена, пожалуйста, поменяйте токен для перевода или измените отсканированный QR-код."
		HoneyLanguage.TraditionalChinese.code -> "貨幣不一致。您所掃描的不是當前Token的二維碼，請您更換token進行轉賬，或者更換掃描的二維碼。"
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
		HoneyLanguage.English.code -> "What is gas?"
		HoneyLanguage.Chinese.code -> "什么是GAS？"
		HoneyLanguage.Japanese.code -> "GASとはなんですか？"
		HoneyLanguage.Korean.code -> "니모닉이란 가스요금？"
		HoneyLanguage.Russian.code -> "Что такое GAS?"
		HoneyLanguage.TraditionalChinese.code -> "什麼是GAS?"
		else -> ""
	}
	@JvmField
	val whatIsKeystore = when (currentLanguage) {
		HoneyLanguage.English.code -> "What is a keystore?"
		HoneyLanguage.Chinese.code -> "什么是 keystore?"
		HoneyLanguage.Japanese.code -> "Keystoreとはなんですか？"
		HoneyLanguage.Korean.code -> "keystore란？"
		HoneyLanguage.Russian.code -> "Что такое keystore?"
		HoneyLanguage.TraditionalChinese.code -> "什麼是 keystore?"
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

object ImportMenubar {
	
	
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
		HoneyLanguage.Korean.code -> "개인키(Private Key)"
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
		HoneyLanguage.Russian.code -> "Кошелек только для просмотра"
		HoneyLanguage.TraditionalChinese.code -> "觀察錢包"
		else -> ""
	}
}

object SplashText {
	@JvmField
	val slogan = when (currentLanguage) {
		HoneyLanguage.English.code -> "The safest, most useful wallet in the world"
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

object DateAndTimeText {
	@JvmField
	val hour = when (currentLanguage) {
		HoneyLanguage.English.code -> "hour"
		HoneyLanguage.Chinese.code -> "小时"
		HoneyLanguage.Japanese.code -> "時間"
		HoneyLanguage.Korean.code -> "시간"
		HoneyLanguage.Russian.code -> " ЧАС"
		HoneyLanguage.TraditionalChinese.code -> "小時"
		else -> ""
	}
	@JvmField
	val day = when (currentLanguage) {
		HoneyLanguage.English.code -> "day"
		HoneyLanguage.Chinese.code -> "日"
		HoneyLanguage.Japanese.code -> "日"
		HoneyLanguage.Korean.code -> "주간"
		HoneyLanguage.Russian.code -> "ДЕНЬ"
		HoneyLanguage.TraditionalChinese.code -> "日"
		else -> ""
	}
	@JvmField
	val week = when (currentLanguage) {
		HoneyLanguage.English.code -> "week"
		HoneyLanguage.Chinese.code -> "周"
		HoneyLanguage.Japanese.code -> "週間"
		HoneyLanguage.Korean.code -> "주"
		HoneyLanguage.Russian.code -> "ЧЖОУ"
		HoneyLanguage.TraditionalChinese.code -> "周"
		else -> ""
	}
	@JvmField
	val month = when (currentLanguage) {
		HoneyLanguage.English.code -> "month"
		HoneyLanguage.Chinese.code -> "月"
		HoneyLanguage.Japanese.code -> "ヶ月"
		HoneyLanguage.Korean.code -> "달"
		HoneyLanguage.Russian.code -> "МЕСЯЦ"
		HoneyLanguage.TraditionalChinese.code -> "月"
		else -> ""
	}
	@JvmField
	val second = when (currentLanguage) {
		HoneyLanguage.English.code -> "second"
		HoneyLanguage.Chinese.code -> "秒"
		HoneyLanguage.Japanese.code -> "秒"
		HoneyLanguage.Korean.code -> "초"
		HoneyLanguage.Russian.code -> " секунды"
		HoneyLanguage.TraditionalChinese.code -> "秒"
		else -> ""
	}
	@JvmField
	val minute = when (currentLanguage) {
		HoneyLanguage.English.code -> "minute"
		HoneyLanguage.Chinese.code -> "分钟"
		HoneyLanguage.Japanese.code -> "分"
		HoneyLanguage.Korean.code -> "분"
		HoneyLanguage.Russian.code -> " минуты"
		HoneyLanguage.TraditionalChinese.code -> "分鐘"
		else -> ""
	}
	@JvmField
	val hours = when (currentLanguage) {
		HoneyLanguage.English.code -> "24 Hours"
		HoneyLanguage.Chinese.code -> "24 小时"
		HoneyLanguage.Japanese.code -> "24 時間"
		HoneyLanguage.Korean.code -> "24 시간"
		HoneyLanguage.Russian.code -> "24 часа "
		HoneyLanguage.TraditionalChinese.code -> "24 小時"
		else -> ""
	}
	@JvmField
	val total = when (currentLanguage) {
		HoneyLanguage.English.code -> "Total"
		HoneyLanguage.Chinese.code -> "全部"
		HoneyLanguage.Japanese.code -> "すべて"
		HoneyLanguage.Korean.code -> "모두"
		HoneyLanguage.Russian.code -> "полный"
		HoneyLanguage.TraditionalChinese.code -> "全部"
		else -> ""
	}
	@JvmField
	val ago = when (currentLanguage) {
		HoneyLanguage.English.code -> "ago"
		HoneyLanguage.Chinese.code -> "前"
		HoneyLanguage.Japanese.code -> "前"
		HoneyLanguage.Korean.code -> "전에"
		HoneyLanguage.Russian.code -> " назад"
		HoneyLanguage.TraditionalChinese.code -> "前"
		else -> ""
	}
	
	fun getDateText(): HoneyDateUtil.DataText {
		return HoneyDateUtil.DataText(
			hour,
			week,
			day,
			hour,
			minute,
			second,
			ago,
			HoneyLanguage.getPluralLanguageCode().any { it == currentLanguage }
		)
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
	val Bear = when (currentLanguage) {
		HoneyLanguage.English.code -> "Bear"
		HoneyLanguage.Chinese.code -> "熊老大"
		HoneyLanguage.Japanese.code -> "くま"
		HoneyLanguage.Korean.code -> "곰"
		HoneyLanguage.Russian.code -> "медведь"
		HoneyLanguage.TraditionalChinese.code -> "熊老大"
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
