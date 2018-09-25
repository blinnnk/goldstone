package io.goldstone.blockchain.module.home.wallet.walletdetail.model

import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.error.EthereumRPCError
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.utils.ConcurrentAsyncCombine
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.eos.EOSWalletType
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.crypto.multichain.isEOS
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
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
	var contract: TokenContract,
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
		TokenContract(data.contract),
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
		TokenContract(data.contract),
		data.weight,
		eosWalletType
	)

	companion object {
		fun getLocalModels(hold: (List<WalletDetailCellModel>, List<MyTokenTable>) -> Unit) {
			MyTokenTable.getMyTokens(false) { myTokens ->
				// 当前钱包没有指定 `Token` 直接返回
				if (myTokens.isEmpty()) hold(arrayListOf(), myTokens)
				else DefaultTokenTable.getCurrentChainTokens { localTokens ->
					WalletTable.getCurrentEOSWalletType { eosWalletType ->
						object : ConcurrentAsyncCombine() {
							val tokenList = ArrayList<WalletDetailCellModel>()
							override var asyncCount: Int = myTokens.size
							override fun concurrentJobs() {
								myTokens.forEach { token ->
									System.out.println("$+++++$token")
									val type =
										if (TokenContract(token.contract).isEOS()) eosWalletType else EOSWalletType.None
									localTokens.find {
										it.contract.equals(token.contract, true)
									}?.let { targetToken ->
										/**
										 * 需求会在账单拉取后把当前账号地址下的 `Token` 也显示出来, 拉取账单的地方因为数量和第三方的
										 * 原因, 尽量提速的时候并没有拉取 `TokenName` 所以在这里显示以上原因插入的 `Token` 如
										 * 果 `TokenName` 为空会额外拉取一次.
										 */
										if (targetToken.name.isEmpty()) targetToken.updateTokenNameFromChain(
											{
												LogUtil.error("updateTokenNameFromChain", it)
												completeMark()
											}
										) {
											// 这个 `token name` 的查询方法是 `Ethereum` 的特产, 顾不是 `EOS Wallet`
											tokenList.add(WalletDetailCellModel(it, token.balance, EOSWalletType.None))
											completeMark()
										} else {
											tokenList.add(WalletDetailCellModel(targetToken, token.balance, type))
											completeMark()
										}
									}
								}
							}

							override fun mergeCallBack() = hold(tokenList, myTokens)
						}.start()
					}
				}
			}
		}

		fun getChainModels(
			myTokens: List<MyTokenTable>,
			hold: (List<WalletDetailCellModel>?, GoldStoneError) -> Unit
		): Boolean {
			var balanceError = GoldStoneError.None
			// 没有网络直接返回
			return when {
				!NetworkUtil.hasNetwork(GoldStoneAPI.context) -> false
				// 当前钱包没有指定 `Token` 直接返回
				myTokens.isEmpty() -> {
					hold(arrayListOf(), RequestError.None)
					false
				}
				else -> {
					// 首先更新 `MyToken` 的 `Price`
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
												token.ownerName
											) { balance, error ->
												// 更新数据的余额信息
												if (!balance.isNull() && error.isNone()) {
													MyTokenTable.updateBalanceByContract(
														balance!!,
														token.ownerName,
														TokenContract(targetToken.contract)
													)
													System.out.println("fuck ${token.ownerName} and $balance")
													tokenList.add(WalletDetailCellModel(targetToken, balance, type))
												} else {
													balanceError = error
													// 如果有错的话那么直接显示本地的余额
													tokenList.add(WalletDetailCellModel(targetToken, token.balance, type))
												}
												completeMark()
											}
										}
									}
								}

								override fun mergeCallBack() = hold(tokenList, balanceError)

							}.start()
						}
					}
					true // 正在执行线程
				}
			}
		}

		private fun DefaultTokenTable.updateTokenNameFromChain(
			errorCallback: (EthereumRPCError?) -> Unit,
			callback: (DefaultTokenTable) -> Unit
		) {
			GoldStoneEthCall.getTokenName(
				contract,
				{
					callback(this)
					LogUtil.error("getTokenName ", it)
					errorCallback(it)
				},
				Config.getCurrentChainName()
			) {
				val name = if (it.isEmpty()) symbol else it
				DefaultTokenTable.updateTokenName(TokenContract(contract), name)
				callback(apply { this.name = name })
			}
		}
	}
}