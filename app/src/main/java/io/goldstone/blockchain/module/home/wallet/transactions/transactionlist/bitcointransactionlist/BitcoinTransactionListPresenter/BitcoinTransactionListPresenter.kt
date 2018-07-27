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
	private val pageSize = 10
	
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
			val offset = Math.floor(localData.size / pageSize.toDouble()).toInt() * 20
			doAsync { loadTransactionsFromChain(offset, localData) }
		}
	}
	
	private fun loadTransactionsFromChain(
		offset: Int,
		localData: List<BitcoinTransactionTable>
	) {
		val address = if (Config.isTestEnvironment()) Config.getCurrentBTCTestAddress()
		else Config.getCurrentBTCAddress()
		BitcoinApi.getBTCTransactions(
			address,
			pageSize,
			offset,
			{
				// TODO Error Callback
				fragment.removeLoadingView()
			}
		) {
			// Calculate All Inputs to get transfer value
			it.map {
				// 转换数据格式
				BitcoinTransactionTable(it, address)
			}.filter {
				// 去除翻页机制导致的不可避免的重复数据
				localData.find { it.hash.equals(it.hash, true) }.isNull()
			}.map {
				// 插入数据到数据库
				GoldStoneDataBase.database.bitcoinTransactionDao().insert(it)
				TransactionListModel(it)
			}.let {
				val newData = it + localData.map { TransactionListModel(it) }
				// 更新 `UI` 界面
				GoldStoneAPI.context.runOnUiThread {
					fragment.removeLoadingView()
					diffAndUpdateSingleCellAdapterData<BitcoinTransactionListAdapter>(newData.toArrayList())
				}
			}
		}
	}
}