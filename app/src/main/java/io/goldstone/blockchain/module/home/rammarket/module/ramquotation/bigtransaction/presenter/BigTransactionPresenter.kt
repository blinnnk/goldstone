package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.bigtransaction.presenter

import com.blinnnk.extension.isNull
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.home.rammarket.model.RAMTradeRoomData
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.bigtransaction.view.BigTransactionFragment
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.bigtransaction.view.BigTransactionsAdapter
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model.TradingInfoModel
import org.jetbrains.anko.*

/**
 * @date: 2018/11/5.
 * @author: yanglihai
 * @description:
 */
class BigTransactionPresenter(override val fragment: BigTransactionFragment)
	: BaseRecyclerPresenter<BigTransactionFragment, TradingInfoModel>() {
	
	override fun updateData() {
		super.updateData()
		getBigTransactions()
		
	}
	
	private fun getBigTransactions() {
		doAsync {
			GoldStoneAPI.getBigTransactions(1) { data, error ->
				if (!data.isNull() && error.isNone()) {
					RAMTradeRoomData.bigOrderList = data?.toArrayList()
					GoldStoneAPI.context.runOnUiThread {
						fragment.setRecyclerViewAdapter(fragment.recyclerView, RAMTradeRoomData.bigOrderList?: arrayListOf())
					}
				} else {
					GoldStoneAPI.context.runOnUiThread {
						fragment.context?.alert(error.message)
					}
				}
			}
		}
	}
}