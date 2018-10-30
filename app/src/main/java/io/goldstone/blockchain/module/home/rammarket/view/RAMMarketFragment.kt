package io.goldstone.blockchain.module.home.rammarket.view

import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.Language.EOSRAMText
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.module.home.rammarket.presenter.RAMMarketPresenter
import io.goldstone.blockchain.module.home.rammarket.ramprice.view.RAMPriceDetailFragment
import org.jetbrains.anko.*

/**
 * @date: 2018/10/29.
 * @author: yanglihai
 * @description:
 */
class RAMMarketFragment : BaseOverlayFragment<RAMMarketPresenter>() {
//	override val pageTitle: String =  EOSRAMText.ramTradeRoom
	override val presenter: RAMMarketPresenter = RAMMarketPresenter(this)
	
	override fun ViewGroup.initView() {
		headerTitle = EOSRAMText.ramTradeRoom
		relativeLayout {
			scrollView {
				layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
				verticalLayout {
					layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
					gravity = Gravity.CENTER_HORIZONTAL
					
					frameLayout {
						id = ElementID.chartView
					}.lparams(matchParent, wrapContent)
					addFragmentAndSetArgument<RAMPriceDetailFragment>(ElementID.chartView) {
					}
					
					frameLayout {
						id = ElementID.rankList
					}.lparams(matchParent, 50.uiPX() * 11)
//					addFragmentAndSetArgument<RankFragment>(ElementID.rankList) {}
					
					// 内存交易记录列表
					frameLayout {
						layoutParams = LinearLayout.LayoutParams(matchParent, 400.uiPX()).apply {
							topMargin = 10.uiPX()
							bottomMargin = 10.uiPX()
						}
						id = ElementID.eosMemoryTransactionHistoryList
					}
//					addFragmentAndSetArgument<EOSMemoryTransactionHistoryFragment>(
//						ElementID.eosMemoryTransactionHistoryList
//					) {
//					}
					
					frameLayout {
						id = ElementID.pieChart
					}
//					addFragmentAndSetArgument<RAMTradePercentFragment>(ElementID.pieChart) {}
					
				}
			}
		}
	}
	
}