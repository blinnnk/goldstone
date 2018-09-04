package io.goldstone.blockchain.module.home.quotation.quotationsearch.view

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.ScreenSize
import org.jetbrains.anko.*

/**
 * @date: 2018/9/3.
 * @author: yanglihai
 * @description:
 */
class SearchConfirm(val maxWidth: Int, context: Context): LinearLayout(context) {
	val confirmButton by lazy {
		RoundButton(context).apply {
			text = CommonText.confirm
			setBlueStyle()
			val buttonWidth = ScreenSize.widthWithPadding / 2 - 5.uiPX()
			val buttonHeight = 40.uiPX()
//			val marginSize = 15.uiPX()
			layoutParams = LinearLayout.LayoutParams(buttonWidth, buttonHeight).apply {
				leftMargin = PaddingSize.device
//				topMargin = marginSize
			}
		}
		
		
	}
	val checkBox by lazy {
		CheckBox(context).apply {
			id = ElementID.checkBox
			layoutParams = LinearLayout.LayoutParams(wrapContent, matchParent)
			text = "全选"
		}
	}
	init {
		gravity = Gravity.CENTER
		backgroundColor = Color.WHITE
		addView(checkBox)
		addView(confirmButton)
	}
	
	fun setEvents(onCheckedChangeListener: CompoundButton.OnCheckedChangeListener,
		callback: () -> Unit) {
		checkBox.setOnCheckedChangeListener(onCheckedChangeListener)
		confirmButton.setOnClickListener { callback() }
	}
	
	
}