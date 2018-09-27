package io.goldstone.blockchain.common.language

/**
 * @date 2018/8/8 2:09 AM
 * @author KaySaith
 */

object CreateWalletText {
	@JvmField
	val attention = when (currentLanguage) {
		HoneyLanguage.English.code -> "Password strength is critical for the security of your wallet. We will be unable to recover your password, so make sure save it yourself, and in a very secure way!"
		HoneyLanguage.Chinese.code -> "越强的密码越安全，请尽量设置更复杂的密码。我们不会为您保管密码，请您谨慎保管。"
		HoneyLanguage.Japanese.code -> "強力なパスワードであればあるほど安全です。弊社はお客様に代わってバスワードを保管することはありませんので、気をつけて保管するようにして下さい。"
		HoneyLanguage.Korean.code -> "복잡한 비밀번호일 수록 더 안전합니다, 최대한 복잡한 비밀번호를 설정하십시오. 당사는 귀하의 비밀번호를 보관하지 않으니 귀하께서 잘 보관하십시오. "
		HoneyLanguage.Russian.code -> "Чем сильнее пароль, тем он безопаснее, пожалуйста настройте более сложный пароль. Мы не будем хранить Ваш пароль, пожалуйста, храните его в безопасности."
		HoneyLanguage.TraditionalChinese.code -> "越強的密碼越安全，請盡量設置更複雜的密碼。我們不會為您保管密碼，請您謹慎保管。"
		else -> ""
	}
	@JvmField
	val create = when (currentLanguage) {
		HoneyLanguage.English.code -> "Start a New Wallet"
		HoneyLanguage.Chinese.code -> "创建钱包"
		HoneyLanguage.Japanese.code -> "ウォレットを新規作成"
		HoneyLanguage.Korean.code -> "지갑 만들기"
		HoneyLanguage.Russian.code -> "Новый кошелек"
		HoneyLanguage.TraditionalChinese.code -> "產生錢包"
		else -> ""
	}
	@JvmField
	val passwordRules = when (currentLanguage) {
		HoneyLanguage.English.code -> "A secure passwords must contain both upper and lower case letters, at least one number, and a minimum of 8 characters"
		HoneyLanguage.Chinese.code -> "请设置更安全的密码，同时包含英文大小写和数字，不少于8位"
		HoneyLanguage.Japanese.code -> "アルファベット大文字・小文字・数字を組み合わせた形で、8桁以上のより安全なパスワードを設定して下さい"
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
		HoneyLanguage.Russian.code -> "Консультация"
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
	@JvmField
	val agreementPostString = when (currentLanguage) {
		HoneyLanguage.English.code -> ""
		HoneyLanguage.Chinese.code -> ""
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
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
	@JvmField
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
	val safetyLevelNormal = when (currentLanguage) {
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