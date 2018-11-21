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
import io.goldstone.blockchain.common.value.PaddingSize
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
		x = 60.uiPX().toFloat()
		textSize = fontSize(15)
		textColor = GrayScale.black
		typeface = GoldStoneFont.heavy(context)
	}
	private var radioButton: RadioButton
	protected var icon: ImageView? = null

	init {
		hasArrow = false
		setGrayStyle()
		this.addView(title)
		title.centerInVertical()
		radioButton = radioButton().apply {
			x -= PaddingSize.device
			isDefaultStyle(Spectrum.blue)
			isClickable = false
			alignParentRight()
			centerInVertical()
		}
		layoutParams.height = 50.uiPX()
	}

	override fun onAttachedToWindow() {
		super.onAttachedToWindow()
		if (icon.isNull()) title.x = PaddingSize.device.toFloat()
	}

	fun setSwitchStatusBy(isSelected: Boolean) {
		radioButton.isChecked = isSelected
	}

	fun showIcon(image: Int, color: Int = GrayScale.whiteGray) {
		if (icon.isNull()) {
			icon = ImageView(context).apply {
				imageResource = image
				layoutParams = RelativeLayout.LayoutParams(35.uiPX(), 35.uiPX())
				addCorner(17.uiPX(), color)
				elevation = 2.uiPX().toFloat()
				x = PaddingSize.device.toFloat()
			}
			icon?.into(this)
			icon?.centerInVertical()
		}
	}

	fun setTitle(text: String) {
		title.text = text
	}

}