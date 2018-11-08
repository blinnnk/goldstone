package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.observer

import android.os.Handler
import android.os.Looper
import android.support.annotation.UiThread
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orZero
import com.blinnnk.util.TinyNumberUtils
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.network.ethereum.GoldStoneEthCall
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * @date 2018/5/24 2:53 AM
 * @author KaySaith
 */
abstract class ETHSeriesTransactionObserver {

	private val handler = Handler(Looper.getMainLooper())
	private val targetInterval = 6
	abstract val transactionHash: String
	abstract val chainID: String
	private var isFailed: Boolean? = null
	private val retryTime = 6000L
	private var transaction: TransactionTable? = null

	open fun checkStatusByTransaction() {
		doAsync {
			val chainURL =
				if (ChainID(chainID).isETHSeries()) SharedChain.getCurrentETH()
				else SharedChain.getETCCurrent()
			if (transaction.isNull()) GoldStoneEthCall.getTransactionByHash(
				transactionHash,
				chainURL,
				{
					removeObserver()
					handler.postDelayed(reDo, retryTime)
				}
			) { data, error ->
				// 遇到错误就不再执行轮循
				if (data.isNull() || error.hasError()) removeObserver()
				else {
					transaction = data
					removeObserver()
					handler.postDelayed(reDo, retryTime)
				}
			} else GoldStoneEthCall.getBlockCount(chainURL) { blockCount, error ->
				if (blockCount == null || error.hasError()) return@getBlockCount
				val blockInterval = blockCount - transaction?.blockNumber?.toIntOrNull().orZero()
				val hasConfirmed = blockInterval > targetInterval
				val hasError = TinyNumberUtils.isTrue(transaction?.hasError!!)
				if (!isFailed.isNull() || hasConfirmed) uiThread {
					uiThread {
						getStatus(hasConfirmed, blockInterval, blockCount, hasError)
					}
					if (hasConfirmed || hasError)
						removeObserver()
					else {
						// 没有达到 `6` 个新的 `Block` 确认一直执行监测
						removeObserver()
						handler.postDelayed(reDo, retryTime)
					}
				} else if (ChainID(chainID).isETCSeries()) {
					isFailed = false
					// 没有达到 `6` 个新的 `Block` 确认一直执行监测
					removeObserver()
					handler.postDelayed(reDo, retryTime)
				} else GoldStoneEthCall.getReceiptByHash(transactionHash, chainURL) { failed, failedError ->
					// 存在某些情况, 交易已经完成但是由于只能合约的问题, 交易失败. 这里做一个判断。
					if (failed.isNull() || failedError.hasError()) return@getReceiptByHash
					isFailed = failed
					if (isFailed == true) {
						uiThread {
							getStatus(false, 1, 0, false)
						}
						removeObserver()
					} else {
						// 没有达到 `6` 个新的 `Block` 确认一直执行监测
						removeObserver()
						handler.postDelayed(reDo, retryTime)
					}
				}
			}
		}
	}

	@UiThread
	abstract fun getStatus(
		confirmed: Boolean,
		blockInterval: Int,
		blockNumber: Int,
		hasError: Boolean
	)

	fun removeObserver() {
		handler.removeCallbacks(reDo)
	}

	fun start() {
		checkStatusByTransaction()
	}

	private val reDo: Runnable = Runnable {
		checkStatusByTransaction()
	}
}
