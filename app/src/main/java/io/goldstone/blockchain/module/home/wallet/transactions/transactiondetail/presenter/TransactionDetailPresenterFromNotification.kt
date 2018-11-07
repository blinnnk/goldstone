package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter

import android.support.annotation.UiThread
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orElse
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.TransactionText
import io.goldstone.blockchain.common.utils.TimeUtils
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.utils.toUnitValue
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.bitcoin.BitcoinApi
import io.goldstone.blockchain.kernel.network.bitcoincash.BitcoinCashApi
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.ethereum.GoldStoneEthCall
import io.goldstone.blockchain.kernel.network.litecoin.LitecoinApi
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationTransactionInfo
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionDetailModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionHeaderModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailAdapter
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.getMemoFromInputCode
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/6/6 4:16 PM
 * @author KaySaith
 */
fun TransactionDetailPresenter.updateDataFromNotification() {
	/** 这个是从通知中心进入的, 通知中心的显示是现查账. */
	notificationData?.let { transaction ->
		currentHash = transaction.hash
		/**
		 * 查看本地数据库是否已经记录了这条交易, 这种情况存在于, 用户收到 push 并没有打开通知中心
		 * 而是打开了账单详情. 这条数据已经被存入本地. 这个时候通知中心就不必再从链上查询数据了.
		 */
		when {
			ChainID(transaction.chainID).isBTCSeries() ->
				getBitcoinSeriesTransaction(transaction)
			else -> getETHERC20OrETCTransaction(transaction)
		}
	}
}

fun TransactionDetailPresenter.getETHERC20OrETCTransaction(notification: NotificationTransactionInfo) {
	launch {
		withContext(CommonPool, CoroutineStart.LAZY) {
			val transactionDao = GoldStoneDataBase.database.transactionDao()
			// 如果本地没有数据从链上查询所有需要的数据
			val transaction =
				transactionDao.getByTaxHashAndReceivedStatus(
					notification.hash,
					notification.isReceived,
					false
				)
			withContext(UI) {
				transaction?.apply {
					fragment.asyncData = generateModels(TransactionListModel(this)).toArrayList()
					val headerData = TransactionHeaderModel(
						notificationData?.value ?: value.toDouble(),
						if (isReceive) fromAddress else to,
						symbol,
						false,
						isReceive,
						hasError == "1"
					)
					updateHeaderValue(headerData)
					headerModel = headerData
				} ?: fragment.apply {
					showLoadingView()
					updateByNotificationHash(notification) {
						removeLoadingView()
					}
				}
			}
		}
	}
}

// 通过从 `notification` 计算后传入的值来完善 `token` 基础信息的方法
fun TransactionDetailPresenter.prepareHeaderValueFromNotification(
	address: String,
	value: Double,
	isReceive: Boolean
) {
	updateHeaderValue(
		TransactionHeaderModel(
			value,
			address,
			notificationData?.symbol.orEmpty(),
			false,
			isReceive
		)
	)
}

fun TransactionDetailPresenter.updateByNotificationHash(
	info: NotificationTransactionInfo,
	@UiThread callback: (RequestError) -> Unit
) {
	GoldStoneEthCall.getTransactionByHash(
		currentHash,
		ChainID(info.chainID).getChainURL()!!
	) { receipt, error ->
		if (receipt != null && error.hasError()) receipt.apply {
			// 通过 `Notification` 获取确实信息
			this.recordOwnerAddress = if (info.isReceived) info.toAddress else info.fromAddress
			this.symbol = notificationData?.symbol.orEmpty()
			this.timeStamp = info.timeStamp.toString()
			this.isReceive = info.isReceived
			this.memo = getMemoFromInputCode(
				receipt.input,
				TokenContract(receipt.contractAddress, receipt.symbol, null).isERC20Token()
			)
			this.fromAddress = info.fromAddress
		}.let {
			fragment.context?.runOnUiThread {
				if (fragment.asyncData.isNull()) fragment.asyncData = it.toAsyncData()
				else fragment.presenter.diffAndUpdateAdapterData<TransactionDetailAdapter>(it.toAsyncData())
				updateHeaderFromNotification(info)
				callback(RequestError.None)
			}
		} else GoldStoneAPI.context.runOnUiThread {
			callback(error)
		}
	}
}

fun TransactionDetailPresenter.getBitcoinSeriesTransaction(info: NotificationTransactionInfo) {
	BTCSeriesTransactionTable.getTransactionsByHash(currentHash, info.isReceived) { localTransaction ->
		if (localTransaction != null && localTransaction.blockNumber.toIntOrNull() != null) {
			fragment.asyncData = generateModels(TransactionListModel(localTransaction)).toArrayList()
			val headerData = TransactionHeaderModel(
				notificationData?.value ?: localTransaction.value.toDouble(),
				if (localTransaction.isReceive) localTransaction.fromAddress else localTransaction.to,
				localTransaction.symbol,
				false,
				localTransaction.isReceive,
				false
			)
			updateHeaderValue(headerData)
			headerModel = headerData
		} else {
			fragment.showLoadingView()
			when {
				CoinSymbol(info.symbol).isBTC() ->
					updateBTCTransactionByNotificationHash(info) {
						fragment.removeLoadingView()
					}
				CoinSymbol(info.symbol).isLTC() ->
					updateLTCTransactionByNotificationHash(info) {
						fragment.removeLoadingView()
					}
				CoinSymbol(info.symbol).isBCH() -> {
					updateBCHTransactionByNotificationHash(info) {
						fragment.removeLoadingView()
					}
				}
			}
		}
	}
}

/**
 * 通知中心的拉取 是混合主网和测试网的状态, 所以这里需要通过 `ChainID` 来决定
 * 是从主网请求数据还是测试网络请求数据
 */
fun TransactionDetailPresenter.updateBTCTransactionByNotificationHash(
	info: NotificationTransactionInfo,
	callback: () -> Unit
) {
	BitcoinApi.getTransactionByHash(
		currentHash,
		info.fromAddress,
		ChainID(info.chainID).getThirdPartyURL()
	) { receipt, error ->
		if (receipt.isNull() || error.hasError()) return@getTransactionByHash
		// 通过 `Notification` 获取确实信息
		receipt?.apply {
			this.symbol = notificationData?.symbol.orEmpty()
			this.timeStamp = info.timeStamp.toString()
			this.isReceive = info.isReceived
		}?.toAsyncData()?.let {
			fragment.context?.runOnUiThread {
				if (fragment.asyncData.isNull()) fragment.asyncData = it
				else fragment.presenter.diffAndUpdateAdapterData<TransactionDetailAdapter>(it)
				updateHeaderFromNotification(info)
				callback()
			}
		}
	}
}

fun TransactionDetailPresenter.updateLTCTransactionByNotificationHash(
	info: NotificationTransactionInfo,
	@UiThread callback: () -> Unit
) {
	LitecoinApi.getTransactionByHash(
		currentHash,
		info.fromAddress,
		ChainID(info.chainID).getThirdPartyURL()
	) { receipt, error ->
		if (receipt == null || error.hasError()) return@getTransactionByHash
		receipt.apply {
			// 通过 `Notification` 获取确实信息
			this.symbol = notificationData?.symbol.orEmpty()
			this.timeStamp = info.timeStamp.toString()
			this.isReceive = info.isReceived
		}.toAsyncData().let {
			fragment.context?.runOnUiThread {
				if (fragment.asyncData.isNull()) fragment.asyncData = it
				else fragment.presenter.diffAndUpdateAdapterData<TransactionDetailAdapter>(it)
				updateHeaderFromNotification(info)
				callback()
			}
		}
	}
}

fun TransactionDetailPresenter.updateBCHTransactionByNotificationHash(
	info: NotificationTransactionInfo,
	callback: () -> Unit
) {
	BitcoinCashApi.getTransactionByHash(
		currentHash,
		info.fromAddress,
		ChainID(info.chainID).getThirdPartyURL()
	) { receipt, error ->
		if (receipt.isNull() || error.hasError()) return@getTransactionByHash
		// 通过 `Notification` 获取确实信息
		receipt?.apply {
			this.symbol = notificationData?.symbol.orEmpty()
			this.timeStamp = info.timeStamp.toString()
			this.isReceive = info.isReceived
		}?.toAsyncData()?.let {
			fragment.context?.runOnUiThread {
				if (fragment.asyncData.isNull()) fragment.asyncData = it
				else fragment.presenter.diffAndUpdateAdapterData<TransactionDetailAdapter>(it)
				updateHeaderFromNotification(info)
				callback()
			}
		}
	}
}

private fun TransactionDetailPresenter.updateHeaderFromNotification(info: NotificationTransactionInfo) {
	prepareHeaderValueFromNotification(
		if (info.isReceived) info.fromAddress else info.toAddress,
		notificationData?.value.orElse(0.0),
		info.isReceived
	)
}

private fun TransactionTable.toAsyncData(): ArrayList<TransactionDetailModel> {
	val receiptData = arrayListOf(
		(gas.toBigDecimal() * gasPrice.toBigDecimal()).toDouble().toUnitValue(
			if (TokenContract(contractAddress, symbol, null).isETC()) CoinSymbol.etc
			else CoinSymbol.eth
		),
		if (memo.isEmpty()) TransactionText.noMemo else memo,
		fromAddress,
		to,
		hash,
		blockNumber,
		TimeUtils.formatDate(timeStamp),
		TransactionListModel.generateTransactionURL(
			hash,
			symbol,
			EOSAccount(fromAddress).isValid(false)
		)
	)
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
		TransactionDetailModel(receiptData[index], it)
	}.let {
		return it.toArrayList()
	}
}

private fun BTCSeriesTransactionTable.toAsyncData(): ArrayList<TransactionDetailModel> {
	val receiptData = arrayListOf(
		"${fee.toDouble().toBigDecimal().toPlainString()} $symbol",
		fromAddress,
		TransactionListModel.formatToAddress(to),
		hash,
		blockNumber,
		TimeUtils.formatDate(timeStamp),
		TransactionListModel.generateTransactionURL(hash, symbol, false)
	)
	arrayListOf(
		TransactionText.minerFee,
		CommonText.from,
		CommonText.to,
		TransactionText.transactionHash,
		TransactionText.blockNumber,
		TransactionText.transactionDate,
		TransactionText.url
	).mapIndexed { index, it ->
		TransactionDetailModel(receiptData[index], it)
	}.let {
		return it.toArrayList()
	}
}