package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter

import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.utils.showAfterColonContent
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.crypto.utils.MultiChainUtils
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.bitcoincash.BitcoinCashApi
import io.goldstone.blockchain.kernel.network.bitcoincash.BitcoinCashUrl
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionHeaderModel
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/8/15 1:38 PM
 * @author KaySaith
 */

private var hasBlockNumber = false

/** ———————————— 这里是从转账完成后跳入的账单详情界面用到的数据 ————————————*/
fun TransactionDetailPresenter.observerBCHTransaction() {
	// 在页面销毁后需要用到, `activity` 所以提前存储起来
	val currentActivity = fragment.getMainActivity()
	object : BTCSeriesTransactionStatusObserver() {
		override val chainName = Config.getBCHCurrentChainName()
		override val hash = currentHash
		override fun getStatus(confirmed: Boolean, blockInterval: Int) {
			if (confirmed) {
				onBCHTransactionSucceed()
				val address = MultiChainUtils.getAddressBySymbol(CoinSymbol.bch)
				updateWalletDetailBCHValue(address, currentActivity)
				if (confirmed) {
					updateConformationBarFinished()
				}
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

private fun TransactionDetailPresenter.updateWalletDetailBCHValue(
	address: String,
	activity: MainActivity?
) {
	updateBCHBalanceByTransaction(address) {
		activity?.getWalletDetailFragment()?.presenter?.updateData()
	}
}

private fun TransactionDetailPresenter.updateBCHBalanceByTransaction(
	address: String,
	callback: () -> Unit
) {
	MyTokenTable.getBalanceWithContract(
		TokenContract.bchContract,
		address,
		false,
		{ error, reason ->
			fragment.context?.alert(reason ?: error.toString().showAfterColonContent())
			LogUtil.error("updateMyTokenBalanceByTransaction $reason", error)
			GoldStoneAPI.context.runOnUiThread { callback() }
		}
	) {
		MyTokenTable.updateBalanceWithContract(it, TokenContract.ltcContract, address)
		GoldStoneAPI.context.runOnUiThread { callback() }
	}
}

/**
 * 当 `Transaction` 监听到自身发起的交易的时候执行这个函数, 关闭监听以及执行动作
 */
private fun TransactionDetailPresenter.onBCHTransactionSucceed() {
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
		getBCHTransactionFromChain(false)
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
		getBCHTransactionFromChain(false)
	}
}

// 从转账界面进入后, 自动监听交易完成后, 用来更新交易数据的工具方法
private fun TransactionDetailPresenter.getBCHTransactionFromChain(
	isPending: Boolean
) {
	val address =
		if (Config.isTestEnvironment()) Config.getCurrentBTCSeriesTestAddress()
		else Config.getCurrentBCHAddress()
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