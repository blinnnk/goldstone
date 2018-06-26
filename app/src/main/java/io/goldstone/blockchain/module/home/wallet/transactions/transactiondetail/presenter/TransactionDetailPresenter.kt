package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter

import android.os.Bundle
import android.support.v4.app.Fragment
import com.blinnnk.extension.addFragmentAndSetArguments
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orEmpty
import com.blinnnk.extension.preventDuplicateClicks
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.crypto.ChainType
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.kernel.network.ChainURL
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import io.goldstone.blockchain.module.home.wallet.notifications.notification.view.NotificationFragment
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.presenter.NotificationTransactionInfo
import io.goldstone.blockchain.module.home.wallet.transactions.transaction.view.TransactionFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.ReceiptModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionDetailModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionHeaderModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailCell
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailHeaderView
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 27/03/2018 3:27 AM
 * @author KaySaith
 * @description
 * 这个界面由三个入场景公用, 分别是账单列表，转账完成或通知中心进入, `fragment` 承担了多重身份
 * 固再次需要注意.
 */
class TransactionDetailPresenter(
	override val fragment: TransactionDetailFragment
) : BaseRecyclerPresenter<TransactionDetailFragment, TransactionDetailModel>() {
	
	internal val data by lazy {
		fragment.arguments?.get(ArgumentKey.transactionDetail) as? ReceiptModel
	}
	internal val dataFromList by lazy {
		fragment.arguments?.get(ArgumentKey.transactionFromList) as? TransactionListModel
	}
	internal val notificationData by lazy {
		fragment.arguments?.get(ArgumentKey.notificationTransaction) as? NotificationTransactionInfo
	}
	internal var count = 0.0
	internal var currentHash = ""
	internal var headerModel: TransactionHeaderModel? = null
	
	override fun updateData() {
		/** 这个是从账目列表进入的详情, `Transaction List`, `TokenDetail` */
		updateDataFromTransactionList()
		/** 这个是转账完毕后进入的初始数据 */
		updateDataFromTransferFragment()
		/** 这个是从通知中心进入的, 通知中心的显示是现查账. */
		updateDataFromNotification()
	}
	
	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		fragment.setBackEventByParentFragment()
	}
	
	// 更新头部数字的工具
	fun updateHeaderValue(headerModel: TransactionHeaderModel) {
		fragment.recyclerView.getItemAtAdapterPosition<TransactionDetailHeaderView>(0) {
			it?.setIconStyle(headerModel)
		}
	}
	
	fun runBackEventBy(parent: Fragment) {
		when (parent) {
			is TransactionFragment -> {
				parent.headerTitle = TransactionText.detail
				parent.presenter.popFragmentFrom<TransactionDetailFragment>()
			}
			
			is TokenDetailOverlayFragment -> {
				parent.headerTitle = TokenDetailText.tokenDetail
				parent.presenter.popFragmentFrom<TransactionDetailFragment>()
			}
			
			is NotificationFragment -> {
				parent.headerTitle = NotificationText.notification
				parent.presenter.popFragmentFrom<TransactionDetailFragment>()
			}
		}
	}
	
	fun getCunrrentChainType(): ChainType {
		return ChainURL.getChainTypeBySymbol(
			data?.token?.symbol
			?: dataFromList?.symbol
			?: notificationData?.symbol.orEmpty()
		)
	}
	
	fun getCurrentChainName(): String {
		return ChainURL.getChainNameBySymbol(
			data?.token?.symbol
			?: dataFromList?.symbol
			?: notificationData?.symbol.orEmpty()
		)
	}
	
	fun getUnitSymbol(): String {
		return if (getCunrrentChainType() == ChainType.ETH) CryptoSymbol.eth
		else CryptoSymbol.etc
	}
	
	fun showAddContactsButton(cell: TransactionDetailCell) {
		ContactTable.getAllContacts {
			if (it.find { contact ->
					contact.address.equals(cell.model.info, true)
				}.isNull()) {
				cell.showAddContactButton {
					onClick {
						fragment.parentFragment?.apply {
							when (this) {
								is TokenDetailOverlayFragment -> presenter.removeSelfFromActivity()
								is TransactionFragment -> presenter.removeSelfFromActivity()
							}
						}
						fragment.getMainActivity()?.apply {
							addFragmentAndSetArguments<ProfileOverlayFragment>(ContainerID.main) {
								putString(ArgumentKey.profileTitle, ProfileText.contactsInput)
								putString(ArgumentKey.address, cell.model.info)
							}
						}
						preventDuplicateClicks()
					}
				}
			}
		}
	}
	
	fun showEtherScanTransactionFragment() {
		val argument = Bundle().apply {
			putString(
				ArgumentKey.webViewUrl,
				TransactionListModel.generateTransactionURL(
					currentHash,
					dataFromList?.symbol ?: data?.token?.contract ?: notificationData?.symbol
				)
			)
		}
		fragment.parentFragment.apply {
			when (this) {
				is TransactionFragment -> {
					presenter.showTargetFragment<WebViewFragment>(
						TransactionText.etherScanTransaction, TransactionText.detail, argument
					)
				}
				
				is TokenDetailOverlayFragment -> {
					presenter.showTargetFragment<WebViewFragment>(
						TransactionText.etherScanTransaction, TokenDetailText.tokenDetail, argument
					)
				}
				
				is NotificationFragment -> {
					presenter.showTargetFragment<WebViewFragment>(
						TransactionText.etherScanTransaction, NotificationText.notification, argument
					)
				}
			}
		}
	}
	
	private fun TransactionDetailFragment.setBackEventByParentFragment() {
		parentFragment.apply {
			when (this) {
				is TransactionFragment -> {
					overlayView.header.backButton.onClick {
						headerTitle = TransactionText.detail
						presenter.popFragmentFrom<TransactionDetailFragment>(TransactionFragment.viewPagerSize)
					}
				}
				
				is TokenDetailOverlayFragment -> {
					overlayView.header.backButton.onClick {
						headerTitle = TokenDetailText.tokenDetail
						presenter.popFragmentFrom<TransactionDetailFragment>()
					}
				}
				
				is NotificationFragment -> {
					overlayView.header.backButton.onClick {
						headerTitle = TokenDetailText.tokenDetail
						presenter.popFragmentFrom<TransactionDetailFragment>()
					}
				}
			}
		}
	}
}