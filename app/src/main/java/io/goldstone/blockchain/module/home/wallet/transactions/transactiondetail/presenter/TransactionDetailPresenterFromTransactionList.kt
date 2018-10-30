package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter

import com.blinnnk.extension.isNull
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.language.LoadingText
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.ethereum.GoldStoneEthCall
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionHeaderModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/6/6 3:59 PM
 * @author KaySaith
 */
fun TransactionDetailPresenter.updateDataFromTransactionList() {
	dataFromList?.apply {
		val headerData = TransactionHeaderModel(
			count,
			if (isReceived) fromAddress else toAddress,
			symbol,
			isPending,
			isReceived,
			hasError
		)
		headerModel = headerData
		currentHash = transactionHash
		fragment.showLoadingView(LoadingText.loadingDataFromChain)
		when {
			contract.isBTCSeries() -> dataFromList?.let {
				fragment.asyncData = generateModels(it).toArrayList()
				updateHeaderValue(headerData)
				fragment.removeLoadingView()
				if (isPending) when {
					contract.isBTC() -> observerBTCTransaction()
					contract.isBCH() -> observerBCHTransaction()
					else -> observerLTCTransaction()
				}
			}

			contract.isEOSSeries() -> dataFromList?.let {
				fragment.asyncData = generateModels(it).toArrayList()
				updateHeaderValue(headerData)
				fragment.removeLoadingView()
				if (isPending) observerEOSTransaction()
			}

			contract.isETC() -> {
				getETHERC20OrETCMemo(headerData)
				if (isPending) observerTransaction()
			}

			else -> {
				//  从 `EtherScan` 拉取账单列表的时候，并没有从链上获取
				// 未知 `Token` 的 `Name`, 这里需要额外判断补充一下.
				checkTokenNameInfoOrUpdate()
				getETHERC20OrETCMemo(headerData)
				if (isPending) observerTransaction()
			}
		}
	}
}

private fun TransactionDetailPresenter.getETHERC20OrETCMemo(headerData: TransactionHeaderModel) {
	dataFromList?.apply {
		TransactionTable.getMemoByHashAndReceiveStatus(
			currentHash,
			isReceived,
			if (contract.isETC()) SharedChain.getETCCurrent()
			else SharedChain.getCurrentETH()
		) { memo, error ->
			if (!memo.isNull() && error.isNone()) GoldStoneAPI.context.runOnUiThread {
				fragment.removeLoadingView()
				fragment.asyncData = generateModels(this).toArrayList().apply {
					this[1].info = memo!!
				}
				updateHeaderValue(headerData)
			}
		}
	}
}

private fun TransactionListModel.checkTokenNameInfoOrUpdate() {
	DefaultTokenTable.getCurrentChainToken(contract) { defaultToken ->
		if (defaultToken?.name?.isEmpty() == true) {
			GoldStoneEthCall.getTokenName(defaultToken.contract, contract.getChainURL()) { name, error ->
				if (!name.isNullOrEmpty() && error.isNone()) {
					DefaultTokenTable.updateTokenName(contract, name!!)
				}
			}
		}
	}
}