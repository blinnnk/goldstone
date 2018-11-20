package io.goldstone.blockchain.module.home.profile.currency.presenter

import android.support.annotation.UiThread
import com.blinnnk.extension.isNull
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.commonmodel.SupportCurrencyTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.module.home.profile.currency.view.CurrencyAdapter
import io.goldstone.blockchain.module.home.profile.currency.view.CurrencyFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast

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

	fun updateCurrency(symbol: String) {
		GlobalScope.launch(Dispatchers.Default) {
			val currencyDao = GoldStoneDataBase.database.currencyDao()
			currencyDao.setCurrentCurrencyUnused()
			currencyDao.setCurrencyInUse(symbol)
			val rate = currencyDao.getCurrencyBySymbol(symbol)?.rate
			AppConfigTable.dao.updateCurrency(symbol)
			rate?.let { SharedWallet.updateCurrentRate(it) }
			SharedWallet.updateCurrencyCode(symbol)
			launchUI {
				fragment.recyclerView.adapter?.notifyDataSetChanged()
				fragment.context?.toast(CommonText.succeed)
			}
		}
	}
}