package io.goldstone.blockchain.module.home.quotation.tradermemory.tradermemorydetail.view

import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.view.Gravity
import android.widget.LinearLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.button.RoundButton
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.module.home.quotation.tradermemory.ramtrend.view.EosRamPriceTrendFragment
import io.goldstone.blockchain.module.home.quotation.tradermemory.eosmemorytransactionhistorygeneralview.view.EOSMemoryTransactionHistoryFragment
import io.goldstone.blockchain.module.home.quotation.tradermemory.tradermemorydetail.present.TraderMemoryDetailPresenter
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 18/09/2018 6:36 PM
 * @author wcx
 */
class TraderMemoryDetailFragment : BaseFragment<TraderMemoryDetailPresenter>() {
	override val presenter: TraderMemoryDetailPresenter = TraderMemoryDetailPresenter(this)

	@SuppressLint("ResourceType", "CommitTransaction")
	override fun AnkoContext<Fragment>.initView() {
		relativeLayout {
			scrollView {
				layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
				verticalLayout {
					layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
					gravity = Gravity.CENTER_HORIZONTAL

					textView {
						text = "基础信息"
					}.lparams(matchParent, 300.uiPX())
					frameLayout {
						layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
						id = ElementID.chartView
					}
					addFragmentAndSetArgument<EosRamPriceTrendFragment>(ElementID.chartView) {}
					frameLayout {
						layoutParams = LinearLayout.LayoutParams(matchParent, 200.uiPX()).apply {
							topMargin = 10.uiPX()
							bottomMargin = 10.uiPX()
						}
						id = ElementID.traderMemorySalesRecordAndLargeSingleRecord
					}
					addFragmentAndSetArgument<EOSMemoryTransactionHistoryFragment>(
						ElementID.traderMemorySalesRecordAndLargeSingleRecord
					) {
					}

					textView {
						text = "持量大户"
					}
					textView {
						text = "成交资金分布"
					}
					textView {
						text = "买入/卖出"
					}
				}
			}

			val roundButton = RoundButton(context)
			roundButton.into(this)
			roundButton.apply {
				text = "买入/卖出 RAM"
				y -= 10.uiPX()
				setBlueStyle(20.uiPX(), ScreenSize.widthWithPadding - 40.uiPX())
				setAlignParentBottom()
				setCenterInHorizontal()
				onClick {
					presenter.sendRAM()
				}
			}
		}
	}
}