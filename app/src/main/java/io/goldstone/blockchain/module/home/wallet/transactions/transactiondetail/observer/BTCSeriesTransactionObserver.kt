package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.observer

import android.os.Handler
import android.os.Looper
import android.support.annotation.UiThread
import com.blinnnk.extension.orZero
import io.goldstone.blockchain.crypto.multichain.node.ChainURL
import io.goldstone.blockchain.kernel.network.bitcoin.BTCSeriesJsonRPC
import io.goldstone.blockchain.kernel.network.bitcoin.BTCSeriesJsonRPC.getBlockCount
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

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
		doAsync {
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
						uiThread {
							getStatus(hasConfirmed, confirmationCount, blockNumber.orZero())
						}
						removeObserver()
					} else {
						if (blockNumber == null) getBlockCount(chainURL) { blockCount, blockCountError ->
							if (blockCount != null && blockCountError.isNone()) {
								blockNumber = blockCount - confirmationCount
								uiThread {
									getStatus(hasConfirmed, confirmationCount, blockNumber!!)
								}
							}
						}
						// 没有达到 `6` 个新的 `Block` 确认一直执行监测
						removeObserver()
						handler.postDelayed(reDo, retryTime)
						uiThread {
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