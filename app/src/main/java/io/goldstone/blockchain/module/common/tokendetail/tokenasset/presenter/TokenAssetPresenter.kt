package io.goldstone.blockchain.module.common.tokendetail.tokenasset.presenter

import android.os.Bundle
import com.blinnnk.extension.getGrandFather
import com.blinnnk.extension.isNull
import com.blinnnk.extension.suffix
import com.blinnnk.util.getParentFragment
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.TokenDetailText
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.ArgumentKey
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.eos.EOSUnit
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.utils.formatCount
import io.goldstone.blockchain.crypto.utils.toEOSCount
import io.goldstone.blockchain.kernel.commonmodel.eos.EOSTransactionTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.kernel.network.eos.eosram.EOSResourceUtil
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.EOSAccountTable
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.view.EOSAccountSelectionFragment
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.cputradingdetail.view.CPUTradingFragment
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.nettradingdetail.view.NETTradingFragment
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.ramtradingdetail.view.RAMTradingFragment
import io.goldstone.blockchain.module.common.tokendetail.tokenasset.view.TokenAssetFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetailcenter.view.TokenDetailCenterFragment
import io.goldstone.blockchain.module.common.tokendetail.tokendetailoverlay.view.TokenDetailOverlayFragment
import io.goldstone.blockchain.module.common.tokendetail.tokeninfo.presenter.TokenInfoPresenter
import io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.presenter.QRCodePresenter
import org.jetbrains.anko.runOnUiThread


/**
 * @author KaySaith
 * @date  2018/09/10
 */
class TokenAssetPresenter(
	override val fragment: TokenAssetFragment
) : BasePresenter<TokenAssetFragment>() {
	private val tokenInfo by lazy {
		fragment.getParentFragment<TokenDetailCenterFragment>()?.token
	}

	private val currentAddress by lazy {
		CoinSymbol(tokenInfo?.symbol).getAddress()
	}

	override fun onFragmentShowFromHidden() {
		super.onFragmentShowFromHidden()
		// 在详情界面有可能有 `Stake` 或 `Trade` 操作这里, 恢复显示的时候从
		// 数据库更新一次信息
		updateAccountInfo()
	}

	override fun onFragmentViewCreated() {
		super.onFragmentViewCreated()
		getAccountTransactionCount()
		val info = TokenInfoPresenter.getDetailButtonInfo(tokenInfo, currentAddress)
		val code = QRCodePresenter.generateQRCode(currentAddress)
		val chainName = CoinSymbol.eos suffix TokenDetailText.chainType
		fragment.setTokenInfo(code, chainName, CommonText.calculating, info.first) {
			TokenInfoPresenter.showThirdPartyAddressDetail(
				fragment.getGrandFather<TokenDetailOverlayFragment>(),
				info.second
			)
		}
		updateAccountInfo()
	}

	fun showPublicKeyAccountNames() {
		fragment.getGrandFather<TokenDetailOverlayFragment>()
			?.presenter?.showTargetFragment<EOSAccountSelectionFragment>(
			Bundle().apply {
				putString(ArgumentKey.defaultEOSAccountName, Config.getCurrentEOSAccount().accountName)
			},
			2
		)
	}

	fun showResourceTradingFragmentByTitle(title: String) {
		val tokenDetailOverlayPresenter =
			fragment.getGrandFather<TokenDetailOverlayFragment>()?.presenter
		when (title) {
			TokenDetailText.delegateCPU -> tokenDetailOverlayPresenter
				?.showTargetFragment<CPUTradingFragment>(
					Bundle(),
					2
				)
			TokenDetailText.delegateNET -> tokenDetailOverlayPresenter
				?.showTargetFragment<NETTradingFragment>(
					Bundle(),
					2
				)
			TokenDetailText.buySellRAM -> tokenDetailOverlayPresenter
				?.showTargetFragment<RAMTradingFragment>(
					Bundle(),
					2
				)
		}
	}

	private fun updateAccountInfo(onlyUpdateLocalData: Boolean = false) {
		val account = Config.getCurrentEOSAccount()
		EOSAccountTable.getAccountByName(account.accountName) { localData ->
			// 首先显示数据库的数据在界面上
			localData?.updateUIValue()
			if (onlyUpdateLocalData) return@getAccountByName
			EOSAPI.getAccountInfo(
				account,
				{
					LogUtil.error("getAccountInfo", it)
				}
			) { eosAccount ->
				val newData =
					if (localData.isNull()) eosAccount else eosAccount.apply { this.id = localData!!.id }
				GoldStoneDataBase.database.eosAccountDao().insert(newData)
				GoldStoneAPI.context.runOnUiThread {
					eosAccount.updateUIValue()
				}
			}
		}
	}

	private fun getAccountTransactionCount() {
		// 先查数据库获取交易从数量, 如果数据库数据是空的那么从网络查询转账总个数
		val account = Config.getCurrentEOSAccount()
		EOSTransactionTable.getTransactionByAccountName(
			account.accountName,
			Config.getEOSCurrentChain()
		) { localData ->
			if (localData.isEmpty()) {
				EOSAPI.getTransactionsLastIndex(
					account,
					{
						fragment.setTransactionCount(CommonText.calculating)
						LogUtil.error("getTransactionsLastIndex", it)
					}
				) {
					val count = if (it.isNull()) 0 else it!! + 1
					fragment.setTransactionCount(count.toString())
				}
			} else {
				fragment.setTransactionCount(localData.size.toString())
			}
		}
	}

	private fun EOSAccountTable.updateUIValue() {
		fragment.setEOSBalance(if (balance.isEmpty()) "0.0" else balance)
		val availableRAM = ramQuota - ramUsed
		val availableCPU = cpuLimit.max - cpuLimit.used
		val cpuEOSValue = "${cpuWeight.toEOSCount()}" suffix CoinSymbol.eos
		val availableNet = netLimit.max - netLimit.used
		val netEOSValue = "${netWeight.toEOSCount()}" suffix CoinSymbol.eos
		EOSResourceUtil.getRAMPrice(EOSUnit.Byte) { price, error ->
			if (!price.isNull() && error.isNone()) {
				val ramEOSCount = "≈ " + (availableRAM.toDouble() * price!!).formatCount(4) suffix CoinSymbol.eos
				fragment.setResourcesValue(
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
			} else fragment.context.alert(error.message)
		}
	}
}