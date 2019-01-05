package io.goldstone.blinnnk.common.component.button

import android.content.Context
import android.view.Gravity
import android.view.ViewManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.centerInParent
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.utils.glideImage
import io.goldstone.blinnnk.common.value.GrayScale
import io.goldstone.blinnnk.common.value.ShadowSize
import io.goldstone.blinnnk.common.value.Spectrum
import io.goldstone.blinnnk.common.value.fontSize
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView

/**
 * @date 2018/6/27 1:32 AM
 * @author KaySaith
 */
class IconWithTitle(context: Context) : LinearLayout(context) {

	private var icon: RelativeLayout
	private val title = TextView(context)

	init {
		orientation = VERTICAL
		gravity = Gravity.CENTER_HORIZONTAL
		icon = relativeLayout {
			padding = 12.uiPX()
			elevation = ShadowSize.Cell
		}
		title.apply {
			topPadding = 5.uiPX()
			layoutParams = LinearLayout.LayoutParams(wrapContent, wrapContent)
			typeface = GoldStoneFont.medium(context)
			textSize = fontSize(12)
			textColor = GrayScale.midGray
		}.into(this)
	}

	fun <T> setContent(imageSrc: T, text: String, color: Int, iconSize: Int = 60.uiPX()) {
		icon.apply {
			addCorner(60.uiPX(), color)
			layoutParams = LinearLayout.LayoutParams(iconSize, iconSize)
			imageView {
				glideImage(imageSrc)
				layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
				scaleType = ImageView.ScaleType.CENTER_INSIDE
				setColorFilter(Spectrum.white)
				centerInParent()
			}
		}
		title.text = text
	}
}

fun ViewManager.titleIcon() = titleIcon {}
inline fun ViewManager.titleIcon(init: IconWithTitle.() -> Unit) = ankoView({ IconWithTitle(it) }, 0, init)