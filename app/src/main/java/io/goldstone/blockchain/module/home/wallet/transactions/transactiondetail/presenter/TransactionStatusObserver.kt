package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter

import android.os.Handler
import android.os.Looper
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
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
			GoldStoneEthCall
				.getTransactionByHash(
					transactionHash,
					GoldStoneApp.getCurrentChain(),
					{
						handler.removeCallbacks(reDo)
						handler.postDelayed(reDo, 6000L)
					}, { error, _ ->
						LogUtil.error("checkStatusByTransaction", error)
						// error callback if need to do something
					}) {
					GoldStoneAPI.context.runOnUiThread {
						getStatus(true)
					}
				}
		}
	}
	
	abstract fun getStatus(status: Boolean)
	
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