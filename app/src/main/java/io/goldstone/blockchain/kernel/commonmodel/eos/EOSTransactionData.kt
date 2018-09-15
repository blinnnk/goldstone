package io.goldstone.blockchain.kernel.commonmodel.eos

import android.arch.persistence.room.TypeConverter
import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.crypto.eos.transaction.EOSTransactionInfo
import io.goldstone.blockchain.crypto.utils.CryptoUtils
import org.json.JSONObject


/**
 * @author KaySaith
 * @date  2018/09/13
 * {"from":"lioninjungle","to":"kaysaith1522","quantity":"10000.0000 JUNGLE","memo":"Jungle Faucet"}
 */

data class EOSTransactionData(
	val fromName: String,
	val toName: String,
	val quantity: String,
	val memo: String
) {

	constructor(data: EOSTransactionInfo) : this(
		data.fromAccount,
		data.toAccount,
		"${CryptoUtils.toCountByDecimal(data.amount, data.decimal)} ${data.symbol}",
		data.memo
	)

	constructor(data: JSONObject) : this(
		data.safeGet("from"),
		data.safeGet("to"),
		data.safeGet("quantity"),
		data.safeGet("memo")
	)
}

class EOSTransactionDataConverter {
	@TypeConverter
	fun revertJSONObject(content: String): EOSTransactionData {
		val data = JSONObject(content)
		return EOSTransactionData(
			data.safeGet("from"),
			data.safeGet("to"),
			data.safeGet("quantity"),
			data.safeGet("memo")
		)
	}

	@TypeConverter
	fun convertToString(data: EOSTransactionData): String {
		return "{\"from\":\"${data.fromName}\",\"to\":\"${data.toName}\",\"quantity\":\"${data.quantity}\",\"memo\":\"${data.memo}\"}"
	}
}