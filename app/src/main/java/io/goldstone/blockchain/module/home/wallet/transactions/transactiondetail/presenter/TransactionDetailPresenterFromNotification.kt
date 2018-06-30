package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter

import com.blinnnk.extension.isNull
import com.blinnnk.extension.orElse
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.utils.TimeUtils
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.ChainID
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.common.value.LoadingText
import io.goldstone.blockchain.common.value.TransactionText
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.crypto.utils.toUnitValue
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.presenter.NotificationTransactionInfo
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionDetailModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionHeaderModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailAdapter
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.getMemoFromInputCode
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
					fragment.asyncData = generateModels(TransactionListModel(this))
					val headerData = TransactionHeaderModel(
						notificationData?.value ?: value.toDouble(),
						if (isReceive) fromAddress else tokenReceiveAddress ?: to,
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
	callback: () -> Unit
) {
	GoldStoneEthCall.getTransactionByHash(
		currentHash,
		ChainID.getChainNameByID(info.chainID),
		errorCallback = { error, reason ->
			fragment.context?.alert(reason ?: error.toString())
		}
	) { receipt ->
		// 通过 `Notification` 获取确实信息
		receipt.apply {
			this.recordOwnerAddress = if (info.isReceived) info.toAddress else info.fromAddress
			this.symbol = notificationData?.symbol.orEmpty()
			this.timeStamp = info.timeStamp.toString()
			this.isReceive = info.isReceived
			this.memo = getMemoFromInputCode(receipt.input, CryptoValue.isToken(receipt.contractAddress))
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

private fun TransactionDetailPresenter.updateHeaderFromNotification(
	info: NotificationTransactionInfo
) {
	prepareHeaderValueFromNotification(
		if (info.isReceived) info.fromAddress else info.toAddress,
		notificationData?.value.orElse(0.0),
		info.isReceived
	)
}

private fun TransactionTable.toAsyncData(): ArrayList<TransactionDetailModel> {
	val receiptData = arrayListOf(
		(gas.toBigDecimal() * gasPrice.toBigDecimal())
			.toDouble()
			.toUnitValue(
				if (symbol.equals(CryptoSymbol.etc, true)) CryptoSymbol.etc
				else CryptoSymbol.eth
			),
		if (memo.isEmpty()) TransactionText.noMemo else memo,
		fromAddress,
		to,
		hash,
		blockNumber,
		TimeUtils.formatDate(timeStamp),
		TransactionListModel.generateTransactionURL(hash, symbol)
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