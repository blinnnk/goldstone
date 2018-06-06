package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter

import com.blinnnk.extension.*
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.utils.TimeUtils
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.LoadingText
import io.goldstone.blockchain.common.value.TransactionText
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.crypto.InputCodeData
import io.goldstone.blockchain.crypto.toEthValue
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.network.EtherScanApi
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.presenter.NotificationTransactionInfo
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionDetailModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionHeaderModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.TransactionListModel
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/6/6 4:16 PM
 * @author KaySaith
 */
/**
 * 从通知中心进入的, 使用获取的 `Transaction` 转换成标准的使用格式, 这里临时填写
 * `Timestamp` 数字会在准备详情界面的时候获取时间戳, 见 [getTimestampAndInsertToDatabase]
 */
fun TransactionDetailPresenter.updateHeaderValueFromNotification() {
	/** 这个是从通知中心进入的, 通知中心的显示是现查账. */
	notificationData?.let { transaction ->
		currentHash = transaction.hash
		/**
		 * 查看本地数据库是否已经记录了这条交易, 这种情况存在于, 用户收到 push 并没有打开通知中心
		 * 而是打开了账单详情. 这条数据已经被存入本地. 这个时候通知中心就不必再从链上查询数据了.
		 */
		TransactionTable.getByHashAndReceivedStatus(
			transaction.hash,
			transaction.isReceived
		) { localTransaction ->
			if (localTransaction.isNull()) {
				// 如果本地没有数据从链上查询所有需要的数据
				fragment.apply {
					showLoadingView(LoadingText.transactionData)
					updateByNotificationHash(transaction) {
						removeLoadingView()
					}
				}
			} else {
				// 本地有数据直接展示本地数据
				localTransaction?.apply {
					fragment.asyncData = generateModels(TransactionListModel(localTransaction))
					val headerData = TransactionHeaderModel(
						value.toDouble(),
						fromAddress,
						symbol,
						false,
						isReceive,
						hasError == "1"
					)
					updateHeaderValue(headerData)
					headerModel = headerData
				}
			}
		}
	}
}

fun TransactionDetailPresenter.convertDataToAsynData(
	table: TransactionTable
): ArrayList<TransactionDetailModel> {
	return table.toAsyncData()
}

// 通过从 `notification` 计算后传入的值来完善 `token` 基础信息的方法
fun TransactionDetailPresenter.prepareHeaderValueFromNotification(
	receipt: TransactionTable,
	transaction: InputCodeData,
	isReceive: Boolean,
	chainID: String
) {
	DefaultTokenTable.getCurrentChainTokenByContract(receipt.to) {
		val address = if (isReceive) receipt.fromAddress else transaction.address
		it.isNull() isTrue {
			GoldStoneEthCall
				.getTokenSymbolAndDecimalByContract(
					receipt.to,
					chainID,
					{ error, reason ->
						fragment.context?.alert(reason ?: error.toString())
					}) { symbol, decimal ->
					val count = CryptoUtils.toCountByDecimal(transaction.count, decimal)
					updateHeaderValue(
						TransactionHeaderModel(
							count,
							address,
							symbol,
							false,
							isReceive
						)
					)
				}
		} otherwise {
			val count = CryptoUtils.toCountByDecimal(
				transaction.count,
				it?.decimals.orElse(0.0)
			)
			updateHeaderValue(
				TransactionHeaderModel(
					count,
					address,
					it?.symbol.orEmpty(),
					false,
					isReceive
				)
			)
		}
	}
}

fun TransactionDetailPresenter.updateByNotificationHash(
	info: NotificationTransactionInfo,
	callback: () -> Unit
) {
	GoldStoneEthCall.getTransactionByHash(
		currentHash,
		info.chainID,
		{
			// unfinished callback
		},
		{ error, reason ->
			fragment.context?.alert(reason ?: error.toString())
		}
	) { receipt ->
		receipt.getTimestampAndInsertToDatabase(fragment, info.chainID) { timestamp ->
			fragment.context?.runOnUiThread {
				if (fragment.asyncData.isNull()) {
					TransactionTable.updateMemoByHashAndReceiveStatus(
						info.hash,
						info.isReceived,
						info.chainID
					) { memo ->
						convertDataToAsynData(receipt).let {
							it[4].info = TimeUtils.formatDate(timestamp)
							it[1].info = memo
							fragment.asyncData = it
							updateHeaderFromNotification(receipt, info)
						}
					}
				}
				callback()
			}
		}
	}
}

/**
 * JSON RPC `GetTransactionByHash` 获取不到 `Timestamp` 需要从 `Transaction` 里面首先获取
 * `Block Hash` 然后再发起新的 `JSON RPC` 获取  `Block` 的 `TimeStamp` 来完善交易信息.
 */
private fun TransactionTable.getTimestampAndInsertToDatabase(
	fragment: TransactionDetailFragment,
	chainID: String = GoldStoneApp.getCurrentChain(),
	callback: (Long) -> Unit
) {
	GoldStoneEthCall.getBlockTimeStampByBlockHash(
		blockHash,
		chainID,
		{ error, reason ->
			fragment.context?.alert(reason ?: error.toString())
		}
	) {
		this.timeStamp = it.toString()
		callback(it)
	}
}

private fun TransactionDetailPresenter.updateHeaderFromNotification(
	receipt: TransactionTable,
	info: NotificationTransactionInfo
) {
	// 解析 `input code` 获取 `ERC20` 接收 `address`, 及接收 `count`
	val transactionInfo =
		CryptoUtils.loadTransferInfoFromInputData(receipt.input)
	
	CryptoUtils.isERC20TransferByInputCode(receipt.input) {
		transactionInfo?.let {
			prepareHeaderValueFromNotification(receipt, it, info.isReceived, info.chainID)
		}
	} isFalse {
		val count = CryptoUtils.toCountByDecimal(receipt.value.toDouble(), 18.0)
		updateHeaderValue(
			TransactionHeaderModel(
				count,
				if (info.isReceived) receipt.fromAddress else receipt.to,
				CryptoSymbol.eth,
				false,
				info.isReceived
			)
		)
	}
}

private fun TransactionTable.toAsyncData(): ArrayList<TransactionDetailModel> {
	val receiptData = arrayListOf(
		(gas.toBigDecimal() * gasPrice.toBigDecimal()).toDouble().toEthValue(),
		"There isn't a memo",
		hash,
		blockNumber,
		TimeUtils.formatDate(0),
		EtherScanApi.transactionsByHash(hash)
	)
	arrayListOf(
		TransactionText.minerFee,
		TransactionText.memo,
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