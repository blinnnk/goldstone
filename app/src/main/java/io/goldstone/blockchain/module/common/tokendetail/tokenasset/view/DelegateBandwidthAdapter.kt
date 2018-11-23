package io.goldstone.blockchain.module.common.tokendetail.tokenasset.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import com.blinnnk.extension.preventDuplicateClicks
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.DelegateBandWidthInfo
import org.jetbrains.anko.sdk27.coroutines.onClick


/**
 * @author KaySaith
 * @date  2018/11/22
 */
class DelegateBandwidthAdapter(
	override val dataSet: ArrayList<DelegateBandWidthInfo>,
	private val action: DelegateBandWidthInfo.() -> Unit
) : HoneyBaseAdapter<DelegateBandWidthInfo, DelegateBandwidthCell>() {
	override fun generateCell(context: Context) = DelegateBandwidthCell(context)

	override fun DelegateBandwidthCell.bindCell(data: DelegateBandWidthInfo, position: Int) {
		model = data
		onClick {
			action(data)
			preventDuplicateClicks()
		}
	}

}