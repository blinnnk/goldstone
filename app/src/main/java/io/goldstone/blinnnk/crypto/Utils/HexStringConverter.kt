package io.goldstone.blinnnk.crypto.utils

import android.util.Log
import io.goldstone.blinnnk.crypto.ethereum.SolidityCode
import java.io.ByteArrayOutputStream

/**
 * @date 2018/6/13 4:21 PM
 * @author ZhangTaoShuai
 * @reWriter KaySaith
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


fun String.toStringFromHex(): String {
	val hexStringRegular = "0123456789ABCDEF".toCharArray()
	if (isEmpty()) return ""
	val validFormattedText =
		if (substring(0, 2).equals(SolidityCode.ethTransfer, true)) substring(2, length)
		else this
	val byteArrayOutputStream = ByteArrayOutputStream(validFormattedText.length / 2)
	var index = 0
	// 将每2位16进制整数组装成一个字节
	while (index < validFormattedText.length) {
		try {
			byteArrayOutputStream.write(
				hexStringRegular.indexOf(validFormattedText[index]) shl 4 or hexStringRegular.indexOf(
					validFormattedText[index + 1]
				)
			)
			index += 2
		} catch (error: Exception) {
			Log.e("convertHexToString", error.message)
			return try {
				toAscii(false)
			} catch (error: Exception) {
				" "
			}
		}
	}
	return String(byteArrayOutputStream.toByteArray())
}