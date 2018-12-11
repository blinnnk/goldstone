package io.goldstone.blockchain.module.common.tokendetail.tokenasset.presenter

import android.support.annotation.WorkerThread
import com.blinnnk.extension.*
import com.blinnnk.util.HoneyDateUtil
import com.blinnnk.util.load
import com.blinnnk.util.then
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.DateAndTimeText
import io.goldstone.blockchain.common.language.TokenDetailText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.common.sharedpreference.SharedValue
import io.goldstone.blockchain.common.thread.launchUI
import io.goldstone.blockchain.crypto.eos.EOSCodeName
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.eos.account.EOSPrivateKey
import io.goldstone.blockchain.crypto.eos.base.EOSResponse
import io.goldstone.blockchain.crypto.eos.delegate.EOSDelegateTransaction
import io.goldstone.blockchain.crypto.eos.transaction.ExpirationType
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.crypto.utils.formatCount
import io.goldstone.blockchain.crypto.utils.toEOSCount
import io.goldstone.blockchain.kernel.commontable.EOSTransactionTable
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.DelegateBandWidthInfo
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.EOSAccountTable
import io.goldstone.blockchain.module.common.tokendetail.tokenasset.contract.TokenAssetContract
import io.goldstone.blockchain.module.home.wallet.walletsettings.privatekeyexport.presenter.PrivateKeyExportPresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.math.BigInteger


/**
 * @author KaySaith
 * @date  2018/09/10
 */
class TokenAssetPresenter(
	private val assetView: TokenAssetContract.GSView
) : TokenAssetContract.GSPresenter {

	private val account = SharedAddress.getCurrentEOSAccount()
	private val chainID = SharedChain.getEOSCurrent().chainID

	// 在详情界面有可能有 `Stake` 或 `Trade` 操作这里, 恢复显示的时候从
	// 数据库更新一次信息
	override fun start() {
		updateRefundInfo()
		updateAccountInfo()
		setDelegateBandwidthEOSCount()
		showTransactionCount()
	}

	override fun updateRefundInfo() {
		EOSAPI.getRecycledBandWidthList(account) { data, error ->
			if (!data.isNullOrEmpty() && error.isNone()) {
				val info = data.first()
				EOSAccountTable.dao.updateRefundData(info, account.name, chainID.id)
				launchUI {
					assetView.setEOSRefunds(info.getRefundDescription())
				}
			} else if (error.hasError()) assetView.showError(error)
		}
	}

	override fun getLatestActivationDate(contract: TokenContract, hold: (String) -> Unit) {
		GlobalScope.launch(Dispatchers.Default) {
			val time = EOSTransactionTable.dao.getMaxDataIndex(
				account.name,
				contract.contract,
				contract.symbol,
				chainID.id
			)?.time
			launchUI {
				if (time.isNotNull()) hold(HoneyDateUtil.getSinceTime(time, DateAndTimeText.getDateText()))
				else hold(CommonText.calculating)
			}
		}
	}

	private fun setDelegateBandwidthEOSCount() {
		GlobalScope.launch(Dispatchers.Default) {
			val totalDelegate =
				EOSAccountTable.dao.getAccount(account.name, chainID.id)?.totalDelegateBandInfo
			val description = if (totalDelegate.isNullOrEmpty()) {
				TokenDetailText.checkData
			} else {
				var totalEOSCount = listOf<String>()
				totalDelegate.forEach {
					totalEOSCount += it.cpuWeight
					totalEOSCount += it.netWeight
				}
				val totalDelegateCount =
					totalEOSCount.map { it.substringBefore(" ").toDoubleOrZero() }.sum()
				totalDelegateCount.formatCount(4) suffix CoinSymbol.EOS.symbol
			}
			launchUI {
				assetView.setEOSDelegateBandWidth(description)
			}
		}
	}

	override fun getDelegateBandWidthData(hold: (ArrayList<DelegateBandWidthInfo>) -> Unit) {
		load {
			EOSAccountTable.dao.getAccount(account.name, chainID.id)?.totalDelegateBandInfo
		} then {
			if (!it.isNullOrEmpty()) hold(it.toArrayList())
			else {
				assetView.showCenterLoading(true)
				EOSAPI.getDelegateBandWidthList(account) { data, error ->
					if (data.isNotNull() && error.isNone()) {
						EOSAccountTable.dao.updateDelegateBandwidthData(data, account.name, chainID.id)
						hold(data.toArrayList())
					} else assetView.showError(error)
					assetView.showCenterLoading(false)
				}
			}
		}
	}

	override fun redemptionBandwidth(
		password: String,
		receiver: EOSAccount,
		cpuAmount: BigInteger,
		netAmount: BigInteger,
		hold: (response: EOSResponse?, error: GoldStoneError) -> Unit
	) {
		PrivateKeyExportPresenter.getPrivateKey(
			SharedAddress.getCurrentEOS(),
			ChainType.EOS,
			password
		) { privateKey, error ->
			if (privateKey.isNotNull() && error.isNone()) {
				EOSDelegateTransaction(
					account,
					receiver,
					cpuAmount,
					netAmount,
					ExpirationType.FiveMinutes
				).send(EOSPrivateKey(privateKey), hold)
			} else hold(null, error)
		}
	}

	private fun updateAccountInfo() {
		GlobalScope.launch(Dispatchers.Default) {
			val localData =
				EOSAccountTable.dao.getAccount(account.name, chainID.id)
			// 本地有数据的话优先显示本地数据
			if (localData.isNotNull()) launchUI {
				localData.updateUIValue()
			}
			updateEOSAccountInfoFromChain(account, chainID) {
				updateRefundInfo()
				launchUI {
					it.updateUIValue()
				}
			}
		}
	}

	private fun showTransactionCount() {
		// 先查数据库获取交易从数量, 如果数据库数据是空的那么从网络查询转账总个数
		val account = SharedAddress.getCurrentEOSAccount()
		EOSAPI.getTransactionCount(
			SharedChain.getEOSCurrent().chainID,
			account,
			EOSCodeName.EOSIOToken.value,
			CoinSymbol.EOS
		) { latestCount, error ->
			if (latestCount.isNotNull() && error.isNone()) launchUI {
				assetView.setTransactionCount(latestCount)
			} else assetView.showError(error)
		}
	}

	private fun EOSAccountTable.updateUIValue() {
		assetView.setEOSBalance(if (balance.isEmpty()) "0.0" else balance)
		if (refundInfo.isNull()) assetView.setEOSRefunds("0.0")
		else assetView.setEOSRefunds(refundInfo.getRefundDescription())
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

	companion object {
		fun updateEOSAccountInfoFromChain(
			account: EOSAccount,
			chainID: ChainID,
			@WorkerThread callback: (account: EOSAccountTable) -> Unit
		) {
			// 异步更新网络数据
			EOSAPI.getAccountInfo(account) { eosAccount, error ->
				if (eosAccount.isNotNull() && error.isNone()) {
					// 初始化插入数据
					EOSAccountTable.updateOrInsert(eosAccount, chainID)
					callback(eosAccount)
				}
			}
		}
	}
}