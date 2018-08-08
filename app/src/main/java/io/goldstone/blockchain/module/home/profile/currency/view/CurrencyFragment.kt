package io.goldstone.blockchain.module.home.profile.currency.view

import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.preventDuplicateClicks
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.kernel.commonmodel.SupportCurrencyTable
import io.goldstone.blockchain.module.home.profile.currency.presenter.CurrencyPresenter
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 26/03/2018 2:24 PM
 * @author KaySaith
 */
class CurrencyFragment : BaseRecyclerFragment<CurrencyPresenter, SupportCurrencyTable>() {
	
	override val presenter = CurrencyPresenter(this)
	
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<SupportCurrencyTable>?
	) {
		recyclerView.adapter = CurrencyAdapter(asyncData.orEmptyArray()) { item, _ ->
			item.apply {
				onClick {
					presenter.setCurrencyAlert(model.currencySymbol) {
						setSwitchStatusBy(this)
					}
					preventDuplicateClicks()
				}
			}
		}
	}
}