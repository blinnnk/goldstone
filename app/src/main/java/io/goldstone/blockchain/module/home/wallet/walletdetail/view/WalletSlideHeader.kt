package io.goldstone.blockchain.module.home.wallet.walletdetail.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.SliderHeader
import io.goldstone.blockchain.common.component.button.CircleButton
import io.goldstone.blockchain.common.component.title.TwoLineTitles
import io.goldstone.blockchain.common.language.ChainText
import io.goldstone.blockchain.common.language.WalletText
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.crypto.utils.formatCurrency

/**
 * @date 24/03/2018 12:50 AM
 * @author KaySaith
 */
@SuppressLint("SetTextI18n")
class WalletSlideHeader(context: Context) : SliderHeader(context) {

	val notifyButton = CircleButton(context)
	val searchButton = CircleButton(context)
	private val balance = TwoLineTitles(context)

	init {
		notifyButton.apply {
			title = WalletText.notifyButton
			src = R.drawable.notifications_icon
			x += PaddingSize.device
			y = 18.uiPX().toFloat()
		}.into(this)
		notifyButton.apply {
			centerInVertical()
		}

		searchButton.apply {
			title = WalletText.addToken
			src = R.drawable.manage_token_icon
			x -= PaddingSize.device
			y = 18.uiPX().toFloat()
		}.into(this)
		searchButton.apply {
			centerInVertical()
			alignParentRight()
		}
		balance.apply {
			setBigWhiteStyle(18)
			title.y += 2.uiPX()
			isCenter = true
			visibility = View.GONE
			y = 5.uiPX().toFloat()
		}.into(this)
	}

	override fun onHeaderShowedStyle() {
		super.onHeaderShowedStyle()
		notifyButton.setUnTransparent()
		searchButton.setUnTransparent()
		balance.apply {
			centerInParent()
			visibility = View.VISIBLE
		}

		setBalanceValue(SharedWallet.getCurrentBalance().formatCurrency())
	}

	override fun onHeaderHidesStyle() {
		super.onHeaderHidesStyle()
		notifyButton.setDefaultStyle()
		searchButton.setDefaultStyle()
		balance.visibility = View.GONE
	}

	private fun setBalanceValue(value: String) {
		balance.title.text = value
		balance.subtitle.text = setBalanceInfo()
	}

	companion object {
		fun setBalanceInfo(): String {
			val watchOnlyPrefix = if (SharedWallet.isWatchOnlyWallet()) WalletText.watchOnly + " · " else ""
			return watchOnlyPrefix + if (!SharedValue.isTestEnvironment()) {
				WalletText.totalAssets suffix  "(${SharedWallet.getCurrencyCode()})"
			} else {
				ChainText.testnet + " · " + WalletText.totalAssets + " (" + SharedWallet.getCurrencyCode() + ")"
			}
		}
	}
}