package io.goldstone.blockchain.crypto.utils

import android.text.format.DateUtils
import com.blinnnk.extension.isTrue
import com.blinnnk.extension.otherwise
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.*
import io.goldstone.blockchain.crypto.extensions.toHexStringZeroPadded
import io.goldstone.blockchain.kernel.commonmodel.TransactionTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import java.math.BigInteger
import java.security.MessageDigest
import java.text.DecimalFormat
import java.util.*

/**
 * @date 01/04/2018 7:54 PM
 * @author KaySaith
 */
data class InputCodeData(val type: String, val address: String, val count: Double)

object CryptoUtils {

	fun scaleTo16(address: String): String {
		return if (address.length < 16) address
		else address.substring(0, 16) + "..."
	}

	fun scaleTo22(address: String): String {
		return if (address.length < 22) address
		else address.substring(0, 22) + "..."
	}

	fun scaleTo32(address: String): String {
		return if (address.length < 32) address
		else address.substring(0, 32) + "..."
	}

	fun scaleMiddleAddress(address: String, halfSize: Int = 12): String {
		return if (address.length > halfSize) address.substring(0, halfSize) + " ... " + address.substring(
			address.length - halfSize,
			address.length
		)
		else "wrong address"
	}

	fun formatDouble(value: Double): Double {
		return DecimalFormat("0.000000000").format(value).toDouble()
	}

	fun toCountByDecimal(value: Double, decimal: Double = 18.0): Double {
		return value / Math.pow(10.0, decimal)
	}

	fun toCountByDecimal(value: Long, decimal: Int = 18): Double {
		return value / Math.pow(10.0, decimal.toDouble())
	}

	fun toGasUsedEther(gas: String?, gasPrice: String?, isHex: Boolean = true): String {
		return if (gas.isNullOrBlank() || gasPrice.isNullOrBlank()) {
			"0"
		} else if (!isHex) {
			(gas!!.toBigDecimal() * gasPrice!!.toBigDecimal()).toDouble().toEthCount().toBigDecimal()
				.toString()
		} else {
			(gas!!.hexToDecimal().toBigDecimal() * gasPrice!!.hexToDecimal().toBigDecimal())
				.toDouble().toEthCount().toBigDecimal().toString()
		}
	}

	fun toValueWithDecimal(count: Double, decimal: Double = 18.0): BigInteger {
		return (count.toBigDecimal() * Math.pow(10.0, decimal).toBigDecimal()).toBigInteger()
	}

	fun loadTransferInfoFromInputData(inputCode: String): InputCodeData? {
		var address: String
		var count: Double
		isTransferInputCode(inputCode) isTrue {
			// analysis input code and get the received address
			address = inputCode.substring(
				SolidityCode.contractTransfer.length, SolidityCode.contractTransfer.length + 64
			)
			address =
				toHexValue(address.substring(address.length - 40, address.length))
			// analysis input code and get the received count
			count = inputCode.substring(74, 138).hexToDecimal()
			return InputCodeData("transfer", address, count)
		} otherwise {
			LogUtil.debug("loadTransferInfoFromInputData", "not a contract transfer")
			return null
		}
	}

	fun isERC20Transfer(transactionTable: TransactionTable, hold: () -> Unit): Boolean {
		return if (
			transactionTable.input.length >= 138 && isTransferInputCode(transactionTable.input)
			// 有一部分 `token income` 数据是从 e`vent log` 获取，这个值 `logIndex` 可以做判断
			|| transactionTable.logIndex.isNotEmpty()
		) {
			hold()
			true
		} else {
			false
		}
	}

	fun isERC20TransferByInputCode(inputCode: String, hold: () -> Unit = {}): Boolean {
		return if (inputCode.length >= 138 && isTransferInputCode(
				inputCode
			)
		) {
			hold()
			true
		} else {
			false
		}
	}

	fun getTargetDayInMills(distanceSinceToday: Int = 0): Long {
		val calendar = Calendar.getInstance()
		val year = calendar.get(Calendar.YEAR)
		val month = calendar.get(Calendar.MONTH)
		val date = calendar.get(Calendar.DATE) + distanceSinceToday
		calendar.clear()
		calendar.set(year, month, date)
		return calendar.timeInMillis
	}

	val dateInDay: (Long) -> String = {
		DateUtils.formatDateTime(GoldStoneAPI.context, it, DateUtils.FORMAT_NO_YEAR)
	}

	private fun toHexValue(value: String): String {
		return "0x$value"
	}

	private fun isTransferInputCode(inputCode: String) = inputCode.length > 10 && inputCode.substring(
		0, SolidityCode.contractTransfer.length
	) == SolidityCode.contractTransfer

	fun getAddressFromPrivateKey(privateKey: String): String {
		/** Convert PrivateKey To BigInteger */
		val currentPrivateKey = privateKey.toBigInteger(16)
		/** Get Public Key and Private Key*/
		val publicKey =
			ECKeyPair(currentPrivateKey, publicKeyFromPrivate(currentPrivateKey)).getAddress()
		return publicKey.prepend0xPrefix()
	}
}

fun Double.toUnitValue(symbol: String = CryptoSymbol.eth): String {
	val formatEditor = DecimalFormat("#")
	formatEditor.maximumFractionDigits = 18
	val value = this / 1000000000000000000.0
	val prefix = if (value >= 1.0) "" else if (value == 0.0) "0." else "0"
	return "$prefix${formatEditor.format(this / 1000000000000000000.0)} $symbol"
}

fun Double.toEthCount(): Double {
	return this / 1000000000000000000.0
}

fun Double.toBTCCount(): Double {
	return this / 100000000.0
}

fun Double.toSatoshi(): Long {
	return (this * 100000000.0).toLong()
}

fun Long.toBTCCount(): Double {
	return this / 100000000.0
}

fun Double.toGasValue(): String {
	val formatEditor = DecimalFormat("#")
	formatEditor.maximumFractionDigits = 9
	return formatEditor.format(this)
}

fun Double.toGWeiValue(): String {
	val formatEditor = DecimalFormat("#")
	formatEditor.maximumFractionDigits = 9
	return formatEditor.format(this / 1000000000)
}

fun Double.formatCurrency(): String {
	val rate = Config.getCurrentRate()
	val formatEditor = DecimalFormat("#")
	formatEditor.maximumFractionDigits = 3
	val value = formatEditor.format(this).toDouble() // 这里要转换 `Double` 和返回的不同
	val prefix = if (value * rate >= 1.0) "" else if (value == 0.0) "0." else "0"
	return prefix + formatEditor.format(this * rate)
}

fun Double.formatCount(count: Int = 9): String {
	val formatEditor = DecimalFormat("#")
	formatEditor.maximumFractionDigits = count
	val value = formatEditor.format(this).toDouble()
	val prefix = if (value >= 1.0) "" else if (value == 0.0) "0." else "0"
	return prefix + formatEditor.format(this)
}

fun Int.daysAgoInMills(): Long =
	CryptoUtils.getTargetDayInMills(-this)

fun Double.toGwei() = (this / 1000000000.0).toLong()
fun Long.scaleToGwei() = this * 1000000000

/**
 * 把常规的 `Double` 个数转换成合约要用的 `hex` 类型,
 * @important Double 是转换后的个数, 不是 `Decimal` 精度的数字
 */
fun BigInteger.toDataString() = this.toHexStringZeroPadded(64, false)

fun String.isValidTaxHash() = length == CryptoValue.taxHashLength

// 这个是返回 `EventLog` 中需要的地址格式
@Throws
fun String.toAddressCode(hasPrefix: Boolean = true): String {
	return if (!Address(this).isValid()) {
		(if (hasPrefix) "0x" else "") + "000000000000000000000000" + substring(2, length)
	} else {
		throw Exception("It is a wrong address code format")
	}
}
@Throws
fun String.toAddressFromCode(): String {
	val address = if (length == 66) {
	"0x" + substring(26, length)
	} else {
		""
	}
	if (!Address(address).isValid())  throw Exception("It is a wrong address code format")
	return address
}

fun <T : List<*>> T.getObjectMD5HexString(): String {
	return try {
		val byteArray = this.toString().toByteArray()
		val md = MessageDigest.getInstance("MD5")
		md.digest(byteArray).toNoPrefixHexString()
	} catch (error: Exception) {
		println(error)
		"error"
	}
}

fun String.getObjectMD5HexString(): String {
	return try {
		val byteArray = this.toByteArray()
		val md = MessageDigest.getInstance("MD5")
		md.digest(byteArray).toNoPrefixHexString()
	} catch (error: Exception) {
		println(error)
		"error"
	}
}