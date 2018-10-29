package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.language.LoadingText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
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
	callback: (GoldStoneError) -> Unit
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
	callback: (error: RequestError) -> Unit
) {
	GoldStoneAPI.getETCTransactions(
		SharedChain.getETCCurrent().chainID,
		SharedAddress.getCurrentETC(),
		blockNumber
	) { newData, error ->
		if (!newData.isNull() && error.isNone()) {
			// 插入数据库的抽象方法
			fun insertDataToDataBase(data: List<ETCTransactionModel>) {
				// 生成最终的数据格式
				data.map {
					// 加工数据并存如数据库
					TransactionTable(it).apply {
						GoldStoneDataBase.database.transactionDao().insert(this)
					}
				}
			}
			if (newData!!.isNotEmpty()) {
				val finalNewData = newData.filterNot { new ->
					// 和本地数据去重处理
					localData.any {
						it.transactionHash.equals(new.hash, true)
					}
				}
				insertDataToDataBase(finalNewData)
				// Copy 出燃气费的部分
				val feeData = finalNewData.filter {
					it.from.equals(SharedAddress.getCurrentETC(), true)
				}.apply {
					forEach { it.isFee = true }
				}
				insertDataToDataBase(feeData)
			}
			GoldStoneAPI.context.runOnUiThread { callback(error) }
		} else GoldStoneAPI.context.runOnUiThread { callback(error) }
	}
}