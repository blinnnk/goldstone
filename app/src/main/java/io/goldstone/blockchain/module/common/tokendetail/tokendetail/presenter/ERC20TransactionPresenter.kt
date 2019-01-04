package io.goldstone.blockchain.module.common.tokendetail.tokendetail.presenter

import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.kernel.commontable.TransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.RequisitionUtil
import io.goldstone.blockchain.kernel.network.ethereum.EtherScanApi
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.ERC20TransactionModel

/**
 * @date 2018/8/14 5:02 PM
 * @author KaySaith
 */

@WorkerThread
fun TokenDetailPresenter.loadERCChainData(blockNumber: Int) {
	// 本地数据库没有交易数据的话那就从链上获取交易数据进行筛选
	updateLocalERC20Transactions(blockNumber) {
		// 返回的是交易记录, 筛选当前的 `Symbol` 如果没有就返回空数组
		// 有数据后重新执行从数据库拉取数据
		if (it.isNone()) getETHSeriesData()
		else {
			if (it.isEmptyResult()) detailView.showLoading(false)
			detailView.showBottomLoading(false)
		}
	}
}

@WorkerThread
fun TokenDetailPresenter.updateLocalERC20Transactions(startBlock: Int, callback: (RequestError) -> Unit) {
	RequisitionUtil.requestUnCryptoData<ERC20TransactionModel>(
		EtherScanApi.getTargetTokenTransactions(
			SharedAddress.getCurrentEthereum(),
			token.contract.contract,
			startBlock
		),
		listOf("result")
	) { transactions, error ->
		when {
			transactions?.isNotEmpty() == true && error.isNone() -> {
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
			}
			transactions?.isEmpty() == true -> callback(RequestError.EmptyResut)
			else -> callback(error)
		}
	}
}