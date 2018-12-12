package io.goldstone.blockchain.module.home.dapp.dappoverlay.view

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.blinnnk.extension.getParentFragment
import com.blinnnk.util.addFragmentAndSetArgument
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.module.home.dapp.dappexplorer.view.DAPPExplorerFragment
import io.goldstone.blockchain.module.home.dapp.dapplist.model.DAPPType
import io.goldstone.blockchain.module.home.dapp.dapplist.view.DAPPListFragment
import io.goldstone.blockchain.module.home.dapp.dappoverlay.event.DAPPExplorerDisplayEvent
import io.goldstone.blockchain.module.home.dapp.dappoverlay.presenter.DAPPOverlayPresenter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * @author KaySaith
 * @date  2018/12/08
 */
class DAPPOverlayFragment : BaseOverlayFragment<DAPPOverlayPresenter>() {

	var cancelSearchEvent: Runnable? = null
	var enterKeyEvent: Runnable? = null

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
	fun updateDisplayEvent(displayEvent: DAPPExplorerDisplayEvent) {
		if (displayEvent.isShown) getMainActivity()?.showChildFragment(this)
		else getMainActivity()?.supportFragmentManager?.beginTransaction()?.hide(this)?.commitNow()
	}

	override val presenter = DAPPOverlayPresenter(this)
	override fun ViewGroup.initView() {
		if (type == DAPPType.Explorer) {
			addFragmentAndSetArgument<DAPPExplorerFragment>(ContainerID.content)
			showSearchInput(
				cancelEvent = {
					cancelSearchEvent?.run()
					showCloseButton(true) {
						presenter.removeSelfFromActivity()
					}
					showSearchButton(true) {
						showSearchInput(
							cancelEvent = {
								cancelSearchEvent?.run()
								showCloseButton(true) {
									presenter.removeSelfFromActivity()
								}
							},
							enterKeyEvent = {
								enterKeyEvent?.run()
							}
						)
					}
				},
				enterKeyEvent = {
					enterKeyEvent?.run()
				}
			)

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