package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter

import android.os.Handler
import android.os.Looper
import com.blinnnk.extension.findChildFragmentByTag
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.FragmentTag
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionHeaderModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailHeaderView
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/5/24 2:53 AM
 * @author KaySaith
 */
abstract class TransactionStatusObserver {
	
	private val handler = Handler(Looper.getMainLooper())
	private val targetIntervla = 6
	abstract val transactionHash: String
	
	open fun checkStatusByTransaction() {
		doAsync {
			GoldStoneEthCall
				.getTransactionByHash(
					transactionHash,
					Config.getCurrentChain(),
					{
						removeObserver()
						handler.postDelayed(reDo, 6000L)
					}, { error, _ ->
						LogUtil.error("checkStatusByTransaction", error)
						// error callback if need to do something
					}) { transaction ->
					GoldStoneEthCall.getBlockNumber(
						{ error, _ ->
							LogUtil.error("checkStatusByTransaction", error)
							// error callback if need to do something
						}) {
						GoldStoneAPI.context.runOnUiThread {
							val blockInterval = it - transaction.blockNumber.toInt() + 1
							val hasConfirmed = blockInterval > targetIntervla
							getStatus(hasConfirmed, blockInterval)
							if (hasConfirmed) {
								removeObserver()
							} else {
								// 没有达到 `6` 个新的 `Block` 确认一直执行监测
								handler.postDelayed(reDo, 6000L)
							}
						}
					}
				}
		}
	}
	
	abstract fun getStatus(status: Boolean, blockInterval: Int)
	
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

/** ———————————— 这里是从转账完成后跳入的账单详情界面用到的数据 ————————————*/
fun TransactionDetailPresenter.observerTransaction() {
	// 在页面销毁后需要用到, `activity` 所以提前存储起来
	val currentActivity = fragment.getMainActivity()
	object : TransactionStatusObserver() {
		override val transactionHash = currentHash
		override fun getStatus(status: Boolean, blockInterval: Int) {
			if (status) {
				onTransactionSucceed()
				updateWalletDetailValue(currentActivity)
			} else {
				showConformationInterval(blockInterval)
			}
		}
	}.start()
}

private fun TransactionDetailPresenter.showConformationInterval(
	intervalCount: Int
) {
	fragment.recyclerView.getItemAtAdapterPosition<TransactionDetailHeaderView>(0) {
		it?.apply {
			headerModel?.let { updateHeaderValue(it) }
			updateConformationBar(intervalCount)
		}
	}
}

/**
 * 当 `Transaction` 监听到自身发起的交易的时候执行这个函数, 关闭监听以及执行动作
 */
private fun TransactionDetailPresenter.onTransactionSucceed() {
	data?.apply {
		updateHeaderValue(
			TransactionHeaderModel(
				count,
				address,
				token.symbol,
				false,
				false
			)
		)
		getTransactionFromChain()
	}
	
	dataFromList?.apply {
		updateHeaderValue(
			TransactionHeaderModel(
				count,
				addressName,
				symbol,
				false,
				false
			)
		)
		getTransactionFromChain()
	}
}

private fun TransactionDetailPresenter.updateWalletDetailValue(activity: MainActivity?) {
	updateMyTokenBalanceByTransaction {
		activity?.apply {
			supportFragmentManager.findFragmentByTag(FragmentTag.home)
				.findChildFragmentByTag<WalletDetailFragment>(FragmentTag.walletDetail)?.apply {
					runOnUiThread { presenter.updateData() }
				}
		}
	}
}

private fun TransactionDetailPresenter.updateMyTokenBalanceByTransaction(callback: () -> Unit) {
	GoldStoneEthCall
		.getTransactionByHash(
			currentHash,
			Config.getCurrentChain(),
			{
				// unfinish callback
			},
			{ error, reason ->
				fragment.context?.alert(reason ?: error.toString())
			}
		) { transaction ->
			if (transaction.isERC20) {
				val contract = transaction.to
				GoldStoneEthCall.getTokenBalanceWithContract(
					contract,
					WalletTable.current.address, { error, reason ->
						fragment.context?.alert(reason ?: error.toString())
					}) { balance ->
					MyTokenTable.updateCurrentWalletBalanceWithContract(balance, contract)
					callback()
				}
			} else {
				GoldStoneEthCall.getEthBalance(
					WalletTable.current.address, { error, reason ->
					fragment.context?.alert(reason ?: error.toString())
				}) {
					MyTokenTable.updateCurrentWalletBalanceWithContract(it, CryptoValue.ethContract)
					callback()
				}
			}
		}
}