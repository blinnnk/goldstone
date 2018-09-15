package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.presenter

import com.blinnnk.component.HoneyBaseSwitch
import com.blinnnk.extension.*
import com.blinnnk.util.TinyNumber
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.language.LoadingText
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.utils.getMainActivity
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.WalletType
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.multichain.CryptoValue
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

	private var canSearch = true
	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		fragment.getParentFragment<TokenManagementFragment> {
			overlayView.header.searchInputLinstener(
				{
					// 在 `Input` focus 的时候就进行网络判断, 移除在输入的时候监听的不严谨提示.
					if (it) {
						canSearch = if (WalletType.isBTCSeriesType(Config.getCurrentWalletType())) {
							fragment.context.alert(
								"This is a single block chain wallet so you canot add other crypot currency"
							)
							false
						} else {
							NetworkUtil.hasNetworkWithAlert(context)
						}
					}
				}
			) { defaultTokens ->
				canSearch isTrue {
					searchTokenByContractOrSymbol(defaultTokens) { result ->
						context?.runOnUiThread {
							if (Config.getCurrentWalletType().equals(WalletType.ETHERCAndETCOnly.content, true)) {
								// 如果是以太坊钱包Only那么过滤掉比特币系列链的 Coin
								diffAndUpdateSingleCellAdapterData<TokenSearchAdapter>(result.filterNot {
									CoinSymbol(it.symbol).isBTCSeries()
								}.toArrayList())
							} else {
								diffAndUpdateSingleCellAdapterData<TokenSearchAdapter>(result)
							}
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
				DefaultTokenTable.getCurrentChainToken(searchToken.contract) { localToken ->
					localToken.isNotNull {
						// 通过拉取账单获取的 `Token` 很可能没有名字, 这里在添加的时候顺便更新名字
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
							chainID = CryptoValue.chainID(contract)
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
				TokenManagementListPresenter.updateMyTokensInfoBy(switch, it)
			}
		}
	}

	private fun searchTokenByContractOrSymbol(
		content: String,
		hold: (ArrayList<DefaultTokenTable>) -> Unit
	) {
		val isSearchingSymbol = content.length != CryptoValue.contractAddressLength
		fragment.showLoadingView(LoadingText.searchingToken)
		GoldStoneAPI.getTokenInfoBySymbolFromServer(
			content,
			{ it ->
				// Usually this kinds of Exception will be connect to the service Timeout
				fragment.context?.alert(
					it.toString().trimStart {
						it.toString().startsWith(":", true)
					}
				)
			}
		) { result ->
			MyTokenTable.getMyTokens { localTokens ->
				if (!result.isNullOrEmpty()) {
					// 从服务器请求目标结果
					try {
						hold(result.map { serverToken ->
							// 更新使用中的按钮状态
							DefaultTokenTable(serverToken).apply {
								val status = localTokens.any {
									it.contract.equals(serverToken.contract, true)
								}
								isDefault = status
								isUsed = status
							}
						}.toArrayList())
					} catch (error: Exception) {
						LogUtil.error("getTokenInfoBySymbolFromServer", error)
					}
				} else {
					if (isSearchingSymbol) {
						hold(arrayListOf())
						return@getMyTokens
					}
					// 如果服务器没有结果返回, 那么确认是否是 `ContractAddress` 搜索, 如果是就从 `ethereum` 搜索结果
					// 判断搜索出来的 `Token` 是否是正在使用的 `Token`
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
							val status = localTokens.any {
								it.contract.equals(content, true)
							}
							hold(
								arrayListOf(
									DefaultTokenTable(
										0,
										"",
										content,
										"",
										symbol,
										TinyNumber.False.value,
										0.0,
										name,
										decimal,
										null,
										status,
										0,
										CoinSymbol(symbol).getChainID(),
										isUsed = status
									)
								)
							)
						}
					}
				}
			}
		}
	}
}