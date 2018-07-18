package io.goldstone.blockchain.module.home.wallet.walletsettings.allsinglechainaddresses.view

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.clickToCopy
import io.goldstone.blockchain.common.component.GraySqualCellWithButtons
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.crypto.ChainType
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 2018/7/16 9:54 PM
 * @author KaySaith
 */
class ChainAddressesHeaderView(context: Context) : LinearLayout(context) {
	
	private val defaultTitle = textView {
		textSize = fontSize(12)
		typeface = GoldStoneFont.heavy(context)
		textColor = GrayScale.black
		gravity = Gravity.START or Gravity.CENTER_VERTICAL
		layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, 40.uiPX())
	}
	private val currentAddress = GraySqualCellWithButtons(context)
	private val allAddressTitle = TextView(context).apply {
		textSize = fontSize(12)
		typeface = GoldStoneFont.heavy(context)
		textColor = GrayScale.black
		gravity = Gravity.START or Gravity.CENTER_VERTICAL
		layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, 40.uiPX())
	}
	
	init {
		topPadding = 10.uiPX()
		orientation = VERTICAL
		gravity = Gravity.CENTER_HORIZONTAL
		layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
		defaultTitle.text = WalletSettingsText.defaultAddress
		currentAddress.into(this)
		allAddressTitle.into(this)
		currentAddress.updateBackgroundColor()
	}
	
	fun setDefaultAddress(
		index: String,
		address: String,
		chainType: Int,
		showDashboardEvent: GraySqualCellWithButtons.() -> Unit
	) {
		setClickEvent(address, showDashboardEvent)
		currentAddress.setTitle(index)
		val halfSize = if (chainType == ChainType.BTC.id) 12 else 14
		currentAddress.setSubtitle(CryptoUtils.scaleMiddleAddress(address, halfSize))
		when (chainType) {
			ChainType.ETH.id -> {
				allAddressTitle.text = WalletSettingsText.allETHAndERCAddresses
			}
			
			ChainType.ETC.id -> {
				allAddressTitle.text = WalletSettingsText.allETCAddresses
			}
			
			ChainType.BTC.id -> {
				allAddressTitle.text =
					if (Config.isTestEnvironment()) WalletSettingsText.allBtCTestAddresses
					else WalletSettingsText.allBtCAddresses
			}
		}
	}
	
	private fun setClickEvent(
		address: String,
		showDashboardEvent: GraySqualCellWithButtons.() -> Unit
	) {
		currentAddress.copyButton.onClick {
			currentAddress.context.clickToCopy(address)
		}
		currentAddress.moreButton.onClick {
			showDashboardEvent(currentAddress)
			currentAddress.preventDuplicateClicks()
		}
	}
}