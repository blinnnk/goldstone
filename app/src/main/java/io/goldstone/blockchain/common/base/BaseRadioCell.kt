package io.goldstone.blockchain.common.base

import android.content.Context
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.component.HoneyRadioButton
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.textColor

/**
 * @date 26/03/2018 6:49 PM
 * @author KaySaith
 */
open class BaseRadioCell(context: Context) : BaseCell(context) {
	
	var checkedStatus: Boolean by observing(false) {
		radioButton.isChecked = checkedStatus
	}
	protected val title = TextView(context)
	private val radioButton = HoneyRadioButton(context)
	protected var icon: ImageView? = null
	
	init {
		hasArrow = false
		setGrayStyle()
		
		this.addView(title.apply {
			textSize = fontSize(15)
			textColor = GrayScale.black
			typeface = GoldStoneFont.medium(context)
		})
		
		title.setCenterInVertical()
		
		this.addView(radioButton.apply {
			setColorStyle(GrayScale.midGray, Spectrum.green)
		})
		
		radioButton.apply {
			isClickable = false
			setAlignParentRight()
			setCenterInVertical()
		}
		
		layoutParams.height = 50.uiPX()
	}
	
	fun setSwitchStatusBy(isSelected: Boolean) {
		radioButton.isChecked = isSelected
	}
	
	fun showIcon(image: Int, color: Int = GrayScale.whiteGray) {
		title.x = 50.uiPX().toFloat()
		if (icon.isNull()) {
			icon = ImageView(context).apply {
				layoutParams = RelativeLayout.LayoutParams(35.uiPX(), 35.uiPX())
				addCorner(17.uiPX(), color)
			}
			icon?.into(this)
			icon?.setCenterInVertical()
		}
		icon?.imageResource = image
	}
	
	fun setTitle(text: String) {
		title.text = text
	}
	
	fun getTitle(): String {
		return title.text.toString()
	}
}