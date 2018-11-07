package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.presenter

import android.support.annotation.UiThread
import com.blinnnk.extension.toArrayList
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.crypto.multichain.*
import io.goldstone.blockchain.crypto.multichain.node.ChainURL
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.ethereum.GoldStoneEthCall.getTokenInfoByContractAddress
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionHeaderModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailAdapter
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.uiThread
import java.math.BigInteger

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
		fragment.showLoadingView()
		when {
			contract.isBTCSeries() -> dataFromList?.let {
				fragment.asyncData = generateModels(it).toArrayList()
				updateHeaderValue(headerData)
				fragment.removeLoadingView()
				if (isPending) when {
					contract.isBTC() -> observerBTCTransaction()
					contract.isBCH() -> observerBCHTransaction()
					else -> observerLTCTransaction()
				}
			}

			contract.isEOSSeries() -> dataFromList?.let {
				fragment.asyncData = generateModels(it).toArrayList()
				updateHeaderValue(headerData)
				fragment.removeLoadingView()
				if (isPending) observerEOSTransaction()
			}

			contract.isETC() -> {
				getETHERC20OrETCMemo(SharedChain.getETCCurrent(), headerData)
				if (isPending) observerTransaction()
			}
			else -> {
				val chainURL = SharedChain.getCurrentETH()
				//  从 `EtherScan` 拉取账单列表的时候，并没有从链上获取
				// 未知 `Token` 的 `Name`, 这里需要额外判断补充一下.
				updateFeeTokenInfo(chainURL) { symbol, count ->
					if (symbol != null && count != null) {
						getETHERC20OrETCMemo(chainURL, TransactionHeaderModel(headerData, count, symbol))
					} else getETHERC20OrETCMemo(chainURL, headerData)
				}
				if (isPending) observerTransaction()
			}
		}
	}
}

private fun TransactionDetailPresenter.updateFeeTokenInfo(
	chainURL: ChainURL,
	@UiThread callback: (symbol: String?, count: Double?) -> Unit
) {
	dataFromList?.apply {
		// 这种情况意味着没有燃气费的条目是 `ERC20` 的条目, 没有换算出 `Symbol` 和 `Decimal`
		// 从而导致的 `Count` 为 `0` 的情况, 这里需要阻碍 `UI` 显示, 更新到这个数据后再允许显示下一步
		if (isFee && count == 0.0) {
			doAsync {
				val defaultDao =
					GoldStoneDataBase.database.defaultTokenDao()
				val transactionDao =
					GoldStoneDataBase.database.transactionDao()
				val targetToken =
					defaultDao.getERC20Token(contract.contract.orEmpty(), chainURL.chainID.id)
				// 如果本地有该条燃气费的 `DefaultToken` 信息那么直接从数据库获取信息并补全
				// 否则就获取 `ContractAddress` 从链上查询对应的数据并补全本地信息
				if (targetToken != null) {
					val count =
						CryptoUtils.toCountByDecimal(BigInteger(value), targetToken.decimals)
					transactionDao.updateFeeInfo(targetToken.symbol, count, transactionHash)
					uiThread { callback(targetToken.symbol, count) }
				} else getTokenInfoByContractAddress(contract.contract.orEmpty(), chainURL) { symbol, name, decimal, error ->
					if (error.isNone()) {
						val count = CryptoUtils.toCountByDecimal(BigInteger(value), decimal!!)
						transactionDao.updateFeeInfo(symbol!!, count, transactionHash)
						defaultDao.insert(
							DefaultTokenTable(
								contract.contract.orEmpty(),
								symbol,
								decimal,
								chainURL.chainID,
								"",
								name!!,
								true
							)
						)
						uiThread { callback(symbol, count) }
					}
				}
			}
		} else callback(null, null)
	}
}

private fun TransactionDetailPresenter.getETHERC20OrETCMemo(
	chainURL: ChainURL,
	headerData: TransactionHeaderModel
) {
	dataFromList?.apply {
		// 首先更新带入的数据
		updateHeaderValue(headerData)
		fragment.asyncData = generateModels(this).toArrayList()
		// 异步检查 Memo 是否需要更新
		TransactionTable.getMemoByHashAndReceiveStatus(currentHash, isReceived, isFee, chainURL) { memo, _ ->
			GoldStoneAPI.context.runOnUiThread {
				fragment.removeLoadingView()
				updateMemo(memo!!)
				updateHeaderValue(headerData)
			}
		}
	}
}