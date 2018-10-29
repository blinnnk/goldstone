package io.goldstone.blockchain.common.component.button

import android.content.Context
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.addCircleBorder
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.common.value.CornerSize
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor
import org.jetbrains.anko.wrapContent


/**
 * @author KaySaith
 * @date  2018/09/11
 */
class RadiusButton(context: Context) : RelativeLayout(context) {
	private val icon = ImageView(context)
	private val title = TextView(context)
	private val arrowIcon = ImageView(context)
	private val buttonHeight = 40.uiPX()

	init {
		layoutParams = RelativeLayout.LayoutParams(matchParent, buttonHeight)
		addCircleBorder(CornerSize.small.toInt(), 1, GrayScale.midGray)
		icon.apply {
			layoutParams = RelativeLayout.LayoutParams(buttonHeight, buttonHeight)
			scaleType = ImageView.ScaleType.CENTER_INSIDE
		}.into(this)
		title.apply {
			textSize = fontSize(12)
			typeface = GoldStoneFont.heavy(context)
			textColor = Spectrum.blue
			layoutParams = RelativeLayout.LayoutParams(matchParent, wrapContent)
			x = icon.layoutParams.width.toFloat() + 10.uiPX()
		}.into(this)
		title.setCenterInVertical()
		arrowIcon.apply {
			layoutParams = RelativeLayout.LayoutParams(buttonHeight, buttonHeight)
			imageResource = R.drawable.arrow_icon
			scaleType = ImageView.ScaleType.CENTER_INSIDE
			x += 5.uiPX()
			setColorFilter(GrayScale.lightGray)
		}.into(this)
		arrowIcon.setCenterInVertical()
		arrowIcon.setAlignParentRight()
	}

	fun setTitle(text: String) {
		title.text = text
	}

	fun <T>setIcon(image: T) {
		icon.glideImage(image)
	}
}