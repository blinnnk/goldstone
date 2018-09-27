package io.goldstone.blockchain.common.language

import io.goldstone.blockchain.crypto.multichain.CoinSymbol

/**
 * @date 2018/8/8 2:16 AM
 * @author KaySaith
 */

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
	val containsBTCTest = when (currentLanguage) {
		HoneyLanguage.English.code -> "(Contains Test Address)"
		HoneyLanguage.Chinese.code -> "(包含测试链地址)"
		HoneyLanguage.Japanese.code -> "(テストチェーンアドレスが含まれています)"
		HoneyLanguage.Korean.code -> "(테스트 체인 주소가 들어 있습니다)"
		HoneyLanguage.Russian.code -> "(Содержит тестовый адрес)"
		HoneyLanguage.TraditionalChinese.code -> "(包含測試鏈地址)"
		else -> ""
	}

	val addressCountSubtitle: (
		count: Int,
		description: String
	) -> String = { count, description ->

		when (currentLanguage) {
			HoneyLanguage.English.code -> if (count > 1) {
				"There are $count addresses in this wallet $description"
			} else {
				"There is $count address in this wallet $description"
			}
			HoneyLanguage.Russian.code -> if (count > 1) {
				"В текущем кошельке есть $count адреса $description"
			} else {
				"В текущем кошельке всего $count адрес $description"
			}
			HoneyLanguage.Chinese.code -> "当前钱包中一共有$count 个地址 $description"
			HoneyLanguage.Japanese.code -> "$count のアドレスがあります $description"
			HoneyLanguage.Korean.code -> "현재 지갑에는 $count 개의 주소가 있습니다 $description"
			HoneyLanguage.TraditionalChinese.code -> "當前錢包中一共有$count 個地址 $description"
			else -> ""
		}
	}

	@JvmField
	val newETHAndERCAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "New ETH Series Address"
		HoneyLanguage.Chinese.code -> "新的ETH和ERC20代币地址"
		HoneyLanguage.Japanese.code -> "新しいETHおよびERC20トークンアドレス"
		HoneyLanguage.Korean.code -> "새로운 ETH 및 ERC20 토큰 주소"
		HoneyLanguage.Russian.code -> "Новые адреса токенов ETH и ERC20"
		HoneyLanguage.TraditionalChinese.code -> "新的ETH和ERC20代幣地址"
		else -> ""
	}
	@JvmField
	val newETCAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "New ETC Address"
		HoneyLanguage.Chinese.code -> "新的ETC地址"
		HoneyLanguage.Japanese.code -> "新しいETCアドレス"
		HoneyLanguage.Korean.code -> "새 ETC 주소"
		HoneyLanguage.Russian.code -> "Новый адрес ETC"
		HoneyLanguage.TraditionalChinese.code -> "新的ETC地址"
		else -> ""
	}
	@JvmField
	val newEOSAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "New EOS Address"
		HoneyLanguage.Chinese.code -> "新的EOS地址"
		HoneyLanguage.Japanese.code -> "新しいEOSアドレス"
		HoneyLanguage.Korean.code -> "새 EOS 주소"
		HoneyLanguage.Russian.code -> "Новый адрес EOS"
		HoneyLanguage.TraditionalChinese.code -> "新的EOS地址"
		else -> ""
	}
	@JvmField
	val newBTCAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "New ${CoinSymbol.btc()} Address"
		HoneyLanguage.Chinese.code -> "新的${CoinSymbol.btc()}地址"
		HoneyLanguage.Japanese.code -> "新しい${CoinSymbol.btc()}アドレス"
		HoneyLanguage.Korean.code -> "새로운 ${CoinSymbol.btc()} 주소"
		HoneyLanguage.Russian.code -> "Новый адрес BTC"
		HoneyLanguage.TraditionalChinese.code -> "新的${CoinSymbol.btc()}地址"
		else -> ""
	}
	@JvmField
	val newLTCAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "New LTC Address"
		HoneyLanguage.Chinese.code -> "新的 LTC 地址"
		HoneyLanguage.Japanese.code -> "新しい LTC アドレス"
		HoneyLanguage.Korean.code -> "새로운 LTC 주소"
		HoneyLanguage.Russian.code -> "Новый адрес LTC"
		HoneyLanguage.TraditionalChinese.code -> "新的 LTC 地址"
		else -> ""
	}
	@JvmField
	val newBCHAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "New BCH Address"
		HoneyLanguage.Chinese.code -> "新的 BCH 地址"
		HoneyLanguage.Japanese.code -> "新しい BCH アドレス"
		HoneyLanguage.Korean.code -> "새로운 BCH 주소"
		HoneyLanguage.Russian.code -> "Новый адрес BCH"
		HoneyLanguage.TraditionalChinese.code -> "新的 BCH 地址"
		else -> ""
	}
	@JvmField
	val viewAddresses = when (currentLanguage) {
		HoneyLanguage.English.code -> "View All Addresses"
		HoneyLanguage.Chinese.code -> "查看所有地址"
		HoneyLanguage.Japanese.code -> "すべてのアドレスを表示"
		HoneyLanguage.Korean.code -> "모든 주소보기"
		HoneyLanguage.Russian.code -> "Просмотреть все адреса"
		HoneyLanguage.TraditionalChinese.code -> "查看所有地址"
		else -> ""
	}
	@JvmField
	val allETHSeriesAddresses = when (currentLanguage) {
		HoneyLanguage.English.code -> "All ETH & ERC20 Token Addresses"
		HoneyLanguage.Chinese.code -> "所有ETH和ERC20代币地址"
		HoneyLanguage.Japanese.code -> "すべてのETHおよびERC20トークンアドレス"
		HoneyLanguage.Korean.code -> "모든 ETH 및 ERC20 토큰 주소"
		HoneyLanguage.Russian.code -> "Все адреса маркеров ETH и ERC20"
		HoneyLanguage.TraditionalChinese.code -> "所有ETH和ERC20代幣地址"
		else -> ""
	}
	@JvmField
	val allETCAddresses = when (currentLanguage) {
		HoneyLanguage.English.code -> "All ETC Addresses"
		HoneyLanguage.Chinese.code -> "所有ETC地址"
		HoneyLanguage.Japanese.code -> "すべてのETCアドレス"
		HoneyLanguage.Korean.code -> "모든 기타 주소"
		HoneyLanguage.Russian.code -> "Все адреса ETC"
		HoneyLanguage.TraditionalChinese.code -> "所有ETC地址"
		else -> ""
	}
	@JvmField
	val allEOSAddresses = when (currentLanguage) {
		HoneyLanguage.English.code -> "All EOS Addresses"
		HoneyLanguage.Chinese.code -> "所有EOS地址"
		HoneyLanguage.Japanese.code -> "All EOS Addresses"
		HoneyLanguage.Korean.code -> "All EOS Addresses"
		HoneyLanguage.Russian.code -> "Все адреса EOS"
		HoneyLanguage.TraditionalChinese.code -> "所有EOS地址"
		else -> ""
	}
	@JvmField
	val allBtcAddresses = when (currentLanguage) {
		HoneyLanguage.English.code -> "All BTC Addresses"
		HoneyLanguage.Chinese.code -> "所有BTC地址"
		HoneyLanguage.Japanese.code -> "すべてのBTCアドレス"
		HoneyLanguage.Korean.code -> "모든 BTC 주소"
		HoneyLanguage.Russian.code -> "Все адреса BTC"
		HoneyLanguage.TraditionalChinese.code -> "所有BTC地址"
		else -> ""
	}
	@JvmField
	val allBCHAddresses = when (currentLanguage) {
		HoneyLanguage.English.code -> "All BCH Addresses"
		HoneyLanguage.Chinese.code -> "所有BCH(比特币现金)地址"
		HoneyLanguage.Japanese.code -> "すべての BCH アドレス"
		HoneyLanguage.Korean.code -> "모든 BCH 주소"
		HoneyLanguage.Russian.code -> "Все адреса BCH"
		HoneyLanguage.TraditionalChinese.code -> "所有BCH(比特幣現金)地址"
		else -> ""
	}
	@JvmField
	val allLTCAddresses = when (currentLanguage) {
		HoneyLanguage.English.code -> "All Litecoin Addresses"
		HoneyLanguage.Chinese.code -> "所有LTC(莱特币)地址"
		HoneyLanguage.Japanese.code -> "すべてのLTC(リテコイン)アドレス"
		HoneyLanguage.Korean.code -> "모든 Litecoin 주소"
		HoneyLanguage.Russian.code -> "Все адреса Litecoin"
		HoneyLanguage.TraditionalChinese.code -> "所有LTC(萊特幣)地址"
		else -> ""
	}
	@JvmField
	val allBtCTestAddresses = when (currentLanguage) {
		HoneyLanguage.English.code -> "All BTC Testnet Addresses"
		HoneyLanguage.Chinese.code -> "所有BTC(比特币)测试网地址"
		HoneyLanguage.Japanese.code -> "すべてのBTCテストネットワークアドレス"
		HoneyLanguage.Korean.code -> "모든 BTC 테스트 네트워크 주소"
		HoneyLanguage.Russian.code -> "Все сетевые адреса тестовой сети BTC"
		HoneyLanguage.TraditionalChinese.code -> "所有BTC(比特幣)測試網地址"
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
		HoneyLanguage.Chinese.code -> "默认地址"
		HoneyLanguage.Japanese.code -> "デフォルトアドレス"
		HoneyLanguage.Korean.code -> "기본 주소"
		HoneyLanguage.Russian.code -> "Адрес по умолчанию"
		HoneyLanguage.TraditionalChinese.code -> "默認地址"
		else -> ""
	}
	@JvmField
	val currentMultiChainAddresses = when (currentLanguage) {
		HoneyLanguage.English.code -> "Current Multi-Chain Wallet Addresses"
		HoneyLanguage.Chinese.code -> "当前的多链地址"
		HoneyLanguage.Japanese.code -> "現在のマルチチェーンアドレス"
		HoneyLanguage.Korean.code -> "현재 다중 체인 주소"
		HoneyLanguage.Russian.code -> "Текущий многоцелевой адрес"
		HoneyLanguage.TraditionalChinese.code -> "當前的多鏈地址"
		else -> ""
	}
	@JvmField
	val ethereumSeriesAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "Ethereum Address"
		HoneyLanguage.Chinese.code -> "以太坊地址"
		HoneyLanguage.Japanese.code -> "エテリアルアドレス"
		HoneyLanguage.Korean.code -> "에테르 주소"
		HoneyLanguage.Russian.code -> "Эфирный адрес"
		HoneyLanguage.TraditionalChinese.code -> "以太坊地址"
		else -> ""
	}
	@JvmField
	val ethereumClassicAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "Ethereum Classic Address"
		HoneyLanguage.Chinese.code -> "以太坊经典地址"
		HoneyLanguage.Japanese.code -> "エテリアムクラシック住所"
		HoneyLanguage.Korean.code -> "에테 리움 클래식 주소"
		HoneyLanguage.Russian.code -> "Ethereum Classic Адрес"
		HoneyLanguage.TraditionalChinese.code -> "以太坊經典地址"
		else -> ""
	}
	@JvmField
	val litecoinAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "Litecoin Cash Address"
		HoneyLanguage.Chinese.code -> "LTC(莱特币)地址"
		HoneyLanguage.Japanese.code -> "LTC(リテコイン)アドレス"
		HoneyLanguage.Korean.code -> "Litecoin 주소"
		HoneyLanguage.Russian.code -> "Адрес Litecoin"
		HoneyLanguage.TraditionalChinese.code -> "LTC(萊特幣)地址"
		else -> ""
	}
	@JvmField
	val eosAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "EOS Address"
		HoneyLanguage.Chinese.code -> "EOS地址"
		HoneyLanguage.Japanese.code -> "EOSアドレス"
		HoneyLanguage.Korean.code -> "EOS 주소"
		HoneyLanguage.Russian.code -> "Адрес EOS"
		HoneyLanguage.TraditionalChinese.code -> "EOS地址"
		else -> ""
	}
	@JvmField
	val bitcoinCashAddress = when (currentLanguage) {
		HoneyLanguage.English.code -> "Bitcoin Cash Address"
		HoneyLanguage.Chinese.code -> "BCH(比特币现金)地址"
		HoneyLanguage.Japanese.code -> "BCHアドレス"
		HoneyLanguage.Korean.code -> "Bitcoin Cash 주소"
		HoneyLanguage.Russian.code -> "Bitcoin Cash Address"
		HoneyLanguage.TraditionalChinese.code -> "BCH(比特幣現金)地址"
		else -> ""
	}
	@JvmField
	val bitcoinAddress: (isYingYongBao: Boolean) -> String = {
		when (currentLanguage) {
			HoneyLanguage.English.code -> "${HoneyLanguage.bitcoinPrefix(it)} Address"
			HoneyLanguage.Chinese.code -> "${HoneyLanguage.bitcoinPrefix(it)} 地址"
			HoneyLanguage.Japanese.code -> "${HoneyLanguage.bitcoinPrefix(it)} アドレス"
			HoneyLanguage.Korean.code -> "비트 코인 주소"
			HoneyLanguage.Russian.code -> "Адрес биткойна"
			HoneyLanguage.TraditionalChinese.code -> "比特幣地址"
			else -> ""
		}
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
	val createSubAccount = when (currentLanguage) {
		HoneyLanguage.English.code -> "Create a new subaddress"
		HoneyLanguage.Chinese.code -> "创建一个新的子地址"
		HoneyLanguage.Japanese.code -> "新しいサブアドレスを作成する"
		HoneyLanguage.Korean.code -> "새 하위 주소 만들기"
		HoneyLanguage.Russian.code -> "Создание нового субадресса"
		HoneyLanguage.TraditionalChinese.code -> "創建一個新的子地址"
		else -> ""
	}
	@JvmField
	val createSubAccountIntro = when (currentLanguage) {
		HoneyLanguage.English.code -> "Subaddresses are created following the BIP44 standard. You can restore this subaddress in any wallet software that supports BIP44 at any time via mnemonic and subaddress path."
		HoneyLanguage.Chinese.code -> "子地址遵循BIP44标准创建。你可以随时通过助记词与子地址路径在任意支持BIP44的钱包软件中恢复这个子地址"
		HoneyLanguage.Japanese.code -> "サブアドレスはBIP44規格に従って作成されます。 このサブアドレスは、任意のBIP44対応ウォレットソフトウェアで、ニーモニックとサブアドレスのパス(Path)を使用していつでも復元できます。"
		HoneyLanguage.Korean.code -> "하위 주소는 BIP44 표준에 따라 작성됩니다. 니모닉 및 하위 주소 경로(Path) 를 통해 언제든지 BIP44를 지원하는 모든 지갑 소프트웨어에서이 하위 주소를 복원 할 수 있습니다."
		HoneyLanguage.Russian.code -> "Субадресы создаются по стандарту BIP44. Вы можете восстановить этот субадресс в любом программном обеспечении кошелька с поддержкой BIP44 в любое время через путь(Path) мнемоники и субадресса."
		HoneyLanguage.TraditionalChinese.code -> "子地址遵循BIP44標準創建。你可以隨時通過助記詞與子地址路徑在任意支持BIP44的錢包軟件中恢復這個子地址"
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

	@JvmField
	val switchChainNetAlert: (customContent: String) -> String = {
		when (currentLanguage) {
			HoneyLanguage.English.code -> "This wallet is a $it single-chain wallet. Do you want to switch your wallet to the test network?"
			HoneyLanguage.Chinese.code -> "这是一个仅限 $it 的钱包。您是否要切换钱包至测试网络？"
			HoneyLanguage.Japanese.code -> "これは $it 専用のウォレットです。 財布をテストネットワークに切り替えるのですか？"
			HoneyLanguage.Korean.code -> "이것은 $it 전용 지갑입니다. 지갑을 테스트 네트워크로 전환 하시겠습니까?"
			HoneyLanguage.Russian.code -> "Это кошелек $it. Вы хотите переключить свой кошелек в тестовую сеть?"
			HoneyLanguage.TraditionalChinese.code -> "這是一個僅限$it 的錢包。您是否要切換錢包至測試網絡？"
			else -> ""
		}
	}
}