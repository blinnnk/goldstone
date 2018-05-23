package io.goldstone.blockchain.crypto

import java.math.BigInteger

/**
 * @date 31/03/2018 2:47 PM
 * @author KaySaith
 */

fun String.toAscii(removeBlank: Boolean = true): String {
	/** 去掉前两个字母 `0x` */
	var hex = if (substring(0, 2) == "0x") substring(2, length) else this
	var ascii = ""
	var str: String

	// Convert hex string to "even" length
	val rmd: Int
	val length: Int = hex.length
	rmd = length % 2
	if (rmd == 1) hex = "0$hex"

	// split into two characters
	var i = 0
	while (i < hex.length - 1) {
		//split the hex into pairs
		val pair = hex.substring(i, i + 2)
		//convert hex to decimal
		val dec = Integer.parseInt(pair, 16)
		str = if (removeBlank) {
			checkCode(dec).trim()
		} else {
			checkCode(dec)
		}
		ascii += str
		i += 2
	}
	return ascii
}

private fun checkCode(dec: Int): String {
	var str: String

	// convert the decimal to character
	str = Character.toString(dec.toChar())
	if (dec < 32 || dec in 127 .. 160) str = ""
	return str
}

/**
 * `hash` 值转换为 `Decimal`
 */

fun String.hexToDecimal(): Double {
	// 以太坊的地址都是含有 `0x` 开头, 这里首先去掉 `0x`
	var hexNum = if (this.substring(0, 2) == "0x") replace("0x", "") else this
	val digits = "0123456789ABCDEF"
	hexNum = hexNum.toUpperCase()
	var value: BigInteger = BigInteger.valueOf(0)
	(0 until hexNum.length).map { hexNum[it] }.map { digits.indexOf(it.toString()) }
		.forEachIndexed { index, it ->
			value += (Math.pow(16.0, hexNum.length - (index + 1.0)) * it).toBigDecimal().toBigInteger()
		}
	return value.toDouble()
}

fun String.toDecimalFromHex(): String {
	// 以太坊的地址都是含有 `0x` 开头, 这里首先去掉 `0x`
	var hexNum = if (this.substring(0, 2) == "0x") replace("0x", "") else this
	val digits = "0123456789ABCDEF"
	hexNum = hexNum.toUpperCase()
	var value: BigInteger = BigInteger.valueOf(0)
	(0 until hexNum.length).map { hexNum[it] }.map { digits.indexOf(it.toString()) }
		.forEachIndexed { index, it ->
			value += (Math.pow(16.0, hexNum.length - (index + 1.0)) * it).toLong().toBigDecimal().toBigInteger()
		}
	return value.toDouble().formatHex()
}