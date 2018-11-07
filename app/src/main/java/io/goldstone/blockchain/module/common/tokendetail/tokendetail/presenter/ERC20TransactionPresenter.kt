package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.RequisitionUtil
import io.goldstone.blockchain.kernel.network.ethereum.EtherScanApi
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.ERC20TransactionModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * @date 2018/8/14 5:02 PM
 * @author KaySaith
 */

fun TokenDetailPresenter.loadERCChainData(localERCData: List<TransactionListModel>) {
	fragment.showLoadingView()
	doAsync {
		val startBlockNumber = localERCData.maxBy { it.blockNumber }?.blockNumber ?: "0"
		// 本地数据库没有交易数据的话那就从链上获取交易数据进行筛选
		updateTargetLocalERC20Transactions(startBlockNumber) {
			// 返回的是交易记录, 筛选当前的 `Symbol` 如果没有就返回空数组
			// 有数据后重新执行从数据库拉取数据
			if (it.isNone()) loadDataFromDatabaseOrElse()
			uiThread { fragment.removeLoadingView() }
		}
	}
}

fun TokenDetailPresenter.updateTargetLocalERC20Transactions(
	startBlock: String,
	@WorkerThread callback: (RequestError) -> Unit
) {
	RequisitionUtil.requestUnCryptoData<ERC20TransactionModel>(
		EtherScanApi.getTargetTokenTransactions(
			SharedAddress.getCurrentEthereum(),
			token?.contract?.contract.orEmpty(),
			startBlock
		),
		"result"
	) { transactions, error ->
		if (transactions?.isNotEmpty() == true && error.isNone()) {
			val defaultDao =
				GoldStoneDataBase.database.defaultTokenDao()
			val transactionDao =
				GoldStoneDataBase.database.transactionDao()
			// onConflict InsertAll 利用 RoomDatabase 进行双主键做重复判断 
			// 覆盖或新增到本地 TransactionTable 数据库里
			transactionDao.insertAll(transactions.map { TransactionTable(it) })
			// 检测获取的 ERC20 本地是否有对应的 DefaultToken 记录, 如果没有插入到本地数据库
			// 供其他场景使用
			val chainID = SharedChain.getCurrentETH().chainID
			val token = transactions.first()
			val target = defaultDao.getToken(
				token.contract,
				token.tokenSymbol,
				chainID.id
			)
			if (target.isNull()) defaultDao.insert(DefaultTokenTable(token, chainID))
			callback(error)
		} else callback(error)
	}
}