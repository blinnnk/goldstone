package io.goldstone.blockchain.module.home.wallet.walletdetail.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import com.blinnnk.extension.into
import com.blinnnk.extension.isNull
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.basecell.BaseCell
import io.goldstone.blockchain.common.component.button.BasicRadiusButton
import io.goldstone.blockchain.common.component.button.SquareIcon
import io.goldstone.blockchain.common.component.title.TwoLineTitles
import io.goldstone.blockchain.common.language.EOSAccountText
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.common.utils.isEmptyThen
import io.goldstone.blockchain.crypto.eos.EOSWalletType
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.multichain.isEOSSeries
import io.goldstone.blockchain.crypto.utils.formatCount
import io.goldstone.blockchain.crypto.utils.formatCurrency
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import org.jetbrains.anko.imageResource

@SuppressLint("SetTextI18n")
/**
 * @date 23/03/2018 6:19 PM
 * @author KaySaith
 */
class WalletDetailCell(context: Context) : BaseCell(context) {

	var model: WalletDetailCellModel? by observing(null) {
		model?.apply {
			if (iconUrl.isBlank()) {
				icon.image.imageResource = R.drawable.default_token
			} else {
				when (symbol) {
					CoinSymbol.eth -> icon.image.imageResource = R.drawable.eth_icon
					CoinSymbol.etc -> icon.image.imageResource = R.drawable.etc_icon
					CoinSymbol.ltc -> icon.image.imageResource = R.drawable.ltc_icon
					CoinSymbol.bch -> icon.image.imageResource = R.drawable.bch_icon
					CoinSymbol.eos -> icon.image.imageResource = R.drawable.eos_icon
					CoinSymbol.btc() ->
						icon.image.imageResource =
							if (SharedWallet.getYingYongBaoInReviewStatus()) R.drawable.default_token
							else R.drawable.btc_icon
					else -> icon.image.glideImage("$iconUrl?imageView2/1/w/120/h/120")
				}
			}
			tokenInfo.title.text = CoinSymbol.updateSymbolIfInReview(symbol)
			// 部分 `Token` 没有 `Name` 这里就直接显示 `Symbol`
			tokenInfo.subtitle.text = CoinSymbol.updateNameIfInReview(tokenName isEmptyThen symbol)
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
			setCenterInVertical()
			x += 50.uiPX()
			y += 2.uiPX()
		}

		icon.setCenterInVertical()

		valueInfo.apply {
			setBoldTitles()
			setAlignParentRight()
			setCenterInVertical()
			x -= 30.uiPX()
			isFloatRight = true
			y += 2.uiPX()
		}
	}

	private fun showStatusButton(style: BasicRadiusButton.Companion.Style) {
		valueInfo.visibility = View.GONE
		if (statusButton.isNull()) {
			val title = when (style) {
				BasicRadiusButton.Companion.Style.Pending -> EOSAccountText.pendingActivation
				else -> EOSAccountText.pendingConfirmation
			}
			statusButton = BasicRadiusButton(context)
			statusButton?.setTitle(title)
			statusButton?.into(this)
			statusButton?.setStyle(style)
			statusButton?.setCenterInVertical()
			statusButton?.setAlignParentRight()
			statusButton!!.x -= 30.uiPX()
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