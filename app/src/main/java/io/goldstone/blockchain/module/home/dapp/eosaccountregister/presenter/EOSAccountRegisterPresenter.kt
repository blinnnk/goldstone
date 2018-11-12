package io.goldstone.blockchain.module.home.dapp.eosaccountregister.presenter

import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orZero
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.sharedpreference.SharedAddress
import io.goldstone.blockchain.common.sharedpreference.SharedChain
import io.goldstone.blockchain.crypto.eos.EOSCodeName
import io.goldstone.blockchain.crypto.eos.EOSUnit
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.eos.accountregister.EOSActor
import io.goldstone.blockchain.crypto.eos.base.showDialog
import io.goldstone.blockchain.crypto.eos.transaction.EOSAuthorization
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.crypto.utils.formatDecimal
import io.goldstone.blockchain.crypto.utils.toEOSUnit
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.kernel.network.eos.EOSRegisterTransaction
import io.goldstone.blockchain.kernel.network.eos.eosram.EOSResourceUtil
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.presenter.BaseTradingPresenter
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
			if (currency != null && currencyError.isNone()) {
				EOSResourceUtil.getRAMPrice(EOSUnit.Byte) { ramPriceInEOS, error ->
					if (ramPriceInEOS != null && error.isNone()) {
						hold(currency.firstOrNull()?.price.orZero(), ramPriceInEOS, error)
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
		callback: (GoldStoneError) -> Unit
	) {
		// 首先查询 `RAM` 每 `Byte` 对应的 `EOS Count` 计算出即将分配的 `RAM` 价值的 `EOS Count`
		EOSResourceUtil.getRAMPrice(EOSUnit.Byte) { priceInEOS, ramPriceError ->
			if (!priceInEOS.isNull() && ramPriceError.isNone()) {
				val ramEOSCount = (ramAmount.toDouble() * priceInEOS!!).formatDecimal(4)
				val creatorAccount = SharedAddress.getCurrentEOSAccount()
				val totalSpent = cpuEOSCount + netAEOSCount + ramEOSCount
				checkNewAccountInfoInChain(newAccountName, publicKey) { validAccount, validPublicKey, error ->
					if (!error.isNone()) callback(error)
					else BaseTradingPresenter.prepareTransaction(
						fragment.context,
						creatorAccount,
						validAccount!!,
						totalSpent,
						TokenContract.EOS,
						false
					) { privateKey, privateKeyError ->
						if (error.isNone() && !privateKey.isNull()) {
							EOSRegisterTransaction(
								SharedChain.getEOSCurrent().chainID,
								EOSAuthorization(creatorAccount.accountName, EOSActor.Active),
								validAccount.accountName,
								validPublicKey.orEmpty(),
								ramAmount,
								cpuEOSCount.toEOSUnit(),
								netAEOSCount.toEOSUnit()
							).send(privateKey!!) { response, error ->
								if (!response.isNull() && error.isNone()) {
									fragment.getParentContainer()?.apply { response!!.showDialog(this) }
									callback(GoldStoneError.None)
								} else callback(error)
							}
						} else callback(privateKeyError)
					}
				}
			} else callback(ramPriceError)
		}
	}

	private fun checkNewAccountInfoInChain(
		newAccount: EOSAccount,
		publicKey: String,
		@UiThread hold: (
			validAccount: EOSAccount?,
			validPublicKey: String?,
			error: GoldStoneError
		) -> Unit) {
		if (newAccount.accountName.isEmpty()) {
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
			if (!isAvailable.isNull() && error.isNone()) {
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
					if (!resource.isNull())
						hold(null, RequestError.RPCResult("this account name has been registered"))
					else hold(true, error)
				}
			}
		}
	}
}