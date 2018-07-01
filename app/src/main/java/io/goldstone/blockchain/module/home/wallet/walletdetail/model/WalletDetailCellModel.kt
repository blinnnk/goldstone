package io.goldstone.blockchain.module.home.wallet.walletdetail.model

import com.blinnnk.extension.orElse
import io.goldstone.blockchain.common.component.GoldStoneDialog.Companion.chainError
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.utils.toJsonArray
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.formatCount
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.walletdetail.view.WalletDetailFragment
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
	
	constructor(data: DefaultTokenTable, balance: Double, countHasDecimal: Boolean = false) : this(
		data.iconUrl,
		data.symbol,
		data.name,
		data.decimals,
		countHasDecimal.convertBalance(balance, data.decimals),
		data.price,
		CryptoUtils.formatDouble(
			countHasDecimal.convertBalance(balance, data.decimals) * data.price
		),
		data.contract,
		data.weight
	)
	
	companion object {
		
		fun Boolean.convertBalance(balance: Double, decimal: Double): Double {
			return if (this) {
				balance.formatCount(5).toDoubleOrNull().orElse(0.0)
			} else {
				CryptoUtils.formatDouble(balance / Math.pow(10.0, decimal))
			}
		}
		
		fun getLocalModels(
			walletAddress: String = Config.getCurrentAddress(),
			hold: (ArrayList<WalletDetailCellModel>) -> Unit
		) {
			MyTokenTable.getCurrentChainTokensWithAddress(walletAddress) { myTokens ->
				// 当前钱包没有指定 `Token` 直接返回
				if (myTokens.isEmpty()) {
					hold(arrayListOf())
					return@getCurrentChainTokensWithAddress
				}
				DefaultTokenTable.getCurrentChainTokens { localTokens ->
					object : ConcurrentAsyncCombine() {
						val tokenList = ArrayList<WalletDetailCellModel>()
						override var asyncCount: Int = myTokens.size
						override fun concurrentJobs() {
							myTokens.forEach { token ->
								localTokens.find {
									it.contract.equals(token.contract, true)
								}?.let { targetToken ->
									tokenList.add(WalletDetailCellModel(targetToken, token.balance))
								}
								completeMark()
							}
						}
						
						override fun mergeCallBack() {
							hold(tokenList)
						}
					}.start()
				}
			}
		}
		
		fun getChainModels(
			fragment: WalletDetailFragment,
			walletAddress: String = Config.getCurrentAddress(),
			hold: (ArrayList<WalletDetailCellModel>) -> Unit
		) {
			/** 这个页面检查的比较频繁所以在这里通过 `Boolean` 对线程的开启状态标记 */
			fragment.presenter.isGettingDataInAsyncThreadNow = true
			/** 没有网络直接返回 */
			if (!NetworkUtil.hasNetwork(GoldStoneAPI.context)) return
			// 获取我的钱包的 `Token` 列表
			MyTokenTable.getCurrentChainTokensWithAddress(walletAddress) { myTokens ->
				// 当前钱包没有指定 `Token` 直接返回
				if (myTokens.isEmpty()) {
					hold(arrayListOf())
					return@getCurrentChainTokensWithAddress
				}
				// 首先更新 `MyToken` 的 `Price`
				myTokens.updateMyTokensPrices {
					DefaultTokenTable.getCurrentChainTokens { localTokens ->
						object : ConcurrentAsyncCombine() {
							val tokenList = ArrayList<WalletDetailCellModel>()
							override var asyncCount: Int = myTokens.size
							override fun concurrentJobs() {
								myTokens.forEach { token ->
									localTokens.find {
										it.contract.equals(token.contract, true)
									}?.let { targetToken ->
										// 链上查余额
										MyTokenTable.getBalanceWithContract(
											targetToken.contract,
											walletAddress,
											false,
											{ error, reason ->
												completeMark()
												fragment.context?.apply { chainError(reason, error, this) }
											}
										) {
											MyTokenTable.updateCurrentWalletBalanceWithContract(it, targetToken.contract)
											tokenList.add(WalletDetailCellModel(targetToken, it))
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
		
		private fun ArrayList<MyTokenTable>.updateMyTokensPrices(callback: () -> Unit) {
			map { it.contract }.toJsonArray {
				GoldStoneAPI.getPriceByContractAddress(
					it,
					{
						callback()
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
}