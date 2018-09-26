package io.goldstone.blockchain.module.home.quotation.tradermemory.tradepercent.view

import android.support.v4.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.chart.pie.PieChart
import io.goldstone.blockchain.common.component.chart.pie.PieChartView
import io.goldstone.blockchain.module.home.quotation.tradermemory.tradepercent.presenter.RAMTradePercentPresenter
import org.jetbrains.anko.*

/**
 * @date: 2018/9/25.
 * @author: yanglihai
 * @description:
 */
class RAMTradePercentFragment : BaseFragment<RAMTradePercentPresenter>() {
	
	override val presenter: RAMTradePercentPresenter = RAMTradePercentPresenter(this)
	
	val pieChart by lazy {
		PieChartView(context!!).apply {
			layoutParams = ViewGroup.LayoutParams(matchParent, 200.uiPX())
		}
	}
	
	val ramPercentChartIn by lazy {
		RAMPercentChartView(context!!)
	}
	
	val ramPercentChartOut by lazy {
		RAMPercentChartView(context!!)
	}
	
	override fun AnkoContext<Fragment>.initView() {
		verticalLayout {
			addView(pieChart)
			
			linearLayout {
				addView(ramPercentChartIn)
				addView(ramPercentChartOut)
				addView(View(context).apply {
					layoutParams = LinearLayout.LayoutParams(matchParent,200.uiPX())
				})
			}
		}
	}
	
}