package io.goldstone.blinnnk.common.language

/**
 * @date 2018/8/8 2:12 AM
 * @author KaySaith
 */

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
	val gotIt = when (currentLanguage) {
		HoneyLanguage.English.code -> "Got It"
		HoneyLanguage.Chinese.code -> "知道了"
		HoneyLanguage.Japanese.code -> "閉じる"
		HoneyLanguage.Korean.code -> "나는 알고있다"
		HoneyLanguage.Russian.code -> "Я знаю"
		HoneyLanguage.TraditionalChinese.code -> "知道了"
		else -> ""
	}

	@JvmField
	val waiting = when (currentLanguage) {
		HoneyLanguage.English.code -> "Waiting ..."
		HoneyLanguage.Chinese.code -> "请稍候 ..."
		HoneyLanguage.Japanese.code -> "お待ちください ..."
		HoneyLanguage.Korean.code -> "기다려주십시오 ..."
		HoneyLanguage.Russian.code -> "Пожалуйста, подождите ..."
		HoneyLanguage.TraditionalChinese.code -> "請稍後 ..."
		else -> ""
	}
	@JvmField
	val calculating = when (currentLanguage) {
		HoneyLanguage.English.code -> "Calculating ..."
		HoneyLanguage.Chinese.code -> "正在计算 ..."
		HoneyLanguage.Japanese.code -> "計算中 ..."
		HoneyLanguage.Korean.code -> "계산 중 ..."
		HoneyLanguage.Russian.code -> "Расчет ..."
		HoneyLanguage.TraditionalChinese.code -> "正在計算 ..."
		else -> ""
	}
	@JvmField
	val wrongChainID = when (currentLanguage) {
		HoneyLanguage.English.code -> "Wrong Chain ID"
		HoneyLanguage.Chinese.code -> "错误的链 ID"
		HoneyLanguage.Japanese.code -> "間違ったチェーンID"
		HoneyLanguage.Korean.code -> "잘못된 체인 ID"
		HoneyLanguage.Russian.code -> "Неверный идентификатор цепи"
		HoneyLanguage.TraditionalChinese.code -> "錯誤的鏈 ID"
		else -> ""
	}
	@JvmField
	val checkAll = when (currentLanguage) {
		HoneyLanguage.English.code->"Check All"
		HoneyLanguage.Chinese.code->"查看全部"
		HoneyLanguage.Japanese.code->"すべて"
		HoneyLanguage.Korean.code->"모두보기"
		HoneyLanguage.Russian.code->"Просмотреть все"
		HoneyLanguage.TraditionalChinese.code->"查看全部"
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
		HoneyLanguage.English.code -> "SUCCESS"
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
		HoneyLanguage.Japanese.code -> "キャンセル"
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
	val switchBCHAddressFormat = when (currentLanguage) {
		HoneyLanguage.English.code -> "Switch BCH address format"
		HoneyLanguage.Chinese.code -> "切换 BCH 地址格式"
		HoneyLanguage.Japanese.code -> "BCHアドレス形式の切り替え"
		HoneyLanguage.Korean.code -> "BCH 주소 형식 전환"
		HoneyLanguage.Russian.code -> "Переключить формат адреса BCH"
		HoneyLanguage.TraditionalChinese.code -> "切換 BCH 地址格式"
		else -> ""
	}
	@JvmField
	val shareQRImage = when (currentLanguage) {
		HoneyLanguage.English.code -> "Share QR Image"
		HoneyLanguage.Chinese.code -> "分享二维码"
		HoneyLanguage.Japanese.code -> "QR コードをシェアする"
		HoneyLanguage.Korean.code -> "QR 코드 공유"
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
		HoneyLanguage.English.code -> "DELETE"
		HoneyLanguage.Chinese.code -> "删除"
		HoneyLanguage.Japanese.code -> "削除する"
		HoneyLanguage.Korean.code -> "삭제"
		HoneyLanguage.Russian.code -> "УДАЛЯТЬ"
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
		HoneyLanguage.Russian.code -> "НЕ УДАЛОСЬ"
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
		HoneyLanguage.Russian.code -> "ВСЕ"
		HoneyLanguage.TraditionalChinese.code -> "所有"
		else -> ""
	}
	@JvmField
	val upgrade = when (currentLanguage) {
		HoneyLanguage.English.code -> "UPGRADE"
		HoneyLanguage.Chinese.code -> "升级版本"
		HoneyLanguage.Japanese.code -> "バージョンアップをする"
		HoneyLanguage.Korean.code -> "업그레이드"
		HoneyLanguage.Russian.code -> "ОБНОВИТЬ"
		HoneyLanguage.TraditionalChinese.code -> "升級版本"
		else -> ""
	}

	@JvmField
	val amount100Million = when (currentLanguage) {
		HoneyLanguage.English.code->""
		HoneyLanguage.Chinese.code->"亿"
		HoneyLanguage.Japanese.code->"億"
		HoneyLanguage.Korean.code->" 억"
		HoneyLanguage.Russian.code->""
		HoneyLanguage.TraditionalChinese.code->"億"
		else -> ""
	}
	@JvmField
	val amount10Thousand = when (currentLanguage) {
		HoneyLanguage.English.code->""
		HoneyLanguage.Chinese.code->"万"
		HoneyLanguage.Japanese.code->"万"
		HoneyLanguage.Korean.code->" 만"
		HoneyLanguage.Russian.code->""
		HoneyLanguage.TraditionalChinese.code->"萬"
		else -> ""
	}
}