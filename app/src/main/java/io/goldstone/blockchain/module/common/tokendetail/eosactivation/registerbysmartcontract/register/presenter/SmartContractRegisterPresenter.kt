package io.goldstone.blockchain.module.common.tokendetail.eosactivation.registerbysmartcontract.register.presenter

import android.os.Bundle
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.registerbysmartcontract.detail.view.SmartContractRegisterDetailFragment
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.registerbysmartcontract.register.view.SmartContractRegisterFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment


/**
 * @author KaySaith
 * @date  2018/09/25
 */
class SmartContractRegisterPresenter(
	override val fragment: SmartContractRegisterFragment
) : BasePresenter<SmartContractRegisterFragment>() {

	fun showSmartContractRegisterDetailFragment(accountName: String) {
		fragment.getParentFragment<TokenDetailOverlayFragment>()
			?.presenter?.showTargetFragment<SmartContractRegisterDetailFragment>(
			Bundle().apply { putString(ArgumentKey.eosAccountRegister, accountName) }
		)
	}

	fun getEOSCurrencyPrice(hold: (currency: Double?, error: RequestError) -> Unit) {
		GoldStoneAPI.getPriceByContractAddress(
			listOf("{\"address\":\"${TokenContract.EOS.contract}\",\"symbol\":\"${TokenContract.EOS.symbol}\"}"),
			// 网络获取价格出错后从本地数据库获取价格
			{ hold(null, it) }
		) {
			hold(it.firstOrNull()?.price, RequestError.None)
		}
	}
}