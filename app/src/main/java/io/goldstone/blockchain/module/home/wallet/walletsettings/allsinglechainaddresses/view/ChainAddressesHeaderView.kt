package io.goldstone.blockchain.module.home.wallet.walletsettings.allsinglechainaddresses.view

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.clickToCopy
import io.goldstone.blockchain.common.component.cell.GraySquareCellWithButtons
import io.goldstone.blockchain.common.component.cell.GraySquareCellWithButtons.Companion
import io.goldstone.blockchain.common.language.WalletSettingsText
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.crypto.multichain.*
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
	private val currentAddress = GraySquareCellWithButtons(context)
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
		currentAddress.updateStyle(Companion.CellType.Default)
	}

	fun setDefaultAddress(
		index: String,
		address: String,
		chainType: ChainType,
		showDashboardEvent: GraySquareCellWithButtons.() -> Unit
	) {
		setClickEvent(address, showDashboardEvent)
		currentAddress.setTitle(index)
		val halfSize = if (chainType.isBTC()) 12 else 14
		currentAddress.setSubtitle(CryptoUtils.scaleMiddleAddress(address, halfSize))
		when {
			chainType.isETH() -> allAddressTitle.text = WalletSettingsText.allETHSeriesAddresses
			chainType.isETC() -> allAddressTitle.text = WalletSettingsText.allETCAddresses
			chainType.isEOS() -> allAddressTitle.text = WalletSettingsText.allEOSAddresses
			chainType.isBCH() -> allAddressTitle.text = WalletSettingsText.allBCHAddresses
			chainType.isLTC() -> allAddressTitle.text = WalletSettingsText.allLTCAddresses
			chainType.isBTC() -> {
				allAddressTitle.text =
					if (SharedValue.isTestEnvironment()) WalletSettingsText.allBtCTestAddresses
					else WalletSettingsText.allBtcAddresses
			}
		}
	}

	private fun setClickEvent(
		address: String,
		showDashboardEvent: GraySquareCellWithButtons.() -> Unit
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