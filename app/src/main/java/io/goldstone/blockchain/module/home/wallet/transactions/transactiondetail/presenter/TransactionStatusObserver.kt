package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter

import android.os.Handler
import android.os.Looper
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orFalse
import com.blinnnk.util.TinyNumber
import com.blinnnk.util.TinyNumberUtils
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.ChainURL
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionHeaderModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/5/24 2:53 AM
 * @author KaySaith
 */
abstract class TransactionStatusObserver {

	private val handler = Handler(Looper.getMainLooper())
	private val targetInterval = 6
	abstract val transactionHash: String
	abstract val chainID: String
	private var isFailed: Boolean? = null
	private val retryTime = 6000L
	private var transaction: TransactionTable? = null

	open fun checkStatusByTransaction() {
		doAsync {
			if (transaction.isNull()) {
				GoldStoneEthCall.getTransactionByHash(
					transactionHash,
					ChainID(chainID).getChainName(),
					{
						removeObserver()
						handler.postDelayed(reDo, retryTime)
					},
					{
						removeObserver()
						LogUtil.error("checkStatus getTransactionByHash", it)
					}
				) { data ->
					transaction = data
					removeObserver()
					handler.postDelayed(reDo, retryTime)
				}
			} else {
				GoldStoneEthCall.getBlockNumber(
					{
						LogUtil.error("checkStatus getBlockNumber", it)
					},
					ChainID(chainID).getChainName()
				) { blockNumber ->
					val blockInterval = blockNumber - transaction?.blockNumber?.toInt()!!
					val hasConfirmed = blockInterval > targetInterval
					val hasError = TinyNumberUtils.isTrue(transaction?.hasError!!)
					if (!isFailed.isNull() || hasConfirmed) {
						GoldStoneAPI.context.runOnUiThread {
							getStatus(
								hasConfirmed,
								blockInterval,
								hasError,
								isFailed.orFalse()
							)
							if (hasConfirmed || hasError) {
								removeObserver()
							} else {
								// 没有达到 `6` 个新的 `Block` 确认一直执行监测
								removeObserver()
								handler.postDelayed(reDo, retryTime)
							}
						}
					} else {
						if (ChainURL.etcChainName.any {
								it.equals(ChainID(chainID).getChainName(), true)
							}) {
							isFailed = false
							// 没有达到 `6` 个新的 `Block` 确认一直执行监测
							removeObserver()
							handler.postDelayed(reDo, retryTime)
						} else {
							// 存在某些情况, 交易已经完成但是由于只能合约的问题, 交易失败. 这里做一个判断。
							GoldStoneEthCall.getReceiptByHash(
								transactionHash,
								{
									LogUtil.error("checkStatusByTransaction", it)
								},
								ChainID(chainID).getChainName()
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
									removeObserver()
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
		override val chainID: String = ChainID.getChainIDByName(CoinSymbol(getUnitSymbol()).getCurrentChainName())
		override val transactionHash = currentHash
		override fun getStatus(
			confirmed: Boolean,
			blockInterval: Int,
			hasError: Boolean,
			isFailed: Boolean
		) {
			if (confirmed || hasError || isFailed) {
				onTransactionSucceed(hasError, isFailed)
				val address =
					data?.fromAddress ?: dataFromList?.fromAddress ?: notificationData?.fromAddress.orEmpty()
				updateWalletDetailValue(address, currentActivity)
				if (confirmed) {
					updateConformationBarFinished()
				}
			} else {
				showConformationInterval(blockInterval)
			}
		}
	}.start()
}

/**
 * 当 `Transaction` 监听到自身发起的交易的时候执行这个函数, 关闭监听以及执行动作
 */
private fun TransactionDetailPresenter.onTransactionSucceed(
	hasError: Boolean,
	isFailed: Boolean
) {
	// 交易过程中发生错误
	if (hasError) updateDataWhenHasError()
	// 交易流程全部成功, 但是合约的问题导致失败
	if (isFailed) updateDataWhenFailed()

	val address = data?.toAddress ?: dataFromList?.addressName ?: ""
	val symbol = getUnitSymbol()
	updateHeaderValue(
		TransactionHeaderModel(
			count,
			address,
			symbol,
			false,
			false,
			false
		)
	)
	getTransactionFromChain {
		if (!it.isNone()) fragment.context.alert(it.message)
	}
}

// 从转账界面进入后, 自动监听交易完成后, 用来更新交易数据的工具方法
private fun TransactionDetailPresenter.getTransactionFromChain(
	errorCallback: (RequestError) -> Unit
) {
	GoldStoneEthCall.getTransactionByHash(
		currentHash,
		CoinSymbol(getUnitSymbol()).getCurrentChainName(),
		errorCallback = errorCallback
	) {
		fragment.context?.runOnUiThread {
			fragment.asyncData?.clear()
			val data = generateModels(it)
			fragment.asyncData?.addAll(generateModels(it))
			fragment.recyclerView.adapter?.notifyItemRangeChanged(1, data.size)
		}
		// 成功获取数据后在异步线程更新数据库记录
		updateDataInDatabase(it.blockNumber)
	}
}

// 自动监听交易完成后, 将转账信息插入数据库
private fun TransactionDetailPresenter.updateDataInDatabase(blockNumber: String) {
	GoldStoneDataBase.database.transactionDao().apply {
		getTransactionByTaxHash(currentHash).let { it ->
			it.forEach {
				update(it.apply {
					this.blockNumber = blockNumber
					isPending = false
					hasError = "0"
					txReceiptStatus = "1"
				})
			}
		}
	}
}

fun TransactionDetailPresenter.updateDataWhenHasError() {
	TransactionTable.getTransactionByHash(currentHash) { it ->
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

private fun TransactionDetailPresenter.updateDataWhenFailed() {
	TransactionTable.getTransactionByHash(currentHash) { it ->
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
	address: String,
	activity: MainActivity?
) {
	updateMyTokenBalanceByTransaction(address) {
		activity?.getWalletDetailFragment()?.presenter?.updateData()
	}
}

private fun TransactionDetailPresenter.updateMyTokenBalanceByTransaction(
	address: String,
	callback: (RequestError) -> Unit
) {
	GoldStoneEthCall.getTransactionByHash(
		currentHash,
		CoinSymbol(getUnitSymbol()).getCurrentChainName(),
		errorCallback = callback
	) { transaction ->
		val contract =
			ChainURL.getContractByTransaction(transaction, CoinSymbol(getUnitSymbol()).getCurrentChainName())
		MyTokenTable.getBalanceByContract(
			contract,
			address,
			callback
		) {
			MyTokenTable.updateBalanceByContract(it, address, contract)
			GoldStoneAPI.context.runOnUiThread { callback(RequestError.None) }
		}
	}
}