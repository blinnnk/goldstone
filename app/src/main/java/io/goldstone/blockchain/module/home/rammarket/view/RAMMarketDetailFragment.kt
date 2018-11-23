package io.goldstone.blockchain.module.home.rammarket.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.extension.*
import com.github.mikephil.charting.data.CandleEntry
import io.goldstone.blockchain.common.Language.EOSRAMExchangeText
import io.goldstone.blockchain.common.base.gsfragment.GSFragment
import io.goldstone.blockchain.common.component.overlay.TopMiniLoadingView
import io.goldstone.blockchain.common.utils.ErrorDisplayManager
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.crypto.eos.base.showDialog
import io.goldstone.blockchain.crypto.utils.formatCount
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.home.quotation.markettokendetail.model.CandleChartModel
import io.goldstone.blockchain.module.home.rammarket.contract.RAMMarketDetailContract
import io.goldstone.blockchain.module.home.rammarket.module.ramprice.presenter.saveCandleDataToDatabase
import io.goldstone.blockchain.module.home.rammarket.module.ramprice.presenter.updateRAMCandleData
import io.goldstone.blockchain.module.home.rammarket.module.ramprice.view.EOSRAMPriceInfoView
import io.goldstone.blockchain.module.home.rammarket.module.ramprice.view.RAMPriceChartAndMenuView
import io.goldstone.blockchain.module.home.rammarket.module.ramquotation.view.QuotationViewPager
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.model.TradingInfoModel
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.presenter.*
import io.goldstone.blockchain.module.home.rammarket.module.ramtrade.view.TradingView
import io.goldstone.blockchain.module.home.rammarket.presenter.RAMMarketDetailPresenter
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.UI
import java.math.BigDecimal

/**
 * @date: 2018/10/29.
 * @author: yangLiHai
 * @description: price信息，包含蜡烛走势图
 */
class RAMMarketDetailFragment : GSFragment(), RAMMarketDetailContract.GSView {
	
	override val pageTitle: String = EOSRAMExchangeText.ramExchange
	private val ramPriceView by lazy { EOSRAMPriceInfoView(context!!) }
	private val priceMenuCandleChart by lazy {
		RAMPriceChartAndMenuView(context!!) {
			showCandleDataLoadingView()
			presenter.updateRAMCandleData(it)
		}
	}
	private val tradingView by lazy { TradingView(context!!, this) }
	private val quotationViewParent by lazy { QuotationViewPager(this) }
	private val loadingView by lazy {
		RelativeLayout(context).apply {
			visibility = View.GONE
			backgroundColor = Spectrum.opacity5White
			onClick { }
			layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)
			addView(TopMiniLoadingView(context).apply {
				(layoutParams as? RelativeLayout.LayoutParams)?.apply {
					addRule(RelativeLayout.CENTER_VERTICAL)
				}
			})
		}
	}
	override lateinit var presenter: RAMMarketDetailPresenter
	fun AnkoContext<Fragment>.initView() {
		relativeLayout {
			scrollView {
				layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
				verticalLayout {
					layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
					gravity = Gravity.CENTER_HORIZONTAL
					addView(ramPriceView)
					addView(priceMenuCandleChart)
					addView(tradingView)
					addView(quotationViewParent)
					tradingView.tradingDashboardView.ramEditText.apply {
						afterTextChanged = Runnable {
							if (hasFocus() && text.toString().trim().isNotEmpty() && presenter.ramInformationModel.currentPrice != 0.0) {
								val ram = text.toString().trim().toFloat() * presenter.ramInformationModel.currentPrice
								if (ram != 0.0) {
									tradingView.tradingDashboardView.eosEditText.setText(ram.formatCount(3))
								}
							}
						}
					}
					
					tradingView.tradingDashboardView.eosEditText.apply {
						afterTextChanged = Runnable {
							if (hasFocus() && text.toString().trim().isNotEmpty() && presenter.ramInformationModel.currentPrice != 0.0) {
								val eos = text.toString().trim().toFloat() / presenter.ramInformationModel.currentPrice
								if (eos != 0.0) {
									tradingView.tradingDashboardView.ramEditText.setText(eos.formatCount(3))
								}
							}
						}
					}
					
					tradingView.tradingDashboardView.setConfirmEvent(Runnable {
						val amount = if (tradingView.tradingDashboardView.stakeType.isSellRam())
							tradingView.tradingDashboardView.ramEditText.text.toString().trim().toDoubleOrZero() * 1024.0 // kb 转换成byte
						else tradingView.tradingDashboardView.eosEditText.text.toString().trim().toDoubleOrZero()
						if (amount == 0.0) return@Runnable
						loadingView.visibility = View.VISIBLE
						presenter.tradeRAM(
							context,
							amount,
							tradingView.tradingDashboardView.stakeType
						) { eosResponse, error ->
							if (eosResponse.isNotNull() && error.isNone()) {
								presenter.updateAccountData {
									presenter.setAcountInfoFromDatabase()
								}
								GoldStoneAPI.context.runOnUiThread {
									loadingView.visibility = View.GONE
									eosResponse.showDialog(context)
								}
							} else {
								if (!error.isNone()) {
									GoldStoneAPI.context.runOnUiThread {
										loadingView.visibility = View.GONE
										this@RAMMarketDetailFragment.context.alert(error.message)
									}
								}
							}
						}
					})
				}
			}
			
			addView(loadingView)
			
		}
	}
	
	override fun setCurrentPriceAndPercent(price: Double, percent: Double) {
		val formatCount = when {
			price < 10.0 -> 4
			price < 100.0 -> 3
			else -> 2
		}
		tradingView.tradingDashboardView.ramEditText.title = "${EOSRAMExchangeText.ram}(${price.formatCount(formatCount)} EOS/KB)"
		ramPriceView.currentPriceView.currentPrice.text = price.toDouble().formatCount(4)
		ramPriceView.currentPriceView.trendcyPercent.apply {
			if (percent > 0) {
				text = "+${BigDecimal(percent).setScale(2, BigDecimal.ROUND_HALF_UP)}%"
				textColor = Spectrum.green
			} else {
				text = "${BigDecimal(percent).setScale(2, BigDecimal.ROUND_HALF_UP)}%"
				textColor = Spectrum.lightRed
			}
		}
		
	}
	
	override fun setTodayPrice(startPrice: String, highPrice: String, lowPrice: String) {
		ramPriceView.todayPriceView.startPrice.text = EOSRAMExchangeText.openPrice(startPrice)
		ramPriceView.todayPriceView.highPrice.text = EOSRAMExchangeText.highPrice(highPrice)
		ramPriceView.todayPriceView.lowPrice.text = EOSRAMExchangeText.lowPrice(lowPrice)
	}
	
	override fun setSocketDisconnectedPercentColor(color: Int) {
		ramPriceView.currentPriceView.trendcyPercent.textColor = color
	}
	
	override fun updateCandleChartUI(dateType: Int, data: ArrayList<CandleChartModel>) {
		priceMenuCandleChart.removeLoadingView()
		priceMenuCandleChart.candleChart.resetData(dateType, data.mapIndexed { index, entry ->
			CandleEntry(
				index.toFloat(),
				entry.high.toFloat(),
				entry.low.toFloat(),
				entry.open.toFloat(),
				entry.close.toFloat(),
				entry.time)
		})
	
	}
	
	override fun setTradingViewData(buyList: List<TradingInfoModel>, sellList: List<TradingInfoModel>) {
		tradingView.recentTradingListView.setData(buyList, sellList)
	}
	override fun notifyTradingViewData() {
		tradingView.recentTradingListView.adapter?.notifyDataSetChanged()
	}
	
	override fun setRAMBalance(ramBalance: String, eosBalance: String) {
		tradingView.tradingDashboardView.ramBalance.text = EOSRAMExchangeText.ramBalanceDescription(ramBalance)
		tradingView.tradingDashboardView.eosBalance.text = EOSRAMExchangeText.eosBalanceDescription(eosBalance)
	}
	override fun showError(error: Throwable) {
		ErrorDisplayManager(error).show(context)
	}
	private fun showCandleDataLoadingView() {
		priceMenuCandleChart.showLoadingView()
	}
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return UI {
			initView()
		}.view
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		presenter = RAMMarketDetailPresenter(this)
		presenter.start()
	}
	
	override fun onResume() {
		super.onResume()
		presenter.onFragmentResume()
	}
	
	override fun onPause() {
		super.onPause()
		presenter.onFragmentPause()
	}
	
	override fun onDestroy() {
		super.onDestroy()
		presenter.saveCandleDataToDatabase()
	}
	
	override fun onHiddenChanged(hidden: Boolean) {
		super.onHiddenChanged(hidden)
		if (!hidden) {
			getParentFragment<RAMMarketOverlayFragment> {
				showCloseButton(true) {
					presenter.removeSelfFromActivity()
				}
			}
		}
	}
}