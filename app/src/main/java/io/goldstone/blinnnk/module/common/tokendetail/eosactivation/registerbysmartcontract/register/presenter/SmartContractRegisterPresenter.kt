package io.goldstone.blinnnk.module.common.tokendetail.eosactivation.registerbysmartcontract.register.presenter

import android.os.Bundle
import com.blinnnk.extension.getParentFragment
import com.blinnnk.util.getParentFragment
import io.goldstone.blinnnk.common.base.basefragment.BasePresenter
import io.goldstone.blinnnk.common.error.RequestError
import io.goldstone.blinnnk.common.value.ArgumentKey
import io.goldstone.blinnnk.crypto.multichain.TokenContract
import io.goldstone.blinnnk.kernel.network.common.GoldStoneAPI
import io.goldstone.blinnnk.module.common.tokendetail.eosactivation.registerbysmartcontract.detail.view.SmartContractRegisterDetailFragment
import io.goldstone.blinnnk.module.common.tokendetail.eosactivation.registerbysmartcontract.register.view.SmartContractRegisterFragment
import io.goldstone.blinnnk.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment


/**
 * @author KaySaith
 * @date  2018/09/25
 */
class SmartContractRegisterPresenter(
	override val fragment: SmartContractRegisterFragment
) : BasePresenter<SmartContractRegisterFragment>() {

	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		fragment.getParentFragment<TokenDetailOverlayFragment> {
			showBackButton(true) {
				presenter.popFragmentFrom<SmartContractRegisterFragment>()
			}
		}
	}

	fun showSmartContractRegisterDetailFragment(accountName: String) {
		fragment.getParentFragment<TokenDetailOverlayFragment>()
			?.presenter?.showTargetFragment<SmartContractRegisterDetailFragment>(
			Bundle().apply { putString(ArgumentKey.eosAccountRegister, accountName) }
		)
	}

	fun getEOSCurrencyPrice(hold: (currency: Double?, error: RequestError) -> Unit) {
		GoldStoneAPI.getPriceByContractAddress(
			listOf("{\"address\":\"${TokenContract.EOS.contract}\",\"symbol\":\"${TokenContract.EOS.symbol}\"}")
		) { currency, error ->
			hold(currency?.firstOrNull()?.price, error)
		}
	}
}