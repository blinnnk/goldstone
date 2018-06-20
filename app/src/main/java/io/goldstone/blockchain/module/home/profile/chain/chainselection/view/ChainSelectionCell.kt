package io.goldstone.blockchain.module.home.profile.chain.chainselection.view

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.measureTextWidth
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.BaseCell
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.ChainText
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.module.home.profile.chain.chainselection.model.ChainSelectionModel
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView
import org.jetbrains.anko.wrapContent

/**
 * @date 2018/5/11 4:27 PM
 * @author KaySaith
 */
class ChainSelectionCell(context: Context) : BaseCell(context) {
	
	var model: ChainSelectionModel by observing(ChainSelectionModel()) {
		icon.imageResource = model.icon
		titles.title.text = model.title
		titles.subtitle.text = model.description
		if (model.isMainnet) isUsingText.visibility = View.VISIBLE
		isUsingText.x = model.title.measureTextWidth(16.uiPX().toFloat()) + 80.uiPX()
	}
	private val titles = TwoLineTitles(context)
	private val icon = ImageView(context)
	private val cellHeight = 90.uiPX()
	private val isUsingText = textView {
		visibility = View.GONE
		textColor = Spectrum.green
		textSize = fontSize(12)
		typeface = GoldStoneFont.black(context)
		layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent)
		text = ChainText.isUsing
		y += 19.uiPX()
	}
	
	init {
		setGrayStyle()
		hasArrow = true
		layoutParams.height = cellHeight
		
		icon
			.apply {
				setColorFilter(GrayScale.black)
				scaleType = ImageView.ScaleType.CENTER_INSIDE
				layoutParams = RelativeLayout.LayoutParams(60.uiPX(), cellHeight)
			}
			.into(this)
		
		titles
			.apply {
				setBoldTiltes(GrayScale.black, GrayScale.midGray)
				x += 70.uiPX()
				layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent)
			}
			.into(this)
		
		titles.setCenterInVertical()
	}
}