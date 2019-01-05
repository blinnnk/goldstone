@file:Suppress("DEPRECATION")

package io.goldstone.blinnnk.module.home.wallet.walletdetail.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.customListAdapter
import com.blinnnk.extension.addFragment
import com.blinnnk.extension.addFragmentAndSetArguments
import com.blinnnk.extension.orEmptyArray
import io.goldstone.blinnnk.common.base.baserecyclerfragment.BaseRecyclerView
import io.goldstone.blinnnk.common.base.gsfragment.GSRecyclerFragment
import io.goldstone.blinnnk.common.component.overlay.GoldStoneDialog
import io.goldstone.blinnnk.common.language.CommonText
import io.goldstone.blinnnk.common.language.TransactionText
import io.goldstone.blinnnk.common.language.WalletSettingsText
import io.goldstone.blinnnk.common.sharedpreference.SharedValue
import io.goldstone.blinnnk.common.utils.click
import io.goldstone.blinnnk.common.utils.getMainActivity
import io.goldstone.blinnnk.common.utils.safeShowError
import io.goldstone.blinnnk.common.value.ArgumentKey
import io.goldstone.blinnnk.common.value.ContainerID
import io.goldstone.blinnnk.common.value.FragmentTag
import io.goldstone.blinnnk.kernel.receiver.XinGePushReceiver
import io.goldstone.blinnnk.module.common.passcode.view.PasscodeFragment
import io.goldstone.blinnnk.module.common.tokendetail.tokendetailoverlay.presenter.TokenDetailOverlayPresenter
import io.goldstone.blinnnk.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blinnnk.module.home.home.view.findIsItExist
import io.goldstone.blinnnk.module.home.wallet.notifications.notification.view.NotificationFragment
import io.goldstone.blinnnk.module.home.wallet.tokenmanagement.tokenmanagement.view.TokenManagementFragment
import io.goldstone.blinnnk.module.home.wallet.tokenselectionlist.TokenSelectionAdapter
import io.goldstone.blinnnk.module.home.wallet.tokenselectionlist.TokenSelectionRecyclerView
import io.goldstone.blinnnk.module.home.wallet.walletdetail.contract.WalletDetailContract
import io.goldstone.blinnnk.module.home.wallet.walletdetail.model.WalletDetailCellModel
import io.goldstone.blinnnk.module.home.wallet.walletdetail.model.WalletDetailHeaderModel
import io.goldstone.blinnnk.module.home.wallet.walletdetail.presenter.WalletDetailPresenter
import io.goldstone.blinnnk.module.home.wallet.walletsettings.walletsettings.view.WalletSettingsFragment
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.*


/**
 * @date 23/03/2018 3:44 PM
 * @author KaySaith
 */
@Suppress("DEPRECATION")
class WalletDetailFragment : GSRecyclerFragment<WalletDetailCellModel>(), WalletDetailContract.GSView {

	override val pageTitle: String = "Wallet Detail"
	private lateinit var slideHeader: WalletSlideHeader
	private var headerView: WalletDetailHeaderView? = null
	override lateinit var presenter: WalletDetailContract.GSPresenter
	/**
	 * this `slideHeader` will show or hide depends on the distance that user sliding the
	 * recyclerView, and not in the same layer with `RecyclerView's headerView`
	 */
	@SuppressLint("MissingPermission")
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		asyncData = arrayListOf()
		presenter = WalletDetailPresenter(this)
		slideHeader = WalletSlideHeader(context!!)
		wrapper.addView(slideHeader)
		with(slideHeader) {
			notifyButton.click {
				showNotificationListFragment()
			}
			searchButton.click {
				showTokenManagementFragment()
			}
			System.out.println("____")
		}
	}

	override fun onHiddenChanged(hidden: Boolean) {
		super.onHiddenChanged(hidden)
		if (!hidden) {
			presenter.start()
			getMainActivity()?.backEvent = null // 恢复回退站
		} else {
			headerView?.clearBitmap()
		}
	}

	override fun setHeaderData(model: WalletDetailHeaderModel) {
		headerView?.model = model
	}

	override fun showLoading(status: Boolean) {
		headerView?.showLoadingView(status)
	}

	override fun setUnreadCount(count: Int) {
		if (count > 0) slideHeader.notifyButton.setRedDotStyle(count)
		else slideHeader.notifyButton.removeRedDot()
	}

	override fun showSelectionDashboard(tokens: ArrayList<WalletDetailCellModel>, isAddress: Boolean) {
		// Prepare token list and show content scroll overlay view
		val dialog = MaterialDialog(context!!)
		dialog.title(text = TransactionText.tokenSelection)
			.customListAdapter(TokenSelectionAdapter(tokens) {
				if (isAddress)
					TokenSelectionRecyclerView.showTransferAddressFragment(context, it)
				else TokenSelectionRecyclerView.showDepositFragment(context, it)
				dialog.dismiss()
			})
			.negativeButton(text = CommonText.gotIt)
			.show()
	}

	override fun showError(error: Throwable) {
		safeShowError(error)
	}

	override fun setRecyclerViewAdapter(recyclerView: BaseRecyclerView, asyncData: ArrayList<WalletDetailCellModel>?) {
		recyclerView.adapter = WalletDetailAdapter(
			asyncData.orEmptyArray(),
			{ showTokenDetailFragment(it) }
		) {
			headerView = this
			currentAccount.onClick { showWalletSettingsFragment() }
			sendButton.onClick { presenter.showTransferDashboard(true) }
			depositButton.onClick {
				presenter.showTransferDashboard(false)
			}
		}
	}

	private var isShow = false
	private val headerHeight by lazy { slideHeader.layoutParams.height }
	private var totalRange = 0

	override fun observingRecyclerViewVerticalOffset(offset: Int, range: Int) {
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
		asyncData = arrayListOf()
		presenter.start()
		// 检查是否需要显示 `PIN Code` 界面
		if (SharedValue.getPincodeDisplayStatus()) PasscodeFragment.show(this)
		getMainActivity()?.backEvent = null // 恢复回退站
	}

	override fun showMnemonicBackUpFragment() {
		TokenDetailOverlayPresenter.showMnemonicBackupFragment(this)
	}

	override fun showAddressSelectionFragment(data: WalletDetailCellModel) {
		TokenSelectionRecyclerView.showTransferAddressFragment(context, data)
	}

	override fun updateAdapterData(data: ArrayList<WalletDetailCellModel>) {
		updateAdapterData<WalletDetailAdapter>(data)
	}

	override fun showDepositFragment(data: WalletDetailCellModel) {
		TokenSelectionRecyclerView.showDepositFragment(context, data)
	}

	override fun showMnemonicBackUpDialog() {
		GoldStoneDialog(context!!).showBackUpMnemonicStatus {
			showMnemonicBackUpFragment()
		}
	}

	override fun showNotificationListFragment() {
		XinGePushReceiver.clearAppIconReDot()
		activity?.apply {
			if (!findIsItExist(FragmentTag.notification))
				addFragment<NotificationFragment>(ContainerID.main, FragmentTag.notification)
		}
	}

	override fun showChainError() {
		GoldStoneDialog(context!!).showChainErrorDialog()
	}

	private fun showTokenManagementFragment() {
		activity?.apply {
			if (!findIsItExist(FragmentTag.tokenManagement))
				addFragment<TokenManagementFragment>(ContainerID.main, FragmentTag.tokenManagement)
		}
	}

	private fun showWalletSettingsFragment() {
		activity?.apply {
			if (!findIsItExist(FragmentTag.walletSettings))
				addFragmentAndSetArguments<WalletSettingsFragment>(
					ContainerID.main,
					FragmentTag.walletSettings
				) {
					putString(ArgumentKey.walletSettingsTitle, WalletSettingsText.walletSettings)
				}
		}
	}

	private fun showTokenDetailFragment(model: WalletDetailCellModel) {
		activity?.apply {
			if (!findIsItExist(FragmentTag.tokenDetail))
				addFragmentAndSetArguments<TokenDetailOverlayFragment>(ContainerID.main, FragmentTag.tokenDetail) {
					putSerializable(ArgumentKey.tokenDetail, model)
				}
		}
	}
}
