package io.goldstone.blockchain.module.home.profile.currency.presenter

import com.blinnnk.extension.jump
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.value.Alert
import io.goldstone.blockchain.common.value.CountryCode
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.profile.currency.model.CurrencyModel
import io.goldstone.blockchain.module.home.profile.currency.view.CurrencyAdapter
import io.goldstone.blockchain.module.home.profile.currency.view.CurrencyFragment
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.yesButton
import java.util.*

@Suppress("DEPRECATION")
/**
 * @date 26/03/2018 2:24 PM
 * @author KaySaith
 */

class CurrencyPresenter(
	override val fragment: CurrencyFragment
) : BaseRecyclerPresenter<CurrencyFragment, CurrencyModel>() {

	private val isCurrent: (String) -> Boolean = {
		GoldStoneApp.currencyCode == it
	}

	private val getCurrencyByCountry: (String) -> String = {
		Currency.getInstance(Locale(it.toLowerCase(), it)).currencyCode
	}

	override fun updateData() {
		fragment.asyncData = arrayListOf(
			CurrencyModel(
				Currency.getInstance(CountryCode.china).currencyCode,
				isCurrent(Locale.CHINA.country)
			),
			CurrencyModel(
				Currency.getInstance(CountryCode.japan).currencyCode,
				isCurrent(Locale.JAPAN.country)
			),
			CurrencyModel(
				Currency.getInstance(CountryCode.korean).currencyCode,
				isCurrent(Locale.KOREA.country)
			),
			CurrencyModel(
				Currency.getInstance(CountryCode.america).currencyCode,
				isCurrent(CountryCode.america.country)
			),
			CurrencyModel(
				Currency.getInstance(CountryCode.russia).currencyCode,
				isCurrent(CountryCode.russia.country)
			)
		)
	}

	fun setCurrencyAlert(code: String, hold: Boolean.() -> Unit) {
		fragment.context?.apply {
			alert(Alert.selectCurrency) {
				yesButton {
					updateData(code)
					hold(true)
				}
				noButton {
					hold(false)
				}
			}.show()
		}
	}

	private fun updateData(code: String) {
		WalletTable.updateCurrency(code) {
			fragment.activity?.jump<SplashActivity>()
			// 杀掉进程
			android.os.Process.killProcess(android.os.Process.myPid())
			System.exit(0)
		}
	}

	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		getCountryList()
	}

	private fun getCountryList() {
		GoldStoneAPI.getCountryList {
			fragment.apply {
				context?.runOnUiThread {
					diffAndUpdateSingleCellAdapterData<CurrencyAdapter>(it.map {
						CurrencyModel(getCurrencyByCountry(it), isCurrent(getCurrencyByCountry(it)))
					}.toArrayList())
				}
			}
		}
	}
}