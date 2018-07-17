package io.goldstone.blockchain.module.home.wallet.walletsettings.allsinglechainaddresses.view

import android.content.Context
import android.view.View
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter

/**
 * @date 2018/7/16 8:27 PM
 * @author KaySaith
 */
class ChainAddressesAdapter(
	override val dataSet: ArrayList<Pair<String, String>>,
	private val hold: ChainAddressesCell.() -> Unit
) : HoneyBaseAdapterWithHeaderAndFooter<Pair<String, String>, ChainAddressesHeaderView, ChainAddressesCell, View>() {
	
	override fun generateFooter(context: Context) = View(context)
	override fun generateHeader(context: Context) = ChainAddressesHeaderView(context)
	override fun generateCell(context: Context) = ChainAddressesCell(context)
	override fun ChainAddressesCell.bindCell(data: Pair<String, String>, position: Int) {
		model = data
		hold(this)
	}
}