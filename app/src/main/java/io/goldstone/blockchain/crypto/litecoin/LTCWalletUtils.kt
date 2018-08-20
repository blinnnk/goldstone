package io.goldstone.blockchain.crypto.litecoin

import io.goldstone.blockchain.crypto.CryptoValue
import io.goldstone.blockchain.crypto.bip32.generateKey
import io.goldstone.blockchain.crypto.bip39.Mnemonic
import io.goldstone.blockchain.crypto.utils.hexToDecimal
import io.goldstone.blockchain.crypto.utils.toNoPrefixHexString
import org.bitcoinj.core.Base58
import org.bitcoinj.core.ECKey
import org.bitcoinj.core.Sha256Hash
import org.bitcoinj.params.MainNetParams
import org.spongycastle.jcajce.provider.digest.RIPEMD160
import org.spongycastle.util.encoders.Hex
import java.math.BigInteger

/**
 * @date 2018/8/10 12:57 PM
 * @author KaySaith
 * @important 不同的链的生成私钥和公钥的时候要配备不同的前缀值。具体需要根据增加链的时候自己查询。
 * Bitcoin Prefix: [https://en.bitcoin.it/wiki/List_of_address_prefixes]
 */

object LTCWalletUtils {
	/**
	 * 地址分为压缩地址和非压缩地址, 非压缩地址为 `04` 开头， 配合 `128` 位的控告地址
	 */

	fun generateBase58Keypair(
		mnemonic: String,
		path: String,
		netVersion: ChainPrefix,
		isCompress: Boolean
	): BaseKeyPair {
		val seed = Mnemonic.mnemonicToSeed(mnemonic, "")
		val keyPair = generateKey(seed, path)
		val base58PrivateKey = generateWIFPrivatekey(keyPair.keyPair.privateKey, netVersion, isCompress)
		val address = generateBase58Address(keyPair.keyPair.privateKey, netVersion, isCompress)
		return BaseKeyPair(address, base58PrivateKey)
	}

	private fun generateBase58Address(
		privateKey: BigInteger,
		version: ChainPrefix,
		isCompress: Boolean
	): String {
		val compressPublicKey = ECKey.fromPrivate(privateKey, isCompress).publicKeyAsHex
		val sha256 = Sha256Hash.hash(Hex.decode(compressPublicKey))
		val publicKeyWithRipemd160 = RIPEMD160.Digest().digest(sha256)
		val mainnetPublicHash = version.publicKey + publicKeyWithRipemd160.toNoPrefixHexString()
		val versionWithSHA256 = Sha256Hash.hash(Hex.decode(mainnetPublicHash.toUpperCase()))
		val double256Hash = Sha256Hash.hash(versionWithSHA256)
		val binary = (mainnetPublicHash + double256Hash.toNoPrefixHexString().substring(0, 8)).toUpperCase()
		return Base58.encode(Hex.decode(binary))
	}

	fun generateBase58AddressByWIFKey(
		wifKey: String,
		version: ChainPrefix
	): String {
		val privateKey =
			ECKey.fromPrivate(getPrivateKeyFromWIFKey(wifKey, version).toBigInteger(16)).privKey
		return generateBase58Address(privateKey, version, true)
	}

	/**
	 * 前缀 + 私钥哈希 + 压缩标识
	 * bitcoin Mainnet `80` Testnet `EF`
	 * 压缩标识
	 * `empty` 不压缩, `01` 在结尾压缩
	 */
	fun generateWIFPrivatekey(
		privateKey: BigInteger,
		version: ChainPrefix,
		isCompress: Boolean
	): String {
		val versionPrivateKey =
			version.privateKey + privateKey.toString(16) + if (isCompress) ChainPrefix.compressSuffix else ""
		val sha256PrivateKey = Sha256Hash.hash(Hex.decode(versionPrivateKey))
		val doubleSha256 = Sha256Hash.hash(sha256PrivateKey)
		val first4bytes = doubleSha256.toNoPrefixHexString().substring(0, 8)
		val finalKey = versionPrivateKey + first4bytes
		return Base58.encode(Hex.decode(finalKey))
	}

	@Throws
	fun getPrivateKeyFromWIFKey(wifKey: String, version: ChainPrefix): String {
		val isValid = isValidWIFKey(wifKey, version)
		if (!isValid) throw Exception("WIF Key is incorrect")
		val decodeKey = Base58.decode(wifKey)
		val decodeHex = decodeKey.toNoPrefixHexString()
		val dropLast4Bytes = decodeHex.substring(0, decodeHex.length - 8)
		return dropLast4Bytes.substring(2, dropLast4Bytes.length - 2)
	}

	/**
	 * 判断 双 `Sha256 hash` 的码是否一致, 以及 `Version Code` 是否一致
	 */
	fun isValidWIFKey(wifKey: String, version: ChainPrefix): Boolean {
		val decode = Base58.decode(wifKey)
		val decodeHex = decode.toNoPrefixHexString()
		val versionCode = decodeHex.substring(0, 2)
		val end4Bytes = decodeHex.substring(decodeHex.length - 8)
		val withoutEndPrefix = decodeHex.substring(0, decodeHex.length - 8)
		val sha256Decode = Sha256Hash.hash(Hex.decode(withoutEndPrefix))
		val doubleSha256 = Sha256Hash.hash(sha256Decode)
		val first4Bytes = doubleSha256.toNoPrefixHexString().substring(0, 8)
		return end4Bytes.equals(first4Bytes, true) && versionCode.equals(version.privateKey, true)
	}

	/**
	 * 压缩 `PublicKey` 规则, `Y `轴的数值 `ToDecimal` 后是奇数 + `02` 是偶数 + `03`
	 */
	private fun String.convertToCompresskey(): String {
		val prefix =
			if (substring(64).hexToDecimal() % 2 == 0.0) {
				"02"
			} else "03"
		return prefix + substring(0, 64)
	}

	fun isValidAddress(address: String): Boolean {
		return when {
			address.isEmpty() -> false
			address.length != CryptoValue.bitcoinAddressLength -> false
			!address.matches("^[1-9A-HJ-NP-Za-z]+$".toRegex()) -> false
			!address.substring(0, 1).equals("L", true) -> false
			else -> true
		}
	}
}

// An alternative network
class LitecoinNetParams : MainNetParams() {
	init {
		id = "alt.network"
		addressHeader = 48
		p2shHeader = 5
		dumpedPrivateKeyHeader = 176
//		acceptableAddressCodes = intArrayOf(addressHeader, p2shHeader)
	}
}
