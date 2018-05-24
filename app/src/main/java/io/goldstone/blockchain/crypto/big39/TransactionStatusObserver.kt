package io.goldstone.blockchain.crypto.big39

import android.os.Handler
import android.os.Looper
import io.goldstone.blockchain.crypto.GoldStoneEthCall
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/5/24 2:53 AM
 * @author KaySaith
 */

abstract class TransactionStatusObserver {

	private val handler = Handler(Looper.getMainLooper())

	abstract val transactionHash: String

	open fun checkStatusByTransaction() {
		doAsync {
			GoldStoneEthCall.getTransactionByHash(transactionHash, {
				handler.removeCallbacks(reDo)
				handler.postDelayed(reDo, 3000L)
			}) {
				GoldStoneAPI.context.runOnUiThread {
					getStatus(true)
				}
			}
		}
	}

	abstract fun getStatus(status: Boolean)

	private val reDo: Runnable = Runnable {
		checkStatusByTransaction()
	}

	fun start() {
		checkStatusByTransaction()
	}

}