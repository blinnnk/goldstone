package io.goldstone.blinnnk.module.home.wallet.walletmanagement.walletlist.view

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import com.blinnnk.extension.preventDuplicateClicks
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.module.home.wallet.walletmanagement.walletlist.model.WalletListModel
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * @date 24/03/2018 8:57 PM
 * @author KaySaith
 */
class WalletListAdapter(
	override val dataSet: ArrayList<WalletListModel>,
	private val clickEvent: (address: String) -> Unit
) : HoneyBaseAdapterWithHeaderAndFooter<WalletListModel, View, WalletListCardCell, View>() {

	override fun generateCell(context: Context) = WalletListCardCell(context)

	override fun generateFooter(context: Context) = View(context)

	override fun generateHeader(context: Context) = View(context).apply {
		layoutParams = ViewGroup.LayoutParams(matchParent, 10.uiPX())
	}

	override fun WalletListCardCell.bindCell(data: WalletListModel, position: Int) {
		model = data
		onClick {
			clickEvent(data.address)
			preventDuplicateClicks()
		}
	}
}