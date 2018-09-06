package io.goldstone.blockchain.crypto.eos.account

/**
 * @author KaySaith
 * @date 2018/09/05
 */

import io.goldstone.blockchain.crypto.eos.ecc.CurveParam
import io.goldstone.blockchain.crypto.eos.ecc.EcDsa
import io.goldstone.blockchain.crypto.eos.ecc.EcPoint
import io.goldstone.blockchain.crypto.eos.ecc.Sha256
import io.goldstone.blockchain.crypto.eos.eccutils.EcSignature
import io.goldstone.blockchain.crypto.eos.eccutils.EcTools
import io.goldstone.blockchain.crypto.eos.eccutils.EosEcUtil
import org.bitcoinj.core.Base58
import org.bitcoinj.core.DumpedPrivateKey
import org.bitcoinj.params.MainNetParams
import java.math.BigInteger
import java.security.SecureRandom
import kotlin.experimental.and

class EosPrivateKey(base58: String) {

	val asBigInteger: BigInteger
	val publicKey: EosPublicKey

	val curveParam: CurveParam

	val bytes: ByteArray
		get() {
			val result = ByteArray(32)
			val bytes = asBigInteger.toByteArray()
			if (bytes.size <= result.size) {
				System.arraycopy(bytes, 0, result, result.size - bytes.size, bytes.size)
			} else {
				if (bytes.size != 33 || bytes[0].toInt() != 0) throw AssertionError()
				System.arraycopy(bytes, 1, result, 0, bytes.size - 1)
			}
			return result
		}

	init {
		val split = EosEcUtil.safeSplitEosCryptoString(base58)
		val keyPair = DumpedPrivateKey.fromBase58(MainNetParams.get(), base58)
		val keyBytes = keyPair.key.privKeyBytes
		curveParam = if (split.size == 1) {
			EcTools.getCurveParam(CurveParam.SECP256_K1)!!
		} else {
			if (split.size < 3) {
				throw IllegalArgumentException("Invalid private key format: $base58")
			}
			EosEcUtil.getCurveParamFrom(split[1])!!
		}
		asBigInteger = getOrCreatePrivateKeyBigInteger(keyBytes)
		publicKey = EosPublicKey(findPubKey(asBigInteger), curveParam)
	}

	fun clear() {
		asBigInteger.multiply(BigInteger.ZERO)
	}

	private fun findPubKey(bnum: BigInteger): ByteArray {
		var point = EcTools.multiply(curveParam.G(), bnum) // Secp256k1Param.G, bnum);
		point = EcPoint(point.curve, point.x, point.y, true)
		return point.encoded
	}

	private fun toWif(): String {
		val rawPrivateKey = bytes
		val resultWIFBytes = ByteArray(1 + 32 + 4)
		resultWIFBytes[0] = 0x80.toByte()
		System.arraycopy(rawPrivateKey, if (rawPrivateKey.size > 32) 1 else 0, resultWIFBytes, 1, 32)
		val hash = Sha256.doubleHash(resultWIFBytes)
		System.arraycopy(hash.bytes, 0, resultWIFBytes, 33, 4)
		return Base58.encode(resultWIFBytes)
	}

	fun sign(digest: Sha256): EcSignature {
		return EcDsa.sign(digest, this)
	}

	override fun toString(): String {
		return if (curveParam.isType(CurveParam.SECP256_K1)) {
			toWif()
		} else EosEcUtil.encodeEosCrypto(PREFIX, curveParam, bytes)

	}

	private fun getOrCreatePrivateKeyBigInteger(value: ByteArray?): BigInteger {
		if (null != value) {
			return if (value[0].toInt() and 0x80 != 0) {
				BigInteger(1, value)
			} else BigInteger(value)
		}

		val nBitLength = curveParam.n().bitLength()// Secp256k1Param.n.bitLength();
		var d: BigInteger
		do {
			// Make a BigInteger from bytes to ensure that Android and 'classic'
			// java make the same BigIntegers from the same random source with the
			// same seed. Using BigInteger(nBitLength, random)
			// produces different results on Android compared to 'classic' java.
			val bytes = ByteArray(nBitLength / 8)
			secuRandom.nextBytes(bytes)
			bytes[0] = (bytes[0] and 0x7F) // ensure positive number
			d = BigInteger(bytes)
		} while (d == BigInteger.ZERO || d >= curveParam.n())// Secp256k1Param.n) >= 0));

		return d
	}

	companion object {
		const val PREFIX = "PVT"
		@JvmField
		val secuRandom = SecureRandom()
	}
}
