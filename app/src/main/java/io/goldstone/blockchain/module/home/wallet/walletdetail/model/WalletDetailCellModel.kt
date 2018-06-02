package io.goldstone.blockchain.module.home.wallet.walletdetail.model

import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.toJsonArray
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import java.io.Serializable

/**
 * @date 23/03/2018 11:57 PM
 * @author KaySaith
 */
data class WalletDetailCellModel(
	var iconUrl: String = "",
	var symbol: String = "",
	var name: String = "",
	var decimal: Double = 0.0,
	var count: Double = 0.0,
	var price: Double = 0.0,
	var currency: Double = 0.0,
	var contract: String = "",
	var weight: Int = 0
) : Serializable {
	
	constructor(data: DefaultTokenTable, balance: Double) : this(
		data.iconUrl,
		data.symbol,
		data.name,
		data.decimals,
		CryptoUtils.formatDouble(balance / Math.pow(10.0, data.decimals)),
		data.price,
		CryptoUtils.formatDouble(
			CryptoUtils.formatDouble(
				balance / Math.pow(
					10.0,
					data.decimals
				)
			) * data.price
		),
		data.contract,
		data.weight
	)
	
	companion object {
		
		fun getLocalModels(
			walletAddress: String = WalletTable.current.address,
			hold: (ArrayList<WalletDetailCellModel>) -> Unit
		) {
			MyTokenTable.getCurrentChainTokensWith(walletAddress) { allTokens ->
				// 当前钱包没有指定 `Token` 直接返回
				if (allTokens.isEmpty()) {
					hold(arrayListOf())
					return@getCurrentChainTokensWith
				}
				DefaultTokenTable.getAllTokens { localTokens ->
					object : ConcurrentAsyncCombine() {
						val tokenList = ArrayList<WalletDetailCellModel>()
						override var asyncCount: Int = allTokens.size
						override fun concurrentJobs() {
							allTokens.forEach { token ->
								localTokens.find { it.contract == token.contract }?.let { targetToken ->
									tokenList.add(WalletDetailCellModel(targetToken, token.balance))
									completeMark()
								}
							}
						}
						
						override fun mergeCallBack() =
							hold(tokenList)
					}.start()
				}
			}
		}
		
		fun getChainModels(
			walletAddress: String = WalletTable.current.address,
			hold: (ArrayList<WalletDetailCellModel>) -> Unit
		) {
			/** 没有网络直接返回 */
			if (!NetworkUtil.hasNetwork(GoldStoneAPI.context)) return
			// 获取我的钱包的 `Token` 列表
			MyTokenTable.getCurrentChainTokensWith(walletAddress) { myTokens ->
				// 当前钱包没有指定 `Token` 直接返回
				if (myTokens.isEmpty()) {
					hold(arrayListOf())
					return@getCurrentChainTokensWith
				}
				// 首先更新 `MyToken` 的 `Price`
				myTokens.updateMyTokensPrices {
					DefaultTokenTable.getCurrentChainTokens { localTokens ->
						object : ConcurrentAsyncCombine() {
							val tokenList = ArrayList<WalletDetailCellModel>()
							override var asyncCount: Int = myTokens.size
							override fun concurrentJobs() {
								myTokens.forEach { token ->
									localTokens.find { it.contract == token.contract }?.let { targetToken ->
										if (targetToken.contract == CryptoValue.ethContract) {
											GoldStoneEthCall
												.getEthBalance(walletAddress) {
													MyTokenTable
														.updateCurrentWalletBalanceWithContract(
															it,
															targetToken.contract
														)
													tokenList.add(WalletDetailCellModel(targetToken, it))
													completeMark()
												}
										} else {
											GoldStoneEthCall.getTokenBalanceWithContract(
												targetToken.contract,
												walletAddress
											) {
												MyTokenTable
													.updateCurrentWalletBalanceWithContract(
														it,
														targetToken.contract
													)
												tokenList.add(WalletDetailCellModel(targetToken, it))
												completeMark()
											}
										}
									}
								}
							}
							
							override fun mergeCallBack() {
								hold(tokenList)
							}
						}.start()
					}
				}
			}
		}
		
		private fun ArrayList<MyTokenTable>.updateMyTokensPrices(callback: () -> Unit) {
			map { it.contract }.toJsonArray {
				GoldStoneAPI.getPriceByContractAddress(it, errorCallback = {
					callback()
				}) { newPrices ->
					object : ConcurrentAsyncCombine() {
						override var asyncCount: Int = size
						override fun concurrentJobs() {
							newPrices.forEach {
								// 同时更新缓存里面的数据
								DefaultTokenTable.updateTokenPrice(it.contract, it.price) {
									completeMark()
								}
							}
						}
						
						override fun mergeCallBack() {
							callback()
						}
					}.start()
				}
			}
		}
	}
}