package io.goldstone.blockchain.module.home.quotation.quotationsearch.view

import android.content.Context
import android.view.Gravity
import android.widget.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.value.ElementID
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
			setBlueStyle(10.uiPX(), maxWidth / 2)
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
		leftPadding = 20.uiPX()
		rightPadding = 20.uiPX()
		gravity = Gravity.CENTER_VERTICAL
		addView(checkBox)
		addView(confirmButton)
	}
	
	fun setEvents(onCheckedChangeListener: CompoundButton.OnCheckedChangeListener,
		callback: () -> Unit) {
		checkBox.setOnCheckedChangeListener(onCheckedChangeListener)
		confirmButton.setOnClickListener { callback() }
	}
	
	
}