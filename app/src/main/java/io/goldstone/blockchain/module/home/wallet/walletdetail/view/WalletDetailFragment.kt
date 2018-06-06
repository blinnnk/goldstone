package io.goldstone.blockchain.module.home.wallet.walletdetail.view

import android.os.Bundle
import android.view.View
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.preventDuplicateClicks
import io.goldstone.blockchain.common.base.BaseRecyclerView
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerFragment
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import io.goldstone.blockchain.module.home.wallet.walletdetail.presenter.WalletDetailPresenter
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.util.*

/**
 * @date 23/03/2018 3:44 PM
 * @author KaySaith
 */
class WalletDetailFragment : BaseRecyclerFragment<WalletDetailPresenter, WalletDetailCellModel>() {
	
	private val slideHeader by lazy { WalletSlideHeader(context!!) }
	private var headerView: WalletDetailHeaderView? = null
	override val presenter = WalletDetailPresenter(this)
	
	override fun setRecyclerViewAdapter(
		recyclerView: BaseRecyclerView,
		asyncData: ArrayList<WalletDetailCellModel>?
	) {
		recyclerView.adapter = WalletDetailAdapter(asyncData.orEmptyArray(), {
			onClick {
				model?.apply {
					presenter.showMyTokenDetailFragment(this)
				}
				preventDuplicateClicks()
			}
		}) {
			headerView = this
			currentAccount.onClick { presenter.showWalletSettingsFragment() }
			addTokenButton.onClick { presenter.showTokenManagementFragment() }
			sendButton.onClick {
				presenter.showTransferSelectionOverlay(true)
			}
			depositButton.onClick {
				presenter.showTransferSelectionOverlay(false)
			}
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
			historyButton.onClick { presenter.showTransactionsFragment() }
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
	
	fun setNotificationUnreadCount(count: String) {
		slideHeader.notifyButton.setRedotStyle(count)
	}
	
	fun recoveryNotifyButtonStyle() {
		slideHeader.notifyButton.removeRedot()
	}
}