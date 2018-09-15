package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter

import android.os.Handler
import android.os.Looper
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import org.jetbrains.anko.doAsync


/**
 * @author KaySaith
 * @date  2018/09/16
 */

abstract class EOSTransactionObserver {

	abstract val hash: String
	private val handler = Handler(Looper.getMainLooper())
	private val targetInterval = 6
	private val retryTime = 6000L
	private var maxRetryTimes = 6

	open fun checkStatusByTransaction() {
		doAsync {
			EOSAPI.getBlockNumberByTxID(
				hash,
				{
					// 出错失败最大重试次数设定
					if (maxRetryTimes <= 0) removeObserver()
					else maxRetryTimes -= 1
					// TODO ERROR Alert
					LogUtil.error("Observing EOS getBlockNumberByTxID", it)
				}
			) { blockNumber ->
				if (blockNumber.isNull()) {
					removeObserver()
					handler.postDelayed(reDo, retryTime)
				} else {
					val hasConfirmed = blockNumber!! > targetInterval
					if (hasConfirmed) {
						removeObserver()
					} else {
						// 没有达到 `6` 个新的 `Block` 确认一直执行监测
						removeObserver()
						handler.postDelayed(reDo, retryTime)
					}
					getStatus(hasConfirmed, blockNumber, blockNumber)
				}
			}
		}
	}

	@WorkerThread
	abstract fun getStatus(confirmed: Boolean, blockInterval: Int, blockNumber: Int)

	private fun removeObserver() = handler.removeCallbacks(reDo)
	fun start() = checkStatusByTransaction()

	private val reDo: Runnable = Runnable {
		checkStatusByTransaction()
	}
}