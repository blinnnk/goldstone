package io.goldstone.blockchain.module.home.dapp.eosaccountregister.presenter

import android.support.annotation.UiThread
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.base.basefragment.BasePresenter
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.error.EOSRPCError
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.common.error.RequestError
import io.goldstone.blockchain.common.utils.toJsonArray
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.eos.EOSCodeName
import io.goldstone.blockchain.crypto.eos.EOSUnit
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.eos.accountregister.EOSActor
import io.goldstone.blockchain.crypto.eos.base.showDialog
import io.goldstone.blockchain.crypto.eos.transaction.EOSAuthorization
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.crypto.utils.toEOSUnit
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.kernel.network.eos.EOSAPI
import io.goldstone.blockchain.kernel.network.eos.EOSRAM.EOSResourceUtil
import io.goldstone.blockchain.kernel.network.eos.EOSRegisterTransaction
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
		@UiThread hold: (currency: Double?, ramPrice: Double?, error: RequestError) -> Unit
	) {
		GoldStoneAPI.getPriceByContractAddress(
			listOf(TokenContract.eosContract).toJsonArray(),
			// 网络获取价格出错后从本地数据库获取价格
			{ hold(null, null, it) }
		) {
			EOSResourceUtil.getRAMPrice(EOSUnit.Byte) { price, error ->
				if (!price.isNull() && error.isNone()) {
					hold(it.first().price, price!!, error)
				} else hold(null, null, error)
			}
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
		val creatorAccount = Config.getCurrentEOSName()
		val totalSpent = (cpuEOSCount + netAEOSCount) + 2.0 // TODO 内存假设就是 2 颗 EOS
		checkNewAccountInfoInChain(newAccountName, publicKey) { validAccount, validPublicKey, error ->
			if (!error.isNone()) {
				callback(error)
				return@checkNewAccountInfoInChain
			}
			val chainID = Config.getEOSCurrentChain()
			BaseTradingPresenter.prepareTransaction(
				fragment.context,
				creatorAccount,
				validAccount!!,
				totalSpent,
				CoinSymbol.EOS,
				false
			) { privateKey, privateKeyError ->
				if (error.isNone() && !privateKey.isNull()) {
					EOSRegisterTransaction(
						chainID,
						EOSAuthorization(creatorAccount.accountName, EOSActor.Active),
						validAccount.accountName,
						validPublicKey.orEmpty(),
						ramAmount,
						cpuEOSCount.toEOSUnit(),
						netAEOSCount.toEOSUnit()
					).send(privateKey!!, callback) {
						fragment.getParentContainer()?.apply { it.showDialog(this) }
						callback(GoldStoneError.None)
					}
				} else callback(privateKeyError)
			}
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
		EOSAPI.getAccountResource(
			newAccount,
			EOSCodeName.EOSIO,
			{ hold(null, null, it) }
		) {
			fragment.context?.runOnUiThread {
				if (!it.isNull()) hold(null, null, EOSRPCError.RegisteredName)
				else hold(newAccount, publicKey, GoldStoneError.None)
			}
		}
	}
}