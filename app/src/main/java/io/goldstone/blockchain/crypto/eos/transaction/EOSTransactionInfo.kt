package io.goldstone.blockchain.crypto.eos.transaction

import android.content.Context
import android.support.annotation.WorkerThread
import com.blinnnk.extension.isNotNull
import com.blinnnk.extension.orElse
import io.goldstone.blockchain.common.error.AccountError
import io.goldstone.blockchain.common.error.GoldStoneError
import io.goldstone.blockchain.crypto.eos.EOSUtils
import io.goldstone.blockchain.crypto.eos.account.EOSAccount
import io.goldstone.blockchain.crypto.eos.account.EOSPrivateKey
import io.goldstone.blockchain.crypto.eos.accountregister.EOSActor
import io.goldstone.blockchain.crypto.eos.base.EOSModel
import io.goldstone.blockchain.crypto.eos.base.EOSResponse
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.multichain.CryptoValue
import io.goldstone.blockchain.crypto.multichain.TokenContract
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.toCount
import io.goldstone.blockchain.crypto.utils.toNoPrefixHexString
import io.goldstone.blockchain.kernel.network.ParameterUtil
import io.goldstone.blockchain.kernel.network.eos.EOSTransaction
import io.goldstone.blockchain.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.presenter.BaseTradingPresenter
import java.io.Serializable
import java.math.BigInteger

/**
 * @author KaySaith
 * @date 2018/09/03
 */

data class EOSTransactionInfo(
	val fromAccount: EOSAccount,
	val toAccount: EOSAccount,
	val amount: BigInteger, // 这里是把精度包含进去的最小单位的值, 签名的时候会对这个值直接转换
	val contract: TokenContract,
	val memo: String,
	// 账单的模型和买内存的复用, 唯一不同的是, 是否包含 `Memo`. 这个 `Boolean` 主要的用处是
	// 在序列化的时候不让 `Memo` 参与其中. 注意 就算 `Memo` 为空 序列化也会得出 `00` 的值, 所以
	// 一定是不参与序列化
	val isTransaction: Boolean
) : Serializable, EOSModel {

	constructor(
		fromAccount: EOSAccount,
		toAccount: EOSAccount,
		amount: BigInteger
	) : this(
		fromAccount,
		toAccount,
		amount,
		TokenContract.EOS,
		"",
		false
	)

	constructor(
		fromAccount: EOSAccount,
		toAccount: EOSAccount,
		amount: BigInteger,
		memo: String,
		contract: TokenContract
	) : this(
		fromAccount,
		toAccount,
		amount,
		contract,
		memo,
		true
	)

	fun trade(
		context: Context?,
		@WorkerThread hold: (response: EOSResponse?, error: GoldStoneError) -> Unit
	) {
		if (!toAccount.isValid(false)) {
			hold(null, AccountError.InvalidAccountName)
		} else {
			BaseTradingPresenter.prepareTransaction(
				context,
				amount.toCount(contract.decimal.orElse(CryptoValue.eosDecimal)),
				contract,
				false
			) { privateKey, error ->
				if (error.isNone() && privateKey.isNotNull()) {
					transfer(privateKey, hold)
				} else hold(null, error)
			}
		}
	}

	private fun transfer(
		privateKey: EOSPrivateKey,
		@WorkerThread hold: (response: EOSResponse?, error: GoldStoneError) -> Unit
	) {
		EOSTransaction(
			EOSAuthorization(fromAccount.name, EOSActor.Active),
			toAccount.name,
			amount,
			memo,
			// 这里现在默认有效期设置为 5 分钟. 日后根据需求可以用户自定义
			ExpirationType.FiveMinutes,
			contract
		).send(privateKey, hold)
	}

	override fun createObject(): String {
		return ParameterUtil.prepareObjectContent(
			Pair("from", fromAccount),
			Pair("to", toAccount),
			// `Count` 与 `Symbol` 之间留一个空格, 官方强制的脑残需求
			Pair("quantity", "${CryptoUtils.toCountByDecimal(amount, contract.decimal.orElse(CryptoValue.eosDecimal))} " + contract.symbol),
			Pair("memo", fromAccount)
		)
	}

	override fun serialize(): String {
		val encryptFromAccount = EOSUtils.getLittleEndianCode(fromAccount.name)
		val encryptToAccount = EOSUtils.getLittleEndianCode(toAccount.name)
		val amountCode = EOSUtils.convertAmountToCode(amount)
		val decimalCode = EOSUtils.getEvenHexOfDecimal(contract.decimal.orElse(CryptoValue.eosDecimal))
		var symbolCode = contract.symbol.toByteArray().toNoPrefixHexString()
		// `Symbol` 编码后的 `Count` 不能少于 `6` 位 不足的补 `0`
		if (symbolCode.length < 6) symbolCode = symbolCode.completeZero(6 - symbolCode.count())
		// `EOS Token` 的 `Memo` 不用补位
		val completeZero = "00000000"
		val memoCode = if (isTransaction) EOSUtils.convertMemoToCode(memo) else ""
		return encryptFromAccount + encryptToAccount + amountCode + decimalCode + symbolCode + completeZero + memoCode
	}

	companion object {
		fun serializedEOSAmount(amount: BigInteger): String {
			val amountCode = EOSUtils.convertAmountToCode(amount)
			val decimalCode = EOSUtils.getEvenHexOfDecimal(CryptoValue.eosDecimal)
			val symbolCode = CoinSymbol.eos.toByteArray().toNoPrefixHexString()
			val completeZero = "00000000"
			return amountCode + decimalCode + symbolCode + completeZero
		}
	}
}