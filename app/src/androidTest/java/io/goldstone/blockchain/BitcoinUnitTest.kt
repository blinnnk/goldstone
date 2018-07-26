@file:Suppress("DEPRECATION")

package io.goldstone.blockchain

import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.crypto.bitcoin.BTCWalletUtils
import io.goldstone.blockchain.kernel.network.bitcoin.BitcoinApi
import io.goldstone.blockchain.module.home.home.view.MainActivity
import junit.framework.Assert.fail
import org.bitcoinj.core.ECKey
import org.bitcoinj.core.Sha256Hash
import org.bitcoinj.core.Transaction
import org.bitcoinj.core.Utils
import org.bitcoinj.crypto.TransactionSignature
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
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
}