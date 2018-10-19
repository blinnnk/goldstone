package io.goldstone.blockchain.common.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.component.HoneyBaseSwitch
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.GoldStoneFont
import org.jetbrains.anko.sdk25.coroutines.onClick
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.value.*
import org.jetbrains.anko.*

/**
 * @date 11/09/2018 3:45 PM
 * @author wcx
 */
@SuppressLint("ViewConstructor")
class SingleLineSwitch(
	context: Context,
	private val isSwitchIconType: Boolean
): RelativeLayout(context) {
	private val switch by lazy { HoneyBaseSwitch(context) }
	private val arrowIcon by lazy { ImageView(context) }
	private val title by lazy { TextView(context) }
	private var paddingSize = 0
	private val paint = Paint().apply {
		isAntiAlias = true
		color = GrayScale.midGray
		style = Paint.Style.FILL
	}

	init {
		setWillNotDraw(false)
		layoutParams = LayoutParams(
			matchParent,
			80.uiPX()
		)
		title.apply {
			textSize = fontSize(15)
			textColor = GrayScale.black
			typeface = GoldStoneFont.heavy(context)
			gravity = Gravity.CENTER_VERTICAL
			layoutParams = LayoutParams(
				matchParent,
				matchParent
			)
		}.into(this)

		switch.apply {
			isChecked = false
			setAlignParentRight()
		}.into(this)

		arrowIcon.apply {
			layoutParams = LayoutParams(
				24.uiPX(),
				24.uiPX()
			)
			setCenterInVertical()
			setAlignParentRight()
			imageResource = R.drawable.arrow_icon
			setColorFilter(GrayScale.lightGray)
		}.into(this)
	}

	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)
		canvas?.drawLine(
			paddingSize.toFloat(),
			height - BorderSize.default,
			(width - paddingSize).toFloat(),
			height - BorderSize.default,
			paint
		)
	}

	fun setHorizontalPadding(paddingSize: Int = PaddingSize.device) {
		this.paddingSize = paddingSize
		leftPadding = paddingSize
		rightPadding = paddingSize
		invalidate()
	}

	fun setOnclick(callback: (HoneyBaseSwitch) -> Unit) {
		if(isSwitchIconType) {
			arrowIcon.visibility = View.GONE
			switch.onClick {
				callback(switch)
			}
		} else {
			switch.visibility = View.GONE
			onClick {
				callback(switch)
			}
		}
	}

	fun setSwitchStatus(isChecked: Boolean) {
		switch.isChecked = isChecked
	}

	fun getSwitchCheckedStatus(): Boolean {
		return switch.isChecked
	}

	fun setTitle(title: String) {
		this.title.text = title
	}
}