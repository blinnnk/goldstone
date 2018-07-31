package io.goldstone.blockchain.module.home.profile.chain.chainselection.view

import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.preventDuplicateClicks
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.AlertText
import io.goldstone.blockchain.common.value.ChainText
import io.goldstone.blockchain.common.value.WalletType
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
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
		WalletTable.getWalletType {
			when (it) {
				WalletType.BTCTestOnly -> context.alert(AlertText.testnetOnly)
				WalletType.BTCOnly -> context.alert(AlertText.mainnetOnly)
				else -> callback()
			}
		}
	}
}