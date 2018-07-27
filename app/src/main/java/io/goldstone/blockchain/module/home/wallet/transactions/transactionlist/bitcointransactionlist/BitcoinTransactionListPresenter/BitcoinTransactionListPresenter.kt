package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.bitcointransactionlist.bitcointransactionlistPresenter

import com.blinnnk.extension.isNull
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.LoadingText
import io.goldstone.blockchain.kernel.commonmodel.BitcoinTransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.bitcoin.BitcoinApi
import io.goldstone.blockchain.module.home.wallet.transactions.transaction.view.TransactionFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.bitcointransactionlist.view.BitcoinTransactionListAdapter
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.bitcointransactionlist.view.BitcoinTransactionListFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/7/26 11:57 PM
 * @author KaySaith
 */
class BitcoinTransactionListPresenter(
	override val fragment: BitcoinTransactionListFragment
) : BaseRecyclerPresenter<BitcoinTransactionListFragment, TransactionListModel>() {
	
	private val address = if (Config.isTestEnvironment()) Config.getCurrentBTCTestAddress()
	else Config.getCurrentBTCAddress()
	
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
	
	private fun loadTransactionsFromDatabase() {
		BitcoinTransactionTable.getTransactionsByAddress(address) { localData ->
			localData.map {
				TransactionListModel(it)
			}.let {
				diffAndUpdateSingleCellAdapterData<BitcoinTransactionListAdapter>(it.toArrayList())
			}
			doAsync {
				loadTransactionsFromChain(
					pageSize,
					localData,
					{
						fragment.removeLoadingView()
						// TODO ERROR Alert
					}
				) {
					GoldStoneAPI.context.runOnUiThread {
						fragment.removeLoadingView()
						diffAndUpdateSingleCellAdapterData<BitcoinTransactionListAdapter>(it.toArrayList())
					}
				}
			}
		}
	}
	
	companion object {
		const val pageSize = 40
		fun loadTransactionsFromChain(
			pageSize: Int,
			localData: List<BitcoinTransactionTable>,
			errorCallback: (Exception) -> Unit,
			successCallback: (List<TransactionListModel>) -> Unit
		) {
			val offset =
				Math.floor(localData.size / pageSize.toDouble()).toInt() * pageSize
			val address =
				if (Config.isTestEnvironment())
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
					BitcoinTransactionTable(it, address)
				}.apply {
				}.filterNot { chainData ->
					// 去除翻页机制导致的不可避免的重复数据
					val localTransaction =
						localData.find { it.hash.equals(chainData.hash, true) }
					// 本地的数据更新网络数据, 因为本地可能有  `Pending` 拼接的数据, 所以重复的都首先更新网络
					!localTransaction?.apply {
						BitcoinTransactionTable
							.updateLocalDataByHash(hash, this, false)
					}.isNull()
				}.map {
					// 插入数据到数据库
					GoldStoneDataBase.database.bitcoinTransactionDao().insert(it)
					TransactionListModel(it)
				}.let {
					val newData = it + localData.map { TransactionListModel(it) }
					// 更新 `UI` 界面
					successCallback(newData.sortedByDescending { it.timeStamp })
				}
			}
		}
	}
}