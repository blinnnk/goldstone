package io.goldstone.blockchain.module.home.quotation.quotationsearch.view

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.QuotationText
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.ScreenSize
import org.jetbrains.anko.*

/**
 * @date: 2018/9/3.
 * @author: yanglihai
 * @description:
 */
class ExchangeFilterDashboardBottomBar(val maxWidth: Int, context: Context): LinearLayout(context) {
	val confirmButton by lazy {
		RoundButton(context).apply {
			text = CommonText.confirm
			setBlueStyle()
			val buttonWidth = ScreenSize.widthWithPadding / 2 - 5.uiPX()
			val buttonHeight = 40.uiPX()
			layoutParams = LinearLayout.LayoutParams(buttonWidth, buttonHeight)
		}
		
		
	}
	
	private val textView by lazy {
		TextView(context).apply {
			text = QuotationText.selectAll
		}
	}
	private val checkBox by lazy {
		CheckBox(context).apply {
			id = ElementID.checkBox
			layoutParams = LinearLayout.LayoutParams(wrapContent, matchParent)
		}
	}
	
	private val selectedAllContainer by lazy {
		LinearLayout(context).apply {
			gravity = Gravity.RIGHT
			layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
		}
	}
	
	init {
		gravity = Gravity.CENTER
		leftPadding = PaddingSize.device
		rightPadding = PaddingSize.device
		
		addView(confirmButton)
		addView(selectedAllContainer.apply {
			addView(textView)
			addView(checkBox)
		})
	}
	
	fun setEvents(onCheckedChangeListener: CompoundButton.OnCheckedChangeListener,
		callback: () -> Unit) {
		checkBox.setOnCheckedChangeListener(onCheckedChangeListener)
		confirmButton.setOnClickListener { callback() }
	}
	
	
}