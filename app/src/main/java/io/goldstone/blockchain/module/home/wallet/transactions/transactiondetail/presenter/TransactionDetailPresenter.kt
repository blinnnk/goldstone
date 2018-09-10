package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter

import android.os.Bundle
import android.support.v4.app.Fragment
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.baseoverlayfragment.BaseOverlayFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.language.*
import io.goldstone.blockchain.common.utils.TimeUtils
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.utils.toMillisecond
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.crypto.multichain.CryptoSymbol
import io.goldstone.blockchain.crypto.utils.toBTCCount
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.network.ChainURL
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import io.goldstone.blockchain.module.home.profile.contacts.contractinput.model.ContactModel
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import io.goldstone.blockchain.module.home.profile.profileoverlay.view.ProfileOverlayFragment
import io.goldstone.blockchain.module.home.wallet.notifications.notification.view.NotificationFragment
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationTransactionInfo
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
 * 这个界面由三个入场景公用, 分别是账单列表，转账完成和通知中心进入, `fragment` 承担了多重身份
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
		updateDataFromTransfer()
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
			it.setIconStyle(headerModel)
		}
	}

	fun runBackEventBy(parent: Fragment) {
		when (parent) {
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

	fun getCurrentChainName(): String {
		return ChainURL.getChainNameBySymbol(
			data?.token?.symbol
				?: dataFromList?.symbol
				?: notificationData?.symbol.orEmpty()
		)
	}

	private fun getUnitSymbol(): String {
		val symbol = notificationData?.symbol ?: data?.token?.symbol ?: dataFromList?.symbol
		return when {
			symbol.equals(CryptoSymbol.etc, true) -> CryptoSymbol.etc
			symbol.equals(CryptoSymbol.btc(), true) -> CryptoSymbol.btc()
			symbol.equals(CryptoSymbol.ltc, true) -> CryptoSymbol.ltc
			symbol.equals(CryptoSymbol.bch, true) -> CryptoSymbol.bch
			else -> CryptoSymbol.eth
		}
	}

	fun showAddContactsButton(cell: TransactionDetailCell) {
		TransactionListModel
			.convertMultiToOrFromAddresses(cell.model.info).forEachIndexed { index, address ->
				ContactTable.hasContacts(address) { exist ->
					if (exist) return@hasContacts
					cell.showAddContactButton(index) {
						onClick {
							fragment.parentFragment?.apply {
								when (this) {
									is TokenDetailOverlayFragment -> presenter.removeSelfFromActivity()
									is NotificationFragment -> presenter.removeSelfFromActivity()
								}
							}
							fragment.getMainActivity()?.apply {
								addFragmentAndSetArguments<ProfileOverlayFragment>(ContainerID.main) {
									putString(ArgumentKey.profileTitle, ProfileText.contactsInput)
									putSerializable(ArgumentKey.addressModel, ContactModel(address, getUnitSymbol()))
								}
							}
							preventDuplicateClicks()
						}
					}
				}
			}
	}

	fun showTransactionWebFragment() {
		val symbol = dataFromList?.symbol ?: data?.token?.symbol ?: notificationData?.symbol
		val argument = Bundle().apply {
			putString(
				ArgumentKey.webViewUrl,
				TransactionListModel.generateTransactionURL(currentHash, symbol)
			)
		}
		fragment.parentFragment.apply {
			val webTitle =
				when {
					symbol.equals(CryptoSymbol.etc, true) -> TransactionText.gasTracker
					CryptoSymbol.isBTCSeriesSymbol(symbol) -> TransactionText.transactionWeb
					else -> TransactionText.etherScanTransaction
				}
			when (this) {
				is TokenDetailOverlayFragment -> presenter.showTargetFragment<WebViewFragment>(
					webTitle, TokenDetailText.tokenDetail, argument
				)
				is NotificationFragment -> presenter.showTargetFragment<WebViewFragment>(
					webTitle, NotificationText.notification, argument
				)
			}
		}
	}

	// 根据传入转账信息类型, 来生成对应的更新界面的数据
	fun generateModels(
		receipt: Any? = null
	): ArrayList<TransactionDetailModel> {
		// 从转账界面跳转进来的界面判断燃气费是否是 `BTC`
		val timstamp =
			data?.timestamp
				?: dataFromList?.timeStamp?.toLongOrNull()
				?: notificationData?.timeStamp.orElse(0L)
		val date = TimeUtils.formatDate(timstamp.toMillisecond())
		val memo =
			if (data?.memo.isNull()) TransactionText.noMemo
			else data?.memo
		val fromAddress = data?.fromAddress
			?: dataFromList?.fromAddress
			?: notificationData?.fromAddress
		val symbol = data?.token?.symbol
			?: dataFromList?.symbol
			?: notificationData?.symbol.orEmpty()
		val receiptData = when (receipt) {
			is TransactionListModel -> {
				arrayListOf(
					receipt.minerFee,
					receipt.memo,
					receipt.fromAddress,
					receipt.toAddress,
					receipt.transactionHash,
					receipt.blockNumber,
					receipt.date,
					receipt.url
				)
			}

			is TransactionTable -> {
				arrayListOf(
					formattedMinerFee(),
					memo,
					if (receipt.isReceive) receipt.to
					else fromAddress,
					if (receipt.isReceive) fromAddress
					else receipt.to,
					currentHash,
					receipt.blockNumber,
					date,
					TransactionListModel.generateTransactionURL(currentHash, receipt.symbol)
				)
			}

			is BTCSeriesTransactionTable -> {
				arrayListOf(
					"${receipt.fee.toBigDecimal().toPlainString()} ${receipt.symbol}",
					memo,
					receipt.fromAddress,
					TransactionListModel.formatToAddress(receipt.to),
					currentHash,
					receipt.blockNumber,
					TimeUtils.formatDate(receipt.timeStamp.toMillisecond()),
					TransactionListModel.generateTransactionURL(currentHash, receipt.symbol)
				)
			}

			else -> {
				arrayListOf(
					formattedMinerFee(),
					memo,
					fromAddress,
					data?.toAddress.orEmpty(),
					currentHash,
					TransactionText.pendingBlockConfirmation,
					date,
					TransactionListModel.generateTransactionURL(currentHash, symbol)
				)
			}
		}
		arrayListOf(
			TransactionText.minerFee,
			TransactionText.memo,
			CommonText.from,
			CommonText.to,
			TransactionText.transactionHash,
			TransactionText.blockNumber,
			TransactionText.transactionDate,
			TransactionText.url
		).mapIndexed { index, it ->
			TransactionDetailModel(receiptData[index].toString(), it)
		}.let { models ->
			return if (CryptoSymbol.isBTCSeriesSymbol(getUnitSymbol())) {
				// 如果是 `比特币` 账单不显示 `Memo`
				models.filterNot {
					it.description.equals(TransactionText.memo, true)
				}.toArrayList()
			} else {
				models.toArrayList()
			}
		}
	}

	private fun formattedMinerFee(): String? {
		val dataMinerFee =
			if (CryptoSymbol.isBTCSeriesSymbol(data?.token?.symbol))
				data?.minerFee?.toDouble()?.toBTCCount()?.toBigDecimal()?.toPlainString()
			else data?.minerFee
		return if (data.isNull()) dataFromList?.minerFee
		else "$dataMinerFee ${getUnitSymbol()}"
	}

	private fun TransactionDetailFragment.setBackEventByParentFragment() {
		parentFragment?.let { parent ->
			if (parent is BaseOverlayFragment<*>) {
				parent.headerTitle = TransactionText.detail
				parent.overlayView.header.backButton.onClick {
					runBackEventBy(parent)
				}
			}
		}
	}
}