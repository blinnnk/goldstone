package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter

import android.support.annotation.WorkerThread
import com.blinnnk.extension.getChildFragment
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.common.tokendetail.tokendetailcenter.view.TokenDetailCenterFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionHeaderModel
import org.jetbrains.anko.runOnUiThread


/**
 * @author KaySaith
 * @date  2018/09/16
 */

/** ———————————— 这里是从转账完成后跳入的账单详情界面用到的数据 ————————————*/
fun TransactionDetailPresenter.observerEOSTransaction() {
	object : EOSTransactionObserver() {
		override val hash = currentHash
		@WorkerThread
		override fun getStatus(confirmed: Boolean, blockInterval: Int, blockNumber: Int) {
			// Update Database BlockNumber
			GoldStoneDataBase.database.eosTransactionDao()
				.updateBlockNumberByTxID(currentHash, blockNumber)
			if (confirmed)
				GoldStoneDataBase.database.eosTransactionDao().updatePendingStatusByTxID(currentHash)
			GoldStoneAPI.context.runOnUiThread {
				if (confirmed) {
					onEOSTransactionSucceed(blockNumber)
					updateConformationBarFinished()
				} else showConformationInterval(blockInterval)
			}
		}
	}.start()
}

/**
 * 当 `Transaction` 监听到自身发起的交易的时候执行这个函数, 关闭监听以及执行动作
 */
private fun TransactionDetailPresenter.onEOSTransactionSucceed(blockNumber: Int) {
	data?.apply {
		updateHeaderValue(
			TransactionHeaderModel(
				count,
				toAddress,
				token.symbol,
				false,
				false,
				false
			)
		)
		updateBlockNumberInUI(blockNumber)
	}

	dataFromList?.apply {
		updateHeaderValue(
			TransactionHeaderModel(
				count,
				addressName,
				symbol,
				false,
				false,
				false
			)
		)
		updateBlockNumberInUI(blockNumber)
	}
}

private fun TransactionDetailPresenter.updateBlockNumberInUI(blockNumber: Int) {
	val data = fragment.asyncData?.mapIndexed { index, model ->
		if (index == 5) model.apply { info = "$blockNumber" }
		else model
	}!!
	fragment.asyncData?.clear()
	fragment.asyncData?.addAll(data)
	fragment.recyclerView.adapter?.notifyItemRangeChanged(1, data.size)
	fragment.parentFragment
		?.getChildFragment<TokenDetailCenterFragment>()
		?.presenter?.refreshTransactionListFromDatabase()
}