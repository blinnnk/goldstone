package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import android.support.annotation.WorkerThread
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
	val account = Config.getCurrentEOSAccount()
	EOSAPI.getTransactionsLastIndex(
		account,
		{ LogUtil.error("loadEOSChainData", it) }
	) { transactionCount ->
		loadTransactionsFromChain(
			account.accountName,
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
	@WorkerThread successCallback: (hasData: Boolean) -> Unit
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
		/**
		 * @Important
		 * 通过 `EOS History Chain` 获取的转账历史数据会出现重复的情况, 具体原因还不详
		 * 这里先在本地做去重复处理, 但是问题依旧, 并且拉取重复的数据很消耗流量.
		 * 需要时刻跟进随时更改这里的实现.
		 * */
		// Calculate All Inputs to get transfer value
		val newData = transactionList.asSequence().filterNot {
			// 某些非交易类的行为会在多条重复的 Action 中夹杂 From 和 To 的值为空的数据跳步
			// 在去重复处理的时候有的时候会被保留, 这里首先移除这种情况的数据
			it.transactionData.fromName.isEmpty() || it.transactionData.toName.isEmpty()
		}.distinctBy {
			it.txID
		}.map {
			// 插入转账数据到数据库
			EOSTransactionTable.preventDuplicateInsert(accountName, it)
		}.toList()
		successCallback(newData.isNotEmpty())
	}
}