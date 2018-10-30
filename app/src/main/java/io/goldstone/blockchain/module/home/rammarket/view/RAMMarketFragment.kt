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
 * @description: 内存交易所的fragment
 */
class RAMMarketFragment : BaseOverlayFragment<RAMMarketPresenter>() {
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
						id = ElementID.ramPriceLayout
					}.lparams(matchParent, wrapContent)
					addFragmentAndSetArgument<RAMPriceDetailFragment>(ElementID.ramPriceLayout) {
					}
					
				}
			}
		}
	}
	
}