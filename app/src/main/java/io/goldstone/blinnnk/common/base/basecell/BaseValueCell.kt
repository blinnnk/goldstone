package io.goldstone.blinnnk.common.base.basecell

import android.content.Context
import android.widget.ImageView
import com.blinnnk.extension.alignParentRight
import com.blinnnk.extension.centerInVertical
import com.blinnnk.extension.isNull
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.common.component.button.RoundIcon
import io.goldstone.blinnnk.common.component.title.TwoLineTitles
import io.goldstone.blinnnk.common.value.GrayScale
import io.goldstone.blinnnk.common.value.Spectrum
import org.jetbrains.anko.imageResource

/**
 * @date 24/03/2018 8:41 PM
 * @author KaySaith
 */

open class BaseValueCell(context: Context) : BaseCell(context) {

	protected val icon by lazy { RoundIcon(context) }
	protected val info by lazy { TwoLineTitles(context) }
	protected var count: TwoLineTitles? = null

	init {
		setIconColor()
		this.addView(icon)
		setHorizontalPadding()
		this.addView(info.apply {
			setBlackTitles()
			x += 60.uiPX()
		})

		icon.centerInVertical()
		info.centerInVertical()

		layoutParams.height = 75.uiPX()

	}

	fun setIconColor(color: Int = GrayScale.lightGray) {
		icon.iconColor = color
	}

	fun setIconResource(resource: Int, color: Int = Spectrum.white) {
		icon.imageResource = resource
		icon.setColorFilter(color)
	}

	fun setValueStyle(isScaleIcon: Boolean = false) {
		if (isScaleIcon) icon.scaleType = ImageView.ScaleType.CENTER_INSIDE
		else icon.scaleType = ImageView.ScaleType.CENTER_CROP
		if (count.isNull()) {
			count = TwoLineTitles(context)
			this.addView(count)
		}
		count?.apply {
			isFloatRight = true
			x -= 30.uiPX()
			setBlackTitles()
			centerInVertical()
			alignParentRight()
		}
	}
}