package io.goldstone.blockchain.module.home.wallet.walletsettings.addressmanager.view

import android.content.Context
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.extension.centerInVertical
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.basecell.BaseCell
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.*


/**
 * @author KaySaith
 * @date  2018/11/18
 */
class AddressGeneratorCell(context: Context) : BaseCell(context) {
	var model: Pair<Int, String>? by observing(null) {
		model?.apply {
			icon.imageResource = first
			textView.text = second
		}
	}
	private var icon: ImageView
	private var textView: TextView

	init {
		setHorizontalPadding()
		layoutParams.height = 45.uiPX()
		setGrayStyle()
		icon = imageView {
			scaleType = ImageView.ScaleType.CENTER_INSIDE
			layoutParams = LinearLayout.LayoutParams(35.uiPX(), 35.uiPX())
		}
		icon.centerInVertical()
		textView = textView {
			x = 40.uiPX().toFloat()
			textSize = fontSize(12)
			layoutParams = LinearLayout.LayoutParams(wrapContent, wrapContent)
			textColor = GrayScale.black
			typeface = GoldStoneFont.heavy(context)
			gravity = Gravity.CENTER_VERTICAL
		}
		textView.centerInVertical()
	}
}