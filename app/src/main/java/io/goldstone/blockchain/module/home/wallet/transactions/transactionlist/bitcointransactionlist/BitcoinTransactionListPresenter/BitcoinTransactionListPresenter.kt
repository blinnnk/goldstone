package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.bitcointransactionlist.bitcointransactionlistPresenter

import com.blinnnk.extension.isNull
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.LoadingText
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.kernel.commonmodel.BitcoinSeriesTransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.bitcoin.BitcoinApi
import io.goldstone.blockchain.module.home.wallet.transactions.transaction.view.TransactionFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.bitcointransactionlist.view.BitcoinTransactionListAdapter
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.bitcointransactionlist.view.BitcoinTransactionListFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * @date 2018/7/26 11:57 PM
 * @author KaySaith
 */
class BitcoinTransactionListPresenter(
	override val fragment: BitcoinTransactionListFragment
) : BaseRecyclerPresenter<BitcoinTransactionListFragment, TransactionListModel>() {
	
	private val address: () -> String = {
		if (Config.isTestEnvironment()) Config.getCurrentBTCTestAddress()
		else Config.getCurrentBTCAddress()
	}
	
	override fun updateData() {
		fragment.asyncData = arrayListOf()
	}
	
	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		fragment.getParentFragment<TransactionFragment>()?.apply {
			isBTCListShown = Runnable {
				fragment.showLoadingView(LoadingText.transactionData)
				// TODO 触底翻页的逻辑
				loadTransactionsFromDatabase()
			}
		}
	}
	
	private var hasLoadServerData = false
	private fun loadTransactionsFromDatabase() {
		BitcoinSeriesTransactionTable.getTransactionsByAddress(address()) { localData ->
			localData.map {
				TransactionListModel(it)
			}.let {
				diffAndUpdateSingleCellAdapterData<BitcoinTransactionListAdapter>(it.toArrayList())
			}
			// 如果已经更新了网络数据就不再继续执行
			if (!hasLoadServerData) {
				doAsync {
					loadTransactionsFromChain(
						pageSize,
						localData,
						{
							fragment.removeLoadingView()
							// TODO ERROR Alert
						}
					) {
						uiThread { fragment.removeLoadingView() }
						if (it) {
							loadTransactionsFromDatabase()
							hasLoadServerData = true
						}
					}
				}
			}
		}
	}
	
	companion object {
		const val pageSize = 40
		fun loadTransactionsFromChain(
			pageSize: Int,
			localData: List<BitcoinSeriesTransactionTable>,
			errorCallback: (Throwable) -> Unit,
			successCallback: (hasData: Boolean) -> Unit
		) {
			val offset =
				Math.floor(localData.size / pageSize.toDouble()).toInt() * pageSize
			val address = if (Config.isTestEnvironment())
				Config.getCurrentBTCTestAddress()
			else Config.getCurrentBTCAddress()
			BitcoinApi.getBTCTransactions(
				address,
				pageSize,
				offset,
				errorCallback
			) {
				// Calculate All Inputs to get transfer value
				it.map {
					// 转换数据格式
					BitcoinSeriesTransactionTable(
						it,
						CryptoSymbol.btc,
						address,
						false
					)
				}.filterNot { chainData ->
					// 去除翻页机制导致的不可避免的重复数据
					val localTransaction =
						localData.find { it.hash.equals(chainData.hash, true) }
					// 本地的数据更新网络数据, 因为本地可能有  `Pending` 拼接的数据, 所以重复的都首先更新网络
					!localTransaction?.apply {
						BitcoinSeriesTransactionTable
							.updateLocalDataByHash(
								hash,
								this,
								false,
								false
							)
					}.isNull()
				}.map {
					// 插入转账数据到数据库
					GoldStoneDataBase.database
						.bitcoinTransactionDao()
						.insert(it)
					// 同样的账单插入一份燃气费的数据
					if (!it.isReceive) {
						GoldStoneDataBase.database
							.bitcoinTransactionDao()
							.insert(it.apply { isFee = true })
					}
					TransactionListModel(it)
				}.let {
					// 更新 `UI` 界面
					successCallback(it.isNotEmpty())
				}
			}
		}
	}
}