@file:Suppress("DEPRECATION")

package io.goldstone.blockchain.common.value

import io.goldstone.blockchain.GoldStoneApp.Companion.currentLanguage

/**
 * @date 21/03/2018 7:34 PM
 * @author KaySaith
 */

object CreateWalletText {

	@JvmField
	val attention = when (currentLanguage) {
		HoneyLanguage.English.code -> "Password strength is critical for the security of your wallet. We will be unable to recover your password, so make sure save it yourself, and in a very secure way!"
		HoneyLanguage.Chinese.code -> "越强的密码越安全，请尽量设置更复杂的密码。我们不会为您保管密码，请您谨慎保管。"
		HoneyLanguage.Japanese.code -> "パスワードが複雑になればなるほど、財布はより安全になります。 あなたのパスワードを保持しません、慎重にバックアップしてください。"
		HoneyLanguage.Korean.code -> "암호가 복잡할수록 지갑은 안전합니다"
		HoneyLanguage.Russian.code -> "Чем сложнее пароль, тем более безопасен кошелек"
		HoneyLanguage.TraditionalChinese.code -> "越強的密碼越安全，請盡量設置更複雜的密碼。我們不會為您保管密碼，請您謹慎保管。"
		else -> ""
	}

	@JvmField
	val create = when (currentLanguage) {
		HoneyLanguage.English.code -> "Start a New Wallet"
		HoneyLanguage.Chinese.code -> "创建钱包"
		HoneyLanguage.Japanese.code -> "ウォレット作成"
		HoneyLanguage.Korean.code -> "지갑 만들기"
		HoneyLanguage.Russian.code -> "Создать кошелек"
		HoneyLanguage.TraditionalChinese.code -> "產生錢包"
		else -> ""
	}

	@JvmField
	val passwordRules = when (currentLanguage) {
		HoneyLanguage.English.code -> "Password strength is critical to guard your wallet We can’t recover the password, please back up cautiously"
		HoneyLanguage.Chinese.code -> "越强的密码越安全，请尽量设置更复杂的密码。我们不会为您保管密码，请您谨慎保管。"
		HoneyLanguage.Japanese.code -> "パスワードが複雑になればなるほど、財布はより安全になります。 あなたのパスワードを保持しません、慎重にバックアップしてください。"
		HoneyLanguage.Korean.code -> "암호가 복잡할수록 지갑은 안전합니다"
		HoneyLanguage.Russian.code -> "Чем сложнее пароль, тем более безопасен кошелек"
		HoneyLanguage.TraditionalChinese.code -> "越強的密碼越安全，請盡量設置更複雜的密碼。我們不會為您保管密碼，請您謹慎保管。"
		else -> ""
	}

	@JvmField
	val mnemonicBackUp = when (currentLanguage) {
		HoneyLanguage.English.code -> "Back up your mnemonic"
		HoneyLanguage.Chinese.code -> "备份助记词"
		HoneyLanguage.Japanese.code -> "バックアップニーモニック"
		HoneyLanguage.Korean.code -> "백업 니모닉"
		HoneyLanguage.Russian.code -> "Резервная мнемоника"
		HoneyLanguage.TraditionalChinese.code -> "備份助憶口令"
		else -> ""
	}

	@JvmField
	val agreement = when (currentLanguage) {
		HoneyLanguage.English.code -> "User Agreement"
		HoneyLanguage.Chinese.code -> "用户协议"
		HoneyLanguage.Japanese.code -> "利用規約"
		HoneyLanguage.Korean.code -> "사용자 동의서"
		HoneyLanguage.Russian.code -> "Пользовательское соглашение"
		HoneyLanguage.TraditionalChinese.code -> "用戶協議"
		else -> ""
	}

	@JvmField
	val agreeRemind = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please read and agree to the terms"
		HoneyLanguage.Chinese.code -> "请阅读并同意用户协议"
		HoneyLanguage.Japanese.code -> "条件に同意する必要があります"
		HoneyLanguage.Korean.code -> "이용 약관을 읽고 동의하십시오"
		HoneyLanguage.Russian.code -> "Прочтите и согласитесь с пользовательским соглашением"
		HoneyLanguage.TraditionalChinese.code -> "請閱讀並同意用戶協議"
		else -> ""
	}

	@JvmField
	val mnemonicBackupAttention = when (currentLanguage) {
		HoneyLanguage.English.code -> "We do not save any record of our users' mnemonics, so please take good care of them! To minimize risk, it's best not to save them digitally. Maybe write it down, you know like your grandmother used to do!"
		HoneyLanguage.Chinese.code -> "请将助记词抄写在安全的地方，不要保存到网络上也不要截屏以防被黑客盗走。"
		HoneyLanguage.Japanese.code -> "ニーモニックを再度確認して、正しくバックアップするようにします"
		HoneyLanguage.Korean.code -> "니모닉 단어를 확인하여 계정을 기억하십시오"
		HoneyLanguage.Russian.code -> "Подтвердите мнемонику, чтобы убедиться, что вы правильно настроили"
		HoneyLanguage.TraditionalChinese.code -> "確認一遍助憶口令，以確保您的備份正確"
		else -> ""
	}

	@JvmField
	val mnemonicConfirmationDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please click the mnemonic words in order. This makes sure that your backup is correct."
		HoneyLanguage.Chinese.code -> "按顺序点选助记词中的单词，以确保您的备份正确。"
		HoneyLanguage.Japanese.code -> "ニーモニックを再度確認して、正しくバックアップするようにします"
		HoneyLanguage.Korean.code -> "니모닉 단어를 확인하여 계정을 기억하십시오"
		HoneyLanguage.Russian.code -> "Подтвердите мнемонику, чтобы убедиться, что вы правильно настроили"
		HoneyLanguage.TraditionalChinese.code -> "確認一遍助憶口令，以確保您的備份正確"
		else -> ""
	}

	@JvmField
	val password = when (currentLanguage) {
		HoneyLanguage.English.code -> "Password"
		HoneyLanguage.Chinese.code -> "钱包密码"
		HoneyLanguage.Japanese.code -> "パスワード"
		HoneyLanguage.Korean.code -> "월렛 비밀번호"
		HoneyLanguage.Russian.code -> "Пароль кошелька"
		HoneyLanguage.TraditionalChinese.code -> "錢包密碼"
		else -> ""
	}

	@JvmField
	val hint = when (currentLanguage) {
		HoneyLanguage.English.code -> "Password Hint"
		HoneyLanguage.Chinese.code -> "密码提示"
		HoneyLanguage.Japanese.code -> "パスワードのヒント"
		HoneyLanguage.Korean.code -> "비밀번호 힌트"
		HoneyLanguage.Russian.code -> "Изменить пароль"
		HoneyLanguage.TraditionalChinese.code -> "密碼提示"
		else -> ""
	}

	@JvmField
	val repeatPassword = when (currentLanguage) {
		HoneyLanguage.English.code -> "Repeat Password"
		HoneyLanguage.Chinese.code -> "确认密码"
		HoneyLanguage.Japanese.code -> "繰り返しパスワード"
		HoneyLanguage.Korean.code -> "비밀번호를 입력하십시오"
		HoneyLanguage.Russian.code -> "Реальный пароль"
		HoneyLanguage.TraditionalChinese.code -> "再次輸入密碼"
		else -> ""
	}

	@JvmField
	val name = when (currentLanguage) {
		HoneyLanguage.English.code -> "Wallet Name"
		HoneyLanguage.Chinese.code -> "钱包名称"
		HoneyLanguage.Japanese.code -> "ウォレット名"
		HoneyLanguage.Korean.code -> "월렛 이름"
		HoneyLanguage.Russian.code -> "Название кошелька"
		HoneyLanguage.TraditionalChinese.code -> "錢包名稱"
		else -> ""
	}

	@JvmField
	val mnemonicConfirmation = when (currentLanguage) {
		HoneyLanguage.English.code -> "Mnemonic Confirmation"
		HoneyLanguage.Chinese.code -> "确认助记词"
		HoneyLanguage.Japanese.code -> "ニーモニックを確認する"
		HoneyLanguage.Korean.code -> "확인 보장 코드"
		HoneyLanguage.Russian.code -> "Подтвердить мнемонику"
		HoneyLanguage.TraditionalChinese.code -> "確認助憶口令"
		else -> ""
	}
}

object ImportWalletText {

	@JvmField
	val importWallet = when (currentLanguage) {
		HoneyLanguage.English.code -> "Import Wallet"
		HoneyLanguage.Chinese.code -> "导入钱包"
		HoneyLanguage.Japanese.code -> "ウォレットをインポート"
		HoneyLanguage.Korean.code -> "월렛 가져 오기"
		HoneyLanguage.Russian.code -> "Импортный кошелек"
		HoneyLanguage.TraditionalChinese.code -> "導入錢包"
		else -> ""
	}

	@JvmField
	val mnemonicHint = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter your mnemonic, split with spaces"
		HoneyLanguage.Chinese.code -> "按顺序输入助记词，使用空格间隔"
		HoneyLanguage.Japanese.code -> "単語の間のスペースを使用してニーモニックを順番に入力してください"
		HoneyLanguage.Korean.code -> "단어 사이의 공백을 사용하여 니모닉을 순서대로 입력하십시오."
		HoneyLanguage.Russian.code -> "Введите свою мнемонику, разделите ее пробелами. Введите пароль."
		HoneyLanguage.TraditionalChinese.code -> "請按順序輸入助記詞，詞間使用空格符間隔"
		else -> ""
	}

	@JvmField
	val keystoreHint = when (currentLanguage) {
		HoneyLanguage.English.code -> "Paste your keystore here"
		HoneyLanguage.Chinese.code -> "在此输入您的keystore"
		HoneyLanguage.Japanese.code -> "キーストアをここに入力してください"
		HoneyLanguage.Korean.code -> "여기에 키 스토어를 입력하십시오."
		HoneyLanguage.Russian.code -> "Введите свое хранилище ключей здесь"
		HoneyLanguage.TraditionalChinese.code -> "在此輸入您的keystore密鑰庫"
		else -> ""
	}

	@JvmField
	val privateKeyHint = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter your private key here"
		HoneyLanguage.Chinese.code -> "在此输入您的私钥"
		HoneyLanguage.Japanese.code -> "ここに秘密鍵を入力してください"
		HoneyLanguage.Korean.code -> "여기에 비공개 키를 입력하십시오."
		HoneyLanguage.Russian.code -> "Введите свой секретный ключ здесь"
		HoneyLanguage.TraditionalChinese.code -> "在此輸入您的私鑰"
		else -> ""
	}

	@JvmField
	val address = when (currentLanguage) {
		HoneyLanguage.English.code -> "Address"
		HoneyLanguage.Chinese.code -> "钱包地址"
		HoneyLanguage.Japanese.code -> "ウォレットアドレス"
		HoneyLanguage.Korean.code -> "월렛 주소"
		HoneyLanguage.Russian.code -> "Адрес кошелька"
		HoneyLanguage.TraditionalChinese.code -> "錢包地址"
		else -> ""
	}

	@JvmField
	val unvalidPrivateKey = when (currentLanguage) {
		HoneyLanguage.English.code -> "Invalid private key"
		HoneyLanguage.Chinese.code -> "这不是正确格式的私钥"
		HoneyLanguage.Japanese.code -> "これは、秘密鍵の正しい形式ではありません"
		HoneyLanguage.Korean.code -> "이것은 개인 키의 올바른 형식이 아닙니다."
		HoneyLanguage.Russian.code -> "Недопустимый закрытый ключ"
		HoneyLanguage.TraditionalChinese.code -> "這不是正確格式的私鑰"
		else -> ""
	}

	@JvmField
	val existAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "This address has already been imported"
		HoneyLanguage.Chinese.code -> "这个地址已经导入过了"
		HoneyLanguage.Japanese.code -> "このアドレスは既にインポートされています"
		HoneyLanguage.Korean.code -> "이 주소는 이미 가져 왔습니다."
		HoneyLanguage.Russian.code -> "Этот адрес уже импортирован"
		HoneyLanguage.TraditionalChinese.code -> "這個地址已經導入過了"
		else -> ""
	}

	@JvmField
	val exportKeystore = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter password and then click confirm to get your keystore"
		HoneyLanguage.Chinese.code -> "输入密码，然后单击确认按钮以获取keystore"
		HoneyLanguage.Japanese.code -> "パスワードを入力し、[OK]ボタンをクリックしてキーストアを取得します"
		HoneyLanguage.Korean.code -> "암호를 입력하고 확인 버튼을 클릭하여 키 저장소를 가져옵니다."
		HoneyLanguage.Russian.code -> "Введите пароль и нажмите кнопку подтверждения, чтобы получить хранилище ключей."
		HoneyLanguage.TraditionalChinese.code -> "輸入密碼，然後單擊確認按鈕以獲取keystore"
		else -> ""
	}

	@JvmField
	val exportPrivateKey = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter password and then click confirm to get your private key"
		HoneyLanguage.Chinese.code -> "输入密码，然后点击确认按钮获得私钥"
		HoneyLanguage.Japanese.code -> "パスワードを入力して確認ボタンをクリックすると、秘密鍵が取得されます"
		HoneyLanguage.Korean.code -> "비밀 번호를 입력하고 확인 버튼을 클릭하여 개인 키를 가져옵니다."
		HoneyLanguage.Russian.code -> "Введите пароль и нажмите кнопку подтверждения, чтобы получить секретный ключ"
		HoneyLanguage.TraditionalChinese.code -> "輸入密碼，然後點擊確認按鈕獲得私鑰"
		else -> ""
	}

	@JvmField
	val exportWrongPassword = when (currentLanguage) {
		HoneyLanguage.English.code -> "Incorrect password, try again"
		HoneyLanguage.Chinese.code -> "请输入正确的密码"
		HoneyLanguage.Japanese.code -> "Enter the corre"
		HoneyLanguage.Korean.code -> "Enter the corre"
		HoneyLanguage.Russian.code -> "Enter the corre"
		HoneyLanguage.TraditionalChinese.code -> "Enter the corre"
		else -> ""
	}

	@JvmField
	val privateKeyAlert = when (currentLanguage) {
		HoneyLanguage.English.code -> "Incorrect private key"
		HoneyLanguage.Chinese.code -> "Incorrect private key"
		HoneyLanguage.Japanese.code -> "Incorrect private key"
		HoneyLanguage.Korean.code -> "Incorrect private key"
		HoneyLanguage.Russian.code -> "Incorrect private key"
		HoneyLanguage.TraditionalChinese.code -> "Incorrect private key"
		else -> ""
	}

	@JvmField
	val mnemonicAlert = when (currentLanguage) {
		HoneyLanguage.English.code -> "Incorrect private key"
		HoneyLanguage.Chinese.code -> "Incorrect private key"
		HoneyLanguage.Japanese.code -> "incorre"
		HoneyLanguage.Korean.code -> "incorre"
		HoneyLanguage.Russian.code -> "incorre"
		HoneyLanguage.TraditionalChinese.code -> "incorre"
		else -> ""
	}

	@JvmField
	val mnemonicLengthAlert = when (currentLanguage) {
		HoneyLanguage.English.code -> "The mnemonic is too short"
		HoneyLanguage.Chinese.code -> "助记词不够长哦"
		HoneyLanguage.Japanese.code -> "The mnemonic is too short"
		HoneyLanguage.Korean.code -> "The mnemonic is too short"
		HoneyLanguage.Russian.code -> "The mnemonic is too short"
		HoneyLanguage.TraditionalChinese.code -> "The mnemonic is too short"
		else -> ""
	}

	@JvmField
	val pathAlert = when (currentLanguage) {
		HoneyLanguage.English.code -> "invalid path"
		HoneyLanguage.Chinese.code -> "路径格式不正确"
		HoneyLanguage.Japanese.code -> "invalid path"
		HoneyLanguage.Korean.code -> "invalid path"
		HoneyLanguage.Russian.code -> "invalid path"
		HoneyLanguage.TraditionalChinese.code -> "invalid path"
		else -> ""
	}

	@JvmField
	val addressFromatAlert = when (currentLanguage) {
		HoneyLanguage.English.code -> "Invalid address"
		HoneyLanguage.Chinese.code -> "Invalid address"
		HoneyLanguage.Japanese.code -> "Invalid address"
		HoneyLanguage.Korean.code -> "Invalid address"
		HoneyLanguage.Russian.code -> "Invalid address"
		HoneyLanguage.TraditionalChinese.code -> "Invalid address"
		else -> ""
	}
}

object DialogText {

	@JvmField
	val backUpMnemonicSucceed = when (currentLanguage) {
		HoneyLanguage.English.code -> "You have already back un your mnemonic yet, Please take care it because we have no way to find it back, once you lost it please keep you digtal assets"
		HoneyLanguage.Chinese.code -> "You have already back un your mnemonic yet, Please take care it because we have no way to find it back, once you lost it please keep you digtal assets"
		HoneyLanguage.Japanese.code -> "You have already back un your mnemonic yet, Please take care it because we have no way to find it back, once you lost it please keep you digtal assets"
		HoneyLanguage.Korean.code -> "You have already back un your mnemonic yet, Please take care it because we have no way to find it back, once you lost it please keep you digtal assets"
		HoneyLanguage.Russian.code -> "You have already back un your mnemonic yet, Please take care it because we have no way to find it back, once you lost it please keep you digtal assets"
		HoneyLanguage.TraditionalChinese.code -> "You have already back un your mnemonic yet, Please take care it because we have no way to find it back, once you lost it please keep you digtal assets"
		else -> ""
	}

	@JvmField
	val backUpMnemonic = when (currentLanguage) {
		HoneyLanguage.English.code -> "Back Up Mnemonic"
		HoneyLanguage.Chinese.code -> "Back Up Mnemonic"
		HoneyLanguage.Japanese.code -> "Back Up Mnemonic"
		HoneyLanguage.Korean.code -> "Back Up Mnemonic"
		HoneyLanguage.Russian.code -> "Back Up Mnemonic"
		HoneyLanguage.TraditionalChinese.code -> "Back Up Mnemonic"
		else -> ""
	}

	@JvmField
	val backUpMnemonicDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "An extensible dialog system I designed for the ItsON SaaS telecom solution for mobile Android devices at the OS level. Having dialogs easily identifiable as the brand of the phones service provider allows the context to be clearly understood"
		HoneyLanguage.Chinese.code -> "An extensible dialog system I designed for the ItsON SaaS telecom solution for mobile Android devices at the OS level. Having dialogs easily identifiable as the brand of the phones service provider allows the context to be clearly understood"
		HoneyLanguage.Japanese.code -> "An extensible dialog system I designed for the ItsON SaaS telecom solution for mobile Android devices at the OS level. Having dialogs easily identifiable as the brand of the phones service provider allows the context to be clearly understood"
		HoneyLanguage.Korean.code -> "An extensible dialog system I designed for the ItsON SaaS telecom solution for mobile Android devices at the OS level. Having dialogs easily identifiable as the brand of the phones service provider allows the context to be clearly understood"
		HoneyLanguage.Russian.code -> "An extensible dialog system I designed for the ItsON SaaS telecom solution for mobile Android devices at the OS level. Having dialogs easily identifiable as the brand of the phones service provider allows the context to be clearly understood"
		HoneyLanguage.TraditionalChinese.code -> "An extensible dialog system I designed for the ItsON SaaS telecom solution for mobile Android devices at the OS level. Having dialogs easily identifiable as the brand of the phones service provider allows the context to be clearly understood"
		else -> ""
	}

	@JvmField
	val networkTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Network unavailable"
		HoneyLanguage.Chinese.code -> "无网络"
		HoneyLanguage.Japanese.code -> "Network Browken"
		HoneyLanguage.Korean.code -> "Network Browken"
		HoneyLanguage.Russian.code -> "Network Browken"
		HoneyLanguage.TraditionalChinese.code -> "Network Browken"
		else -> ""
	}

	@JvmField
	val networkDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "An extensible dialog system I designed for the ItsON SaaS telecom solution for mobile Android devices at the OS level. Having dialogs easily identifiable as the brand of the phones service provider allows the context to be clearly understood"
		HoneyLanguage.Chinese.code -> "An extensible dialog system I designed for the ItsON SaaS telecom solution for mobile Android devices at the OS level. Having dialogs easily identifiable as the brand of the phones service provider allows the context to be clearly understood"
		HoneyLanguage.Japanese.code -> "An extensible dialog system I designed for the ItsON SaaS telecom solution for mobile Android devices at the OS level. Having dialogs easily identifiable as the brand of the phones service provider allows the context to be clearly understood"
		HoneyLanguage.Korean.code -> "An extensible dialog system I designed for the ItsON SaaS telecom solution for mobile Android devices at the OS level. Having dialogs easily identifiable as the brand of the phones service provider allows the context to be clearly understood"
		HoneyLanguage.Russian.code -> "An extensible dialog system I designed for the ItsON SaaS telecom solution for mobile Android devices at the OS level. Having dialogs easily identifiable as the brand of the phones service provider allows the context to be clearly understood"
		HoneyLanguage.TraditionalChinese.code -> "An extensible dialog system I designed for the ItsON SaaS telecom solution for mobile Android devices at the OS level. Having dialogs easily identifiable as the brand of the phones service provider allows the context to be clearly understood"
		else -> ""
	}

	@JvmField
	val goToBackUp = when (currentLanguage) {
		HoneyLanguage.English.code -> ""
		HoneyLanguage.Chinese.code -> ""
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}
}

object WalletText {

	@JvmField
	val totalAssets = when (currentLanguage) {
		HoneyLanguage.English.code -> "Total Assets"
		HoneyLanguage.Chinese.code -> "钱包所有财产"
		HoneyLanguage.Japanese.code -> "総資産"
		HoneyLanguage.Korean.code -> "총자산"
		HoneyLanguage.Russian.code -> "Итого активы"
		HoneyLanguage.TraditionalChinese.code -> "總資產"
		else -> ""
	}

	@JvmField
	val manage = when (currentLanguage) {
		HoneyLanguage.English.code -> "MANAGE MY WALLETS"
		HoneyLanguage.Chinese.code -> "管理我的钱包"
		HoneyLanguage.Japanese.code -> "ウォレットを管理する"
		HoneyLanguage.Korean.code -> "월렛 관리"
		HoneyLanguage.Russian.code -> "Управление кошельками"
		HoneyLanguage.TraditionalChinese.code -> "管理我的錢包"
		else -> ""
	}

	@JvmField
	val section = when (currentLanguage) {
		HoneyLanguage.English.code -> "My Tokens:"
		HoneyLanguage.Chinese.code -> "我的资产:"
		HoneyLanguage.Japanese.code -> "マイトークン:"
		HoneyLanguage.Korean.code -> "내 토큰:"
		HoneyLanguage.Russian.code -> "Мои токены:"
		HoneyLanguage.TraditionalChinese.code -> "我的資產:"
		else -> ""
	}

	@JvmField
	val addToken = when (currentLanguage) {
		HoneyLanguage.English.code -> "Add More Tokens"
		HoneyLanguage.Chinese.code -> "添加其他Token"
		HoneyLanguage.Japanese.code -> "トークンを追加する"
		HoneyLanguage.Korean.code -> "더 많은 토큰 추가"
		HoneyLanguage.Russian.code -> "Add More Tokens"
		HoneyLanguage.TraditionalChinese.code -> "添加其他Token"
		else -> ""
	}

	@JvmField
	val addWallet = when (currentLanguage) {
		HoneyLanguage.English.code -> "Add Wallet"
		HoneyLanguage.Chinese.code -> "添加钱包"
		HoneyLanguage.Japanese.code -> "ウォレットを追加"
		HoneyLanguage.Korean.code -> "지갑 추가"
		HoneyLanguage.Russian.code -> "Добавить кошелек"
		HoneyLanguage.TraditionalChinese.code -> "添加錢包"
		else -> ""
	}

	@JvmField
	val wallet = when (currentLanguage) {
		HoneyLanguage.English.code -> "wallet"
		HoneyLanguage.Chinese.code -> "wallet"
		HoneyLanguage.Japanese.code -> "wallet"
		HoneyLanguage.Korean.code -> "wallet"
		HoneyLanguage.Russian.code -> "wallet"
		HoneyLanguage.TraditionalChinese.code -> "wallet"
		else -> ""
	}

	@JvmField
	val historyButton = when (currentLanguage) {
		HoneyLanguage.English.code -> "history"
		HoneyLanguage.Chinese.code -> "history"
		HoneyLanguage.Japanese.code -> "history"
		HoneyLanguage.Korean.code -> "history"
		HoneyLanguage.Russian.code -> "history"
		HoneyLanguage.TraditionalChinese.code -> "history"
		else -> ""
	}

	@JvmField
	val notifyButton = when (currentLanguage) {
		HoneyLanguage.English.code -> "notify"
		HoneyLanguage.Chinese.code -> "notify"
		HoneyLanguage.Japanese.code -> "notify"
		HoneyLanguage.Korean.code -> "notify"
		HoneyLanguage.Russian.code -> "notify"
		HoneyLanguage.TraditionalChinese.code -> "notify"
		else -> ""
	}
}

object TransactionText {

	@JvmField
	val transaction = when (currentLanguage) {
		HoneyLanguage.English.code -> "Transaction History"
		HoneyLanguage.Chinese.code -> "交易历史"
		HoneyLanguage.Japanese.code -> "取引履歴"
		HoneyLanguage.Korean.code -> "거래 내역"
		HoneyLanguage.Russian.code -> "История транзакций"
		HoneyLanguage.TraditionalChinese.code -> "交易歷史"
		else -> ""
	}

	@JvmField
	val detail = when (currentLanguage) {
		HoneyLanguage.English.code -> "Transaction Details"
		HoneyLanguage.Chinese.code -> "交易详情"
		HoneyLanguage.Japanese.code -> "取引の詳細"
		HoneyLanguage.Korean.code -> "거래 세부 정보"
		HoneyLanguage.Russian.code -> "Подробности транзакции"
		HoneyLanguage.TraditionalChinese.code -> "交易明細"
		else -> ""
	}

	@JvmField
	val etherScanTransaction = when (currentLanguage) {
		HoneyLanguage.English.code -> "Etherscan Details"
		HoneyLanguage.Chinese.code -> "EtherScan详情"
		HoneyLanguage.Japanese.code -> "EtherScanの詳細"
		HoneyLanguage.Korean.code -> "EtherScan 세부 정보"
		HoneyLanguage.Russian.code -> "Подробное описание EtherScan"
		HoneyLanguage.TraditionalChinese.code -> "EtherScan詳情"
		else -> ""
	}

	@JvmField
	val url = when (currentLanguage) {
		HoneyLanguage.English.code -> "Open URL"
		HoneyLanguage.Chinese.code -> "从网址打开"
		HoneyLanguage.Japanese.code -> "URLを開く"
		HoneyLanguage.Korean.code -> "URL 열기"
		HoneyLanguage.Russian.code -> "Открыть адрес"
		HoneyLanguage.TraditionalChinese.code -> "從網址打開"
		else -> ""
	}

	@JvmField
	val confirmTransaction = when (currentLanguage) {
		HoneyLanguage.English.code -> "Confirm transaction with your password"
		HoneyLanguage.Chinese.code -> "输入您的密码以确认交易"
		HoneyLanguage.Japanese.code -> "パスワードで取引を確認してください。"
		HoneyLanguage.Korean.code -> "귀하의 비밀번호로 거래를 확인하십시오"
		HoneyLanguage.Russian.code -> "подтвердите транзакцию с помощью своего пароля, тогда транзакция начнется"
		HoneyLanguage.TraditionalChinese.code -> "輸入您的密碼以確認交易"
		else -> ""
	}

	@JvmField
	val minerFee = when (currentLanguage) {
		HoneyLanguage.English.code -> "Miner Fee"
		HoneyLanguage.Chinese.code -> "矿工费"
		HoneyLanguage.Japanese.code -> "鉱夫料"
		HoneyLanguage.Korean.code -> "광부비"
		HoneyLanguage.Russian.code -> "Гонорар шахтеров"
		HoneyLanguage.TraditionalChinese.code -> "礦工費"
		else -> ""
	}

	@JvmField
	val memo = when (currentLanguage) {
		HoneyLanguage.English.code -> "Memo"
		HoneyLanguage.Chinese.code -> "备注"
		HoneyLanguage.Japanese.code -> "メモ"
		HoneyLanguage.Korean.code -> "메모"
		HoneyLanguage.Russian.code -> "напоминание"
		HoneyLanguage.TraditionalChinese.code -> "메모"
		else -> ""
	}

	@JvmField
	val transactionHash = when (currentLanguage) {
		HoneyLanguage.English.code -> "Transaction Hash"
		HoneyLanguage.Chinese.code -> "交易Hash"
		HoneyLanguage.Japanese.code -> "トランザクションハッシュ"
		HoneyLanguage.Korean.code -> "트랜잭션 해시"
		HoneyLanguage.Russian.code -> "Transaction Hash"
		HoneyLanguage.TraditionalChinese.code -> "交易Hash"
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
		HoneyLanguage.Japanese.code -> "取引日"
		HoneyLanguage.Korean.code -> "거래 날짜"
		HoneyLanguage.Russian.code -> "Transaction Date"
		HoneyLanguage.TraditionalChinese.code -> "交易日期"
		else -> ""
	}

	@JvmField
	val gasLimit = when (currentLanguage) {
		HoneyLanguage.English.code -> "Gas Limit"
		HoneyLanguage.Chinese.code -> "燃气费上限"
		HoneyLanguage.Japanese.code -> "ガスキャップ"
		HoneyLanguage.Korean.code -> "가스 캡"
		HoneyLanguage.Russian.code -> "Газовый предел"
		HoneyLanguage.TraditionalChinese.code -> "燃氣費上限"
		else -> ""
	}

	@JvmField
	val gasPrice = when (currentLanguage) {
		HoneyLanguage.English.code -> "Gas Price (Gwei)"
		HoneyLanguage.Chinese.code -> "燃气单价"
		HoneyLanguage.Japanese.code -> "ガス単価"
		HoneyLanguage.Korean.code -> "가스 단가"
		HoneyLanguage.Russian.code -> "Цена газа"
		HoneyLanguage.TraditionalChinese.code -> "燃氣單價"
		else -> ""
	}
}

object TokenDetailText {

	@JvmField
	val address = when (currentLanguage) {
		HoneyLanguage.English.code -> "Recipient Address"
		HoneyLanguage.Chinese.code -> "接收地址"
		HoneyLanguage.Japanese.code -> "宛先"
		HoneyLanguage.Korean.code -> "수신 주소"
		HoneyLanguage.Russian.code -> "Recipient Address"
		HoneyLanguage.TraditionalChinese.code -> "接收地址"
		else -> ""
	}

	@JvmField
	val deposit = when (currentLanguage) {
		HoneyLanguage.English.code -> "Deposit"
		HoneyLanguage.Chinese.code -> "接收"
		HoneyLanguage.Japanese.code -> "受信"
		HoneyLanguage.Korean.code -> "수신"
		HoneyLanguage.Russian.code -> "Deposit"
		HoneyLanguage.TraditionalChinese.code -> "接收"
		else -> ""
	}

	@JvmField
	val customGas = when (currentLanguage) {
		HoneyLanguage.English.code -> "Gas Editor"
		HoneyLanguage.Chinese.code -> "自定义燃气费"
		HoneyLanguage.Japanese.code -> "カスタムガス料金"
		HoneyLanguage.Korean.code -> "맞춤 가스 요금"
		HoneyLanguage.Russian.code -> "Редактор газа"
		HoneyLanguage.TraditionalChinese.code -> "自定義燃氣費"
		else -> ""
	}

	@JvmField
	val paymentValue = when (currentLanguage) {
		HoneyLanguage.English.code -> "Payment Value"
		HoneyLanguage.Chinese.code -> "实际价值"
		HoneyLanguage.Japanese.code -> "実際の値"
		HoneyLanguage.Korean.code -> "실제 값"
		HoneyLanguage.Russian.code -> "Стоимость платежа"
		HoneyLanguage.TraditionalChinese.code -> "實際價值"
		else -> ""
	}

	@JvmField
	val transferDetail = when (currentLanguage) {
		HoneyLanguage.English.code -> "Transaction Details"
		HoneyLanguage.Chinese.code -> "交易详情"
		HoneyLanguage.Japanese.code -> "転送の詳細"
		HoneyLanguage.Korean.code -> "이체 세부 사항"
		HoneyLanguage.Russian.code -> "Сведения о передаче"
		HoneyLanguage.TraditionalChinese.code -> "交易詳情"
		else -> ""
	}

	@JvmField
	val customMiner = when (currentLanguage) {
		HoneyLanguage.English.code -> "Custom miner fee"
		HoneyLanguage.Chinese.code -> "自定义矿工费"
		HoneyLanguage.Japanese.code -> "カスタム鉱夫料金"
		HoneyLanguage.Korean.code -> "맞춤 광부 요금"
		HoneyLanguage.Russian.code -> "Индивидуальная плата за шахт"
		HoneyLanguage.TraditionalChinese.code -> "自定義礦工費"
		else -> ""
	}

	@JvmField
	val tokenDetail = when (currentLanguage) {
		HoneyLanguage.English.code -> "Token Description"
		HoneyLanguage.Chinese.code -> "代币详情"
		HoneyLanguage.Japanese.code -> "トークンの詳細"
		HoneyLanguage.Korean.code -> "토큰 세부 정보"
		HoneyLanguage.Russian.code -> "Детали токена"
		HoneyLanguage.TraditionalChinese.code -> "代幣詳情"
		else -> ""
	}

	@JvmField
	val transferToLocalWalletAlertDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "are you decide to transfer to this address which is existing in your local wallets?"
		HoneyLanguage.Chinese.code -> "are you decide to transfer to this address which is existing in your local wallets?"
		HoneyLanguage.Japanese.code -> "are you decide to transfer to this address which is existing in your local wallets?"
		HoneyLanguage.Korean.code -> "are you decide to transfer to this address which is existing in your local wallets?"
		HoneyLanguage.Russian.code -> "are you decide to transfer to this address which is existing in your local wallets?"
		HoneyLanguage.TraditionalChinese.code -> "are you decide to transfer to this address which is existing in your local wallets?"
		else -> ""
	}

	@JvmField
	val transferToLocalWalletAlertTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Transfer Attention"
		HoneyLanguage.Chinese.code -> "Transfer Attention"
		HoneyLanguage.Japanese.code -> "Transfer Attention"
		HoneyLanguage.Korean.code -> "Transfer Attention"
		HoneyLanguage.Russian.code -> "Transfer Attention"
		HoneyLanguage.TraditionalChinese.code -> "Transfer Attention"
		else -> ""
	}

	@JvmField
	val setTransferCountAlert = when (currentLanguage) {
		HoneyLanguage.English.code -> "You have to set the transfer count"
		HoneyLanguage.Chinese.code -> "You have to set the transfer count"
		HoneyLanguage.Japanese.code -> "You have to set the transfer count"
		HoneyLanguage.Korean.code -> "You have to set the transfer count"
		HoneyLanguage.Russian.code -> "You have to set the transfer count"
		HoneyLanguage.TraditionalChinese.code -> "You have to set the transfer count"
		else -> ""
	}
}

object CommonText {

	@JvmField
	val confirm = when (currentLanguage) {
		HoneyLanguage.English.code -> "CONFIRM"
		HoneyLanguage.Chinese.code -> "确认"
		HoneyLanguage.Japanese.code -> "確認"
		HoneyLanguage.Korean.code -> "확인"
		HoneyLanguage.Russian.code -> "подтвердить"
		HoneyLanguage.TraditionalChinese.code -> "確認"
		else -> ""
	}

	@JvmField
	val wrongPassword = when (currentLanguage) {
		HoneyLanguage.English.code -> "Wrong Password"
		HoneyLanguage.Chinese.code -> "Wrong Password"
		HoneyLanguage.Japanese.code -> "Wrong Password"
		HoneyLanguage.Korean.code -> "Wrong Password"
		HoneyLanguage.Russian.code -> "Wrong Password"
		HoneyLanguage.TraditionalChinese.code -> "Wrong Password"
		else -> ""
	}

	@JvmField
	val succeed = when (currentLanguage) {
		HoneyLanguage.English.code -> "Success"
		HoneyLanguage.Chinese.code -> "成功"
		HoneyLanguage.Japanese.code -> "Succeed"
		HoneyLanguage.Korean.code -> "Succeed"
		HoneyLanguage.Russian.code -> "Succeed"
		HoneyLanguage.TraditionalChinese.code -> "Succeed"
		else -> ""
	}

	@JvmField
	val skip = when (currentLanguage) {
		HoneyLanguage.English.code -> "Back Up Later"
		HoneyLanguage.Chinese.code -> "Back Up Later"
		HoneyLanguage.Japanese.code -> "Back Up Later"
		HoneyLanguage.Korean.code -> "Back Up Later"
		HoneyLanguage.Russian.code -> "Back Up Later"
		HoneyLanguage.TraditionalChinese.code -> "Back Up Later"
		else -> ""
	}

	@JvmField
	val create = when (currentLanguage) {
		HoneyLanguage.English.code -> "Create.toUpperCase()"
		HoneyLanguage.Chinese.code -> "添加"
		HoneyLanguage.Japanese.code -> "追加"
		HoneyLanguage.Korean.code -> "만들기"
		HoneyLanguage.Russian.code -> "создать"
		HoneyLanguage.TraditionalChinese.code -> "添加"
		else -> ""
	}

	@JvmField
	val cancel = when (currentLanguage) {
		HoneyLanguage.English.code -> "CANCEL"
		HoneyLanguage.Chinese.code -> "取消"
		HoneyLanguage.Japanese.code -> "キャンセル"
		HoneyLanguage.Korean.code -> "취소"
		HoneyLanguage.Russian.code -> "CANCEL"
		HoneyLanguage.TraditionalChinese.code -> "取消"
		else -> ""
	}

	@JvmField
	val next = when (currentLanguage) {
		HoneyLanguage.English.code -> "Next"
		HoneyLanguage.Chinese.code -> "下一步"
		HoneyLanguage.Japanese.code -> "次へ"
		HoneyLanguage.Korean.code -> "다음"
		HoneyLanguage.Russian.code -> "Next"
		HoneyLanguage.TraditionalChinese.code -> "下一步"
		else -> ""
	}

	@JvmField
	val saveToAlbum = when (currentLanguage) {
		HoneyLanguage.English.code -> "SAVE TO ALBUM"
		HoneyLanguage.Chinese.code -> "保存到相册"
		HoneyLanguage.Japanese.code -> "アルバムに保存"
		HoneyLanguage.Korean.code -> "앨범에 저장"
		HoneyLanguage.Russian.code -> "Сохранить в альбом"
		HoneyLanguage.TraditionalChinese.code -> "保存到相簿"
		else -> ""
	}

	@JvmField
	val shareQRImage = when (currentLanguage) {
		HoneyLanguage.English.code -> "Share QR Image"
		HoneyLanguage.Chinese.code -> "Share QR Image"
		HoneyLanguage.Japanese.code -> "Share QR Image"
		HoneyLanguage.Korean.code -> "Share QR Image"
		HoneyLanguage.Russian.code -> "Share QR Image"
		HoneyLanguage.TraditionalChinese.code -> "Share QR Image"
		else -> ""
	}

	@JvmField
	val copyAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "Click to Copy Address"
		HoneyLanguage.Chinese.code -> "点击复制地址"
		HoneyLanguage.Japanese.code -> "コピー"
		HoneyLanguage.Korean.code -> "지갑 주소 복사를 클릭하십시오"
		HoneyLanguage.Russian.code -> "Копировать адрес"
		HoneyLanguage.TraditionalChinese.code -> "點擊複製地址"
		else -> ""
	}

	@JvmField
	val startImporting = when (currentLanguage) {
		HoneyLanguage.English.code -> "START IMPORTING"
		HoneyLanguage.Chinese.code -> "开始导入"
		HoneyLanguage.Japanese.code -> "インポートを開始する"
		HoneyLanguage.Korean.code -> "가져 오기 시작"
		HoneyLanguage.Russian.code -> "Начать импорт"
		HoneyLanguage.TraditionalChinese.code -> "開始導入"
		else -> ""
	}

	@JvmField
	val enterPassword = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter Password"
		HoneyLanguage.Chinese.code -> "输入钱包密码"
		HoneyLanguage.Japanese.code -> "パスワードを入力してください"
		HoneyLanguage.Korean.code -> "비밀번호를 입력하십시오"
		HoneyLanguage.Russian.code -> "Введите пароль"
		HoneyLanguage.TraditionalChinese.code -> "輸入錢包密碼"
		else -> ""
	}

	@JvmField
	val delete = when (currentLanguage) {
		HoneyLanguage.English.code -> "Delete Wallet"
		HoneyLanguage.Chinese.code -> "删除钱包"
		HoneyLanguage.Japanese.code -> "削除"
		HoneyLanguage.Korean.code -> "지갑 지우기"
		HoneyLanguage.Russian.code -> "УДАЛИТЬ"
		HoneyLanguage.TraditionalChinese.code -> "刪除錢包"
		else -> ""
	}

	@JvmField
	val slow = when (currentLanguage) {
		HoneyLanguage.English.code -> "SLOW"
		HoneyLanguage.Chinese.code -> "慢"
		HoneyLanguage.Japanese.code -> "遅い"
		HoneyLanguage.Korean.code -> "느리게"
		HoneyLanguage.Russian.code -> "SLOW"
		HoneyLanguage.TraditionalChinese.code -> "慢"
		else -> ""
	}

	@JvmField
	val fast = when (currentLanguage) {
		HoneyLanguage.English.code -> "FAST"
		HoneyLanguage.Chinese.code -> "快速"
		HoneyLanguage.Japanese.code -> "速い"
		HoneyLanguage.Korean.code -> "빠른"
		HoneyLanguage.Russian.code -> "FAST"
		HoneyLanguage.TraditionalChinese.code -> "快速"
		else -> ""
	}
}

object AlertText {

	@JvmField
	val watchOnly = when (currentLanguage) {
		HoneyLanguage.English.code -> "Current wallet is watch-only. This operation is not allowed."
		HoneyLanguage.Chinese.code -> "这是观察钱包，无法进行转账交易。"
		HoneyLanguage.Japanese.code -> "これは観測ウォレットであり、トランザクションを転送できません。"
		HoneyLanguage.Korean.code -> "이것은 관측 지갑이며 거래를 전송할 수 없습니다"
		HoneyLanguage.Russian.code -> "Это наблюдательный кошелек, который не может передавать транзакции"
		HoneyLanguage.TraditionalChinese.code -> "這是觀察錢包，無法進行轉賬交易。"
		else -> ""
	}

	@JvmField
	val importWalletNetwork = when (currentLanguage) {
		HoneyLanguage.English.code -> "Network unavailable. Network is required to check the value of the importing wallet."
		HoneyLanguage.Chinese.code -> "没有检测到网络，导入钱包时需要网络环境查询您的货币余额"
		HoneyLanguage.Japanese.code -> "ネットワークが見つかりません。ウォレットをインポートするとネットワークの価値を確認する必要があります"
		HoneyLanguage.Korean.code -> "네트워크를 찾을 수 없습니다. 지갑을 가져 와서 값을 확인해야합니다."
		HoneyLanguage.Russian.code -> "Сеть не найдена, Импорт сети для кошелька, чтобы проверить ее значение"
		HoneyLanguage.TraditionalChinese.code -> "沒有檢測到網絡，導入錢包時需要網絡環境查詢您的貨幣餘額\n"
		else -> ""
	}

	@JvmField
	val balanceNotEnough = when (currentLanguage) {
		HoneyLanguage.English.code -> "You haven't enough currency to transfer and gas fee"
		HoneyLanguage.Chinese.code -> "You haven't enough currency to transfer and gas fee"
		HoneyLanguage.Japanese.code -> "You haven't enough currency to transfer and gas fee"
		HoneyLanguage.Korean.code -> "You haven't enough currency to transfer and gas fee"
		HoneyLanguage.Russian.code -> "You haven't enough currency to transfer and gas fee"
		HoneyLanguage.TraditionalChinese.code -> "You haven't enough currency to transfer and gas fee"
		else -> ""
	}

	@JvmField
	val transferWrongDecimal = when (currentLanguage) {
		HoneyLanguage.English.code -> "The value's decimal you inputed is bigger than this currency token's decimal please re-input"
		HoneyLanguage.Chinese.code -> "The value's decimal you inputed is bigger than this currency token's decimal please re-input"
		HoneyLanguage.Japanese.code -> "The value's decimal you inputed is bigger than this currency token's decimal please re-input"
		HoneyLanguage.Korean.code -> "The value's decimal you inputed is bigger than this currency token's decimal please re-input"
		HoneyLanguage.Russian.code -> "The value's decimal you inputed is bigger than this currency token's decimal please re-input"
		HoneyLanguage.TraditionalChinese.code -> "The value's decimal you inputed is bigger than this currency token's decimal please re-input"
		else -> ""
	}

	@JvmField
	val emptyTransferValue = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please Enter Your Transfer Value"
		HoneyLanguage.Chinese.code -> "Please Enter Your Transfer Value"
		HoneyLanguage.Japanese.code -> "Please Enter Your Transfer Value"
		HoneyLanguage.Korean.code -> "Please Enter Your Transfer Value"
		HoneyLanguage.Russian.code -> "Please Enter Your Transfer Value"
		HoneyLanguage.TraditionalChinese.code -> "Please Enter Your Transfer Value"
		else -> ""
	}

	@JvmField
	val gasEditorEmpty = when (currentLanguage) {
		HoneyLanguage.English.code -> "You have to set gas price or gas limit"
		HoneyLanguage.Chinese.code -> "You have to set gas price or gas limit"
		HoneyLanguage.Japanese.code -> "You have to set gas price or gas limit"
		HoneyLanguage.Korean.code -> "You have to set gas price or gas limit"
		HoneyLanguage.Russian.code -> "You have to set gas price or gas limit"
		HoneyLanguage.TraditionalChinese.code -> "You have to set gas price or gas limit"
		else -> ""
	}

	@JvmField
	val gasLimitValue = when (currentLanguage) {
		HoneyLanguage.English.code -> "Gas limit must more than"
		HoneyLanguage.Chinese.code -> "Gas limit must more than"
		HoneyLanguage.Japanese.code -> "Gas limit must more than"
		HoneyLanguage.Korean.code -> "Gas limit must more than"
		HoneyLanguage.Russian.code -> "Gas limit must more than"
		HoneyLanguage.TraditionalChinese.code -> "Gas limit must more than"
		else -> ""
	}

	@JvmField
	val transferUnvalidInputFromat = when (currentLanguage) {
		HoneyLanguage.English.code -> "Invalid transfer amount"
		HoneyLanguage.Chinese.code -> "请输入正确的转账金额"
		HoneyLanguage.Japanese.code -> "Invalid transfer amount"
		HoneyLanguage.Korean.code -> "Invalid transfer amount"
		HoneyLanguage.Russian.code -> "Invalid transfer amount"
		HoneyLanguage.TraditionalChinese.code -> "Invalid transfer amount"
		else -> ""
	}

	@JvmField
	val switchLanguage = when (currentLanguage) {
		HoneyLanguage.English.code -> "Once you selected it, application will be rebooted and just wait several seconds. Are You Sure To Switch Language Settings?"
		HoneyLanguage.Chinese.code -> "一旦你选择切换语言，应用程序将被重新启动，并等待几秒钟。 你确定要切换货币设置吗？"
		HoneyLanguage.Japanese.code -> "Once you selected it, application will be rebooted and just wait several seconds. Are You Sure To Switch Language Settings?"
		HoneyLanguage.Korean.code -> "Once you selected it, application will be rebooted and just wait several seconds. Are You Sure To Switch Language Settings?"
		HoneyLanguage.Russian.code -> "Once you selected it, application will be rebooted and just wait several seconds. Are You Sure To Switch Language Settings?"
		HoneyLanguage.TraditionalChinese.code -> "Once you selected it, application will be rebooted and just wait several seconds. Are You Sure To Switch Language Settings?"
		else -> ""
	}

	@JvmField
	val switchLanguageConfirmText = when (currentLanguage) {
		HoneyLanguage.English.code -> "Change Language"
		HoneyLanguage.Chinese.code -> "切换语言"
		HoneyLanguage.Japanese.code -> "Change Language"
		HoneyLanguage.Korean.code -> "Change Language"
		HoneyLanguage.Russian.code -> "Change Language"
		HoneyLanguage.TraditionalChinese.code -> "Change Language"
		else -> ""
	}

	@JvmField
	val wrongKeyStorePassword = when (currentLanguage) {
		HoneyLanguage.English.code -> "Invaild keystore format or password"
		HoneyLanguage.Chinese.code -> "出错啦，请检查keystore或密码的格式是否正确"
		HoneyLanguage.Japanese.code -> "Invaild keystore format or password"
		HoneyLanguage.Korean.code -> "Invaild keystore format or password"
		HoneyLanguage.Russian.code -> "Invaild keystore format or password"
		HoneyLanguage.TraditionalChinese.code -> "Invaild keystore format or password"
		else -> ""
	}

	@JvmField
	val getRateFromServerError = when (currentLanguage) {
		HoneyLanguage.English.code -> "Something wrong happened, cannot get currency rate now"
		HoneyLanguage.Chinese.code -> "出错了，暂时无法获取汇率信息"
		HoneyLanguage.Japanese.code -> "Something wrong happened, cannot get currency rate now"
		HoneyLanguage.Korean.code -> "Something wrong happened, cannot get currency rate now"
		HoneyLanguage.Russian.code -> "Something wrong happened, cannot get currency rate now"
		HoneyLanguage.TraditionalChinese.code -> "Something wrong happened, cannot get currency rate now"
		else -> ""
	}
}

object CurrentWalletText {

	@JvmField
	val Wallets = when (currentLanguage) {
		HoneyLanguage.English.code -> "Wallets"
		HoneyLanguage.Chinese.code -> "钱包列表"
		HoneyLanguage.Japanese.code -> "すべてのワォレット"
		HoneyLanguage.Korean.code -> "모든 지갑"
		HoneyLanguage.Russian.code -> "Все кошельки"
		HoneyLanguage.TraditionalChinese.code -> "錢包列表"
		else -> ""
	}
}

object WatchOnlyText {

	@JvmField
	val enterDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter the address of the wallet to be observed"
		HoneyLanguage.Chinese.code -> "请输入想观察的钱包地址"
		HoneyLanguage.Japanese.code -> "Enter Address That You Want to Watch"
		HoneyLanguage.Korean.code -> "Enter Address That You Want to Watch"
		HoneyLanguage.Russian.code -> "Enter Address That You Want to Watch"
		HoneyLanguage.TraditionalChinese.code -> "Enter Address That You Want to Watch"
		else -> ""
	}

	@JvmField
	val intro = when (currentLanguage) {
		HoneyLanguage.English.code -> "You are convinced your brain is working at peak efficiency today, yet you wonder why you continue to run into obstacles that you"
		HoneyLanguage.Chinese.code -> "You are convinced your brain is working at peak efficiency today, yet you wonder why you continue to run into obstacles that you"
		HoneyLanguage.Japanese.code -> "You are convinced your brain is working at peak efficiency today, yet you wonder why you continue to run into obstacles that you"
		HoneyLanguage.Korean.code -> "You are convinced your brain is working at peak efficiency today, yet you wonder why you continue to run into obstacles that you"
		HoneyLanguage.Russian.code -> "You are convinced your brain is working at peak efficiency today, yet you wonder why you continue to run into obstacles that you"
		HoneyLanguage.TraditionalChinese.code -> "You are convinced your brain is working at peak efficiency today, yet you wonder why you continue to run into obstacles that you"
		else -> ""
	}
}

object NotificationText {

	@JvmField
	val notification = when (currentLanguage) {
		HoneyLanguage.English.code -> "Notifications"
		HoneyLanguage.Chinese.code -> "通知中心"
		HoneyLanguage.Japanese.code -> "通知センター"
		HoneyLanguage.Korean.code -> "알림 센터"
		HoneyLanguage.Russian.code -> "Notifications"
		HoneyLanguage.TraditionalChinese.code -> "通知中心"
		else -> ""
	}
}

object TokenManagementText {

	@JvmField
	val addToken = when (currentLanguage) {
		HoneyLanguage.English.code -> "Add Token"
		HoneyLanguage.Chinese.code -> "添加其他币种"
		HoneyLanguage.Japanese.code -> "トークンを追加"
		HoneyLanguage.Korean.code -> "토큰 추가"
		HoneyLanguage.Russian.code -> "Добавить токен"
		HoneyLanguage.TraditionalChinese.code -> "添加其他幣種"
		else -> ""
	}
}

object Alert {

	@JvmField
	val selectCurrency = when (currentLanguage) {
		HoneyLanguage.English.code -> "Once you've selected this, you'll need to wait a moment while we restart the app. Are you sure you'd like to switch currency settings?"
		HoneyLanguage.Chinese.code -> "一旦你选择切换货币，应用程序将被重新启动，并等待几秒钟。 你确定要切换货币设置吗？"
		HoneyLanguage.Japanese.code -> "通貨の切り替えを選択すると、アプリケーションが再起動され、数秒待つことになります。 通貨設定を変更してもよろしいですか？"
		HoneyLanguage.Korean.code -> "통화 전환을 선택하면 응용 프로그램이 다시 시작되고 몇 초 기다립니다. 통화 설정을 전환 하시겠습니까?"
		HoneyLanguage.Russian.code -> "После того, как вы его выбрали, приложение будет перезагружено и просто подождите несколько секунд. Правильно ли вы переключаете настройки валют?"
		HoneyLanguage.TraditionalChinese.code -> "一旦你選擇切換貨幣，應用程序將被重新啟動，並等待幾秒鐘。你確定要切換貨幣設置嗎？"
		else -> ""
	}
}

object WalletSettingsText {

	@JvmField
	val copy = when (currentLanguage) {
		HoneyLanguage.English.code -> "Click to Copy Address"
		HoneyLanguage.Chinese.code -> "点击复制地址"
		HoneyLanguage.Japanese.code -> "アドレスをコピーする"
		HoneyLanguage.Korean.code -> "지갑 주소 복사를 클릭하십시오"
		HoneyLanguage.Russian.code -> "Копировать адрес"
		HoneyLanguage.TraditionalChinese.code -> "點擊複製地址"
		else -> ""
	}

	@JvmField
	val checkQRCode = when (currentLanguage) {
		HoneyLanguage.English.code -> "Check QR Code"
		HoneyLanguage.Chinese.code -> "查看二维码"
		HoneyLanguage.Japanese.code -> "QRコードをチェックする"
		HoneyLanguage.Korean.code -> "QR 코드 확인"
		HoneyLanguage.Russian.code -> "Проверить QR-код"
		HoneyLanguage.TraditionalChinese.code -> "查看二維碼"
		else -> ""
	}

	@JvmField
	val balance = when (currentLanguage) {
		HoneyLanguage.English.code -> "Balance"
		HoneyLanguage.Chinese.code -> "余额"
		HoneyLanguage.Japanese.code -> "残高"
		HoneyLanguage.Korean.code -> "균형"
		HoneyLanguage.Russian.code -> "Balance"
		HoneyLanguage.TraditionalChinese.code -> "余额"
		else -> ""
	}

	@JvmField
	val walletName = when (currentLanguage) {
		HoneyLanguage.English.code -> "Wallet Name"
		HoneyLanguage.Chinese.code -> "钱包名称"
		HoneyLanguage.Japanese.code -> "ウォレット名"
		HoneyLanguage.Korean.code -> "월렛 이름"
		HoneyLanguage.Russian.code -> "Название кошелька"
		HoneyLanguage.TraditionalChinese.code -> "錢包名稱"
		else -> ""
	}

	@JvmField
	val walletNameSettings = when (currentLanguage) {
		HoneyLanguage.English.code -> "Name Your Wallet"
		HoneyLanguage.Chinese.code -> "钱包名称设置"
		HoneyLanguage.Japanese.code -> "ウォレット設定"
		HoneyLanguage.Korean.code -> "월렛 이름 설정"
		HoneyLanguage.Russian.code -> "Настройка имени кошелька"
		HoneyLanguage.TraditionalChinese.code -> "錢包名稱設置"
		else -> ""
	}

	@JvmField
	val walletSettings = when (currentLanguage) {
		HoneyLanguage.English.code -> "Wallet Settings"
		HoneyLanguage.Chinese.code -> "钱包设置"
		HoneyLanguage.Japanese.code -> "ウォレット設定"
		HoneyLanguage.Korean.code -> "월렛 설정"
		HoneyLanguage.Russian.code -> "Настройки кошелька"
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
		HoneyLanguage.Japanese.code -> "パスワードのヒント"
		HoneyLanguage.Korean.code -> "비밀번호 힌트"
		HoneyLanguage.Russian.code -> "Изменить пароль"
		HoneyLanguage.TraditionalChinese.code -> "密碼提示"
		else -> ""
	}

	@JvmField
	val hintAlert = when (currentLanguage) {
		HoneyLanguage.English.code -> "It is empty please enter some word"
		HoneyLanguage.Chinese.code -> "It is empty please enter some word"
		HoneyLanguage.Japanese.code -> "It is empty please enter some word"
		HoneyLanguage.Korean.code -> "It is empty please enter some word"
		HoneyLanguage.Russian.code -> "It is empty please enter some word"
		HoneyLanguage.TraditionalChinese.code -> "It is empty please enter some word"
		else -> ""
	}

	@JvmField
	val exportPrivateKey = when (currentLanguage) {
		HoneyLanguage.English.code -> "Export Private Key"
		HoneyLanguage.Chinese.code -> "导出私钥"
		HoneyLanguage.Japanese.code -> "秘密鍵のエクスポート"
		HoneyLanguage.Korean.code -> "개인 키 내보내기"
		HoneyLanguage.Russian.code -> "Экспорт секретного ключа"
		HoneyLanguage.TraditionalChinese.code -> "導出金鑰"
		else -> ""
	}

	@JvmField
	val exportKeystore = when (currentLanguage) {
		HoneyLanguage.English.code -> "Export Keystore"
		HoneyLanguage.Chinese.code -> "导出keystore"
		HoneyLanguage.Japanese.code -> "キーストアのエクスポート"
		HoneyLanguage.Korean.code -> "키 스토어 내보내기"
		HoneyLanguage.Russian.code -> "Экспортный Keystore"
		HoneyLanguage.TraditionalChinese.code -> "導出 Keystore"
		else -> ""
	}

	@JvmField
	val backUpMnemonic = when (currentLanguage) {
		HoneyLanguage.English.code -> "Back Up Your Mnemonic"
		HoneyLanguage.Chinese.code -> "Back Up Your Mnemonic"
		HoneyLanguage.Japanese.code -> "Back Up Your Mnemonic"
		HoneyLanguage.Korean.code -> "Back Up Your Mnemonic"
		HoneyLanguage.Russian.code -> "Back Up Your Mnemonic"
		HoneyLanguage.TraditionalChinese.code -> "Back Up Your Mnemonic"
		else -> ""
	}

	@JvmField
	val backUpMnemonicGotBefore = when (currentLanguage) {
		HoneyLanguage.English.code -> "confirm mnemonic which you got before."
		HoneyLanguage.Chinese.code -> "按顺序点选您抄写下的助记词"
		HoneyLanguage.Japanese.code -> "confirm mnemonic which you got before."
		HoneyLanguage.Korean.code -> "confirm mnemonic which you got before."
		HoneyLanguage.Russian.code -> "confirm mnemonic which you got before."
		HoneyLanguage.TraditionalChinese.code -> "confirm mnemonic which you got before."
		else -> ""
	}

	@JvmField
	val safeAttention = when (currentLanguage) {
		HoneyLanguage.English.code -> "Safety Alert"
		HoneyLanguage.Chinese.code -> "安全提示"
		HoneyLanguage.Japanese.code -> "Safe Attention"
		HoneyLanguage.Korean.code -> "Safe Attention"
		HoneyLanguage.Russian.code -> "Safe Attention"
		HoneyLanguage.TraditionalChinese.code -> "Safe Attention"
		else -> ""
	}

	@JvmField
	val delete = when (currentLanguage) {
		HoneyLanguage.English.code -> "Delete Wallet"
		HoneyLanguage.Chinese.code -> "删除钱包"
		HoneyLanguage.Japanese.code -> "ウォレットを削除"
		HoneyLanguage.Korean.code -> "지갑 지우기"
		HoneyLanguage.Russian.code -> "Удалить кошелек"
		HoneyLanguage.TraditionalChinese.code -> "刪除錢包"
		else -> ""
	}

	@JvmField
	val deleteInfoTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Are you sure you want to delete the current wallet? Be sure you have backed it up!"
		HoneyLanguage.Chinese.code -> "确认要删除钱包吗？(删除前请确保已妥善备份)"
		HoneyLanguage.Japanese.code -> "ウォレットを削除してもよろしいですか？ （削除する前に必ずバックアップしてください）"
		HoneyLanguage.Korean.code -> "지갑을 삭제 하시겠습니까? (삭제하기 전에 반드시 백업 해 두십시오)"
		HoneyLanguage.Russian.code -> "Вы действительно хотите удалить кошелек? (Обязательно создайте резервную копию перед удалением)"
		HoneyLanguage.TraditionalChinese.code -> "確認要刪除錢包嗎？ (刪除前請確保已妥善備份)"
		else -> ""
	}

	@JvmField
	val deleteInfoSubtitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Before deleting your wallet, please back up its information (private key, keystore, mnemonics). We never save your data, so we won't be able to recover it."
		HoneyLanguage.Chinese.code -> "在删除您的钱包之前，请备份您的钱包信息，我们绝不会保存您的数据，因此我们无法恢复此操作"
		HoneyLanguage.Japanese.code -> "ウォレットを削除する前に、ウォレットの情報をバックアップしてください。データは保存されないため、この操作を回復することはできません"
		HoneyLanguage.Korean.code -> "지갑을 지우기 전에 지갑 정보를 백업하십시오. 데이터를 저장하지 않으므로이 작업을 복구 할 수 없습니다."
		HoneyLanguage.Russian.code -> "Прежде чем удалять свой кошелек, пожалуйста, создайте резервную копию информации о кошельке, мы никогда не сохраняем ваши данные, поэтому мы не можем восстановить эту операцию"
		HoneyLanguage.TraditionalChinese.code -> "在刪除您的錢包之前，請備份您的錢包信息，我們絕不會保存您的數據，因此我們無法恢復此操作"
		else -> ""
	}

	@JvmField
	val oldPassword = when (currentLanguage) {
		HoneyLanguage.English.code -> "Old Password"
		HoneyLanguage.Chinese.code -> "旧密码"
		HoneyLanguage.Japanese.code -> "古いパスワード"
		HoneyLanguage.Korean.code -> "이전 암호"
		HoneyLanguage.Russian.code -> "Старый пароль"
		HoneyLanguage.TraditionalChinese.code -> "舊密碼"
		else -> ""
	}

	@JvmField
	val newPassword = when (currentLanguage) {
		HoneyLanguage.English.code -> "New Password"
		HoneyLanguage.Chinese.code -> "新密码"
		HoneyLanguage.Japanese.code -> "新しいパスワード"
		HoneyLanguage.Korean.code -> "새 암호"
		HoneyLanguage.Russian.code -> "Новый пароль"
		HoneyLanguage.TraditionalChinese.code -> "新密碼"
		else -> ""
	}

	@JvmField
	val emptyNameAleryt = when (currentLanguage) {
		HoneyLanguage.English.code -> "The wallet name is empty"
		HoneyLanguage.Chinese.code -> "Please input a wallet name"
		HoneyLanguage.Japanese.code -> "Please input a wallet name"
		HoneyLanguage.Korean.code -> "Please input a wallet name"
		HoneyLanguage.Russian.code -> "Please input a wallet name"
		HoneyLanguage.TraditionalChinese.code -> "Please input a wallet name"
		else -> ""
	}
}

object ProfileText {

	@JvmField
	val profile = when (currentLanguage) {
		HoneyLanguage.English.code -> "Profile"
		HoneyLanguage.Chinese.code -> "个人主页"
		HoneyLanguage.Japanese.code -> "プロフィール"
		HoneyLanguage.Korean.code -> "프로필"
		HoneyLanguage.Russian.code -> "профиль"
		HoneyLanguage.TraditionalChinese.code -> "個人檔案"
		else -> ""
	}

	@JvmField
	val contacts = when (currentLanguage) {
		HoneyLanguage.English.code -> "Contacts"
		HoneyLanguage.Chinese.code -> "通讯录"
		HoneyLanguage.Japanese.code -> "連絡先"
		HoneyLanguage.Korean.code -> "연락처"
		HoneyLanguage.Russian.code -> "контакты"
		HoneyLanguage.TraditionalChinese.code -> "聯繫人"
		else -> ""
	}

	@JvmField
	val contactsInput = when (currentLanguage) {
		HoneyLanguage.English.code -> "Add Contact"
		HoneyLanguage.Chinese.code -> "添加联系人"
		HoneyLanguage.Japanese.code -> "連絡先を追加"
		HoneyLanguage.Korean.code -> "연락처 추가"
		HoneyLanguage.Russian.code -> "Добавить контакт"
		HoneyLanguage.TraditionalChinese.code -> "添加聯繫人"
		else -> ""
	}

	@JvmField
	val currency = when (currentLanguage) {
		HoneyLanguage.English.code -> "Currency Settings"
		HoneyLanguage.Chinese.code -> "货币"
		HoneyLanguage.Japanese.code -> "自国通貨"
		HoneyLanguage.Korean.code -> "통화 설정"
		HoneyLanguage.Russian.code -> "Настройки валюты"
		HoneyLanguage.TraditionalChinese.code -> "貨幣"
		else -> ""
	}

	@JvmField
	val hint = when (currentLanguage) {
		HoneyLanguage.English.code -> "Password Hint"
		HoneyLanguage.Chinese.code -> "密码提示"
		HoneyLanguage.Japanese.code -> "パスワードのヒント"
		HoneyLanguage.Korean.code -> "비밀번호 힌트"
		HoneyLanguage.Russian.code -> "Изменить пароль"
		HoneyLanguage.TraditionalChinese.code -> "密碼提示"
		else -> ""
	}

	@JvmField
	val language = when (currentLanguage) {
		HoneyLanguage.English.code -> "Language"
		HoneyLanguage.Chinese.code -> "语言"
		HoneyLanguage.Japanese.code -> "言語"
		HoneyLanguage.Korean.code -> "언어"
		HoneyLanguage.Russian.code -> "язык"
		HoneyLanguage.TraditionalChinese.code -> "語言"
		else -> ""
	}

	@JvmField
	val aboutUs = when (currentLanguage) {
		HoneyLanguage.English.code -> "About Us"
		HoneyLanguage.Chinese.code -> "关于我们"
		HoneyLanguage.Japanese.code -> "チーム紹介"
		HoneyLanguage.Korean.code -> "회사 소개"
		HoneyLanguage.Russian.code -> "О КОМПАНИИ"
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}

	@JvmField
	val support = when (currentLanguage) {
		HoneyLanguage.English.code -> "Support"
		HoneyLanguage.Chinese.code -> "帮助中心"
		HoneyLanguage.Japanese.code -> "Support"
		HoneyLanguage.Korean.code -> "Support"
		HoneyLanguage.Russian.code -> "Support"
		HoneyLanguage.TraditionalChinese.code -> "Support"
		else -> ""
	}

	@JvmField
	val privacy = when (currentLanguage) {
		HoneyLanguage.English.code -> "Privacy Policy"
		HoneyLanguage.Chinese.code -> "隐私条款"
		HoneyLanguage.Japanese.code -> "Privacy Policy"
		HoneyLanguage.Korean.code -> "Privacy Policy"
		HoneyLanguage.Russian.code -> "Privacy Policy"
		HoneyLanguage.TraditionalChinese.code -> "Privacy Policy"
		else -> ""
	}

	@JvmField
	val terms = when (currentLanguage) {
		HoneyLanguage.English.code -> "User Agreement"
		HoneyLanguage.Chinese.code -> "用户协议"
		HoneyLanguage.Japanese.code -> "Terms & Conditions"
		HoneyLanguage.Korean.code -> "Terms & Conditions"
		HoneyLanguage.Russian.code -> "Terms & Conditions"
		HoneyLanguage.TraditionalChinese.code -> "Terms & Conditions"
		else -> ""
	}

	@JvmField
	val version = when (currentLanguage) {
		HoneyLanguage.English.code -> "Version"
		HoneyLanguage.Chinese.code -> "软件版本"
		HoneyLanguage.Japanese.code -> "Version"
		HoneyLanguage.Korean.code -> "Version"
		HoneyLanguage.Russian.code -> "Version"
		HoneyLanguage.TraditionalChinese.code -> "Version"
		else -> ""
	}

	@JvmField
	val shareApp = when (currentLanguage) {
		HoneyLanguage.English.code -> "Share Application"
		HoneyLanguage.Chinese.code -> "分享Goldstone"
		HoneyLanguage.Japanese.code -> "Share Application"
		HoneyLanguage.Korean.code -> "Share Application"
		HoneyLanguage.Russian.code -> "Share Application"
		HoneyLanguage.TraditionalChinese.code -> "Share Application"
		else -> ""
	}

	@JvmField
	val pinCode = when (currentLanguage) {
		HoneyLanguage.English.code -> "Pin Code"
		HoneyLanguage.Chinese.code -> "Pin 码"
		HoneyLanguage.Japanese.code -> "ピンコード"
		HoneyLanguage.Korean.code -> "핀 코드"
		HoneyLanguage.Russian.code -> "Контактный код"
		HoneyLanguage.TraditionalChinese.code -> "PIN碼"
		else -> ""
	}

	@JvmField
	val chain = when (currentLanguage) {
		HoneyLanguage.English.code -> "Select Chain Node"
		HoneyLanguage.Chinese.code -> "选择节点"
		HoneyLanguage.Japanese.code -> "Select Chain Node"
		HoneyLanguage.Korean.code -> "Select Chain Node"
		HoneyLanguage.Russian.code -> "Select Chain Node"
		HoneyLanguage.TraditionalChinese.code -> "Select Chain Node"
		else -> ""
	}
}

object EmptyText {

	@JvmField
	val tokenDetailTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "No token transactions found"
		HoneyLanguage.Chinese.code -> "还没有任何交易记录"
		HoneyLanguage.Japanese.code -> "トランザクション履歴はまだありません"
		HoneyLanguage.Korean.code -> "토큰 트랜잭션 없음"
		HoneyLanguage.Russian.code -> "Не найдено никаких транзакций токена"
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}

	@JvmField
	val tokenDetailSubtitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "No transaction history for this token"
		HoneyLanguage.Chinese.code -> "在区块链上没有交易，所以您没有图表和记录信息。"
		HoneyLanguage.Japanese.code -> "ブロックチェーンにはトランザクションはなく、チャートやレコードはありません。"
		HoneyLanguage.Korean.code -> "블록 체인에는 트랜잭션이 없으므로 차트 및 레코드가 없습니다"
		HoneyLanguage.Russian.code -> "В блокчейне нет транзакций, поэтому у вас нет диаграмм и записей"
		HoneyLanguage.TraditionalChinese.code -> "區塊鏈中沒有交易，所以您沒有圖表和記錄"
		else -> ""
	}

	@JvmField
	val searchTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Token not found"
		HoneyLanguage.Chinese.code -> "没有找到这个Token"
		HoneyLanguage.Japanese.code -> "トークンが見つかりません"
		HoneyLanguage.Korean.code -> "토큰을 찾을 수 없음"
		HoneyLanguage.Russian.code -> "Токен не найден"
		HoneyLanguage.TraditionalChinese.code -> "沒有找到這個Token"
		else -> ""
	}

	@JvmField
	val searchSubtitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "You have not added any trading pairs yet. Please click on the upper left button to search for and add real-time"
		HoneyLanguage.Chinese.code -> "你还没有添加交易对，点击界面左上角添加市场里的token，可以看实时行情"
		HoneyLanguage.Japanese.code -> "インタフェースの左上隅をクリックしてマーケット内のtokenを追加することで、リアルタイムの市場価格を見ることが出来ます。"
		HoneyLanguage.Korean.code -> "아직 거래 내역이 없습니다"
		HoneyLanguage.Russian.code -> "Соответствующий токен не найден"
		HoneyLanguage.TraditionalChinese.code -> "你还没有添加交易对，点击界面左上角添加市场里的token，可以看实时行情"
		else -> ""
	}

	@JvmField
	val contractTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Empty Contact List"
		HoneyLanguage.Chinese.code -> "通讯录里空空的"
		HoneyLanguage.Japanese.code -> "まだ連絡がありません"
		HoneyLanguage.Korean.code -> "아직 연락이 없습니다"
		HoneyLanguage.Russian.code -> "Пока нет контакта"
		HoneyLanguage.TraditionalChinese.code -> "通訊簿里沒有記錄"
		else -> ""
	}

	@JvmField
	val contractSubtitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Click the plus sign in the upper-left corner to add a contact address"
		HoneyLanguage.Chinese.code -> "點擊左上角加號，可以添加常用聯繫人的地址"
		HoneyLanguage.Japanese.code -> "左上隅のプラス記号をクリックして、共通の連絡先のアドレスを追加します"
		HoneyLanguage.Korean.code -> "왼쪽 상단의 더하기 기호를 클릭하여 공통 연락처의 주소를 추가하십시오"
		HoneyLanguage.Russian.code -> "Click the plus sign in the upper-left corner to add a contact address"
		HoneyLanguage.TraditionalChinese.code -> "點擊左上角加號，可以添加常用聯繫人的地址"
		else -> ""
	}

	@JvmField
	val currency = when (currentLanguage) {
		HoneyLanguage.English.code -> "Click the plus sign in the upper-left corner to add a contact address"
		HoneyLanguage.Chinese.code -> "通貨の設定"
		HoneyLanguage.Japanese.code -> "自国通貨"
		HoneyLanguage.Korean.code -> "통화 설정"
		HoneyLanguage.Russian.code -> "Настройки валюты"
		HoneyLanguage.TraditionalChinese.code -> "貨幣設置"
		else -> ""
	}

	@JvmField
	val language = when (currentLanguage) {
		HoneyLanguage.English.code -> "Language"
		HoneyLanguage.Chinese.code -> "语言"
		HoneyLanguage.Japanese.code -> "言语"
		HoneyLanguage.Korean.code -> "언어"
		HoneyLanguage.Russian.code -> "язык"
		HoneyLanguage.TraditionalChinese.code -> "語言"
		else -> ""
	}

	@JvmField
	val aboutUs = when (currentLanguage) {
		HoneyLanguage.English.code -> "About Us"
		HoneyLanguage.Chinese.code -> "关于我们"
		HoneyLanguage.Japanese.code -> "チーム情報"
		HoneyLanguage.Korean.code -> "회사 소개"
		HoneyLanguage.Russian.code -> "О КОМПАНИИ"
		HoneyLanguage.TraditionalChinese.code -> "關於我們"
		else -> ""
	}
}

object QuotationText {

	@JvmField
	val market = when (currentLanguage) {
		HoneyLanguage.English.code -> "Markets"
		HoneyLanguage.Chinese.code -> "市场行情"
		HoneyLanguage.Japanese.code -> "通貨相場"
		HoneyLanguage.Korean.code -> "시장 시세"
		HoneyLanguage.Russian.code -> "Рыночные котировки"
		HoneyLanguage.TraditionalChinese.code -> "市場行情"
		else -> ""
	}

	@JvmField
	val management = when (currentLanguage) {
		HoneyLanguage.English.code -> "My Markets"
		HoneyLanguage.Chinese.code -> "自选管理"
		HoneyLanguage.Japanese.code -> "マイグループ"
		HoneyLanguage.Korean.code -> "자동 선거 관리"
		HoneyLanguage.Russian.code -> "Моя котировка"
		HoneyLanguage.TraditionalChinese.code -> "自選管理"
		else -> ""
	}

	@JvmField
	val tokenDetail = when (currentLanguage) {
		HoneyLanguage.English.code -> "Market Token"
		HoneyLanguage.Chinese.code -> "市场交易对"
		HoneyLanguage.Japanese.code -> "市場取引のペア"
		HoneyLanguage.Korean.code -> "시장 거래 쌍"
		HoneyLanguage.Russian.code -> "Значок рынка"
		HoneyLanguage.TraditionalChinese.code -> "市場交易對"
		else -> ""
	}

	@JvmField
	val search = when (currentLanguage) {
		HoneyLanguage.English.code -> "Search pairs"
		HoneyLanguage.Chinese.code -> "搜索"
		HoneyLanguage.Japanese.code -> "検索"
		HoneyLanguage.Korean.code -> "검색"
		HoneyLanguage.Russian.code -> "Search pairs"
		HoneyLanguage.TraditionalChinese.code -> "搜索"
		else -> ""
	}

	@JvmField
	val addToken = when (currentLanguage) {
		HoneyLanguage.English.code -> "Search pairs"
		HoneyLanguage.Chinese.code -> "管理"
		HoneyLanguage.Japanese.code -> "管理"
		HoneyLanguage.Korean.code -> "관리"
		HoneyLanguage.Russian.code -> "управление"
		HoneyLanguage.TraditionalChinese.code -> "管理"
		else -> ""
	}

	@JvmField
	val alarm = when (currentLanguage) {
		HoneyLanguage.English.code -> "Alerts"
		HoneyLanguage.Chinese.code -> "价格提醒"
		HoneyLanguage.Japanese.code -> "リマインダー"
		HoneyLanguage.Korean.code -> "가격 알림"
		HoneyLanguage.Russian.code -> "тревоги"
		HoneyLanguage.TraditionalChinese.code -> "價格提醒"
		else -> ""
	}

	@JvmField
	val currentPrice = when (currentLanguage) {
		HoneyLanguage.English.code -> "Current Price"
		HoneyLanguage.Chinese.code -> "当前价格"
		HoneyLanguage.Japanese.code -> "価格"
		HoneyLanguage.Korean.code -> "가격"
		HoneyLanguage.Russian.code -> "Текущая цена"
		HoneyLanguage.TraditionalChinese.code -> "時價"
		else -> ""
	}

	@JvmField
	val priceHistory = when (currentLanguage) {
		HoneyLanguage.English.code -> "Price History"
		HoneyLanguage.Chinese.code -> "价格历史"
		HoneyLanguage.Japanese.code -> "価格履歴"
		HoneyLanguage.Korean.code -> "가격 이력"
		HoneyLanguage.Russian.code -> "История цен"
		HoneyLanguage.TraditionalChinese.code -> "價格歷史"
		else -> ""
	}

	@JvmField
	val tokenDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "Token Description"
		HoneyLanguage.Chinese.code -> "Token 简介"
		HoneyLanguage.Japanese.code -> "トークンの説明"
		HoneyLanguage.Korean.code -> "토큰 소개"
		HoneyLanguage.Russian.code -> "Информация о токенах"
		HoneyLanguage.TraditionalChinese.code -> "Token 簡介"
		else -> ""
	}

	@JvmField
	val tokenInformation = when (currentLanguage) {
		HoneyLanguage.English.code -> "Token Information"
		HoneyLanguage.Chinese.code -> "Token 信息"
		HoneyLanguage.Japanese.code -> "トークン情報"
		HoneyLanguage.Korean.code -> "토큰 정보"
		HoneyLanguage.Russian.code -> "Token Information"
		HoneyLanguage.TraditionalChinese.code -> "Token 信息"
		else -> ""
	}

	@JvmField
	val tokenDescriptionPlaceHolder = when (currentLanguage) {
		HoneyLanguage.English.code -> "Token Description Content"
		HoneyLanguage.Chinese.code -> "Token 信息内容"
		HoneyLanguage.Japanese.code -> "トークンの説明コンテンツ"
		HoneyLanguage.Korean.code -> "토큰 설명 내용"
		HoneyLanguage.Russian.code -> "Описание токена"
		HoneyLanguage.TraditionalChinese.code -> "Token 信息內容"
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
		HoneyLanguage.Russian.code -> "Контактный код"
		HoneyLanguage.TraditionalChinese.code -> "PIN碼"
		else -> ""
	}

	@JvmField
	val repeat = when (currentLanguage) {
		HoneyLanguage.English.code -> "Repeat PIN"
		HoneyLanguage.Chinese.code -> "重复PIN码"
		HoneyLanguage.Japanese.code -> "PINの繰り返し"
		HoneyLanguage.Korean.code -> "PIN 반복"
		HoneyLanguage.Russian.code -> "Повторить PIN-код"
		HoneyLanguage.TraditionalChinese.code -> "重複PIN碼"
		else -> ""
	}

	@JvmField
	val description = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please set a 4-digit PIN"
		HoneyLanguage.Chinese.code -> "输入四位密码密码"
		HoneyLanguage.Japanese.code -> "4桁のパスワードを入力してください"
		HoneyLanguage.Korean.code -> "4 자리 암호를 입력하십시오."
		HoneyLanguage.Russian.code -> "Введите четыре битовых шифра кода"
		HoneyLanguage.TraditionalChinese.code -> "輸入四位密碼密碼"
		else -> ""
	}

	@JvmField
	val countAlert = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please Enter four bit ciphers"
		HoneyLanguage.Chinese.code -> "Please Enter four bit ciphers"
		HoneyLanguage.Japanese.code -> "Please Enter four bit ciphers"
		HoneyLanguage.Korean.code -> "Please Enter four bit ciphers"
		HoneyLanguage.Russian.code -> "Please Enter four bit ciphers"
		HoneyLanguage.TraditionalChinese.code -> "Please Enter four bit ciphers"
		else -> ""
	}

	@JvmField
	val verifyAlert = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please repeat the same PIN"
		HoneyLanguage.Chinese.code -> "Please repeat the same PIN"
		HoneyLanguage.Japanese.code -> "Please repeat the same PIN"
		HoneyLanguage.Korean.code -> "Please repeat the same PIN"
		HoneyLanguage.Russian.code -> "Please repeat the same PIN"
		HoneyLanguage.TraditionalChinese.code -> "Please repeat the same PIN"
		else -> ""
	}

	@JvmField
	val turnOnAttention = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please repeat the same PIN"
		HoneyLanguage.Chinese.code -> "Please repeat the same PIN"
		HoneyLanguage.Japanese.code -> "please set your pin code on below"
		HoneyLanguage.Korean.code -> "please set your pin code on below"
		HoneyLanguage.Russian.code -> "please set your pin code on below"
		HoneyLanguage.TraditionalChinese.code -> "please set your pin code on below"
		else -> ""
	}

	@JvmField
	val show = when (currentLanguage) {
		HoneyLanguage.English.code -> "Show PIN"
		HoneyLanguage.Chinese.code -> "显示PIN码"
		HoneyLanguage.Japanese.code -> "PINを表示する"
		HoneyLanguage.Korean.code -> "PIN 표시"
		HoneyLanguage.Russian.code -> "Показать PIN-код"
		HoneyLanguage.TraditionalChinese.code -> "顯示PIN碼"
		else -> ""
	}

	@JvmField
	val enterPincode = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter Passcode"
		HoneyLanguage.Chinese.code -> "输入Passcode"
		HoneyLanguage.Japanese.code -> "Enter Passcode"
		HoneyLanguage.Korean.code -> "Enter Passcode"
		HoneyLanguage.Russian.code -> "Enter Passcode"
		HoneyLanguage.TraditionalChinese.code -> "Enter Passcode"
		else -> ""
	}

	@JvmField
	val enterPincodeDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "Set the PIN to protect privacy. Once you open GoldStone, you need to enter PIN to see your wallet "
		HoneyLanguage.Chinese.code -> "设置锁屏密码保护隐私，一旦开启锁屏密码，每次打开GoldStone时需要输入锁屏密码才能查看钱包"
		HoneyLanguage.Japanese.code -> "passcode to prote"
		HoneyLanguage.Korean.code -> "passcode to prote"
		HoneyLanguage.Russian.code -> "passcode to prote"
		HoneyLanguage.TraditionalChinese.code -> "passcode to prote"
		else -> ""
	}
}

object PrepareTransferText {

	@JvmField
	val memoInformation = when (currentLanguage) {
		HoneyLanguage.English.code -> "Memo Information"
		HoneyLanguage.Chinese.code -> "备注信息"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}

	@JvmField
	val memo = when (currentLanguage) {
		HoneyLanguage.English.code -> "Memo"
		HoneyLanguage.Chinese.code -> "备注"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}

	@JvmField
	val addAMemo = when (currentLanguage) {
		HoneyLanguage.English.code -> "Add a Memo"
		HoneyLanguage.Chinese.code -> "添加备注"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}

	@JvmField
	val price = when (currentLanguage) {
		HoneyLanguage.English.code -> "UNIT PRICE"
		HoneyLanguage.Chinese.code -> "单价"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}

	@JvmField
	val currentPrice = when (currentLanguage) {
		HoneyLanguage.English.code -> "Current Price"
		HoneyLanguage.Chinese.code -> "当前价"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}

	@JvmField
	val accountInfo = when (currentLanguage) {
		HoneyLanguage.English.code -> "Account Information"
		HoneyLanguage.Chinese.code -> "账户信息"
		HoneyLanguage.Japanese.code -> "Account Information"
		HoneyLanguage.Korean.code -> "Account Information"
		HoneyLanguage.Russian.code -> "Account Information"
		HoneyLanguage.TraditionalChinese.code -> "Account Information"
		else -> ""
	}

	@JvmField
	val willSpending = when (currentLanguage) {
		HoneyLanguage.English.code -> "WILL SPEND"
		HoneyLanguage.Chinese.code -> "预计花费"
		HoneyLanguage.Japanese.code -> "WILL SPENDING"
		HoneyLanguage.Korean.code -> "WILL SPENDING"
		HoneyLanguage.Russian.code -> "WILL SPENDING"
		HoneyLanguage.TraditionalChinese.code -> "WILL SPENDING"
		else -> ""
	}

	@JvmField
	val send = when (currentLanguage) {
		HoneyLanguage.English.code -> "SEND TO"
		HoneyLanguage.Chinese.code -> "发送至"
		HoneyLanguage.Japanese.code -> "SEND TO"
		HoneyLanguage.Korean.code -> "SEND TO"
		HoneyLanguage.Russian.code -> "SEND TO"
		HoneyLanguage.TraditionalChinese.code -> "SEND TO"
		else -> ""
	}

	@JvmField
	val from = when (currentLanguage) {
		HoneyLanguage.English.code -> "FROM"
		HoneyLanguage.Chinese.code -> "发送者"
		HoneyLanguage.Japanese.code -> "FROM"
		HoneyLanguage.Korean.code -> "FROM"
		HoneyLanguage.Russian.code -> "FROM"
		HoneyLanguage.TraditionalChinese.code -> "FROM"
		else -> ""
	}
}

object ContactText {

	@JvmField
	val emptyNameAlert = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please enter a contact name"
		HoneyLanguage.Chinese.code -> "请填写联系人名称"
		HoneyLanguage.Japanese.code -> "Please enter a contact name"
		HoneyLanguage.Korean.code -> "Please enter a contact name"
		HoneyLanguage.Russian.code -> "Please enter a contact name"
		HoneyLanguage.TraditionalChinese.code -> "Please enter a contact name"
		else -> ""
	}

	@JvmField
	val emptyAddressAlert = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please enter a wallet address"
		HoneyLanguage.Chinese.code -> "请填写联系人钱包地址"
		HoneyLanguage.Japanese.code -> "You must enter a wallet address"
		HoneyLanguage.Korean.code -> "You must enter a wallet address"
		HoneyLanguage.Russian.code -> "You must enter a wallet address"
		HoneyLanguage.TraditionalChinese.code -> "You must enter a wallet address"
		else -> ""
	}

	@JvmField
	val wrongAddressFormat = when (currentLanguage) {
		HoneyLanguage.English.code -> "Incorrect wallet address format"
		HoneyLanguage.Chinese.code -> "钱包地址格式错误"
		HoneyLanguage.Japanese.code -> "Incorrect wallet address format"
		HoneyLanguage.Korean.code -> "Incorrect wallet address format"
		HoneyLanguage.Russian.code -> "Incorrect wallet address format"
		HoneyLanguage.TraditionalChinese.code -> "Incorrect wallet address format"
		else -> ""
	}

	@JvmField
	val contactName = when (currentLanguage) {
		HoneyLanguage.English.code -> "Contact Name"
		HoneyLanguage.Chinese.code -> "联系人名称"
		HoneyLanguage.Japanese.code -> "Contact Name"
		HoneyLanguage.Korean.code -> "Contact Name"
		HoneyLanguage.Russian.code -> "Contact Name"
		HoneyLanguage.TraditionalChinese.code -> "Contact Name"
		else -> ""
	}

	@JvmField
	val hint = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter address that you want to store"
		HoneyLanguage.Chinese.code -> "您希望保存的钱包地址"
		HoneyLanguage.Japanese.code -> "Enter address that you want to store"
		HoneyLanguage.Korean.code -> "Enter address that you want to store"
		HoneyLanguage.Russian.code -> "Enter address that you want to store"
		HoneyLanguage.TraditionalChinese.code -> "Enter address that you want to store"
		else -> ""
	}
}

object ChainText {

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
	val ropstan = when (currentLanguage) {
		HoneyLanguage.English.code -> "Ropstan Testnet"
		HoneyLanguage.Chinese.code -> "Ropstan Testnet"
		HoneyLanguage.Japanese.code -> "Ropstan Testnet"
		HoneyLanguage.Korean.code -> "Ropstan Testnet"
		HoneyLanguage.Russian.code -> "Ropstan Testnet"
		HoneyLanguage.TraditionalChinese.code -> "Ropstan Testnet"
		else -> ""
	}

	@JvmField
	val koven = when (currentLanguage) {
		HoneyLanguage.English.code -> "Koven Testnet"
		HoneyLanguage.Chinese.code -> "Koven 测试网络"
		HoneyLanguage.Japanese.code -> "Koven Testnet"
		HoneyLanguage.Korean.code -> "Koven Testnet"
		HoneyLanguage.Russian.code -> "Koven Testnet"
		HoneyLanguage.TraditionalChinese.code -> "Koven Testnet"
		else -> ""
	}

	@JvmField
	val rinkeby = when (currentLanguage) {
		HoneyLanguage.English.code -> "Rinkeby Testnet"
		HoneyLanguage.Chinese.code -> "Rinkeby 测试网络"
		HoneyLanguage.Japanese.code -> "Rinkeby Testnet"
		HoneyLanguage.Korean.code -> "Rinkeby Testnet"
		HoneyLanguage.Russian.code -> "Rinkeby Testnet"
		HoneyLanguage.TraditionalChinese.code -> "Rinkeby Testnet"
		else -> ""
	}
}

object LoadingText {

	@JvmField
	val searchingQuotation = when (currentLanguage) {
		HoneyLanguage.English.code -> "Searching token information..."
		HoneyLanguage.Chinese.code -> "搜索Token行情"
		HoneyLanguage.Japanese.code -> "Searching token information now"
		HoneyLanguage.Korean.code -> "Searching token information now"
		HoneyLanguage.Russian.code -> "Searching token information now"
		HoneyLanguage.TraditionalChinese.code -> "Searching token information now"
		else -> ""
	}

	@JvmField
	val searchingToken = when (currentLanguage) {
		HoneyLanguage.English.code -> "Searching token information now"
		HoneyLanguage.Chinese.code -> "搜索token"
		HoneyLanguage.Japanese.code -> "Searching token information now"
		HoneyLanguage.Korean.code -> "Searching token information now"
		HoneyLanguage.Russian.code -> "Searching token information now"
		HoneyLanguage.TraditionalChinese.code -> "Searching token information now"
		else -> ""
	}

	@JvmField
	val transactionData = when (currentLanguage) {
		HoneyLanguage.English.code -> "Loading transaction data..."
		HoneyLanguage.Chinese.code -> "正在加载转账记录"
		HoneyLanguage.Japanese.code -> "Loading transaction data now"
		HoneyLanguage.Korean.code -> "Loading transaction data now"
		HoneyLanguage.Russian.code -> "Loading transaction data now"
		HoneyLanguage.TraditionalChinese.code -> "Loading transaction data now"
		else -> ""
	}

	// TokenDetailFragment 的 `Loading` 提示
	@JvmField
	val tokenData = when (currentLanguage) {
		HoneyLanguage.English.code -> "Loading token data..."
		HoneyLanguage.Chinese.code -> "正在加载token信息"
		HoneyLanguage.Japanese.code -> "Loading token data now"
		HoneyLanguage.Korean.code -> "Loading token data now"
		HoneyLanguage.Russian.code -> "Loading token data now"
		HoneyLanguage.TraditionalChinese.code -> "Loading token data now"
		else -> ""
	}

	@JvmField
	val notificationData = when (currentLanguage) {
		HoneyLanguage.English.code -> "loading notifications..."
		HoneyLanguage.Chinese.code -> "正在加载通知信息"
		HoneyLanguage.Japanese.code -> "loading notifications..."
		HoneyLanguage.Korean.code -> "loading notifications..."
		HoneyLanguage.Russian.code -> "loading notifications..."
		HoneyLanguage.TraditionalChinese.code -> "loading notifications..."
		else -> ""
	}
}

object QRText {

	@JvmField
	val savedAttention = when (currentLanguage) {
		HoneyLanguage.English.code -> "QR code has been saved to device photos"
		HoneyLanguage.Chinese.code -> "二维码已保存至相册"
		HoneyLanguage.Japanese.code -> "QR code image has saved to album"
		HoneyLanguage.Korean.code -> "QR code image has saved to album"
		HoneyLanguage.Russian.code -> "QR code image has saved to album"
		HoneyLanguage.TraditionalChinese.code -> "QR code image has saved to album"
		else -> ""
	}

	@JvmField
	val shareQRTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "SHARE QR IMAGE"
		HoneyLanguage.Chinese.code -> "分享二维码"
		HoneyLanguage.Japanese.code -> "Share QR Image"
		HoneyLanguage.Korean.code -> "Share QR Image"
		HoneyLanguage.Russian.code -> "Share QR Image"
		HoneyLanguage.TraditionalChinese.code -> "Share QR Image"
		else -> ""
	}


	@JvmField
	val screenText = when (currentLanguage) {
		HoneyLanguage.English.code -> "Scan QR Code GoldStone"
		HoneyLanguage.Chinese.code -> "扫描Goldstone的二维码"
		HoneyLanguage.Japanese.code -> "Scan QR Code GoldStone"
		HoneyLanguage.Korean.code -> "Scan QR Code GoldStone"
		HoneyLanguage.Russian.code -> "Scan QR Code GoldStone"
		HoneyLanguage.TraditionalChinese.code -> "Scan QR Code GoldStone"
		else -> ""
	}

	@JvmField
	val unvalidQRCodeAlert = when (currentLanguage) {
		HoneyLanguage.English.code -> "Invalid QR code"
		HoneyLanguage.Chinese.code -> "未识别到有效的二维码图片"
		HoneyLanguage.Japanese.code -> "Not valid QR code image"
		HoneyLanguage.Korean.code -> "Not valid QR code image"
		HoneyLanguage.Russian.code -> "Not valid QR code image"
		HoneyLanguage.TraditionalChinese.code -> "Not valid QR code image"
		else -> ""
	}

	// 扫描的 `QR Code` 的 `Contract Address` 不是进入的当前的 `Token Contract`
	@JvmField
	val unvalidContract = when (currentLanguage) {
		HoneyLanguage.English.code -> "Inconsistent currency. The QR code scanned is not that of the current token, please change the transfer token, or change the scanned QR code."
		HoneyLanguage.Chinese.code -> "货币不一致。您所扫描的不是当前Token的二维码，请您更换token进行转账，或者更换扫描的二维码。"
		HoneyLanguage.Japanese.code -> "The Token which got by scanning QR code is different with current token please check"
		HoneyLanguage.Korean.code -> "The Token which got by scanning QR code is different with current token please check"
		HoneyLanguage.Russian.code -> "The Token which got by scanning QR code is different with current token please check"
		HoneyLanguage.TraditionalChinese.code -> "The Token which got by scanning QR code is different with current token please check"
		else -> ""
	}
}

object QAText {

	@JvmField
	val whatIsMnemonic = when (currentLanguage) {
		HoneyLanguage.English.code -> "What is mnemonic?"
		HoneyLanguage.Chinese.code -> "什么是助记词？"
		HoneyLanguage.Japanese.code -> "What is mnemonic?"
		HoneyLanguage.Korean.code -> "What is mnemonic?"
		HoneyLanguage.Russian.code -> "What is mnemonic?"
		HoneyLanguage.TraditionalChinese.code -> "What is mnemonic?"
		else -> ""
	}

	@JvmField
	val whatIsKeystore = when (currentLanguage) {
		HoneyLanguage.English.code -> "What is a keystore?"
		HoneyLanguage.Chinese.code -> "什么是 keystore?"
		HoneyLanguage.Japanese.code -> "What is a keystore?"
		HoneyLanguage.Korean.code -> "What is a keystore?"
		HoneyLanguage.Russian.code -> "What is a keystore?"
		HoneyLanguage.TraditionalChinese.code -> "What is a keystore?"
		else -> ""
	}

	@JvmField
	val whatIsWatchOnlyWallet = when (currentLanguage) {
		HoneyLanguage.English.code -> "What is a watch-only wallet?"
		HoneyLanguage.Chinese.code -> "什么是观察钱包？"
		HoneyLanguage.Japanese.code -> "What is a watch-only wallet?"
		HoneyLanguage.Korean.code -> "What is a watch-only wallet?"
		HoneyLanguage.Russian.code -> "What is a watch-only wallet?"
		HoneyLanguage.TraditionalChinese.code -> "What is a watch-only wallet?"
		else -> ""
	}

	@JvmField
	val whatIsPrivateKey = when (currentLanguage) {
		HoneyLanguage.English.code -> "What is a private key?"
		HoneyLanguage.Chinese.code -> "什么是私钥？"
		HoneyLanguage.Japanese.code -> "What is a private key?"
		HoneyLanguage.Korean.code -> "What is a private key?"
		HoneyLanguage.Russian.code -> "What is a private key?"
		HoneyLanguage.TraditionalChinese.code -> "What is a private key?"
		else -> ""
	}
}

object ImporMneubar {

	@JvmField
	val mnemonic = when (currentLanguage) {
		HoneyLanguage.English.code -> "Mnemonic"
		HoneyLanguage.Chinese.code -> "助记词"
		HoneyLanguage.Japanese.code -> "mnemonic"
		HoneyLanguage.Korean.code -> "mnemonic"
		HoneyLanguage.Russian.code -> "mnemonic"
		HoneyLanguage.TraditionalChinese.code -> "mnemonic"
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
		HoneyLanguage.Chinese.code -> "Private Key"
		HoneyLanguage.Japanese.code -> "Private Key"
		HoneyLanguage.Korean.code -> "Private Key"
		HoneyLanguage.Russian.code -> "Private Key"
		HoneyLanguage.TraditionalChinese.code -> "Private Key"
		else -> ""
	}

	@JvmField
	val watchOnly = when (currentLanguage) {
		HoneyLanguage.English.code -> "Watch-Only Wallet"
		HoneyLanguage.Chinese.code -> "观察钱包"
		HoneyLanguage.Japanese.code -> "watch only"
		HoneyLanguage.Korean.code -> "watch only"
		HoneyLanguage.Russian.code -> "watch only"
		HoneyLanguage.TraditionalChinese.code -> "watch only"
		else -> ""
	}
}

object SplashText {

	@JvmField
	val slogan = when (currentLanguage) {
		HoneyLanguage.English.code -> "the most useful and safest wallet in the world"
		HoneyLanguage.Chinese.code -> "有用又安全的区块链钱包"
		HoneyLanguage.Japanese.code -> "the most useful and safest wallet in the world"
		HoneyLanguage.Korean.code -> "the most useful and safest wallet in the world"
		HoneyLanguage.Russian.code -> "the most useful and safest wallet in the world"
		HoneyLanguage.TraditionalChinese.code -> "the most useful and safest wallet in the world"
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


