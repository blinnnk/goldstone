package io.goldstone.blinnnk.kernel.network.eos

import android.content.Context
import android.support.annotation.WorkerThread
import com.blinnnk.extension.getDecimalCount
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.toDoubleOrZero
import io.goldstone.blinnnk.common.error.AccountError
import io.goldstone.blinnnk.common.error.GoldStoneError
import io.goldstone.blinnnk.common.sharedpreference.SharedChain
import io.goldstone.blinnnk.crypto.eos.EOSCodeName
import io.goldstone.blinnnk.crypto.eos.EOSTransactionSerialization
import io.goldstone.blinnnk.crypto.eos.account.EOSAccount
import io.goldstone.blinnnk.crypto.eos.account.EOSPrivateKey
import io.goldstone.blinnnk.crypto.eos.base.EOSResponse
import io.goldstone.blinnnk.crypto.eos.transaction.EOSAction
import io.goldstone.blinnnk.crypto.eos.transaction.EOSTransactionUtils
import io.goldstone.blinnnk.crypto.eos.transaction.ExpirationType
import io.goldstone.blinnnk.crypto.multichain.TokenContract
import io.goldstone.blinnnk.kernel.network.eos.contract.EOSTransactionInterface
import io.goldstone.blinnnk.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.presenter.BaseTradingPresenter
import io.goldstone.blinnnk.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.StakeType
import java.io.Serializable


/**
 * @author KaySaith
 * @date  2018/12/24
 */
class MultipleActionsTransaction(
	private val quantity: String,
	private val actions: List<EOSAction>
) : Serializable, EOSTransactionInterface() {

	override fun serialized(hold: (serialization: EOSTransactionSerialization?, error: GoldStoneError) -> Unit) {
		EOSAPI.getTransactionHeader(ExpirationType.FiveMinutes) { header, error ->
			if (header.isNotNull() && error.isNone()) {
				val serialization = EOSTransactionUtils.serialize(
					SharedChain.getEOSCurrent().chainID,
					header,
					actions
				)
				hold(serialization, error)
			} else hold(null, error)
		}
	}

	fun trade(
		context: Context,
		chaiURL: String = SharedChain.getEOSCurrent().getURL(),
		cancelAction: () -> Unit,
		@WorkerThread hold: (response: EOSResponse?, error: GoldStoneError) -> Unit
	) {
		val decimal = quantity.substringBefore(" ").getDecimalCount()
		val symbol = quantity.substringAfter(" ")
		val tradingCount = quantity.substringBefore(" ").toDoubleOrZero()
		val code = actions.find { it.method.isTransfer() }?.code ?: EOSCodeName.EOSIOToken
		val invalidAccount = actions.map {
			it.authorizations
		}.flatten().map {
			EOSAccount(it.actor).isValid(false)
		}.any { !it }
		if (invalidAccount) {
			hold(null, AccountError.InvalidAccountName)
		} else {
			BaseTradingPresenter.prepareTransaction(
				context,
				tradingCount,
				TokenContract(code.value, symbol, decimal),
				StakeType.Trade,
				cancelAction = cancelAction
			) { privateKey, error ->
				if (error.isNone() && privateKey.isNotNull()) {
					send(EOSPrivateKey(privateKey), chaiURL, hold)
				} else hold(null, error)
			}
		}
	}
}