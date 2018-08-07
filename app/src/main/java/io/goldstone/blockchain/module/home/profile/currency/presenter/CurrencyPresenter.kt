package io.goldstone.blockchain.module.home.profile.currency.presenter

import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.language.Alert
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.commonmodel.SupportCurrencyTable
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.profile.currency.view.CurrencyAdapter
import io.goldstone.blockchain.module.home.profile.currency.view.CurrencyFragment
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
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

	fun setCurrencyAlert(
		code: String,
		hold: Boolean.() -> Unit
	) {
		fragment.context?.apply {
			alert(Alert.selectCurrency) {
				yesButton {
					updateCurrencyValue(code)
					hold(true)
				}
				noButton {
					hold(false)
				}
			}.show()
		}
	}

	private fun updateCurrencyValue(code: String) {
		SupportCurrencyTable.updateUsedStatus(code) {
			AppConfigTable.updateCurrency(code) {
				fragment.activity?.jump<SplashActivity>()
			}
		}
	}
}