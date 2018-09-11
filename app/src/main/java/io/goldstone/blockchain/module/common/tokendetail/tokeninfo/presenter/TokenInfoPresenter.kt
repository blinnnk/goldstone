package io.goldstone.blockchain.module.common.tokendetail.tokeninfo.presenter

import android.os.Bundle
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orElse
import com.blinnnk.extension.orZero
import com.blinnnk.extension.toMillisecond
import com.blinnnk.util.HoneyDateUtil
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.TokenDetailText
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.getGrandFather
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.litecoin.LitecoinNetParams
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.crypto.multichain.CryptoName
import io.goldstone.blockchain.crypto.multichain.CryptoSymbol
import io.goldstone.blockchain.crypto.utils.MultiChainUtils
import io.goldstone.blockchain.crypto.utils.toNoPrefixHexString
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.commonmodel.EOSTransactionTable
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.network.ChainURL
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.kernel.network.bitcoin.BitcoinApi
import io.goldstone.blockchain.kernel.network.bitcoincash.BitcoinCashApi
import io.goldstone.blockchain.kernel.network.litecoin.LitecoinApi
import io.goldstone.blockchain.module.common.tokendetail.tokendetailcenter.view.TokenDetailCenterFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokendetail.tokeninfo.view.TokenInfoFragment
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.presenter.QRCodePresenter
import org.bitcoinj.core.Address
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.params.TestNet3Params


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
		MultiChainUtils.getAddressBySymbol(tokenInfo?.symbol)
	}

	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		generateQRCode()
		showBalance()
		showAddress()
		showTransactionInfo()
		showCoinInfo(CommonText.calculating)
		setCheckDetailButtonInfo()
	}

	fun isBTCSeriesCoin(): Boolean {
		return CryptoSymbol.isBTCSeriesSymbol(tokenInfo?.symbol)
	}

	private fun showBalance() {
		MyTokenTable.getTokenBalance(
			tokenInfo?.contract.orEmpty(),
			currentAddress
		) {
			fragment.showBalance("${it.orZero()} ${tokenInfo?.symbol}")
		}
	}

	private fun generateQRCode() {
		val code = QRCodePresenter.generateQRCode(currentAddress)
		fragment.showQRCodeImage(code)
	}

	private fun showAddress() {
		val net = when (tokenInfo?.symbol) {
			CryptoSymbol.bch, CryptoSymbol.btc() ->
				if (Config.isTestEnvironment()) TestNet3Params.get() else MainNetParams.get()
			CryptoSymbol.ltc -> if (Config.isTestEnvironment()) TestNet3Params.get() else LitecoinNetParams()
			else -> null
		}
		val hash160 =
			if (net.isNull()) null
			else Address.fromBase58(net, currentAddress).hash160.toNoPrefixHexString()
		fragment.showAddress(currentAddress, hash160)
	}

	private fun setCheckDetailButtonInfo() {
		val icon = when (tokenInfo?.symbol) {
			CryptoSymbol.btc() -> R.drawable.bithumb_icon
			CryptoSymbol.ltc -> R.drawable.bithumb_icon
			CryptoSymbol.bch -> R.drawable.bithumb_icon
			CryptoSymbol.eos -> R.drawable.bithumb_icon
			CryptoSymbol.etc -> R.drawable.bithumb_icon
			else -> R.drawable.bithumb_icon
		}
		val url = when (tokenInfo?.symbol) {
			CryptoSymbol.btc() -> ChainURL.btcAddressDetail(currentAddress)
			CryptoSymbol.ltc -> ChainURL.ltcAddressDetail(currentAddress)
			CryptoSymbol.bch -> ChainURL.bchAddressDetail(currentAddress)
			CryptoSymbol.eos -> ChainURL.bchAddressDetail(currentAddress)
			CryptoSymbol.etc -> ChainURL.etcAddressDetail(currentAddress) // TODO
			else -> ChainURL.ethAddressDetail(currentAddress)
		}
		fragment.setCheckDetailButtonIconAndEvent(icon) {
			fragment
				.getGrandFather<TokenDetailOverlayFragment>()
				?.presenter
				?.showTargetFragment<WebViewFragment>(
					"Address Detail",
					TokenDetailText.tokenDetail,
					Bundle().apply { putString(ArgumentKey.webViewUrl, url) },
					2
				)
		}
	}

	private fun showTransactionInfo() {
		val chainType = ChainType.getChainTypeBySymbol(tokenInfo?.symbol)
		when {
			CryptoSymbol.isBTCSeriesSymbol(tokenInfo?.symbol) -> BTCSeriesTransactionTable
				.getTransactionsByAddressAndChainType(currentAddress, chainType) { transactions ->
					// 如果本地没有数据库那么从网络检查获取
					if (transactions.isEmpty()) {
						getBTCSeriesTransactionCountFromChain {
							fragment.showTransactionCount(it)
						}
					} else {
						// 去除燃气费的部分剩下的计算为交易数量
						fragment.showTransactionCount(transactions.filterNot { it.isFee }.size)
						// 分别查询 `接收的总值` 和 `支出的总值`
						val totalReceiveValue =
							transactions.filter { it.isReceive }.sumByDouble { it.value.toDoubleOrNull().orZero() }
						val totalSentValue =
							transactions.filter { !it.isReceive && !it.isFee }.sumByDouble { it.value.toDoubleOrNull().orZero() }
						setTotalValue(totalReceiveValue, totalSentValue)
						// 获取最近一笔交易的时间显示最后活跃时间
						val latestDate =
							HoneyDateUtil.getSinceTime(
								transactions.maxBy {
									it.timeStamp.toLongOrNull() ?: 0
								}?.timeStamp?.toMillisecond().orElse(0L)
							)
						showCoinInfo(latestDate)
					}
				}

			tokenInfo?.symbol == CryptoSymbol.eos -> {
				// TODO EOS Instead of Account name
				EOSTransactionTable.getTransactionByAccountName(currentAddress) {
					if (it.isEmpty()) {

					} else {
						fragment.showTransactionCount(it.size)
					}
				}
			}

			tokenInfo?.symbol == CryptoSymbol.etc -> {
				TransactionTable.getETCTransactionsByAddress(currentAddress) { transactions ->
					fragment.showTransactionCount(transactions.filterNot { it.isFee }.size)
					// 分别查询 `接收的总值` 和 `支出的总值`
					val totalReceiveValue =
						transactions.filter { it.isReceived }.sumByDouble { it.value.toDoubleOrNull().orZero() }
					val totalSentValue =
						transactions.filter { !it.isReceived && !it.isFee }.sumByDouble { it.value.toDoubleOrNull().orZero() }
					setTotalValue(totalReceiveValue, totalSentValue)
					// 获取最近一笔交易的时间显示最后活跃时间
					val latestDate =
						HoneyDateUtil.getSinceTime(
							transactions.maxBy {
								it.timeStamp.toLongOrNull() ?: 0
							}?.timeStamp?.toMillisecond().orElse(0L)
						)
					showCoinInfo(latestDate)
				}
			}

			else -> TransactionTable.getERCTransactionsByAddress(currentAddress) { transactions ->
				if (transactions.isEmpty()) {
					// 本地没有数据的话从链上获取 `Count`
					GoldStoneEthCall.getUsableNonce(
						{ error, reason ->
							LogUtil.error("getUsableNonce $reason", error)
						},
						ChainType.ETH,
						currentAddress
					) {
						fragment.showTransactionCount(it.toInt())
					}
				} else {
					fragment.showTransactionCount(transactions.filterNot { it.isFee }.size)
					// 分别查询 `接收的总值` 和 `支出的总值`
					val totalReceiveValue =
						transactions.filter {
							it.isReceived && it.symbol.equals(tokenInfo?.symbol, true)
						}.sumByDouble { it.value.toDoubleOrNull().orZero() }
					val totalSentValue =
						transactions.filter {
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
					showCoinInfo(latestDate)
				}
			}
		}
	}

	private fun setTotalValue(receivedValue: Double, sentValue: Double) {
		val content: (value: Double) -> String = { "$it ${tokenInfo?.symbol}" }
		fragment.showTotalValue(content(receivedValue), content(sentValue))
	}

	private fun showCoinInfo(date: String) {
		val chainName =
			CryptoName.getChainNameBySymbol(tokenInfo?.symbol).toUpperCase() + "CHAIN TYPE"
		fragment.showCoinInfo(chainName, date)
	}

	private fun getBTCSeriesTransactionCountFromChain(hold: (Int) -> Unit) {
		when (tokenInfo?.symbol) {
			CryptoSymbol.btc() -> BitcoinApi.getTransactionCount(
				currentAddress,
				{
					hold(0)
					LogUtil.error("bitcoin showTransactionCount", it)
				},
				hold
			)
			CryptoSymbol.ltc -> LitecoinApi.getTransactionCount(
				currentAddress,
				{
					hold(0)
					LogUtil.error("litecoin showTransactionCount", it)
				},
				hold
			)
			CryptoSymbol.bch -> BitcoinCashApi.getTransactionCount(
				currentAddress,
				{
					hold(0)
					LogUtil.error("bitcoin cash showTransactionCount", it)
				},
				hold
			)
			else -> hold(0)
		}
	}
}
