package io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.view

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.module.home.wallet.walletmanagement.walletlist.model.WalletListModel
import org.jetbrains.anko.matchParent

/**
 * @date 24/03/2018 8:57 PM
 * @author KaySaith
 */
class WalletListAdapter(
	override val dataSet: ArrayList<WalletListModel>,
	private val callback: WalletListCardCell.() -> Unit
) : HoneyBaseAdapterWithHeaderAndFooter<WalletListModel, View, WalletListCardCell, View>() {
	
	override fun generateCell(context: Context) = WalletListCardCell(context)
	
	override fun generateFooter(context: Context) = View(context)
	
	override fun generateHeader(context: Context) = View(context).apply {
		layoutParams = ViewGroup.LayoutParams(matchParent, 20.uiPX())
	}
	
	override fun WalletListCardCell.bindCell(data: WalletListModel, position: Int) {
		model = data
		callback(this)
	}
}