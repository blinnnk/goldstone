package io.goldstone.blockchain.crypto.utils

import android.text.format.DateUtils
import com.blinnnk.extension.*
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.crypto.ethereum.*
import io.goldstone.blockchain.crypto.extensions.toHexStringZeroPadded
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.multichain.CryptoValue
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
import java.math.BigDecimal
import java.math.BigInteger
import java.security.MessageDigest
import java.text.DecimalFormat
import java.util.*

/**
 * @date 01/04/2018 7:54 PM
 * @author KaySaith
 */
data class InputCodeData(val type: String, val address: String, val amount: BigInteger)

object CryptoUtils {

	fun scaleMiddleAddress(address: String, halfSize: Int = 12): String {
		return if (address.length > halfSize) address.substring(0, halfSize) + " ... " + address.substring(
			address.length - halfSize,
			address.length
		)
		else address
	}

	fun toCountByDecimal(value: BigInteger, decimal: Int = CryptoValue.ethDecimal): Double {
		return value.toDouble() / Math.pow(10.0, decimal.toDouble())
	}

	fun toCountByDecimal(value: Double, decimal: Int = CryptoValue.ethDecimal): Double {
		return value / Math.pow(10.0, decimal.toDouble())
	}

	fun toTargetUnit(
		value: BigInteger,
		decimal: Double,
		hexadecimal: Double
	): Double {
		return value.toDouble() / Math.pow(hexadecimal, decimal)
	}

	fun toGasUsedEther(gas: String?, gasPrice: String?, isHex: Boolean = true): String {
		return if (gas.isNullOrBlank() || gasPrice.isNullOrBlank()) {
			"0"
		} else if (!isHex) {
			(gas!!.toBigDecimal() * gasPrice!!.toBigDecimal()).toEthCount().toBigDecimal().toPlainString()
		} else {
			(gas!!.hexToDecimal().toBigDecimal() * gasPrice!!.hexToDecimal().toBigDecimal()).toEthCount().toBigDecimal().toPlainString()
		}
	}

	fun toValueWithDecimal(count: Double, decimal: Int = CryptoValue.ethDecimal): BigInteger {
		return (count.toBigDecimal() * Math.pow(10.0, decimal.toDouble()).toBigDecimal()).toBigInteger()
	}

	fun getTransferInfoFromInputData(inputCode: String): InputCodeData? {
		var address: String
		var amount: BigInteger
		isTransferInputCode(inputCode) isTrue {
			// analysis input code and get the received address
			address = inputCode.substring(
				SolidityCode.contractTransfer.length, SolidityCode.contractTransfer.length + 64
			)
			address = toHexValue(address.substring(address.length - 40, address.length))
			// analysis input code and get the received count
			amount = inputCode.substring(74, 138).hexToDecimal()
			return InputCodeData("transfer", address, amount)
		} otherwise {
			LogUtil.debug("loadTransferInfoFromInputData", "not a contract transfer")
			return null
		}
	}

	fun isERC20Transfer(inputData: String): Boolean {
		// 有一部分 `token income` 数据是从 e`vent log` 获取，这个值 `logIndex` 可以做判断
		return inputData.length >= 138 && isTransferInputCode(inputData)
	}

	fun isERC20TransferByInputCode(inputCode: String, hold: () -> Unit = {}): Boolean {
		return if (inputCode.length >= 138 && isTransferInputCode(inputCode)
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

fun Double.toUnitValue(symbol: String = CoinSymbol.eth): String {
	val formatEditor = DecimalFormat("#")
	formatEditor.maximumFractionDigits = 18
	val value = this / 1000000000000000000.0
	val prefix = if (value >= 1.0) "" else if (value == 0.0) "0." else "0"
	return "$prefix${formatEditor.format(this / 1000000000000000000.0)} $symbol"
}

fun BigInteger.toEthCount(): Double {
	return CryptoUtils.toCountByDecimal(this, CryptoValue.ethDecimal)
}

fun BigDecimal.toEthCount(): Double {
	return CryptoUtils.toCountByDecimal(this.toBigInteger(), CryptoValue.ethDecimal)
}

fun Double.toEOSUnit(): BigInteger {
	return (this.toBigDecimal() * BigDecimal.valueOf(10000L)).toBigInteger()
}

fun Double.toAmount(decimal: Int): BigInteger {
	return (this.toBigDecimal() * BigDecimal.valueOf(Math.pow(10.0, decimal.toDouble()))).toBigInteger()
}

fun BigInteger.toEOSCount(): Double {
	return CryptoUtils.toCountByDecimal(this, CryptoValue.eosDecimal)
}

fun BigInteger.toCount(decimal: Int): Double {
	return CryptoUtils.toCountByDecimal(this, decimal)
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
	val rate = SharedWallet.getCurrentRate()
	val formatEditor = DecimalFormat("#")
	formatEditor.maximumFractionDigits = 6
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

fun Double.formatDecimal(count: Int = 9): Double {
	return this.formatCount(count).toDouble()
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
	return if (Address(this).isValid()) {
		(if (hasPrefix) "0x" else "") + "000000000000000000000000" + substring(2, length)
	} else {
		throw Exception("It is a wrong address code format")
	}
}

@Throws
fun String.toAddressFromCode(): String {
	val address = if (length == 66) substring(26, length).prepend0xPrefix() else ""
	if (!Address(address).isValid()) throw Exception("It is a wrong address code format")
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

fun String.isValidDecimal(decimal: Int): Boolean {
	return when {
		getDecimalCount().isNull() -> return true
		getDecimalCount().orZero() > decimal.orZero() -> false
		else -> true
	}
}