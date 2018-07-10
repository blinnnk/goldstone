package io.goldstone.blockchain.module.home.wallet.walletsettings.walletaddressmanager.view

import android.support.v4.app.Fragment
import com.blinnnk.extension.into
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletaddressmanager.presenter.WalletAddressManagerPresneter
import org.jetbrains.anko.*

/**
 * @date 2018/7/11 12:44 AM
 * @author KaySaith
 */
class WalletAddressManagerFragment : BaseFragment<WalletAddressManagerPresneter>() {
	
	private val ethereumAddress by lazy { EthereumSeriesAddress(context!!) }
	override val presenter = WalletAddressManagerPresneter(this)
	
	override fun AnkoContext<Fragment>.initView() {
		scrollView {
			lparams(matchParent, matchParent)
			verticalLayout {
				lparams {
					leftPadding = PaddingSize.device
					topPadding = 20.uiPX()
				}
				ethereumAddress.into(this)
			}
		}
	}
	
	fun setEthereumAddressModel(model: List<String>) {
		ethereumAddress.model = model
	}
}

