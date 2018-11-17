package io.goldstone.blockchain.common.component.edittext

import android.content.Context
import android.content.res.ColorStateList
import android.view.Gravity
import android.widget.EditText
import android.widget.RelativeLayout
import com.blinnnk.extension.alignParentRight
import com.blinnnk.extension.centerInVertical
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.*

/**
 * @date 2018/7/12 4:42 PM
 * @author KaySaith
 */
class TitleEditText(context: Context) : RelativeLayout(context) {

	private val viewHeight = 50.uiPX()
	private val titleView = textView {
		gravity = Gravity.CENTER_VERTICAL
		textColor = GrayScale.black
		typeface = GoldStoneFont.black(context)
		layoutParams = RelativeLayout.LayoutParams(wrapContent, viewHeight)
		y -= 3.uiPX()
	}
	private val editText = editText {
		layoutParams = RelativeLayout.LayoutParams(wrapContent, matchParent)
		minWidth = 150.uiPX()
		typeface = GoldStoneFont.black(context)
		backgroundTintList = ColorStateList.valueOf(Spectrum.blue)
		textColor = GrayScale.black
		textSize = fontSize(14)
		hint = "0'/0/0"
	}

	init {
		layoutParams = RelativeLayout.LayoutParams(matchParent, viewHeight)
		editText.alignParentRight()
		titleView.centerInVertical()
	}

	fun setTitle(text: String, textSize: Float = fontSize(14)) {
		titleView.text = text
		titleView.textSize = textSize
	}

	fun getEditText(): EditText {
		return editText
	}
}