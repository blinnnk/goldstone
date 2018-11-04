package io.goldstone.blockchain.module.home.quotation.quotationsearch.view

import android.content.Context
import android.view.Gravity
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.QuotationText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.utils.isDefaultStyle
import io.goldstone.blockchain.common.value.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * @date: 2018/9/3.
 * @author: yanglihai
 * @description:
 */
class ExchangeFilterDashboardBottomBar(context: Context) : LinearLayout(context) {

	var confirmButtonClickEvent: Runnable? = null
	var checkAllEvent: Runnable? = null
	val confirmButton by lazy {
		RoundButton(context).apply {
			text = CommonText.confirm
			setBlueStyle(6.uiPX(), OverlaySize.maxWidth - 135.uiPX())
			onClick { confirmButtonClickEvent?.run() }
		}
	}

	private lateinit var textView: TextView
	private lateinit var checkBox: CheckBox

	private val selectedAllContainer by lazy {
		LinearLayout(context).apply {
			gravity = Gravity.END
			layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
		}
	}

	init {
		layoutParams = RelativeLayout.LayoutParams(matchParent, 60.uiPX())
		backgroundColor = GrayScale.whiteGray
		gravity = Gravity.CENTER
		leftPadding = PaddingSize.device
		rightPadding = PaddingSize.device
		addView(confirmButton)
		selectedAllContainer.apply {
			textView = textView {
				text = QuotationText.selectAll.toUpperCase()
				textColor = GrayScale.black
				typeface = GoldStoneFont.black(context)
				textSize = fontSize(12)
			}
			checkBox = checkBox {
				isDefaultStyle()
				id = ElementID.checkBox
				layoutParams = LinearLayout.LayoutParams(wrapContent, matchParent)
				click { checkAllEvent?.run() }
			}
		}.into(this)
	}
}