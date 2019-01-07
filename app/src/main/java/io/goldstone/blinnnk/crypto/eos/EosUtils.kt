package io.goldstone.blinnnk.crypto.eos

import android.annotation.SuppressLint
import com.blinnnk.extension.getDecimalCount
import com.blinnnk.extension.isEvenCount
import com.subgraph.orchid.encoders.Hex
import io.goldstone.blinnnk.crypto.eos.eostypes.EosByteWriter
import io.goldstone.blinnnk.crypto.eos.transaction.completeZero
import io.goldstone.blinnnk.crypto.multichain.CryptoValue
import io.goldstone.blinnnk.crypto.utils.*
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author KaySaith
 * @date 2018/09/03
 */

object EOSUtils {

	private const val maxNameIndex = 12

	@Throws
	fun toLittleEndian(content: String): String {
		if (!content.isEvenCount()) throw Exception("content length must be even")
		var endianValue = ""
		var startIndex = content.length
		for (index in 0 until content.length / 2) {
			startIndex -= 2
			endianValue += content.substring(startIndex, startIndex + 2)
		}
		return endianValue
	}

	fun getLittleEndianCode(content: String): String {
		val writer = EosByteWriter(255)
		writer.putLongLE(EOSUtils.nameToLong(content))
		return writer.toBytes().toNoPrefixHexString()
	}

	fun getRefBlockNumberCode(refBlockNumber: Int): String {
		val writer = EosByteWriter(255)
		writer.putShortLE((refBlockNumber and 0xFFFF).toShort())
		return writer.toBytes().toNoPrefixHexString()
	}

	fun getRefBlockPrefixCode(refBlockPrefix: Int): String {
		val writer = EosByteWriter(255)
		writer.putIntLE(refBlockPrefix and -0x1)
		return writer.toBytes().toNoPrefixHexString()
	}

	fun <T : Number> getVariableUInt(value: T): String {
		val writer = EosByteWriter(255)
		val convertedValue = when (value) {
			is Int -> value.toLong()
			is Double -> value.toLong()
			is Float -> value.toLong()
			else -> value.toLong()
		}
		writer.putVariableUInt(convertedValue)
		return writer.toBytes().toNoPrefixHexString()
	}

	fun convertAmountToCode(amount: BigInteger): String {
		val amountHex = amount.toString(16)
		val evenCountHex = amountHex.completeToEven()
		val littleEndianAmountHex = toLittleEndian(evenCountHex)
		return littleEndianAmountHex.completeZero(16 - littleEndianAmountHex.count())
	}

	fun getEvenHexOfDecimal(decimal: Int): String {
		return decimal.toString(16).completeToEven()
	}

	fun convertMemoToCode(memo: String): String {
		val lengthCode = EOSUtils.getVariableUInt(memo.length)
		return lengthCode + memo.toCryptHexString()
	}

	private fun String.completeToEven(): String {
		return if (!isEvenCount()) "0$this"
		else this
	}

	private fun charToByte(char: Char): Byte {
		if (char in 'a' .. 'z')
			return (char - 'a' + 6).toByte()
		return if (char in '1' .. '5') (char - '1' + 1).toByte() else 0.toByte()
	}

	private fun nameToLong(content: String?): Long {
		if (content == null) return 0L
		val len = content.length
		var value: Long = 0
		for (index in 0 .. maxNameIndex) {
			var char: Long = 0
			if (index < len && index <= maxNameIndex) char = charToByte(content[index]).toLong()
			if (index < maxNameIndex) {
				char = char and 0x1f
				char = char shl (64 - 5 * (index + 1))
			} else {
				char = char and 0x0f
			}
			value = value or char
		}
		return value
	}

	/**
	 * 由 `EOS RPC getInfo` 方法返回的16进制的 `head_block_id`
	 * 然后字段截取 `16` 到 `24` 然后 `ToLittleEndian` 然后再转成 `10` 进制
	 */
	@Throws
	fun getRefBlockPrefix(headBlockID: String): Int {
		if (headBlockID.length < 64) throw Exception("wrong head block id length")
		return try {
			toLittleEndian(headBlockID.substring(16, 24)).hexToDecimal().toInt()
		} catch (error: Exception) {
			throw Exception("wrong head block id length wrong prefix")
		}
	}

	@Throws
	fun getRefBlockNumber(headBlockID: String): Int {
		if (headBlockID.length < 64) throw Exception("wrong head block id length")
		return try {
			Integer.valueOf(headBlockID.substring(0, 8), 16)
		} catch (error: Exception) {
			throw Exception("wrong head block id length when Integer.valueOf")
		}
	}

	@SuppressLint("SimpleDateFormat")
	/**
	 *  对 `expiration` 编码时间格式转得到 `long` 由于时间 `java` 得到的 `time` 是精确到毫秒
	 *  而 `eos`只精确到秒 所以需要除以 `1000`
	 */
	@Throws
	fun getExpirationCode(date: String): String {
		if (!date.contains("T")) throw Exception("Wrong Date Formatted")
		val formattedDate = date
			.replace("T", " ")
			.replace("-", "/")
		val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
		// 按照 `UTC` 时间进行解析
		dateFormat.timeZone = TimeZone.getTimeZone("UTC")
		// `EOS` 规定使用的时间是秒
		val writer = EosByteWriter(255)
		val valueInSecond = (dateFormat.parse(formattedDate).time) / 1000
		writer.putIntLE(valueInSecond.toInt())
		return writer.toBytes().toNoPrefixHexString()
	}

	@SuppressLint("SimpleDateFormat")
	fun getUTCTimeStamp(date: String): Long {
		if (!date.contains("T")) throw Exception("Wrong Date Formatted")
		val formattedDate = date
			.replace("T", " ")
			.replace("-", "/")
		val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
		// 按照 `UTC` 时间进行解析
		dateFormat.timeZone = TimeZone.getTimeZone("UTC")
		return dateFormat.parse(formattedDate).time
	}

	fun getExpirationCode(timeStamp: Long): String {
		val writer = EosByteWriter(255)
		writer.putIntLE(timeStamp.toInt())
		return writer.toBytes().toNoPrefixHexString()
	}

	@SuppressLint("SimpleDateFormat")
	fun getCurrentUTCStamp(): Long {
		val date = Date()
		val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
		dateFormat.timeZone = TimeZone.getTimeZone("UTC")
		val utcDate = dateFormat.format(date)
		return dateFormat.parse(utcDate).time / 1000
	}

	fun convertAmountToValidFormat(amount: BigInteger): String {
		val count = CryptoUtils.toCountByDecimal(amount, CryptoValue.eosDecimal)
		val decimalCount = count.getDecimalCount()
		return when (decimalCount) {
			null -> "$count 0000"
			CryptoValue.eosDecimal -> "$count"
			else -> "$count${completeZero(CryptoValue.eosDecimal - decimalCount)}"
		}
	}

	fun completeZero(count: Int): String {
		var completeZero = ""
		for (index in 0 until count) {
			completeZero += "0"
		}
		return completeZero
	}

	fun getHexDataByteLengthCode(hexData: String): String {
		val formattedHexData = if (hexData.isEvenCount()) hexData else hexData + "0"
		return EOSUtils.getVariableUInt(Hex.decode(formattedHexData).size)
	}

	fun isValidMemoSize(memo: String): Boolean {
		return memo.toUtf8Bytes().size <= EOSValue.memoMaxCharacterSize
	}
}

