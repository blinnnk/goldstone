package io.goldstone.blinnnk.common.language

/**
 * @date 2018/9/29 18:25 AM
 * @author Rita
 */

object ErrorText {
	@JvmField
	val inputTooBig = when (currentLanguage) {
		HoneyLanguage.English.code -> "The value you entered is too large and is outside the allowable range."
		HoneyLanguage.Chinese.code -> "您输入的数值过大，超出了允许的范围。"
		HoneyLanguage.Japanese.code -> "入力した値が大きすぎ、許容範囲外です。"
		HoneyLanguage.Korean.code -> "입력 한 값이 너무 커서 허용 범위를 벗어났습니다."
		HoneyLanguage.Russian.code -> "Введенное вами значение слишком велико и выходит за допустимый диапазон."
		HoneyLanguage.TraditionalChinese.code -> "您輸入的數值過大，超出了允許的範圍。"
		else -> ""
	}

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
		HoneyLanguage.Chinese.code -> "卖出的RAM需要大于 1 byte(字节)."
		HoneyLanguage.Japanese.code -> "販売されるRAMは1バイト（バイト）より大きくする必要があります。"
		HoneyLanguage.Korean.code -> "판매 된 RAM은 1 바이트 (바이트)보다 커야합니다."
		HoneyLanguage.Russian.code -> "Объем продаваемой ОЗУ должен быть больше 1 байта (байты)."
		HoneyLanguage.TraditionalChinese.code -> "賣出的RAM需要大於 1 byte(字節)."
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
	val wrongPermission = when (currentLanguage) {
		HoneyLanguage.English.code -> "Insufficient current account permissions"
		HoneyLanguage.Chinese.code -> "当前账户权限不足"
		HoneyLanguage.Japanese.code -> "アカウントのアクセス許可が不十分です"
		HoneyLanguage.Korean.code -> "현재 계정 권한이 충분하지 않습니다."
		HoneyLanguage.Russian.code -> "Недостаточно разрешений текущей учетной записи."
		HoneyLanguage.TraditionalChinese.code -> "當前賬戶權限不足"
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
		HoneyLanguage.Japanese.code -> "エラー"
		HoneyLanguage.Korean.code -> "틀린"
		HoneyLanguage.Russian.code -> "Ошибка"
		HoneyLanguage.TraditionalChinese.code -> "出错了"
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

	// 导入观察钱包时导入了未激活的账号
	@JvmField
	val inactivedAccountName = when (currentLanguage) {
		HoneyLanguage.English.code -> "This account name has not been activated yet"
		HoneyLanguage.Chinese.code -> "这个 EOS 账户还没有被注册"
		HoneyLanguage.Japanese.code -> "このアカウントはまだ登録されていません"
		HoneyLanguage.Korean.code -> "이 계정은 아직 등록되지 않았습니다."
		HoneyLanguage.Russian.code -> "Эта учетная запись еще не зарегистрирована."
		HoneyLanguage.TraditionalChinese.code -> "這個 EOS 賬戶還沒有被註冊"
		else -> ""
	}
}

object TransactionErrorText {
	@JvmField
	val transferToInactiveEOSAcount = when (currentLanguage) {
		HoneyLanguage.English.code -> "The EOS account you want to transfer to doesn't exist."
		HoneyLanguage.Chinese.code -> "你想要转账的EOS账户尚未激活"
		HoneyLanguage.Japanese.code -> "転送したいEOSアカウントは有効化されていません"
		HoneyLanguage.Korean.code -> "전송하려는 EOS 계정이 활성화되지 않았습니다."
		HoneyLanguage.Russian.code -> "Учетная запись EOS, которую вы хотите передать, не активирована."
		HoneyLanguage.TraditionalChinese.code -> "你想要轉賬的EOS賬戶尚未激活"
		else -> ""
	}

	@JvmField
	val emptyConfirmPassword = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please enter password to unlock your wallet."
		HoneyLanguage.Chinese.code -> "请输入密码以解锁钱包"
		HoneyLanguage.Japanese.code -> "ウォレットのロックを解除するにはパスワードを入力してください"
		HoneyLanguage.Korean.code -> "지갑 잠금을 해제하려면 비밀번호를 입력하십시오."
		HoneyLanguage.Russian.code -> "Введите пароль, чтобы разблокировать свой кошелек."
		HoneyLanguage.TraditionalChinese.code -> "請輸入密碼以解鎖錢包"
		else -> ""
	}
	// 余额不足以支付燃气费的提示
	@JvmField
	val notEnoughGasFee = when (currentLanguage) {
		HoneyLanguage.English.code -> "Your account's ETH balance is not sufficient to pay for gas."
		HoneyLanguage.Chinese.code -> "您账户的ETH余额不足以支付燃气费"
		HoneyLanguage.Japanese.code -> "アカウントのETH残高はガスのために支払うために十分ではありません"
		HoneyLanguage.Korean.code -> "계정의 ETH 잔액으로 가스 요금을 지불하기에 충분하지 않습니다."
		HoneyLanguage.Russian.code -> "Баланса ETH для вашей учетной записи недостаточно для оплаты газа."
		HoneyLanguage.TraditionalChinese.code -> "您賬戶的ETH餘額不足以支付燃氣費"
		else -> ""
	}

}

object WalletErrorText {
	@JvmField
	val mnemonicsBackUpReminder = when (currentLanguage) {
		HoneyLanguage.English.code -> "Please back up your mnemonics before proceeding with asset operations."
		HoneyLanguage.Chinese.code -> "请先备份好助记词再进行资产操作。"
		HoneyLanguage.Japanese.code -> "資産操作を続行する前にニーモニックをバックアップしてください。"
		HoneyLanguage.Korean.code -> "자산 조작을 계속하기 전에 니모닉을 백업하십시오."
		HoneyLanguage.Russian.code -> "Пожалуйста, создайте резервную копию своей мнемоники перед продолжением операций с активами."
		HoneyLanguage.TraditionalChinese.code -> "請先備份好助記詞再進行資產操作。"
		else -> ""
	}
}

object ChainErrorText {
	@JvmField
	val getKeyAccountsError = when (currentLanguage) {
		HoneyLanguage.English.code -> "Failed to get the EOS account list correctly from chain."
		HoneyLanguage.Chinese.code -> "未能从链上正确获取EOS账户列表。"
		HoneyLanguage.Japanese.code -> "チェーンからEOSアカウントリストを正しく取得できませんでした。"
		HoneyLanguage.Korean.code -> "체인에서 EOS 계정 목록을 제대로 가져 오지 못했습니다."
		HoneyLanguage.Russian.code -> "Не удалось правильно получить список учетных записей EOS из цепочки."
		HoneyLanguage.TraditionalChinese.code -> "未能從鏈上正確獲取EOS賬戶列表。"
		else -> ""
	}
	@JvmField
	val getEOSBalanceError = when (currentLanguage) {
		HoneyLanguage.English.code -> "Failed to update currency balances from EOS chain."
		HoneyLanguage.Chinese.code -> "未能从EOS链上正确更新货币余额。"
		HoneyLanguage.Japanese.code -> "EOSチェーンから通貨バランスを正しく更新できませんでした。"
		HoneyLanguage.Korean.code -> "EOS 체인의 통화 잔액을 올바르게 업데이트하지 못했습니다."
		HoneyLanguage.Russian.code -> "Не удалось правильно обновить валютный баланс из сети EOS."
		HoneyLanguage.TraditionalChinese.code -> "未能從鏈上正確獲取EOS賬戶列表。"
		else -> ""
	}
}

object EosResourceErrorText {
	@JvmField
	val ramNoDecimals = when (currentLanguage) {
		HoneyLanguage.English.code -> "The smallest unit of memory trading is bytes (Byte, 1 KB = 1024 Byte). Decimal numbers are not supported. Please check your input."
		HoneyLanguage.Chinese.code -> "内存交易的最小单位是字节(Byte, 1 KB = 1024 Byte)，不支持小数数字，请检查您的输入。"
		HoneyLanguage.Japanese.code -> "メモリ取引の最小単位はバイト(Byte, 1 KB = 1024 Byte)です。小数はサポートされていません。入力を確認してください。"
		HoneyLanguage.Korean.code -> "메모리 거래의 최소 단위는 바이트 (Byte, 1 KB = 1024 Byte) 이며 십진수는 지원되지 않습니다. 입력을 확인하십시오."
		HoneyLanguage.Russian.code -> "Наименьшая единица памяти - байты(Byte, 1 KB = 1024 Byte). Десятичные числа не поддерживаются. Проверьте свои данные."
		HoneyLanguage.TraditionalChinese.code -> "內存交易的最小單位是字節(Byte, 1 KB = 1024 Byte)，不支持小數數字，請檢查您的輸入。"
		else -> ""
	}
	@JvmField
	val ramNotEnoughForNewAccount = when (currentLanguage) {
		HoneyLanguage.English.code -> "Your RAM balance is not sufficient to activate your new account."
		HoneyLanguage.Chinese.code -> "您的内存(RAM)余额不足以激活新账号。"
		HoneyLanguage.Japanese.code -> "RAMの残高では、新しいアカウントを有効にすることはできません。"
		HoneyLanguage.Korean.code -> "RAM 잔액만으로는 새 계정을 활성화 할 수 없습니다."
		HoneyLanguage.Russian.code -> "При балансе RAM вы не сможете включить новые учетные записи."
		HoneyLanguage.TraditionalChinese.code -> "您的內存(RAM)餘額不足以激活新賬號。"
		else -> ""
	}

	@JvmField
	val cpuNotEnough = when (currentLanguage) {
		HoneyLanguage.English.code -> "Your CPU balance is insufficient."
		HoneyLanguage.Chinese.code -> "您的 CPU (计算) 余额不足。"
		HoneyLanguage.Japanese.code -> "CPU（計算）の残高が不足しています。"
		HoneyLanguage.Korean.code -> "CPU (계산) 잔액이 부족합니다."
		HoneyLanguage.Russian.code -> "Ваш CPU (расчетный) баланс недостаточен."
		HoneyLanguage.TraditionalChinese.code -> "您的 CPU (計算) 餘額不足。"
		else -> ""
	}

	@JvmField
	val netNotEnough = when (currentLanguage) {
		HoneyLanguage.English.code -> "Your NET balance is insufficient."
		HoneyLanguage.Chinese.code -> "您的 NET (计算) 余额不足。"
		HoneyLanguage.Japanese.code -> "NET（計算）の残高が不足しています。"
		HoneyLanguage.Korean.code -> "NET (계산) 잔액이 부족합니다."
		HoneyLanguage.Russian.code -> "Ваш NET (расчетный) баланс недостаточен."
		HoneyLanguage.TraditionalChinese.code -> "您的 NET (計算) 餘額不足。"
		else -> ""
	}
}