package io.goldstone.blockchain.module.home.dapp.dapplist.presenter

import com.blinnnk.extension.isNotNull
import com.blinnnk.util.load
import com.blinnnk.util.then
import io.goldstone.blockchain.common.value.DataValue
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.home.dapp.dappcenter.model.DAPPTable
import io.goldstone.blockchain.module.home.dapp.dapplist.contract.DAPPListContract
import io.goldstone.blockchain.module.home.dapp.dapplist.model.DAPPType


/**
 * @author KaySaith
 * @date  2018/12/08
 */
class DAPPListPresenter(
	private val view: DAPPListContract.GSView
) : DAPPListContract.GSPresenter {
	override fun start() {

	}

	override fun getData(type: DAPPType, hold: (List<DAPPTable>) -> Unit) {
		load {
			when (type) {
				DAPPType.New -> DAPPTable.dao.getAll(DataValue.dappPageCount)
				// 本地的数据加载全部
				DAPPType.Latest -> {
					DAPPTable.dao.getUsed(20)
				}
				DAPPType.Recommend -> DAPPTable.dao.getRecommended(DataValue.dappPageCount)
				else -> throw Throwable("Wrong DAPP Type")
			}
		} then (hold)
	}

	override fun loadMore(
		pageIndex: Int,
		dataType: DAPPType,
		hold: (List<DAPPTable>) -> Unit
	) {
		when (dataType) {
			DAPPType.Recommend -> GoldStoneAPI.getRecommendDAPPs(pageIndex) { data, error ->
				if (data.isNotNull() && error.isNone()) {
					hold(data)
				} else view.showError(error)
			}
			DAPPType.New -> GoldStoneAPI.getNewDAPPs(pageIndex) { data, error ->
				if (data.isNotNull() && error.isNone()) {
					hold(data)
				} else view.showError(error)
			}
			else -> hold(listOf())
		}
	}
}