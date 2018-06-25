package io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.classictransactionlist.presenter

import com.blinnnk.extension.isNull
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.LoadingText
import io.goldstone.blockchain.crypto.ChainType
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.wallet.transactions.transaction.view.TransactionFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.classictransactionlist.view.ClassicTransactionListAdapter
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.classictransactionlist.view.ClassicTransactionListFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.presenter.TransactionListPresenter
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/6/25 12:08 PM
 * @author KaySaith
 */
class ClassicTransactionListPresenter(
	override val fragment: ClassicTransactionListFragment
) : BaseRecyclerPresenter<ClassicTransactionListFragment, TransactionListModel>() {
	
	override fun updateData() {
		fragment.asyncData = arrayListOf()
	}
	
	override fun onFragmentViewCreated() {
		fragment.getParentFragment<TransactionFragment>()?.apply {
			isETCListShown = Runnable {
				getETCTransactionsFromChain()
			}
		}
	}
	
	private fun getETCTransactionsFromDatabase() {
		TransactionTable.getETCTransactionsByAddress(Config.getCurrentAddress()) {
		}
	}
	
	private fun getETCTransactionsFromChain() {
		fragment.showLoadingView(LoadingText.transactionData)
		GoldStoneAPI.getETCTransactions(
			ChainType.ETC.id.toInt(),
			Config.getETCCurrentChain().toInt(),
			Config.getCurrentAddress(),
			{
				LogUtil.error(this.javaClass.simpleName, it)
			}
		) {
			if (it.isNotEmpty()) {
				it.map {
					TransactionListModel(TransactionTable(it))
				}.toArrayList().apply {
					GoldStoneAPI.context.runOnUiThread {
						fragment.removeLoadingView()
						TransactionListPresenter.checkAddressNameInContacts(this@apply) {
							if (fragment.asyncData.isNull()) {
								fragment.asyncData = this@apply
							} else {
								diffAndUpdateSingleCellAdapterData<ClassicTransactionListAdapter>(this@apply)
							}
						}
					}
				}
			}
		}
	}
}