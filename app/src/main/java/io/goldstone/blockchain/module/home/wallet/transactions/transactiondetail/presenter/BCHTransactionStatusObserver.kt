package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter

import android.os.Handler
import android.os.Looper
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.utils.showAfterColonContent
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.bitcoin.BTCSeriesJsonRPC
import io.goldstone.blockchain.kernel.network.bitcoincash.BitcoinCashApi
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionHeaderModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/8/15 1:38 PM
 * @author KaySaith
 */

abstract class BCHTransactionStatusObserver {

	abstract val hash: String
	private val handler = Handler(Looper.getMainLooper())
	private val targetIntervla = 6
	private val retryTime = 20000L
	private var currentBlockNumber: Int? = null
	private var maxRetryTimes = 6

	open fun checkStatusByTransaction() {
		doAsync {
			if (currentBlockNumber.isNull()) {
				BitcoinCashApi.getBlockNumberByTransactionHash(
					hash,
					{
						// 出错失败最大重试次数设定
						if (maxRetryTimes <= 0) removeObserver()
						else maxRetryTimes -= 1
						// TODO ERROR Alert
						LogUtil.error("Observering getBlockNumberByTransactionHash", it)
					}
				) {
					removeObserver()
					currentBlockNumber = it
					handler.postDelayed(reDo, retryTime)
				}
			} else {
				BTCSeriesJsonRPC.getCurrentBlockHeight(
					Config.getBCHCurrentChainName(),
					{
						removeObserver()
						// TODO ERROR Alert
					}
				) { it ->
					it?.let {
						val blockInterval = it - currentBlockNumber!!
						val hasConfirmed = blockInterval > targetIntervla
						if (hasConfirmed) {
							removeObserver()
						} else {
							// 没有达到 `6` 个新的 `Block` 确认一直执行监测
							removeObserver()
							handler.postDelayed(reDo, retryTime)
						}
						getStatus(hasConfirmed, blockInterval)
					}
				}
			}
		}
	}

	abstract fun getStatus(confirmed: Boolean, blockInterval: Int)

	private fun removeObserver() {
		handler.removeCallbacks(reDo)
	}

	fun start() {
		checkStatusByTransaction()
	}

	private val reDo: Runnable = Runnable {
		checkStatusByTransaction()
	}
}

private var hasBlockNumber = false
/** ———————————— 这里是从转账完成后跳入的账单详情界面用到的数据 ————————————*/
fun TransactionDetailPresenter.observerBCHTransaction() {
	// 在页面销毁后需要用到, `activity` 所以提前存储起来
	val currentActivity = fragment.getMainActivity()
	object : BCHTransactionStatusObserver() {
		override val hash = currentHash
		override fun getStatus(confirmed: Boolean, blockInterval: Int) {
			if (confirmed) {
				onBCHTransactionSucceed()
				val address = WalletTable.getAddressBySymbol(CryptoSymbol.bch)
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
		CryptoValue.bchContract,
		address,
		false,
		{ error, reason ->
			fragment.context?.alert(reason ?: error.toString().showAfterColonContent())
			LogUtil.error("updateMyTokenBalanceByTransaction $reason", error)
			GoldStoneAPI.context.runOnUiThread { callback() }
		}
	) {
		MyTokenTable.updateBalanceWithContract(it, CryptoValue.ltcContract, address)
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