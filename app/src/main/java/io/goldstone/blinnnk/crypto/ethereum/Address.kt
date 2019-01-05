package io.goldstone.blinnnk.crypto.ethereum

import io.goldstone.blinnnk.crypto.utils.HEX_REGEX
import io.goldstone.blinnnk.crypto.utils.has0xPrefix

/**
 * @date 2018/6/17 7:00 PM
 * @author KaySaith
 */
data class Address(private val input: String) {
	
	private val cleanHex = input.removePrefix("0x")
	@Transient
	val hex = "0x$cleanHex"
	
	override fun toString() = hex
	
	override fun equals(other: Any?) =
		other is Address && other.cleanHex.toUpperCase() == cleanHex.toUpperCase()
	
	override fun hashCode() = cleanHex.toUpperCase().hashCode()
}

fun Address.isValid() = hex.has0xPrefix() && hex.length == 42 && HEX_REGEX.matches(hex)