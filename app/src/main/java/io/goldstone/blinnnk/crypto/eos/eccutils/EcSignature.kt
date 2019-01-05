package io.goldstone.blinnnk.crypto.eos.eccutils

import io.goldstone.blinnnk.crypto.eos.ecc.CurveParam
import org.spongycastle.util.encoders.Hex
import java.math.BigInteger

/**
 * @author KaySaith
 * @date 2018/09/04
 */

class EcSignature internal constructor(
	@JvmField val r: BigInteger,
	@JvmField val s: BigInteger,
	private val curveParam: CurveParam
) {

	@JvmField var recId = -1

	fun setRecid(recid: Int) {
		this.recId = recid
	}

	override fun equals(other: Any?): Boolean {
		if (this === other)
			return true

		if (null == other || javaClass != other.javaClass)
			return false

		val otherSig = other as EcSignature?
		return r == otherSig!!.r && s == otherSig.s
	}

	private fun eosEncodingHex(): String {
		if (recId < 0 || recId > 3) {
			throw IllegalStateException("signature has invalid recid.")
		}

		val headerByte = recId + 27 + 4
		val sigData = ByteArray(65) // 1 header + 32 bytes for R + 32 bytes for S
		sigData[0] = headerByte.toByte()
		System.arraycopy(EcTools.integerToBytes(this.r, 32), 0, sigData, 1, 32)
		System.arraycopy(EcTools.integerToBytes(this.s, 32), 0, sigData, 33, 32)

		return EosEcUtil.encodeEosCrypto(PREFIX, curveParam, sigData)
	}

	override fun toString(): String {
		return if (recId < 0 || recId > 3) {
			"no recovery sig: " + Hex.toHexString(this.r.toByteArray()) + Hex.toHexString(this.s.toByteArray())
		} else eosEncodingHex()

	}

	override fun hashCode(): Int {
		var result = r.hashCode()
		result = 31 * result + s.hashCode()
		result = 31 * result + curveParam.hashCode()
		result = 31 * result + recId
		return result
	}

	companion object {
		private const val PREFIX = "SIG"
	}
}