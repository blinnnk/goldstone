package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter

import android.os.Handler
import android.os.Looper
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.utils.*
import io.goldstone.blockchain.common.value.ChainID
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.ChainURL
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionHeaderModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailHeaderView
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
	abstract val chainID: String
	private var isFailed: Boolean? = null
	private val retryTime = 6000L
	
	open fun checkStatusByTransaction() {
		doAsync {
			GoldStoneEthCall.getTransactionByHash(
				transactionHash,
				ChainID.getChainNameByID(chainID),
				{
					removeObserver()
					handler.postDelayed(reDo, retryTime)
				},
				{ error, reason ->
					LogUtil.error("checkStatus getTransactionByHash $reason", error)
				}
			) { transaction ->
				GoldStoneEthCall.getBlockNumber(
					{ error, reason ->
						LogUtil.error("checkStatus getBlockNumber $reason", error)
					},
					ChainID.getChainNameByID(chainID)
				) { blockNumber ->
					isFailed?.let { failed ->
						GoldStoneAPI.context.runOnUiThread {
							val blockInterval = blockNumber - transaction.blockNumber.toInt()
							val hasConfirmed = blockInterval > targetIntervla
							val hasError = TinyNumberUtils.isTrue(transaction.hasError)
							getStatus(
								hasConfirmed,
								blockInterval,
								hasError,
								failed
							)
							
							if (hasConfirmed || hasError) {
								removeObserver()
							} else {
								// 没有达到 `6` 个新的 `Block` 确认一直执行监测
								handler.postDelayed(reDo, retryTime)
							}
						}
					}
					// 只判断一次是否是失败的交易
					if (isFailed.isNull()) {
						if (ChainURL.etcChainName.any {
								it.equals(ChainID.getChainNameByID(chainID), true)
							}) {
							isFailed = false
							// 没有达到 `6` 个新的 `Block` 确认一直执行监测
							handler.postDelayed(reDo, retryTime)
						} else {
							// 存在某些情况, 交易已经完成但是由于只能合约的问题, 交易失败. 这里做一个判断。
							GoldStoneEthCall.getReceiptByHash(
								transactionHash,
								{ error, reason ->
									LogUtil.error("checkStatusByTransaction$reason", error)
								},
								ChainID.getChainNameByID(chainID)
							) { failed ->
								isFailed = failed
								if (isFailed == true) {
									getStatus(
										false,
										1,
										false,
										failed
									)
									removeObserver()
								} else {
									// 没有达到 `6` 个新的 `Block` 确认一直执行监测
									handler.postDelayed(reDo, retryTime)
								}
							}
						}
					}
				}
			}
		}
	}
	
	abstract fun getStatus(
		confirmed: Boolean,
		blockInterval: Int,
		hasError: Boolean,
		isFailed: Boolean
	)
	
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
		override val chainID: String = ChainID.getChainIDByName(getCurrentChainName())
		override val transactionHash = currentHash
		override fun getStatus(
			confirmed: Boolean,
			blockInterval: Int,
			hasError: Boolean,
			isFailed: Boolean
		) {
			if (confirmed || hasError || isFailed) {
				onTransactionSucceed(hasError, isFailed)
				updateWalletDetailValue(currentActivity)
				if (confirmed) {
					updateConformationBarFinished()
				}
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
			headerModel?.let {
				updateHeaderValue(it)
			}
			updateConformationBar(intervalCount)
		}
	}
}

fun TransactionDetailPresenter.updateConformationBarFinished() {
	fragment.recyclerView.getItemAtAdapterPosition<TransactionDetailHeaderView>(0) {
		it?.apply {
			updateConformationBar(CryptoValue.confirmBlockNumber)
		}
	}
}

/**
 * 当 `Transaction` 监听到自身发起的交易的时候执行这个函数, 关闭监听以及执行动作
 */
private fun TransactionDetailPresenter.onTransactionSucceed(
	hasError: Boolean, isFailed: Boolean
) {
	// 交易过程中发生错误
	if (hasError) {
		updateDataWhenHasError()
	}
	// 交易流程全部成功, 但是合约的问题导致失败
	if (isFailed) {
		updateDataWhenFailed()
	}
	
	data?.apply {
		updateHeaderValue(
			TransactionHeaderModel(
				count,
				address,
				token.symbol,
				false,
				false,
				TinyNumberUtils.hasTrue(hasError, isFailed)
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
				false,
				TinyNumberUtils.hasTrue(hasError, isFailed)
			)
		)
		getTransactionFromChain()
	}
}

fun TransactionDetailPresenter.updateDataWhenHasError() {
	TransactionTable.getTransactionByHash(currentHash) {
		it.find {
			it.hash == currentHash
		}?.let {
			doAsync {
				GoldStoneDataBase
					.database
					.transactionDao()
					.update(
						it.apply {
							this.hasError = TinyNumber.True.value.toString()
						}
					)
			}
		}
	}
}

fun TransactionDetailPresenter.updateDataWhenFailed() {
	TransactionTable.getTransactionByHash(currentHash) {
		it.find {
			it.hash == currentHash
		}?.let {
			doAsync {
				GoldStoneDataBase
					.database
					.transactionDao()
					.update(it.apply { isFailed = true })
			}
		}
	}
}

private fun TransactionDetailPresenter.updateWalletDetailValue(
	activity: MainActivity?
) {
	updateMyTokenBalanceByTransaction {
		activity?.getWalletDetailFragment()?.presenter?.updateData()
	}
}

private fun TransactionDetailPresenter.updateMyTokenBalanceByTransaction(
	callback: () -> Unit
) {
	GoldStoneEthCall.getTransactionByHash(
		currentHash,
		getCurrentChainName(),
		errorCallback = { error, reason ->
			fragment.context?.alert(reason ?: error.toString())
		}
	) { transaction ->
		val contract = ChainURL.getContractByTransaction(transaction, getCurrentChainName())
		MyTokenTable.getBalanceWithContract(
			contract,
			Config.getCurrentAddress(),
			false,
			{ error, reason ->
				fragment.context?.alert(reason ?: error.toString().showAfterColonContent())
				LogUtil.error("updateMyTokenBalanceByTransaction $reason", error)
				GoldStoneAPI.context.runOnUiThread { callback() }
			}
		) {
			MyTokenTable.updateBalanceWithContract(it, contract, Config.getCurrentAddress())
			GoldStoneAPI.context.runOnUiThread { callback() }
		}
	}
}