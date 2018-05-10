package io.goldstone.blockchain.module.home.wallet.walletdetail.model

import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.crypto.GoldStoneEthCall
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
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
		0.0,
		data.contract,
		data.weight
	) {
		currency = CryptoUtils.formatDouble(count * data.price)
	}

	companion object {

		fun getModels(
			walletAddress: String = WalletTable.current.address,
			hold: (ArrayList<WalletDetailCellModel>) -> Unit
		) {
			// 获取我的钱包的 `Token` 列表
			MyTokenTable.getTokensWith(walletAddress) { allTokens ->
				// 当前钱包没有指定 `Token` 直接返回
				if (allTokens.isEmpty()) {
					hold(arrayListOf())
					return@getTokensWith
				}
				object : ConcurrentAsyncCombine() {
					val tokenList = ArrayList<WalletDetailCellModel>()
					override var asyncCount: Int = allTokens.size
					override fun concurrentJobs() {
						DefaultTokenTable.getTokens { localTokens ->
							allTokens.forEach { token ->
								localTokens.find { it.symbol == token.symbol }?.let { targetToken ->
									/** 有网络的时候从链上更新 `Balance` */
									NetworkUtil.hasNetwork(GoldStoneAPI.context) isTrue {
										if (targetToken.symbol == CryptoSymbol.eth) {
											GoldStoneEthCall.getEthBalance(walletAddress) {
												tokenList.add(WalletDetailCellModel(targetToken, it))
												completeMark()
											}
										} else {
											GoldStoneEthCall.getTokenBalanceWithContract(targetToken.contract, walletAddress) {
												tokenList.add(WalletDetailCellModel(targetToken, it))
												completeMark()
											}
										}
									} otherwise {
										/** 没网的时候显示数据库的 `Balance` */
										tokenList.add(WalletDetailCellModel(targetToken, token.balance))
										completeMark()
									}
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