package io.goldstone.blockchain.common.component

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.into
import com.blinnnk.extension.measureTextWidth
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.*

/**
 * @date 2018/5/13 10:19 PM
 * @author KaySaith
 */
class TagCell(context: Context) : LinearLayout(context) {
	
	private val number = TextView(context)
	private val title = TextView(context)
	private var tagLayout: LinearLayout
	
	init {
		layoutParams = LinearLayout.LayoutParams(wrapContent, 47.uiPX())
		leftPadding = 5.uiPX()
		rightPadding = 5.uiPX()
		// 在 `API 22` 上的动态 `Margin` 不生效, 临时用套层方法. 发现好的方法随时替换
		tagLayout = linearLayout {
			layoutParams = LinearLayout.LayoutParams(0, 42.uiPX())
			number.apply {
				textColor = Spectrum.white
				textSize = fontSize(15)
				gravity = Gravity.CENTER
				layoutParams = LinearLayout.LayoutParams(30.uiPX(), 30.uiPX())
				typeface = GoldStoneFont.black(context)
				x = 6.uiPX().toFloat()
				addCorner(15.uiPX(), GrayScale.black)
			}.into(this)
			
			title.apply {
				textColor = GrayScale.black
				textSize = fontSize(15)
				gravity = Gravity.CENTER
				typeface = GoldStoneFont.black(context)
				layoutParams = LinearLayout.LayoutParams(0, matchParent)
			}.into(this)
			
			addCorner(42, GrayScale.whiteGray)
		}
	}
	
	fun setNumberAndText(number: Int, title: String) {
		this.number.text = number.toString()
		this.title.text = title
		
		tagLayout.layoutParams.width =
			42.uiPX() + this.title.text.measureTextWidth(15.uiPX().toFloat()).toInt() + 20.uiPX()
		this.title.layoutParams.width = tagLayout.layoutParams.width - 36.uiPX()
		requestLayout()
	}
}