package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.presenter

import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.isNull
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagement.view.TokenManagementFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.view.TokenManagementListAdapter
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.view.TokenManagementListFragment
import org.jetbrains.anko.doAsync

/**
 * @date 25/03/2018 5:11 PM
 * @author KaySaith
 */

class TokenManagementListPresenter(
	override val fragment: TokenManagementListFragment
) : BaseRecyclerPresenter<TokenManagementListFragment, DefaultTokenTable>() {

	override fun updateData() {
		if (fragment.asyncData.isNull()) fragment.asyncData = arrayListOf()
		// 从异步更新数据在决定是否更新 `UI` 及内存中的数据
		// 如果是 `ETHSeries` 的 `Token` 需要额外更新
		fragment.getParentFragment<TokenManagementFragment> {
			prepareMyDefaultTokens(SharedWallet.getCurrentWalletType().isETHSeries())
		}
	}

	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		updateData()
		fragment.getParentFragment<TokenManagementFragment> {
			showCloseButton(true) {
				presenter.removeSelfFromActivity()
			}
		}
	}

	// 在异步线程更新数据
	private fun prepareMyDefaultTokens(isETHERCAndETCOnly: Boolean) {
		DefaultTokenTable.getDefaultTokens { defaultTokens ->
			MyTokenTable.getMyTokens { myTokens ->
				object : ConcurrentAsyncCombine() {
					override var asyncCount: Int = defaultTokens.size
					override fun doChildTask(index: Int) {
						defaultTokens[index].isUsed = !myTokens.find {
							defaultTokens[index].contract.equals(it.contract, true) &&
								defaultTokens[index].symbol.equals(it.symbol, true)
						}.isNull()
						completeMark()
					}

					override fun mergeCallBack() {
						val sortedList =
							defaultTokens.sortedByDescending { it.weight }.toArrayList()
						if (isETHERCAndETCOnly) sortedList.filter {
							TokenContract(it).isETH() ||
								TokenContract(it).isERC20Token() ||
								TokenContract(it).isETC()
						}.let {
							diffAndUpdateSingleCellAdapterData<TokenManagementListAdapter>(it.toArrayList())
						} else diffAndUpdateSingleCellAdapterData<TokenManagementListAdapter>(sortedList)
					}
				}.start()
			}
		}
	}

	companion object {
		fun addOrCloseMyToken(isChecked: Boolean, token: DefaultTokenTable) {
			doAsync {
				// once it is checked then insert this symbol into `MyTokenTable` database
				if (isChecked) MyTokenTable.addNewOrOpen(TokenContract(token), token.chainID)
				else GoldStoneDataBase.database.myTokenDao().updateCloseStatus(
					token.contract,
					token.symbol,
					TokenContract(token).getAddress(),
					token.chainID,
					true
				)
			}
		}
	}
}