package io.goldstone.blinnnk.module.home.profile.currency.view

import com.blinnnk.extension.orEmptyArray
import io.goldstone.blinnnk.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blinnnk.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blinnnk.common.language.CommonText
import io.goldstone.blinnnk.common.language.ProfileText
import io.goldstone.blinnnk.common.sandbox.SandBoxManager
import io.goldstone.blinnnk.common.utils.getMainActivity
import io.goldstone.blinnnk.kernel.commontable.SupportCurrencyTable
import io.goldstone.blinnnk.module.home.profile.currency.presenter.CurrencyPresenter
import org.jetbrains.anko.toast

/**
 * @date 26/03/2018 2:24 PM
 * @author KaySaith
 */
class CurrencyFragment : BaseRecyclerFragment<CurrencyPresenter, SupportCurrencyTable>() {

	override val presenter = CurrencyPresenter(this)

	private var currentSymbol: String? = null
	override val pageTitle: String = ProfileText.currency
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<SupportCurrencyTable>?
	) {
		recyclerView.adapter = CurrencyAdapter(asyncData.orEmptyArray()) {
			// 更新内存的数据状态
			asyncData?.forEach { data ->
				if (data.isUsed) data.isUsed = false
				if (data.currencySymbol.equals(currencySymbol, true))
					data.isUsed = true
			}
			currentSymbol = currencySymbol
			recyclerView.adapter?.notifyDataSetChanged()
			context?.toast(CommonText.succeed)
			presenter.updateCurrency(currencySymbol)
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		currentSymbol?.let {
			getMainActivity()?.getProfileFragment()?.presenter?.updateData()
			SandBoxManager.updateCurrency(it)
		}
	}
}