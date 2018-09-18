package io.goldstone.blockchain.module.home.profile.chain.chainselection.view

import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.preventDuplicateClicks
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.language.AlertText
import io.goldstone.blockchain.common.language.ChainText
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.module.home.profile.chain.chainselection.model.ChainSelectionModel
import io.goldstone.blockchain.module.home.profile.chain.chainselection.presenter.ChainSelectionPresenter
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 2018/5/11 4:26 PM
 * @author KaySaith
 */
class ChainSelectionFragment : BaseRecyclerFragment<ChainSelectionPresenter, ChainSelectionModel>() {

	override val presenter = ChainSelectionPresenter(this)

	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<ChainSelectionModel>?
	) {
		recyclerView.adapter = ChainSelectionAdapter(asyncData.orEmptyArray()) {
			onClick {
				checkIsSingleChainWalletOrElse {
					presenter.showNodeSelectionFragment(
						model.title.equals(ChainText.mainnet, true)
					)
				}
				preventDuplicateClicks()
			}
		}
	}

	private fun checkIsSingleChainWalletOrElse(callback: () -> Unit) {
		val type = Config.getCurrentWalletType()
		when {
			type.isBTCTest() -> context.alert(AlertText.testnetOnly)
			type.isBTC() -> context.alert(AlertText.mainnetOnly)
			type.isLTC() -> context.alert(AlertText.mainnetOnly)
			type.isBCH() -> context.alert(AlertText.mainnetOnly)
			else -> callback()
		}
	}
}