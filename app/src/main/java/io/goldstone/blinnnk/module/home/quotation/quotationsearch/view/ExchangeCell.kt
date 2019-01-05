package io.goldstone.blinnnk.module.home.quotation.quotationsearch.view

import android.content.Context
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.alignParentRight
import com.blinnnk.extension.centerInVertical
import com.blinnnk.extension.isDefaultStyle
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blinnnk.common.base.basecell.BaseCell
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.utils.glideImage
import io.goldstone.blinnnk.common.value.GrayScale
import io.goldstone.blinnnk.common.value.Spectrum
import io.goldstone.blinnnk.common.value.fontSize
import io.goldstone.blinnnk.module.home.quotation.quotationsearch.model.ExchangeTable
import org.jetbrains.anko.*

/**
 * @date: 2018/8/29.
 * @author: yanglihai
 * @description:
 */
class ExchangeCell(context: Context) : BaseCell(context) {

	var model: ExchangeTable? by observing(null) {
		model?.apply {
			exchangeIcon.glideImage(iconUrl)
			textView.text = exchangeName
			checkBox.isChecked = isSelected
		}
	}

	var checkBox: CheckBox
	var textView: TextView
	private var exchangeIcon: ImageView
	private val iconSize = 36.uiPX()

	init {
		hasArrow = false
		setHorizontalPadding()
		exchangeIcon = imageView {
			addCorner(18.uiPX(), GrayScale.whiteGray)
			layoutParams = RelativeLayout.LayoutParams(iconSize, iconSize)
		}
		textView = textView {
			textSize = fontSize(14)
			textColor = GrayScale.black
			typeface = GoldStoneFont.heavy(context)
			x = 50.uiPX().toFloat()
		}
		checkBox = checkBox {
			isDefaultStyle(Spectrum.blue)
			layoutParams = RelativeLayout.LayoutParams(wrapContent, matchParent)
		}
		exchangeIcon.centerInVertical()
		textView.centerInVertical()
		checkBox.apply {
			centerInVertical()
			alignParentRight()
		}
		setGrayStyle()
	}
}