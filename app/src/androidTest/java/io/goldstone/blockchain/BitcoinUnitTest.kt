@file:Suppress("DEPRECATION")

package io.goldstone.blockchain

import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.crypto.bitcoin.BTCTransactionUtils
import io.goldstone.blockchain.crypto.bitcoin.BTCWalletUtils
import io.goldstone.blockchain.crypto.bitcoin.DoubleSHA256
import io.goldstone.blockchain.crypto.bitcoin.toLittleEndian
import io.goldstone.blockchain.crypto.utils.toNoPrefixHexString
import io.goldstone.blockchain.kernel.network.bitcoin.BitcoinApi
import io.goldstone.blockchain.module.home.home.view.MainActivity
import junit.framework.Assert.assertTrue
import junit.framework.Assert.fail
import org.bitcoinj.core.ECKey
import org.bitcoinj.core.Sha256Hash
import org.bitcoinj.core.Transaction
import org.bitcoinj.core.Utils
import org.bitcoinj.core.Utils.HEX
import org.bitcoinj.crypto.TransactionSignature
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.spongycastle.util.encoders.Hex
import java.math.BigInteger
import java.util.*

/**
 * @date 2018/6/9 7:25 PM
 * @author KaySaith
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class BitcoinUnitTest {
	
	@Rule
	@JvmField
	val mActivityRule = ActivityTestRule(MainActivity::class.java)
	private val positon = this.javaClass.simpleName
	
	@Test
	fun getBitcoinBalance() {
		val address = "mh9F9Bpb9XcKmCnU6BkAe55bC8xwSqHyVw"
		BitcoinApi.getBalanceByAddress(address) {
			LogUtil.debug("$positon getBitcoinBalance", "$it")
		}
	}
	
	@Test
	fun getBitcoinAddress() {
		val seedCode = "yard impulse luxury drive today throw farm pepper survey wreck glass federal"
		
		BTCWalletUtils.getBitcoinWalletByMnemonic(seedCode) { address, secret ->
			LogUtil.debug("getBitcoinAddress", "$address and $secret")
			
			BTCWalletUtils.getPublicKeyFromBase58PrivateKey(secret, false) {
				LogUtil.debug(
					"getBitcoinAddress",
					it
				)
			}
		}
	}
	
	@Test
	@Throws(Exception::class)
	fun testCreatedSigAndPubkeyAreCanonical() {
		// Tests that we will not generate non-canonical pubkeys or signatures
		// We dump failed data to error log because this test is not expected to be deterministic
		val key = ECKey()
		if (!ECKey.isPubKeyCanonical(key.pubKey)) {
			LogUtil.debug("testCreatedSigAndPubkeyAreCanonical", Utils.HEX.encode(key.pubKey))
			fail()
		}
		val hash = ByteArray(32)
		Random().nextBytes(hash)
		val sigBytes = key.sign(Sha256Hash.wrap(hash)).encodeToDER()
		val encodedSig = Arrays.copyOf(sigBytes, sigBytes.size + 1)
		encodedSig[sigBytes.size] = Transaction.SigHash.ALL.byteValue()
		if (!TransactionSignature.isEncodingCanonical(encodedSig)) {
			LogUtil.debug("testCreatedSigAndPubkeyAreCanonical", Utils.HEX.encode(sigBytes))
			fail()
		}
	}
	
	@Test
	fun endianConvert() {
		val privateKey = "cPoTY5H8dmo6Dd3MroXrg9xeKghi2CrV31HFn6kzWGb1XGAv1CRm"
		val rawTransaction =
			"0200000001c87dfba0c790f8e8c1b1b74e7fe4af41f633b2bda9096f7ff59682363d601bd70100000000ffffffff0240420f00000000001976a9148cb357fd65f42f78a1ce48b47a018e504bbcd1cf88acc02eb709000000001976a91411d529e706f1366ff97a8fe0ce3ec8ece9bc72ab88ac00000000"
		val doubleSha256 = DoubleSHA256.gen(Hex.decode(rawTransaction))
		val endianHash = doubleSha256.toNoPrefixHexString().toLittleEndian()
		System.out.println(endianHash)
		endianHash?.let {
			val signHash = BTCTransactionUtils.signHash(it, privateKey, true)
			LogUtil.debug("endianConvert", signHash)
		}
	}
	
	@Test
	fun signRawTransaction() {
		BitcoinApi.getUnspentListByAddress("mh9F9Bpb9XcKmCnU6BkAe55bC8xwSqHyVw") {
			System.out.println("**$it")
			BTCTransactionUtils.generateSignedRawTransaction(
				3000000,
				1000000,
				"mtLujvsriGN8Yj2dFKhSchZvrEsf3mwg2G",
				"mh9F9Bpb9XcKmCnU6BkAe55bC8xwSqHyVw",
				it,
				"cPoTY5H8dmo6Dd3MroXrg9xeKghi2CrV31HFn6kzWGb1XGAv1CRm",
				true
			).let {
				System.out.println("___$it")
			}
		}
	}
	
	@Test
	@Throws(Exception::class)
	fun testSignatures() {
		// Test that we can construct an ECKey from a private key (deriving the public from the private), then signing
		// a message with it.
		val privkey =
			BigInteger(1, HEX.decode("180cb41c7c600be951b5d3d0a7334acc7506173875834f7a6c4c786a28fcbb19"))
		val key = ECKey.fromPrivate(privkey)
		val output = key.sign(Sha256Hash.ZERO_HASH).encodeToDER()
		assertTrue(key.verify(Sha256Hash.ZERO_HASH.bytes, output))
		// Test interop with a signature from elsewhere.
		val sig = HEX.decode(
			"3046022100dffbc26774fc841bbe1c1362fd643609c6e42dcb274763476d87af2c0597e89e022100c59e3c13b96b316cae9fa0ab0260612c7a133a6fe2b3445b6bf80b3123bf274d"
		)
		System.out.println("${key.verify(Sha256Hash.ZERO_HASH.bytes, sig)}")
		assertTrue(key.verify(Sha256Hash.ZERO_HASH.bytes, sig))
	}
}