package io.goldstone.blockchain.module.home.dapp.dappexplorer.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.base.gsfragment.GSRecyclerFragment
import io.goldstone.blockchain.common.language.DappCenterText
import io.goldstone.blockchain.common.utils.ErrorDisplayManager
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.module.home.dapp.dappbrowser.view.PreviousView
import io.goldstone.blockchain.module.home.dapp.dappcenter.model.DAPPTable
import io.goldstone.blockchain.module.home.dapp.dappcenter.view.DAPPCenterFragment
import io.goldstone.blockchain.module.home.dapp.dappexplorer.contract.DAPPExplorerContract
import io.goldstone.blockchain.module.home.dapp.dappexplorer.presenter.DAPPExplorerPresenter
import io.goldstone.blockchain.module.home.dapp.dappoverlay.event.DAPPExplorerDisplayEvent
import io.goldstone.blockchain.module.home.dapp.dappoverlay.view.DAPPOverlayFragment
import org.greenrobot.eventbus.EventBus


/**
 * @author KaySaith
 * @date  2018/12/08
 */
class DAPPExplorerFragment : GSRecyclerFragment<DAPPTable>(), DAPPExplorerContract.GSView {

	override val pageTitle: String get() = DappCenterText.dappExplorer
	override lateinit var presenter: DAPPExplorerContract.GSPresenter

	override fun showError(error: Throwable) {
		ErrorDisplayManager(error).show(context)
	}

	private val overlayFragment by lazy {
		parentFragment as? DAPPOverlayFragment
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		asyncData = arrayListOf()
		presenter = DAPPExplorerPresenter(this)
		presenter.start()
		overlayFragment?.let {
			it.searchInputListener { condition ->
				presenter.getSearchResult(condition) { result ->
					updateAdapterData<DAPPExplorerAdapter>(result.toArrayList())
				}
			}
			it.cancelSearchEvent = Runnable {
				updateAdapterData<DAPPExplorerAdapter>(arrayListOf())
			}
			it.enterKeyEvent = Runnable {
				getMainActivity()?.showDappBrowserFragment(
					it.getSearchContent(),
					PreviousView.DAPPExplorer,
					"FFFFFF",
					this
				)
			}
		}
	}

	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<DAPPTable>?
	) {
		recyclerView.adapter = DAPPExplorerAdapter(asyncData.orEmptyArray()) {
			DAPPCenterFragment.showAttentionOrElse(context!!, id) {
				getMainActivity()?.apply {
					showDappBrowserFragment(
						url,
						PreviousView.DAPPExplorer,
						backgroundColor,
						null
					)
					EventBus.getDefault().post(DAPPExplorerDisplayEvent(false))
					getDAPPCenterFragment()?.refreshLatestUsed()
				}
			}
		}
	}
}