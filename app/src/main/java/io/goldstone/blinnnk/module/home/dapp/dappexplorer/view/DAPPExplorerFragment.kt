package io.goldstone.blinnnk.module.home.dapp.dappexplorer.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.SoftKeyboard
import io.goldstone.blinnnk.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blinnnk.common.base.gsfragment.GSRecyclerFragment
import io.goldstone.blinnnk.common.language.DappCenterText
import io.goldstone.blinnnk.common.utils.ErrorDisplayManager
import io.goldstone.blinnnk.common.utils.getMainActivity
import io.goldstone.blinnnk.module.home.dapp.dappbrowser.view.PreviousView
import io.goldstone.blinnnk.module.home.dapp.dappcenter.model.DAPPTable
import io.goldstone.blinnnk.module.home.dapp.dappcenter.view.DAPPCenterFragment
import io.goldstone.blinnnk.module.home.dapp.dappexplorer.contract.DAPPExplorerContract
import io.goldstone.blinnnk.module.home.dapp.dappexplorer.presenter.DAPPExplorerPresenter
import io.goldstone.blinnnk.module.home.dapp.dappoverlay.event.DAPPExplorerDisplayEvent
import io.goldstone.blinnnk.module.home.dapp.dappoverlay.view.DAPPOverlayFragment
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
				it.presenter.removeSelfFromActivity()
			}
			it.enterKeyEvent = Runnable {
				getMainActivity()?.apply {
					showDappBrowserFragment(
						formattedURL(it.getSearchContent()),
						PreviousView.DAPPExplorer,
						"FFFFFF",
						parentFragment
					)
					SoftKeyboard.hide(this)
				}
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

	private fun formattedURL(url: String): String {
		return if (!url.contains("http", true)) {
			"https://$url"
		} else url
	}
}