package io.goldstone.blockchain.crypto.eos

import com.subgraph.orchid.encoders.Hex
import io.goldstone.blockchain.crypto.bip32.generateKey
import io.goldstone.blockchain.crypto.bip39.Mnemonic
import io.goldstone.blockchain.crypto.bitcoin.BTCUtils
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
}