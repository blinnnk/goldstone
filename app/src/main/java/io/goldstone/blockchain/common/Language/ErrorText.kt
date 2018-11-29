package io.goldstone.blockchain.common.language

/**
 * @date 2018/9/29 18:25 AM
 * @author Rita
 */

object ErrorText {
	@JvmField
	val balanceIsNotEnough = when (currentLanguage) {
		HoneyLanguage.English.code -> "Insufficient current account balance"
		HoneyLanguage.Chinese.code -> "当前账户余额不足"
		HoneyLanguage.Japanese.code -> "経常収支の不足"
		HoneyLanguage.Korean.code -> "경상 수지 부족"
		HoneyLanguage.Russian.code -> "Недостаточный баланс текущего счета"
		HoneyLanguage.TraditionalChinese.code -> "當前賬戶餘額不足"
		else -> ""
	}
	@JvmField
	val sellRAMTooLess = when (currentLanguage) {
		HoneyLanguage.English.code -> "The RAM sold needs to be larger than 1 byte (bytes)."
		HoneyLanguage.Chinese.code -> "卖出的RAM需要大于1byte(字节)."
		HoneyLanguage.Japanese.code -> "販売されるRAMは1バイト（バイト）より大きくする必要があります。"
		HoneyLanguage.Korean.code -> "판매 된 RAM은 1 바이트 (바이트)보다 커야합니다."
		HoneyLanguage.Russian.code -> "Объем продаваемой ОЗУ должен быть больше 1 байта (байты)."
		HoneyLanguage.TraditionalChinese.code -> "賣出的RAM需要大於1byte(字節)."
		else -> ""
	}
	@JvmField
	val incorrectDecimal = when (currentLanguage) {
		HoneyLanguage.English.code -> "The decimal point precision exceeds the range allowed by the current token. Please shorten the decimal part a bit."
		HoneyLanguage.Chinese.code -> "小数点精度超过了当前token允许的范围，请把小数部分改短一点"
		HoneyLanguage.Japanese.code -> "小数点の精度が現在のトークンで許容される範囲を超えています。小数点以下は少し小さくしてください。"
		HoneyLanguage.Korean.code -> "소수점 정밀도가 현재 토큰에서 허용하는 범위를 초과합니다. 소수 부분을 조금 줄이십시오."
		HoneyLanguage.Russian.code -> "Точность десятичной точки превышает диапазон, разрешенный текущим токеном. Немного сократите десятичную часть."
		HoneyLanguage.TraditionalChinese.code -> "小數點精度超過了當前token允許的範圍，請把小數部分改短一點"
		else -> ""
	}
	@JvmField
	val getWrongFeeFromChain = when (currentLanguage) {
		HoneyLanguage.English.code -> "Failed to get the fee information from the chain"
		HoneyLanguage.Chinese.code -> "从链上获取手续费信息失败"
		HoneyLanguage.Japanese.code -> "チェーンから料金情報を取得できませんでした"
		HoneyLanguage.Korean.code -> "사슬에서 수수료 정보를 가져 오는 데 실패했습니다."
		HoneyLanguage.Russian.code -> "Не удалось получить информацию о платежах из сети"
		HoneyLanguage.TraditionalChinese.code -> "從鏈上獲取手續費信息失敗"
		else -> ""
	}

	@JvmField
	val tradingInputIsEmpty = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please enter the quantity you want to trade."
		HoneyLanguage.Chinese.code -> "请输入想交易的数量"
		HoneyLanguage.Japanese.code -> "あなたが取引したい数量を入力してください。"
		HoneyLanguage.Korean.code -> "거래하고자하는 수량을 입력하십시오."
		HoneyLanguage.Russian.code -> "Укажите количество, которое вы хотите продать."
		HoneyLanguage.TraditionalChinese.code -> "請輸入想交易的數量"
		else -> ""
	}

	@JvmField
	val decryptKeyStoreError = when (currentLanguage) {
		HoneyLanguage.English.code -> "Decrypt your keystore by password found error"
		HoneyLanguage.Chinese.code -> "不是正确的密码"
		HoneyLanguage.Japanese.code -> "交易資源時不能轉移給自​​己。"
		HoneyLanguage.Korean.code -> "비밀번호 찾기 오류로 키 스토어의 암호 해독"
		HoneyLanguage.Russian.code -> "Расшифруйте хранилище ключей по ошибке, найденной паролем"
		HoneyLanguage.TraditionalChinese.code -> "不是正確的密碼"
		else -> ""
	}

	@JvmField
	val invalidAccountName = when (currentLanguage) {
		HoneyLanguage.English.code -> "Invalid eos account name"
		HoneyLanguage.Chinese.code -> "无效的eos帐户名称"
		HoneyLanguage.Japanese.code -> "無効なeosアカウント名"
		HoneyLanguage.Korean.code -> "잘못된 eos 계정 이름"
		HoneyLanguage.Russian.code -> "Недопустимое имя учетной записи eos"
		HoneyLanguage.TraditionalChinese.code -> "無效的eos帳戶名稱"
		else -> ""
	}

	@JvmField
	val invalidChainAddress: (symbol: String) -> String = {
		when (currentLanguage) {
			HoneyLanguage.English.code -> "This is a invalid address type for $it, Please check it again"
			HoneyLanguage.Chinese.code -> "This is a invalid address type for $it, Please check it again"
			HoneyLanguage.Japanese.code -> "This is a invalid address type for $it, Please check it again"
			HoneyLanguage.Korean.code -> "This is a invalid address type for $it, Please check it again"
			HoneyLanguage.Russian.code -> "This is a invalid address type for $it, Please check it again"
			HoneyLanguage.TraditionalChinese.code -> "This is a invalid address type for $it, Please check it again"
			else -> ""
		}
	}

	@JvmField
	val emptyName = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please enter the account name which you decide to register"
		HoneyLanguage.Chinese.code -> "请输入您决定注册的帐户名称"
		HoneyLanguage.Japanese.code -> "登録することを決めたアカウントの名前を入力してください"
		HoneyLanguage.Korean.code -> "등록하기로 결정한 계정 이름을 입력하십시오."
		HoneyLanguage.Russian.code -> "Введите имя учетной записи, которую вы решили зарегистрировать"
		HoneyLanguage.TraditionalChinese.code -> "請輸入您決定註冊的帳戶名稱"
		else -> ""
	}

	@JvmField
	val emptyRepeatPassword = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please repeat password to confirm."
		HoneyLanguage.Chinese.code -> "请填写重复密码。"
		HoneyLanguage.Japanese.code -> "確認のためにパスワードを繰り返してください。"
		HoneyLanguage.Korean.code -> "반복 암호가 비어 있습니다."
		HoneyLanguage.Russian.code -> "Повторить пароль сейчас пуст."
		HoneyLanguage.TraditionalChinese.code -> "請填寫重複密碼。"
		else -> ""
	}

	@JvmField
	val differentRepeatPassword = when (currentLanguage) {
		HoneyLanguage.English.code -> "Passwords do not match."
		HoneyLanguage.Chinese.code -> "输入两次的密码不一致"
		HoneyLanguage.Japanese.code -> "パスワードが一致しません。"
		HoneyLanguage.Korean.code -> "일치하지 않는 암호가 두 번 입력되었습니다."
		HoneyLanguage.Russian.code -> "Неверный пароль вводится дважды."
		HoneyLanguage.TraditionalChinese.code -> "兩次輸入的密碼不一致。"
		else -> ""
	}

	@JvmField
	val agreeTerms = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please read and agree to the terms."
		HoneyLanguage.Chinese.code -> "请阅读并同意用户条款。"
		HoneyLanguage.Japanese.code -> "ユーザー規約を読み、同意してください。"
		HoneyLanguage.Korean.code -> "약관을 읽고 동의하십시오."
		HoneyLanguage.Russian.code -> "Прочтите и согласитесь с условиями."
		HoneyLanguage.TraditionalChinese.code -> "請閱讀並同意用戶條條款。"
		else -> ""
	}

	@JvmField
	val invalidMnemonic = when (currentLanguage) {
		HoneyLanguage.English.code -> "Incorrect mnemonic format"
		HoneyLanguage.Chinese.code -> "助记符格式不正确"
		HoneyLanguage.Japanese.code -> "ニーモニック形式が正しくありません"
		HoneyLanguage.Korean.code -> "잘못된 니모닉 형식"
		HoneyLanguage.Russian.code -> "Недействительный мнемонический формат"
		HoneyLanguage.TraditionalChinese.code -> "助記符格式不正確"
		else -> ""
	}

	@JvmField
	val invalidBip44Path = when (currentLanguage) {
		HoneyLanguage.English.code -> "Incorrect Bip44 Path"
		HoneyLanguage.Chinese.code -> "Bip44 路径不正确"
		HoneyLanguage.Japanese.code -> "Bip44 のパスが正しくありません"
		HoneyLanguage.Korean.code -> "잘못된 Bip44 경로"
		HoneyLanguage.Russian.code -> "Неправильный путь Bip43"
		HoneyLanguage.TraditionalChinese.code -> "Bip44 路徑不正確"
		else -> ""
	}

	@JvmField
	val emptyPublicKey = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please enter the public key which you decide to bind the account name"
		HoneyLanguage.Chinese.code -> "请输入您决定绑定帐户名称的公钥"
		HoneyLanguage.Japanese.code -> "アカウント名をバインドすることを決定した公開鍵を入力してください。"
		HoneyLanguage.Korean.code -> "계정 이름을 바인드하기로 결정한 공개 키를 입력하십시오."
		HoneyLanguage.Russian.code -> "Введите открытый ключ, который вы решите связать имя учетной записи"
		HoneyLanguage.TraditionalChinese.code -> "請輸入您決定綁定帳戶名稱的公鑰"
		else -> ""
	}

	@JvmField
	val wrongPassword = when (currentLanguage) {
		HoneyLanguage.English.code -> "Wrong Password"
		HoneyLanguage.Chinese.code -> "密码错误"
		HoneyLanguage.Japanese.code -> "間違ったパスワード"
		HoneyLanguage.Korean.code -> "잘못된 비밀번호"
		HoneyLanguage.Russian.code -> "Неправильный пароль"
		HoneyLanguage.TraditionalChinese.code -> "密碼錯誤"
		else -> ""
	}

	@JvmField
	val invalidAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "Address Formatted is Invalid"
		HoneyLanguage.Chinese.code -> "地址格式无效"
		HoneyLanguage.Japanese.code -> "無効なアドレス形式"
		HoneyLanguage.Korean.code -> "서식이 지정된 주소가 잘못되었습니다."
		HoneyLanguage.Russian.code -> "Неверный формат адреса"
		HoneyLanguage.TraditionalChinese.code -> "地址格式無效"
		else -> ""
	}

	@JvmField
	val existAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "This Address Has Existed In Your Wallet"
		HoneyLanguage.Chinese.code -> "此地址已存在于您的钱包中"
		HoneyLanguage.Japanese.code -> "このアドレスは既にあなたのウォレットにあります"
		HoneyLanguage.Korean.code -> "이 주소는 월렛에 존재합니다."
		HoneyLanguage.Russian.code -> "Этот адрес существует в вашем кошельке"
		HoneyLanguage.TraditionalChinese.code -> "此地址已存在於您的錢包中"
		else -> ""
	}

	@JvmField
	val invalidPrivateKey = when (currentLanguage) {
		HoneyLanguage.English.code -> "Invalid Private Key"
		HoneyLanguage.Chinese.code -> "私钥无效"
		HoneyLanguage.Japanese.code -> "秘密鍵が無効です"
		HoneyLanguage.Korean.code -> "잘못된 개인 키"
		HoneyLanguage.Russian.code -> "Недопустимый закрытый ключ"
		HoneyLanguage.TraditionalChinese.code -> "私鑰無效"
		else -> ""
	}

	@JvmField
	val passwordFormatted = when (currentLanguage) {
		HoneyLanguage.English.code -> "Password Formatted is Wrong "
		HoneyLanguage.Chinese.code -> "密码格式错误"
		HoneyLanguage.Japanese.code -> "間違ったパスワード形式"
		HoneyLanguage.Korean.code -> "암호가 잘못되었습니다."
		HoneyLanguage.Russian.code -> "Неверный формат пароля"
		HoneyLanguage.TraditionalChinese.code -> "Неверный формат пароля"
		else -> ""
	}

	@JvmField
	val postFailed = when (currentLanguage) {
		HoneyLanguage.English.code -> "Post error"
		HoneyLanguage.Chinese.code -> "请求出错"
		HoneyLanguage.Japanese.code -> "リクエストエラー"
		HoneyLanguage.Korean.code -> "요청 오류"
		HoneyLanguage.Russian.code -> "Ошибка запроса"
		HoneyLanguage.TraditionalChinese.code -> "請求出錯"
		else -> ""
	}
	@JvmField
	val resolveDataError = when (currentLanguage) {
		HoneyLanguage.English.code -> "Failed to parse the return value correctly"
		HoneyLanguage.Chinese.code -> "未能正确解析返回值"
		HoneyLanguage.Japanese.code -> "戻り値を正しく解析できませんでした"
		HoneyLanguage.Korean.code -> "반환 값을 올바르게 구문 분석하지 못했습니다."
		HoneyLanguage.Russian.code -> "Не удалось правильно проанализировать возвращаемое значение"
		HoneyLanguage.TraditionalChinese.code -> "未能正確解析返回值"
		else -> ""
	}
	@JvmField
	val nullResponse = when (currentLanguage) {
		HoneyLanguage.English.code -> "Not getting a valid return value"
		HoneyLanguage.Chinese.code -> "未获取有效返回值"
		HoneyLanguage.Japanese.code -> "有効な戻り値が取得されない"
		HoneyLanguage.Korean.code -> "유효한 반환 값을 얻지 못했습니다."
		HoneyLanguage.Russian.code -> "Не получать действительное возвращаемое значение"
		HoneyLanguage.TraditionalChinese.code -> "返回值為空"
		else -> ""
	}

	@JvmField
	val none = when (currentLanguage) {
		HoneyLanguage.English.code -> "No error message."
		HoneyLanguage.Chinese.code -> "没有错误信息"
		HoneyLanguage.Japanese.code -> "エラーメッセージなし"
		HoneyLanguage.Korean.code -> "오류 메시지 없음"
		HoneyLanguage.Russian.code -> "Нет сообщения об ошибке"
		HoneyLanguage.TraditionalChinese.code -> "沒有錯誤信息"
		else -> ""
	}
	@JvmField
	val error = when (currentLanguage) {
		HoneyLanguage.English.code -> "Error"
		HoneyLanguage.Chinese.code -> "出错了"
		HoneyLanguage.Japanese.code -> "何かが間違っていた"
		HoneyLanguage.Korean.code -> "틀린"
		HoneyLanguage.Russian.code -> "Что-то пошло не так."
		HoneyLanguage.TraditionalChinese.code -> "出錯了"
		else -> ""
	}

	@JvmField
	val eosNameResultUnavailable = when (currentLanguage) {
		HoneyLanguage.English.code -> "The account name has already been registered by someone else"
		HoneyLanguage.Chinese.code -> "该用户名已经被别人注册"
		HoneyLanguage.Japanese.code -> "ユーザー名は既に他のユーザーによって登録されています"
		HoneyLanguage.Korean.code -> "이미 다른 사용자가 사용자 이름을 등록했습니다."
		HoneyLanguage.Russian.code -> "Имя пользователя уже зарегистрировано кем-то другим."
		HoneyLanguage.TraditionalChinese.code -> "該用戶名已經被別人註冊`"
		else -> ""
	}

	@JvmField
	val inactivatedAccountName = when (currentLanguage) {
		HoneyLanguage.English.code->"This account name has not been activated yet"
		HoneyLanguage.Chinese.code->"这个账户没有被别人注册, 你可以使用。"
		HoneyLanguage.Japanese.code->"このアカウントは他人によって登録されておらず、あなたはそれを使用することができます。"
		HoneyLanguage.Korean.code->"이 계정은 다른 사람이 등록하지 않았으므로이 계정을 사용할 수 있습니다."
		HoneyLanguage.Russian.code->"Эта учетная запись не зарегистрирована кем-то еще, и вы можете ее использовать."
		HoneyLanguage.TraditionalChinese.code->"這個賬戶沒有被別人註冊, 你可以使用。"
		else -> ""
	}
}