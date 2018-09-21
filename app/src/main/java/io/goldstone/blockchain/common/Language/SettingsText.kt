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
	val eosAccountRegister = when (currentLanguage) {
		HoneyLanguage.English.code -> "EOS Account Register"
		HoneyLanguage.Chinese.code -> "EOS Account Register"
		HoneyLanguage.Japanese.code -> "EOS Account Register"
		HoneyLanguage.Korean.code -> "EOS Account Register"
		HoneyLanguage.Russian.code -> "EOS Account Register"
		HoneyLanguage.TraditionalChinese.code -> "EOS Account Register"
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