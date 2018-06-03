package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter

import android.os.Bundle
import android.support.v4.app.Fragment
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.TimeUtils
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.crypto.*
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.EtherScanApi
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.notifications.notification.view.NotificationFragment
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.presenter.NotificationTransactionInfo
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.transactions.transaction.view.TransactionFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.ReceiptModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionDetailModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailHeaderView
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.TransactionListModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.model.getMemoFromInputCode
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.sdk25.coroutines.onClick

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
	private val notificationData by lazy {
		fragment.arguments?.get(ArgumentKey.notificationTransaction) as? NotificationTransactionInfo
	}
	private var count = 0.0
	private var currentHash = ""
	
	override fun updateData() {
		/** 这个是从账目列表进入的详情, `Transaction List`, `TokenDetail` */
		dataFromList?.apply {
			updateHeaderValue(
				count,
				targetAddress,
				symbol,
				isPending,
				isReceived,
				hasError
			)
			
			currentHash = transactionHash
			if (memo.isEmpty()) {
				fragment.showLoadingView("Load transaction detail information")
				TransactionTable.updateTransactionMemoByHashAndReceiveStatus(transactionHash, isReceived) {
					fragment.asyncData = generateModels(this.apply { memo = it })
					updateHeaderValue(
						count,
						targetAddress,
						symbol,
						isPending,
						isReceived,
						hasError
					)
					fragment.removeLoadingView()
				}
			} else {
				fragment.asyncData = generateModels(this)
				updateHeaderValue(
					count,
					targetAddress,
					symbol,
					isPending,
					isReceived,
					hasError
				)
			}
			
			if (isPending) {
				// 异步从链上查一下这条 `taxHash` 是否有最新的状态变化
				observerTransaction()
			}
		}
		/** 这个是转账完毕后进入的初始数据 */
		data?.apply {
			currentHash = taxHash
			count = CryptoUtils.toCountByDecimal(value.toDouble(), token.decimal)
			fragment.asyncData = generateModels()
			observerTransaction()
			updateHeaderValue(
				count,
				address,
				token.symbol,
				true
			)
		}
		/** 这个是从通知中心进入的, 通知中心的显示是现查账. */
		notificationData?.let { transaction ->
			currentHash = transaction.hash
			/**
			 * 查看本地数据库是否已经记录了这条交易, 这种情况存在于, 用户收到 push 并没有打开通知中心
			 * 而是打开了账单详情. 这条数据已经被存入本地. 这个时候通知中心就不必再从链上查询数据了.
			 */
			TransactionTable.getTransactionByHashAndReceivedStatus(
				transaction.hash,
				transaction.isReceived
			) { localTransaction ->
				if (localTransaction.isNull()) {
					// 如果本地没有数据从链上查询所有需要的数据
					fragment.apply {
						showLoadingView(LoadingText.transactionData)
						updateTransactionByNotificationHash(transaction) {
							removeLoadingView()
						}
					}
				} else {
					// 本地有数据直接展示本地数据
					localTransaction?.apply {
						fragment.asyncData = generateModels(TransactionListModel(localTransaction))
						updateHeaderValue(
							value.toDouble(),
							fromAddress,
							symbol,
							false,
							isReceive,
							hasError == "1"
						)
					}
				}
			}
		}
		// 如果没有拉取到 `Input Code` 这里再拉取并存入数据库
		saveInputCodeByTaxHash(currentHash) { input, isERC20 ->
			fragment.asyncData!![1].info = getMemoFromInputCode(input, isERC20)
		}
	}
	
	private fun saveInputCodeByTaxHash(
		taxHash: String,
		callback: (input: String, isETHTransfer: Boolean) -> Unit
	) {
		doAsync {
			TransactionTable.getTransactionByHash(taxHash) {
				it.find { it.hash == taxHash }?.let { transaction ->
					if (transaction.input.isEmpty()) {
						GoldStoneEthCall.getInputCodeByHash(taxHash) {
							TransactionTable.updateInputCodeByHash(taxHash, it) {
								callback(it, transaction.isERC20)
							}
						}
					}
				}
			}
		}
	}
	
	override fun updateParentContentLayoutHeight(
		dataCount: Int?,
		cellHeight: Int,
		maxHeight: Int
	) {
		setHeightMatchParent()
	}
	
	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		fragment.setBackEventByParentFragment()
	}
	
	private fun TransactionDetailFragment.setBackEventByParentFragment() {
		parentFragment.apply {
			when (this) {
				is TransactionFragment -> {
					overlayView.header.backButton.onClick {
						headerTitle = TransactionText.detail
						presenter.popFragmentFrom<TransactionDetailFragment>()
					}
				}
				
				is TokenDetailOverlayFragment -> {
					overlayView.header.backButton.onClick {
						headerTitle = TokenDetailText.tokenDetail
						presenter.popFragmentFrom<TransactionDetailFragment>()
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
	
	fun runBackEventBy(parent: Fragment) {
		when (parent) {
			is TransactionFragment -> {
				parent.headerTitle = TransactionText.detail
				parent.presenter.popFragmentFrom<TransactionDetailFragment>()
				setHeightMatchParent()
			}
			
			is TokenDetailOverlayFragment -> {
				parent.headerTitle = TokenDetailText.tokenDetail
				parent.presenter.popFragmentFrom<TransactionDetailFragment>()
				setHeightMatchParent()
			}
			
			is NotificationFragment -> {
				parent.headerTitle = NotificationText.notification
				parent.presenter.popFragmentFrom<TransactionDetailFragment>()
				updateParentContentLayoutHeight(fragment.asyncData?.size)
			}
		}
	}
	
	fun showEtherScanTransactionFragment() {
		val argument = Bundle().apply {
			putString(ArgumentKey.webViewUrl, EtherScanApi.transactionDetail(currentHash))
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
						TransactionText.etherScanTransaction, NotificationText.notification, argument
					)
				}
			}
		}
	}
	
	// 根据传入转账信息类型, 来生成对应的更新界面的数据
	private fun generateModels(
		receipt: Any? = null
	): ArrayList<TransactionDetailModel> {
		val minerFee =
			if (data.isNull()) dataFromList?.minerFee
			else (data!!.gasLimit * data!!.gasPrice).toDouble().toEthValue()
		val date =
			if (data.isNull()) dataFromList?.date
			else TimeUtils.formatDate(data!!.timestamp / 1000)
		val memo =
			if (data?.memo.isNull()) "There isn't a memo"
			else data?.memo
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
			
			is TransactionTable -> {
				arrayListOf(
					minerFee,
					memo,
					currentHash,
					receipt.blockNumber,
					date,
					EtherScanApi.transactionDetail(currentHash)
				)
			}
			
			else -> {
				arrayListOf(
					minerFee,
					memo,
					currentHash,
					"Waiting...",
					date,
					EtherScanApi.transactionDetail(currentHash)
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
		count: Double,
		address: String,
		symbol: String,
		isPending: Boolean,
		isReceive: Boolean = false,
		isError: Boolean = false
	) {
		fragment.recyclerView.getItemAtAdapterPosition<TransactionDetailHeaderView>(0) {
			it?.setIconStyle(
				count,
				address,
				symbol,
				isReceive,
				isPending,
				isError
			)
		}
	}
	
	/** ———————————— 这里是从转账完成后跳入的账单详情界面用到的数据 ————————————*/
	private fun observerTransaction() {
		// 在页面销毁后需要用到, `activity` 所以提前存储起来
		val currentActivity = fragment.getMainActivity()
		object : TransactionStatusObserver() {
			override val transactionHash = currentHash
			override fun getStatus(status: Boolean) {
				if (status) {
					onTransactionSucceed()
					updateWalletDetailValue(currentActivity)
					removeObserver()
				}
			}
		}.start()
	}
	
	private fun updateWalletDetailValue(activity: MainActivity?) {
		updateMyTokenBalanceByTransaction {
			activity?.apply {
				supportFragmentManager.findFragmentByTag(FragmentTag.home)
					.findChildFragmentByTag<WalletDetailFragment>(FragmentTag.walletDetail)?.apply {
						runOnUiThread { presenter.updateData() }
					}
			}
		}
	}
	
	private fun updateMyTokenBalanceByTransaction(callback: () -> Unit) {
		GoldStoneEthCall
			.getTransactionByHash(currentHash) { transaction ->
				if (transaction.isERC20) {
					val contract = transaction.to
					GoldStoneEthCall.getTokenBalanceWithContract(
						contract, WalletTable.current.address
					) { balance ->
						MyTokenTable.updateCurrentWalletBalanceWithContract(balance, contract)
						callback()
					}
				} else {
					GoldStoneEthCall.getEthBalance(WalletTable.current.address) {
						MyTokenTable.updateCurrentWalletBalanceWithContract(it, CryptoValue.ethContract)
						callback()
					}
				}
			}
	}
	
	/**
	 * 当 `Transaction` 监听到自身发起的交易的时候执行这个函数, 关闭监听以及执行动作
	 */
	private fun onTransactionSucceed() {
		data?.apply {
			updateHeaderValue(count, address, token.symbol, false, false)
			fragment.getTransactionFromChain()
		}
		
		dataFromList?.apply {
			updateHeaderValue(count, addressName, symbol, false, false)
			fragment.getTransactionFromChain()
		}
	}
	
	// 从转账界面进入后, 自动监听交易完成后, 用来更新交易数据的工具方法
	private fun TransactionDetailFragment.getTransactionFromChain(
		callback: () -> Unit = {}
	) {
		GoldStoneEthCall.getTransactionByHash(currentHash) {
			context?.runOnUiThread {
				asyncData?.clear()
				asyncData?.addAll(generateModels(it))
				recyclerView.adapter.notifyItemRangeChanged(1, 6)
				callback()
			}
			// 成功获取数据后在异步线程更新数据库记录
			updateDataInDatabase(it.blockNumber)
		}
	}
	
	private fun TransactionDetailFragment.updateTransactionByNotificationHash(
		info: NotificationTransactionInfo,
		callback: () -> Unit
	) {
		GoldStoneEthCall.getTransactionByHash(currentHash) { receipt ->
			receipt.getTimestampAndInsertToDatabase { timestamp ->
				context?.runOnUiThread {
					// 解析 `input code` 获取 `ERC20` 接收 `address`, 及接收 `count`
					val transactionInfo =
						CryptoUtils.loadTransferInfoFromInputData(receipt.input)
					
					CryptoUtils.isERC20TransferByInputCode(receipt.input) {
						transactionInfo?.let {
							prepareHeaderValueFromNotification(receipt, it, info.isReceived)
						}
					} isFalse {
						val count = CryptoUtils.toCountByDecimal(receipt.value.toDouble(), 18.0)
						updateHeaderValue(
							count,
							if (info.isReceived) receipt.fromAddress else receipt.to,
							CryptoSymbol.eth,
							false,
							info.isReceived
						)
					}
					
					if (asyncData.isNull()) {
						receipt.toAsyncData().let {
							it[4].info = TimeUtils.formatDate(timestamp)
							asyncData = it
						}
					}
					callback()
				}
			}
		}
	}
	
	/**
	 * JSON RPC `GetTransactionByHash` 获取不到 `Timestamp` 需要从 `Transaction` 里面首先获取
	 * `Block Hash` 然后再发起新的 `JSON RPC` 获取  `Block` 的 `TimeStamp` 来完善交易信息.
	 */
	private fun TransactionTable.getTimestampAndInsertToDatabase(callback: (Long) -> Unit) {
		GoldStoneEthCall.getBlockTimeStampByBlockHash(blockHash) {
			this.timeStamp = it.toString()
			GoldStoneDataBase.database.transactionDao().insert(this)
			callback(it)
		}
	}
	
	// 通过从 `notification` 计算后传入的值来完善 `token` 基础信息的方法
	private fun prepareHeaderValueFromNotification(
		receipt: TransactionTable,
		transaction: InputCodeData,
		isReceive: Boolean
	) {
		DefaultTokenTable.getCurrentChainTokenByContract(receipt.to) {
			val address = if (isReceive) receipt.fromAddress else transaction.address
			it.isNull() isTrue {
				GoldStoneEthCall
					.getTokenSymbolAndDecimalByContract(receipt.to) { symbol, decimal ->
						val count = CryptoUtils.toCountByDecimal(transaction.count, decimal)
						updateHeaderValue(
							count,
							address,
							symbol,
							false,
							isReceive
						)
					}
			} otherwise {
				val count = CryptoUtils.toCountByDecimal(
					transaction.count,
					it?.decimals.orElse(0.0)
				)
				updateHeaderValue(
					count,
					address,
					it?.symbol.orEmpty(),
					false,
					isReceive
				)
			}
		}
	}
	
	/**
	 * 从通知中心进入的, 使用获取的 `Transaction` 转换成标准的使用格式, 这里临时填写
	 * `Timestamp` 数字会在准备详情界面的时候获取时间戳, 见 [getTimestampAndInsertToDatabase]
	 */
	private fun TransactionTable.toAsyncData(): ArrayList<TransactionDetailModel> {
		val receiptData = arrayListOf(
			(gas.toBigDecimal() * gasPrice.toBigDecimal()).toDouble().toEthValue(),
			"There isn't a memo",
			hash,
			blockNumber,
			TimeUtils.formatDate(0),
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
			TransactionDetailModel(receiptData[index], it)
		}.let {
			return it.toArrayList()
		}
	}
	
	// 自动监听交易完成后, 将转账信息插入数据库
	private fun updateDataInDatabase(blockNumber: String) {
		GoldStoneDataBase.database.transactionDao().apply {
			getTransactionByTaxHash(currentHash).let {
				it.forEach {
					update(it.apply {
						this.blockNumber = blockNumber
						isPending = false
						hasError = "0"
						txreceipt_status = "1"
					})
				}
			}
		}
	}
}