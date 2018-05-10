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
		HoneyLanguage.English.code -> "Password strength is critical to guard your wallet We can’t recover the password, please back up cautiously"
		HoneyLanguage.Chinese.code -> "越强的密码越安全，请尽量设置更复杂的密码。我们不会为您保管密码，请您谨慎保管。"
		HoneyLanguage.Japanese.code -> "パスワードが複雑になればなるほど、財布はより安全になります。 あなたのパスワードを保持しません、慎重にバックアップしてください。"
		HoneyLanguage.Korean.code -> "암호가 복잡할수록 지갑은 안전합니다"
		HoneyLanguage.Russian.code -> "Чем сложнее пароль, тем более безопасен кошелек"
		HoneyLanguage.TraditionalChinese.code -> "越強的密碼越安全，請盡量設置更複雜的密碼。我們不會為您保管密碼，請您謹慎保管。"
		else -> "Password strength is critical to guard your wallet We can’t recover the password, please back up cautiously"
	}
	@JvmField
	val create = when (currentLanguage) {
		HoneyLanguage.English.code -> "Create Wallet"
		HoneyLanguage.Chinese.code -> "创建钱包"
		HoneyLanguage.Japanese.code -> "ウォレット作成"
		HoneyLanguage.Korean.code -> "지갑 만들기"
		HoneyLanguage.Russian.code -> "Создать кошелек"
		HoneyLanguage.TraditionalChinese.code -> "產生錢包"
		else -> "Create Wallet"
	}
	@JvmField
	val mnemonicBackUp = when (currentLanguage) {
		HoneyLanguage.English.code -> "Mnemonic Backup"
		HoneyLanguage.Chinese.code -> "备份助记词"
		HoneyLanguage.Japanese.code -> "バックアップニーモニック"
		HoneyLanguage.Korean.code -> "백업 니모닉"
		HoneyLanguage.Russian.code -> "Резервная мнемоника"
		HoneyLanguage.TraditionalChinese.code -> "備份助憶口令"
		else -> "Mnemonic Backup"
	}
	@JvmField
	val agreement = when (currentLanguage) {
		HoneyLanguage.English.code -> "Agreement"
		HoneyLanguage.Chinese.code -> "用户协议"
		HoneyLanguage.Japanese.code -> "利用規約"
		HoneyLanguage.Korean.code -> "사용자 동의서"
		HoneyLanguage.Russian.code -> "Пользовательское соглашение"
		HoneyLanguage.TraditionalChinese.code -> "用戶協議"
		else -> "Agreement"
	}
	@JvmField
	val agreeRemind = when (currentLanguage) {
		HoneyLanguage.English.code -> "You need to agree the terms"
		HoneyLanguage.Chinese.code -> "请阅读并同意用户协议"
		HoneyLanguage.Japanese.code -> "条件に同意する必要があります"
		HoneyLanguage.Korean.code -> "이용 약관을 읽고 동의하십시오"
		HoneyLanguage.Russian.code -> "Прочтите и согласитесь с пользовательским соглашением"
		HoneyLanguage.TraditionalChinese.code -> "請閱讀並同意用戶協議"
		else -> "You need to agree the terms"
	}
	@JvmField
	val mnemonicBackupAttention = when (currentLanguage) {
		HoneyLanguage.English.code -> "Confirm your mnemonic words to remember your account"
		HoneyLanguage.Chinese.code -> "确认一遍助记词，以确保您的备份正确"
		HoneyLanguage.Japanese.code -> "ニーモニックを再度確認して、正しくバックアップするようにします"
		HoneyLanguage.Korean.code -> "니모닉 단어를 확인하여 계정을 기억하십시오"
		HoneyLanguage.Russian.code -> "Подтвердите мнемонику, чтобы убедиться, что вы правильно настроили"
		HoneyLanguage.TraditionalChinese.code -> "確認一遍助憶口令，以確保您的備份正確"
		else -> "Confirm your mnemonic words to remember your account"
	}
	@JvmField
	val mnemonicConfirmationDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "Confirm your mnemonic words to remember your account"
		HoneyLanguage.Chinese.code -> "确认一遍助记词，以确保您的备份正确"
		HoneyLanguage.Japanese.code -> "ニーモニックを再度確認して、正しくバックアップするようにします"
		HoneyLanguage.Korean.code -> "니모닉 단어를 확인하여 계정을 기억하십시오"
		HoneyLanguage.Russian.code -> "Подтвердите мнемонику, чтобы убедиться, что вы правильно настроили"
		HoneyLanguage.TraditionalChinese.code -> "確認一遍助憶口令，以確保您的備份正確"
		else -> "Confirm your mnemonic words to remember your account"
	}
	@JvmField
	val password = when (currentLanguage) {
		HoneyLanguage.English.code -> "Password"
		HoneyLanguage.Chinese.code -> "钱包密码"
		HoneyLanguage.Japanese.code -> "パスワード"
		HoneyLanguage.Korean.code -> "월렛 비밀번호"
		HoneyLanguage.Russian.code -> "Пароль кошелька"
		HoneyLanguage.TraditionalChinese.code -> "錢包密碼"
		else -> "Password"
	}
	@JvmField
	val hint = when (currentLanguage) {
		HoneyLanguage.English.code -> "Password Hint"
		HoneyLanguage.Chinese.code -> "密码提示"
		HoneyLanguage.Japanese.code -> "パスワードのヒント"
		HoneyLanguage.Korean.code -> "비밀번호 힌트"
		HoneyLanguage.Russian.code -> "Изменить пароль"
		HoneyLanguage.TraditionalChinese.code -> "密碼提示"
		else -> "Password Hint"
	}
	@JvmField
	val repeatPassword = when (currentLanguage) {
		HoneyLanguage.English.code -> "Repeat Password"
		HoneyLanguage.Chinese.code -> "确认密码"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
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
		else -> "Wallet Name"
	}
	@JvmField
	val mnemonicConfirmation = when (currentLanguage) {
		HoneyLanguage.English.code -> "Mnemonic Confirmation"
		HoneyLanguage.Chinese.code -> "确认助记词"
		HoneyLanguage.Japanese.code -> "ニーモニックを確認する"
		HoneyLanguage.Korean.code -> "확인 보장 코드"
		HoneyLanguage.Russian.code -> "Подтвердить мнемонику"
		HoneyLanguage.TraditionalChinese.code -> "確認助憶口令"
		else -> "Mnemonic Confirmation"
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
		else -> "Import Wallet"
	}
	@JvmField
	val mnemonicHint = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter your mnemonic with space split"
		HoneyLanguage.Chinese.code -> "请按顺序输入助记词，词间使用空格符间隔"
		HoneyLanguage.Japanese.code -> "単語の間のスペースを使用してニーモニックを順番に入力してください"
		HoneyLanguage.Korean.code -> "단어 사이의 공백을 사용하여 니모닉을 순서대로 입력하십시오."
		HoneyLanguage.Russian.code -> "Пожалуйста, введите мнемонику в порядке, используя пробелы между словами"
		HoneyLanguage.TraditionalChinese.code -> "請按順序輸入助記詞，詞間使用空格符間隔"
		else -> "Enter your mnemonic with space split"
	}
	@JvmField
	val keystoreHint = when (currentLanguage) {
		HoneyLanguage.English.code -> " Enter your keystore here"
		HoneyLanguage.Chinese.code -> " 在此输入您的keystore"
		HoneyLanguage.Japanese.code -> " キーストアをここに入力してください"
		HoneyLanguage.Korean.code -> "여기에 키 스토어를 입력하십시오."
		HoneyLanguage.Russian.code -> " Введите свое хранилище ключей здесь"
		HoneyLanguage.TraditionalChinese.code -> " 在此輸入您的keystore密鑰庫"
		else -> " Enter your keystore here"
	}
	@JvmField
	val privateKeyHint = when (currentLanguage) {
		HoneyLanguage.English.code -> " Enter your private key here"
		HoneyLanguage.Chinese.code -> " 在此输入您的私钥"
		HoneyLanguage.Japanese.code -> " ここに秘密鍵を入力してください"
		HoneyLanguage.Korean.code -> "여기에 비공개 키를 입력하십시오."
		HoneyLanguage.Russian.code -> " Введите свой секретный ключ здесь"
		HoneyLanguage.TraditionalChinese.code -> " 在此輸入您的私鑰"
		else -> " Enter your private key here"
	}
	@JvmField
	val address = when (currentLanguage) {
		HoneyLanguage.English.code -> "Address"
		HoneyLanguage.Chinese.code -> "钱包地址"
		HoneyLanguage.Japanese.code -> "ウォレットアドレス"
		HoneyLanguage.Korean.code -> "월렛 주소"
		HoneyLanguage.Russian.code -> "Адрес кошелька"
		HoneyLanguage.TraditionalChinese.code -> "錢包地址"
		else -> "Address"
	}
	@JvmField
	val unvalidPrivateKey = when (currentLanguage) {
		HoneyLanguage.English.code -> "Unvalid Private Key"
		HoneyLanguage.Chinese.code -> " 这不是正确格式的私钥"
		HoneyLanguage.Japanese.code -> "これは、秘密鍵の正しい形式ではありません"
		HoneyLanguage.Korean.code -> "이것은 개인 키의 올바른 형식이 아닙니다."
		HoneyLanguage.Russian.code -> "Недопустимый закрытый ключ"
		HoneyLanguage.TraditionalChinese.code -> "這不是正確格式的私鑰"
		else -> "Unvalid Private Key"
	}
	@JvmField
	val existAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "This address has already been imported"
		HoneyLanguage.Chinese.code -> "这个地址已经导入过了"
		HoneyLanguage.Japanese.code -> "このアドレスは既にインポートされています"
		HoneyLanguage.Korean.code -> "이 주소는 이미 가져 왔습니다."
		HoneyLanguage.Russian.code -> "Этот адрес уже импортирован"
		HoneyLanguage.TraditionalChinese.code -> "這個地址已經導入過了"
		else -> "This address has already been imported"
	}
	@JvmField
	val exportKeystore = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter password and then click confirm button to get keystore"
		HoneyLanguage.Chinese.code -> "输入密码，然后单击确认按钮以获取keystore"
		HoneyLanguage.Japanese.code -> "パスワードを入力し、[OK]ボタンをクリックしてキーストアを取得します"
		HoneyLanguage.Korean.code -> "암호를 입력하고 확인 버튼을 클릭하여 키 저장소를 가져옵니다."
		HoneyLanguage.Russian.code -> "Введите пароль и нажмите кнопку подтверждения, чтобы получить хранилище ключей."
		HoneyLanguage.TraditionalChinese.code -> "輸入密碼，然後單擊確認按鈕以獲取keystore"
		else -> "Enter password and then click confirm button to get keystore"
	}
	@JvmField
	val exportPrivateKey = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter password and then click confirm button to get private key"
		HoneyLanguage.Chinese.code -> "输入密码，然后点击确认按钮获得私钥"
		HoneyLanguage.Japanese.code -> "パスワードを入力して確認ボタンをクリックすると、秘密鍵が取得されます"
		HoneyLanguage.Korean.code -> "비밀 번호를 입력하고 확인 버튼을 클릭하여 개인 키를 가져옵니다."
		HoneyLanguage.Russian.code -> "Введите пароль и нажмите кнопку подтверждения, чтобы получить секретный ключ"
		HoneyLanguage.TraditionalChinese.code -> "輸入密碼，然後點擊確認按鈕獲得私鑰"
		else -> "Enter password and then click confirm button to get private key"
	}
}

object WalletText {

	@JvmField
	val totalAssets = when (currentLanguage) {
		HoneyLanguage.English.code -> "Total assets"
		HoneyLanguage.Chinese.code -> "钱包所有财产"
		HoneyLanguage.Japanese.code -> "総資産"
		HoneyLanguage.Korean.code -> "총자산"
		HoneyLanguage.Russian.code -> "Итого активы"
		HoneyLanguage.TraditionalChinese.code -> "總資產"
		else -> "Total assets"
	}
	@JvmField
	val manage = when (currentLanguage) {
		HoneyLanguage.English.code -> "Manage My Wallets"
		HoneyLanguage.Chinese.code -> "管理我的钱包"
		HoneyLanguage.Japanese.code -> "ウォレットを管理する"
		HoneyLanguage.Korean.code -> "월렛 관리"
		HoneyLanguage.Russian.code -> "Управление кошельками"
		HoneyLanguage.TraditionalChinese.code -> "管理我的錢包"
		else -> "Manage My Wallets"
	}
	@JvmField
	val section = when (currentLanguage) {
		HoneyLanguage.English.code -> "My tokens: "
		HoneyLanguage.Chinese.code -> "我的代币: "
		HoneyLanguage.Japanese.code -> "マイトークン: "
		HoneyLanguage.Korean.code -> "내 토큰: "
		HoneyLanguage.Russian.code -> "Мои токены: "
		HoneyLanguage.TraditionalChinese.code -> "我的代幣: "
		else -> "My tokens: "
	}
	@JvmField
	val addToken = when (currentLanguage) {
		HoneyLanguage.English.code -> "Add More Token"
		HoneyLanguage.Chinese.code -> "添加其他代币"
		HoneyLanguage.Japanese.code -> "トークンを追加する"
		HoneyLanguage.Korean.code -> "더 많은 토큰 추가"
		HoneyLanguage.Russian.code -> "Добавить еще токен"
		HoneyLanguage.TraditionalChinese.code -> "添加其他代幣"
		else -> "Add More Token"
	}
	@JvmField
	val addWallet = when (currentLanguage) {
		HoneyLanguage.English.code -> "Add Wallet"
		HoneyLanguage.Chinese.code -> "添加钱包"
		HoneyLanguage.Japanese.code -> "ウォレットを追加"
		HoneyLanguage.Korean.code -> "지갑 추가"
		HoneyLanguage.Russian.code -> "Добавить кошелек"
		HoneyLanguage.TraditionalChinese.code -> "添加錢包"
		else -> "Add Wallet"
	}
	@JvmField
	val importWallet = when (currentLanguage) {
		HoneyLanguage.English.code -> "Import Wallet"
		HoneyLanguage.Chinese.code -> "导入钱包"
		HoneyLanguage.Japanese.code -> "ウォレットをインポート"
		HoneyLanguage.Korean.code -> "월렛 가져 오기"
		HoneyLanguage.Russian.code -> "Импортный кошелек"
		HoneyLanguage.TraditionalChinese.code -> "導入錢包"
		else -> "Import Wallet"
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
		else -> "Transaction History"
	}
	@JvmField
	val detail = when (currentLanguage) {
		HoneyLanguage.English.code -> "Transaction Detail"
		HoneyLanguage.Chinese.code -> "交易详情"
		HoneyLanguage.Japanese.code -> "取引の詳細"
		HoneyLanguage.Korean.code -> "거래 세부 정보"
		HoneyLanguage.Russian.code -> "Подробности транзакции"
		HoneyLanguage.TraditionalChinese.code -> "交易明細"
		else -> "Transaction Detail"
	}
	@JvmField
	val etherScanTransaction = when (currentLanguage) {
		HoneyLanguage.English.code -> "EtherScan Detail"
		HoneyLanguage.Chinese.code -> "EtherScan详情"
		HoneyLanguage.Japanese.code -> "EtherScanの詳細"
		HoneyLanguage.Korean.code -> "EtherScan 세부 정보"
		HoneyLanguage.Russian.code -> "Подробное описание EtherScan"
		HoneyLanguage.TraditionalChinese.code -> "EtherScan詳情"
		else -> "EtherScan Detail"
	}
	@JvmField
	val url = when (currentLanguage) {
		HoneyLanguage.English.code -> "Open A Url"
		HoneyLanguage.Chinese.code -> "从网址打开"
		HoneyLanguage.Japanese.code -> "URLを開く"
		HoneyLanguage.Korean.code -> "URL 열기"
		HoneyLanguage.Russian.code -> "Открыть адрес"
		HoneyLanguage.TraditionalChinese.code -> "從網址打開"
		else -> "Open A Url"
	}
	@JvmField
	val confirmTransaction = when (currentLanguage) {
		HoneyLanguage.English.code -> "Confirm transaction with your password then transaction will begin"
		HoneyLanguage.Chinese.code -> "输入您的密码以确认交易"
		HoneyLanguage.Japanese.code -> "パスワードで取引を確認してください。"
		HoneyLanguage.Korean.code -> "귀하의 비밀번호로 거래를 확인하십시오"
		HoneyLanguage.Russian.code -> "подтвердите транзакцию с помощью своего пароля, тогда транзакция начнется"
		HoneyLanguage.TraditionalChinese.code -> "輸入您的密碼以確認交易"
		else -> "Confirm transaction with your password then transaction will begin"
	}
	@JvmField
	val minerFee = when (currentLanguage) {
		HoneyLanguage.English.code -> "Miner Fee"
		HoneyLanguage.Chinese.code -> "矿工费"
		HoneyLanguage.Japanese.code -> "鉱夫料"
		HoneyLanguage.Korean.code -> "광부비"
		HoneyLanguage.Russian.code -> "Гонорар шахтеров"
		HoneyLanguage.TraditionalChinese.code -> "礦工費"
		else -> "Miner Fee"
	}
	@JvmField
	val memo = when (currentLanguage) {
		HoneyLanguage.English.code -> "Memo"
		HoneyLanguage.Chinese.code -> "备注"
		HoneyLanguage.Japanese.code -> "メモ"
		HoneyLanguage.Korean.code -> "메모"
		HoneyLanguage.Russian.code -> "напоминание"
		HoneyLanguage.TraditionalChinese.code -> "메모"
		else -> "Memo"
	}
	@JvmField
	val transactionHash = when (currentLanguage) {
		HoneyLanguage.English.code -> "Transaction Hash"
		HoneyLanguage.Chinese.code -> "交易Hash"
		HoneyLanguage.Japanese.code -> "トランザクションハッシュ"
		HoneyLanguage.Korean.code -> "트랜잭션 해시"
		HoneyLanguage.Russian.code -> "Сделка транзакций"
		HoneyLanguage.TraditionalChinese.code -> "交易Hash"
		else -> "Transaction Hash"
	}
	@JvmField
	val blockNumber = when (currentLanguage) {
		HoneyLanguage.English.code -> "Block Height"
		HoneyLanguage.Chinese.code -> "区块高度"
		HoneyLanguage.Japanese.code -> "ブロック高さ"
		HoneyLanguage.Korean.code -> "블록 높이"
		HoneyLanguage.Russian.code -> "Высота блока"
		HoneyLanguage.TraditionalChinese.code -> "區塊高度"
		else -> "Block Height"
	}
	@JvmField
	val transactionDate = when (currentLanguage) {
		HoneyLanguage.English.code -> "Transaction Date"
		HoneyLanguage.Chinese.code -> "交易日期"
		HoneyLanguage.Japanese.code -> "取引日"
		HoneyLanguage.Korean.code -> "거래 날짜"
		HoneyLanguage.Russian.code -> "Дата транзакции"
		HoneyLanguage.TraditionalChinese.code -> "交易日期"
		else -> "Transaction Date"
	}
	@JvmField
	val gasLimit = when (currentLanguage) {
		HoneyLanguage.English.code -> "Gas Limit"
		HoneyLanguage.Chinese.code -> "燃气费上限"
		HoneyLanguage.Japanese.code -> "ガスキャップ"
		HoneyLanguage.Korean.code -> "가스 캡"
		HoneyLanguage.Russian.code -> "Газовый предел"
		HoneyLanguage.TraditionalChinese.code -> "燃氣費上限"
		else -> "Gas Limit"
	}
	@JvmField
	val gasPrice = when (currentLanguage) {
		HoneyLanguage.English.code -> "Gas Price"
		HoneyLanguage.Chinese.code -> "燃气单价"
		HoneyLanguage.Japanese.code -> "ガス単価"
		HoneyLanguage.Korean.code -> "가스 단가"
		HoneyLanguage.Russian.code -> "Цена газа"
		HoneyLanguage.TraditionalChinese.code -> "燃氣單價"
		else -> "Gas Price"
	}
}

object TokenDetailText {

	@JvmField
	val address = when (currentLanguage) {
		HoneyLanguage.English.code -> "Address"
		HoneyLanguage.Chinese.code -> "接收地址"
		HoneyLanguage.Japanese.code -> "宛先"
		HoneyLanguage.Korean.code -> "수신 주소"
		HoneyLanguage.Russian.code -> "Адрес"
		HoneyLanguage.TraditionalChinese.code -> "接收地址"
		else -> "Address"
	}
	@JvmField
	val deposit = when (currentLanguage) {
		HoneyLanguage.English.code -> "Deposit"
		HoneyLanguage.Chinese.code -> "接收"
		HoneyLanguage.Japanese.code -> "受信"
		HoneyLanguage.Korean.code -> "수신"
		HoneyLanguage.Russian.code -> "депозит"
		HoneyLanguage.TraditionalChinese.code -> "接收"
		else -> "Deposit"
	}
	@JvmField
	val customGas = when (currentLanguage) {
		HoneyLanguage.English.code -> "Gas Editor"
		HoneyLanguage.Chinese.code -> "自定义燃气费"
		HoneyLanguage.Japanese.code -> "カスタムガス料金"
		HoneyLanguage.Korean.code -> "맞춤 가스 요금"
		HoneyLanguage.Russian.code -> "Редактор газа"
		HoneyLanguage.TraditionalChinese.code -> "自定義燃氣費"
		else -> "Gas Editor"
	}
	@JvmField
	val paymentValue = when (currentLanguage) {
		HoneyLanguage.English.code -> "Payment Value"
		HoneyLanguage.Chinese.code -> "实际价值"
		HoneyLanguage.Japanese.code -> "実際の値"
		HoneyLanguage.Korean.code -> "실제 값"
		HoneyLanguage.Russian.code -> "Стоимость платежа"
		HoneyLanguage.TraditionalChinese.code -> "實際價值"
		else -> "Payment Value"
	}
	@JvmField
	val transferDetail = when (currentLanguage) {
		HoneyLanguage.English.code -> "Transfer Detail"
		HoneyLanguage.Chinese.code -> "交易详情"
		HoneyLanguage.Japanese.code -> "転送の詳細"
		HoneyLanguage.Korean.code -> "이체 세부 사항"
		HoneyLanguage.Russian.code -> "Сведения о передаче"
		HoneyLanguage.TraditionalChinese.code -> "交易詳情"
		else -> "Transfer Detail"
	}
	@JvmField
	val customMiner = when (currentLanguage) {
		HoneyLanguage.English.code -> "Custom miner fee"
		HoneyLanguage.Chinese.code -> "自定义矿工费"
		HoneyLanguage.Japanese.code -> "カスタム鉱夫料金"
		HoneyLanguage.Korean.code -> "맞춤 광부 요금"
		HoneyLanguage.Russian.code -> "Индивидуальная плата за шахт"
		HoneyLanguage.TraditionalChinese.code -> "自定義礦工費"
		else -> "Custom miner fee"
	}
	@JvmField
	val tokenDetail = when (currentLanguage) {
		HoneyLanguage.English.code -> "Token Detail"
		HoneyLanguage.Chinese.code -> "代币详情"
		HoneyLanguage.Japanese.code -> "トークンの詳細"
		HoneyLanguage.Korean.code -> "토큰 세부 정보"
		HoneyLanguage.Russian.code -> "Детали токена"
		HoneyLanguage.TraditionalChinese.code -> "代幣詳情"
		else -> "Token Detail"
	}
}

object CommonText {

	@JvmField
	val confirm = when (currentLanguage) {
		HoneyLanguage.English.code -> "Confirm"
		HoneyLanguage.Chinese.code -> "确认"
		HoneyLanguage.Japanese.code -> "確認"
		HoneyLanguage.Korean.code -> "확인"
		HoneyLanguage.Russian.code -> "подтвердить"
		HoneyLanguage.TraditionalChinese.code -> "確認"
		else -> "Confirm"
	}
	@JvmField
	val create = when (currentLanguage) {
		HoneyLanguage.English.code -> "Create"
		HoneyLanguage.Chinese.code -> "添加"
		HoneyLanguage.Japanese.code -> "追加"
		HoneyLanguage.Korean.code -> "만들기"
		HoneyLanguage.Russian.code -> "создать"
		HoneyLanguage.TraditionalChinese.code -> "添加"
		else -> "Create"
	}
	@JvmField
	val cancel = when (currentLanguage) {
		HoneyLanguage.English.code -> "Cancel"
		HoneyLanguage.Chinese.code -> "取消"
		HoneyLanguage.Japanese.code -> "キャンセル"
		HoneyLanguage.Korean.code -> "취소"
		HoneyLanguage.Russian.code -> "отменить"
		HoneyLanguage.TraditionalChinese.code -> "取消"
		else -> "Cancel"
	}
	@JvmField
	val next = when (currentLanguage) {
		HoneyLanguage.English.code -> "Next"
		HoneyLanguage.Chinese.code -> "下一步"
		HoneyLanguage.Japanese.code -> "次へ"
		HoneyLanguage.Korean.code -> "다음"
		HoneyLanguage.Russian.code -> "следующий"
		HoneyLanguage.TraditionalChinese.code -> "下一步"
		else -> "Next"
	}
	@JvmField
	val saveToAlbum = when (currentLanguage) {
		HoneyLanguage.English.code -> "Save To Album"
		HoneyLanguage.Chinese.code -> "保存到相册"
		HoneyLanguage.Japanese.code -> "アルバムに保存"
		HoneyLanguage.Korean.code -> "앨범에 저장"
		HoneyLanguage.Russian.code -> "Сохранить в альбом"
		HoneyLanguage.TraditionalChinese.code -> "保存到相簿"
		else -> "Save To Album"
	}
	@JvmField
	val copyAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "Copy Address"
		HoneyLanguage.Chinese.code -> "点击复制地址"
		HoneyLanguage.Japanese.code -> "コピー"
		HoneyLanguage.Korean.code -> "지갑 주소 복사를 클릭하십시오"
		HoneyLanguage.Russian.code -> "Копировать адрес"
		HoneyLanguage.TraditionalChinese.code -> "點擊複製地址"
		else -> "Copy Address"
	}
	@JvmField
	val startImporting = when (currentLanguage) {
		HoneyLanguage.English.code -> "Start Importing"
		HoneyLanguage.Chinese.code -> "开始导入"
		HoneyLanguage.Japanese.code -> "インポートを開始する"
		HoneyLanguage.Korean.code -> "가져 오기 시작"
		HoneyLanguage.Russian.code -> "Начать импорт"
		HoneyLanguage.TraditionalChinese.code -> "開始導入"
		else -> "Start Importing"
	}
	@JvmField
	val enterPassword = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter your password"
		HoneyLanguage.Chinese.code -> "输入钱包密码"
		HoneyLanguage.Japanese.code -> "パスワードを入力してください"
		HoneyLanguage.Korean.code -> "비밀번호를 입력하십시오"
		HoneyLanguage.Russian.code -> "Введите пароль"
		HoneyLanguage.TraditionalChinese.code -> "輸入錢包密碼"
		else -> "Enter your password"
	}
	@JvmField
	val delete = when (currentLanguage) {
		HoneyLanguage.English.code -> "DELETE"
		HoneyLanguage.Chinese.code -> "删除钱包"
		HoneyLanguage.Japanese.code -> "削除"
		HoneyLanguage.Korean.code -> "지갑 지우기"
		HoneyLanguage.Russian.code -> "УДАЛИТЬ"
		HoneyLanguage.TraditionalChinese.code -> "刪除錢包"
		else -> "DELETE"
	}
	@JvmField
	val slow = when (currentLanguage) {
		HoneyLanguage.English.code -> "SLOW"
		HoneyLanguage.Chinese.code -> "慢"
		HoneyLanguage.Japanese.code -> "遅い"
		HoneyLanguage.Korean.code -> "느리게"
		HoneyLanguage.Russian.code -> "МЕДЛЕННЫЙ"
		HoneyLanguage.TraditionalChinese.code -> "慢"
		else -> "SLOW"
	}
	@JvmField
	val fast = when (currentLanguage) {
		HoneyLanguage.English.code -> "FAST"
		HoneyLanguage.Chinese.code -> "快速"
		HoneyLanguage.Japanese.code -> "速い"
		HoneyLanguage.Korean.code -> "빠른"
		HoneyLanguage.Russian.code -> "БЫСТРО"
		HoneyLanguage.TraditionalChinese.code -> "快速"
		else -> "FAST"
	}
}

object AlertText {

	@JvmField
	val watchOnly = when (currentLanguage) {
		HoneyLanguage.English.code -> "Current wallet is watch only type, This kind of operation is not allowed"
		HoneyLanguage.Chinese.code -> "这是观察钱包，无法进行转账交易。"
		HoneyLanguage.Japanese.code -> "これは観測ウォレットであり、トランザクションを転送できません。"
		HoneyLanguage.Korean.code -> "이것은 관측 지갑이며 거래를 전송할 수 없습니다"
		HoneyLanguage.Russian.code -> "Это наблюдательный кошелек, который не может передавать транзакции"
		HoneyLanguage.TraditionalChinese.code -> "這是觀察錢包，無法進行轉賬交易。"
		else -> "Current wallet is watch only type, This kind of operation is not allowed"
	}
	@JvmField
	val modifyCountAfoterCustomGas = when (currentLanguage) {
		HoneyLanguage.English.code -> "Modifying transfer count will lead to a recalculation and you need to reset the custom gas settings"
		HoneyLanguage.Chinese.code -> "修转账金额将导致重新计算燃气花费，您需要重置自定燃气费设置"
		HoneyLanguage.Japanese.code -> "修理の転送量はガスコストの再計算をもたらすでしょう、あなたはカスタムガス料金設定をリセットする必要があります"
		HoneyLanguage.Korean.code -> "수리 양도로 가스 비용이 다시 계산됩니다. 맞춤 가스 요금 설정을 재설정해야합니다."
		HoneyLanguage.Russian.code -> "Изменение количества переводов приведет к перерасчету, и вам необходимо сбросить настройки пользовательского газа"
		HoneyLanguage.TraditionalChinese.code -> "修轉賬金額將導致重新計算燃氣花費，您需要重置自定燃氣費設置"
		else -> "Modifying transfer count will lead to a recalculation and you need to reset the custom gas settings"
	}
	@JvmField
	val importWalletNetwork = when (currentLanguage) {
		HoneyLanguage.English.code -> "There isn't network found, Importing Wallet need network to check the value of it"
		HoneyLanguage.Chinese.code -> "没有检测到网络，导入钱包时需要网络环境查询您的货币余额"
		HoneyLanguage.Japanese.code -> "ネットワークが見つかりません。ウォレットをインポートするとネットワークの価値を確認する必要があります"
		HoneyLanguage.Korean.code -> "네트워크를 찾을 수 없습니다. 지갑을 가져 와서 값을 확인해야합니다."
		HoneyLanguage.Russian.code -> "Сеть не найдена, Импорт сети для кошелька, чтобы проверить ее значение"
		HoneyLanguage.TraditionalChinese.code -> "沒有檢測到網絡，導入錢包時需要網絡環境查詢您的貨幣餘額\n"
		else -> "There isn't network found, Importing Wallet need network to check the value of it"
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
		else -> "Wallets"
	}
}

object NotificationText {

	@JvmField
	val notification = when (currentLanguage) {
		HoneyLanguage.English.code -> "Notifications"
		HoneyLanguage.Chinese.code -> "通知中心"
		HoneyLanguage.Japanese.code -> "通知センター"
		HoneyLanguage.Korean.code -> "알림 센터"
		HoneyLanguage.Russian.code -> "Центр уведомлений"
		HoneyLanguage.TraditionalChinese.code -> "通知中心"
		else -> "Notifications"
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
		else -> "Add Token"
	}
}

object Alert {

	@JvmField
	val selectCurrency = when (currentLanguage) {
		HoneyLanguage.English.code -> "Once you selected it, application will be rebooted and just wait several seconds. Are You Sure To Switch Currency Settings?"
		HoneyLanguage.Chinese.code -> "一旦你选择切换货币，应用程序将被重新启动，并等待几秒钟。 你确定要切换货币设置吗？"
		HoneyLanguage.Japanese.code -> "通貨の切り替えを選択すると、アプリケーションが再起動され、数秒待つことになります。 通貨設定を変更してもよろしいですか？"
		HoneyLanguage.Korean.code -> "통화 전환을 선택하면 응용 프로그램이 다시 시작되고 몇 초 기다립니다. 통화 설정을 전환 하시겠습니까?"
		HoneyLanguage.Russian.code -> "После того, как вы его выбрали, приложение будет перезагружено и просто подождите несколько секунд. Правильно ли вы переключаете настройки валют?"
		HoneyLanguage.TraditionalChinese.code -> "一旦你選擇切換貨幣，應用程序將被重新啟動，並等待幾秒鐘。你確定要切換貨幣設置嗎？"
		else -> "Once you selected it, application will be rebooted and just wait several seconds. Are You Sure To Switch Currency Settings?"
	}
}

object WalletSettingsText {

	@JvmField
	val copy = when (currentLanguage) {
		HoneyLanguage.English.code -> "Click to copy address"
		HoneyLanguage.Chinese.code -> "点击复制地址"
		HoneyLanguage.Japanese.code -> "アドレスをコピーする"
		HoneyLanguage.Korean.code -> "지갑 주소 복사를 클릭하십시오"
		HoneyLanguage.Russian.code -> "Копировать адрес"
		HoneyLanguage.TraditionalChinese.code -> "點擊複製地址"
		else -> "Click to copy address"
	}
	@JvmField
	val checkQRCode = when (currentLanguage) {
		HoneyLanguage.English.code -> "Check QR Code"
		HoneyLanguage.Chinese.code -> "查看二维码"
		HoneyLanguage.Japanese.code -> "QRコードをチェックする"
		HoneyLanguage.Korean.code -> "QR 코드 확인"
		HoneyLanguage.Russian.code -> "Проверить QR-код"
		HoneyLanguage.TraditionalChinese.code -> "查看二維碼"
		else -> "Check QR Code"
	}
	@JvmField
	val balance = when (currentLanguage) {
		HoneyLanguage.English.code -> "Balance"
		HoneyLanguage.Chinese.code -> "余额"
		HoneyLanguage.Japanese.code -> "残高"
		HoneyLanguage.Korean.code -> "균형"
		HoneyLanguage.Russian.code -> "баланс"
		HoneyLanguage.TraditionalChinese.code -> "余额"
		else -> "Balance"
	}
	@JvmField
	val walletName = when (currentLanguage) {
		HoneyLanguage.English.code -> "Wallet Name"
		HoneyLanguage.Chinese.code -> "钱包名称"
		HoneyLanguage.Japanese.code -> "ウォレット名"
		HoneyLanguage.Korean.code -> "월렛 이름"
		HoneyLanguage.Russian.code -> "Название кошелька"
		HoneyLanguage.TraditionalChinese.code -> "錢包名稱"
		else -> "Wallet Name"
	}
	@JvmField
	val walletNameSettings = when (currentLanguage) {
		HoneyLanguage.English.code -> "Wallet Name"
		HoneyLanguage.Chinese.code -> "钱包名称设置"
		HoneyLanguage.Japanese.code -> "ウォレット設定"
		HoneyLanguage.Korean.code -> "월렛 이름 설정"
		HoneyLanguage.Russian.code -> "Настройка имени кошелька"
		HoneyLanguage.TraditionalChinese.code -> "錢包名稱設置"
		else -> "Wallet Name"
	}
	@JvmField
	val walletSettings = when (currentLanguage) {
		HoneyLanguage.English.code -> "Wallet Settings"
		HoneyLanguage.Chinese.code -> "钱包设置"
		HoneyLanguage.Japanese.code -> "ウォレット設定"
		HoneyLanguage.Korean.code -> "월렛 설정"
		HoneyLanguage.Russian.code -> "Настройки кошелька"
		HoneyLanguage.TraditionalChinese.code -> "錢包設置"
		else -> "Wallet Settings"
	}
	@JvmField
	val passwordSettings = when (currentLanguage) {
		HoneyLanguage.English.code -> "Change Password"
		HoneyLanguage.Chinese.code -> "修改密码"
		HoneyLanguage.Japanese.code -> "パスワードを変更する"
		HoneyLanguage.Korean.code -> "비밀번호 변경"
		HoneyLanguage.Russian.code -> "Изменить пароль"
		HoneyLanguage.TraditionalChinese.code -> "修改密碼"
		else -> "Change Password"
	}
	@JvmField
	val hint = when (currentLanguage) {
		HoneyLanguage.English.code -> "Password Hint"
		HoneyLanguage.Chinese.code -> "密码提示"
		HoneyLanguage.Japanese.code -> "パスワードのヒント"
		HoneyLanguage.Korean.code -> "비밀번호 힌트"
		HoneyLanguage.Russian.code -> "Изменить пароль"
		HoneyLanguage.TraditionalChinese.code -> "密碼提示"
		else -> "Password Hint"
	}
	@JvmField
	val exportPrivateKey = when (currentLanguage) {
		HoneyLanguage.English.code -> "Export Private Key"
		HoneyLanguage.Chinese.code -> "导出私钥"
		HoneyLanguage.Japanese.code -> "秘密鍵のエクスポート"
		HoneyLanguage.Korean.code -> "개인 키 내보내기"
		HoneyLanguage.Russian.code -> "Экспорт секретного ключа"
		HoneyLanguage.TraditionalChinese.code -> "導出金鑰"
		else -> "Export Private Key"
	}
	@JvmField
	val exportKeystore = when (currentLanguage) {
		HoneyLanguage.English.code -> "Export Keystore"
		HoneyLanguage.Chinese.code -> "导出私钥"
		HoneyLanguage.Japanese.code -> "キーストアのエクスポート"
		HoneyLanguage.Korean.code -> "키 스토어 내보내기"
		HoneyLanguage.Russian.code -> "Экспортный Keystore"
		HoneyLanguage.TraditionalChinese.code -> "導出 Keystore"
		else -> "Export Keystore"
	}
	@JvmField
	val delete = when (currentLanguage) {
		HoneyLanguage.English.code -> "Dellet Wallet"
		HoneyLanguage.Chinese.code -> "删除钱包"
		HoneyLanguage.Japanese.code -> "ウォレットを削除"
		HoneyLanguage.Korean.code -> "지갑 지우기"
		HoneyLanguage.Russian.code -> "Удалить кошелек"
		HoneyLanguage.TraditionalChinese.code -> "刪除錢包"
		else -> "Dellet Wallet"
	}
	@JvmField
	val deleteInfoTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Are you sure to delete current wallet? (Be sure you have backed up it)"
		HoneyLanguage.Chinese.code -> "确认要删除钱包吗？(删除前请确保已妥善备份)"
		HoneyLanguage.Japanese.code -> "ウォレットを削除してもよろしいですか？ （削除する前に必ずバックアップしてください）"
		HoneyLanguage.Korean.code -> "지갑을 삭제 하시겠습니까? (삭제하기 전에 반드시 백업 해 두십시오)"
		HoneyLanguage.Russian.code -> "Вы действительно хотите удалить кошелек? (Обязательно создайте резервную копию перед удалением)"
		HoneyLanguage.TraditionalChinese.code -> "確認要刪除錢包嗎？ (刪除前請確保已妥善備份)"
		else -> "Are you sure to delete current wallet? (Be sure you have backed up it)"
	}
	@JvmField
	val deleteInfoSubtitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Before you delete your wallet please backup your wallet information, we never save your data, so we can't recovery this operation"
		HoneyLanguage.Chinese.code -> "在删除您的钱包之前，请备份您的钱包信息，我们绝不会保存您的数据，因此我们无法恢复此操作"
		HoneyLanguage.Japanese.code -> "ウォレットを削除する前に、ウォレットの情報をバックアップしてください。データは保存されないため、この操作を回復することはできません"
		HoneyLanguage.Korean.code -> "지갑을 지우기 전에 지갑 정보를 백업하십시오. 데이터를 저장하지 않으므로이 작업을 복구 할 수 없습니다."
		HoneyLanguage.Russian.code -> "Прежде чем удалять свой кошелек, пожалуйста, создайте резервную копию информации о кошельке, мы никогда не сохраняем ваши данные, поэтому мы не можем восстановить эту операцию"
		HoneyLanguage.TraditionalChinese.code -> "在刪除您的錢包之前，請備份您的錢包信息，我們絕不會保存您的數據，因此我們無法恢復此操作"
		else -> "Before you delete your wallet please backup your wallet information, we never save your data, so we can't recovery this operation"
	}
	@JvmField
	val oldPassword = when (currentLanguage) {
		HoneyLanguage.English.code -> "Old Password"
		HoneyLanguage.Chinese.code -> "旧密码"
		HoneyLanguage.Japanese.code -> "古いパスワード"
		HoneyLanguage.Korean.code -> "이전 암호"
		HoneyLanguage.Russian.code -> "Старый пароль"
		HoneyLanguage.TraditionalChinese.code -> "舊密碼"
		else -> "Old Password"
	}
	@JvmField
	val newPassword = when (currentLanguage) {
		HoneyLanguage.English.code -> "New Password"
		HoneyLanguage.Chinese.code -> "新密码"
		HoneyLanguage.Japanese.code -> "新しいパスワード"
		HoneyLanguage.Korean.code -> "새 암호"
		HoneyLanguage.Russian.code -> "Новый пароль"
		HoneyLanguage.TraditionalChinese.code -> "新密碼"
		else -> "New Password"
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
		else -> "Profile"
	}
	@JvmField
	val contacts = when (currentLanguage) {
		HoneyLanguage.English.code -> "Contacts"
		HoneyLanguage.Chinese.code -> "通讯录"
		HoneyLanguage.Japanese.code -> "連絡先"
		HoneyLanguage.Korean.code -> "연락처"
		HoneyLanguage.Russian.code -> "контакты"
		HoneyLanguage.TraditionalChinese.code -> "聯繫人"
		else -> "Contacts"
	}
	@JvmField
	val contactsInput = when (currentLanguage) {
		HoneyLanguage.English.code -> "Add Contact"
		HoneyLanguage.Chinese.code -> "添加联系人"
		HoneyLanguage.Japanese.code -> "連絡先を追加"
		HoneyLanguage.Korean.code -> "연락처 추가"
		HoneyLanguage.Russian.code -> "Добавить контакт"
		HoneyLanguage.TraditionalChinese.code -> "添加聯繫人"
		else -> "Add Contact"
	}
	@JvmField
	val currency = when (currentLanguage) {
		HoneyLanguage.English.code -> "Currency Settings"
		HoneyLanguage.Chinese.code -> "货币设置"
		HoneyLanguage.Japanese.code -> "自国通貨"
		HoneyLanguage.Korean.code -> "통화 설정"
		HoneyLanguage.Russian.code -> "Настройки валюты"
		HoneyLanguage.TraditionalChinese.code -> "貨幣設置"
		else -> "Currency Settings"
	}
	@JvmField
	val hint = when (currentLanguage) {
		HoneyLanguage.English.code -> "Password Hint"
		HoneyLanguage.Chinese.code -> "密码提示"
		HoneyLanguage.Japanese.code -> "パスワードのヒント"
		HoneyLanguage.Korean.code -> "비밀번호 힌트"
		HoneyLanguage.Russian.code -> "Изменить пароль"
		HoneyLanguage.TraditionalChinese.code -> "密碼提示"
		else -> "Password Hint"
	}
	@JvmField
	val language = when (currentLanguage) {
		HoneyLanguage.English.code -> "Language"
		HoneyLanguage.Chinese.code -> "语言"
		HoneyLanguage.Japanese.code -> "言語"
		HoneyLanguage.Korean.code -> "언어"
		HoneyLanguage.Russian.code -> "язык"
		HoneyLanguage.TraditionalChinese.code -> "語言"
		else -> "Language"
	}
	@JvmField
	val aboutUs = when (currentLanguage) {
		HoneyLanguage.English.code -> "About Us"
		HoneyLanguage.Chinese.code -> "关于我们"
		HoneyLanguage.Japanese.code -> "チーム紹介"
		HoneyLanguage.Korean.code -> "회사 소개"
		HoneyLanguage.Russian.code -> "О КОМПАНИИ"
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> "About Us"
	}
	@JvmField
	val pinCode = when (currentLanguage) {
		HoneyLanguage.English.code -> "Pin Code"
		HoneyLanguage.Chinese.code -> "Pin 密码"
		HoneyLanguage.Japanese.code -> "ピンコード"
		HoneyLanguage.Korean.code -> "핀 코드"
		HoneyLanguage.Russian.code -> "Контактный код"
		HoneyLanguage.TraditionalChinese.code -> "PIN碼"
		else -> "Pin Code"
	}
}

object EmptyText {

	@JvmField
	val tokenDetailTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "No Token Transactions Found"
		HoneyLanguage.Chinese.code -> "还没有任何交易记录"
		HoneyLanguage.Japanese.code -> "トランザクション履歴はまだありません"
		HoneyLanguage.Korean.code -> "토큰 트랜잭션 없음"
		HoneyLanguage.Russian.code -> "Не найдено никаких транзакций токена"
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> "No Token Transactions Found"
	}
	@JvmField
	val tokenDetailSubtitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "There isn't transaction in blockchain, so you haven't chart and records"
		HoneyLanguage.Chinese.code -> "在区块链上没有交易，也不会有图表和记录信息。"
		HoneyLanguage.Japanese.code -> "ブロックチェーンにはトランザクションはなく、チャートやレコードはありません。"
		HoneyLanguage.Korean.code -> "블록 체인에는 트랜잭션이 없으므로 차트 및 레코드가 없습니다"
		HoneyLanguage.Russian.code -> "В блокчейне нет транзакций, поэтому у вас нет диаграмм и записей"
		HoneyLanguage.TraditionalChinese.code -> "區塊鏈中沒有交易，所以您沒有圖表和記錄"
		else -> "There isn't transaction in blockchain, so you haven't chart and records"
	}
	@JvmField
	val searchTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "No Result"
		HoneyLanguage.Chinese.code -> "没有您要检索的结果"
		HoneyLanguage.Japanese.code -> "取得したい結果がありません"
		HoneyLanguage.Korean.code -> "검색 할 결과가 없습니다"
		HoneyLanguage.Russian.code -> "Нет результатов, которые вы хотите получить"
		HoneyLanguage.TraditionalChinese.code -> "没有找到对应的代币"
		else -> "No Result"
	}
	@JvmField
	val searchSubtitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "No Token Found"
		HoneyLanguage.Chinese.code -> "没有找到对应的代币"
		HoneyLanguage.Japanese.code -> "トークントランザクションが見つかりません"
		HoneyLanguage.Korean.code -> "아직 거래 내역이 없습니다"
		HoneyLanguage.Russian.code -> "Соответствующий токен не найден"
		HoneyLanguage.TraditionalChinese.code -> "没有找到对应的代币"
		else -> "No Token Found"
	}
	@JvmField
	val contractTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "No Contact Found"
		HoneyLanguage.Chinese.code -> "通讯录里空空的"
		HoneyLanguage.Japanese.code -> "まだ連絡がありません"
		HoneyLanguage.Korean.code -> "아직 연락이 없습니다"
		HoneyLanguage.Russian.code -> "Пока нет контакта"
		HoneyLanguage.TraditionalChinese.code -> "通訊簿里沒有記錄"
		else -> "No Contact Found"
	}
	@JvmField
	val contractSubtitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Click the plus sign in the upper left corner to add the address of a common contact"
		HoneyLanguage.Chinese.code -> "點擊左上角加號，可以添加常用聯繫人的地址"
		HoneyLanguage.Japanese.code -> "左上隅のプラス記号をクリックして、共通の連絡先のアドレスを追加します"
		HoneyLanguage.Korean.code -> "왼쪽 상단의 더하기 기호를 클릭하여 공통 연락처의 주소를 추가하십시오"
		HoneyLanguage.Russian.code -> "Нажмите знак «плюс» в верхнем левом углу, чтобы добавить адрес общего контакта"
		HoneyLanguage.TraditionalChinese.code -> "點擊左上角加號，可以添加常用聯繫人的地址"
		else -> "Click the plus sign in the upper left corner to add the address of a common contact"
	}
	@JvmField
	val currency = when (currentLanguage) {
		HoneyLanguage.English.code -> "Currency Settings"
		HoneyLanguage.Chinese.code -> "通貨の設定"
		HoneyLanguage.Japanese.code -> "自国通貨"
		HoneyLanguage.Korean.code -> "통화 설정"
		HoneyLanguage.Russian.code -> "Настройки валюты"
		HoneyLanguage.TraditionalChinese.code -> "貨幣設置"
		else -> "Currency Settings"
	}
	@JvmField
	val language = when (currentLanguage) {
		HoneyLanguage.English.code -> "Language"
		HoneyLanguage.Chinese.code -> "语言"
		HoneyLanguage.Japanese.code -> "言语"
		HoneyLanguage.Korean.code -> "언어"
		HoneyLanguage.Russian.code -> "язык"
		HoneyLanguage.TraditionalChinese.code -> "語言"
		else -> "Language"
	}
	@JvmField
	val aboutUs = when (currentLanguage) {
		HoneyLanguage.English.code -> "About Us"
		HoneyLanguage.Chinese.code -> "关于我们"
		HoneyLanguage.Japanese.code -> "チーム情報"
		HoneyLanguage.Korean.code -> "회사 소개"
		HoneyLanguage.Russian.code -> "О КОМПАНИИ"
		HoneyLanguage.TraditionalChinese.code -> "關於我們"
		else -> "About Us"
	}
}

object QuotationText {

	@JvmField
	val market = when (currentLanguage) {
		HoneyLanguage.English.code -> "Quotation"
		HoneyLanguage.Chinese.code -> "市场行情"
		HoneyLanguage.Japanese.code -> "通貨相場"
		HoneyLanguage.Korean.code -> "시장 시세"
		HoneyLanguage.Russian.code -> "Рыночные котировки"
		HoneyLanguage.TraditionalChinese.code -> "市場行情"
		else -> "Quotation"
	}
	@JvmField
	val management = when (currentLanguage) {
		HoneyLanguage.English.code -> "My Quotation"
		HoneyLanguage.Chinese.code -> "自选管理"
		HoneyLanguage.Japanese.code -> "マイグループ"
		HoneyLanguage.Korean.code -> "자동 선거 관리"
		HoneyLanguage.Russian.code -> "Моя котировка"
		HoneyLanguage.TraditionalChinese.code -> "自選管理"
		else -> "My Quotation"
	}
	@JvmField
	val tokenDetail = when (currentLanguage) {
		HoneyLanguage.English.code -> "Market Token"
		HoneyLanguage.Chinese.code -> "市场交易对"
		HoneyLanguage.Japanese.code -> "市場取引のペア"
		HoneyLanguage.Korean.code -> "시장 거래 쌍"
		HoneyLanguage.Russian.code -> "Значок рынка"
		HoneyLanguage.TraditionalChinese.code -> "市場交易對"
		else -> "Market Token"
	}
	@JvmField
	val search = when (currentLanguage) {
		HoneyLanguage.English.code -> "Selection Search"
		HoneyLanguage.Chinese.code -> "搜索"
		HoneyLanguage.Japanese.code -> "検索"
		HoneyLanguage.Korean.code -> "검색"
		HoneyLanguage.Russian.code -> "поиск"
		HoneyLanguage.TraditionalChinese.code -> "搜索"
		else -> "Selection Search"
	}
	@JvmField
	val addToken = when (currentLanguage) {
		HoneyLanguage.English.code -> "Token"
		HoneyLanguage.Chinese.code -> "代币"
		HoneyLanguage.Japanese.code -> "トークン"
		HoneyLanguage.Korean.code -> "토큰"
		HoneyLanguage.Russian.code -> "знак"
		HoneyLanguage.TraditionalChinese.code -> "代幣"
		else -> "Token"
	}
	@JvmField
	val alarm = when (currentLanguage) {
		HoneyLanguage.English.code -> "Reminder"
		HoneyLanguage.Chinese.code -> "价格提醒"
		HoneyLanguage.Japanese.code -> "価格リマインダー"
		HoneyLanguage.Korean.code -> "가격 알림"
		HoneyLanguage.Russian.code -> "сигнал тревоги"
		HoneyLanguage.TraditionalChinese.code -> "價格提醒"
		else -> "Reminder"
	}
	@JvmField
	val currentPrice = when (currentLanguage) {
		HoneyLanguage.English.code -> "Curren Price"
		HoneyLanguage.Chinese.code -> "当前价格"
		HoneyLanguage.Japanese.code -> "価格"
		HoneyLanguage.Korean.code -> "가격"
		HoneyLanguage.Russian.code -> "Текущая цена"
		HoneyLanguage.TraditionalChinese.code -> "時價"
		else -> "Curren Price"
	}
	@JvmField
	val priceHistory = when (currentLanguage) {
		HoneyLanguage.English.code -> "Price History"
		HoneyLanguage.Chinese.code -> "价格历史"
		HoneyLanguage.Japanese.code -> "価格履歴"
		HoneyLanguage.Korean.code -> "가격 이력"
		HoneyLanguage.Russian.code -> "История цен"
		HoneyLanguage.TraditionalChinese.code -> "價格歷史"
		else -> "Price History"
	}

	@JvmField
	val tokenDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "Token Info"
		HoneyLanguage.Chinese.code -> "货币信息"
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
		else -> ""
	}

	@JvmField
	val tokenDescriptionPlaceHolder = when (currentLanguage) {
		HoneyLanguage.English.code -> "Token Description Content"
		HoneyLanguage.Chinese.code -> ""
		HoneyLanguage.Japanese.code -> ""
		HoneyLanguage.Korean.code -> ""
		HoneyLanguage.Russian.code -> ""
		HoneyLanguage.TraditionalChinese.code -> ""
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
		else -> "Pin Code"
	}
	@JvmField
	val repeat = when (currentLanguage) {
		HoneyLanguage.English.code -> "Repeat Pin Code"
		HoneyLanguage.Chinese.code -> "重复PIN码"
		HoneyLanguage.Japanese.code -> "PINの繰り返し"
		HoneyLanguage.Korean.code -> "PIN 반복"
		HoneyLanguage.Russian.code -> "Повторить PIN-код"
		HoneyLanguage.TraditionalChinese.code -> "重複PIN碼"
		else -> "Repeat Pin Code"
	}
	@JvmField
	val description = when (currentLanguage) {
		HoneyLanguage.English.code -> "Enter four bit pin-code ciphers"
		HoneyLanguage.Chinese.code -> "输入四位密码密码"
		HoneyLanguage.Japanese.code -> "4桁のパスワードを入力してください"
		HoneyLanguage.Korean.code -> "4 자리 암호를 입력하십시오."
		HoneyLanguage.Russian.code -> "Введите четыре битовых шифра кода"
		HoneyLanguage.TraditionalChinese.code -> "輸入四位密碼密碼"
		else -> "Enter four bit pin-code ciphers"
	}
	@JvmField
	val show = when (currentLanguage) {
		HoneyLanguage.English.code -> "Show Pin Code"
		HoneyLanguage.Chinese.code -> "显示PIN码"
		HoneyLanguage.Japanese.code -> "PINを表示する"
		HoneyLanguage.Korean.code -> "PIN 표시"
		HoneyLanguage.Russian.code -> "Показать PIN-код"
		HoneyLanguage.TraditionalChinese.code -> "顯示PIN碼"
		else -> "Show Pin Code"
	}
}

enum class HoneyLanguage(val code: Int, val language: String) {
	English(0, "English"),
	Chinese(1, "Chinese"),
	Japanese(2, "Japanese"),
	Russian(3, "Russian"),
	Korean(4, "Korean"),
	TraditionalChinese(5, "TraditionalChinese");

	companion object {
		fun getLanguageCode(language: String): Int {
			return when (language) {
				HoneyLanguage.English.language -> HoneyLanguage.English.code
				HoneyLanguage.Chinese.language -> HoneyLanguage.Chinese.code
				HoneyLanguage.Japanese.language -> HoneyLanguage.Japanese.code
				HoneyLanguage.Russian.language -> HoneyLanguage.Russian.code
				HoneyLanguage.Korean.language -> HoneyLanguage.Korean.code
				HoneyLanguage.TraditionalChinese.language -> HoneyLanguage.TraditionalChinese.code
				else -> 100
			}
		}

		fun getLanguageSymbol(code: Int): String {
			return when (code) {
				HoneyLanguage.English.code  -> "EN"
				HoneyLanguage.Chinese.code  -> "ZH"
				HoneyLanguage.Japanese.code -> "JP"
				HoneyLanguage.Russian.code -> "RU"
				HoneyLanguage.Korean.code -> "KR"
				HoneyLanguage.TraditionalChinese.code -> "TC"
				else -> ""
			}
		}
	}
}
