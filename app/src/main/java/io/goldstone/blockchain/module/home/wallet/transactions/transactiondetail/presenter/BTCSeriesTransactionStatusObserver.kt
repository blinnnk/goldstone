package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter

import android.os.Handler
import android.os.Looper
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.crypto.multichain.node.ChainURL
import io.goldstone.blockchain.kernel.network.bitcoin.BTCSeriesJsonRPC
import org.jetbrains.anko.doAsync

/**
 * @date 2018/8/20 12:13 PM
 * @author KaySaith
 */

abstract class BTCSeriesTransactionStatusObserver {

	abstract val hash: String
	abstract val chainURL: ChainURL
	private val handler = Handler(Looper.getMainLooper())
	private val targetInterval = 6
	private val retryTime = 20000L
	private var maxRetryTimes = 6

	open fun checkStatusByTransaction() {
		doAsync {
			BTCSeriesJsonRPC.getConfirmations(
				chainURL,
				hash
			) { confirmationCount, error ->
				if (error.hasError()) {
					// 出错失败最大重试次数设定
					if (maxRetryTimes <= 0) removeObserver() else maxRetryTimes -= 1
					return@getConfirmations
				}
				if (confirmationCount.isNull()) {
					removeObserver()
					handler.postDelayed(reDo, retryTime)
				} else {
					val hasConfirmed = confirmationCount!! > targetInterval
					if (hasConfirmed) {
						removeObserver()
					} else {
						// 没有达到 `6` 个新的 `Block` 确认一直执行监测
						removeObserver()
						handler.postDelayed(reDo, retryTime)
					}
					getStatus(hasConfirmed, confirmationCount)
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