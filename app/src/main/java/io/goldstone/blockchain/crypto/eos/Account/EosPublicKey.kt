package io.goldstone.blockchain.crypto.eos.account

import com.blinnnk.extension.orFalse
import io.goldstone.blockchain.crypto.eos.ecc.CurveParam
import io.goldstone.blockchain.crypto.eos.ecc.Ripemd160
import io.goldstone.blockchain.crypto.eos.eccutils.BitUtils
import io.goldstone.blockchain.crypto.eos.eccutils.EcTools
import io.goldstone.blockchain.crypto.eos.eccutils.EosEcUtil
import io.goldstone.blockchain.crypto.eos.eccutils.RefValue
import java.util.*

class EosPublicKey {

	private val mCheck: Long
	private val mCurveParam: CurveParam?
	val bytes: ByteArray

	class IllegalEosPublickeyFormatException internal constructor(publicKeyStr: String) : IllegalArgumentException("invalid eos public key : $publicKeyStr")

	@JvmOverloads constructor(data: ByteArray, curveParam: CurveParam? = EcTools.getCurveParam(CurveParam.SECP256_K1)) {
		bytes = Arrays.copyOf(data, 33)
		mCurveParam = curveParam

		mCheck = BitUtils.unit32ToLong(Ripemd160.from(bytes, 0, bytes.size).bytes(), 0)
	}

	constructor(base58Str: String) {
		val checksumRef = RefValue<Long>()

		val parts = EosEcUtil.safeSplitEosCryptoString(base58Str)
		if (base58Str.startsWith(LEGACY_PREFIX)) {
			if (parts.size == 1) {
				mCurveParam = EcTools.getCurveParam(CurveParam.SECP256_K1)!!
				bytes = EosEcUtil.getBytesIfMatchedRipemd160(base58Str.substring(LEGACY_PREFIX.length), null, checksumRef)
			} else {
				throw IllegalEosPublickeyFormatException(base58Str)
			}
		} else {
			if (parts.size < 3) {
				throw IllegalEosPublickeyFormatException(base58Str)
			}

			// [0]: prefix, [1]: curve type, [2]: data
			if (PREFIX != parts[0]) throw IllegalEosPublickeyFormatException(base58Str)
			mCurveParam = EosEcUtil.getCurveParamFrom(parts[1])
			bytes = EosEcUtil.getBytesIfMatchedRipemd160(parts[2], parts[1], checksumRef)
		}

		mCheck = checksumRef.data ?: 0
	}

	override fun toString(): String {
		val isR1 = mCurveParam?.isType(CurveParam.SECP256_R1).orFalse()
		return EosEcUtil.encodeEosCrypto(if (isR1) PREFIX else LEGACY_PREFIX, if (isR1) mCurveParam else null, bytes)
	}

	override fun hashCode(): Int {
		return (mCheck and 0xFFFFFFFFL).toInt()
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true

		return if (null == other || javaClass != other.javaClass) false else this.bytes.contentEquals((other as EosPublicKey).bytes)

	}

	companion object {
		private const val LEGACY_PREFIX = "EOS"
		private const val PREFIX = "PUB"
	}

}
