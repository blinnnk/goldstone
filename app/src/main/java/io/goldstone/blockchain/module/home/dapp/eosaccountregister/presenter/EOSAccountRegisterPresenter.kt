package io.goldstone.blockchain.module.home.dapp.eosaccountregister.presenter

import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.error.TransferError
import io.goldstone.blockchain.common.language.EOSAccountText
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.crypto.eos.EOSCodeName
import io.goldstone.blockchain.crypto.eos.EOSUnit
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.eos.accountregister.EOSActor
import io.goldstone.blockchain.crypto.eos.base.EOSResponse
import io.goldstone.blockchain.crypto.eos.transaction.EOSAuthorization
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.crypto.utils.formatDecimal
import io.goldstone.blockchain.crypto.utils.toEOSUnit
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.kernel.network.eos.EOSRegisterTransaction
import io.goldstone.blockchain.kernel.network.eos.eosram.EOSResourceUtil
import io.goldstone.blockchain.module.common.tokendetail.eosactivation.accountselection.model.EOSAccountTable
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.presenter.BaseTradingPresenter
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.StakeType
import io.goldstone.blockchain.module.home.dapp.eosaccountregister.view.EOSAccountRegisterFragment
import org.jetbrains.anko.runOnUiThread
import java.math.BigInteger


/**
 * @author KaySaith
 * @date  2018/09/21
 */
class EOSAccountRegisterPresenter(
	override val fragment: EOSAccountRegisterFragment
) : BasePresenter<EOSAccountRegisterFragment>() {

	fun getEOSCurrencyAndRAMPrice(
		@WorkerThread hold: (currency: Double?, ramPrice: Double?, error: RequestError) -> Unit
	) {
		GoldStoneAPI.getPriceByContractAddress(
			listOf("{\"address\":\"${TokenContract.EOS.contract}\",\"symbol\":\"${TokenContract.EOS.symbol}\"}")
		) { currency, currencyError ->
			if (currency?.firstOrNull() != null && currencyError.isNone()) {
				EOSResourceUtil.getRAMPrice(EOSUnit.Byte) { ramPriceInEOS, error ->
					if (ramPriceInEOS.isNotNull() && error.isNone()) {
						hold(currency.first().price, ramPriceInEOS, error)
					} else hold(null, null, error)
				}
			} else hold(null, null, currencyError)
		}
	}

	fun registerAccount(
		newAccountName: EOSAccount,
		publicKey: String,
		ramAmount: BigInteger,
		cpuEOSCount: Double,
		netAEOSCount: Double,
		callback: (response: EOSResponse?, error: GoldStoneError) -> Unit
	) {
		if (ramAmount < BigInteger.valueOf(4096)) {
			callback(null, TransferError.LessRAMForRegister)
			return
		}
		// 首先查询 `RAM` 每 `Byte` 对应的 `EOS Count` 计算出即将分配的 `RAM` 价值的 `EOS Count`
		EOSResourceUtil.getRAMPrice(EOSUnit.Byte) { priceInEOS, ramPriceError ->
			if (priceInEOS.isNotNull() && ramPriceError.isNone()) {
				val ramEOSCount = ramAmount.toDouble() * priceInEOS
				val creatorAccount = SharedAddress.getCurrentEOSAccount()
				val totalSpent = (cpuEOSCount + netAEOSCount + ramEOSCount).formatDecimal(4)
				checkNewAccountInfo(newAccountName, publicKey) { validAccount, validPublicKey, error ->
					if (error.hasError()) callback(null, error)
					else BaseTradingPresenter.prepareTransaction(
						fragment.context,
						totalSpent,
						TokenContract.EOS,
						StakeType.Register
					) { privateKey, privateKeyError ->
						// 首先检测当前私钥的权限, 是单独 `Owner` 或 单独 `Active` 或全包包含,
						// 首先选择 `Active` 权限, 如果不是选择 `Owner` 如果都没有返回 `error` 权限错误
						val chainID = SharedChain.getEOSCurrent().chainID
						val permission = EOSAccountTable.getValidPermission(creatorAccount, chainID)
						when {
							permission.isNull() -> callback(null, GoldStoneError("Wrong Permission Keys"))
							error.isNone() && privateKey.isNotNull() -> EOSRegisterTransaction(
								chainID,
								EOSAuthorization(creatorAccount.name, permission),
								validAccount!!.name,
								validPublicKey.orEmpty(),
								ramAmount,
								cpuEOSCount.toEOSUnit(),
								netAEOSCount.toEOSUnit()
							).send(privateKey, callback)
							else -> callback(null, privateKeyError)
						}
					}
				}
			} else callback(null, ramPriceError)
		}
	}

	private fun checkNewAccountInfo(
		newAccount: EOSAccount,
		publicKey: String,
		@UiThread hold: (
			validAccount: EOSAccount?,
			validPublicKey: String?,
			error: GoldStoneError
		) -> Unit) {
		if (newAccount.name.isEmpty()) {
			hold(null, null, AccountError.EmptyName)
			return
		}
		if (publicKey.isEmpty()) {
			hold(null, null, AccountError.EmptyPublicKey)
			return
		}
		// 检查 AccountName 是否是合规的公钥
		if (!newAccount.isValid()) {
			hold(null, null, AccountError.InvalidAccountName)
			return
		}
		// 检查公钥是否是合规的公钥
		if (!EOSWalletUtils.isValidAddress(publicKey)) {
			hold(null, null, AccountError.InvalidAddress)
			return
		}
		// 检查当前合规的用户名是否可被注册
		checkNameIsAvailableInChain(newAccount) { isAvailable, error ->
			if (isAvailable.isNotNull() && error.isNone()) {
				hold(newAccount, publicKey, GoldStoneError.None)
			} else hold(null, null, error)
		}
	}

	companion object {
		fun checkNameIsAvailableInChain(
			newAccount: EOSAccount,
			@UiThread hold: (isAvailable: Boolean?, error: RequestError) -> Unit
		) {
			// 检查当前合规的用户名是否可被注册
			EOSAPI.getAccountResource(
				newAccount,
				EOSCodeName.EOSIO
			) { resource, error ->
				GoldStoneAPI.context.runOnUiThread {
					if (resource.isNotNull())
						hold(null, RequestError.RPCResult(EOSAccountText.checkNameResultUnavailable))
					else hold(true, error)
				}
			}
		}
	}
}