package io.goldstone.blinnnk.common.base.basecell

import android.content.Context
import android.view.ViewManager
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.value.GrayScale
import io.goldstone.blinnnk.common.value.Spectrum
import io.goldstone.blinnnk.common.value.fontSize
import org.jetbrains.anko.custom.ankoView
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
		this.addView(title)
		title.centerInVertical()
		radioButton = radioButton().apply {
			isDefaultStyle(Spectrum.blue)
			isClickable = false
			alignParentRight()
			centerInVertical()
		}
		setHorizontalPadding()
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
				elevation = 2.uiPX().toFloat()
			}
			icon?.into(this)
			icon?.centerInVertical()
		}
		icon?.imageResource = image
	}

	fun setTitle(text: String) {
		title.text = text
	}

}


fun ViewManager.radioCell() = radioCell {}
inline fun ViewManager.radioCell(init: BaseRadioCell.() -> Unit) = ankoView({ BaseRadioCell(it) }, 0, init)