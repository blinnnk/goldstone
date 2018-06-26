package io.goldstone.blockchain.common.component

import android.content.Context
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.textColor
import org.jetbrains.anko.topPadding
import org.jetbrains.anko.wrapContent

/**
 * @date 2018/6/27 1:32 AM
 * @author KaySaith
 */
class IconWithTitle(context: Context) : LinearLayout(context) {
	
	private val icon = ImageView(context)
	private val title = TextView(context)
	
	init {
		topPadding = 20.uiPX()
		orientation = VERTICAL
		gravity = Gravity.CENTER_HORIZONTAL
		icon.apply {
			scaleType = ImageView.ScaleType.CENTER_INSIDE
			setColorFilter(GrayScale.midGray)
		}.into(this)
		
		title.apply {
			topPadding = 5.uiPX()
			layoutParams = LinearLayout.LayoutParams(wrapContent, wrapContent)
			typeface = GoldStoneFont.heavy(context)
			textSize = fontSize(12)
			textColor = GrayScale.midGray
		}.into(this)
	}
	
	fun setCcontent(imageSrc: String, text: String, iconSize: Int = 50.uiPX()) {
		icon.glideImage(imageSrc)
		icon.layoutParams = LinearLayout.LayoutParams(iconSize, iconSize)
		title.text = text
	}
}