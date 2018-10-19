package io.goldstone.blockchain.common.language

/**
 * @date 2018/8/8 2:11 AM
 * @author KaySaith
 */
object DialogText {
	@JvmField
	val backUpMnemonicSucceed = when(currentLanguage) {
		HoneyLanguage.English.code -> "You have backed up your Mnemonics backed up!"
		HoneyLanguage.Chinese.code -> "助记词备份成功！"
		HoneyLanguage.Japanese.code -> "ニーモニックのバックアップに成功しました。"
		HoneyLanguage.Korean.code -> "니모닉 백업이 성공적이었습니다!"
		HoneyLanguage.Russian.code -> "Резервная копия мнемонической записи завершена!"
		HoneyLanguage.TraditionalChinese.code -> "助憶口令備份成功！"
		else -> ""
	}
	@JvmField
	val backUpMnemonic = when(currentLanguage) {
		HoneyLanguage.English.code -> "Back Up Mnemonic"
		HoneyLanguage.Chinese.code -> "备份助记词"
		HoneyLanguage.Japanese.code -> "ニーモニックのバックアップ"
		HoneyLanguage.Korean.code -> "니모닉 백업"
		HoneyLanguage.Russian.code -> "Мнемонические записи резервной копии"
		HoneyLanguage.TraditionalChinese.code -> "備份助記詞"
		else -> ""
	}
	@JvmField
	val backUpMnemonicDescription = when(currentLanguage) {
		HoneyLanguage.English.code -> "You have not backed up your mnemonic yet. It is extremely important that take care of your mnemonic. If you lose it, you will lose your digital assets."
		HoneyLanguage.Chinese.code -> "你还没有备份您的钱包。GoldStone不会为您保存任何形式的私钥/助记词/keystore，一旦您忘记就无法找回。请您一定确保钱包妥善备份后再用这个钱包接收转账。"
		HoneyLanguage.Japanese.code -> "まだお客様のウォレットがバックアップされていません。GoldStoneどのような形式であれプライペートキー/ニーモニック/Keystoreは保存されません。忘れてしまった場合は探し出すことは出来ません。必ずウォレットが適切にバックアップされていることを確認してから、このウォレットを使用して振込を受け付けて下さい。"
		HoneyLanguage.Korean.code -> "귀하의 지갑을 백업하지 않았습니다. GoldStone는 귀하의 임의의 형식의 개인키/니모닉/keystore를 저장하지 않으며, 일단 귀하께서 분실할 경우 찾을수 없습니다. 지갑을 정확히 백업한후 이 지갑으로 이체금액을 수령하십시오. "
		HoneyLanguage.Russian.code -> "Вы еще не сделали резервную копию Вашего кошелька. Никакой формат закрытого ключа / мнемонической записи / keystore не будет сохранен, если Вы его забудете, Вы уже не сможете его восстановить. Пожалуйста, убедитесь, что кошелек имеет должную резервную копию, а затем получите перевод с этого кошелька."
		HoneyLanguage.TraditionalChinese.code -> "你還沒有備份您的錢包。GoldStone不會為您保存任何形式的私鑰/助記詞/密鑰庫，一旦您忘記就無法找回。“請您一定確保錢包妥善備份後再用這個錢包接收轉賬。"
		else -> ""
	}
	@JvmField
	val networkTitle = when(currentLanguage) {
		HoneyLanguage.English.code -> "Network unavailable"
		HoneyLanguage.Chinese.code -> "未检测到网络"
		HoneyLanguage.Japanese.code -> "ネットワークが検出できません"
		HoneyLanguage.Korean.code -> "네트우크 무"
		HoneyLanguage.Russian.code -> "Интернет недоступен"
		HoneyLanguage.TraditionalChinese.code -> "未檢測到網絡"
		else -> ""
	}
	@JvmField
	val networkDescription = when(currentLanguage) {
		HoneyLanguage.English.code -> "The current state of the network is not good. Please check. You can try turning on and off airplane mode to try to recover."
		HoneyLanguage.Chinese.code -> "现在的网络状态不好，请检查。您可以尝试开启再关闭飞行模式来尝试恢复。"
		HoneyLanguage.Japanese.code -> "現在のネットワーク状態がよくありませんので、チェックするようにして下さい。飛行モードをオンにした後オフにして回復を試みて下さい。"
		HoneyLanguage.Korean.code -> "네트워크의 현재 상태가 좋지 않습니다. 확인하십시오. 비행기 모드를 켜고 끄고 복구를 시도 할 수 있습니다.An extensible dialog system I designed for the ItsON SaaS telecom solution for mobile Android devices at the OS level. Having dialogs easily identifiable as the brand of the phones service provider allows the context to be clearly understood"
		HoneyLanguage.Russian.code -> "Текущее состояние Интернета плохое, пожалуйста, проверьте. Вы можете попробовать включить и выключить режим в самолете, чтобы попытаться восстановить."
		HoneyLanguage.TraditionalChinese.code -> "現在的網絡狀態不好，請檢查。您可以嘗試開啟再關閉飛行模式來嘗試恢復。"
		else -> ""
	}
	@JvmField
	val goToBackUp = when(currentLanguage) {
		HoneyLanguage.English.code -> "BACK UP"
		HoneyLanguage.Chinese.code -> "立即备份"
		HoneyLanguage.Japanese.code -> "すぐにバックアップする"
		HoneyLanguage.Korean.code -> "백업"
		HoneyLanguage.Russian.code -> "Немедленное резервное копирование"
		HoneyLanguage.TraditionalChinese.code -> "立即備份"
		else -> ""
	}
	@JvmField
	val title = when(currentLanguage) {
		HoneyLanguage.English.code -> "title"
		HoneyLanguage.Chinese.code -> "title"
		HoneyLanguage.Japanese.code -> "title"
		HoneyLanguage.Korean.code -> "title"
		HoneyLanguage.Russian.code -> "title"
		HoneyLanguage.TraditionalChinese.code -> "title"
		else -> ""
	}
	@JvmField
	val subtitle = when(currentLanguage) {
		HoneyLanguage.English.code -> "subtitle"
		HoneyLanguage.Chinese.code -> "subtitle"
		HoneyLanguage.Japanese.code -> "subtitle"
		HoneyLanguage.Korean.code -> "subtitle"
		HoneyLanguage.Russian.code -> "subtitle"
		HoneyLanguage.TraditionalChinese.code -> "subtitle"
		else -> ""
	}
	@JvmField
	val serverBusyTitle = when(currentLanguage) {
		HoneyLanguage.English.code -> "Network Busy"
		HoneyLanguage.Chinese.code -> "网络繁忙，无法连接"
		HoneyLanguage.Japanese.code -> "ネットワークビジーで接続できません"
		HoneyLanguage.Korean.code -> "네트워크가 사용 중입니다."
		HoneyLanguage.Russian.code -> "Сеть занята"
		HoneyLanguage.TraditionalChinese.code -> "網絡繁忙，無法連接"
		else -> ""
	}
	@JvmField
	val serverBusyDescription = when(currentLanguage) {
		HoneyLanguage.English.code -> "Can't connect with service right now, sorry, please try again later."
		HoneyLanguage.Chinese.code -> "现在无法和服务连接，抱歉呀，请稍后再试。"
		HoneyLanguage.Japanese.code -> "現在サーバーと接続できません。大変申し訳ありませんが、しばらくしてから再度試して下さい。"
		HoneyLanguage.Korean.code -> "지금 서비스에 연결할 수 없습니다. 죄송합니다. 잠시 후 다시 시도하십시오."
		HoneyLanguage.Russian.code -> "Не удается подключиться к службе прямо сейчас, приносим свои извинения, повторите попытку позже."
		HoneyLanguage.TraditionalChinese.code -> "現在無法和服務連接，抱歉呀，請稍後再試。"
		else -> ""
	}
}