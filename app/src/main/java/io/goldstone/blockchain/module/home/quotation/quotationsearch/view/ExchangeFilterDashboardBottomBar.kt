package io.goldstone.blockchain.module.home.quotation.quotationsearch.view

import android.content.Context
import android.view.Gravity
import android.widget.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.QuotationText
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.common.value.ScreenSize
import org.jetbrains.anko.*

/**
 * @date: 2018/9/3.
 * @author: yanglihai
 * @description:
 */
class ExchangeFilterDashboardBottomBar(context: Context): LinearLayout(context) {
	
	var confirmButtonClickEvent: Runnable? = null
	var checkAllEvent: Runnable? = null
	val confirmButton by lazy {
		RoundButton(context).apply {
			text = CommonText.confirm
			setBlueStyle()
			val buttonWidth = ScreenSize.widthWithPadding / 2 - 5.uiPX()
			val buttonHeight = 40.uiPX()
			layoutParams = LinearLayout.LayoutParams(buttonWidth, buttonHeight)
			click {
				confirmButtonClickEvent?.run()
			}
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
			click { checkAllEvent?.run() }
		}
	}
	
	private val selectedAllContainer by lazy {
		LinearLayout(context).apply {
			gravity = Gravity.END
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
}