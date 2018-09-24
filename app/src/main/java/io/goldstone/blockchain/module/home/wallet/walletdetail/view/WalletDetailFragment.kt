package io.goldstone.blockchain.module.home.wallet.walletdetail.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blockchain.common.component.overlay.ContentScrollOverlayView
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.ElementID
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.module.common.passcode.view.PasscodeFragment
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import io.goldstone.blockchain.module.home.wallet.walletdetail.presenter.WalletDetailPresenter
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.util.*

/**
 * @date 23/03/2018 3:44 PM
 * @author KaySaith
 */
class WalletDetailFragment :
	BaseRecyclerFragment<WalletDetailPresenter, WalletDetailCellModel>() {

	private val slideHeader by lazy { WalletSlideHeader(context!!) }
	private var headerView: WalletDetailHeaderView? = null
	override val presenter = WalletDetailPresenter(this)

	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<WalletDetailCellModel>?
	) {
		recyclerView.adapter = WalletDetailAdapter(asyncData.orEmptyArray(), {
			onClick {
				model?.apply { presenter.showMyTokenDetailFragment(this) }
				preventDuplicateClicks()
			}
		}) {
			headerView = this
			currentAccount.onClick { presenter.showWalletSettingsFragment() }
			addTokenButton.onClick { presenter.showTokenManagementFragment() }
			sendButton.onClick { presenter.setQuickTransferEvent(true) }
			depositButton.onClick { presenter.setQuickTransferEvent(false) }
		}
	}

	fun showMiniLoadingView() {
		headerView?.showLoadingView(true)
	}

	fun removeMiniLoadingView() {
		headerView?.showLoadingView(false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		wrapper.addView(slideHeader)
		/**
		 * this `slideHeader` will show or hide depends on the distance that user sliding the
		 * recyclerView, and not in the same layer with `RecyclerView's headerView`
		 */
		slideHeader.apply {
			notifyButton.onClick { presenter.showNotificationListFragment() }
		}
	}

	private var isShow = false
	private val headerHeight by lazy { slideHeader.layoutParams.height }
	private var totalRange = 0

	override fun observingRecyclerViewVerticalOffset(
		offset: Int,
		range: Int
	) {
		if (offset == 0) totalRange = 0
		if (totalRange == 0) totalRange = range

		if (offset >= headerHeight && !isShow) {
			slideHeader.onHeaderShowedStyle()
			isShow = true
		}
		if (range > totalRange - headerHeight && isShow) {
			slideHeader.onHeaderHidesStyle()
			isShow = false
		}
	}

	/** 这个界面设计到的实时价格非常敏感, 所以更新时机会很频发. eg. 汇率 */
	override fun onResume() {
		super.onResume()
		// 检查更新未读消息数字
		updateUnreadCount()
		// 检查是否需要更新数据
		if (!asyncData.isNullOrEmpty()) {
			presenter.updateData()
		}
		// 检查是否需要显示 `PIN Code` 界面
		showPinCodeFragment()
		// 恢复回退站
		getMainActivity()?.backEvent = null
	}

	override fun onHiddenChanged(hidden: Boolean) {
		super.onHiddenChanged(hidden)
		if (!hidden) {
			// 恢复显示的时候检查更新数据
			presenter.updateData()
			// 检查更新未读消息数字
			updateUnreadCount()
			// 恢复回退站
			getMainActivity()?.backEvent = null
		}

	}

	fun updateUnreadCount() {
		presenter.updateUnreadCount { unreadCount, error ->
			if (error.isNone() && !unreadCount.isNull()) {
				if (unreadCount!! > 0) {
					slideHeader.notifyButton.setRedDotStyle(unreadCount)
				} else {
					slideHeader.notifyButton.removeRedDot()
				}
			} else context.alert(error.message)
		}
	}

	override fun setBackEvent(mainActivity: MainActivity?) {
		val overlay = mainActivity
			?.getMainContainer()
			?.findViewById<ContentScrollOverlayView>(ElementID.contentScrollview)
		overlay.isNull() isTrue {
			super.setBackEvent(mainActivity)
		} otherwise {
			overlay?.remove()
			mainActivity?.backEvent = null
		}
	}

	private fun showPinCodeFragment() {
		if (activity?.supportFragmentManager?.findFragmentByTag(FragmentTag.pinCode).isNull()) AppConfigTable.getAppConfig {
			it?.showPincode?.isTrue {
				activity?.addFragmentAndSetArguments<PasscodeFragment>(
					ContainerID.main,
					FragmentTag.pinCode
				)
			}
		}
	}
}