package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.observer

import android.os.Handler
import android.os.Looper
import android.support.annotation.UiThread
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


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
				EOSAPI.getBlockNumberByTxID(hash) { blockNumber, error ->
					if (blockNumber != null && error.isNone()) {
						transactionBlockNumber = blockNumber
						removeObserver()
						handler.postDelayed(reDo, retryTime)
					} else {
						// 出错失败最大重试次数设定
						if (maxRetryTimes <= 0) removeObserver()
						else {
							maxRetryTimes -= 1
							removeObserver()
							handler.postDelayed(reDo, retryTime)
						}
					}
				}
			} else EOSAPI.getChainInfo { chainInfo, error ->
				if (chainInfo != null && error.isNone()) {
					if (totalConfirmedCount.isNull()) {
						totalConfirmedCount = transactionBlockNumber!! - chainInfo.lastIrreversibleBlockNumber
					}
					// 如果当前转账信息大于最近一个不可逆的区块那么就确认完毕了
					val hasConfirmed = chainInfo.lastIrreversibleBlockNumber > transactionBlockNumber!!
					if (hasConfirmed) {
						removeObserver()
					} else {
						// 没有达到 `6` 个新的 `Block` 确认一直执行监测
						removeObserver()
						handler.postDelayed(reDo, retryTime)
					}
					val confirmedCount = totalConfirmedCount!! - (transactionBlockNumber!! - chainInfo.lastIrreversibleBlockNumber)
					uiThread {
						getStatus(hasConfirmed, transactionBlockNumber!!, confirmedCount, totalConfirmedCount!!)
					}
				} else {
					// 出错失败最大重试次数设定
					if (maxRetryTimes <= 0) removeObserver()
					else maxRetryTimes -= 1
				}
			}
		}
	}

	@UiThread
	abstract fun getStatus(confirmed: Boolean, blockNumber: Int, confirmedCount: Int, totalCount: Int)

	fun removeObserver() = handler.removeCallbacks(reDo)
	fun start() = checkStatusByTransaction()

	private val reDo: Runnable = Runnable {
		checkStatusByTransaction()
	}
}