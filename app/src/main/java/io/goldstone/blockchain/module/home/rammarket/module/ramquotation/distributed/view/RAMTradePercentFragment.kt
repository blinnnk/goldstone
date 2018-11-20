package io.goldstone.blockchain.module.home.rammarket.module.ramquotation.distributed.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import com.github.mikephil.charting.data.PieEntry
import io.goldstone.blockchain.common.Language.EOSRAMExchangeText
import io.goldstone.blockchain.common.base.gsfragment.GSFragment
import io.goldstone.blockchain.common.component.chart.pie.PieChartView
import io.goldstone.blockchain.common.utils.ErrorDisplayManager
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.home.rammarket.model.RAMMarketPadding
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.distributed.contract.RAMDistributedContract
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.distributed.presenter.RAMTradePercentPresenter
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI

/**
 * @date: 2018/9/25.
 * @author: yanglihai
 * @description:
 */
class RAMTradePercentFragment : GSFragment(), RAMDistributedContract.GSView {
	
	override fun showError(error: Throwable) {
		ErrorDisplayManager(error).show(context)
	}
	
	override val pageTitle: String
		get() = ""
	
	override val presenter: RAMTradePercentPresenter = RAMTradePercentPresenter(this)
	
	private val pieChart by lazy {
		PieChartView(context!!).apply {
			layoutParams = ViewGroup.LayoutParams(matchParent, 250.uiPX())
		}
	}
	
	private val ramPercentChartIn by lazy {
		RAMPercentChartView(context!!)
	}
	
	private val ramPercentChartOut by lazy {
		RAMPercentChartView(context!!)
	}
	
	private val buy by lazy {
		TextView(context!!).apply {
			textColor = Spectrum.green
			textSize = fontSize(15)
			typeface = GoldStoneFont.medium(context)
		}
	}
	
	private val sell by lazy {
		TextView(context!!).apply {
			textColor = Spectrum.red
			textSize = fontSize(15)
			typeface = GoldStoneFont.medium(context)
		}
	}
	
	private val rules by lazy {
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
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return UI {
			initView()
		}.view
	}
	private fun AnkoContext<Fragment>.initView() {
		verticalLayout {
			leftPadding = RAMMarketPadding
			rightPadding = RAMMarketPadding
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
	
	override fun updateChartData(
		maxValue: Float,
		buyValues: Array<Float>,
		buyColors: Array<Int>,
		sellValues: Array<Float>,
		sellColors: Array<Int>
	) {
			ramPercentChartIn.setDataAndColors(
				buyValues,
				buyColors,
				maxValue
			)
			ramPercentChartOut.setDataAndColors(
				sellValues,
				sellColors,
				maxValue
			)
		
		buyValues.let {
			val buyValue = it[0] + it[1] + it[2]
			buy.text = buyValue.toString()
		}
		sellValues.let {
			val saleValue =  it[0] + it[1] + it[2]
			sell.text = saleValue.toString()
		}
	}
	
	override fun updatePieChartData(entries: ArrayList<PieEntry>, colors: List<Int>) {
		pieChart.resetData(entries, colors)
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		presenter.start()
	}
	
	override fun onDestroy() {
		super.onDestroy()
		presenter.onFragmentDestroy()
	}
	
}








