package io.goldstone.blockchain.module.home.rammarket.module.ramtrade.view

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.extension.*
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.Language.EOSRAMExchangeText
import io.goldstone.blockchain.common.component.button.ButtonMenu
import io.goldstone.blockchain.common.component.edittext.RoundInput
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.utils.*
import io.goldstone.blockchain.common.value.*
import org.jetbrains.anko.*

/**
 * @date: 2018/10/31.
 * @author: yanglihai
 * @description:
 */
class TradingDashboardView(context: Context): LinearLayout(context) {
	private val menu:  ButtonMenu
	private val ramEditText by lazy { RAMPriceRoundInputView(context, "KB") }
	private val eosEditText by lazy { RAMPriceRoundInputView(context, "EOS") }
	private val ramBalance by lazy { TextView(context) }
	private val eosBalance by lazy { TextView(context) }
	private val confirmButton by lazy { RoundButton(context) }
	
	init {
	  orientation = LinearLayout.VERTICAL
		
		menu = ButtonMenu(context)
		menu.layoutParams = LinearLayout.LayoutParams(ScreenSize.Width / 2, 32.uiPX())
		menu.setMargins<LinearLayout.LayoutParams> {
			topMargin = 16.uiPX()
			bottomMargin = 14.uiPX()
		}
		menu.titles = listOf(EOSRAMExchangeText.buy(""), EOSRAMExchangeText.sell(""))
		menu.getButton { button ->
			button.click {
				menu.selected(button.id)
				if (button.id == 0) {
				
				} else {
				
				}
			}
		}
		menu.selected(0)
		menu.into(this)
		ramEditText.apply {
			layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
			setMargins<LinearLayout.LayoutParams> { topMargin = 16.uiPX() }
			title = EOSRAMExchangeText.ram
			hint = EOSRAMExchangeText.enterCountHint
			singleLine = true
		}.into(this)
		
		ramBalance.apply {
			leftPadding = 20.uiPX()
			text = EOSRAMExchangeText.ramBalanceDescription("0")
			textColor = GrayScale.midGray
			typeface = GoldStoneFont.heavy(context)
			textSize = fontSize(10)
		}.into(this)
		
		eosEditText.apply {
			title = EOSRAMExchangeText.eos
			hint = EOSRAMExchangeText.enterCountHint
			layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
			setMargins<LinearLayout.LayoutParams> { topMargin = 16.uiPX() }
			singleLine = true
		}.into(this)
		
		eosBalance.apply {
			leftPadding = 20.uiPX()
			text = EOSRAMExchangeText.eosBalanceDescription("0")
			textColor = GrayScale.midGray
			typeface = GoldStoneFont.heavy(context)
			textSize = fontSize(10)
			
		}.into(this)
		
		textView {
			topPadding = 36.uiPX()
			textColor = Spectrum.blue
			textSize = fontSize(12)
			typeface = GoldStoneFont.black(context)
			text = EOSRAMExchangeText.transactionHistory
			gravity = Gravity.CENTER_VERTICAL
			setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.back,0, 0, 0)
		}
		
		confirmButton.apply {
			setBlueStyle(width = ScreenSize.Width / 2 - 40.uiPX())
			text = EOSRAMExchangeText.confirmToTrade
			(layoutParams as? LinearLayout.LayoutParams)?.apply {
				gravity = Gravity.CENTER_HORIZONTAL
			}
		}.into(this)
		
	}
	
}