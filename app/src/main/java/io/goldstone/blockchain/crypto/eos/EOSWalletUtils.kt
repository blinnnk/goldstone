package io.goldstone.blockchain.crypto.eos

import com.subgraph.orchid.encoders.Hex
import io.goldstone.blockchain.crypto.bip32.generateKey
import io.goldstone.blockchain.crypto.bip39.Mnemonic
import io.goldstone.blockchain.crypto.bitcoin.BTCUtils
import io.goldstone.blockchain.crypto.eos.EOSValue.maxNameLength
import io.goldstone.blockchain.crypto.eos.EOSValue.maxSpecialNameLength
import io.goldstone.blockchain.crypto.litecoin.BaseKeyPair
import io.goldstone.blockchain.crypto.multichain.CoinSymbol
import io.goldstone.blockchain.crypto.multichain.CryptoValue
import io.goldstone.blockchain.crypto.utils.toNoPrefixHexString
import org.bitcoinj.core.Base58
import org.bitcoinj.core.DumpedPrivateKey
import org.bitcoinj.core.ECKey
import org.bitcoinj.params.MainNetParams
import org.spongycastle.jcajce.provider.digest.RIPEMD160
import java.math.BigInteger

/**
 * @date 2018/8/30 11:57 PM
 * @author KaySaith
 */

object EOSWalletUtils {
	fun generateKeyPair(mnemonic: String, path: String): BaseKeyPair {
		val seed = Mnemonic.mnemonicToSeed(mnemonic, "")
		val privateKey = generateKey(seed, path).keyPair.privateKey
		val keyPair = generateKeyPairByPrivateKey(privateKey)
		return BaseKeyPair(keyPair.address, keyPair.privateKey)
	}

	fun generateKeyPairByPrivateKey(privateKey: BigInteger): BaseKeyPair {
		val eckey = ECKey.fromPrivate(privateKey, true)
		val compressPublicKey = eckey.publicKeyAsHex
		val wifPrivateKey = eckey.getPrivateKeyAsWiF(MainNetParams.get())
		val publicKeyWithRipemd160 = RIPEMD160.Digest().digest(Hex.decode(compressPublicKey))
		val checkSum = publicKeyWithRipemd160.toNoPrefixHexString().substring(0, 8)
		val base58PublicKey = Base58.encode(Hex.decode(compressPublicKey + checkSum))
		val eosAddress = "EOS$base58PublicKey"
		return BaseKeyPair(eosAddress, wifPrivateKey)
	}

	fun generateBase58AddressByWIFKey(wifPrivateKey: String): String {
		val net = MainNetParams.get()
		val privateKey = DumpedPrivateKey.fromBase58(net, wifPrivateKey).key.privKey
		return generateKeyPairByPrivateKey(privateKey).address
	}

	fun isValidPrivateKey(privateKey: String): Boolean {
		// `EOS` 的私钥与 `BTC` 的主网私钥是同一个格式
		return BTCUtils.isValidMainnetPrivateKey(privateKey)
	}

	fun isValidAddress(address: String): Boolean {
		return when {
			address.length != CryptoValue.eosAddressLength -> false
			!address.substring(0, 3).equals(CoinSymbol.eos, true) -> false
			else -> true
		}
	}

	/**
	 * EOS Account Name 没有找到特别清晰的官方文档进行校验
	 * 这里采用了这个官方的回答进行校验
	 * https://github.com/EOSIO/eos/issues/955
	 */
	fun isValidAccountName(
		accountName: String,
		isOnlyNormalName: Boolean = true
	): EOSAccountNameChecker {
		val legalCharsIn12 = Regex(".*[a-z1-5.]{0,11}[a-z1-5].*")
		val legalCharsAt13th = Regex(".*[a-j1-5].*")
		// 是否是特殊账号决定长度判断的不同
		val isLegalLength =
			if (isOnlyNormalName) accountName.length == maxNameLength
			else accountName.length in 2 .. maxSpecialNameLength
		if (!isLegalLength) {
			return if (isOnlyNormalName) {
				if (accountName.length < maxNameLength) EOSAccountNameChecker.TooShort
				else EOSAccountNameChecker.TooLong
			} else when {
				accountName.length > maxSpecialNameLength -> EOSAccountNameChecker.TooLong
				else -> EOSAccountNameChecker.TooShort
			}
		}
		val isIllegalSuffixSymbol = accountName.last().toString() == "."
		if (isIllegalSuffixSymbol) {
			return EOSAccountNameChecker.IllegalSuffix
		}
		val isLegalCharacter =
		// 如果是普通用户名检查 `12` 位的规则
			if (isOnlyNormalName) accountName.none { !it.toString().matches(legalCharsIn12) }
			// 如果是特定用户名检查前 `12` 位的规则并且额外检查第 `13` 位的规则
			else {
				if (accountName.length > maxNameLength)
					accountName.substring(0, 11).none { !it.toString().matches(legalCharsIn12) } &&
						accountName.substring(12).none { !it.toString().matches(legalCharsAt13th) }
				else accountName.none { !it.toString().matches(legalCharsIn12) }
			}
		return if (!isLegalCharacter) {
			if (accountName.matches(Regex(".*[6-9].*")) || accountName.contains("0")) {
				EOSAccountNameChecker.NumberOtherThan1To5
			} else if (accountName.length > maxSpecialNameLength) {
				if (accountName.substring(12).matches(Regex(".*[k-z].*")))
					EOSAccountNameChecker.IllegalCharacterAt13th
				else EOSAccountNameChecker.IsLongName
			} else if (accountName.length < maxNameLength)
				EOSAccountNameChecker.IsShortName
			else EOSAccountNameChecker.IsValid
		} else EOSAccountNameChecker.IsValid
	}
}

enum class EOSAccountNameChecker(val content: String, val shortDescription: String) {
	TooLong("Wrong length, this account name is longer than 12", "Length Too Long"),
	TooShort("Wrong Length, this account name is shorter than 12", "Length Too Short"),
	NumberOtherThan1To5("Illegal number in this account name, Only allowed in 1 ~ 5", "Invalid Number"),
	IllegalCharacterAt13th("the 13th character is must in a~j or 1~5", "Invalid 13th Value"),
	ContainsIllegalSymbol("Illegal symbol in this account name, Only allowed '.'", "Illegal Symbol"),
	IllegalSuffix("Illegal suffix in this account name, it never be allowed that contains '.' in name end", "Illegal Suffix"),
	IsShortName("Attention this is a special short account name ", "Special Shot Name"),
	IsLongName("Attention this is a special long account name ", "Special Long Name"),
	IsValid("Is Valid", "Is Valid");

	fun isValid(): Boolean = content.equals(IsValid.content, true)
}