package io.goldstone.blockchain.module.common.tokendetail.tokeninfo.presenter

import android.os.Bundle
import android.support.annotation.WorkerThread
import com.blinnnk.extension.*
import com.blinnnk.util.HoneyDateUtil
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.TokenDetailText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.crypto.litecoin.LitecoinNetParams
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.utils.toNoPrefixHexString
import io.goldstone.blockchain.kernel.commonmodel.BTCSeriesTransactionTable
import io.goldstone.blockchain.kernel.commonmodel.MyTokenTable
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.network.ChainExplorer
import io.goldstone.blockchain.kernel.network.btcseries.insight.InsightApi
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.kernel.network.ethereum.ETHJsonRPC
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokendetail.tokeninfo.contract.TokenInfoContract
import io.goldstone.blockchain.module.common.webview.view.WebViewFragment
import io.goldstone.blockchain.module.home.wallet.walletdetail.model.WalletDetailCellModel
import io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.presenter.QRCodePresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.bitcoinj.core.Address
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.params.TestNet3Params


/**
 * @author KaySaith
 * @date  2018/09/11
 */
class TokenInfoPresenter(
	private val token: WalletDetailCellModel,
	private val infoView: TokenInfoContract.GSView
) : TokenInfoContract.GSPresenter {

	override fun start() {
		showTokenInfo()
		setBalance()
		setAddress()
		setTransactionInfo()
	}

	private fun showTokenInfo() {
		val info = getDetailButtonInfo(token.contract)
		val code = QRCodePresenter.generateQRCode(token.contract.getAddress())
		val chainName =
			CryptoName.getChainNameByContract(token.contract).toUpperCase() + " " + TokenDetailText.chainType
		infoView.setTokenInfo(code, chainName, CommonText.calculating, info.first, info.second)
	}

	private fun setBalance() {
		MyTokenTable.getTokenBalance(token.contract) {
			infoView.showBalance("${it.orZero()}" suffix token.symbol.symbol)
		}
	}

	private fun setAddress() {
		val net = when {
			token.contract.isBCH() || token.contract.isBTC() ->
				if (SharedValue.isTestEnvironment()) TestNet3Params.get() else MainNetParams.get()
			token.contract.isLTC() -> if (SharedValue.isTestEnvironment()) TestNet3Params.get() else LitecoinNetParams()
			else -> null
		}
		val address = token.contract.getAddress()
		val hash160 = if (net.isNull()) ""
		else try {
			Address.fromBase58(net, address).hash160.toNoPrefixHexString()
		} catch (error: Exception) {
			""
		}
		infoView.showAddress(address, hash160)
	}

	private fun setTransactionInfo() {
		val address = token.contract.getAddress(true)
		val chainType = token.contract.getChainType().id
		when {
			token.contract.isBTCSeries() -> GlobalScope.launch(Dispatchers.Default) {
				val transactions =
					BTCSeriesTransactionTable.dao.getTransactions(address, chainType)
				// 如果本地没有数据库那么从网络检查获取
				if (transactions.isEmpty()) getBTCSeriesTransactionCount { count, error ->
					launchUI {
						if (count != null && error.isNone()) infoView.showTransactionCount(count)
						else infoView.showError(error)
						// 如果一笔交易都没有那么设置 `Total Sent` 或 `Total Received` 都是 `0`
						val defaultValue = "0.0" suffix token.symbol.symbol
						if (count == 0) infoView.showTotalValue(defaultValue, defaultValue)
					}
				} else launchUI {
					// 去除燃气费的部分剩下的计算为交易数量
					infoView.showTransactionCount(transactions.filterNot { it.isFee }.size)
					// 分别查询 `接收的总值` 和 `支出的总值`
					val totalReceiveValue =
						transactions.asSequence().filter {
							it.isReceive
						}.sumByDouble {
							it.value.toDoubleOrNull().orZero()
						}.toString() suffix token.symbol.symbol
					val totalSentValue =
						transactions.asSequence().filter {
							!it.isReceive && !it.isFee
						}.sumByDouble {
							it.value.toDoubleOrNull().orZero()
						}.toString() suffix token.symbol.symbol
					infoView.showTotalValue(totalReceiveValue, totalSentValue)
					// 获取最近一笔交易的时间显示最后活跃时间
					val latestDate =
						HoneyDateUtil.getSinceTime(
							transactions.maxBy {
								it.timeStamp.toLongOrNull() ?: 0
							}?.timeStamp?.toMillisecond().orElse(0L)
						)
					infoView.showActivationDate(latestDate)
				}
			}
			token.contract.isETC() -> TransactionTable.getETCTransactions(address) { transactions ->
				infoView.showTransactionCount(transactions.filterNot { it.isFee }.size)
				// 分别查询 `接收的总值` 和 `支出的总值`
				val totalReceiveValue = transactions.asSequence().filter {
					it.isReceived
				}.sumByDouble {
					it.count
				}.toString() suffix token.symbol.symbol
				val totalSentValue = transactions.asSequence().filter {
					!it.isReceived && !it.isFee
				}.sumByDouble {
					it.count
				}.toString() suffix token.symbol.symbol
				infoView.showTotalValue(totalReceiveValue, totalSentValue)
				// 获取最近一笔交易的时间显示最后活跃时间
				val latestDate =
					HoneyDateUtil.getSinceTime(
						transactions.maxBy {
							it.timeStamp.toLongOrNull() ?: 0
						}?.timeStamp?.toMillisecond().orElse(0L)
					)
				infoView.showActivationDate(latestDate)
			}

			token.contract.isEOSToken() -> EOSAPI.getEOSCountInfo(
				SharedChain.getEOSCurrent().chainID,
				SharedAddress.getCurrentEOSAccount(),
				token.contract.contract,
				token.symbol
			) { info, error ->
				if (info != null && error.isNone()) launchUI {
					infoView.showTransactionCount(info.totalCount)
					infoView.showTotalValue("${info.totalReceived}", "${info.totalSent}")
				} else infoView.showError(error)
			}

			else -> GlobalScope.launch(Dispatchers.Default) {
				val transactions =
					TransactionTable.dao.getTransactionsByAddress(
						address,
						SharedChain.getCurrentETH().chainID.id
					)
				if (transactions.isEmpty()) {
					// 本地没有数据的话从链上获取 `Count`
					ETHJsonRPC.getUsableNonce(SharedChain.getCurrentETH(), address) { result, error ->
						if (result != null && error.isNone()) launchUI {
							val convertedCount = result.toInt()
							val count = if (convertedCount > 0) convertedCount + 1 else result.toInt()
							infoView.showTransactionCount(count)
						}
					}
				} else launchUI {
					infoView.showTransactionCount(
						transactions.filter {
							!it.isFee && it.symbol.equals(token.symbol.symbol, true)
						}.size
					)
					// 分别查询 `接收的总值` 和 `支出的总值`
					val totalReceiveValue =
						transactions.asSequence().filter {
							it.isReceive && it.symbol.equals(token.symbol.symbol, true)
						}.sumByDouble {
							it.count
						}.toString() suffix token.symbol.symbol
					val totalSentValue =
						transactions.asSequence().filter {
							!it.isReceive && !it.isFee && it.symbol.equals(token.symbol.symbol, true)
						}.sumByDouble {
							it.count
						}.toString() suffix token.symbol.symbol
					infoView.showTotalValue(totalReceiveValue, totalSentValue)
					// 获取最近一笔交易的时间显示最后活跃时间
					val latestDate =
						HoneyDateUtil.getSinceTime(
							transactions.maxBy {
								it.timeStamp.toLongOrNull() ?: 0
							}?.timeStamp?.toMillisecond().orElse(0L)
						)
					infoView.showActivationDate(latestDate)
				}
			}
		}
	}

	private fun getBTCSeriesTransactionCount(@WorkerThread hold: (count: Int?, error: RequestError) -> Unit) {
		InsightApi.getTransactionCount(
			token.contract.getChainType(),
			!token.contract.isBCH(),
			token.contract.getAddress(),
			hold
		)
	}

	companion object {

		fun getExplorerIcon(contract: TokenContract?): Int {
			return when {
				contract.isBTC() -> R.drawable.blocktrail_icon
				contract.isLTC() -> R.drawable.blockcypher_icon
				contract.isBCH() -> R.drawable.blocktrail_icon
				contract.isEOSSeries() -> R.drawable.bloks_io_icon
				contract.isETC() -> R.drawable.gastracker_icon
				else -> R.drawable.etherscan_icon
			}
		}

		fun getDetailButtonInfo(contract: TokenContract?): Pair<Int, String> {
			val url = when {
				contract.isBTC() -> ChainExplorer.btcAddressDetail(contract.getAddress())
				contract.isLTC() -> ChainExplorer.ltcAddressDetail(contract.getAddress())
				contract.isBCH() -> ChainExplorer.bchAddressDetail(contract.getAddress())
				contract.isEOSSeries() -> ChainExplorer.eosAddressDetail(contract.getAddress())
				contract.isETC() -> ChainExplorer.etcAddressDetail(contract.getAddress())
				else -> ChainExplorer.ethAddressDetail(contract.getAddress())
			}
			return Pair(getExplorerIcon(contract), url)
		}

		fun showThirdPartyAddressDetail(fragment: TokenDetailOverlayFragment?, url: String) {
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
