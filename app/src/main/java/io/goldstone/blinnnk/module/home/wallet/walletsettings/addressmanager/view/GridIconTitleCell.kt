package io.goldstone.blinnnk.module.home.wallet.walletsettings.addressmanager.view

import android.content.Context
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.value.GrayScale
import io.goldstone.blinnnk.common.value.fontSize
import io.goldstone.blinnnk.module.home.wallet.walletsettings.addressmanager.model.GridIconTitleModel
import org.jetbrains.anko.*


/**
 * @author KaySaith
 * @date  2018/11/18
 */
class GridIconTitleCell(context: Context) : LinearLayout(context) {
	var model: GridIconTitleModel? by observing(null) {
		model?.apply {
			icon.imageResource = imageResource
			textView.text = name
		}
	}
	private var icon: ImageView
	private var textView: TextView

	init {
		topPadding = 10.uiPX()
		orientation = LinearLayout.VERTICAL
		layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
		gravity = Gravity.CENTER_HORIZONTAL
		icon = imageView {
			scaleType = ImageView.ScaleType.CENTER_CROP
			layoutParams = LinearLayout.LayoutParams(50.uiPX(), 50.uiPX())
		}
		textView = textView {
			gravity = Gravity.CENTER_HORIZONTAL
			textSize = fontSize(11)
			layoutParams = LinearLayout.LayoutParams(80.uiPX(), wrapContent)
			textColor = GrayScale.midGray
			typeface = GoldStoneFont.medium(context)
		}
	}
}