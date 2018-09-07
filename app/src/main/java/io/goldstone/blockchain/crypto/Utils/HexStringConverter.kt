package io.goldstone.blockchain.crypto.utils

import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.crypto.ethereum.SolidityCode
import java.io.ByteArrayOutputStream

/**
 * @date 2018/6/13 4:21 PM
 * @author ZhangTaoShuai
 * @reWriter KaySaith
 */
/**
 * @param [stringText] 将String文本（包括中文）转换成Hex16进制字符串
 */
fun String.toCryptHexString(): String {
	val hexStringRegular = "0123456789ABCDEF".toCharArray()
	if (isEmpty()) return ""
	val stringBuilder = StringBuilder("")
	val stringTextBytes =
		if (startsWith("0x")) removePrefix("0x").toByteArray()
		else toByteArray()
	stringTextBytes.forEach {
		val position = (it.toInt() and 0xf0) shr 4
		stringBuilder.append(hexStringRegular[position])
		stringBuilder.append(hexStringRegular[(it.toInt() and 0x0f) shr 0])
	}
	return stringBuilder.toString()
}

/**
 * @param [hexText] 将Hex16进制字符串转换成 String 文本（包括中文）
 */
fun String.toStringFromHex(): String {
	val hexStringRegular = "0123456789ABCDEF".toCharArray()
	if (isEmpty()) return ""
	val validFromatText =
		if (substring(0, 2).equals(SolidityCode.ethTransfer, true)) substring(2, length)
		else this
	val byteArrrayOutputStream = ByteArrayOutputStream(validFromatText.length / 2)
	var index = 0
	// 将每2位16进制整数组装成一个字节
	while (index < validFromatText.length) {
		try {
			byteArrrayOutputStream.write(
				hexStringRegular.indexOf(validFromatText[index]) shl 4 or hexStringRegular.indexOf(
					validFromatText[index + 1]
				)
			)
			index += 2
		} catch (error: Exception) {
			LogUtil.error("convertHexToString", error)
			return try {
				toAscii(false)
			} catch (error: Exception) {
				" "
			}
		}
	}
	return String(byteArrrayOutputStream.toByteArray())
}