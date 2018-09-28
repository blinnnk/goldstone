package io.goldstone.blockchain.module.home.quotation.tradermemory.tradepercent.view

import android.support.v4.app.Fragment
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.Language.EOSRAMText
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.chart.pie.PieChart
import io.goldstone.blockchain.common.component.chart.pie.PieChartView
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.home.quotation.tradermemory.tradepercent.presenter.RAMTradePercentPresenter
import org.jetbrains.anko.*

/**
 * @date: 2018/9/25.
 * @author: yanglihai
 * @description:
 */
class RAMTradePercentFragment : BaseFragment<RAMTradePercentPresenter>() {
	override val pageTitle: String
		get() = ""
	
	override val presenter: RAMTradePercentPresenter = RAMTradePercentPresenter(this)
	
	val pieChart by lazy {
		PieChartView(context!!).apply {
			layoutParams = ViewGroup.LayoutParams(
				matchParent,
				200.uiPX()
			)
		}
	}
	
	val ramPercentChartIn by lazy {
		RAMPercentChartView(context!!)
	}
	
	val ramPercentChartOut by lazy {
		RAMPercentChartView(context!!)
	}
	
	val buying by lazy {
		TextView(context!!).apply {
			textColor = Spectrum.blue
			textSize = fontSize(15)
			layoutParams = LinearLayout.LayoutParams(wrapContent, wrapContent).apply {
				setMargins<LinearLayout.LayoutParams> {
					topMargin = 10.uiPX()
					bottomMargin = 10.uiPX()
				}
				gravity = Gravity.CENTER_HORIZONTAL
			}
		}
	}
	
	val saling by lazy {
		TextView(context!!).apply {
			textColor = Spectrum.red
			textSize = fontSize(15)
			layoutParams = LinearLayout.LayoutParams(wrapContent, wrapContent).apply {
				setMargins<LinearLayout.LayoutParams> {
					topMargin = 10.uiPX()
					bottomMargin = 10.uiPX()
				}
				gravity = Gravity.CENTER_HORIZONTAL
			}
		}
	}
	
	val rules by lazy {
		TextView(context!!).apply {
			textColor = GrayScale.gray
			textSize = fontSize(12)
			text = EOSRAMText.ramOrderRules
			layoutParams = LinearLayout.LayoutParams(wrapContent, wrapContent).apply {
				setMargins<LinearLayout.LayoutParams> {
					topMargin = 10.uiPX()
					bottomMargin = 10.uiPX()
				}
				gravity = Gravity.CENTER_HORIZONTAL
			}
		}
	}
	
	override fun AnkoContext<Fragment>.initView() {
		verticalLayout {
			addView(pieChart)
			
			linearLayout {
				verticalLayout {
					addView(ramPercentChartIn)
					addView(buying)
				}
				verticalLayout {
					addView(ramPercentChartOut)
					addView(saling)
				}
				
			}
			
			addView(rules)
			
			addView(View(context).apply {
				layoutParams = LinearLayout.LayoutParams(
					matchParent,
					100.uiPX()
				)
			})
		}
	}
	
}







