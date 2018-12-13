package io.goldstone.blockchain.crypto.utils

import com.blinnnk.extension.getDecimalCount
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orZero
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.crypto.ethereum.*
import io.goldstone.blockchain.crypto.extensions.toHexStringZeroPadded
import io.goldstone.blockchain.crypto.multichain.CryptoValue
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

	fun toTargetUnit(
		value: BigDecimal,
		decimal: Double,
		hexadecimal: Double
	): Double {
		val isNegative = value < BigDecimal.ZERO
		return (Math.abs(value.toDouble()) / Math.pow(hexadecimal, decimal)) * if (isNegative) -1 else 1
	}

	fun toGasUsedEther(gas: String?, gasPrice: String?, isHex: Boolean): String {
		return if (gas.isNullOrBlank() || gasPrice.isNullOrBlank()) {
			"0"
		} else if (!isHex) {
			(gas.toBigDecimal() * gasPrice.toBigDecimal()).toETHCount()
		} else {
			(gas.hexToDecimal().toBigDecimal() * gasPrice.hexToDecimal().toBigDecimal()).toETHCount()
		}
	}

	fun toValueWithDecimal(count: Double, decimal: Int = CryptoValue.ethDecimal): BigInteger {
		return (count.toBigDecimal() * Math.pow(10.0, decimal.toDouble()).toBigDecimal()).toBigInteger()
	}

	fun getTransferInfoFromInputData(inputCode: String): InputCodeData? {
		var address: String
		val amount: BigInteger
		return if (isTransferInputCode(inputCode)) {
			// analysis input code and get the received address
			address = inputCode.substring(
				SolidityCode.contractTransfer.length, SolidityCode.contractTransfer.length + 64
			)
			address = (address.substring(address.length - 40, address.length)).prepend0xPrefix()
			// analysis input code and get the received count
			amount = inputCode.substring(74, 138).hexToDecimal()
			InputCodeData("transfer", address, amount)
		} else {
			LogUtil.debug("loadTransferInfoFromInputData", "not a contract transfer")
			null
		}
	}

	fun isERC20Transfer(inputCode: String, hold: () -> Unit = {}): Boolean {
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

fun BigDecimal.toCount(decimal: Int): BigDecimal {
	return divide(BigDecimal.valueOf(10).pow(decimal).add(BigDecimal.ZERO))
}

fun BigDecimal.toETHCount(): String {
	return toCount(18).toPlainString()
}

fun Double.toWei(decimal: Int): BigInteger {
	return (BigDecimal.valueOf(this)).multiply(BigDecimal(10).pow(decimal).add(BigDecimal.ZERO)).toBigInteger()
}

fun Double.toEOSUnit(): BigInteger {
	return (this.toBigDecimal() * BigDecimal.valueOf(10000L)).toBigInteger()
}

fun Double.toAmount(decimal: Int): BigInteger {
	return (BigDecimal.valueOf(this).multiply(BigDecimal(10).pow(decimal).add(BigDecimal.ZERO))).toBigInteger()
}

fun BigInteger.toEOSCount(): Double {
	return CryptoUtils.toCountByDecimal(this, CryptoValue.eosDecimal)
}

fun BigInteger.toCount(decimal: Int): Double {
	return CryptoUtils.toCountByDecimal(this, decimal)
}

fun Double.toSatoshi(): Long {
	return (BigDecimal.valueOf(this).multiply(BigDecimal(10).pow(8).add(BigDecimal.ZERO))).toLong()
}

fun Long.toBTCCount(): Double {
	return BigDecimal(this).divide(BigDecimal(10).pow(8).add(BigDecimal.ZERO)).toDouble()
}


fun Double.formatCurrency(): String {
	val rate = SharedWallet.getCurrentRate()
	val formatEditor = DecimalFormat("#")
	formatEditor.maximumFractionDigits = 5
	return formatEditor.format(this * rate).toBigDecimal().toPlainString()
}

fun Double.formatCount(count: Int = 9): String {
	val isNegative = this < 0
	val formatEditor = DecimalFormat("#")
	formatEditor.maximumFractionDigits = count
	return (if (isNegative) "-" else "") + formatEditor.format(Math.abs(this)).toBigDecimal().toPlainString()
}

fun Double.formatDecimal(count: Int = 9): Double {
	return this.formatCount(count).toDouble()
}

fun BigDecimal.formatCount(count: Int = 9): String {
	val formatEditor = DecimalFormat("#")
	formatEditor.maximumFractionDigits = count
	return formatEditor.format(this).toBigDecimal().toPlainString()
}

fun Int.daysAgoInMills(): Long =
	CryptoUtils.getTargetDayInMills(-this)

fun Long.scaleToGwei() = BigInteger.valueOf(this) * BigInteger.valueOf(10).pow(9).add(BigInteger.ZERO)

/**
 * 把常规的 `Double` 个数转换成合约要用的 `hex` 类型,
 * @important Double 是转换后的个数, 不是 `Decimal` 精度的数字
 */
fun BigInteger.toDataString() = this.toHexStringZeroPadded(64, false)

fun String.isValidTaxHash() = length == CryptoValue.taxHashLength

fun String.toUtf8Bytes() = this.toByteArray(Charsets.UTF_8)
fun stringFromUtf8Bytes(bytes: ByteArray) = String(bytes, Charsets.UTF_8)

// 这个是返回 `EventLog` 中需要的地址格式
@Throws
fun String.toAddressCode(hasPrefix: Boolean = true): String {
	return if (Address(this).isValid()) {
		(if (hasPrefix) "0x" else "") + "000000000000000000000000" + substring(2, length)
	} else {
		throw Exception("It is a wrong address code format")
	}
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