@file:Suppress("DEPRECATION")

package io.goldstone.blockchain

import android.support.test.filters.LargeTest
import android.support.test.internal.util.LogUtil
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import io.goldstone.blockchain.crypto.*
import io.goldstone.blockchain.crypto.extensions.hexToBigInteger
import io.goldstone.blockchain.crypto.litecoin.ChainPrefix
import io.goldstone.blockchain.crypto.litecoin.LTCWalletUtils
import io.goldstone.blockchain.crypto.utils.toHexString
import io.goldstone.blockchain.crypto.walletfile.*
import io.goldstone.blockchain.module.home.home.view.MainActivity
import junit.framework.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigInteger

/**
 * @date 2018/6/17 6:51 PM
 * @author KaySaith
 */
@Suppress("DEPRECATION")
@RunWith(AndroidJUnit4::class)
@LargeTest
class GoldStoneWalletTest {

	@Rule
	@JvmField
	val mActivityRule = ActivityTestRule(MainActivity::class.java)
	private val positon = this.javaClass.simpleName

	@Test
	fun signTransaction() {
		val transaction = Transaction().apply {
			nonce = BigInteger.valueOf(9)
			gasPrice = BigInteger.valueOf(20000000000L)
			gasLimit = BigInteger.valueOf(21000)
			to = Address("0x6E3DF901A984d50b68355eeDE503cBfC1eAd8F13")
			value = BigInteger.valueOf(1000000000000000000L)
		}
		val privateKey =
			"7e1875b5805e160408dad465b57d7227740a1b05e148b53020aa2245be9dfdc9".hexToBigInteger()
		val publicKey = publicKeyFromPrivate(privateKey)
		val keyPair = ECKeyPair(privateKey, publicKey)
		val signatureData = transaction.signViaEIP155(keyPair, ChainDefinition(1L))
		val result = transaction.encodeRLP(signatureData).toHexString()
		val expected =
			"result0xf86c098504a817c800825208946e3df901a984d50b68355eede503cbfc1ead8f13880de0b6b3a76400008026a0f2ee3d057dc5e12a29e2b3c2f3b9f61e867fcd585d65c09f72c65a3c22160e07a048bb82ab020ceb40c55c54bcf4c2421afced529d0c65596fcc1806d0a9db5880"
		LogUtil.logDebug(positon + "signTransaction", result)
		Assert.assertTrue("Sign Transaction wrong", result.equals(expected, true))
	}

	@Test
	fun decryptKeystoreFile() {
		val walletJSON =
			"{\"address\":\"2d6fae3553f082b0419c483309450caf6bc4573e\",\"crypto\":{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"8a687ceb6def34bdb7a544236949bb3d0c883e142da6cadd2beb11ed45817ec2\",\"cipherparams\":{\"iv\":\"4d923c1978f7d7317b893c127d786e62\"},\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":4096,\"p\":6,\"r\":8,\"salt\":\"21b1793cfa58b9579c3180a17c384a620eac23b575a3d45a6461110aae67f034\"},\"mac\":\"24b76db086393c9deabbbc71af8d50bad34156f4d5faba038253f3c0dd4089d6\"},\"id\":\"fb793cf7-ed16-4734-99fb-80fe6c2d4174\",\"version\":3}"
		val keystoreModel = walletJSON.convertKeystoreToModel()
		val cryptWallet = WalletCrypto(
			keystoreModel.cipher,
			keystoreModel.ciphertext,
			CipherParams(keystoreModel.iv),
			keystoreModel.kdf,
			ScryptKdfParams(
				keystoreModel.n,
				keystoreModel.p,
				keystoreModel.r,
				keystoreModel.dklen,
				keystoreModel.salt
			),
			keystoreModel.mac
		)
		val wallet = Wallet(
			keystoreModel.address,
			cryptWallet,
			keystoreModel.id,
			keystoreModel.version
		)
		val expectValue = "2d6fae3553f082b0419c483309450caf6bc4573e"
		val address = wallet.decrypt("125883").getAddress()
		Assert.assertTrue(
			"Wrong Keystore Value",
			address.equals(expectValue, true)
		)
	}

	@Test
	fun generateLitecoinAccount() {
		LTCWalletUtils.getPrivateKeyFromWIFKey("T8DGhBg9M1WJdTcjLVvsHWZPAFgbov6VckWxHZKix591eGaoS6Ws", ChainPrefix.Litecoin).let {
			LogUtil.logDebug(positon + "getPrivateKeyFromWIFKey", it)
		}
	}
}