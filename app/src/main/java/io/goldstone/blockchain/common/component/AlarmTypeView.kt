package io.goldstone.blockchain.common.component

import android.content.Context
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basecell.BaseRadioCell
import io.goldstone.blockchain.common.component.cell.TopBottomLineCell
import io.goldstone.blockchain.common.language.AlarmText
import io.goldstone.blockchain.common.value.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.textColor
import org.jetbrains.anko.wrapContent

/**
 * @date 02/08/2018 21:12 PM
 * @author wcx
 */
class AlarmTypeView(context: Context) : LinearLayout(context) {
	private val alarmTypeTitleCell by lazy { TopBottomLineCell(context) }
	private val alarmTypeTitleTextView by lazy { TextView(context) }
	private val titleLayout by lazy {
		LinearLayout(context).apply {
			layoutParams = LinearLayout.LayoutParams(
				ScreenSize.widthWithPadding,
				wrapContent
			).apply {
				bottomMargin = 5.uiPX()
			}
		}
	}
	private val repeatingCell = BaseRadioCell(context)
	private val oneTimeCell = BaseRadioCell(context)
	private var alarmType = ArgumentKey.repeatingForAlarm

	init {
		layoutParams = LinearLayout.LayoutParams(
			matchParent,
			wrapContent).apply {
			bottomMargin = 10.uiPX()
		}
		orientation = LinearLayout.VERTICAL

		titleLayout.into(this)

		// 永久闹铃
		repeatingCell.apply {
			setTitle(AlarmText.alarmRepeatingType)
			if (alarmType == ArgumentKey.repeatingForAlarm) {
				setSwitchStatusBy(true)
			}
			onClick {
				alarmType = ArgumentKey.repeatingForAlarm
				repeatingCell.setSwitchStatusBy(true)
				oneTimeCell.setSwitchStatusBy(false)
			}
		}
		repeatingCell.into(this)

		// 一次闹铃
		oneTimeCell.apply {
			setTitle(AlarmText.alarmOnlyOneTimeType)
			if (alarmType == ArgumentKey.oneTimeForAlram) {
				setSwitchStatusBy(true)
			}
			onClick {
				alarmType =  ArgumentKey.oneTimeForAlram
				oneTimeCell.setSwitchStatusBy(true)
				repeatingCell.setSwitchStatusBy(false)
			}
		}
		oneTimeCell.into(this)
	}


	fun getAlarmType(): Int {
		return alarmType
	}

	fun setAlarmType(alarmType: Int) {
		this.alarmType = alarmType
	}

	fun getRepeatingCell(): BaseRadioCell {
		return repeatingCell
	}

	fun getOneTimeCell(): BaseRadioCell {
		return oneTimeCell
	}

	fun showAlarmTypeTitleCell(title: String) {
		alarmTypeTitleCell.apply {
			setTitle(title, fontSize(14), Spectrum.blue)
		}.into(titleLayout)
	}

	fun showAlarmTypeTitleTextView() {
		alarmTypeTitleTextView.apply {
			text = AlarmText.alarmTypeTitle
			textSize = fontSize(14)
			textColor = GrayScale.midGray
		}.into(titleLayout)
	}

	fun setRepeatingCellTitle(title: String) {
		repeatingCell.setTitle(title)
	}

	fun setOneTimeCellTitle(title: String) {
		oneTimeCell.setTitle(title)
	}

	fun setRepeatingCellSwitchStatusBy(flag: Boolean) {
		repeatingCell.setSwitchStatusBy(flag)
	}

	fun setOneTimeCellSwitchStatusBy(flag: Boolean) {
		oneTimeCell.setSwitchStatusBy(flag)
	}
}
