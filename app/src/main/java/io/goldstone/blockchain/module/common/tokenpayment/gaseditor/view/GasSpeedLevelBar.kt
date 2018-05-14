package io.goldstone.blockchain.module.common.tokenpayment.gaseditor.view

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.animation.updateWidthAnimation
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.component.GradientType
import io.goldstone.blockchain.common.component.GradientView
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView

/**
 * @date 2018/5/8 3:40 PM
 * @author KaySaith
 */

class GasSpeedLevelBar(context: Context) : RelativeLayout(context) {

	private var processLayout: LinearLayout

	init {

		textView {
			text = CommonText.slow
			textSize = fontSize(12)
			textColor = GrayScale.black
			typeface = GoldStoneFont.black(context)
			layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)
			gravity = Gravity.START
		}

		textView {
			text = CommonText.fast
			textSize = fontSize(12)
			textColor = GrayScale.black
			typeface = GoldStoneFont.black(context)
			layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)
			gravity = Gravity.END
		}

		layoutParams = RelativeLayout.LayoutParams(ScreenSize.widthWithPadding, 50.uiPX())

		linearLayout {
			lparams {
				width = ScreenSize.widthWithPadding
				height = 12.uiPX()
			}
			addCorner(6.uiPX(), GrayScale.whiteGray)
			y = 30.uiPX().toFloat()
		}

		processLayout = linearLayout {
			val minWidth = (ScreenSize.widthWithPadding * 0.1).toInt()
			lparams {
				width = minWidth
				height = 12.uiPX()
			}
			y = 30.uiPX().toFloat()
			addCorner(6.uiPX(), Color.TRANSPARENT)
			GradientView(context).apply {
				setStyle(GradientType.BlueGreenHorizontal, ScreenSize.widthWithPadding)
			}.into(this)
		}
	}

	/** Value in 0 ~ 1 */
	fun setProgressValue(value: Double) {
		val currentValue = if (value > 1.0) 1.0 else if (value < 0.1) 0.1 else value
		processLayout.updateWidthAnimation((currentValue * ScreenSize.widthWithPadding).toInt())
	}
}