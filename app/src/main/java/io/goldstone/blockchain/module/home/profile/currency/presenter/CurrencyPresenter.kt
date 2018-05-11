package io.goldstone.blockchain.module.home.profile.currency.presenter

import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.jump
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.value.Alert
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
		SupportCurrencyTable.getSupportCurrencies {
			fragment.asyncData.isNull() isTrue {
				fragment.asyncData = it
			} otherwise {
				diffAndUpdateSingleCellAdapterData<CurrencyAdapter>(it)
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
				// 杀掉进程
				android.os.Process.killProcess(android.os.Process.myPid())
				System.exit(0)
			}
		}
	}

}