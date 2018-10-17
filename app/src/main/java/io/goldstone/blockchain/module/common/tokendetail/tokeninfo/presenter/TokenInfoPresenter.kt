package io.goldstone.blockchain.module.common.tokendetail.tokeninfo.presenter

import android.os.Bundle
import android.support.annotation.UiThread
import com.blinnnk.extension.*
import com.blinnnk.util.HoneyDateUtil
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.TokenDetailText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.crypto.litecoin.LitecoinNetParams
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.utils.toNoPrefixHexString
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.network.ChainURL
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.kernel.network.bitcoin.BitcoinApi
import io.goldstone.blockchain.kernel.network.bitcoincash.BitcoinCashApi
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.kernel.network.litecoin.LitecoinApi
import io.goldstone.blockchain.module.common.tokendetail.tokendetailcenter.view.TokenDetailCenterFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokendetail.tokeninfo.view.TokenInfoFragment
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.presenter.QRCodePresenter
import org.bitcoinj.core.Address
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.params.TestNet3Params
import org.jetbrains.anko.runOnUiThread


/**
 * @author KaySaith
 * @date  2018/09/11
 */
class TokenInfoPresenter(
	override val fragment: TokenInfoFragment
) : BasePresenter<TokenInfoFragment>() {

	private val tokenInfo by lazy {
		fragment.getParentFragment<TokenDetailCenterFragment>()?.token
	}

	private val currentAddress by lazy {
		tokenInfo?.contract.getAddress()
	}

	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		val info = getDetailButtonInfo(tokenInfo, currentAddress)
		val code = QRCodePresenter.generateQRCode(currentAddress)
		val chainName =
			CryptoName.getChainNameByContract(tokenInfo?.contract).toUpperCase() + " " + TokenDetailText.chainType
		fragment.setTokenInfo(code, chainName, CommonText.calculating, info.first) {
			showThirdPartyAddressDetail(fragment.getGrandFather<TokenDetailOverlayFragment>(), info.second)
		}
	}

	fun isBTCSeriesCoin(): Boolean {
		return CoinSymbol(tokenInfo?.symbol).isBTCSeries()
	}

	fun getBalance(hold: (String) -> Unit) {
		MyTokenTable.getTokenBalance(tokenInfo?.contract.orEmpty(), currentAddress) {
			hold("${it.orZero()} ${tokenInfo?.symbol}")
		}
	}

	fun getAddress(hold: (address: String, hash160: String?) -> Unit) {
		val net = when (tokenInfo?.symbol) {
			CoinSymbol.bch, CoinSymbol.btc() ->
				if (SharedValue.isTestEnvironment()) TestNet3Params.get() else MainNetParams.get()
			CoinSymbol.ltc -> if (SharedValue.isTestEnvironment()) TestNet3Params.get() else LitecoinNetParams()
			else -> null
		}
		val hash160 =
			if (net.isNull()) null
			else try {
				Address.fromBase58(net, currentAddress).hash160.toNoPrefixHexString()
			} catch (error: Exception) {
				null
			}
		hold(currentAddress, hash160)
	}

	fun showTransactionInfo(errorCallback: (RequestError) -> Unit) {
		val chainType = tokenInfo?.contract.getChainType().id
		when {
			tokenInfo?.contract.isBTCSeries() -> BTCSeriesTransactionTable
				.getTransactionsByAddressAndChainType(currentAddress, chainType) { transactions ->
					// 如果本地没有数据库那么从网络检查获取
					if (transactions.isEmpty()) {
						getBTCSeriesTransactionCountFromChain { count, error ->
							if (!count.isNull() && error.isNone()) {
								fragment.showTransactionCount(count)
							} else {
								fragment.context.alert(error.message)
							}

							// 如果一笔交易都没有那么设置 `Total Sent` 或 `Total Received` 都是 `0`
							if (count == 0) {
								setTotalValue(0.0, 0.0)
							}
						}
					} else {
						// 去除燃气费的部分剩下的计算为交易数量
						fragment.showTransactionCount(transactions.filterNot { it.isFee }.size)
						// 分别查询 `接收的总值` 和 `支出的总值`
						val totalReceiveValue =
							transactions.asSequence().filter { it.isReceive }.sumByDouble { it.value.toDoubleOrNull().orZero() }
						val totalSentValue =
							transactions.asSequence().filter { !it.isReceive && !it.isFee }.sumByDouble { it.value.toDoubleOrNull().orZero() }
						setTotalValue(totalReceiveValue, totalSentValue)
						// 获取最近一笔交易的时间显示最后活跃时间
						val latestDate =
							HoneyDateUtil.getSinceTime(
								transactions.maxBy {
									it.timeStamp.toLongOrNull() ?: 0
								}?.timeStamp?.toMillisecond().orElse(0L)
							)
						fragment.updateLatestActivationDate(latestDate)
					}
				}
			tokenInfo?.contract.isETC() -> TransactionTable.getETCTransactions(currentAddress) { transactions ->
				fragment.showTransactionCount(transactions.filterNot { it.isFee }.size)
				// 分别查询 `接收的总值` 和 `支出的总值`
				val totalReceiveValue =
					transactions.asSequence().filter { it.isReceived }.sumByDouble { it.value.toDoubleOrNull().orZero() }
				val totalSentValue =
					transactions.asSequence().filter { !it.isReceived && !it.isFee }.sumByDouble { it.value.toDoubleOrNull().orZero() }
				setTotalValue(totalReceiveValue, totalSentValue)
				// 获取最近一笔交易的时间显示最后活跃时间
				val latestDate =
					HoneyDateUtil.getSinceTime(
						transactions.maxBy {
							it.timeStamp.toLongOrNull() ?: 0
						}?.timeStamp?.toMillisecond().orElse(0L)
					)
				fragment.updateLatestActivationDate(latestDate)
			}

			tokenInfo?.contract.isEOSToken() -> {
				EOSAPI.getEOSCountInfo(
					SharedChain.getEOSCurrent(),
					SharedAddress.getCurrentEOSAccount(),
					tokenInfo?.contract?.contract.orEmpty(),
					tokenInfo?.symbol.orEmpty()
				) { info, error ->
					GoldStoneAPI.context.runOnUiThread {
						if (!info.isNull() && error.isNone()) {
							fragment.showTransactionCount(info!!.totalCount)
							fragment.showTotalValue("${info.totalReceived}", "${info.totalSent}")
						} else fragment.context.alert(error.message)
					}
				}
			}

			else -> TransactionTable.getTokenTransactions(currentAddress) { transactions ->
				if (transactions.isEmpty()) {
					// 本地没有数据的话从链上获取 `Count`
					GoldStoneEthCall.getUsableNonce(
						errorCallback,
						ChainType.ETH,
						currentAddress
					) {
						val convertedCount = it.toInt()
						val count = if (convertedCount > 0) convertedCount + 1 else it.toInt()
						fragment.showTransactionCount(count)
					}
				} else {
					fragment.showTransactionCount(
						transactions.filter {
							!it.isFee && it.symbol.equals(tokenInfo?.symbol, true)
						}.size
					)
					// 分别查询 `接收的总值` 和 `支出的总值`
					val totalReceiveValue =
						transactions.asSequence().filter {
							it.isReceived && it.symbol.equals(tokenInfo?.symbol, true)
						}.sumByDouble { it.value.toDoubleOrNull().orZero() }
					val totalSentValue =
						transactions.asSequence().filter {
							!it.isReceived && !it.isFee && it.symbol.equals(tokenInfo?.symbol, true)
						}.sumByDouble { it.value.toDoubleOrNull().orZero() }
					setTotalValue(totalReceiveValue, totalSentValue)
					// 获取最近一笔交易的时间显示最后活跃时间
					val latestDate =
						HoneyDateUtil.getSinceTime(
							transactions.maxBy {
								it.timeStamp.toLongOrNull() ?: 0
							}?.timeStamp?.toMillisecond().orElse(0L)
						)
					fragment.updateLatestActivationDate(latestDate)
				}
			}
		}
	}

	private fun setTotalValue(receivedValue: Double, sentValue: Double) {
		val content: (value: Double) -> String = { "$it ${tokenInfo?.symbol}" }
		fragment.showTotalValue(content(receivedValue), content(sentValue))
	}

	private fun getBTCSeriesTransactionCountFromChain(@UiThread hold: (count: Int?, error: RequestError) -> Unit) {
		when (tokenInfo?.symbol) {
			CoinSymbol.btc() -> BitcoinApi.getTransactionCount(
				currentAddress,
				{ hold(null, it) }
			) {
				GoldStoneAPI.context.runOnUiThread { hold(it, RequestError.None) }
			}
			CoinSymbol.ltc -> LitecoinApi.getTransactionCount(
				currentAddress,
				{ hold(null, it) }
			) {
				GoldStoneAPI.context.runOnUiThread { hold(it, RequestError.None) }
			}
			CoinSymbol.bch -> BitcoinCashApi.getTransactionCount(
				currentAddress,
				{ hold(null, it) }
			) {
				GoldStoneAPI.context.runOnUiThread { hold(it, RequestError.None) }
			}
			else -> hold(0, RequestError.None)
		}
	}

	companion object {
		fun getDetailButtonInfo(tokenInfo: WalletDetailCellModel?, currentAddress: String): Pair<Int, String> {
			val icon = when {
				tokenInfo?.contract.isBTC() -> R.drawable.blocktrail_icon
				tokenInfo?.contract.isLTC() -> R.drawable.blockcypher_icon
				tokenInfo?.contract.isBCH() -> R.drawable.blocktrail_icon
				tokenInfo?.contract.isEOSSeries() -> R.drawable.bloks_io_icon
				tokenInfo?.contract.isETC() -> R.drawable.gastracker_icon
				else -> R.drawable.etherscan_icon
			}
			val url = when {
				tokenInfo?.contract.isBTC() -> ChainURL.btcAddressDetail(currentAddress)
				tokenInfo?.contract.isLTC() -> ChainURL.ltcAddressDetail(currentAddress)
				tokenInfo?.contract.isBCH() -> ChainURL.bchAddressDetail(currentAddress)
				tokenInfo?.contract.isEOSSeries() -> ChainURL.eosAddressDetail(currentAddress)
				tokenInfo?.contract.isETC() -> ChainURL.etcAddressDetail(currentAddress)
				else -> ChainURL.ethAddressDetail(currentAddress)
			}
			return Pair(icon, url)
		}

		fun showThirdPartyAddressDetail(
			fragment: TokenDetailOverlayFragment?,
			url: String
		) {
			fragment?.presenter?.showTargetFragment<WebViewFragment>(
				Bundle().apply {
					putString(ArgumentKey.webViewUrl, url)
					putString(ArgumentKey.webViewName, TokenDetailText.addressDetail)
				},
				2
			)
		}
	}
}
