package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter

import com.blinnnk.extension.isNull
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.TimeUtils
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.ImportWalletText
import io.goldstone.blockchain.common.value.TransactionText
import io.goldstone.blockchain.crypto.utils.toEthValue
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.network.ChainURL
import io.goldstone.blockchain.kernel.network.EtherScanApi
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionDetailModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionHeaderModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.TransactionListModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.presenter.memoryTransactionListData

/**
 * @date 2018/6/6 3:59 PM
 * @author KaySaith
 */
fun TransactionDetailPresenter.updateDataFromTransactionList() {
	dataFromList?.apply {
		val headerData = TransactionHeaderModel(
			count,
			targetAddress,
			symbol,
			isPending,
			isReceived,
			hasError
		)
		headerModel = headerData
		currentHash = transactionHash
		fragment.showLoadingView("Loading data from chain")
		//  从 `EtherScan` 拉取账单列表的时候，并没有从链上获取未知 `Token` 的 `Name`, 这里需要额外判断补充一下.
		checkTokenNameInfoOrUpdate()
		
		TransactionTable.getMemoByHashAndReceiveStatus(
			currentHash,
			isReceived,
			getCurrentChainName()
		) { memo ->
			fragment.removeLoadingView()
			fragment.asyncData = generateModels(this).apply {
				this[1].info = memo
			}
			// 更新内存里面的数据
			memoryTransactionListData?.find {
				it.transactionHash == dataFromList?.transactionHash
			}?.memo = memo
			
			updateHeaderValue(headerData)
		}
		if (isPending) {
			// 异步从链上查一下这条 `taxHash` 是否有最新的状态变化
			observerTransaction()
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
		if (data?.memo.isNull()) TransactionText.noMemo
		else data?.memo
	var isReceive: Boolean? = null
	val receiptData = when (receipt) {
		is TransactionListModel -> {
			isReceive = receipt.isReceived
			arrayListOf(
				receipt.minerFee,
				receipt.memo,
				Config.getCurrentAddress(),
				receipt.transactionHash,
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
				Config.getCurrentAddress(),
				currentHash,
				receipt.blockNumber,
				date,
				EtherScanApi.transactionDetail(currentHash)
			)
		}
		
		else -> {
			arrayListOf(
				minerFee,
				memo,
				Config.getCurrentAddress(),
				currentHash,
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

private fun TransactionListModel.checkTokenNameInfoOrUpdate() {
	DefaultTokenTable.getCurrentChainTokenByContract(contract) {
		it?.apply {
			if (name.isEmpty()) {
				GoldStoneEthCall.getTokenName(
					contract,
					{ error, reason ->
						LogUtil.error("getCurrentChainTokenByContract $reason", error)
					},
					ChainURL.getChainNameBySymbol(symbol)
				) {
					DefaultTokenTable.updateTokenName(contract, it)
				}
			}
		}
	}
}