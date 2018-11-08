package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter

import android.support.annotation.WorkerThread
import com.blinnnk.util.TinyNumber
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.TransactionText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.utils.isEmptyThen
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.contract.TransactionDetailContract
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.*
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.observer.BTCSeriesTransactionObserver
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.observer.EOSTransactionObserver
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.observer.ETHSeriesTransactionObserver
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter.ETHSeriesTransactionUtils.getCurrentConfirmationNumber
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter.ETHSeriesTransactionUtils.updateERC20FeeInfo
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import org.jetbrains.anko.doAsync


/**
 * @author KaySaith
 * @date  2018/11/07
 */
class TransactionDetailPresenter(
	val data: TransactionSealedModel,
	val detailView: TransactionDetailContract.GSView
) : TransactionDetailContract.GSPresenter {

	override fun start() {
		detailView.showLoading(true)
		setTransactionInformation()
	}

	private fun setTransactionInformation() {
		// Pending 更新数块的逻辑
		if (data.isPending) {
			observerPendingTransaction()
			detailView.showProgress(TransactionProgressModel(data.confirmations))
			detailView.showLoading(false)
		} else detailView.showProgress(null)
		// 显示账单的基础信息
		detailView.showHeaderData(TransactionHeaderModel(data))
		showTransactionInfo(data.blockNumber, data.confirmations)
		detailView.showTransactionAddresses(
			TransactionDetailModel(data.fromAddress, CommonText.from.toUpperCase()),
			TransactionDetailModel(data.toAddress, CommonText.to.toUpperCase())
		)
		// 显示账单的 Memo 信息, 注意只有 ETHSeries 的账单存在拉取链上信息逻辑
		if (data.contract.isETHSeries() && !isFromNotification()) {
			if (data.memo.isEmpty()) {
				detailView.showMemo(TransactionDetailModel("Getting Memo From Chain", TransactionText.memo))
				getAndShowChainMemo()
			} else {
				// `EOS` 系列的 `Memo` 和 `ETHSeries` 获取不一样
				val memo = data.memo isEmptyThen TransactionText.noMemo
				detailView.showMemo(TransactionDetailModel(memo, TransactionText.memo))
				detailView.showLoading(false)
			}
		}

		// `BlockNumber` 为 `-1` 意味着还没有被收录, 所以不存在 `ConfirmationCount`
		if (data.blockNumber > 0) updateConfirmationNumber()
		// isPending 为 False 并且 Confirmation 为 -1 的情况意味当前来自与 Notification
		if (isFromNotification()) updateTransactionFromNotification()

		/**
		 *  这个检查应对的情况是, 从 `EtherScan` 拉取的 `ETH` 账单存在 `ERC20` 的 `Token` 转账信息,
		 * 这个信息没有 `Symbol` 和 `ContractAddress` 以及 `Decimal`, 如果用户拉取了, 在没有拉取
		 * `ERC20` 账单之前进入了这样的燃气费信息的时候, 需要拉取对应的 `ERC20` 的基本信息
		 */
		if (data.isFee && data.count == 0.0 && data.contract.isETHSeries()) {
			updateERC20FeeInfo(data) { symbol, count, error ->
				if (error.isNone()) launch(UI) {
					detailView.showHeaderData(TransactionHeaderModel(data, symbol!!, count!!))
				} else detailView.showErrorAlert(error)
			}
		}
	}

	private fun updateConfirmationNumber() {
		when {
			data.contract.isBTC() || data.contract.isBCH() ->
				updateBTCSeriesTransaction(false, data.contract.getChainURL().chainID)
			data.contract.isLTC() ->
				updateLTCTransaction(true, data.contract.getChainURL().chainID)
			data.contract.isETHSeries() -> updateETHSeriesConfirmationCount()
			data.contract.isEOSSeries() -> {
				detailView.showLoading(false)
			}
		}
	}

	private fun observerPendingTransaction() {
		when {
			data.contract.isBTCSeries() -> btcSeriesObserver.start()
			data.contract.isEOS() -> eosObserver.start()
			data.contract.isETHSeries() -> ethSeriesObserver.start()
		}
	}

	override fun removeObserver() {
		btcSeriesObserver.removeObserver()
		ethSeriesObserver.removeObserver()
		eosObserver.removeObserver()
	}

	private fun onTransferred(blockNumber: Int, totalCount: Int, isFailed: Boolean) {
		when {
			data.contract.isBTCSeries() -> onBTCSeriesTransferred(blockNumber)
			data.contract.isETHSeries() -> onETHSeriesTransferred(blockNumber, isFailed)
			data.contract.isEOSSeries() -> onEOSSeriesTransferred(blockNumber)
		}
		if (data.contract.isEOSSeries())
			detailView.showProgress(TransactionProgressModel(totalCount, totalCount.toLong()))
		else detailView.showProgress(TransactionProgressModel(totalCount))
		showTransactionInfo(blockNumber, totalCount)
		detailView.updateTokenDetailList()
		when (data) {
			is TransactionListModel ->
				detailView.showHeaderData(TransactionHeaderModel(data, false, isFailed))
			is ReceiptModel ->
				detailView.showHeaderData(TransactionHeaderModel(data, false, isFailed))
		}
	}

	private fun updateTransactionFromNotification() {
		when {
			// 从通知来的消息, 可能来自与任何支持的链, 这里需要解出 `Notification` 里面带入的 `ChainID` 作为参数
			data.contract.isBTC() || data.contract.isBCH() -> data.chainID?.let {
				updateBTCSeriesTransaction(true, it)
			}
			data.contract.isLTC() -> data.chainID?.let {
				updateLTCTransaction(true, it)
			}
			data.contract.isETHSeries() ->
				getAndShowETHSeriesTransactionFromNotification()
			data.contract.isEOSSeries() -> {
				// 目前不支持 `EOS` 的 `Notification`
			}
		}
	}

	private val btcSeriesObserver = object : BTCSeriesTransactionObserver() {
		override val chainURL = data.contract.getChainURL()
		override val hash = data.hash
		override fun getStatus(confirmed: Boolean, blockInterval: Int, blockNumber: Int) {
			if (confirmed) onTransferred(blockNumber, 6, false)
			else detailView.showProgress(TransactionProgressModel(blockInterval))
		}
	}

	private val ethSeriesObserver = object : ETHSeriesTransactionObserver() {
		override val chainID: String = data.contract.getCurrentChainID().id
		override val transactionHash = data.hash
		override fun getStatus(
			confirmed: Boolean,
			blockInterval: Int,
			blockNumber: Int,
			hasError: Boolean
		) {
			if (confirmed || hasError) onTransferred(blockNumber, 6, hasError)
			else detailView.showProgress(TransactionProgressModel(blockInterval))
		}
	}

	private val eosObserver = object : EOSTransactionObserver() {
		override val hash = data.hash
		@WorkerThread
		override fun getStatus(
			confirmed: Boolean,
			blockNumber: Int,
			confirmedCount: Int,
			totalCount: Int
		) {
			if (confirmed) onTransferred(blockNumber, totalCount, false)
			else detailView.showProgress(TransactionProgressModel(confirmedCount, totalCount.toLong()))
		}
	}

	private fun onBTCSeriesTransferred(blockNumber: Int) {
		// 交易过程中发生错误
		doAsync {
			val transactionDao =
				GoldStoneDataBase.database.btcSeriesTransactionDao()
			transactionDao.updateBlockNumber(
				blockNumber,
				data.hash,
				data.fromAddress,
				false
			)
		}
	}

	private fun onETHSeriesTransferred(blockNumber: Int, isFailed: Boolean) {
		// 交易过程中发生错误
		doAsync {
			val transactionDao =
				GoldStoneDataBase.database.transactionDao()
			if (isFailed) transactionDao.updateErrorStatus(
				TinyNumber.True.value.toString(),
				data.hash,
				data.fromAddress
			) else transactionDao.updateBlockNumber(
				blockNumber,
				data.hash,
				data.fromAddress,
				false
			)
		}
	}

	private fun onEOSSeriesTransferred(blockNumber: Int) {
		// 交易过程中发生错误
		doAsync {
			val transactionDao =
				GoldStoneDataBase.database.eosTransactionDao()
			// Update Database BlockNumber
			transactionDao.updateBlockNumberByTxID(data.hash, blockNumber, false)
			// 根据服务器计算 `ServerID` 的规则更新本地数 `PendingData` 数据
			EOSAPI.getTransactionServerID(
				blockNumber,
				data.hash,
				SharedAddress.getCurrentEOSAccount()
			) {
				if (it != null) transactionDao.updatePendingDataByTxID(data.hash, it)
			}
		}
	}

	private fun getAndShowChainMemo() = ETHSeriesTransactionUtils.getMemoFromChain(
		data.hash,
		data.isReceive,
		data.isFee,
		data.contract.getChainURL()
	) { memo, error ->
		launch {
			withContext(UI) {
				detailView.showLoading(false)
				if (memo != null && error.isNone())
					detailView.showMemo(TransactionDetailModel(memo, TransactionText.memo))
				else detailView.showErrorAlert(error)
			}
		}
	}

	private fun showTransactionInfo(blockNumber: Int, confirmations: Int) {
		val blockNumberText =
			if (blockNumber == -1) TransactionText.pendingBlockConfirmation
			else "$blockNumber "
		val confirmationsText = when {
			confirmations == -1 -> TransactionText.pendingBlockConfirmation
			data.contract.isEOSSeries() -> TransactionText.irreversible
			else -> "$confirmations "
		}
		detailView.showTransactionInformation(
			TransactionDetailModel(data.hash, TransactionText.transactionHash),
			TransactionDetailModel(blockNumberText, TransactionText.blockNumber),
			TransactionDetailModel(confirmationsText, TransactionText.confirmations),
			TransactionDetailModel(data.date, TransactionText.transactionDate)
		)
	}

	private fun getAndShowETHSeriesTransactionFromNotification() {
		ETHSeriesTransactionUtils.getTransactionByHash(
			data.hash,
			data.isReceive,
			data.chainID?.getChainURL()!!
		) { data, error ->
			if (data != null && error.isNone()) {
				showTransactionInfo(data.blockNumber, data.confirmations)
				if (data.memo.isEmpty()) getAndShowChainMemo()
				else {
					detailView.showMemo(TransactionDetailModel(data.memo, TransactionText.memo))
					detailView.showLoading(false)
				}
			} else detailView.showErrorAlert(error)
		}
	}

	private fun updateBTCSeriesTransaction(checkLocal: Boolean, chainID: ChainID) {
		BTCSeriesTransactionUtils.getTransaction(
			data.hash,
			data.isReceive,
			if (data.isReceive) data.toAddress else data.fromAddress,
			chainID.getThirdPartyURL(),
			checkLocal
		) { transaction, error ->
			if (transaction != null && error.isNone()) launch(UI) {
				showTransactionInfo(transaction.blockNumber, transaction.confirmations)
			} else detailView.showErrorAlert(error)
			launch(UI) {
				detailView.showLoading(false)
			}
		}
	}

	private fun updateLTCTransaction(checkLocal: Boolean, chainID: ChainID) {
		LTCTransactionUtils.getTransaction(
			data.hash,
			data.isReceive,
			if (data.isReceive) data.toAddress else data.fromAddress,
			chainID.getThirdPartyURL(),
			checkLocal
		) { transaction, error ->
			if (transaction != null && error.isNone()) launch(UI) {
				showTransactionInfo(transaction.blockNumber, transaction.confirmations)
			} else detailView.showErrorAlert(error)
			detailView.showLoading(false)
		}
	}

	private fun isFromNotification(): Boolean {
		return !data.isPending && data.confirmations == -1
	}

	private fun updateETHSeriesConfirmationCount() = getCurrentConfirmationNumber(
		data.blockNumber,
		data.contract.getChainURL()
	) { confirmationCount, error ->
		if (confirmationCount != null && error.isNone()) {
			val transactionDao =
				GoldStoneDataBase.database.transactionDao()
			transactionDao.updateConfirmationCount(
				confirmationCount,
				data.hash,
				data.fromAddress,
				data.isPending
			)
			launch(UI) {
				showTransactionInfo(data.blockNumber, confirmationCount)
			}
		} else detailView.showErrorAlert(error)
	}
}

