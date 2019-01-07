package io.goldstone.blinnnk.module.home.wallet.walletdetail.view

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.module.home.wallet.walletdetail.model.WalletDetailCellModel
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
			layoutParams = LinearLayout.LayoutParams(matchParent, 60.uiPX())
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