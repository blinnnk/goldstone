package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.presenter

import android.content.Context
import com.blinnnk.component.HoneyBaseSwitch
import com.blinnnk.extension.getParentFragment
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orEmptyArray
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.component.GoldStoneDialog
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.ErrorTag
import io.goldstone.blockchain.crypto.getObjectMD5HexString
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagement.view.TokenManagementFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.view.TokenManagementListAdapter
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.view.TokenManagementListFragment

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
		fragment.getParentFragment<TokenManagementFragment> {
			afterSetHeightAnimation = Runnable { prepareMyDefaultTokens() }
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
		prepareMyDefaultTokens()
	}
	
	private fun prepareMyDefaultTokens() {
		// 在异步线程更新数据
		DefaultTokenTable.getDefaultTokens { defaultTokens ->
			object : ConcurrentAsyncCombine() {
				override var asyncCount: Int = defaultTokens.size
				
				override fun concurrentJobs() {
					defaultTokens.forEach { default ->
						MyTokenTable.getCurrentChainTokensWithAddress { myTokens ->
							default.isUsed = !myTokens.find { default.contract == it.contract }.isNull()
							completeMark()
						}
					}
				}
				
				override fun mergeCallBack() {
					val sortedList = defaultTokens.sortedByDescending { it.weight }.toArrayList()
					if (memoryTokenData?.getObjectMD5HexString() != sortedList.getObjectMD5HexString()) {
						memoryTokenData = sortedList
						diffAndUpdateSingleCellAdapterData<TokenManagementListAdapter>(memoryTokenData.orEmptyArray())
					} else {
						return
					}
				}
			}.start()
		}
	}
	
	companion object {
		
		fun updateMyTokensInfoBy(
			switch: HoneyBaseSwitch,
			token: DefaultTokenTable,
			context: Context
		) {
			switch.isClickable = false
			if (switch.isChecked) {
				// once it is checked then insert this symbol into `MyTokenTable` database
				MyTokenTable.insertBySymbolAndContract(
					token.symbol,
					token.contract,
					{ error, reason ->
						if (reason.equals(ErrorTag.chain, true)) {
							GoldStoneDialog.showChainErrorDialog(context)
						}
						LogUtil.error("updateMyTokensInfoBy, error: $reason", error)
					}) {
					switch.isClickable = true
				}
			} else {
				// once it is unchecked then delete this symbol from `MyTokenTable` database
				MyTokenTable.deleteByContract(token.contract) {
					switch.isClickable = true
				}
			}
		}
	}
}