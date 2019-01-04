package io.goldstone.blinnnk.common.component.container

import android.content.Context
import android.widget.ImageView
import android.widget.RelativeLayout
import com.blinnnk.extension.addCircleBorder
import com.blinnnk.extension.alignParentBottom
import com.blinnnk.extension.alignParentRight
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.R
import io.goldstone.blinnnk.common.component.title.TwoLineTitles
import io.goldstone.blinnnk.common.value.BorderSize
import io.goldstone.blinnnk.common.value.CornerSize
import io.goldstone.blinnnk.common.value.ScreenSize
import io.goldstone.blinnnk.common.value.Spectrum
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.padding
import org.jetbrains.anko.wrapContent

/**
 * @date 2018/6/6 1:21 AM
 * @author KaySaith
 */
class BorderCardView(context: Context) : RelativeLayout(context) {

	private val titles = TwoLineTitles(context)
	private val icon = ImageView(context)

	init {
		layoutParams = RelativeLayout.LayoutParams(ScreenSize.widthWithPadding, 140.uiPX())
		addCircleBorder(CornerSize.default.toInt(), BorderSize.crude.toInt(), Spectrum.white)
		padding = 20.uiPX()
		titles.apply {
			layoutParams = RelativeLayout.LayoutParams(ScreenSize.widthWithPadding - 80.uiPX(), wrapContent)
			setBigWhiteStyle(18, 13, 10.uiPX())
		}.into(this)

		icon.apply {
			imageResource = R.drawable.add_icon
			scaleType = ImageView.ScaleType.FIT_CENTER
			setColorFilter(Spectrum.white)
			layoutParams = RelativeLayout.LayoutParams(40.uiPX(), 40.uiPX())
		}.into(this)

		icon.alignParentBottom()
		icon.alignParentRight()
	}


	fun setTitles(title: String, subtitle: String) {
		titles.apply {
			this.title.text = title
			this.subtitle.text = subtitle
		}
	}
}