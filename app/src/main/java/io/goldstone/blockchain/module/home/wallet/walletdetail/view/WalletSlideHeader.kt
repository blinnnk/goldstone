package io.goldstone.blockchain.module.home.wallet.walletdetail.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInParent
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.language.ChainText
import io.goldstone.blockchain.common.language.WalletText
import io.goldstone.blockchain.common.component.button.CircleButton
import io.goldstone.blockchain.common.component.SliderHeader
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.crypto.utils.formatCurrency

/**
 * @date 24/03/2018 12:50 AM
 * @author KaySaith
 */
@SuppressLint("SetTextI18n")
class WalletSlideHeader(context: Context) : SliderHeader(context) {
	
	val historyButton by lazy { CircleButton(context) }
	val notifyButton by lazy { CircleButton(context) }
	private val balance by lazy { TwoLineTitles(context) }
	
	init {
		historyButton.apply {
			title = WalletText.historyButton
			src = R.drawable.transfer_icon_small
			x += PaddingSize.device
			y = 15.uiPX().toFloat()
		}.into(this)
		
		historyButton.apply {
			setCenterInVertical()
		}
		
		notifyButton.apply {
			title = WalletText.notifyButton
			src = R.drawable.notifications_icon
			x -= PaddingSize.device
			y = 15.uiPX().toFloat()
		}.into(this)
		
		notifyButton.apply {
			setCenterInVertical()
			setAlignParentRight()
		}
		
		balance.apply {
			title.textSize = fontSize(18)
			title.typeface = GoldStoneFont.black(context)
			title.y += 3.uiPX()
			subtitle.apply {
				text = setBalanceInfo()
				textSize = fontSize(12)
			}
			isCenter = true
			visibility = View.GONE
			y -= 7.uiPX()
		}.into(this)
	}
	
	override fun onHeaderShowedStyle() {
		super.onHeaderShowedStyle()
		historyButton.setUnTransparent()
		notifyButton.setUnTransparent()
		
		balance.apply {
			setCenterInParent()
			visibility = View.VISIBLE
		}
		
		setBalanceValue(Config.getCurrentBalance().formatCurrency())
	}
	
	override fun onHeaderHidesStyle() {
		super.onHeaderHidesStyle()
		historyButton.setDefaultStyle()
		notifyButton.setDefaultStyle()
		balance.visibility = View.GONE
	}
	
	private fun setBalanceValue(value: String) {
		balance.title.text = value
	}
	
	companion object {
		fun setBalanceInfo(): String {
			return if (!Config.isTestEnvironment()) {
				WalletText.totalAssets + " " + Config.getCurrencyCode()
			} else {
				ChainText.testnet + " · " + WalletText.totalAssets + " (" + Config.getCurrencyCode() + ")"
			}
		}
	}
}