package io.goldstone.blinnnk.module.home.wallet.transactions.transactiondetail.observer

import android.os.Handler
import android.os.Looper
import android.support.annotation.UiThread
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orZero
import io.goldstone.blinnnk.common.thread.launchUI
import io.goldstone.blinnnk.crypto.multichain.node.ChainURL
import io.goldstone.blinnnk.kernel.network.bitcoin.BTCSeriesJsonRPC
import io.goldstone.blinnnk.kernel.network.bitcoin.BTCSeriesJsonRPC.getBlockCount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * @date 2018/8/20 12:13 PM
 * @author KaySaith
 */

abstract class BTCSeriesTransactionObserver {
	abstract val hash: String
	abstract val chainURL: ChainURL
	private val handler = Handler(Looper.getMainLooper())
	private val targetInterval = 6
	private val retryTime = 20000L
	private var maxRetryTimes = 6
	private var blockNumber: Int? = null

	open fun checkStatusByTransaction() {
		GlobalScope.launch(Dispatchers.Default) {
			BTCSeriesJsonRPC.getConfirmations(chainURL, hash) { confirmationCount, error ->
				if (error.hasError()) {
					// 出错失败最大重试次数设定
					if (maxRetryTimes <= 0) removeObserver() else maxRetryTimes -= 1
				} else if (confirmationCount == null) {
					removeObserver()
					handler.postDelayed(reDo, retryTime)
				} else {
					val hasConfirmed = confirmationCount > targetInterval
					if (hasConfirmed) {
						launchUI {
							getStatus(hasConfirmed, confirmationCount, blockNumber.orZero())
						}
						removeObserver()
					} else {
						if (blockNumber.isNull()) getBlockCount(chainURL) { blockCount, blockCountError ->
							if (blockCount.isNotNull() && blockCountError.isNone()) {
								blockNumber = blockCount - confirmationCount
								launchUI {
									getStatus(hasConfirmed, confirmationCount, blockNumber!!)
								}
							}
						}
						// 没有达到 `6` 个新的 `Block` 确认一直执行监测
						removeObserver()
						handler.postDelayed(reDo, retryTime)
						launchUI {
							blockNumber?.let { getStatus(hasConfirmed, confirmationCount, it) }
						}
					}
				}
			}
		}
	}

	@UiThread
	abstract fun getStatus(confirmed: Boolean, blockInterval: Int, blockNumber: Int)

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