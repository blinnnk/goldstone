package io.goldstone.blockchain.common.component

import android.content.Context
import android.view.Gravity
import android.widget.EditText
import android.widget.RelativeLayout
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
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
		typeface = GoldStoneFont.heavy(context)
		layoutParams = RelativeLayout.LayoutParams(wrapContent, viewHeight)
	}
	private val editText = editText {
		layoutParams = RelativeLayout.LayoutParams(200.uiPX(), matchParent)
		typeface = GoldStoneFont.heavy(context)
		textColor = GrayScale.black
		textSize = fontSize(14)
		hint = "0'/0/0"
	}
	
	init {
		layoutParams = RelativeLayout.LayoutParams(matchParent, viewHeight)
		editText.setAlignParentRight()
	}
	
	fun setTitle(text: String, textSize: Float = fontSize(14)) {
		titleView.text = text
		titleView.textSize = textSize
	}
	
	fun getEditText(): EditText {
		return editText
	}
	
	fun getText(): String {
		return if (editText.text.isEmpty()) ""
		else editText.text.toString()
	}
}