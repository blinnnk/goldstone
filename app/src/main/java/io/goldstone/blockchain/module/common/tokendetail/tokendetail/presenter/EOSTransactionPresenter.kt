package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import com.blinnnk.extension.orZero
import io.goldstone.blockchain.common.language.LoadingText
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.kernel.commonmodel.eos.EOSTransactionTable
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import org.jetbrains.anko.runOnUiThread


/**
 * @author KaySaith
 * @date  2018/09/13
 */

fun TokenDetailPresenter.loadEOSDataFromChain(localMaxIndex: Int) {
	fragment.showLoadingView(LoadingText.transactionData)
	val accountName = Config.getCurrentEOSName()
	EOSAPI.getTransactionsLastIndex(
		accountName,
		{
			LogUtil.error("loadEOSChainData", it)
		}
	) { transactionCount ->
		loadTransactionsFromChain(
			accountName,
			localMaxIndex,
			transactionCount.orZero(),
			{
				LogUtil.error("EOS loadTransactionsFromChain", it)
			}
		) {
			fragment.context?.runOnUiThread {
				fragment.removeLoadingView()
			}
			loadDataFromDatabaseOrElse()
		}
	}
}

private fun loadTransactionsFromChain(
	accountName: String,
	localDataMaxIndex: Int,
	transactionCount: Int,
	errorCallback: (Throwable) -> Unit,
	successCallback: (hasData: Boolean) -> Unit
) {
	val pageInfo = EOSAPI.getPageInfo(localDataMaxIndex, transactionCount)
	// 意味着网络没有更新的数据直接返回
	if (pageInfo.to == 0) {
		successCallback(false)
		return
	}
	EOSAPI.getAccountTransactionHistory(
		accountName,
		pageInfo.from,
		pageInfo.to,
		errorCallback
	) { transactionList ->
		// Calculate All Inputs to get transfer value
		successCallback(transactionList.map {
			// 插入转账数据到数据库
			EOSTransactionTable.preventDuplicateInsert(accountName, it)
		}.isNotEmpty())
	}
}