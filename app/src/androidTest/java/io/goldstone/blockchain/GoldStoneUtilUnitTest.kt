@file:Suppress("DEPRECATION")

package io.goldstone.blockchain

import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.CountryCode
import io.goldstone.blockchain.crypto.bip39.Mnemonic
import io.goldstone.blockchain.crypto.bitcoin.BTCUtils
import io.goldstone.blockchain.crypto.getAddress
import io.goldstone.blockchain.crypto.utils.JavaKeystoreUtil
import io.goldstone.blockchain.crypto.utils.prepend0xPrefix
import io.goldstone.blockchain.crypto.utils.toCryptHexString
import io.goldstone.blockchain.crypto.utils.toStringFromHex
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.home.view.MainActivity
import junit.framework.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@Suppress("DEPRECATION", "NAME_SHADOWING")
@RunWith(AndroidJUnit4::class)
@LargeTest
class GoldStoneUtilUnitTest {
	
	@Rule
	@JvmField
	val mActivityRule = ActivityTestRule(MainActivity::class.java)
	private val positon = this.javaClass.simpleName
	
	@Test
	fun getSysteDefaultLanguageSymbol() {
		LogUtil.debug("$positon Get System Language Symbol", CountryCode.currentLanguageSymbol)
	}
	
	@Test
	fun getAppconfig() {
		AppConfigTable.getAppConfig {
			LogUtil.debug("$positon + getAppconfig", it.toString())
		}
	}
	
	@Test
	fun getSystemParameter() {
		LogUtil.debug(positon, CountryCode.currentCountry)
		LogUtil.debug(positon + "getSystemParameter", CountryCode.currentLanguageSymbol)
	}
	
	@Test
	fun hextStringConverter() {
		LogUtil.debug(positon, "你好".toCryptHexString())
		LogUtil.debug(positon, "e7bb86e88a82".toUpperCase().toStringFromHex())
	}
	
	@Test
	fun getBitcoinAddress() {
		val seedCode = "yard impulse luxury drive today throw farm pepper survey wreck glass federal"
		
		BTCUtils.getBitcoinWalletByMnemonic(seedCode) { address, secret ->
			LogUtil.debug("getBitcoinAddress", "$address and $secret")
			
			BTCUtils.getPublicKeyFromBase58PrivateKey(secret, false) {
				LogUtil.debug(
					"getBitcoinAddress",
					"**$it"
				)
			}
		}
	}
	
	@Test
	fun getCurrentWallet() {
		WalletTable.getCurrentWallet {
			LogUtil.debug("getWalletByEthseriesAddress + $positon", it.toString())
		}
	}
	
	@Test
	fun getLatestEthereumChildAddressIndex() {
		WalletTable.getETHAndERCWalletLatestChildAddressIndex { _, ethereumChildAddressIndex ->
			LogUtil.debug("getLatestEthereumChildAddressIndex + $positon", "$ethereumChildAddressIndex")
		}
	}
	
	@Test
	fun cryptoMnemonic() {
		val mnemonic = "arrest tiger powder ticket snake aunt that debris enrich gown guard people"
		val entropy = Mnemonic.mnemonicToEntropy(mnemonic)
		val decryptEntropy = Mnemonic.entropyToMnemonic(entropy)
		LogUtil.debug("cryptoMnemonic", "entroy$entropy decryptEntropy$decryptEntropy")
	}
	
	@Test
	fun newEthereumChildAddress() {
		WalletTable.getETHAndERCWalletLatestChildAddressIndex() { wallet, ethereumChildAddressIndex ->
			wallet.encryptMnemonic?.let {
				val mnemonic = JavaKeystoreUtil().decryptData(it)
				val index = ethereumChildAddressIndex + 1
				val childPath = wallet.ethPath.substringBeforeLast("/") + "/" + index
				val masterKey = Mnemonic.mnemonicToKey(mnemonic, childPath)
				val current = masterKey.keyPair.getAddress().prepend0xPrefix()
				Assert.assertTrue(
					"wrong address value", current.equals
				("0x6e3df901a984d50b68355eede503cbfc1ead8f13", true)
				)
			}
		}
	}
}