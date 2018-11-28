package io.goldstone.blockchain.module.home.wallet.walletdetail.view

import android.content.Context
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.View
import android.widget.LinearLayout
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * @date 23/03/2018 4:16 PM
 * @author KaySaith
 */
class WalletDetailAdapter(
	override var dataSet: ArrayList<WalletDetailCellModel>,
	private val clickCellEvent: (WalletDetailCellModel) -> Unit,
	private val holdHeader: WalletDetailHeaderView.() -> Unit
) :
	HoneyBaseAdapterWithHeaderAndFooter<WalletDetailCellModel, WalletDetailHeaderView, WalletDetailCell, View>() {

	override fun generateCell(context: Context) = WalletDetailCell(context)

	override fun generateFooter(context: Context): View {
		return View(context).apply {
			val barHeight =
				if ((!KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)) || SharedWallet.isNotchScreen()) {
					60.uiPX()
				} else 10.uiPX()
			layoutParams = LinearLayout.LayoutParams(matchParent, barHeight)
		}
	}

	override fun generateHeader(context: Context) =
		WalletDetailHeaderView(context).apply {
			holdHeader(this)
		}

	override fun WalletDetailCell.bindCell(data: WalletDetailCellModel, position: Int) {
		model = data
		onClick {
			clickCellEvent(data)
			preventDuplicateClicks()
		}
	}
}