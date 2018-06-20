package io.goldstone.blockchain.module.home.profile.chainselection.presenter

import com.blinnnk.extension.jump
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.value.ChainText
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.module.entrance.splash.view.SplashActivity
import io.goldstone.blockchain.module.home.profile.chainselection.model.ChainSelectionModel
import io.goldstone.blockchain.module.home.profile.chainselection.view.ChainSelectionFragment

/**
 * @date 2018/5/11 4:26 PM
 * @author KaySaith
 */
class ChainSelectionPresenter(
	override val fragment: ChainSelectionFragment
) : BaseRecyclerPresenter<ChainSelectionFragment, ChainSelectionModel>() {
	
	override fun updateData() {
		AppConfigTable.getAppConfig {
			it?.apply {
				fragment.asyncData = arrayListOf(
					ChainSelectionModel(
						ChainText.mainnet,
						ChainText.mainnetDescription,
						R.drawable.mainnet_icon,
						true
					),
					ChainSelectionModel(
						ChainText.testnet,
						ChainText.testnetDescription,
						R.drawable.testnet_icon,
						false
					)
				)
			}
		}
	}
	
	fun updateCurrentChainID(chainID: String) {
		AppConfigTable.updateChainID(chainID) {
			fragment.activity?.jump<SplashActivity>()
		}
	}
}