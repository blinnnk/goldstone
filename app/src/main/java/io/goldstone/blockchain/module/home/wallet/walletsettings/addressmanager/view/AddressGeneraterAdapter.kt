package io.goldstone.blockchain.module.home.wallet.walletsettings.addressmanager.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import com.blinnnk.extension.preventDuplicateClicks
import org.jetbrains.anko.sdk27.coroutines.onClick


/**
 * @author KaySaith
 * @date  2018/11/18
 */
class AddressGeneratorAdapter(
	override val dataSet: ArrayList<Pair<Int, String>>,
	private val clickEvent: (Pair<Int, String>) -> Unit
) : HoneyBaseAdapter<Pair<Int, String>, AddressGeneratorCell>() {
	override fun generateCell(context: Context) = AddressGeneratorCell(context)

	override fun AddressGeneratorCell.bindCell(data: Pair<Int, String>, position: Int) {
		model = data
		onClick {
			clickEvent(data)
			preventDuplicateClicks()
		}
	}

}