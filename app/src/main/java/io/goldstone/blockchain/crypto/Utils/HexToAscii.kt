package io.goldstone.blockchain.crypto.utils

import java.math.BigInteger

/**
 * @date 31/03/2018 2:47 PM
 * @author KaySaith
 */
fun String.toAscii(removeBlank: Boolean = true): String {
	var hex = clean0xPrefix()
	var ascii = ""
	var string: String
	// Convert hex string to "even" length
	val rmd: Int
	val length: Int = hex.length
	rmd = length % 2
	if (rmd == 1) hex = "0$hex"
	// split into two characters
	var index = 0
	while (index < hex.length - 1) {
		//split the hex into pairs
		val pair = hex.substring(index, index + 2)
		//convert hex to decimal
		val dec = Integer.parseInt(pair, 16)
		string = if (removeBlank) {
			checkCode(dec).trim()
		} else {
			checkCode(dec)
		}
		ascii += string
		index += 2
	}
	return ascii
}

private fun checkCode(dec: Int): String {
	var string: String
	// convert the decimal to character
	string = Character.toString(dec.toChar())
	if (dec < 32 || dec in 127 .. 160) string = ""
	return string
}

/**
 * `hash` 值转换为 `Decimal`
 * 超长数字会导致 直接 转换 `Long` 或 `Double` 丢失精度, 转换类型的时候需要斟酌
 */
fun String.hexToDecimal(): BigInteger {
	// 以太坊的地址都是含有 `0x` 开头, 这里首先去掉 `0x`
	val hexValue = clean0xPrefix()
	return BigInteger(hexValue, 16)
}

fun String.toDecimalFromHex(): String {
	return hexToDecimal().toDouble().formatCount(3)
}

fun String.toDoubleFromHex(): Double {
	return hexToDecimal().toDouble()
}

fun String.toIntFromHex(): Int {
	return hexToDecimal().toInt()
}