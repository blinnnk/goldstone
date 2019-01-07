package io.goldstone.blinnnk.module.home.wallet.walletdetail.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import com.blinnnk.extension.alignParentRight
import com.blinnnk.extension.centerInVertical
import com.blinnnk.extension.into
import com.blinnnk.extension.isNull
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blinnnk.R
import io.goldstone.blinnnk.common.base.basecell.BaseCell
import io.goldstone.blinnnk.common.component.button.BasicRadiusButton
import io.goldstone.blinnnk.common.component.button.SquareIcon
import io.goldstone.blinnnk.common.component.title.TwoLineTitles
import io.goldstone.blinnnk.common.language.EOSAccountText
import io.goldstone.blinnnk.common.sharedpreference.SharedWallet
import io.goldstone.blinnnk.common.utils.glideImage
import io.goldstone.blinnnk.common.utils.isEmptyThen
import io.goldstone.blinnnk.crypto.eos.EOSWalletType
import io.goldstone.blinnnk.crypto.multichain.*
import io.goldstone.blinnnk.crypto.utils.formatCount
import io.goldstone.blinnnk.crypto.utils.formatCurrency
import io.goldstone.blinnnk.module.home.wallet.walletdetail.model.WalletDetailCellModel
import org.jetbrains.anko.imageResource

@SuppressLint("SetTextI18n")
/**
 * @date 23/03/2018 6:19 PM
 * @author KaySaith
 */
class WalletDetailCell(context: Context) : BaseCell(context) {

	var model: WalletDetailCellModel? by observing(null) {
		model?.apply {
			if (iconUrl.isBlank()) icon.image.imageResource = R.drawable.default_token
			else when {
				symbol.isETH() -> icon.image.imageResource = R.drawable.eth_icon
				symbol.isETC() -> icon.image.imageResource = R.drawable.etc_icon
				symbol.isLTC() -> icon.image.imageResource = R.drawable.ltc_icon
				symbol.isBCH() -> icon.image.imageResource = R.drawable.bch_icon
				symbol.isEOS() -> icon.image.imageResource = R.drawable.eos_icon
				symbol.isBTC() ->
					icon.image.imageResource = R.drawable.btc_icon
				else -> icon.image.glideImage("$iconUrl?imageView2/1/w/120/h/120")
			}
			tokenInfo.title.text = symbol.symbol
			// 部分 `Token` 没有 `Name` 这里就直接显示 `Symbol`
			tokenInfo.subtitle.text = tokenName isEmptyThen symbol.symbol
			if (contract.isEOSSeries() && eosWalletType != EOSWalletType.Available) {
				if (eosWalletType == EOSWalletType.Inactivated)
					showStatusButton(BasicRadiusButton.Companion.Style.Pending)
				else if (eosWalletType == EOSWalletType.NoDefault)
					showStatusButton(BasicRadiusButton.Companion.Style.ToBeSet)
			} else {
				clearStatusButton()
				valueInfo.title.text = count.formatCount()
				val money = if (currency == 0.0) count * price else currency
				valueInfo.subtitle.text = "≈ " + money.formatCurrency() + " (${SharedWallet.getCurrencyCode()})"
			}
		}
	}
	private val icon by lazy { SquareIcon(context, SquareIcon.Companion.Style.Big) }
	private val tokenInfo by lazy { TwoLineTitles(context) }
	private val valueInfo by lazy { TwoLineTitles(context) }
	private var statusButton: BasicRadiusButton? = null

	init {
		icon.into(this)
		tokenInfo.into(this)
		valueInfo.into(this)
		setHorizontalPadding()
		tokenInfo.apply {
			setBoldTitles()
			centerInVertical()
			x += 50.uiPX()
			y += 2.uiPX()
		}

		icon.centerInVertical()

		valueInfo.apply {
			setBoldTitles()
			alignParentRight()
			centerInVertical()
			x -= 30.uiPX()
			isFloatRight = true
			y += 2.uiPX()
		}
	}

	private fun showStatusButton(style: BasicRadiusButton.Companion.Style) {
		valueInfo.visibility = View.GONE
		if (statusButton == null) {
			val title = when (style) {
				BasicRadiusButton.Companion.Style.Pending -> EOSAccountText.pendingActivation
				else -> EOSAccountText.pendingConfirmation
			}
			statusButton = BasicRadiusButton(context)
			statusButton?.into(this)
			statusButton?.apply {
				setTitle(title)
				setStyle(style)
				centerInVertical()
				alignParentRight()
				x -= 30.uiPX()
			}
		}
	}

	private fun clearStatusButton() {
		if (!statusButton.isNull()) {
			removeView(statusButton)
			valueInfo.visibility = View.VISIBLE
			statusButton = null
		}
	}
}