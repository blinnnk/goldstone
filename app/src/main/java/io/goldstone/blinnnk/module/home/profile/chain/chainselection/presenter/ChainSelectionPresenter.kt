package io.goldstone.blinnnk.module.home.profile.chain.chainselection.presenter

import android.os.Bundle
import com.blinnnk.extension.getParentFragment
import io.goldstone.blinnnk.R
import io.goldstone.blinnnk.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blinnnk.common.language.ChainText
import io.goldstone.blinnnk.common.sharedpreference.SharedValue
import io.goldstone.blinnnk.common.value.ArgumentKey
import io.goldstone.blinnnk.module.home.profile.chain.chainselection.model.ChainSelectionModel
import io.goldstone.blinnnk.module.home.profile.chain.chainselection.view.ChainSelectionFragment
import io.goldstone.blinnnk.module.home.profile.chain.nodeselection.view.NodeSelectionFragment
import io.goldstone.blinnnk.module.home.profile.profileoverlay.view.ProfileOverlayFragment

/**
 * @date 2018/5/11 4:26 PM
 * @author KaySaith
 */
class ChainSelectionPresenter(
	override val fragment: ChainSelectionFragment
) : BaseRecyclerPresenter<ChainSelectionFragment, ChainSelectionModel>() {

	override fun updateData() {
		fragment.asyncData = arrayListOf(
			ChainSelectionModel(
				ChainText.mainnet,
				ChainText.mainnetDescription,
				R.drawable.mainnet_icon,
				!SharedValue.isTestEnvironment()
			),
			ChainSelectionModel(
				ChainText.testnet,
				ChainText.testnetDescription,
				R.drawable.testnet_icon,
				SharedValue.isTestEnvironment()
			)
		)
	}

	fun showNodeSelectionFragment(isMainnet: Boolean) {
		fragment.getParentFragment<ProfileOverlayFragment> {
			presenter.showTargetFragment<NodeSelectionFragment>(
				Bundle().apply { putBoolean(ArgumentKey.isMainnet, isMainnet) }
			)
		}
	}
}