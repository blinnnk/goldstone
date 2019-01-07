package io.goldstone.blinnnk.crypto.extensions

import io.goldstone.blinnnk.crypto.utils.clean0xPrefix
import org.spongycastle.jcajce.provider.digest.RIPEMD160
import org.spongycastle.jcajce.provider.digest.SHA256
import java.math.BigInteger
import java.util.*
import kotlin.experimental.or

fun ByteArray.toBitArray(): BooleanArray {
	val bits = BooleanArray(this.size * 8)
	for (byteIndex in this.indices)
		for (bitIndex in 0 .. 7) {
			bits[byteIndex * 8 + bitIndex] = (1 shl (7 - bitIndex)) and this[byteIndex].toInt() != 0
		}
	return bits
}

fun BooleanArray.toByteArray(len: Int = this.size / 8): ByteArray {
	val result = ByteArray(len)
	for (byteIndex in result.indices)
		for (bitIndex in 0 .. 7)
			if (this[byteIndex * 8 + bitIndex]) {
				result[byteIndex] = result[byteIndex] or (1 shl (7 - bitIndex)).toByte()
			}
	return result
}

fun ByteArray.sha256() = SHA256.Digest().let {
	it.update(this)
	it.digest()
}!!

fun ByteArray.ripemd160() = RIPEMD160.Digest().let {
	it.update(this)
	it.digest()
}!!

fun BigInteger.toBytesPadded(length: Int): ByteArray {
	val result = ByteArray(length)
	val bytes = toByteArray()
	val bytesLength: Int
	val srcOffset: Int
	if (bytes[0].toInt() == 0) {
		bytesLength = bytes.size - 1
		srcOffset = 1
	} else {
		bytesLength = bytes.size
		srcOffset = 0
	}

	if (bytesLength > length) {
		throw RuntimeException("Input is too large to put in byte array of size $length")
	}
	val destOffset = length - bytesLength
	System.arraycopy(bytes, srcOffset, result, destOffset, bytesLength)
	return result
}

fun BigInteger.toHexStringNoPrefix(): String = toString(16)

fun BigInteger.toHexStringZeroPadded(size: Int, withPrefix: Boolean = true): String {
	var result = toHexStringNoPrefix()
	val length = result.length
	if (length > size) {
		throw UnsupportedOperationException("Value $result is larger then length $size")
	} else if (signum() < 0) {
		throw UnsupportedOperationException("Value cannot be negative")
	}

	if (length < size) {
		result = "0".repeat(size - length) + result
	}

	return if (withPrefix) {
		"0x$result"
	} else {
		result
	}
}

fun String.hexToBigInteger() = BigInteger(clean0xPrefix(), 16)
fun ByteArray.toBigInteger(offset: Int, length: Int) =
	BigInteger(1, Arrays.copyOfRange(this, offset, offset + length))

fun ByteArray.toBigInteger() = BigInteger(1, this)

