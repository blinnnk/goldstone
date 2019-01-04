package io.goldstone.blinnnk.crypto.ethereum

import java.math.BigInteger
import java.util.*

/**
 * @date 2018/6/17 7:04 PM
 * @author KaySaith
 */
internal const val ELEMENT_OFFSET = 128
internal const val LIST_OFFSET = 192

sealed class RLPType

data class RLPElement(val bytes: ByteArray) : RLPType() {
	override fun equals(other: Any?) = when (other) {
		is RLPElement -> Arrays.equals(bytes, other.bytes)
		else -> false
	}
	
	override fun hashCode() = Arrays.hashCode(bytes)
}

data class RLPList(val element: List<RLPType>) : RLPType()

fun BigInteger.toRLP() = RLPElement(toByteArray().removeLeadingZero())
fun ByteArray.toRLP() = RLPElement(this)
fun Byte.toRLP() = RLPElement(ByteArray(1) { this })

fun Int.toByteArray() = ByteArray(4) { i -> shr(8 * (3 - i)).toByte() }
fun Int.toMinimalByteArray() = toByteArray().let { it.copyOfRange(it.minimalStart(), 4) }
private fun ByteArray.minimalStart() =
	indexOfFirst { it != 0.toByte() }.let { if (it == -1) 3 else it }

fun ByteArray.removeLeadingZero() = if (first() == 0.toByte()) copyOfRange(1, size) else this