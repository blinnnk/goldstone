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
import io.goldstone.blockchain.common.component.edittext.RoundInput
import io.goldstone.blockchain.common.utils.*
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.StakeType
import io.goldstone.blockchain.module.home.rammarket.model.RAMMarketPadding
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * @date: 2018/10/31.
 * @author: yanglihai
 * @description:
 */
class TradingDashboardView(context: Context): LinearLayout(context) {
	private val menu:  TradingDashboardMenu
	val ramEditText = RoundInput(context).apply {
		showRightLabel("KB")
	}
	val eosEditText = RoundInput(context).apply {
		showRightLabel("EOS")
	}
	val ramBalance = TextView(context)
	val eosBalance = TextView(context)
	private val confirmButton = RoundButton(context)
	private var showHistoryEvent: Runnable? = null
	private var confirmEvent: Runnable? = null
	private val dashboardWidth = ScreenSize.Width / 2 + RAMMarketPadding * 2
	
	var stakeType = StakeType.BuyRam
	
	fun setShowHistoryEvent(runnable: Runnable) {
		showHistoryEvent = runnable
	}
	fun setConfirmEvent(runnable: Runnable) {
		confirmEvent = runnable
	}
	
	init {
	  orientation = LinearLayout.VERTICAL
		layoutParams = LinearLayout.LayoutParams(dashboardWidth, wrapContent)
		menu = TradingDashboardMenu(context)
		menu.layoutParams = LinearLayout.LayoutParams(dashboardWidth - RAMMarketPadding * 2, 32.uiPX())
		menu.setMargins<LinearLayout.LayoutParams> {
			leftMargin = 10.uiPX()
			topMargin = 15.uiPX()
		}
		menu.titles = listOf(EOSRAMExchangeText.buy(""), EOSRAMExchangeText.sell(""))
		menu.getButton { button ->
			button.click {
				menu.selected(button.id)
				if (button.id == 0) {
					stakeType = StakeType.BuyRam
					confirmButton.setBlueStyle(width = dashboardWidth - RAMMarketPadding)
					confirmButton.resetConfirmParams()
				} else {
					stakeType = StakeType.SellRam
					confirmButton.setRedStyle(width = dashboardWidth - RAMMarketPadding)
					confirmButton.resetConfirmParams()
				}
			}
		}
		menu.selected(0)
		menu.into(this)
		ramEditText.apply {
			layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
			setMargins<LinearLayout.LayoutParams> {
				topMargin = 10.uiPX()
				leftMargin = 5.uiPX()
			}
			title = EOSRAMExchangeText.ram
			hint = EOSRAMExchangeText.enterCountHint
			singleLine = true
			keyListener = DigitsKeyListener.getInstance("1234567890.")
			filters = arrayOf(InputFilter.LengthFilter(10))
			
		}.into(this)
		
		ramBalance.apply {
			leftPadding = RAMMarketPadding
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
				leftMargin = 5.uiPX()
			}
			singleLine = true
			keyListener = DigitsKeyListener.getInstance("1234567890.")
			filters = arrayOf(InputFilter.LengthFilter(10))
		}.into(this)
		
		eosBalance.apply {
			leftPadding = RAMMarketPadding
			text = EOSRAMExchangeText.eosBalanceDescription("0")
			textColor = GrayScale.midGray
			typeface = GoldStoneFont.heavy(context)
			textSize = fontSize(10)
		}.into(this)
		
		linearLayout {
			leftPadding = RAMMarketPadding
			gravity = Gravity.CENTER_VERTICAL
			layoutParams = LinearLayout.LayoutParams(wrapContent, 36.uiPX())
			imageView {
				imageResource = R.drawable.trading_discipline
				setColorFilter(Spectrum.deepBlue)
				scaleType = ImageView.ScaleType.FIT_XY
			}.lparams(20.uiPX(), 20.uiPX())
			textView {
				textColor = Spectrum.blue
				textSize = fontSize(12)
				typeface = GoldStoneFont.black(context)
				text = EOSRAMExchangeText.transactionHistory
			}
			onClick {
				showHistoryEvent?.run()
			}
		}
		
		confirmButton.apply {
			setBlueStyle(width = dashboardWidth - RAMMarketPadding)
			text = EOSRAMExchangeText.confirmToTrade
			resetConfirmParams()
			onClick { confirmEvent?.run() }
		}.into(this)
		
	}
	
	private fun RoundButton.resetConfirmParams() {
		(layoutParams as? LinearLayout.LayoutParams)?.apply {
			gravity = Gravity.CENTER_HORIZONTAL
		}
		setMargins<LinearLayout.LayoutParams> {
			leftMargin = 5.uiPX()
			rightMargin = 5.uiPX()
		}
	}
	
}