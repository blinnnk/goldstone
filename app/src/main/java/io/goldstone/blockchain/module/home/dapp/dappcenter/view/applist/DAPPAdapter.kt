package io.goldstone.blockchain.module.home.dapp.dappcenter.view.applist

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import com.blinnnk.extension.preventDuplicateClicks
import io.goldstone.blockchain.module.home.dapp.dappcenter.model.DAPPModel
import org.jetbrains.anko.sdk27.coroutines.onClick


/**
 * @author KaySaith
 * @date  2018/12/02
 */
class DAPPAdapter(
	override val dataSet: ArrayList<DAPPModel>,
	private val hold: DAPPModel.() -> Unit
) : HoneyBaseAdapter<DAPPModel, DAPPCell>() {
	override fun generateCell(context: Context) = DAPPCell(context)

	override fun DAPPCell.bindCell(data: DAPPModel, position: Int) {
		model = data
		onClick {
			hold(data)
			preventDuplicateClicks()
		}
	}

}