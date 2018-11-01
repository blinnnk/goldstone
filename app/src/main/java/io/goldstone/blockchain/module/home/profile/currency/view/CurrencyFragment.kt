package io.goldstone.blockchain.module.home.profile.currency.view

import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.preventDuplicateClicks
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.language.ProfileText
import io.goldstone.blockchain.kernel.commonmodel.SupportCurrencyTable
import io.goldstone.blockchain.module.home.profile.currency.presenter.CurrencyPresenter
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * @date 26/03/2018 2:24 PM
 * @author KaySaith
 */
class CurrencyFragment : BaseRecyclerFragment<CurrencyPresenter, SupportCurrencyTable>() {

	override val presenter = CurrencyPresenter(this)

	override val pageTitle: String = ProfileText.currency
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<SupportCurrencyTable>?
	) {
		recyclerView.adapter = CurrencyAdapter(asyncData.orEmptyArray()) { item, _ ->
			item.apply {
				onClick {
					// 更新内存的数据状态
					asyncData?.forEach { data ->
						if (data.isUsed) data.isUsed = false
						if (data.currencySymbol.equals(model.currencySymbol, true))
							data.isUsed = true
					}
					presenter.setCurrencyAlert(model.currencySymbol) {
						setSwitchStatusBy(this)
					}
					preventDuplicateClicks()
				}
			}
		}
	}
}