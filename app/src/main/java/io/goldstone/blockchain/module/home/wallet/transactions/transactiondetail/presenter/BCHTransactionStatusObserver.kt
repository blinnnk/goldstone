package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter

import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.bitcoincash.BitcoinCashApi
import io.goldstone.blockchain.kernel.network.bitcoincash.BitcoinCashUrl
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionHeaderModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/8/15 1:38 PM
 * @author KaySaith
 */

private var hasBlockNumber = false

/** ———————————— 这里是从转账完成后跳入的账单详情界面用到的数据 ————————————*/
fun TransactionDetailPresenter.observerBCHTransaction() {
	// 在页面销毁后需要用到, `activity` 所以提前存储起来
	object : BTCSeriesTransactionStatusObserver() {
		override val chainName = SharedChain.getBCHCurrentName()
		override val hash = currentHash
		override fun getStatus(confirmed: Boolean, blockInterval: Int) {
			if (confirmed) {
				doAsync {
					GoldStoneDataBase.database.btcSeriesTransactionDao().updatePendingStatus(hash)
				}
				onBCHTransactionSucceed()
				updateConformationBarFinished()
				updateTokenDetailPendingStatus()
			} else {
				if (!hasBlockNumber) {
					// 更新 `BlockNumber` 及时间信息, 并未完全完成 `Pending` 状态
					getBCHTransactionFromChain(true)
					hasBlockNumber = true
				}
				showConformationInterval(blockInterval)
			}
		}
	}.start()
}

/**
 * 当 `Transaction` 监听到自身发起的交易的时候执行这个函数, 关闭监听以及执行动作
 */
private fun TransactionDetailPresenter.onBCHTransactionSucceed() {
	val address = data?.toAddress ?: dataFromList?.addressName ?: ""
	val symbol = getUnitSymbol()
	updateHeaderValue(
		TransactionHeaderModel(
			count,
			address,
			symbol,
			false,
			false,
			false
		)
	)
	getBCHTransactionFromChain(false)
}

// 从转账界面进入后, 自动监听交易完成后, 用来更新交易数据的工具方法
private fun TransactionDetailPresenter.getBCHTransactionFromChain(
	isPending: Boolean
) {
	val address =
		if (SharedValue.isTestEnvironment()) SharedAddress.getCurrentBTCSeriesTest()
		else SharedAddress.getCurrentBCH()
	BitcoinCashApi.getTransactionByHash(
		currentHash,
		address,
		BitcoinCashUrl.currentUrl(),
		{
			fragment.context?.alert(it.toString())
		}
	) { transaction ->
		GoldStoneAPI.context.runOnUiThread {
			fragment.asyncData?.clear()
			val data = generateModels(transaction)
			fragment.asyncData?.addAll(data)
			fragment.recyclerView.adapter?.notifyItemRangeChanged(1, data.size)
		}
		// Update Database
		transaction?.let {
			// 更新本地的燃气费记录以及转账记录的相关信息
			BTCSeriesTransactionTable.updateLocalDataByHash(currentHash, it, false, isPending)
			BTCSeriesTransactionTable.updateLocalDataByHash(currentHash, it, true, isPending)
		}
	}
}