package io.goldstone.blockchain.common.component

import android.content.Context
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basecell.BaseRadioCell
import io.goldstone.blockchain.common.component.cell.TopBottomLineCell
import io.goldstone.blockchain.common.language.AlarmClockText
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.wrapContent

/**
 * @date 02/08/2018 21:12 PM
 * @author wcx
 */
class AlarmTypeView(context: Context) : LinearLayout(context) {
  private val alarmTypeTitleCell = TopBottomLineCell(context)
  private val repeatingCell = BaseRadioCell(context)
  private val oneTimeCell = BaseRadioCell(context)
  private var alarmType = 0

  init {
    layoutParams = LinearLayout.LayoutParams(
      matchParent,
      wrapContent).apply {
      setMargins(
        0,
        20.uiPX(),
        0,
        20.uiPX())
    }
    orientation = LinearLayout.VERTICAL

    alarmTypeTitleCell.apply {
      layoutParams = LinearLayout.LayoutParams(
        matchParent,
        wrapContent).apply {
        setMargins(
          20.uiPX(),
          0,
          20.uiPX(),
          20.uiPX()
        )
      }
      setTitle(AlarmClockText.alarmTypeTitle)
    }.into(this)

    // 永久闹铃
    repeatingCell.apply {
      setTitle(AlarmClockText.alarmRepeatingType)
      if (alarmType == 0) {
        setSwitchStatusBy(true)
      }
      onClick {
        alarmType = 0
        repeatingCell.setSwitchStatusBy(true)
        oneTimeCell.setSwitchStatusBy(false)
      }
    }
    repeatingCell.into(this)

    // 一次闹铃
    oneTimeCell.apply {
      setTitle(AlarmClockText.alarmOnlyOneTimeType)
      if (alarmType == 1) {
        setSwitchStatusBy(true)
      }
      onClick {
        alarmType = 1
        oneTimeCell.setSwitchStatusBy(true)
        repeatingCell.setSwitchStatusBy(false)
      }
    }
    oneTimeCell.into(this)
  }


  fun getAlarmType(): Int {
    return alarmType
  }

  fun setAlarmType(alarmType: Int?) {
    this.alarmType = alarmType!!
  }

  fun getRepeatingCell(): BaseRadioCell {
    return repeatingCell
  }

  fun getOneTimeCell(): BaseRadioCell {
    return oneTimeCell
  }
}