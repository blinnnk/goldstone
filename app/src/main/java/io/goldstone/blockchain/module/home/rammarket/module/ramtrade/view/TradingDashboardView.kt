package io.goldstone.blockchain.module.home.rammarket.module.ramtrade.view

import android.content.Context
import android.text.InputFilter
import android.text.method.DigitsKeyListener
import android.view.Gravity
import android.widget.*
import com.blinnnk.extension.*
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.Language.EOSRAMExchangeText
import io.goldstone.blockchain.common.component.button.ButtonMenu
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.utils.*
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.home.rammarket.model.RAMMarketPadding
import org.jetbrains.anko.*

/**
 * @date: 2018/10/31.
 * @author: yanglihai
 * @description:
 */
class TradingDashboardView(context: Context): LinearLayout(context) {
	private val menu:  ButtonMenu
	val ramEditText by lazy { RAMPriceRoundInputView(context, "KB") }
	val eosEditText by lazy { RAMPriceRoundInputView(context, "EOS") }
	val ramBalance by lazy { TextView(context) }
	val eosBalance by lazy { TextView(context) }
	private val confirmButton by lazy { RoundButton(context) }
	
	private val dashboardWidth = ScreenSize.Width / 2 + RAMMarketPadding * 2
	
	init {
	  orientation = LinearLayout.VERTICAL
		layoutParams = LinearLayout.LayoutParams(dashboardWidth, wrapContent)
		menu = ButtonMenu(context)
		menu.layoutParams = LinearLayout.LayoutParams(dashboardWidth - RAMMarketPadding * 2, 32.uiPX())
		menu.setMargins<LinearLayout.LayoutParams> {
			leftMargin = 10.uiPX()
			topMargin = 16.uiPX()
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
			setMargins<LinearLayout.LayoutParams> {
				topMargin = 16.uiPX()
				leftMargin = 5.uiPX()
			}
			title = EOSRAMExchangeText.ram
			hint = EOSRAMExchangeText.enterCountHint
			singleLine = true
			keyListener = DigitsKeyListener.getInstance("1234567890.")
			filters = arrayOf(InputFilter.LengthFilter(10))
			
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
			setMargins<LinearLayout.LayoutParams> {
				topMargin = 16.uiPX()
				leftMargin = 5.uiPX()
			}
			singleLine = true
			keyListener = DigitsKeyListener.getInstance("1234567890.")
			filters = arrayOf(InputFilter.LengthFilter(10))
		}.into(this)
		
		eosBalance.apply {
			leftPadding = 20.uiPX()
			text = EOSRAMExchangeText.eosBalanceDescription("0")
			textColor = GrayScale.midGray
			typeface = GoldStoneFont.heavy(context)
			textSize = fontSize(10)
		}.into(this)
		
		linearLayout {
			topPadding = 20.uiPX()
			leftPadding = RAMMarketPadding
			gravity = Gravity.CENTER_VERTICAL
			imageView {
				imageResource = R.drawable.trading_discipline
				setColorFilter(Spectrum.deepBlue)
				scaleType = ImageView.ScaleType.FIT_XY
			}.lparams(30.uiPX(), 30.uiPX())
			textView {
				textColor = Spectrum.blue
				textSize = fontSize(12)
				typeface = GoldStoneFont.black(context)
				text = EOSRAMExchangeText.transactionHistory
			}
		}
		
		confirmButton.apply {
			setBlueStyle(width = dashboardWidth - RAMMarketPadding)
			text = EOSRAMExchangeText.confirmToTrade
			(layoutParams as? LinearLayout.LayoutParams)?.apply {
				gravity = Gravity.CENTER_HORIZONTAL
			}
			setMargins<LinearLayout.LayoutParams> {
				topMargin = 11.uiPX()
				leftMargin = 5.uiPX()
				rightMargin = 5.uiPX()
			}
		}.into(this)
		
	}
	
}