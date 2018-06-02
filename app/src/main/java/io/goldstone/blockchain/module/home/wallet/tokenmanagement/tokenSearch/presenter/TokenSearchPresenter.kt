package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.presenter

import com.blinnnk.extension.*
import io.goldstone.blockchain.GoldStoneApp
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.value.LoadingText
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
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
	
	override fun updateParentContentLayoutHeight(
		dataCount: Int?,
		cellHeight: Int,
		maxHeight: Int
	) {
		setHeightMatchParent()
	}
	
	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		fragment.getParentFragment<TokenManagementFragment> {
			overlayView.header.searchInputLinstener {
				NetworkUtil.hasNetworkWithAlert(context) isTrue {
					searchTokenByContractOrSymbol(it) {
						context?.runOnUiThread {
							diffAndUpdateSingleCellAdapterData<TokenSearchAdapter>(it)
							fragment.removeLoadingView()
						}
					}
				}
			}
		}
	}
	
	private fun searchTokenByContractOrSymbol(
		content: String,
		hold: (ArrayList<DefaultTokenTable>) -> Unit
	) {
		val isSearchingSymbol = content.length != CryptoValue.contractAddressLength
		
		fragment.showLoadingView(LoadingText.searchingToken)
		GoldStoneAPI.getCoinInfoBySymbolFromGoldStone(content) { result ->
			result.isNullOrEmpty() isFalse {
				// 从服务器请求目标结果
				MyTokenTable.getCurrentChainTokensWith(WalletTable.current.address) { localTokens ->
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
				isSearchingSymbol isFalse {
					GoldStoneEthCall
						.getTokenInfoByContractAddress(content) { symbol, name, decimal ->
							if (symbol.isEmpty() || name.isEmpty()) {
								hold(arrayListOf())
							} else {
								hold(
									arrayListOf(
										DefaultTokenTable(
											0,
											content,
											"",
											symbol,
											TinyNumber.False.value,
											0.0,
											name,
											decimal,
											null,
											false,
											false,
											0,
											GoldStoneApp.currentChain
										)
									)
								)
							}
						}
				} otherwise {
					hold(arrayListOf())
				}
			}
		}
	}
}