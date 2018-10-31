package io.goldstone.blockchain.common.component

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.CustomTargetTextStyle
import com.blinnnk.extension.into
import com.blinnnk.extension.measureTextWidth
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.language.CreateWalletText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.isDefaultStyle
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick


/**
 * @date 22/03/2018 3:52 PM
 * @author KaySaith
 */

@SuppressLint("SetTextI18n")
class AgreementView(context: Context) : RelativeLayout(context) {

	private val viewHeight = 20.uiPX()
	lateinit var radioButton: RadioButton
	val textView = TextView(context)
	private var isChecked = false

	init {
		setWillNotDraw(false)
		layoutParams = LinearLayout.LayoutParams(wrapContent, viewHeight).apply {
			topMargin = 20.uiPX()
		}
		val terms = CreateWalletText.agreementName
		textView.apply {
			gravity = Gravity.END or Gravity.CENTER_VERTICAL
			layoutParams = LinearLayout.LayoutParams(wrapContent, viewHeight)
			text = CustomTargetTextStyle(
				terms,
				"${CreateWalletText.agreementPreString} $terms ${CreateWalletText.agreementPostString}",
				Spectrum.green,
				11.uiPX()
			)
			textSize = fontSize(11)
			textColor = GrayScale.midGray
			typeface = GoldStoneFont.medium(context)
		}.into(this)
		textView.layoutParams.width = textView.text.measureTextWidth(11.uiPX().toFloat()).toInt() + 15.uiPX()
		linearLayout {
			// 防止自定义的 radio 会漏出后面的条款点击事件, 固在此增加了一个嵌套
			// Layout 防止点击出错
			isClickable = true
			layoutParams = LinearLayout.LayoutParams(80.uiPX(), matchParent)
			radioButton = radioButton {
				isDefaultStyle()
				layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
				scaleX = 0.7f
				scaleY = 0.7f
				x -= 15.uiPX()
				onClick {
					setRadioStatus()
					preventDuplicateClicks()
				}
			}
		}
	}

	fun setRadioStatus() {
		isChecked = !isChecked
		radioButton.isChecked = isChecked
	}
}