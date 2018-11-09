package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import com.blinnnk.extension.isNull
import com.blinnnk.extension.orZero
import io.goldstone.blockchain.common.utils.AddressUtils
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.BTCSeriesApiUtils
import io.goldstone.blockchain.kernel.network.litecoin.LitecoinApi
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

/**
 * @date 2018/8/14 12:13 PM
 * @author KaySaith
 */

// 因为翻页机制利用了 Insight 的特殊接口 `from/to` 所以没有额外增加
// `BlockInfo` 的备份接口
fun TokenDetailPresenter.loadLTCChainData(localDataMaxIndex: Int) {
	fragment.showLoadingView()
	val address = AddressUtils.getCurrentLTCAddress()
	LitecoinApi.getTransactionCount(address) { transactionCount, error ->
		if (transactionCount.isNull() || error.hasError()) return@getTransactionCount
		loadLitecoinTransactionsFromChain(
			address,
			localDataMaxIndex,
			transactionCount.orZero()
		) {
			launch(UI) {
				fragment.removeLoadingView()
			}
			loadDataFromDatabaseOrElse()
		}
	}
}

private fun loadLitecoinTransactionsFromChain(
	address: String,
	localDataMaxIndex: Int,
	transactionCount: Int,
	callback: (hasData: Boolean) -> Unit
) {
	val pageInfo = BTCSeriesApiUtils.getPageInfo(transactionCount, localDataMaxIndex)
	// 意味着网络没有更新的数据直接返回
	if (pageInfo.to == 0) callback(false)
	else LitecoinApi.getTransactions(address, pageInfo.from, pageInfo.to) { transactions, error ->
		// 转换数据格式
		if (transactions != null && error.isNone()) transactions.asSequence().mapIndexed { index, item ->
			BTCSeriesTransactionTable(
				item,
				pageInfo.maxDataIndex + index + 1,
				address,
				CoinSymbol.ltc,
				false,
				ChainType.LTC.id
			)
		}.toList().let { all ->
			val transactionDao =
				GoldStoneDataBase.database.btcSeriesTransactionDao()
			transactionDao.insertAll(all)
			// 同样的账单插入一份燃气费的数据
			transactionDao.insertAll(all.filterNot { it.isReceive }.map { it.apply { it.isFee = true } })
			callback(all.isNotEmpty())
		}
	}
}