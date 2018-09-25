package io.goldstone.blockchain.module.home.profile.chain.chainselection.presenter

import android.os.Bundle
import com.blinnnk.extension.getParentFragment
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.language.ChainText
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.module.home.profile.chain.chainselection.model.ChainSelectionModel
import io.goldstone.blockchain.module.home.profile.chain.chainselection.view.ChainSelectionFragment
import io.goldstone.blockchain.module.home.profile.chain.nodeselection.view.NodeSelectionFragment
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment

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
						isMainnet
					),
					ChainSelectionModel(
						ChainText.testnet,
						ChainText.testnetDescription,
						R.drawable.testnet_icon,
						!isMainnet
					)
				)
			}
		}
	}

	fun showNodeSelectionFragment(isMainnet: Boolean) {
		fragment.getParentFragment<ProfileOverlayFragment> {
			presenter.showTargetFragment<NodeSelectionFragment>(
				Bundle().apply { putBoolean(ArgumentKey.isMainnet, isMainnet) }
			)
		}
	}
}