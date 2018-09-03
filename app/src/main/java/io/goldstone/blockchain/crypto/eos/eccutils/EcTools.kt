package io.goldstone.blockchain.crypto.eos.eccutils

import io.goldstone.blockchain.crypto.eos.ecc.CurveParam
import io.goldstone.blockchain.crypto.eos.ecc.EcPoint
import java.math.BigInteger


object EcTools {

	private val sCurveParams = arrayOfNulls<CurveParam>(2)

	fun getCurveParam(curveType: Int): CurveParam? {

		if (curveType < 0 || sCurveParams.size <= curveType) {
			throw IllegalArgumentException("Unknown Curve Type: $curveType")
		}

		if (null == sCurveParams[curveType]) {
			if (CurveParam.SECP256_K1 == curveType) {
				sCurveParams[CurveParam.SECP256_K1] = CurveParam(CurveParam.SECP256_K1, "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F" // p
					, "0"  // a
					, "7"  // b
					, "79BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798"  //Gx
					, "483ADA7726A3C4655DA4FBFC0E1108A8FD17B448A68554199C47D08FFB10D4B8"  //Gy
					, "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141") // n
			} else if (CurveParam.SECP256_R1 == curveType) {
				sCurveParams[CurveParam.SECP256_R1] = CurveParam(CurveParam.SECP256_R1, "ffffffff00000001000000000000000000000000ffffffffffffffffffffffff" // p
					, "ffffffff00000001000000000000000000000000fffffffffffffffffffffffc" // a
					, "5ac635d8aa3a93e7b3ebbd55769886bc651d06b0cc53b0f63bce3c3e27d2604b" // b
					, "6b17d1f2e12c4247f8bce6e563a440f277037d812deb33a0f4a13945d898c296"  //Gx
					, "4fe342e2fe1a7f9b8ee7eb4a7c0f9e162bce33576b315ececbb6406837bf51f5"  //Gy
					, "ffffffff00000000ffffffffffffffffbce6faada7179e84f3b9cac2fc632551") // n
			}
		}

		return sCurveParams[curveType]
	}


	/**
	 * Get the length of the byte encoding of a field element
	 */
	@JvmStatic fun getByteLength(fieldSize: Int): Int {
		return (fieldSize + 7) / 8
	}

	/**
	 * Get a big integer as an array of bytes of a specified length
	 */
	@JvmStatic fun integerToBytes(s: BigInteger, length: Int): ByteArray {
		val bytes = s.toByteArray()

		if (length < bytes.size) {
			// The length is smaller than the byte representation. Truncate by
			// copying over the least significant bytes
			val tmp = ByteArray(length)
			System.arraycopy(bytes, bytes.size - tmp.size, tmp, 0, tmp.size)
			return tmp
		} else if (length > bytes.size) {
			// The length is larger than the byte representation. Copy over all
			// bytes and leave it prefixed by zeros.
			val tmp = ByteArray(length)
			System.arraycopy(bytes, 0, tmp, tmp.size - bytes.size, bytes.size)
			return tmp
		}
		return bytes
	}

	/**
	 * Multiply a point with a big integer
	 */
	@JvmStatic
	fun multiply(p: EcPoint, k: BigInteger): EcPoint {
		val h = k.multiply(BigInteger.valueOf(3))

		val neg = p.negate()
		var R = p

		for (i in h.bitLength() - 2 downTo 1) {
			R = R.twice()

			val hBit = h.testBit(i)
			val eBit = k.testBit(i)

			if (hBit != eBit) {
				R = R.add(if (hBit) p else neg)
			}
		}

		return R
	}

	@JvmStatic
	fun sumOfTwoMultiplies(P: EcPoint, k: BigInteger, Q: EcPoint, l: BigInteger): EcPoint {
		val m = Math.max(k.bitLength(), l.bitLength())
		val Z = P.add(Q)
		var R = P.curve.infinity

		for (i in m - 1 downTo 0) {
			R = R.twice()

			if (k.testBit(i)) {
				R = if (l.testBit(i)) {
					R.add(Z)
				} else {
					R.add(P)
				}
			} else {
				if (l.testBit(i)) {
					R = R.add(Q)
				}
			}
		}

		return R
	}

	//ported from BitcoinJ
	@JvmStatic
	fun decompressKey(param: CurveParam, x: BigInteger, firstBit: Boolean): EcPoint {
		val size = 1 + getByteLength(param.curve.fieldSize)// Secp256k1Param.curve.getFieldSize());
		val dest = integerToBytes(x, size)
		dest[0] = (if (firstBit) 0x03 else 0x02).toByte()
		return param.curve.decodePoint(dest)// Secp256k1Param.curve.decodePoint(dest);
	}
}

