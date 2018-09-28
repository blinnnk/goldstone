package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import io.goldstone.blockchain.common.language.LoadingText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.ETCTransactionModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/8/14 5:01 PM
 * @author KaySaith
 */

fun TokenDetailPresenter.loadETCChainData(localData: List<TransactionListModel>) {
	fragment.showLoadingView(LoadingText.transactionData)
	getETCTransactionsFromChain(localData) {
		fragment.removeLoadingView()
		loadDataFromDatabaseOrElse()
	}
}

fun TokenDetailPresenter.getETCTransactionsFromChain(
	localData: List<TransactionListModel>,
	callback: () -> Unit
) {
	doAsync {
		val blockNumber = localData.maxBy {
			it.blockNumber
		}?.blockNumber ?: "0"
		loadDataFromChain(blockNumber, localData, callback)
	}
}

private fun loadDataFromChain(
	blockNumber: String,
	localData: List<TransactionListModel>,
	callback: () -> Unit
) {
	GoldStoneAPI.getETCTransactions(
		SharedChain.getETCCurrent(),
		SharedAddress.getCurrentETC(),
		blockNumber,
		{
			LogUtil.error("loadDataFromChain", it)
		}
	) { newData ->
		// 插入数据库的抽象方法
		fun List<ETCTransactionModel>.insertDataToDataBase() {
			// 生成最终的数据格式
			map {
				// 加工数据并存如数据库
				TransactionTable(it).apply {
					GoldStoneDataBase.database.transactionDao().insert(this)
				}
			}
		}

		if (newData.isNotEmpty()) {
			val finalNewData = newData.filterNot { new ->
				// 和本地数据去重处理
				localData.any {
					it.transactionHash.equals(new.hash, true)
				}
			}
			finalNewData.insertDataToDataBase()
			// Copy 出燃气费的部分
			val feeData = finalNewData.filter {
				it.from.equals(SharedAddress.getCurrentETC(), true)
			}.apply {
				forEach { it.isFee = true }
			}

			feeData.insertDataToDataBase()

			GoldStoneAPI.context.runOnUiThread {
				callback()
			}
		} else {
			// 数据为空返回
			GoldStoneAPI.context.runOnUiThread {
				callback()
			}
		}
	}
}