package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.presenter

import com.blinnnk.component.HoneyBaseSwitch
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.TinyNumber
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.LoadingText
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.view.TokenSearchAdapter
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.view.TokenSearchCell
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.view.TokenSearchFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagement.view.TokenManagementFragment
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.presenter.TokenManagementListPresenter
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
	
	fun setMyTokenStatus(cell: TokenSearchCell) {
		cell.apply {
			model?.let { searchToken ->
				DefaultTokenTable.getCurrentChainTokenByContract(searchToken.contract) { localToken ->
					localToken.isNotNull {
						// 通过拉取账单获取的 `Token` 很可能没有名字, 这里在添加的时候更新名字顺便
						DefaultTokenTable.updateTokenDefaultStatus(
							localToken?.contract.orEmpty(),
							switch.isChecked,
							searchToken.name
						) {
							insertToMyToken(switch, localToken)
						}
					} otherwise {
						DefaultTokenTable.insertToken(searchToken.apply {
							isDefault = switch.isChecked
							// 区分 `ETC` 插入的 `ChainID`
							chain_id = CryptoValue.chainID(contract)
						}) {
							insertToMyToken(switch, searchToken)
						}
					}
				}
			}
			switch.preventDuplicateClicks()
		}
	}
	
	private fun insertToMyToken(switch: HoneyBaseSwitch, model: DefaultTokenTable?) {
		fragment.getMainActivity()?.apply {
			model?.let {
				TokenManagementListPresenter.updateMyTokensInfoBy(switch, it, this)
			}
		}
	}
	
	private fun searchTokenByContractOrSymbol(
		content: String,
		hold: (ArrayList<DefaultTokenTable>) -> Unit
	) {
		val isSearchingSymbol = content.length != CryptoValue.contractAddressLength
		
		fragment.showLoadingView(LoadingText.searchingToken)
		GoldStoneAPI.getCoinInfoBySymbolFromGoldStone(
			content,
			{
				// Usually this kinds of Exception will be connect to the service Timeout
				fragment.context?.alert(
					it.toString().trimStart {
						it.toString().startsWith(":", true)
					}
				)
			}
		) { result ->
			result.isNullOrEmpty() isFalse {
				// 从服务器请求目标结果
				MyTokenTable.getCurrentChainTokensWithAddress { localTokens ->
					result.map { serverToken ->
						// 更新使用中的按钮状态
						DefaultTokenTable(serverToken).apply {
							isUsed = localTokens.any { it.symbol.equals(serverToken.symbol, true) }
						}
					}.let {
						hold(it.toArrayList())
					}
				}
			} otherwise {
				// 如果服务器没有结果返回, 那么确认是否是 `ContractAddress` 搜索, 如果是就从 `ethereum` 搜索结果
				isSearchingSymbol isFalse {
					// 判断搜索出来的 `Token` 是否是正在使用的 `Token`
					MyTokenTable.getCurrentChainTokenByContract(content) {
						GoldStoneEthCall.getTokenInfoByContractAddress(
							content,
							{ error, reason ->
								fragment.context?.alert(reason ?: error.toString())
							},
							Config.getCurrentChainName()
						) { symbol, name, decimal ->
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
											!it.isNull(),
											0,
											Config.getCurrentChain()
										)
									)
								)
							}
						}
					}
				} otherwise {
					hold(arrayListOf())
				}
			}
		}
	}
}