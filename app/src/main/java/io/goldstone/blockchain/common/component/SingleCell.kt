package io.goldstone.blockchain.common.component

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.BaseCell
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.PaddingSize
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView
import org.jetbrains.anko.wrapContent

/**
 * @date 2018/5/13 9:28 PM
 * @author KaySaith
 */

class SingleCell(context: Context) : BaseCell(context) {

	private var titleView: TextView
	private var subtitleView: TextView

	init {
		hasTopLine()
		layoutParams = LinearLayout.LayoutParams(
			io.goldstone.blockchain.common.value.ScreenSize.widthWithPadding, 40.uiPX()
		).apply {
			leftMargin = PaddingSize.device
		}
		setGrayStyle()
		titleView = textView {
			textColor = GrayScale.black
			textSize = 5.uiPX().toFloat()
			typeface = GoldStoneFont.heavy(context)
		}
		titleView.setCenterInVertical()

		subtitleView = textView {
			layoutParams = LinearLayout.LayoutParams(wrapContent, wrapContent)
			textColor = GrayScale.gray
			textSize = 5.uiPX().toFloat()
			typeface = GoldStoneFont.book(context)
			gravity = Gravity.END
		}.apply {
			setCenterInVertical()
			setAlignParentRight()
			x -= 30.uiPX()
		}
	}

	fun setTitle(title: String) {
		titleView.text = title
	}

	fun setSubtitle(title: String) {
		subtitleView.text = title
	}

}