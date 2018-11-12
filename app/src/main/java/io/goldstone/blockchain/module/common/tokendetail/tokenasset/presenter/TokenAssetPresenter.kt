package io.goldstone.blockchain.module.common.tokendetail.tokenasset.presenter

import com.blinnnk.extension.suffix
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.common.utils.NetworkUtil
import io.goldstone.blockchain.crypto.eos.EOSCodeName
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.utils.formatCount
import io.goldstone.blockchain.crypto.utils.toEOSCount
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.EOSAccountTable
import io.goldstone.blockchain.module.common.tokendetail.tokenasset.contract.TokenAssetContract
import kotlinx.coroutines.*


/**
 * @author KaySaith
 * @date  2018/09/10
 */
class TokenAssetPresenter(
	private val assetView: TokenAssetContract.GSView
) : TokenAssetContract.GSPresenter {

	// 在详情界面有可能有 `Stake` 或 `Trade` 操作这里, 恢复显示的时候从
	// 数据库更新一次信息
	override fun start() {
		if (NetworkUtil.hasNetwork(GoldStoneAPI.context))
			showTransactionCount()
		updateAccountInfo()
	}

	private fun updateAccountInfo(onlyUpdateLocalData: Boolean = false) {
		val accountDao =
			GoldStoneDataBase.database.eosAccountDao()
		val account = SharedAddress.getCurrentEOSAccount()
		fun showLocalAccounts() = GlobalScope.launch {
			val localData =
				async(Dispatchers.Default) {
					accountDao.getAccount(account.accountName)
				}
			// 首先显示数据库的数据在界面上
			withContext(Dispatchers.Main) {
				localData.await()?.updateUIValue()
			}
		}
		if (onlyUpdateLocalData || !NetworkUtil.hasNetwork(GoldStoneAPI.context))
			showLocalAccounts()
		else EOSAPI.getAccountInfo(account) { eosAccount, error ->
			if (eosAccount != null && error.isNone()) {
				accountDao.insert(eosAccount)
				showLocalAccounts()
			} else assetView.showError(error)
		}
	}

	private fun showTransactionCount() {
		// 先查数据库获取交易从数量, 如果数据库数据是空的那么从网络查询转账总个数
		val account = SharedAddress.getCurrentEOSAccount()
		EOSAPI.getTransactionCount(
			SharedChain.getEOSCurrent().chainID,
			account,
			EOSCodeName.EOSIOToken.value,
			CoinSymbol.eos
		) { latestCount, error ->
			if (latestCount != null && error.isNone()) launchUI {
				assetView.setTransactionCount(latestCount)
			} else assetView.showError(error)
		}
	}

	private fun EOSAccountTable.updateUIValue() {
		assetView.setEOSBalance(if (balance.isEmpty()) "0.0" else balance)
		if (refundInfo == null) assetView.setEOSRefunds("0.0")
		else refundInfo.getRefundDescription().let { assetView.setEOSRefunds(it) }
		val availableRAM = ramQuota - ramUsed
		val availableCPU = cpuLimit.max - cpuLimit.used
		val cpuEOSValue = "${cpuWeight.toEOSCount()}" suffix CoinSymbol.eos
		val availableNet = netLimit.max - netLimit.used
		val netEOSValue = "${netWeight.toEOSCount()}" suffix CoinSymbol.eos
		val ramEOSCount =
			"≈ " + (availableRAM.toDouble() * SharedValue.getRAMUnitPrice() / 1024).formatCount(4) suffix CoinSymbol.eos
		assetView.setResourcesValue(
			availableRAM,
			ramQuota,
			ramEOSCount,
			availableCPU,
			cpuLimit.max,
			cpuEOSValue,
			availableNet,
			netLimit.max,
			netEOSValue
		)
	}
}