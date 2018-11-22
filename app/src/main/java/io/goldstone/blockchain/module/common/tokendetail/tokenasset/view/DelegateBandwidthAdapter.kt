package io.goldstone.blockchain.module.common.tokendetail.tokenasset.view

import android.content.Context
import com.blinnnk.base.HoneyBaseAdapter
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.DelegateBandWidthInfo


/**
 * @author KaySaith
 * @date  2018/11/22
 */
class DelegateBandwidthAdapter(
	override val dataSet: ArrayList<DelegateBandWidthInfo>
) : HoneyBaseAdapter<DelegateBandWidthInfo, DelegateBandwidthCell>() {
	override fun generateCell(context: Context) = DelegateBandwidthCell(context)

	override fun DelegateBandwidthCell.bindCell(data: DelegateBandWidthInfo, position: Int) {
		model = data
	}

}