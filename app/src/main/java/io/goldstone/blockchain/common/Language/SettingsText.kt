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
		HoneyLanguage.English.code -> "DApp Center"
		HoneyLanguage.Chinese.code -> "DApp Center"
		HoneyLanguage.Japanese.code -> "DApp Center"
		HoneyLanguage.Korean.code -> "DApp Center"
		HoneyLanguage.Russian.code -> "DApp Center"
		HoneyLanguage.TraditionalChinese.code -> "DApp Center"
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
		HoneyLanguage.English.code -> "EOS Account Registration"
		HoneyLanguage.Chinese.code -> "EOS 账号注册"
		HoneyLanguage.Japanese.code -> "EOSアカウントの登録"
		HoneyLanguage.Korean.code -> "EOS 계정 등록"
		HoneyLanguage.Russian.code -> "Регистрация учетной записи EOS"
		HoneyLanguage.TraditionalChinese.code -> "EOS 賬號註冊"
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
	val fingerprintSettings = when (currentLanguage) {
		HoneyLanguage.English.code -> "Fingerprint Settings"
		HoneyLanguage.Chinese.code -> "Fingerprint Settings"
		HoneyLanguage.Japanese.code -> "Fingerprint Settings"
		HoneyLanguage.Korean.code -> "Fingerprint Settings"
		HoneyLanguage.Russian.code -> "Fingerprint Settings"
		HoneyLanguage.TraditionalChinese.code -> "Fingerprint Settings"
		else -> ""
	}

	@JvmField
	val fingerprintDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "Literally your fingerprint can replace the cash and plastic that we carry around on a daily basis. With added security and ease, MyTouch gives you the opportunity of linking your fingerprint to your methods of payment in a free and safe process.  "
		HoneyLanguage.Chinese.code -> "Literally your fingerprint can replace the cash and plastic that we carry around on a daily basis. With added security and ease, MyTouch gives you the opportunity of linking your fingerprint to your methods of payment in a free and safe process.  "
		HoneyLanguage.Japanese.code -> "Literally your fingerprint can replace the cash and plastic that we carry around on a daily basis. With added security and ease, MyTouch gives you the opportunity of linking your fingerprint to your methods of payment in a free and safe process.  "
		HoneyLanguage.Korean.code -> "Literally your fingerprint can replace the cash and plastic that we carry around on a daily basis. With added security and ease, MyTouch gives you the opportunity of linking your fingerprint to your methods of payment in a free and safe process.  "
		HoneyLanguage.Russian.code -> "Literally your fingerprint can replace the cash and plastic that we carry around on a daily basis. With added security and ease, MyTouch gives you the opportunity of linking your fingerprint to your methods of payment in a free and safe process.  "
		HoneyLanguage.TraditionalChinese.code -> "Literally your fingerprint can replace the cash and plastic that we carry around on a daily basis. With added security and ease, MyTouch gives you the opportunity of linking your fingerprint to your methods of payment in a free and safe process.  "
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
		HoneyLanguage.English.code -> "GoldStone\ncrypto digtal wallet the safest one for you\nhttps://www.goldstone.io"
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
}