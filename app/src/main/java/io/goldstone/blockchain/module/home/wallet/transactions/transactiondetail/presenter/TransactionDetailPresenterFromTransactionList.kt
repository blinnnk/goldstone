package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter

import com.blinnnk.extension.isNull
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.TimeUtils
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.ImportWalletText
import io.goldstone.blockchain.common.value.TransactionText
import io.goldstone.blockchain.crypto.toEthValue
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.network.EtherScanApi
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionDetailModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionHeaderModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.TransactionListModel
import org.jetbrains.anko.doAsync

/**
 * @date 2018/6/6 3:59 PM
 * @author KaySaith
 */
fun TransactionDetailPresenter.updateHeaderValueFromTransactionList() {
	dataFromList?.apply {
		val headerData = TransactionHeaderModel(
			count,
			targetAddress,
			symbol,
			isPending,
			isReceived,
			hasError
		)
		updateHeaderValue(headerData)
		headerModel = headerData
		currentHash = transactionHash
		
		if (memo.isEmpty() && !isPending) {
			fragment.showLoadingView("Load transaction detail information")
			TransactionTable.updateMemoByHashAndReceiveStatus(transactionHash, isReceived) {
				fragment.asyncData = generateModels(this.apply { memo = it })
				updateHeaderValue(headerData)
				fragment.removeLoadingView()
			}
		} else {
			fragment.asyncData = generateModels(this)
			updateHeaderValue(headerData)
		}
		
		if (isPending) {
			// 异步从链上查一下这条 `taxHash` 是否有最新的状态变化
			observerTransaction()
		}
	}
}

fun TransactionDetailPresenter.saveInputCodeByTaxHash(
	taxHash: String,
	callback: (input: String, isETHTransfer: Boolean) -> Unit
) {
	doAsync {
		TransactionTable.getTransactionByHash(taxHash) {
			it.find { it.hash == taxHash }?.let { transaction ->
				if (transaction.input.isEmpty()) {
					GoldStoneEthCall.getInputCodeByHash(
						taxHash,
						Config.getCurrentChain(), { error, reason ->
							LogUtil.error("saveInputCodeByTaxHash $reason", error)
						}) {
						TransactionTable.updateInputCodeByHash(taxHash, it) {
							callback(it, transaction.isERC20)
						}
					}
				}
			}
		}
	}
}

// 根据传入转账信息类型, 来生成对应的更新界面的数据
fun TransactionDetailPresenter.generateModels(
	receipt: Any? = null
): ArrayList<TransactionDetailModel> {
	val minerFee =
		if (data.isNull()) dataFromList?.minerFee
		else (data!!.gasLimit * data!!.gasPrice).toDouble().toEthValue()
	val date =
		if (data.isNull()) dataFromList?.date
		else TimeUtils.formatDate(data!!.timestamp / 1000)
	val memo =
		if (data?.memo.isNull()) "There isn't a memo"
		else data?.memo
	var isReceive: Boolean? = null
	val receiptData = when (receipt) {
		is TransactionListModel -> {
			isReceive = receipt.isReceived
			arrayListOf(
				receipt.minerFee,
				receipt.memo,
				WalletTable.current.address.toUpperCase(),
				receipt.transactionHash.toUpperCase(),
				receipt.blockNumber,
				receipt.date,
				receipt.url
			)
		}
		
		is TransactionTable -> {
			isReceive = receipt.isReceive
			arrayListOf(
				minerFee,
				memo,
				WalletTable.current.address.toUpperCase(),
				currentHash.toUpperCase(),
				receipt.blockNumber,
				date,
				EtherScanApi.transactionDetail(currentHash)
			)
		}
		
		else -> {
			arrayListOf(
				minerFee,
				memo,
				WalletTable.current.address.toUpperCase(),
				currentHash.toUpperCase(),
				"Waiting...",
				date,
				EtherScanApi.transactionDetail(currentHash)
			)
		}
	}
	arrayListOf(
		TransactionText.minerFee,
		TransactionText.memo,
		(if (isReceive == true) CommonText.to else CommonText.from) + " " + ImportWalletText.address,
		TransactionText.transactionHash,
		TransactionText.blockNumber,
		TransactionText.transactionDate,
		TransactionText.url
	).mapIndexed { index, it ->
		TransactionDetailModel(receiptData[index].toString(), it)
	}.let {
		return it.toArrayList()
	}
}