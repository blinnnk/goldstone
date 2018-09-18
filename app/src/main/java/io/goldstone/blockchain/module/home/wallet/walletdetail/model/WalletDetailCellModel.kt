package io.goldstone.blockchain.module.home.wallet.walletdetail.model

import io.goldstone.blockchain.common.component.overlay.GoldStoneDialog.Companion.chainError
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.toJsonArray
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.eos.EOSWalletType
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailFragment
import java.io.Serializable
import java.math.BigInteger

/**
 * @date 23/03/2018 11:57 PM
 * @author KaySaith
 */
data class WalletDetailCellModel(
	var iconUrl: String = "",
	var symbol: String = "",
	var tokenName: String = "",
	var decimal: Int = 0,
	var count: Double = 0.0,
	var price: Double = 0.0,
	var currency: Double = 0.0,
	var contract: String = "",
	var weight: Int = 0,
	var eosWalletType: EOSWalletType
) : Serializable {

	constructor(
		data: DefaultTokenTable,
		amount: BigInteger,
		eosWalletType: EOSWalletType
	) : this(
		data.iconUrl,
		data.symbol,
		data.name,
		data.decimals,
		CryptoUtils.toCountByDecimal(amount, data.decimals),
		data.price,
		CryptoUtils.toCountByDecimal(amount, data.decimals) * data.price,
		data.contract,
		data.weight,
		eosWalletType
	)

	constructor(
		data: DefaultTokenTable,
		balance: Double,
		eosWalletType: EOSWalletType
	) : this(
		data.iconUrl,
		data.symbol,
		data.name,
		data.decimals,
		balance,
		data.price,
		balance * data.price,
		data.contract,
		data.weight,
		eosWalletType
	)

	companion object {
		fun getLocalModels(hold: (List<WalletDetailCellModel>) -> Unit) {
			MyTokenTable.getMyTokens { myTokens ->
				// 当前钱包没有指定 `Token` 直接返回
				if (myTokens.isEmpty()) {
					hold(arrayListOf())
					return@getMyTokens
				}
				DefaultTokenTable.getCurrentChainTokens { localTokens ->
					WalletTable.getCurrentEOSWalletType { eosWalletType ->
						object : ConcurrentAsyncCombine() {
							val tokenList = ArrayList<WalletDetailCellModel>()
							override var asyncCount: Int = myTokens.size
							override fun concurrentJobs() {
								myTokens.forEach { token ->
									val type =
										if (TokenContract(token.contract).isEOS()) eosWalletType
										else EOSWalletType.None
									localTokens.find {
										it.contract.equals(token.contract, true)
									}?.let { targetToken ->
										/**
										 * 需求会在账单拉取后把当前账号地址下的 `Token` 也显示出来, 拉取账单的地方因为数量和第三方的
										 * 原因, 尽量提速的时候并没有拉取 `TokenName` 所以在这里显示以上原因插入的 `Token` 如
										 * 果 `TokenName` 为空会额外拉取一次.
										 */
										if (targetToken.name.isEmpty()) {
											targetToken.updateTokenNameFromChain(
												{
													LogUtil.error("updateTokenNameFromChain", it)
													completeMark()
												}
											) {
												// 这个 `token name` 的查询方法是 `Ethereum` 的特产, 顾不是 `EOS Wallet`
												tokenList.add(WalletDetailCellModel(it, token.balance, EOSWalletType.None))
												completeMark()
											}
										} else {
											tokenList.add(WalletDetailCellModel(targetToken, token.balance, type))
											completeMark()
										}
									}
								}
							}

							override fun mergeCallBack() = hold(tokenList)
						}.start()
					}
				}
			}
		}

		fun getChainModels(
			fragment: WalletDetailFragment,
			hold: (List<WalletDetailCellModel>) -> Unit
		) {
			/** 这个页面检查的比较频繁所以在这里通过 `Boolean` 对线程的开启状态标记 */
			fragment.presenter.isGettingDataInAsyncThreadNow = true
			/** 没有网络直接返回 */
			if (!NetworkUtil.hasNetwork(GoldStoneAPI.context)) return
			// 获取我的钱包的 `Token` 列表
			MyTokenTable.getMyTokens { myTokens ->
				// 当前钱包没有指定 `Token` 直接返回
				if (myTokens.isEmpty()) {
					hold(arrayListOf())
					return@getMyTokens
				}
				// 首先更新 `MyToken` 的 `Price`
				myTokens.updateMyTokensPrices(
					{
						// 接口可链接但返回出错的情况下不仅需进行操作
						LogUtil.error("update My Tokens Prices", it)
						return@updateMyTokensPrices
					}
				) {
					DefaultTokenTable.getCurrentChainTokens { localTokens ->
						WalletTable.getCurrentEOSWalletType { eosWalletType ->
							object : ConcurrentAsyncCombine() {
								val tokenList = ArrayList<WalletDetailCellModel>()
								override var asyncCount: Int = myTokens.size
								override fun concurrentJobs() {
									myTokens.forEach { token ->
										val type =
											if (TokenContract(token.contract).isEOS()) eosWalletType
											else EOSWalletType.None
										localTokens.find {
											it.contract.equals(token.contract, true)
										}?.let { targetToken ->
											// 链上查余额
											MyTokenTable.getBalanceByContract(
												TokenContract(targetToken.contract),
												token.ownerName,
												{ error, reason ->
													// 如果出错的话余额暂时设定用旧的值
													tokenList.add(WalletDetailCellModel(targetToken, token.balance, type))
													completeMark()
													fragment.context?.apply { chainError(reason, error, this) }
													LogUtil.error("targetToken.contract $reason", error)
												}
											) { balance ->
												// 更新数据的余额信息
												MyTokenTable.updateBalanceByContract(
													balance,
													token.ownerName,
													TokenContract(targetToken.contract)
												)
												tokenList.add(WalletDetailCellModel(targetToken, balance, type))
												completeMark()
											}
										}
									}
								}

								override fun mergeCallBack() = hold(tokenList)
							}.start()
						}
					}
				}
			}
		}

		private fun DefaultTokenTable.updateTokenNameFromChain(
			errorCallback: (Throwable?) -> Unit,
			callback: (DefaultTokenTable) -> Unit
		) {
			GoldStoneEthCall.getTokenName(
				contract,
				{ error, reason ->
					LogUtil.error("getTokenName $reason", error)
					errorCallback(error)
				},
				Config.getCurrentChainName()
			) {
				val name = if (it.isEmpty()) symbol else it
				DefaultTokenTable.updateTokenName(contract, name)
				callback(this.apply { this.name = name })
			}
		}

		private fun List<MyTokenTable>.updateMyTokensPrices(
			errorCallback: (Exception) -> Unit,
			callback: () -> Unit
		) {
			GoldStoneAPI.getPriceByContractAddress(
				map { it.contract }.toJsonArray(),
				{
					errorCallback(it)
					LogUtil.error("updateMyTokensPrices", it)
				}
			) { newPrices ->
				object : ConcurrentAsyncCombine() {
					override var asyncCount: Int = newPrices.size
					override fun concurrentJobs() {
						newPrices.forEach {
							// 同时更新缓存里面的数据
							DefaultTokenTable.updateTokenPrice(it.contract, it.price)
							completeMark()
						}
					}

					override fun mergeCallBack() = callback()
				}.start()
			}
		}
	}
}