package io.goldstone.blockchain.common.base.basecell

import android.content.Context
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.radioButton
import org.jetbrains.anko.textColor

/**
 * @date 26/03/2018 6:49 PM
 * @author KaySaith
 */
open class BaseRadioCell(context: Context) : BaseCell(context) {

	var checkedStatus: Boolean by observing(false) {
		radioButton.isChecked = checkedStatus
	}
	protected val title = TextView(context).apply {
		x = 50.uiPX().toFloat()
		textSize = fontSize(15)
		textColor = GrayScale.black
		typeface = GoldStoneFont.medium(context)
	}
	private var radioButton: RadioButton
	protected var icon: ImageView? = null

	init {
		hasArrow = false
		setGrayStyle()
		setHorizontalPadding()
		this.addView(title)
		title.centerInVertical()
		radioButton = radioButton().apply {
			isDefaultStyle(Spectrum.blue)
			isClickable = false
			alignParentRight()
			centerInVertical()
		}
		layoutParams.height = 50.uiPX()
	}

	override fun onAttachedToWindow() {
		super.onAttachedToWindow()
		if (icon.isNull()) title.x = 0f
	}

	fun setSwitchStatusBy(isSelected: Boolean) {
		radioButton.isChecked = isSelected
	}

	fun showIcon(image: Int, color: Int = GrayScale.whiteGray) {
		if (icon.isNull()) {
			icon = ImageView(context).apply {
				layoutParams = RelativeLayout.LayoutParams(35.uiPX(), 35.uiPX())
				addCorner(17.uiPX(), color)
			}
			icon?.into(this)
			icon?.centerInVertical()
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