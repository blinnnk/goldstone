package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter

import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.language.LoadingText
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.multichain.CryptoSymbol
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.commonmodel.eos.EOSTransactionTable
import io.goldstone.blockchain.kernel.network.ChainURL
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionHeaderModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactionlist.ethereumtransactionlist.model.TransactionListModel
import org.jetbrains.anko.runOnUiThread

/**
 * @date 2018/6/6 3:59 PM
 * @author KaySaith
 */
fun TransactionDetailPresenter.updateDataFromTransactionList() {
	dataFromList?.apply {
		val headerData = TransactionHeaderModel(
			count,
			if (isReceived) fromAddress else toAddress,
			symbol,
			isPending,
			isReceived,
			hasError
		)
		headerModel = headerData
		currentHash = transactionHash
		fragment.showLoadingView(LoadingText.loadingDataFromChain)

		when {
			CryptoSymbol.isBTCSeriesSymbol(symbol) -> {
				dataFromList?.let {
					fragment.asyncData = generateModels(it).toArrayList()
					updateHeaderValue(headerData)
					fragment.removeLoadingView()
					if (isPending) {
						when {
							symbol.equals(CryptoSymbol.btc(), true) -> observerBTCTransaction()
							symbol.equals(CryptoSymbol.bch, true) -> observerBCHTransaction()
							else -> observerLTCTransaction()
						}
					}
				}
			}
			// 目前还没找到判断 EOS Token 系列的统一方法, 暂时使用 AccountName 进行判断
			// 有可能其中地址是超短地址, 这个暂时没法判断, 所以对来往地址双重验证
			EOSWalletUtils.isValidAccountName(fromAddress) ||
				EOSWalletUtils.isValidAccountName(toAddress) -> {
				dataFromList?.let {
					// 本地没有 Miner 信息那么从链上拉取
					fun updateUI(miner: String = "") {
						fragment.asyncData = generateModels(it).toArrayList().apply {
							if (miner.isNotEmpty()) first().info = miner
						}
						updateHeaderValue(headerData)
						fragment.removeLoadingView()
					}
					if (it.minerFee.isEmpty()) getBandWidthUsageAndStatusBy(transactionHash) { cpuUsage, netUsage ->
						val miner = TransactionListModel.generateEOSMinerContent(cpuUsage, netUsage)
						updateUI(miner)
					} else updateUI()
				}
			}

			symbol.equals(CryptoSymbol.etc, true) -> {
				getETHERC20OrETCMemo(headerData)
				if (isPending) observerTransaction()
			}

			else -> {
				//  从 `EtherScan` 拉取账单列表的时候，并没有从链上获取
				// 未知 `Token` 的 `Name`, 这里需要额外判断补充一下.
				checkTokenNameInfoOrUpdate()
				getETHERC20OrETCMemo(headerData)
				if (isPending) observerTransaction()
			}
		}
	}
}

private fun getBandWidthUsageAndStatusBy(txID: String, hold: (cpuUsage: Long, netUsage: Long) -> Unit) {
	EOSAPI.getCPUAndNETUsageByTxID(
		txID,
		{
			LogUtil.error("getBandWidthUsageAndStatusBy", it)
		}
	) { cpuUsage, netUsage, status ->
		EOSTransactionTable.updateBandWidthAndStatusBy(txID, cpuUsage, netUsage, status)
		GoldStoneAPI.context.runOnUiThread { hold(cpuUsage, netUsage) }
	}
}

private fun TransactionDetailPresenter.getETHERC20OrETCMemo(headerData: TransactionHeaderModel) {
	dataFromList?.apply {
		TransactionTable.getMemoByHashAndReceiveStatus(
			currentHash,
			isReceived,
			if (symbol.equals(CryptoSymbol.etc, true)) Config.getETCCurrentChainName()
			else getCurrentChainName()
		) { memo ->
			fragment.removeLoadingView()
			fragment.asyncData = generateModels(this).toArrayList().apply {
				this[1].info = memo
			}
			updateHeaderValue(headerData)
		}
	}
}

private fun TransactionListModel.checkTokenNameInfoOrUpdate() {
	DefaultTokenTable.getCurrentChainToken(contract) { defaultToken ->
		defaultToken?.apply {
			if (name.isEmpty()) {
				GoldStoneEthCall.getTokenName(
					contract,
					{ error, reason ->
						LogUtil.error("getCurrentChainToken $reason", error)
					},
					ChainURL.getChainNameBySymbol(symbol)
				) {
					DefaultTokenTable.updateTokenName(contract, it)
				}
			}
		}
	}
}