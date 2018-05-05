package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.presenter

import android.widget.EditText
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.crypto.GoldStoneEthCall
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.view.TokenSearchAdapter
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.view.TokenSearchFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagement.view.TokenManagementFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.TinyNumber
import org.jetbrains.anko.runOnUiThread

/**
 * @date 27/03/2018 11:23 AM
 * @author KaySaith
 */

class TokenSearchPresenter(
	override val fragment: TokenSearchFragment
) : BaseRecyclerPresenter<TokenSearchFragment, DefaultTokenTable>() {

	override fun updateData() {
		fragment.asyncData = arrayListOf()
	}

	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		fragment.getParentFragment<TokenManagementFragment> {
			overlayView.header.setKeyboardConfirmEvent {
				NetworkUtil.hasNetwork(context) isTrue {
					searchTokenByContractOrSymbol(this) {
						fragment.context?.runOnUiThread {
							diffAndUpdateSingleCellAdapterData<TokenSearchAdapter>(it)
						}
					}
				}
			}
		}
	}

	private fun searchTokenByContractOrSymbol(
		input: EditText, hold: (ArrayList<DefaultTokenTable>) -> Unit
	) {

		val inputValue = input.text.toString()

		if (inputValue.isEmpty()) {
			fragment.context?.alert("Please enter the tokenInformation")
			return
		}

		val isSearchSymbol = inputValue.length != CryptoValue.contractAddressLength

		GoldStoneAPI.getCoinInfoBySymbolFromGoldStone(inputValue) { result ->
			result.isNotNull {
				// 从服务器请求目标结果
				MyTokenTable.getTokensWith(WalletTable.current.address) { localTokens ->
					result.map { serverToken ->
						// 更新使用中的按钮状态
						DefaultTokenTable(serverToken).apply {
							isUsed = localTokens.any { it.symbol == serverToken.symbol }
						}
					}.let {
						hold(it.toArrayList())
					}
				}
			} otherwise {
				// 如果服务器没有结果返回, 那么确认是否是 `ContractAddress` 搜索, 如果是就从 `ethereum` 搜索结果
				isSearchSymbol.isFalse {
					GoldStoneEthCall.getTokenInfoByContractAddress(inputValue) { symbol, name, decimal ->
						hold(
							arrayListOf(
								DefaultTokenTable(
									0,
									inputValue,
									"",
									symbol,
									TinyNumber.False.value,
									0.0,
									name,
									decimal,
									null,
									false,
									false
								)
							)
						)
					}
				}
			}
		}
	}
}