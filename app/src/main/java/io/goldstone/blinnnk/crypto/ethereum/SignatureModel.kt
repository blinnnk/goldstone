package io.goldstone.blinnnk.crypto.ethereum

import java.math.BigInteger
import java.math.BigInteger.ZERO

data class SignatureData(
	var r: BigInteger = ZERO,
	var s: BigInteger = ZERO,
	var v: Byte = 0
)