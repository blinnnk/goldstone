package io.goldstone.blinnnk.module.home.profile.chain.chainselection.view

import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.preventDuplicateClicks
import io.goldstone.blinnnk.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blinnnk.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blinnnk.common.language.AlertText
import io.goldstone.blinnnk.common.language.ChainText
import io.goldstone.blinnnk.common.language.ProfileText
import io.goldstone.blinnnk.common.sharedpreference.SharedWallet
import io.goldstone.blinnnk.common.utils.alert
import io.goldstone.blinnnk.module.home.profile.chain.chainselection.model.ChainSelectionModel
import io.goldstone.blinnnk.module.home.profile.chain.chainselection.presenter.ChainSelectionPresenter
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * @date 2018/5/11 4:26 PM
 * @author KaySaith
 */
class ChainSelectionFragment : BaseRecyclerFragment<ChainSelectionPresenter, ChainSelectionModel>() {

	override val pageTitle: String = ProfileText.chain
	override val presenter = ChainSelectionPresenter(this)

	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<ChainSelectionModel>?
	) {
		recyclerView.adapter = ChainSelectionAdapter(asyncData.orEmptyArray()) {
			val isMainnet = model.title.equals(ChainText.mainnet, true)
			checkIsSingleChainWalletOrElse(isMainnet) {
				presenter.showNodeSelectionFragment(isMainnet)
			}
		}
	}

	private fun checkIsSingleChainWalletOrElse(isMainnet: Boolean, callback: () -> Unit) {
		val type = SharedWallet.getCurrentWalletType()
		when {
			type.isBTCTest() && isMainnet -> context.alert(AlertText.testnetOnly)
			type.isEOSJungle() || type.isEOSKylin() -> context.alert(AlertText.testnetOnly)
			type.isEOSMainnet() && !isMainnet -> context.alert(AlertText.mainnetOnly)
			type.isBTC() && !isMainnet -> context.alert(AlertText.mainnetOnly)
			type.isLTC() && !isMainnet -> context.alert(AlertText.mainnetOnly)
			type.isBCH() && !isMainnet -> context.alert(AlertText.mainnetOnly)
			else -> callback()
		}
	}
}