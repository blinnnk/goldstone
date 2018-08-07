package io.goldstone.blockchain.module.home.quotation.quotation.view

import android.content.Context
import android.widget.TextView
import com.blinnnk.animation.updateOriginYAnimation
import com.blinnnk.extension.into
import com.blinnnk.extension.setCenterInParent
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.button.CircleButton
import io.goldstone.blockchain.common.component.SliderHeader
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.language.QuotationText
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.textColor

/**
 * @date 26/03/2018 9:07 PM
 * @author KaySaith
 */
class QuotationSlideHeader(context: Context) : SliderHeader(context) {
	
	val addTokenButton by lazy { CircleButton(context) }
	private val title = TextView(context)
	
	init {
		addTokenButton.apply {
			title = QuotationText.addToken
			src = R.drawable.add_token_icon
			x += PaddingSize.device
			y = 15.uiPX().toFloat()
		}.into(this)
		
		addTokenButton.apply {
			setCenterInVertical()
		}
		
		title.apply {
			text = QuotationText.market
			textColor = Spectrum.white
			textSize = fontSize(15)
			typeface = GoldStoneFont.heavy(context)
		}.into(this)
		
		title.setCenterInParent()
	}
	
	override fun onHeaderShowedStyle() {
		super.onHeaderShowedStyle()
		addTokenButton.setUnTransparent()
		title.updateOriginYAnimation(26.uiPX().toFloat())
	}
	
	override fun onHeaderHidesStyle() {
		super.onHeaderHidesStyle()
		addTokenButton.setDefaultStyle()
		title.updateOriginYAnimation(34.uiPX().toFloat())
	}
}