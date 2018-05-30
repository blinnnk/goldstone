package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.presenter

import com.blinnnk.component.HoneyBaseSwitch
import com.blinnnk.extension.forEachOrEnd
import com.blinnnk.extension.isNull
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.view.TokenManagementListAdapter
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.view.TokenManagementListFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date 25/03/2018 5:11 PM
 * @author KaySaith
 */
class TokenManagementListPresenter(
	override val fragment: TokenManagementListFragment
) : BaseRecyclerPresenter<TokenManagementListFragment, DefaultTokenTable>() {
	
	override fun updateData() {
		fragment.prepareMyDefaultTokens()
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
		updateData()
	}
	
	private fun TokenManagementListFragment.prepareMyDefaultTokens() {
		doAsync {
			// 在异步线程更新数据
			DefaultTokenTable.getDefaultTokens { defaultTokens ->
				defaultTokens.forEachOrEnd { defaultToken, isEnd ->
					MyTokenTable.getTokensWith { myTokens ->
						defaultToken.isUsed = !myTokens.find { defaultToken.contract == it.contract }.isNull()
						if (isEnd) {
							val sortedList = defaultTokens.sortedByDescending { it.weight }.toArrayList()
							// 在主线程更新 `UI`
							context?.runOnUiThread {
								if (fragment.asyncData.isNull()) fragment.asyncData = sortedList
								else diffAndUpdateSingleCellAdapterData<TokenManagementListAdapter>(sortedList)
							}
						}
					}
				}
			}
		}
	}
	
	
	companion object {
		
		fun updateMyTokensInfoBy(switch: HoneyBaseSwitch, symbol: String) {
			switch.isClickable = false
			if (switch.isChecked) {
				// once it is checked then insert this symbol into `MyTokenTable` database
				MyTokenTable.insertBySymbol(symbol, WalletTable.current.address) {
					switch.isClickable = true
				}
			} else {
				// once it is unchecked then delete this symbol from `MyTokenTable` database
				MyTokenTable.deleteBySymbol(symbol, WalletTable.current.address) {
					switch.isClickable = true
				}
			}
		}
	}
}