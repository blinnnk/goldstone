package io.goldstone.blockchain.module.home.wallet.walletsettings.chainaddresses.view

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
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.Bip44Address
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView
import org.jetbrains.anko.wrapContent

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
		orientation = VERTICAL
		setPadding(PaddingSize.device, 10.uiPX(), PaddingSize.device, PaddingSize.device)
		gravity = Gravity.CENTER_HORIZONTAL
		layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
		defaultTitle.text = WalletSettingsText.defaultAddress
		currentAddress.into(this)
		allAddressTitle.into(this)
		currentAddress.updateStyle(Companion.CellType.Default)
	}

	fun setDefaultAddress(
		bip44Address: Bip44Address,
		showDashboardEvent: GraySquareCellWithButtons.() -> Unit
	) {
		setClickEvent(bip44Address.address, showDashboardEvent)
		currentAddress.setTitle("${bip44Address.index}")
		val chainType = bip44Address.getChainType()
		val halfSize = if (chainType.isBTC()) 12 else 14
		currentAddress.setSubtitle(CryptoUtils.scaleMiddleAddress(bip44Address.address, halfSize))
		when {
			chainType.isETH() -> allAddressTitle.text = WalletSettingsText.allETHSeriesAddresses
			chainType.isETC() -> allAddressTitle.text = WalletSettingsText.allETCAddresses
			chainType.isEOS() -> allAddressTitle.text = WalletSettingsText.allEOSAddresses
			chainType.isBCH() -> allAddressTitle.text = WalletSettingsText.allBCHAddresses
			chainType.isLTC() -> allAddressTitle.text = WalletSettingsText.allLTCAddresses
			chainType.isBTC() -> allAddressTitle.text = WalletSettingsText.allBtcAddresses
			chainType.isAllTest() -> allAddressTitle.text = WalletSettingsText.allBtCTestAddresses
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