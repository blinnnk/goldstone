package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.presenter

import com.blinnnk.component.HoneyBaseSwitch
import com.blinnnk.extension.isNull
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.view.TokenManagementListAdapter
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.view.TokenManagementListFragment

/**
 * @date 25/03/2018 5:11 PM
 * @author KaySaith
 */
class TokenManagementListPresenter(
	override val fragment: TokenManagementListFragment
) : BaseRecyclerPresenter<TokenManagementListFragment, DefaultTokenTable>() {
	
	override fun updateData() {
		prepareMyDefaultTokens()
	}
	
	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		updateData()
	}
	
	private fun prepareMyDefaultTokens() {
		// 在异步线程更新数据
		DefaultTokenTable.getDefaultTokens { defaultTokens ->
			object : ConcurrentAsyncCombine() {
				override var asyncCount: Int = defaultTokens.size
				
				override fun concurrentJobs() {
					defaultTokens.forEach { default ->
						MyTokenTable.getTokensWith { myTokens ->
							default.isUsed = !myTokens.find { default.contract == it.contract }.isNull()
							completeMark()
						}
					}
				}
				
				override fun mergeCallBack() {
					val sortedList = defaultTokens.sortedByDescending { it.weight }.toArrayList()
					if (fragment.asyncData.isNull()) fragment.asyncData = sortedList
					else diffAndUpdateSingleCellAdapterData<TokenManagementListAdapter>(sortedList)
				}
			}.start()
		}
	}
	
	companion object {
		
		fun updateMyTokensInfoBy(switch: HoneyBaseSwitch, symbol: String, contract: String) {
			switch.isClickable = false
			if (switch.isChecked) {
				// once it is checked then insert this symbol into `MyTokenTable` database
				MyTokenTable.insertBySymbolAndContract(symbol, contract) {
					switch.isClickable = true
				}
			} else {
				// once it is unchecked then delete this symbol from `MyTokenTable` database
				MyTokenTable.deleteByContract(contract) {
					switch.isClickable = true
				}
			}
		}
	}
}