package io.goldstone.blockchain.module.home.dapp.dapplistdetail.view

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.module.home.dapp.dappexplorer.view.DAPPExplorerFragment
import io.goldstone.blockchain.module.home.dapp.dapplist.model.DAPPType
import io.goldstone.blockchain.module.home.dapp.dapplist.view.DAPPListFragment
import io.goldstone.blockchain.module.home.dapp.dapplistdetail.presenter.DAPPOverlayPresenter


/**
 * @author KaySaith
 * @date  2018/12/08
 */
class DAPPOverlayFragment : BaseOverlayFragment<DAPPOverlayPresenter>() {

	var cancelSearchEvent: Runnable? = null

	private val type by lazy {
		arguments?.getSerializable(ArgumentKey.dappType) as? DAPPType
	}

	override val presenter = DAPPOverlayPresenter(this)
	override fun ViewGroup.initView() {
		if (type == DAPPType.Explorer) {
			addFragmentAndSetArgument<DAPPExplorerFragment>(ContainerID.content)
			showSearchInput {
				cancelSearchEvent?.run()
				showCloseButton(true) {
					presenter.removeSelfFromActivity()
				}
				showSearchButton(true) {
					showSearchInput {
						cancelSearchEvent?.run()
						showCloseButton(true) {
							presenter.removeSelfFromActivity()
						}
					}
				}
			}

		} else {
			addFragmentAndSetArgument<DAPPListFragment>(ContainerID.content) {
				putSerializable(ArgumentKey.dappType, type)
			}
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		if (type == DAPPType.Explorer) showCloseButton(false) {}
	}
}