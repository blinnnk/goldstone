package io.goldstone.blockchain.common.component

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.extension.addCorner
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.CornerSize
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.Spectrum
import org.jetbrains.anko.leftPadding
import org.jetbrains.anko.rightPadding

/**
 * @date 22/03/2018 1:48 PM
 * @author KaySaith
 */

class AttentionView(context: Context) : TextView(context) {

	init {
		typeface = GoldStoneFont.medium(context)
		gravity = Gravity.CENTER_VERTICAL

		layoutParams = LinearLayout.LayoutParams(
			ScreenSize.Width - PaddingSize.device * 2, 95.uiPX()
		).apply {
			leftPadding = 20.uiPX()
			rightPadding = 20.uiPX()
		}

		setBackgroundColor(Spectrum.green)
	}

	override fun setBackgroundColor(color: Int) {
		addCorner(
			CornerSize.default.toInt(), color
		)
	}

}