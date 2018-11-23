package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.distributed.contract

import com.github.mikephil.charting.data.PieEntry
import io.goldstone.blockchain.module.common.contract.GoldStonePresenter
import io.goldstone.blockchain.module.common.contract.GoldStoneView

/**
 * @date: 2018-11-20.
 * @author: yangLiHai
 * @description:
 */
interface RAMDistributedContract {
	interface GSView: GoldStoneView<GSPresenter> {
		fun updateChartData(maxValue: Float,
			buyValues: Array<Float>,
			buyColors: Array<Int>,
			sellValues: Array<Float>,
			sellColors: Array<Int>
		)
		fun updatePieChartData(entries: ArrayList<PieEntry>, colors: List<Int>)
	}
	interface GSPresenter: GoldStonePresenter {
		fun getTradeData()
	}
}