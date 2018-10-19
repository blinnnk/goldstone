package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.presenter

import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.utils.getObjectMD5HexString
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
var memoryTokenData: ArrayList<DefaultTokenTable>? = null

class TokenManagementListPresenter(
	override val fragment: TokenManagementListFragment
) : BaseRecyclerPresenter<TokenManagementListFragment, DefaultTokenTable>() {

	override fun updateData() {
		// 首先显示内存中的数据
		if (fragment.asyncData.isNull()) fragment.asyncData = memoryTokenData.orEmptyArray()
		// 从异步更新数据在决定是否更新 `UI` 及内存中的数据
		// 如果是 `ETHSeries` 的 `Token` 需要额外更新
		fragment.getParentFragment<TokenManagementFragment> {
			prepareMyDefaultTokens(SharedWallet.getCurrentWalletType().isETHSeries())
		}
	}

	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		updateData()
	}

	// 在异步线程更新数据
	private fun prepareMyDefaultTokens(isETHERCAndETCOnly: Boolean) {
		DefaultTokenTable.getDefaultTokens { defaultTokens ->
			MyTokenTable.getMyTokens { myTokens ->
				object : ConcurrentAsyncCombine() {
					override var asyncCount: Int = defaultTokens.size
					override fun concurrentJobs() {
						defaultTokens.forEach { default ->
							default.isUsed = !myTokens.find {
								default.contract.equals(it.contract, true) &&
									default.symbol.equals(it.symbol, true)
							}.isNull()
							completeMark()
						}
					}

					override fun mergeCallBack() {
						val sortedList = defaultTokens.sortedByDescending { it.weight }.toArrayList()
						if (memoryTokenData?.getObjectMD5HexString() != sortedList.getObjectMD5HexString()) {
							if (isETHERCAndETCOnly) sortedList.filter {
								TokenContract(it.contract).isETH() ||
									TokenContract(it.contract).isERC20Token() ||
									TokenContract(it.contract).isETC()
							}.let {
								memoryTokenData = it.toArrayList()
							} else memoryTokenData = sortedList
							diffAndUpdateSingleCellAdapterData<TokenManagementListAdapter>(memoryTokenData.orEmptyArray())
						} else return
					}
				}.start()
			}
		}
	}

	companion object {
		fun insertOrDeleteMyToken(isChecked: Boolean, token: DefaultTokenTable) {
			doAsync {
				// once it is checked then insert this symbol into `MyTokenTable` database
				if (isChecked) MyTokenTable.addNew(TokenContract(token.contract, token.symbol), token.chainID)
				else GoldStoneDataBase.database.myTokenDao()
					.deleteByContractAndAddress(token.contract, token.symbol, TokenContract(token.contract).getAddress())
			}
		}
	}
}