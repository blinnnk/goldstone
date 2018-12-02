package io.goldstone.blockchain.module.home.dapp.dappcenter.view.recommend

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import com.blinnnk.extension.preventDuplicateClicks
import io.goldstone.blockchain.module.home.dapp.dappcenter.model.DAPPModel
import org.jetbrains.anko.sdk27.coroutines.onClick


/**
 * @author KaySaith
 * @date  2018/12/01
 */
class RecommendDAPPAdapter(
	override val dataSet: ArrayList<DAPPModel>,
	private val hold: DAPPModel.() -> Unit
) : HoneyBaseAdapter<DAPPModel, RecommendDAPPCell>() {

	override fun generateCell(context: Context) = RecommendDAPPCell(context)

	override fun RecommendDAPPCell.bindCell(data: DAPPModel, position: Int) {
		model = data
		onClick {
			hold(data)
			preventDuplicateClicks()
		}
	}
}