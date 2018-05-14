package io.goldstone.blockchain.common.component

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.into
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.measureTextWidth
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import org.jetbrains.anko.margin
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor

/**
 * @date 2018/5/13 10:19 PM
 * @author KaySaith
 */

class TagCell(context: Context) : LinearLayout(context) {

	private val number = TextView(context)
	private val title = TextView(context)

	init {

		layoutParams = LinearLayout.LayoutParams(0, 42.uiPX()).apply {
			margin = 5.uiPX()
		}

		number.apply {
			textColor = Spectrum.white
			textSize = 5.uiPX().toFloat()
			gravity = Gravity.CENTER
			layoutParams = LinearLayout.LayoutParams(30.uiPX(), 30.uiPX())
			typeface = GoldStoneFont.black(context)
			x = 6.uiPX().toFloat()
			addCorner(15.uiPX(), GrayScale.black)
		}.into(this)

		title.apply {
			textColor = GrayScale.black
			textSize = 5.uiPX().toFloat()
			gravity = Gravity.CENTER
			typeface = GoldStoneFont.black(context)
			layoutParams = LinearLayout.LayoutParams(0, matchParent)
		}.into(this)

		addCorner(21.uiPX(), GrayScale.whiteGray)
	}

	fun setNumberAndText(
		number: Int,
		title: String
	) {
		this.number.text = number.toString()
		this.title.text = title

		layoutParams.width =
			42.uiPX() + this.title.text.measureTextWidth(15.uiPX().toFloat()).toInt() + 20.uiPX()
		this.title.layoutParams.width = layoutParams.width - 36.uiPX()
		requestLayout()
	}

}