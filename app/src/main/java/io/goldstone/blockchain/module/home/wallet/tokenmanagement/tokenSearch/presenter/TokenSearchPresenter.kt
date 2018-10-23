package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.presenter

import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNull
import com.blinnnk.extension.toArrayList
import com.blinnnk.util.TinyNumber
import io.goldstone.blockchain.common.base.baserecyclerfragment.BaseRecyclerPresenter
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.language.LoadingText
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.crypto.multichain.CryptoValue
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.crypto.multichain.isBTCSeries
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.ethereum.GoldStoneEthCall
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.view.TokenSearchAdapter
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenSearch.view.TokenSearchFragment
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
		val navigation = fragment.getOverlayHeader()
		if (SharedWallet.getCurrentWalletType().isBTCSeries()) {
			navigation?.showSearchButton(false)
		} else {
			fragment.showLoadingView(LoadingText.searchingToken)
			MyTokenTable.getMyTokens(false) { myTokens ->
				navigation?.searchInputListener({}) { inputContent ->
					getSearchResult(inputContent, myTokens)
				}
			}
		}
	}

	private fun getSearchResult(searchContent: String, myTokens: List<MyTokenTable>) {
		myTokens.searchTokenByContractOrSymbol(searchContent) { result, error ->
			GoldStoneAPI.context.runOnUiThread {
				if (!result.isNull() && error.isNone()) {
					if (SharedWallet.getCurrentWalletType().isETHSeries())
					// 如果是以太坊钱包 Only 那么过滤掉比特币系列链的 Coin
						diffAndUpdateSingleCellAdapterData<TokenSearchAdapter>(result!!.filterNot { TokenContract(it.contract).isBTCSeries() }.toArrayList())
					else diffAndUpdateSingleCellAdapterData<TokenSearchAdapter>(result!!.toArrayList())
					fragment.removeLoadingView()
				} else fragment.context.alert(error.message)
			}
		}
	}

	fun setMyTokenStatus(searchToken: DefaultTokenTable, isChecked: Boolean, callback: () -> Unit) {
		DefaultTokenTable.getCurrentChainToken(TokenContract(searchToken.contract)) { localToken ->
			// 通过拉取账单获取的 `Token` 很可能没有名字, 这里在添加的时候顺便更新名字
			if (!localToken.isNull()) localToken!!.updateDefaultStatus(
				TokenContract(localToken.contract),
				isChecked,
				searchToken.name
			) {
				TokenManagementListPresenter.insertOrDeleteMyToken(isChecked, localToken)
				callback()
			} else searchToken.apply { isDefault = isChecked } insertThen {
				TokenManagementListPresenter.insertOrDeleteMyToken(isChecked, searchToken)
				callback()
			}
		}
	}

	private fun List<MyTokenTable>.searchTokenByContractOrSymbol(
		content: String,
		@WorkerThread hold: (data: List<DefaultTokenTable>?, error: RequestError) -> Unit
	) {
		val isSearchingSymbol = content.length != CryptoValue.contractAddressLength
		GoldStoneAPI.getTokenInfoBySymbolFromServer(content) { result, error ->
			if (!result.isNull() && error.isNone()) {
				// 从服务器请求目标结果
				hold(
					result!!.map { serverToken ->
						// 更新使用中的按钮状态
						DefaultTokenTable(serverToken).apply {
							val status = any {
								it.contract.equals(serverToken.contract, true)
							}
							isDefault = status
							isUsed = status
						}
					},
					RequestError.None
				)
			} else {
				if (isSearchingSymbol) hold(arrayListOf(), error)
				// 如果服务器没有结果返回, 那么确认是否是 `ContractAddress` 搜索, 如果是就从 `ethereum` 搜索结果
				// 判断搜索出来的 `Token` 是否是正在使用的 `Token`
				else searchERCTokenByContractFromChain(content, this, hold)
			}
		}
	}

	private fun searchERCTokenByContractFromChain(
		contract: String,
		myTokens: List<MyTokenTable>,
		hold: (data: List<DefaultTokenTable>?, error: RequestError) -> Unit
	) {
		GoldStoneEthCall.getTokenInfoByContractAddress(
			contract,
			{ fragment.context?.alert(it.message) },
			SharedChain.getCurrentETHName()
		) { symbol, name, decimal ->
			if (symbol.isEmpty() || name.isEmpty())
				hold(arrayListOf(), RequestError.NullResponse("empty symbol and name"))
			else {
				val status = myTokens.any {
					it.contract.equals(contract, true)
				}
				hold(
					listOf(
						DefaultTokenTable(
							0,
							"",
							contract,
							"",
							symbol,
							TinyNumber.False.value,
							0.0,
							name,
							decimal,
							null,
							status,
							0,
							SharedChain.getCurrentETH().id,
							isUsed = status
						)
					),
					RequestError.None
				)
			}
		}
	}
}