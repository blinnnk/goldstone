package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter

import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.TokenDetailText
import io.goldstone.blockchain.common.value.TransactionText
import io.goldstone.blockchain.crypto.*
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.APIPath
import io.goldstone.blockchain.kernel.network.EtherScanApi
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import io.goldstone.blockchain.module.home.wallet.notifications.notification.view.NotificationFragment
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.presenter.NotificationTransactionInfo
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.transactions.transaction.view.TransactionFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.ReceiptModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionDetailModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailHeaderView
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.TransactionListModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.web3j.protocol.Web3jFactory
import org.web3j.protocol.core.methods.response.Transaction
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.http.HttpService
import rx.Subscription

/**
 * @date 27/03/2018 3:27 AM
 * @author KaySaith
 * @description
 * 这个界面由两个入场场景公用, 分别是账单列表进入或转账完成进入, `fragment` 承担了两种身份
 * 固再次需要注意.
 */

class TransactionDetailPresenter(
	override val fragment: TransactionDetailFragment
) : BaseRecyclerPresenter<TransactionDetailFragment, TransactionDetailModel>() {

	private val data by lazy {
		fragment.arguments?.get(ArgumentKey.transactionDetail) as? ReceiptModel
	}
	private val dataFromList by lazy {
		fragment.arguments?.get(ArgumentKey.transactionFromList) as? TransactionListModel
	}

	private val notificationTransaction by lazy {
		fragment.arguments?.get(ArgumentKey.notificationTransaction) as? NotificationTransactionInfo
	}

	private var count = 0.0
	private var currentHash = ""

	override fun updateData() {

		// 这个是从账目列表进入的详情, `Transaction List`, `TokenDetail`
		dataFromList?.apply {
			updateHeaderValue(count, targetAddress, symbol, isPending, isReceived)
			fragment.asyncData = generateModels(this)
			currentHash = transactionHash
			if (isPending) {
				// 异步从链上查一下这条 `taxHash` 是否有最新的状态变化
				doAsync {
					fragment.getTransactionFromChain(transactionHash) {
						updateHeaderValue(count, targetAddress, symbol, false)
					}
				}
			}
		}
		// 这个是转账完毕后进入的界面初始
		data?.apply {
			currentHash = taxHash
			count = CryptoUtils.toCountByDecimal(raw.value.toDouble(), token.decimal)
			fragment.asyncData = generateModels()
			observerTransaction()
			updateHeaderValue(count, address, token.symbol, true)
		}

		// 这个是从通知中心进入的, 通知中心的显示是现查账.
		notificationTransaction?.let { transaction ->
			currentHash = transaction.hash
			// 查看本地数据库是否已经记录了这条交易, 这种情况存在于, 用户收到 push 并没有打开通知中心
			// 而是打开了账单详情. 这条数据已经被存入本地. 这个时候通知中心就不必再从链上查询数据了.
			TransactionTable.getTransactionByHashAndReceivedStatus(
				transaction.hash, transaction.isReceived
			) { localTransaction ->
				if (localTransaction.isNull()) {
					// 如果本地没有数据从链上查询所有需要的数据
					fragment.apply {
						getMainActivity()?.showLoadingView()
						updateTransactionByNotificationHash(transaction) {
							getMainActivity()?.removeLoadingView()
						}
					}
				} else {
					// 本地有数据直接展示本地数据
					localTransaction?.apply {
						fragment.asyncData = generateModels(TransactionListModel(localTransaction))
						updateHeaderValue(value.toDouble(), fromAddress, symbol, false, isReceive)
					}
				}
			}
		}
	}

	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		fragment.parentFragment.apply {
			when (this) {
				is TransactionFragment -> {
					overlayView.header.backButton.onClick {
						headerTitle = TransactionText.detail
						presenter.popFragmentFrom<TransactionDetailFragment>()
						setHeightMatchParent()
					}
				}
				is TokenDetailOverlayFragment -> {
					overlayView.header.backButton.onClick {
						headerTitle = TokenDetailText.tokenDetail
						presenter.popFragmentFrom<TransactionDetailFragment>()
						setHeightMatchParent()
					}
				}
				is NotificationFragment -> {
					overlayView.header.backButton.onClick {
						headerTitle = TokenDetailText.tokenDetail
						presenter.popFragmentFrom<TransactionDetailFragment>()
						updateParentContentLayoutHeight(fragment.asyncData?.size)
					}
				}
			}
		}
	}

	fun showEtherScanTransactionFragment() {
		val argument = Bundle().apply {
			putString(
				ArgumentKey.webViewUrl, "https://ropsten.etherscan.io/tx/$currentHash"
			)
		}
		fragment.parentFragment.apply {
			when (this) {
				is TransactionFragment -> {
					presenter.showTargetFragment<WebViewFragment>(
						TransactionText.etherScanTransaction, TransactionText.detail, argument
					)
				}

				is TokenDetailOverlayFragment -> {
					presenter.showTargetFragment<WebViewFragment>(
						TransactionText.etherScanTransaction, TokenDetailText.tokenDetail, argument
					)
				}

				is NotificationFragment -> {
					presenter.showTargetFragment<WebViewFragment>(
						TransactionText.etherScanTransaction, TokenDetailText.tokenDetail, argument
					)
				}
			}
		}
	}

	// 将时间戳转化为界面显示的时间格式的工具
	private fun formatDate(timeStamp: Long): String {
		return DateUtils.formatDateTime(
			GoldStoneAPI.context, timeStamp * 1000, DateUtils.FORMAT_SHOW_YEAR
		) + " " + DateUtils.formatDateTime(
			GoldStoneAPI.context, timeStamp * 1000, DateUtils.FORMAT_SHOW_TIME
		)
	}

	// 根据传入转账信息类型, 来生成对应的更新界面的数据
	private fun generateModels(
		receipt: Any? = null
	): ArrayList<TransactionDetailModel> {

		val minerFee = if (data.isNull()) dataFromList?.minerFee
		else (data!!.raw.gasLimit * data!!.raw.gasPrice).toDouble().toEthValue()

		val date = if (data.isNull()) dataFromList?.date else formatDate(data!!.timestamp)

		val receiptData = when (receipt) {
			is TransactionListModel -> {
				arrayListOf(
					receipt.minerFee,
					receipt.memo,
					receipt.transactionHash,
					receipt.blockNumber,
					receipt.date,
					receipt.url
				)
			}
			is TransactionReceipt -> {
				arrayListOf(
					minerFee,
					"There isn't a memo",
					currentHash,
					receipt.blockNumber.toBigDecimal(),
					date,
					EtherScanApi.transactionsByHash(currentHash)
				)
			}
			else -> {
				arrayListOf(
					minerFee,
					"There isn't a memo",
					currentHash,
					"waiting",
					date,
					EtherScanApi.transactionsByHash(currentHash)
				)
			}
		}
		arrayListOf(
			TransactionText.minerFee,
			TransactionText.memo,
			TransactionText.transactionHash,
			TransactionText.blockNumber,
			TransactionText.transactionDate,
			TransactionText.url
		).mapIndexed { index, it ->
			TransactionDetailModel(receiptData[index].toString(), it)
		}.let {
			return it.toArrayList()
		}
	}

	// 更新头部数字的工具
	private fun updateHeaderValue(
		count: Double, address: String, symbol: String, isPending: Boolean, isReceive: Boolean = false
	) {
		fragment.recyclerView.getItemViewAtAdapterPosition<TransactionDetailHeaderView>(0) {
			setIconStyle(count, address, symbol, isReceive, isPending)
		}
	}

	/** ———————————— 这里是从转账完成后跳入的账单详情界面用到的数据 ————————————*/

	private val web3j = Web3jFactory.build(HttpService(APIPath.ropstan))
	private var transactionObserver: Subscription? = null
	private fun observerTransaction() {
		doAsync {
			try {
				// 开启监听交易是否完成
				transactionObserver = web3j.transactionObservable().filter {
					Log.d("DEBUG", it.hash)
					it.hash == currentHash
				}.subscribe {
					println("succeed")
					onTransactionSucceed()
				}
			} catch (error: Exception) {
				println(error)
			}
		}
	}

	/**
	 * 当 `Transaction` 监听到自身发起的交易的时候执行这个函数, 关闭监听以及执行动作
	 */
	private fun onTransactionSucceed() {
		data?.apply {
			updateHeaderValue(count, address, token.symbol, false, false)
			transactionObserver!!.unsubscribe()
			transactionObserver = null
			fragment.getTransactionFromChain(taxHash)
		}
	}

	// 从转账界面进入后, 自动监听交易完成后, 用来更新交易数据的工具方法
	private fun TransactionDetailFragment.getTransactionFromChain(
		taxHash: String, callback: () -> Unit = {}
	) {
		web3j.ethGetTransactionReceipt(taxHash).sendAsync().get().transactionReceipt?.let {
			context?.runOnUiThread {
				println(it)
				asyncData?.clear()
				asyncData?.addAll(generateModels(it))
				recyclerView.adapter.notifyItemRangeChanged(1, 6)
				callback()
			}
			// 成功获取数据后在异步线程更新数据库记录
			updateDataInDatabase(it)
		}
	}

	private fun TransactionDetailFragment.updateTransactionByNotificationHash(
		info: NotificationTransactionInfo, callback: () -> Unit
	) {
		web3j.ethGetTransactionByHash(info.hash).sendAsync().get().transaction?.let { receipt ->
			context?.runOnUiThread {
				// 解析 `input code` 获取 `ERC20` 接收 `address`, 及接收 `count`
				val transactionInfo = CryptoUtils.loadTransferInfoFromInputData(receipt.input)
				CryptoUtils.isERC20TransferByInputCode(receipt.input) {
					transactionInfo?.let { prepareHeaderValueFromNotification(receipt, it, info.isReceived) }
				} isFalse {
					val count = CryptoUtils.toCountByDecimal(receipt.value.toDouble(), 18.0)
					updateHeaderValue(count, receipt.from, CryptoSymbol.eth, false, info.isReceived)
				}

				if (asyncData.isNull()) {
					receipt.toAsyncData().let { asyncData = it.toArrayList() }
				}
				callback()
			}
		}
	}

	// 小函数, 通过从 `notification` 计算后传入的值来完善 `token` 基础信息的方法
	private fun prepareHeaderValueFromNotification(
		receipt: Transaction, transaction: InputCodeData, isReceive: Boolean
	) {
		DefaultTokenTable.getTokenByContractAddress(receipt.to) {
			val address = if (isReceive) receipt.from else transaction.address
			it.isNull() isTrue {
				GoldStoneEthCall.getTokenInfoByContractAddress(receipt.to) { symbol, _, decimal ->
					val count = CryptoUtils.toCountByDecimal(transaction.count, decimal)
					updateHeaderValue(count, address, symbol, false, isReceive)
				}
			} otherwise {
				val count = CryptoUtils.toCountByDecimal(transaction.count, it?.decimals.orElse(0.0))
				updateHeaderValue(count, address, it?.symbol.orEmpty(), false, isReceive)
			}
		}
	}

	// 从通知中心进入的, 使用 `web3` 获取的 `Transaction` 转换成标准的使用格式
	private fun Transaction.toAsyncData(): ArrayList<TransactionDetailModel> {
		web3j.ethGetBlockByHash(blockHash, true).sendAsync().get().result.let { block ->
			val receiptData = arrayListOf(
				(gas * gasPrice).toDouble().toEthValue(),
				"There isn't a memo",
				hash,
				blockNumber,
				formatDate(block.timestamp.toLong()),
				EtherScanApi.transactionsByHash(hash)
			)
			arrayListOf(
				TransactionText.minerFee,
				TransactionText.memo,
				TransactionText.transactionHash,
				TransactionText.blockNumber,
				TransactionText.transactionDate,
				TransactionText.url
			).mapIndexed { index, it ->
				TransactionDetailModel(receiptData[index].toString(), it)
			}.let {
				return it.toArrayList()
			}
		}
	}

	// 自动监听交易完成后, 将转账信息插入数据库
	private fun updateDataInDatabase(data: TransactionReceipt) {
		GoldStoneDataBase.database.transactionDao().apply {
			getTransactionByTaxHash(data.transactionHash)?.let {
				update(it.apply {
					blockNumber = data.blockNumber.toString()
					isPending = false
					hasError = "0"
					txreceipt_status = "1"
				})
			}
		}
	}

}