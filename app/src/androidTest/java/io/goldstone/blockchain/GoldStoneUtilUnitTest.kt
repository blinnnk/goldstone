@file:Suppress("DEPRECATION")

package io.goldstone.blockchain

import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.blinnnk.extension.getTargetChild
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.CountryCode
import io.goldstone.blockchain.crypto.bip39.Mnemonic
import io.goldstone.blockchain.crypto.utils.toCryptHexString
import io.goldstone.blockchain.crypto.utils.toStringFromHex
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.database.GoldStoneDataBase
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import junit.framework.Assert
import org.jetbrains.anko.doAsync
import org.json.JSONObject
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
	private val position = this.javaClass.simpleName

	@Test
	fun getSystemDefaultLanguageSymbol() {
		LogUtil.debug("$position Get System Language Symbol", CountryCode.currentLanguageSymbol)
	}

	@Test
	fun getAppConfig() {
		AppConfigTable.getAppConfig {
			LogUtil.debug("$position + getAppconfig", it.apply { it?.terms = "" }.toString())
		}
	}

	@Test
	fun getSystemParameter() {
		LogUtil.debug(position, CountryCode.currentCountry)
		LogUtil.debug(position + "getSystemParameter", CountryCode.currentLanguageSymbol)
	}

	@Test
	fun hexStringConverter() {
		LogUtil.debug(position, "你好".toCryptHexString())
		LogUtil.debug(position, "e7bb86e88a82".toUpperCase().toStringFromHex())
	}

	@Test
	fun getCurrentWallet() {
		WalletTable.getCurrentWallet {
			LogUtil.debug("getWalletByEthseriesAddress + $position", this.toString())
		}
	}

	@Test
	fun getAllWallets() {
		WalletTable.getAll {
			LogUtil.debug("getWalletByEthseriesAddress + $position", this.toString())
		}
	}

	@Test
	fun getWatchOnlyAddress() {
		WalletTable.getWatchOnlyWallet {
			LogUtil.debug("getWatchOnlyAddress", "$this")
		}
	}

	@Test
	fun getMyTokenTable() {
		doAsync {
			GoldStoneDataBase.database.myTokenDao().getAll().let {
				LogUtil.debug("getMyTokenTable", "$it")
			}
		}
	}

	@Test
	fun getTransactionTable() {
		doAsync {
			GoldStoneDataBase.database.transactionDao().getAll().let {
				LogUtil.debug("getTransactionTable", "$it")
			}
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
	fun getMyContactTable() {
		ContactTable.getAllContacts {
			LogUtil.debug("getMyContactTable", "$it")
		}
	}

	data class PricePairModel(val pair: String, val price: String)
	data class PriceAlarmClockTable(
		val pair: String,
		val price: String,
		val priceType: Int,
		val status: Boolean,
		var marketPrice: String
	)

	@Test
	fun getMultiChildJSONObject() {
		val expect = "kaysaith"
		val data = JSONObject("{data : { value: { name: kaysaith }}}")
		LogUtil.debug(position, data.getTargetChild("data", "value", "name"))
		val result = data.getTargetChild("data", "value", "name")
		Assert.assertTrue("convert to wrong value", expect == result)
	}

	@Test
	fun getAllLocalEOSAccount() {
		doAsync {
			val localData = GoldStoneDataBase.database.eosAccountDao().getAll()
			LogUtil.debug("all local eos account tables", localData.toString())
		}
	}

	@Test
	fun getAllLocalWallets() {
		doAsync {
			val wallets = GoldStoneDataBase.database.walletDao().getAllWallets()
			LogUtil.debug("all local wallet tables", wallets.toString())
		}
	}
}

