package io.goldstone.blockchain.module.home.dapp.dapplist.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.hasValue
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BottomLoadingView
import io.goldstone.blockchain.common.base.gsfragment.GSRecyclerFragment
import io.goldstone.blockchain.common.language.DappCenterText
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.ErrorDisplayManager
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.DataValue
import io.goldstone.blockchain.module.home.dapp.dappbrowser.view.PreviousView
import io.goldstone.blockchain.module.home.dapp.dappcenter.model.DAPPTable
import io.goldstone.blockchain.module.home.dapp.dappcenter.view.DAPPCenterFragment
import io.goldstone.blockchain.module.home.dapp.dapplist.contract.DAPPListContract
import io.goldstone.blockchain.module.home.dapp.dapplist.event.DAPPListDisplayEvent
import io.goldstone.blockchain.module.home.dapp.dapplist.model.DAPPType
import io.goldstone.blockchain.module.home.dapp.dapplist.presenter.DAPPListPresenter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * @author KaySaith
 * @date  2018/12/08
 */
class DAPPListFragment : GSRecyclerFragment<DAPPTable>(), DAPPListContract.GSView {

	override val pageTitle: String
		get() = when (type) {
			DAPPType.Recommend -> DappCenterText.recommendDapp
			DAPPType.Latest -> DappCenterText.recentDapp
			DAPPType.New -> DappCenterText.newDapp
			else -> ""
		}
	override lateinit var presenter: DAPPListContract.GSPresenter
	private var bottomLoadingView: BottomLoadingView? = null

	private val type by lazy {
		arguments?.getSerializable(ArgumentKey.dappType) as? DAPPType
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		EventBus.getDefault().register(this)
	}

	override fun onDestroy() {
		super.onDestroy()
		EventBus.getDefault().unregister(this)
	}

	@Subscribe(threadMode = ThreadMode.POSTING)
	fun updateDisplayEvent(displayEvent: DAPPListDisplayEvent) {
		if (displayEvent.isShown) {
			getMainActivity()?.showChildFragment(this)
			recoveryBackEvent()
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		asyncData = arrayListOf()
		presenter = DAPPListPresenter(this)
		presenter.start()
		type?.apply {
			presenter.getData(this) {
				updateAdapterDataSet<DAPPListAdapter>(it.toArrayList())
			}
		}
	}

	private var noMoreData = false
	override fun flipPage() {
		super.flipPage()
		showBottomLoading(!noMoreData)
		val pageIndex = ((asyncData?.size ?: 0) / DataValue.dappPageCount)
		// 因为第一页数据的逻辑, 每当 APP 重启或初始化都会更新, 那么
		if (!noMoreData) {
			presenter.loadMore(pageIndex, type!!) { newData ->
				noMoreData = newData.size < DataValue.dappPageCount
				if (newData.isEmpty()) {
					showBottomLoading(false)
					return@loadMore
				}
				// 如果初始化的页面有数据, 但是不足一页的数量, 那么清楚内存数据
				// 显示全部网络返回的数据, 并更新本地数据库第一页的数据
				if (asyncData?.size.hasValue() && pageIndex == 0) {
					asyncData?.clear()
					asyncData?.addAll(newData)
					DAPPTable.dao.insertAll(newData)
				} else {
					asyncData?.addAll(newData)
				}
				launchUI {
					val totalCount = asyncData?.size!!
					val startPosition = asyncData?.size!! - newData.size
					recyclerView.adapter?.notifyItemRangeChanged(startPosition, totalCount)
					showBottomLoading(false)
				}
			}
		}
	}

	override fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<DAPPTable>?) {
		recyclerView.adapter = DAPPListAdapter(
			asyncData.orEmptyArray(),
			hold = {
				bottomLoadingView = it
			},
			clickEvent = {
				DAPPCenterFragment.showAttentionOrElse(context!!, it.id) {
					getMainActivity()?.apply {
						showDappBrowserFragment(
							it.url,
							PreviousView.DAPPList,
							it.backgroundColor,
							this@DAPPListFragment
						)
						getDAPPCenterFragment()?.presenter?.setUsedDAPPs()
					}
				}
			}
		)
	}

	override fun showError(error: Throwable) {
		ErrorDisplayManager(error).show(context)
	}

	private fun showBottomLoading(status: Boolean) = launchUI {
		if (status) bottomLoadingView?.show()
		else {
			isLoadingData = false
			bottomLoadingView?.hide()
		}
	}

}