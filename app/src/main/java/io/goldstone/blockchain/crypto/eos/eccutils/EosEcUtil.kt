@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package io.goldstone.blockchain.crypto.eos.eccutils

import android.text.TextUtils
import com.blinnnk.extension.orZero
import io.goldstone.blockchain.crypto.eos.ecc.CurveParam
import io.goldstone.blockchain.crypto.eos.ecc.Ripemd160
import org.bitcoinj.core.Base58
import java.util.*
import java.util.regex.PatternSyntaxException


object EosEcUtil {

	private const val PREFIX_K1 = "K1"
	private const val PREFIX_R1 = "R1"


	private const val EOS_CRYPTO_STR_SPLITTER = "_"

	fun getBytesIfMatchedRipemd160(base58Data: String, prefix: String?, checksumRef: RefValue<Long>?): ByteArray {
		val prefixBytes = if (TextUtils.isEmpty(prefix)) ByteArray(0) else prefix?.toByteArray()
		val data = Base58.decode(base58Data)
		val toHashData = ByteArray(data.size - 4 + prefixBytes?.size.orZero())
		System.arraycopy(data, 0, toHashData, 0, data.size - 4) // key data
		System.arraycopy(prefixBytes, 0, toHashData, data.size - 4, prefixBytes?.size.orZero())

		val ripemd160 = Ripemd160.from(toHashData) //byte[] data, int startOffset, int length
		val checksumByCal = BitUtils.unit32ToLong(ripemd160.bytes(), 0)
		val checksumFromData = BitUtils.unit32ToLong(data, data.size - 4)
		if (checksumByCal != checksumFromData) {
			throw IllegalArgumentException("Invalid format, checksum mismatch")
		}

		if (checksumRef != null) {
			checksumRef.data = checksumFromData
		}

		return Arrays.copyOfRange(data, 0, data.size - 4)
	}

	@JvmStatic
	fun encodeEosCrypto(prefix: String, curveParam: CurveParam?, data: ByteArray): String {
		var typePart = ""
		if (curveParam != null) {
			if (curveParam.isType(CurveParam.SECP256_K1)) {
				typePart = PREFIX_K1
			} else if (curveParam.isType(CurveParam.SECP256_R1)) {
				typePart = PREFIX_R1
			}
		}

		val toHashData = ByteArray(data.size + typePart.length)
		System.arraycopy(data, 0, toHashData, 0, data.size)
		if (typePart.isNotEmpty()) {
			System.arraycopy(typePart.toByteArray(), 0, toHashData, data.size, typePart.length)
		}

		val dataToEncodeBase58 = ByteArray(data.size + 4)

		val ripemd160 = Ripemd160.from(toHashData)
		val checksumBytes = ripemd160.bytes()

		System.arraycopy(data, 0, dataToEncodeBase58, 0, data.size) // copy source data
		System.arraycopy(checksumBytes, 0, dataToEncodeBase58, data.size, 4) // copy checksum data

		val result: String
		result = if (typePart.isEmpty()) {
			prefix
		} else {
			prefix + EOS_CRYPTO_STR_SPLITTER + typePart + EOS_CRYPTO_STR_SPLITTER
		}

		return result + Base58.encode(dataToEncodeBase58)
	}

	fun safeSplitEosCryptoString(cryptoStr: String): Array<String> {
		if (TextUtils.isEmpty(cryptoStr)) {
			return arrayOf(cryptoStr)
		}

		try {
			return cryptoStr.split(EOS_CRYPTO_STR_SPLITTER.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
		} catch (e: PatternSyntaxException) {
			e.printStackTrace()
			return arrayOf(cryptoStr)
		}

	}

	fun getCurveParamFrom(curveType: String): CurveParam? {
		return EcTools.getCurveParam(if (PREFIX_R1 == curveType) CurveParam.SECP256_R1 else CurveParam.SECP256_K1)
	}
}
