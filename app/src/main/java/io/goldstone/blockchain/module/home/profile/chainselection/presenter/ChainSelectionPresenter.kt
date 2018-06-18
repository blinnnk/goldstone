package io.goldstone.blockchain.module.home.profile.chainselection.presenter

import com.blinnnk.extension.jump
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.value.ChainID
import io.goldstone.blockchain.common.value.ChainText
import io.goldstone.blockchain.common.value.EthereumNetColor
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
						ChainText.goldStoneMain,
						chainID == ChainID.Main.id,
						EthereumNetColor.main,
						ChainID.Main.id
					),
					ChainSelectionModel(
						ChainText.rinkeby,
						chainID == ChainID.Rinkeby.id,
						EthereumNetColor.rinkeby,
						ChainID.Rinkeby.id
					),
					ChainSelectionModel(
						ChainText.ropsten,
						chainID == ChainID.Ropstan.id,
						EthereumNetColor.ropstan,
						ChainID.Ropstan.id
					),
					ChainSelectionModel(
						ChainText.kovan,
						chainID == ChainID.Kovan.id,
						EthereumNetColor.kovan,
						ChainID.Kovan.id
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