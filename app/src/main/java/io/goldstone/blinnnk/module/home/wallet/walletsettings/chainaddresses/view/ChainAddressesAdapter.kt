package io.goldstone.blinnnk.module.home.wallet.walletsettings.chainaddresses.view

import android.content.Context
import android.view.View
import com.blinnnk.base.HoneyBaseAdapterWithHeaderAndFooter
import io.goldstone.blinnnk.module.common.walletgeneration.createwallet.model.Bip44Address

/**
 * @date 2018/7/16 8:27 PM
 * @author KaySaith
 */
class ChainAddressesAdapter(
	override val dataSet: ArrayList<Bip44Address>,
	private val holdHeader: ChainAddressesHeaderView.() -> Unit,
	private val hold: ChainAddressesCell.() -> Unit
) : HoneyBaseAdapterWithHeaderAndFooter<Bip44Address, ChainAddressesHeaderView, ChainAddressesCell, View>() {
	
	override fun generateFooter(context: Context) = View(context)
	override fun generateHeader(context: Context) = ChainAddressesHeaderView(context).apply(holdHeader)
	override fun generateCell(context: Context) = ChainAddressesCell(context)
	override fun ChainAddressesCell.bindCell(data: Bip44Address, position: Int) {
		model = data
		hold(this)
	}
}