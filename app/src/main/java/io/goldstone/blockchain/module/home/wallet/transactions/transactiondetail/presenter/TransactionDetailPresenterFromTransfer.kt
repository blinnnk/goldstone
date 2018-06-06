package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter

import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionHeaderModel
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/6/6 4:26 PM
 * @author KaySaith
 */
fun TransactionDetailPresenter.updateHeaderValueFromTransferFragment() {
	data?.apply {
		currentHash = taxHash
		count = CryptoUtils.toCountByDecimal(value.toDouble(), token.decimal)
		fragment.asyncData = generateModels()
		observerTransaction()
		val headerData = TransactionHeaderModel(
			count,
			address,
			token.symbol,
			true
		)
		updateHeaderValue(headerData)
		headerModel = headerData
	}
}

// 从转账界面进入后, 自动监听交易完成后, 用来更新交易数据的工具方法
fun TransactionDetailPresenter.getTransactionFromChain() {
	GoldStoneEthCall.getTransactionByHash(
		currentHash,
		GoldStoneApp.getCurrentChain(),
		{
			// unfinish callback
		},
		{ error, reason ->
			fragment.context?.alert(reason ?: error.toString())
		}
	) {
		fragment.context?.runOnUiThread {
			fragment.asyncData?.clear()
			fragment.asyncData?.addAll(generateModels(it))
			fragment.recyclerView.adapter.notifyItemRangeChanged(1, 6)
		}
		// 成功获取数据后在异步线程更新数据库记录
		updateDataInDatabase(it.blockNumber)
	}
}

// 自动监听交易完成后, 将转账信息插入数据库
private fun TransactionDetailPresenter.updateDataInDatabase(blockNumber: String) {
	GoldStoneDataBase.database.transactionDao().apply {
		getTransactionByTaxHash(currentHash).let {
			it.forEach {
				update(it.apply {
					this.blockNumber = blockNumber
					isPending = false
					hasError = "0"
					txreceipt_status = "1"
				})
			}
		}
	}
}