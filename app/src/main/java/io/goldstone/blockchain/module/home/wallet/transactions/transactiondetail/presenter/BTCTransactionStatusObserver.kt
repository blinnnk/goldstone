package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter

import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.bitcoin.BitcoinApi
import io.goldstone.blockchain.kernel.network.bitcoin.BitcoinUrl
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionHeaderModel
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/7/27 5:51 PM
 * @author KaySaith
 */

private var hasBlockNumber = false

/** ———————————— 这里是从转账完成后跳入的账单详情界面用到的数据 ————————————*/
fun TransactionDetailPresenter.observerBTCTransaction() {
	// 在页面销毁后需要用到, `activity` 所以提前存储起来
	val currentActivity = fragment.getMainActivity()
	object : BTCSeriesTransactionStatusObserver() {
		override val chainName = Config.getBTCCurrentChainName()
		override val hash = currentHash
		override fun getStatus(confirmed: Boolean, blockInterval: Int) {
			if (confirmed) {
				onBTCTransactionSucceed()
				val address = CoinSymbol.BTC.getAddress()
				updateWalletDetailBTCValue(address, currentActivity)
				if (confirmed) {
					updateConformationBarFinished()
				}
			} else {
				if (!hasBlockNumber) {
					// 更新 `BlockNumber` 及时间信息, 并未完全完成 `Pending` 状态
					getBTCTransactionFromChain(true)
					hasBlockNumber = true
				}
				showConformationInterval(blockInterval)
			}
		}
	}.start()
}

private fun TransactionDetailPresenter.updateWalletDetailBTCValue(
	address: String,
	activity: MainActivity?
) {
	updateBTCBalanceByTransaction(address) {
		activity?.getWalletDetailFragment()?.presenter?.updateData()
	}
}

private fun TransactionDetailPresenter.updateBTCBalanceByTransaction(
	address: String,
	callback: () -> Unit
) {
	MyTokenTable.getBalanceByContract(
		TokenContract.getBTC(),
		address,
		{
			fragment.context?.alert(it.message)
			callback()
		}
	) {
		MyTokenTable.updateBalanceByContract(it, address, TokenContract.getBTC())
		GoldStoneAPI.context.runOnUiThread { callback() }
	}
}

/**
 * 当 `Transaction` 监听到自身发起的交易的时候执行这个函数, 关闭监听以及执行动作
 */
private fun TransactionDetailPresenter.onBTCTransactionSucceed() {
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
		getBTCTransactionFromChain(false)
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
		getBTCTransactionFromChain(false)
	}
}

// 从转账界面进入后, 自动监听交易完成后, 用来更新交易数据的工具方法
private fun TransactionDetailPresenter.getBTCTransactionFromChain(
	isPending: Boolean
) {
	val address =
		if (Config.isTestEnvironment()) Config.getCurrentBTCSeriesTestAddress()
		else Config.getCurrentBTCAddress()
	BitcoinApi.getTransactionByHash(
		currentHash,
		address,
		BitcoinUrl.currentUrl(),
		{
			fragment.context.alert(it.toString())
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