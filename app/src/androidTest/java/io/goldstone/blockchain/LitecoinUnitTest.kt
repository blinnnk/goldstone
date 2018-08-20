@file:Suppress("DEPRECATION")

package io.goldstone.blockchain

import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import io.goldstone.blockchain.crypto.DefaultPath
import io.goldstone.blockchain.crypto.bip32.generateKey
import io.goldstone.blockchain.crypto.bip39.Mnemonic
import io.goldstone.blockchain.crypto.bitcoincash.BCHUtil
import io.goldstone.blockchain.module.home.home.view.MainActivity
import junit.framework.Assert.assertEquals
import junit.framework.Assert.fail
import org.bitcoinj.core.Address
import org.bitcoinj.core.AddressFormatException
import org.bitcoinj.core.DumpedPrivateKey
import org.bitcoinj.core.ECKey
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.params.Networks
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @date 2018/6/9 7:25 PM
 * @author KaySaith
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class LitecoinUnitTest {

	@Rule
	@JvmField
	val mActivityRule = ActivityTestRule(MainActivity::class.java)
	private val positon = this.javaClass.simpleName

	@Test
	fun getLitecoinBalance() {
		val seed = Mnemonic.mnemonicToSeed("strong vacuum adjust earth circle ready east atom sibling spirit nose fit online pepper dirt")
		val address = ECKey.fromPrivate(generateKey(seed, DefaultPath.bchPath).keyPair.privateKey).toAddress(MainNetParams.get())
		System.out.println(
			"++++bch +++ (${address.toBase58()})" +
				BCHUtil.instance.encodeCashAdrressByLegacy(address.toBase58())
		)
	}

	@Test
	@Throws(Exception::class)
	fun getAltNetwork() {
		// An alternative network
		class AltNetwork : MainNetParams() {
			init {
				id = "alt.network"
				addressHeader = 48
				p2shHeader = 5
				dumpedPrivateKeyHeader = 176
				acceptableAddressCodes = intArrayOf(addressHeader, p2shHeader)
			}
		}

		val altNetwork = AltNetwork()
		System.out.println("fuck you")
		// Add new network params
		Networks.register(altNetwork)
		// Check if can parse address
		var params = Address.getParametersFromAddress("LLzY6ARzX9Qr1mKedR9YZ8s6Jdg3Gd6oZe")
		System.out.println(
			DumpedPrivateKey.fromBase58(altNetwork, "T9FwsV8FZAoSRMtGpSBrhA41a1npewo43PY18Cz9Kd1FDGNhoNjA").key.toAddress(altNetwork)
		)
		assertEquals(altNetwork.id, params.id)
		// Check if main network works as before
		params = Address.getParametersFromAddress("17kzeh4N8g49GFvdDzSf8PjaPfyoD1MndL")
		assertEquals(MainNetParams.get().id, params.id)
		// Unregister network
		Networks.unregister(altNetwork)
		try {
			Address.getParametersFromAddress("LLxSnHLN2CYyzB5eWTR9K9rS9uWtbTQFb6")
			fail()
		} catch (e: AddressFormatException) {
		}
	}

}
