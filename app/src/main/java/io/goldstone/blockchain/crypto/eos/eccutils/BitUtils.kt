package io.goldstone.blockchain.crypto.eos.eccutils

/**
 * @author KaySaith
 * @date 2018/09/04
 */

object BitUtils {

	@JvmStatic fun unit32ToLong(buf: ByteArray, offset: Int): Long {
		var offsetIndex = offset
		return (buf[offsetIndex++].toLong() and 0xFFL or (buf[offsetIndex++].toLong() and 0xFFL shl 8) or (buf[offsetIndex++].toLong() and 0xFFL shl 16)
			or (buf[offsetIndex].toLong() and 0xFFL shl 24))
	}

	// Arrays.copyOfRange implementation which we can use also on Java versions <
	// 1.6
	@JvmStatic fun copyOfRange(original: ByteArray, from: Int, to: Int): ByteArray {
		if (to < from || from < 0 || from > original.size) {
			throw IllegalArgumentException()
		}
		val buf = ByteArray(to - from)
		val lastIndex = Math.min(original.size, to)
		System.arraycopy(original, from, buf, 0, lastIndex - from)
		return buf
	}

}