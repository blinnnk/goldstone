package io.goldstone.blockchain.common.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.component.HoneyBaseSwitch
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentBottom
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.textColor
import org.jetbrains.anko.wrapContent

/**
 * @date 11/09/2018 3:45 PM
 * @author wcx
 */
@SuppressLint("ViewConstructor")
class SingleLineSwitch(
	context: Context,
	private val isSwitchIcon: Boolean
) : RelativeLayout(context) {
	private val switch by lazy { HoneyBaseSwitch(context) }
	private val imageView by lazy { ImageView(context) }
	private val content by lazy { TextView(context) }

	init {
		layoutParams = LayoutParams(
			ScreenSize.widthWithPadding,
			80.uiPX()
		)
		content.apply {
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

		imageView.apply {
			layoutParams = LayoutParams(
				24.uiPX(),
				24.uiPX()
			)
			setCenterInVertical()
			setAlignParentRight()
			setImageDrawable(ContextCompat.getDrawable(
				context,
				R.drawable.arrow_icon
			))
			setColorFilter(GrayScale.lightGray)
		}.into(this)

		// 分割线
		View(context).apply {
			layoutParams = LayoutParams(
				matchParent,
				BorderSize.default.toInt()
			)
			setAlignParentBottom()
			backgroundColor = GrayScale.lightGray
		}.into(this)
	}

	fun setOnclick(callback: (HoneyBaseSwitch) -> Unit) {
		if (isSwitchIcon) {
			imageView.visibility = View.GONE
			switch.onClick {
				callback(switch)
			}
		} else {
			this.apply {
				switch.visibility = View.GONE
				onClick {
					callback(switch)
				}
			}
		}
	}

	fun setSwitch(isChecked: Boolean) {
		switch.isChecked = isChecked
	}

	fun getSwitchChecked(): Boolean {
		return switch.isChecked
	}

	fun setContent(content: String) {
		this.content.text = content
	}
}