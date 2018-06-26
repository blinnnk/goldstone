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
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.common.tokendetail.tokendetail.view.TokenDetailAdapter
import io.goldstone.blockchain.module.home.wallet.transactions.transaction.view.TransactionFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.classictransactionlist.view.ClassicTransactionListFragment
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.presenter.TransactionListPresenter
import org.jetbrains.anko.doAsync
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
				fragment.showChainData()
			}
		}
	}
	
	private var hasUpdateChainData: Boolean = false
	private fun ClassicTransactionListFragment.showChainData() {
		showLoadingView(LoadingText.transactionData)
		// 首先显示本地数据
		getETCTransactionsFromDatabase {
			if (asyncData.isNull()) {
				asyncData = it
			} else {
				diffAndUpdateAdapterData<TokenDetailAdapter>(it)
			}
			if (!hasUpdateChainData) {
				// 异步查询网络数据并决定是否更新
				getInvalidETCTransactionsFromChain(it) {
					removeLoadingView()
					showChainData()
					hasUpdateChainData = true
				}
			} else {
				removeLoadingView()
			}
		}
	}
	
	companion object {
		
		fun getETCTransactionsFromDatabase(
			hold: (ArrayList<TransactionListModel>) -> Unit = {}
		) {
			TransactionTable.getETCTransactionsByAddress(
				Config.getCurrentAddress()
			) {
				TransactionListPresenter.checkAddressNameInContacts(it) {
					hold(it)
				}
			}
		}
		
		fun getInvalidETCTransactionsFromChain(
			localData: ArrayList<TransactionListModel>,
			callback: () -> Unit
		) {
			doAsync {
				val blockNumber = localData.maxBy {
					it.blockNumber
				}?.blockNumber ?: "0"
				loadDataFromChain(blockNumber, localData, callback)
			}
		}
		
		private fun loadDataFromChain(
			blockNumber: String,
			localData: ArrayList<TransactionListModel>,
			callback: () -> Unit
		) {
			GoldStoneAPI.getETCTransactions(
				ChainType.ETC.id.toInt(),
				Config.getETCCurrentChain().toInt(),
				Config.getCurrentAddress(),
				blockNumber,
				{
					LogUtil.error("getETCTransactionsFromChain", it)
				}
			) { newData ->
				if (newData.isNotEmpty()) {
					newData.filterNot { new ->
						// 和本地数据去重处理
						localData.any {
							it.transactionHash.equals(new.hash, true)
						}
					}.map {
						// 加工数据并存如数据库
						TransactionTable(it).apply {
							GoldStoneDataBase.database.transactionDao().insert(this)
						}.let {
							// 返回列表需要的数据格式
							TransactionListModel(it)
						}
					}.toArrayList().apply {
						GoldStoneAPI.context.runOnUiThread {
							callback()
						}
					}
				} else {
					// 数据为空返回空数组
					GoldStoneAPI.context.runOnUiThread {
						callback()
					}
				}
			}
		}
	}
}