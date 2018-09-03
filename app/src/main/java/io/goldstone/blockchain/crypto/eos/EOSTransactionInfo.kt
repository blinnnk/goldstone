package io.goldstone.blockchain.crypto.eos

import io.goldstone.blockchain.crypto.CryptoSymbol
import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import io.goldstone.blockchain.crypto.utils.toNoPrefixHexString
import io.goldstone.blockchain.kernel.network.ParameterUtil
import java.io.Serializable

data class EOSTransactionInfo(
	val fromAccount: String,
	val toAccount: String,
	val amount: Long,
	val symbol: String,
	val decimal: Int,
	val memo: String
) : Serializable {
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
		memo
	)

	companion object {
		fun createObject(data: EOSTransactionInfo): String {
			return ParameterUtil.prepareObjectContent(
				Pair("from", data.fromAccount),
				Pair("to", data.toAccount),
				// `Count` 与 `Symbol` 之间留一个空格, 官方强制的脑残需求
				Pair("quantity", "${CryptoUtils.toCountByDecimal(data.amount, data.decimal)} " + data.symbol),
				Pair("memo", data.fromAccount)
			)
		}

		fun encryptTransactionInfo(data: EOSTransactionInfo): String {
			val encryptFromAccount = EOSUtils.getLittleEndianName(data.fromAccount)
			val encryptToAccount = EOSUtils.getLittleEndianName(data.toAccount)
			val amountCode = EOSUtils.convertAmountToCode(data.amount)
			val decimalCode = EOSUtils.getEvenHexOfDecimal(data.decimal)
			val symbolCode = data.symbol.toByteArray().toNoPrefixHexString()
			val completeZero = "00000000"
			val memoCode = EOSUtils.convertMemoToCode(data.memo)
			return encryptFromAccount + encryptToAccount + amountCode + decimalCode + symbolCode + completeZero + memoCode
		}
	}
}