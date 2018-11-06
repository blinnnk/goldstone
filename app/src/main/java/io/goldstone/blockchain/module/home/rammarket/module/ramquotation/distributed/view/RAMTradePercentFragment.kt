package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.distributed.view

import android.support.v4.app.Fragment
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.Language.EOSRAMExchangeText
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.chart.pie.PieChartView
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.distributed.presenter.RAMTradePercentPresenter
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
				250.uiPX()
			)
		}
	}
	
	val ramPercentChartIn by lazy {
		RAMPercentChartView(context!!)
	}
	
	val ramPercentChartOut by lazy {
		RAMPercentChartView(context!!)
	}
	
	val buy by lazy {
		TextView(context!!).apply {
			textColor = Spectrum.green
			textSize = fontSize(15)
		}
	}
	
	val sell by lazy {
		TextView(context!!).apply {
			textColor = Spectrum.red
			textSize = fontSize(15)
		}
	}
	
	val rules by lazy {
		TextView(context!!).apply {
			textColor = GrayScale.midGray
			textSize = fontSize(12)
			typeface = GoldStoneFont.book(context)
			text = EOSRAMExchangeText.ramOrderRules
			layoutParams = LinearLayout.LayoutParams(wrapContent, wrapContent).apply {
				gravity = Gravity.CENTER_HORIZONTAL
			}
			setMargins<LinearLayout.LayoutParams> {
				topMargin = 16.uiPX()
				bottomMargin = 30.uiPX()
			}
		}
	}
	
	override fun AnkoContext<Fragment>.initView() {
		verticalLayout {
			
			addView(rules)
			
			addView(pieChart)
			
			linearLayout {
				verticalLayout {
					addView(ramPercentChartIn)
					linearLayout {
						layoutParams = LinearLayout.LayoutParams(wrapContent, wrapContent).apply {
							setMargins<LinearLayout.LayoutParams> {
								topMargin = 10.uiPX()
								bottomMargin = 10.uiPX()
							}
							gravity = Gravity.CENTER_HORIZONTAL
						}
						textView {
							textColor = GrayScale.black
							textSize = fontSize(12)
							typeface = GoldStoneFont.book(context)
							text = EOSRAMExchangeText.buy("    ")
						}
						addView(buy)
					}
				}
				verticalLayout {
					addView(ramPercentChartOut)
					linearLayout {
						layoutParams = LinearLayout.LayoutParams(wrapContent, wrapContent).apply {
							setMargins<LinearLayout.LayoutParams> {
								topMargin = 10.uiPX()
								bottomMargin = 10.uiPX()
							}
							gravity = Gravity.CENTER_HORIZONTAL
						}
						textView {
							textColor = GrayScale.black
							textSize = fontSize(12)
							typeface = GoldStoneFont.book(context)
							text = EOSRAMExchangeText.sell("    ")
						}
						addView(sell)
					}
				}
				
			}
			addView(View(context).apply {
				layoutParams = LinearLayout.LayoutParams(matchParent, 100.uiPX())
			})
		}
	}
	
}








