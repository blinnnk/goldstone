package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.presenter

import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.view.TokenManagementListFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date 25/03/2018 5:11 PM
 * @author KaySaith
 */
// 获取数据后把默认列表防止到内存里加快每次访问的速度
private var defaultTokenList: ArrayList<DefaultTokenTable>? = null

class TokenManagementListPresenter(
	override val fragment: TokenManagementListFragment
) : BaseRecyclerPresenter<TokenManagementListFragment, DefaultTokenTable>() {
	
	override fun updateData() {
		defaultTokenList.isNull() isFalse {
			fragment.asyncData = defaultTokenList
			fragment.prepareMyDefaultTokens()
		} otherwise {
			fragment.prepareMyDefaultTokens()
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
		updateData()
	}
	
	private fun TokenManagementListFragment.prepareMyDefaultTokens() {
		doAsync {
			// 在异步线程更新数据
			DefaultTokenTable.getDefaultTokens { defaultTokens ->
				defaultTokens.forEachOrEnd { defaultToken, isEnd ->
					val address = WalletTable.current.address
					MyTokenTable.getTokensWith(address) { myTokens ->
						defaultToken.isUsed = !myTokens.find { defaultToken.symbol == it.symbol }.isNull()
						if (isEnd) {
							val sortedList = defaultTokens.sortedByDescending { it.weight }.toArrayList()
							// 在主线程更新 `UI`
							context?.runOnUiThread {
								asyncData.isNull() isTrue {
									asyncData = sortedList
								} otherwise {
									asyncData = sortedList
									fragment.recyclerView.adapter.notifyDataSetChanged()
								}
								defaultTokenList = sortedList
							}
						}
					}
				}
			}
		}
	}
	
	companion object {
		
		fun updateMyTokensInfoBy(isSelected: Boolean, symbol: String) {
			if (isSelected) {
				// once it is checked then insert this symbol into `MyTokenTable` database
				MyTokenTable.insertBySymbol(symbol, WalletTable.current.address)
			} else {
				// once it is unchecked then delete this symbol from `MyTokenTable` database
				MyTokenTable.deleteBySymbol(symbol, WalletTable.current.address)
			}
		}
	}
}