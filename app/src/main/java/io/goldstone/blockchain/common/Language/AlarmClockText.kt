package io.goldstone.blockchain.common.Language

import io.goldstone.blockchain.common.language.HoneyLanguage
import io.goldstone.blockchain.common.language.currentLanguage

/**
 * @date 10/08/2018 14:19 PM
 * @author wcx
 */

object AlarmClockText {
  @JvmField
  val priceAlarmClockList = when (currentLanguage) {
    HoneyLanguage.English.code -> "Price Alarm"
    HoneyLanguage.Chinese.code -> "价格闹铃"
    HoneyLanguage.Japanese.code -> "価格アラーム"
    HoneyLanguage.Korean.code -> "가격 경보"
    HoneyLanguage.Russian.code -> "Ценовой сигнал"
    HoneyLanguage.TraditionalChinese.code -> "價格鬧鈴"
    else -> ""
  }

  @JvmField
  val priceAlarmClockEditor = when (currentLanguage) {
    HoneyLanguage.English.code -> "Alarm Editor"
    HoneyLanguage.Chinese.code -> "报警编辑器"
    HoneyLanguage.Japanese.code -> "アラームエディタ"
    HoneyLanguage.Korean.code -> "알람 편집기"
    HoneyLanguage.Russian.code -> "Редактор сигналов тревоги"
    HoneyLanguage.TraditionalChinese.code -> "報警編輯器"
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
}