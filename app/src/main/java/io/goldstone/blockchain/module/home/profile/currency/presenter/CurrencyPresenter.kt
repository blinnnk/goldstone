package io.goldstone.blockchain.module.home.profile.currency.presenter

import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.language.Alert
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.commonmodel.SupportCurrencyTable
import io.goldstone.blockchain.module.home.profile.currency.view.CurrencyAdapter
import io.goldstone.blockchain.module.home.profile.currency.view.CurrencyFragment
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

@Suppress("DEPRECATION")
/**
 * @date 26/03/2018 2:24 PM
 * @author KaySaith
 */
class CurrencyPresenter(
	override val fragment: CurrencyFragment
) : BaseRecyclerPresenter<CurrencyFragment, SupportCurrencyTable>() {

	override fun updateData() {
		SupportCurrencyTable.getSupportCurrencies { currencies ->
			fragment.asyncData.isNull() isTrue {
				fragment.asyncData = currencies.distinctBy { it.currencySymbol }.toArrayList()
			} otherwise {
				diffAndUpdateSingleCellAdapterData<CurrencyAdapter>(currencies)
			}
		}
	}

	fun setCurrencyAlert(currencySymbol: String, hold: Boolean.() -> Unit) {
		fragment.context?.apply {
			alert(Alert.selectCurrency) {
				yesButton {
					updateCurrencyValue(currencySymbol)
					hold(true)
				}
				noButton {
					hold(false)
				}
			}.show()
		}
	}

	private fun updateCurrencyValue(currencySymbol: String) {
		SupportCurrencyTable.updateUsedStatus(currencySymbol) { rate ->
			AppConfigTable.updateCurrency(currencySymbol) {
				rate?.let { SharedWallet.updateCurrentRate(it) }
				SharedWallet.updateCurrencyCode(currencySymbol)
				fragment.recyclerView.adapter?.notifyDataSetChanged()
				fragment.context?.toast(CommonText.succeed)
			}
		}
	}
}