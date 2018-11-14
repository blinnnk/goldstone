package io.goldstone.blockchain.module.home.quotation.quotationsearch.view

import android.content.Context
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.isDefaultStyle
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.basecell.BaseCell
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.module.home.quotation.quotationsearch.model.ExchangeTable
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
		exchangeIcon.setCenterInVertical()
		textView.setCenterInVertical()
		checkBox.apply {
			setCenterInVertical()
			setAlignParentRight()
		}
		setGrayStyle()
	}
}