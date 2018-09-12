package io.goldstone.blockchain.common.component.button

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.extension.into
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.CornerSize
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor
import org.jetbrains.anko.wrapContent


/**
 * @author KaySaith
 * @date  2018/09/11
 */
class BasicRadiusButton(context: Context) : LinearLayout(context) {

	private val pendingColor = Color.parseColor("#FF227CA0")
	private val settingColor = Color.parseColor("#FF0C5071")

	private val title = TextView(context).apply {
		textSize = fontSize(12)
		typeface = GoldStoneFont.heavy(context)
		textColor = Spectrum.white
		layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
		gravity = Gravity.CENTER
	}

	init {
		setPadding(10.uiPX(), 2.uiPX(), 10.uiPX(), 2.uiPX())
		gravity = Gravity.CENTER
		title.into(this)
	}

	fun setTitle(text: String) {
		title.text = text
	}

	fun setStyle(style: Style) {
		val themeColor = when (style) {
			Style.Pending -> pendingColor
			Style.ToBeSet -> settingColor
		}
		addTouchRippleAnimation(themeColor, Spectrum.green, RippleMode.Square, CornerSize.small)
	}

	companion object {
		 enum class Style {
			Pending, ToBeSet
		}
	}
}