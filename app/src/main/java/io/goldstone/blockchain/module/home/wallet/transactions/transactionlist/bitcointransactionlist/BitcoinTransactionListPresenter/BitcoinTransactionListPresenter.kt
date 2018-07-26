package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.bitcointransactionlist.bitcointransactionlistPresenter

import com.blinnnk.extension.toArrayList
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.LoadingText
import io.goldstone.blockchain.kernel.commonmodel.BitcoinTransactionTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.bitcoin.BitcoinApi
import io.goldstone.blockchain.module.home.wallet.transactions.transaction.view.TransactionFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.bitcointransactionlist.view.BitcoinTransactionListAdapter
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.bitcointransactionlist.view.BitcoinTransactionListFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/7/26 11:57 PM
 * @author KaySaith
 */
class BitcoinTransactionListPresenter(
	override val fragment: BitcoinTransactionListFragment
) : BaseRecyclerPresenter<BitcoinTransactionListFragment, TransactionListModel>() {
	
	override fun updateData() {
		fragment.asyncData = arrayListOf()
	}
	
	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		fragment.getParentFragment<TransactionFragment>()?.apply {
			isBTCListShown = Runnable {
				fragment.showLoadingView(LoadingText.transactionData)
				loadTransactionsFromChain()
			}
		}
	}
	
	private fun loadTransactionsFromChain() {
		val address = if (Config.isTestEnvironment()) Config.getCurrentBTCTestAddress()
		else Config.getCurrentBTCAddress()
		BitcoinApi.getBTCTransactions(
			address,
			{
				// TODO Error Callback
				fragment.removeLoadingView()
			}
		) {
			// Calculate All Inputs to get transfer value
			it.map {
				TransactionListModel(BitcoinTransactionTable(it, address))
			}.let {
				GoldStoneAPI.context.runOnUiThread {
					fragment.removeLoadingView()
					diffAndUpdateSingleCellAdapterData<BitcoinTransactionListAdapter>(it.toArrayList())
				}
			}
		}
	}
}