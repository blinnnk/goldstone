package io.goldstone.blockchain.common.component

import android.content.Context
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Switch
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.component.HoneyBaseSwitch
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView

open class AlarmCell(context: Context) : RelativeLayout(context) {

	private val switch by lazy {
		HoneyBaseSwitch(context).apply {
			isChecked = false
			setThemColor(Spectrum.blue, Spectrum.blue)
			layoutParams = RelativeLayout.LayoutParams(50.uiPX(), 50.uiPX())
		}
	}
	private val arrowIcon by lazy {
		ImageView(context).apply {
			setColorFilter(GrayScale.midGray)
			layoutParams = RelativeLayout.LayoutParams(20.uiPX(), 30.uiPX())
			scaleType = ImageView.ScaleType.CENTER_CROP
			imageResource = R.drawable.arrow_icon
		}
	}

	private val timeTitle = textView {
		textColor = GrayScale.gray
		textSize = fontSize(12)
		typeface = GoldStoneFont.medium(context)
		layoutParams = RelativeLayout.LayoutParams(matchParent, 60.uiPX())
		text = "Price Threshold"
	}

	private val alarmInfoTitle = TwoLineTitles(context).apply {
		title.textSize = fontSize(18)
		title.typeface = GoldStoneFont.black(context)
		title.textColor = GrayScale.black
		subtitle.textSize = fontSize(12)
		subtitle.typeface = GoldStoneFont.heavy(context)
		subtitle.textColor = GrayScale.gray
		layoutParams = RelativeLayout.LayoutParams(matchParent, 50.uiPX())
		title.text = "1 BTC > 12000 USDT"
		subtitle.text = "Huobi pro BTC/USDT"
	}

	init {
		setPadding(20.uiPX(), 15.uiPX(), 20.uiPX(), 20.uiPX())
		addTouchRippleAnimation(
			GrayScale.whiteGray,
			Spectrum.green,
			RippleMode.Square,
			CornerSize.cell.toFloat()
		)
		layoutParams = RelativeLayout.LayoutParams(ScreenSize.widthWithPadding, 112.uiPX())
		alarmInfoTitle.y += 16.uiPX()
		alarmInfoTitle.setCenterInVertical()
		alarmInfoTitle.into(this)
	}

	fun showArrow() {
		arrowIcon.setAlignParentRight()
		arrowIcon.setCenterInVertical()
		addView(arrowIcon)
	}

	fun showSwitchBVutton() {
		switch.setAlignParentRight()
		switch.setCenterInVertical()
		addView(switch)
	}

	fun setTimeTitle(title: String) {
		timeTitle.text = title
	}

	fun setAlarmInfoTitle(title: String) {
		alarmInfoTitle.setTitle(title)
	}

	fun setAlarmInfoSubtitle(subtitle: String) {
		alarmInfoTitle.setSubtitle(subtitle)
	}

	fun getSwitch(): Switch {
		return switch
	}
}
