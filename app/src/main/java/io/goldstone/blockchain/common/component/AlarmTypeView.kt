package io.goldstone.blockchain.common.component

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.BaseRadioCell
import io.goldstone.blockchain.common.value.AlarmClockText
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.wrapContent

/**
 * @date 02/08/2018 21:12 PM
 * @author wcx
 */
class AlarmTypeView(context: Context) : LinearLayout(context) {
  val alarmTypeTitleTextView = TextView(context)
  val repeatingRadioCell = BaseRadioCell(context)
  val oneTimeRadioCell = BaseRadioCell(context)
  var alarmType = 0

  init {
    val layoutParamsAll = RelativeLayout.LayoutParams(
      matchParent,
      wrapContent)
    layoutParamsAll.setMargins(
      0,
      20.uiPX(),
      0,
      20.uiPX())
    layoutParams = layoutParamsAll
    orientation = LinearLayout.VERTICAL

    alarmTypeTitleTextView.apply {
      text = AlarmClockText.alarmTypeTitle
      val alarmTypeTitleLayoutParams = RelativeLayout.LayoutParams(
        matchParent,
        matchParent)
      alarmTypeTitleLayoutParams.setMargins(
        20.uiPX(),
        0,
        20.uiPX(),
        0)
      this.layoutParams = alarmTypeTitleLayoutParams
    }.into(this)

    View(context).apply {
      val lineLayoutParams = RelativeLayout.LayoutParams(
        matchParent,
        1.uiPX())
      lineLayoutParams.setMargins(
        20.uiPX(),
        20.uiPX(),
        20.uiPX(),
        20.uiPX())
      this.layoutParams = lineLayoutParams
      backgroundColor = Color.BLACK
    }.into(this)

    // 永久闹铃
    repeatingRadioCell.apply {
      setTitle("Alarm Repeating")
      setSwitchStatusBy(true)
      onClick {
        alarmType = 1
        repeatingRadioCell.setSwitchStatusBy(true)
        oneTimeRadioCell.setSwitchStatusBy(false)
      }
    }
    repeatingRadioCell.into(this)

    // 一次闹铃
    oneTimeRadioCell.apply {
      setTitle("Only One Time")
      onClick {
        alarmType = 0
        oneTimeRadioCell.setSwitchStatusBy(true)
        repeatingRadioCell.setSwitchStatusBy(false)
      }
    }
    oneTimeRadioCell.into(this)
  }
}