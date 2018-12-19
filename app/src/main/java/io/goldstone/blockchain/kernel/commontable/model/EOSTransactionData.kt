package io.goldstone.blockchain.kernel.commonmodel.eos

import android.arch.persistence.room.TypeConverter
import com.blinnnk.extension.orElse
import com.blinnnk.extension.safeGet
import io.goldstone.blockchain.common.utils.removeSlash
import io.goldstone.blockchain.crypto.eos.transaction.EOSTransactionInfo
import io.goldstone.blockchain.crypto.multichain.CryptoValue
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
		data.fromAccount.name,
		data.toAccount.name,
		"${CryptoUtils.toCountByDecimal(data.amount, data.contract.decimal.orElse(CryptoValue.eosDecimal))} ${data.contract.symbol}",
		data.memo
	)

	constructor(data: JSONObject) : this(
		data.safeGet("from"),
		data.safeGet("to"),
		data.safeGet("quantity"),
		if (data.safeGet("memo").contains("{")) data.safeGet("memo")
		else data.safeGet("memo")
	)
}

class EOSTransactionDataConverter {
	@TypeConverter
	fun revertJSONObject(content: String): EOSTransactionData {
		val data =
			if (!content.contains("{")) JSONObject(content) else JSONObject(content.removeSlash())
		return EOSTransactionData(data)
	}

	@TypeConverter
	fun convertToString(data: EOSTransactionData): String {
		return "{\"from\":\"${data.fromName}\",\"to\":\"${data.toName}\",\"quantity\":\"${data.quantity}\",\"memo\":\"${data.memo}\"}"
	}
}