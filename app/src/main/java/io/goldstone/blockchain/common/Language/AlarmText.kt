package io.goldstone.blockchain.common.language

import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmlist.model.PriceAlarmTable

/**
 * @date 10/08/2018 14:19 PM
 * @author wcx
 */
object AlarmText {
	@JvmField
	val priceAlarmList = when (currentLanguage) {
		HoneyLanguage.English.code -> "Price Alarm"
		HoneyLanguage.Chinese.code -> "价格闹铃"
		HoneyLanguage.Japanese.code -> "価格アラーム"
		HoneyLanguage.Korean.code -> "가격 경보"
		HoneyLanguage.Russian.code -> "Ценовой сигнал"
		HoneyLanguage.TraditionalChinese.code -> "價格鬧鈴"
		else -> ""
	}

	@JvmField
	val createNewAlarm = when (currentLanguage) {
		HoneyLanguage.English.code -> "Create New Alarm"
		HoneyLanguage.Chinese.code -> "创建新警报"
		HoneyLanguage.Japanese.code -> "新しいアラームを作成する"
		HoneyLanguage.Korean.code -> "새 알람 만들기"
		HoneyLanguage.Russian.code -> "Создать новый сигнал"
		HoneyLanguage.TraditionalChinese.code -> "創建新警報"
		else -> ""
	}

	@JvmField
	val targetPrice = when (currentLanguage) {
		HoneyLanguage.English.code -> "Target Price"
		HoneyLanguage.Chinese.code -> "目标价"
		HoneyLanguage.Japanese.code -> "目標価格"
		HoneyLanguage.Korean.code -> "목표 주가"
		HoneyLanguage.Russian.code -> "Целевая цена"
		HoneyLanguage.TraditionalChinese.code -> "目標價"
		else -> ""
	}

	@JvmField
	val alarmTypeTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Alarm Type"
		HoneyLanguage.Chinese.code -> "报警类型"
		HoneyLanguage.Japanese.code -> "アラームタイプ"
		HoneyLanguage.Korean.code -> "알람 유형"
		HoneyLanguage.Russian.code -> "Тип сигнала тревоги"
		HoneyLanguage.TraditionalChinese.code -> "價格類型"
		else -> ""
	}

	@JvmField
	val alarmRepeatingType = when (currentLanguage) {
		HoneyLanguage.English.code -> "Alarm Repeating"
		HoneyLanguage.Chinese.code -> "报警重复"
		HoneyLanguage.Japanese.code -> "アラームの繰り返し"
		HoneyLanguage.Korean.code -> "반복되는 알람"
		HoneyLanguage.Russian.code -> "Повторение тревоги"
		HoneyLanguage.TraditionalChinese.code -> "報警重複"
		else -> ""
	}

	@JvmField
	val alarmOnlyOneTimeType = when (currentLanguage) {
		HoneyLanguage.English.code -> "Only One Time"
		HoneyLanguage.Chinese.code -> "只有一次"
		HoneyLanguage.Japanese.code -> "1回のみ"
		HoneyLanguage.Korean.code -> "한번만"
		HoneyLanguage.Russian.code -> "Только раз"
		HoneyLanguage.TraditionalChinese.code -> "只有一次"
		else -> ""
	}

	@JvmField
	val priceTypeTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Price Type"
		HoneyLanguage.Chinese.code -> "价格类型"
		HoneyLanguage.Japanese.code -> "価格タイプ"
		HoneyLanguage.Korean.code -> "가격 유형"
		HoneyLanguage.Russian.code -> "Тип цены"
		HoneyLanguage.TraditionalChinese.code -> "價格類型"
		else -> ""
	}

	@JvmField
	val modifyAlarm = when (currentLanguage) {
		HoneyLanguage.English.code -> "Modify Alarm"
		HoneyLanguage.Chinese.code -> "修改警报"
		HoneyLanguage.Japanese.code -> "アラームの変更"
		HoneyLanguage.Korean.code -> "알람 수정"
		HoneyLanguage.Russian.code -> "Изменить сигнал тревоги"
		HoneyLanguage.TraditionalChinese.code -> "修改警報"
		else -> ""
	}

	@JvmField
	val alarmPriceValue = when (currentLanguage) {
		HoneyLanguage.English.code -> "Alarm Price Value"
		HoneyLanguage.Chinese.code -> "报警价格"
		HoneyLanguage.Japanese.code -> "アラーム価格値"
		HoneyLanguage.Korean.code -> "알람 가격 값"
		HoneyLanguage.Russian.code -> "Ценовая стоимость сигнала тревоги"
		HoneyLanguage.TraditionalChinese.code -> "報警價格"
		else -> ""
	}

	@JvmField
	val priceThreshold = when (currentLanguage) {
		HoneyLanguage.English.code -> "Price Threshold"
		HoneyLanguage.Chinese.code -> "价格门槛"
		HoneyLanguage.Japanese.code -> "価格の閾値"
		HoneyLanguage.Korean.code -> "가격 한도"
		HoneyLanguage.Russian.code -> "Порог цены"
		HoneyLanguage.TraditionalChinese.code -> "價格門檻"
		else -> ""
	}

	@JvmField
	val modifyDescription = when (currentLanguage) {
		HoneyLanguage.English.code -> "when the price corresponding to the currency reaches the threshold you set," +
			"we will send you an alarm to inform you."
		HoneyLanguage.Chinese.code -> "当货币对应的价格达到您设定的门槛时，我们会向您发送警报通知您。"
		HoneyLanguage.Japanese.code -> "通貨に対応する価格があなたが設定したしきい値に達すると、私たちはあなたに通知するために警報を送信します。"
		HoneyLanguage.Korean.code -> "통화에 해당하는 가격이 귀하가 설정 한 기준 액에 도달하면, 우리는 귀하에게 통보하기 위해 경보를 보내드립니다."
		HoneyLanguage.Russian.code -> "когда цена, соответствующая валюте, достигает установленного вами порога, мы отправим вам сигнал тревоги, чтобы сообщить вам."
		HoneyLanguage.TraditionalChinese.code -> "當貨幣對應的價格達到您設定的門檻時，我們會向您發送警報通知您。"
		else -> ""
	}

	@JvmField
	val noPriceAlarmTitle = when (currentLanguage) {
		HoneyLanguage.English.code -> "Price Threshold"
		HoneyLanguage.Chinese.code -> "还没有任何价格提醒"
		HoneyLanguage.Japanese.code -> "価格の閾値"
		HoneyLanguage.Korean.code -> "가격 한도"
		HoneyLanguage.Russian.code -> "Порог цены"
		HoneyLanguage.TraditionalChinese.code -> "價格門檻"
		else -> ""
	}

	@JvmField
	val noPriceAlarmContent = when (currentLanguage) {
		HoneyLanguage.English.code -> "Price Threshold"
		HoneyLanguage.Chinese.code -> "点击加号创建一个吧，我们会在货币达到您的指定价格时通知你。"
		HoneyLanguage.Japanese.code -> "価格の閾値"
		HoneyLanguage.Korean.code -> "가격 한도"
		HoneyLanguage.Russian.code -> "Порог цены"
		HoneyLanguage.TraditionalChinese.code -> "價格門檻"
		else -> ""
	}

	@JvmField
	val gotIt = when (currentLanguage) {
		HoneyLanguage.English.code -> "Price Threshold"
		HoneyLanguage.Chinese.code -> "知道了"
		HoneyLanguage.Japanese.code -> "価格の閾値"
		HoneyLanguage.Korean.code -> "가격 한도"
		HoneyLanguage.Russian.code -> "Порог цены"
		HoneyLanguage.TraditionalChinese.code -> "價格門檻"
		else -> ""
	}

	@JvmField
	val viewAlarm = when (currentLanguage) {
		HoneyLanguage.English.code -> "Price Threshold"
		HoneyLanguage.Chinese.code -> "查看闹铃"
		HoneyLanguage.Japanese.code -> "価格の閾値"
		HoneyLanguage.Korean.code -> "가격 한도"
		HoneyLanguage.Russian.code -> "Порог цены"
		HoneyLanguage.TraditionalChinese.code -> "價格門檻"
		else -> ""
	}

	@JvmField
	val achieve = when (currentLanguage) {
		HoneyLanguage.English.code -> "Price Threshold"
		HoneyLanguage.Chinese.code -> "达到"
		HoneyLanguage.Japanese.code -> "価格の閾値"
		HoneyLanguage.Korean.code -> "가격 한도"
		HoneyLanguage.Russian.code -> "Порог цены"
		HoneyLanguage.TraditionalChinese.code -> "價格門檻"
		else -> ""
	}

	@JvmField
	val priceWarning = when (currentLanguage) {
		HoneyLanguage.English.code -> "Price Threshold"
		HoneyLanguage.Chinese.code -> "价格预警"
		HoneyLanguage.Japanese.code -> "価格の閾値"
		HoneyLanguage.Korean.code -> "가격 한도"
		HoneyLanguage.Russian.code -> "Порог цены"
		HoneyLanguage.TraditionalChinese.code -> "價格門檻"
		else -> ""
	}

	@JvmField
	val alarmEditor = when (currentLanguage) {
		HoneyLanguage.English.code -> "Alarm Editor"
		HoneyLanguage.Chinese.code -> "报警编辑器"
		HoneyLanguage.Japanese.code -> "アラームエディタ"
		HoneyLanguage.Korean.code -> "알람 편집기"
		HoneyLanguage.Russian.code -> "Редактор сигналов тревоги"
		HoneyLanguage.TraditionalChinese.code -> "報警編輯器"
		else -> ""
	}

	@JvmField
	val confirmDelete = when (currentLanguage) {
		HoneyLanguage.English.code -> "Confirm Delete?"
		HoneyLanguage.Chinese.code -> "确定要删除吗？"
		HoneyLanguage.Japanese.code -> "アラームエディタ"
		HoneyLanguage.Korean.code -> "알람 편집기"
		HoneyLanguage.Russian.code -> "Редактор сигналов тревоги"
		HoneyLanguage.TraditionalChinese.code -> "報警編輯器"
		else -> ""
	}

	@JvmField
	val confirmDeleteContent = when (currentLanguage) {
		HoneyLanguage.English.code -> "Confirm Delete?"
		HoneyLanguage.Chinese.code -> "删除后对应的闹铃也会一并删除"
		HoneyLanguage.Japanese.code -> "アラームエディタ"
		HoneyLanguage.Korean.code -> "알람 편집기"
		HoneyLanguage.Russian.code -> "Редактор сигналов тревоги"
		HoneyLanguage.TraditionalChinese.code -> "報警編輯器"
		else -> ""
	}

	@JvmField
	val confirmDeleteEditorContent = when (currentLanguage) {
		HoneyLanguage.English.code -> "Confirm Delete?"
		HoneyLanguage.Chinese.code -> "删除后对应的闹铃也会一并删除"
		HoneyLanguage.Japanese.code -> "アラームエディタ"
		HoneyLanguage.Korean.code -> "알람 편집기"
		HoneyLanguage.Russian.code -> "Редактор сигналов тревоги"
		HoneyLanguage.TraditionalChinese.code -> "報警編輯器"
		else -> ""
	}

	val priceAlarmContent: (priceAlarmTable: PriceAlarmTable) -> String = { priceAlarmTable ->
		when (currentLanguage) {
			HoneyLanguage.English.code -> if (priceAlarmTable.priceType == ArgumentKey.greaterThanForPriceType) {
				""
			} else {
				""
			}
			HoneyLanguage.Russian.code -> if (priceAlarmTable.priceType == ArgumentKey.greaterThanForPriceType) {
				""
			} else {
				""
			}
			HoneyLanguage.Chinese.code -> if (priceAlarmTable.priceType == ArgumentKey.greaterThanForPriceType) {
				"${priceAlarmTable.marketName}市场的${priceAlarmTable.pairDisplay}达到${priceAlarmTable.marketPrice},高于于你设置的预警值${priceAlarmTable.price}。\n\n" +
					"你可以在市场模块已经添加的卡片详情中管理您设置好的闹铃。"
			} else {
				"${priceAlarmTable.marketName}市场的${priceAlarmTable.pairDisplay}达到${priceAlarmTable.marketPrice},低于你设置的预警值${priceAlarmTable.price}。\n\n" +
					"你可以在市场模块已经添加的卡片详情中管理您设置好的闹铃。"
			}
			HoneyLanguage.Japanese.code -> if (priceAlarmTable.priceType == ArgumentKey.greaterThanForPriceType) {
				""
			} else {
				""
			}
			HoneyLanguage.Korean.code -> if (priceAlarmTable.priceType == ArgumentKey.greaterThanForPriceType) {
				""
			} else {
				""
			}
			HoneyLanguage.TraditionalChinese.code -> if (priceAlarmTable.priceType == ArgumentKey.greaterThanForPriceType) {
				""
			} else {
				""
			}
			else -> ""
		}
	}

	val priceAlarmNotificationContent: (priceAlarmTable: PriceAlarmTable) -> String = { priceAlarmTable ->
		when (currentLanguage) {
			HoneyLanguage.English.code -> if (priceAlarmTable.priceType == ArgumentKey.greaterThanForPriceType) {
				""
			} else {
				""
			}
			HoneyLanguage.Russian.code -> if (priceAlarmTable.priceType == ArgumentKey.greaterThanForPriceType) {
				""
			} else {
				""
			}
			HoneyLanguage.Chinese.code -> if (priceAlarmTable.priceType == ArgumentKey.greaterThanForPriceType) {
				"${priceAlarmTable.marketName}市场的${priceAlarmTable.pairDisplay}达到${priceAlarmTable.marketPrice}，已高于您设定的目标${priceAlarmTable.price}"
			} else {
				"${priceAlarmTable.marketName} 市场的${priceAlarmTable.pairDisplay}达到${priceAlarmTable.marketPrice}，已低于您设定的目标${priceAlarmTable.price}"
			}
			HoneyLanguage.Japanese.code -> if (priceAlarmTable.priceType == ArgumentKey.greaterThanForPriceType) {
				""
			} else {
				""
			}
			HoneyLanguage.Korean.code -> if (priceAlarmTable.priceType == ArgumentKey.greaterThanForPriceType) {
				""
			} else {
				""
			}
			HoneyLanguage.TraditionalChinese.code -> if (priceAlarmTable.priceType == ArgumentKey.greaterThanForPriceType) {
				""
			} else {
				""
			}
			else -> ""
		}
	}
}