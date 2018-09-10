package io.goldstone.blockchain.crypto.eos.transaction

import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.crypto.eos.EOSUtils
import io.goldstone.blockchain.crypto.eos.base.EOSModel
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.toNoPrefixHexString
import io.goldstone.blockchain.kernel.network.ParameterUtil
import java.io.Serializable

/**
 * @author KaySaith
 * @date 2018/09/03
 */

data class EOSTransactionInfo(
	val fromAccount: String,
	val toAccount: String,
	val amount: Long,
	val symbol: String,
	val decimal: Int,
	val memo: String,
	// 账单的模型和买内存的复用, 唯一不同的是, 是否包含 `Memo`. 这个 `Boolean` 主要的用处是
	// 在序列化的时候不让 `Memo` 参与其中. 注意 就算 `Memo` 为空 序列化也会得出 `00` 的值, 所以
	// 一定是不参与序列化
	val isTransaction: Boolean
) : Serializable, EOSModel {

	constructor(
		fromAccount: String,
		toAccount: String,
		amount: Long
	) : this(
		fromAccount,
		toAccount,
		amount,
		CryptoSymbol.eos,
		CryptoValue.eosDecimal,
		"",
		false
	)

	constructor(
		fromAccount: String,
		toAccount: String,
		amount: Long,
		memo: String
	) : this(
		fromAccount,
		toAccount,
		amount,
		CryptoSymbol.eos,
		CryptoValue.eosDecimal,
		memo,
		true
	)

	override fun createObject(): String {
		return ParameterUtil.prepareObjectContent(
			Pair("from", fromAccount),
			Pair("to", toAccount),
			// `Count` 与 `Symbol` 之间留一个空格, 官方强制的脑残需求
			Pair("quantity", "${CryptoUtils.toCountByDecimal(amount, decimal)} " + symbol),
			Pair("memo", fromAccount)
		)
	}

	override fun serialize(): String {
		val encryptFromAccount = EOSUtils.getLittleEndianCode(fromAccount)
		val encryptToAccount = EOSUtils.getLittleEndianCode(toAccount)
		val amountCode = EOSUtils.convertAmountToCode(amount)
		val decimalCode = EOSUtils.getEvenHexOfDecimal(decimal)
		val symbolCode = symbol.toByteArray().toNoPrefixHexString()
		val completeZero = "00000000"
		val memoCode = if (isTransaction) EOSUtils.convertMemoToCode(memo) else ""
		return encryptFromAccount + encryptToAccount + amountCode + decimalCode + symbolCode + completeZero + memoCode

	}
	companion object {
	    fun serializedEOSAmount(amount: Long): String {
				val amountCode = EOSUtils.convertAmountToCode(amount)
				val decimalCode = EOSUtils.getEvenHexOfDecimal(CryptoValue.eosDecimal)
				val symbolCode = CryptoSymbol.eos.toByteArray().toNoPrefixHexString()
				val completeZero = "00000000"
				return amountCode + decimalCode + symbolCode + completeZero
			}
	}
}