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
	private var transactionBlockNumber: Int? = null
	private var totalConfirmedCount: Int? = null
	private val retryTime = 6000L
	private var maxRetryTimes = 6

	open fun checkStatusByTransaction() {
		doAsync {
			// 首先获取线上的最近的不可逆的区块
			if (transactionBlockNumber.isNull()) {
				EOSAPI.getBlockNumberByTxID(
					hash,
					{
						// 出错失败最大重试次数设定
						if (maxRetryTimes <= 0) removeObserver()
						else {
							maxRetryTimes -= 1
							removeObserver()
							handler.postDelayed(reDo, retryTime)
						}
						LogUtil.error("Observing EOS getBlockNumberByTxID", it)
					}
				) { blockNumber ->
					transactionBlockNumber = blockNumber
					removeObserver()
					handler.postDelayed(reDo, retryTime)
				}
			} else EOSAPI.getChainInfo(
				{
					// 出错失败最大重试次数设定
					if (maxRetryTimes <= 0) removeObserver()
					else maxRetryTimes -= 1
				}
			) {
				if (totalConfirmedCount.isNull()) {
					totalConfirmedCount = transactionBlockNumber!! - it.lastIrreversibleBlockNumber
				}
				// 如果当前转账信息大于最近一个不可逆的区块那么就确认完毕了
				val hasConfirmed = it.lastIrreversibleBlockNumber > transactionBlockNumber!!
				if (hasConfirmed) {
					removeObserver()
				} else {
					// 没有达到 `6` 个新的 `Block` 确认一直执行监测
					removeObserver()
					handler.postDelayed(reDo, retryTime)
				}
				val confirmedCount = totalConfirmedCount!! - (transactionBlockNumber!! - it.lastIrreversibleBlockNumber)
				getStatus(hasConfirmed, transactionBlockNumber!!, confirmedCount, totalConfirmedCount!!)
			}
		}
	}

	@WorkerThread
	abstract fun getStatus(confirmed: Boolean, blockNumber: Int, confirmedCount: Int, totalCount: Int)

	private fun removeObserver() = handler.removeCallbacks(reDo)
	fun start() = checkStatusByTransaction()

	private val reDo: Runnable = Runnable {
		checkStatusByTransaction()
	}
}