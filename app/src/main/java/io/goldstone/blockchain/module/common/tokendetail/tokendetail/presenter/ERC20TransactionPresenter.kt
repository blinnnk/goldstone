package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/8/14 5:02 PM
 * @author KaySaith
 */

fun TokenDetailPresenter.loadERCChainData(localERCData: List<TransactionListModel>) {
	doAsync {
		val startBlockNumber = localERCData.maxBy { it.blockNumber }?.blockNumber ?: "0"
		// 本地数据库没有交易数据的话那就从链上获取交易数据进行筛选
		getTokenTransactions(
			startBlockNumber,
			{
				// ToDo 等自定义的 `Alert` 完成后应当友好提示
				LogUtil.error("error in getTransactionDataFromEtherScan $it")
			}
		) { transactionListModel ->
			// 返回的是交易记录, 筛选当前的 `Symbol` 如果没有就返回空数组
			transactionListModel.find { it.contract == token?.contract }.isNotNull {
				// 有数据后重新执行从数据库拉取数据
				loadDataFromDatabaseOrElse()
			} otherwise {
				GoldStoneAPI.context.runOnUiThread {
					// 链上和本地都没有数据就更新一个空数组作为默认
					fragment.updatePageBy(arrayListOf(), Config.getCurrentEthereumAddress())
					fragment.removeLoadingView()
				}
			}
		}
	}
}