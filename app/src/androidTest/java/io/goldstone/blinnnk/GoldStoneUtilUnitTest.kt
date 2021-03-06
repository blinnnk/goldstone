@file:Suppress("DEPRECATION")

package io.goldstone.blinnnk

import android.support.test.filters.LargeTest
import android.support.test.internal.util.LogUtil
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.blinnnk.extension.getTargetChild
import io.goldstone.blinnnk.common.value.CountryCode
import io.goldstone.blinnnk.crypto.bip39.Mnemonic
import io.goldstone.blinnnk.crypto.utils.toCryptHexString
import io.goldstone.blinnnk.crypto.utils.toStringFromHex
import io.goldstone.blinnnk.kernel.commontable.AppConfigTable
import io.goldstone.blinnnk.kernel.database.GoldStoneDataBase
import io.goldstone.blinnnk.module.common.walletgeneration.createwallet.model.WalletTable
import io.goldstone.blinnnk.module.home.home.view.MainActivity
import junit.framework.Assert
import kotlinx.coroutines.Dispatchers
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
	fun getAppConfig() {
		AppConfigTable.getAppConfig(Dispatchers.Default) {
			LogUtil.logDebug("$position + getAppConfig", it.apply { it?.terms = "" }.toString())
		}
	}

	@Test
	fun getSystemParameter() {
		LogUtil.logDebug(position, CountryCode.currentCountry)
		LogUtil.logDebug(position + "getSystemParameter", CountryCode.currentLanguageSymbol)
	}

	@Test
	fun hexStringConverter() {
		LogUtil.logDebug(position, "你好".toCryptHexString())
		LogUtil.logDebug(position, "e7bb86e88a82".toUpperCase().toStringFromHex())
	}

	@Test
	fun getCurrentWallet() {
		WalletTable.getCurrent(Dispatchers.Default) {
			LogUtil.logDebug("getWalletByEthseriesAddress + $position", this.toString())
		}
	}

	@Test
	fun getAllWallets() {
		WalletTable.getAll {
			LogUtil.logDebug("getWalletByEthseriesAddress + $position", this.toString())
		}
	}

	@Test
	fun getWatchOnlyAddress() {
		WalletTable.getWatchOnlyWallet {
			LogUtil.logDebug("getWatchOnlyAddress", "$this")
		}
	}

	@Test
	fun getMyTokenTable() {
		doAsync {
			GoldStoneDataBase.database.myTokenDao().getAll().let {
				LogUtil.logDebug("getMyTokenTable", "$it")
			}
		}
	}

	@Test
	fun getTransactionTable() {
		doAsync {
			GoldStoneDataBase.database.transactionDao().getAll().let {
				LogUtil.logDebug("getTransactionTable", "$it")
			}
		}
	}

	@Test
	fun cryptoMnemonic() {
		val mnemonic = "arrest tiger powder ticket snake aunt that debris enrich gown guard people"
		val entropy = Mnemonic.mnemonicToEntropy(mnemonic)
		val decryptEntropy = Mnemonic.entropyToMnemonic(entropy)
		LogUtil.logDebug("cryptoMnemonic", "entroy$entropy decryptEntropy$decryptEntropy")
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
		LogUtil.logDebug(position, data.getTargetChild("data", "value", "name"))
		val result = data.getTargetChild("data", "value", "name")
		Assert.assertTrue("convert to wrong value", expect == result)
	}

	@Test
	fun getAllLocalEOSAccount() {
		doAsync {
			val localData = GoldStoneDataBase.database.eosAccountDao().getAll()
			LogUtil.logDebug("all local eos account tables", localData.toString())
		}
	}

	@Test
	fun getAllLocalWallets() {
		doAsync {
			val wallets = GoldStoneDataBase.database.walletDao().getAllWallets()
			LogUtil.logDebug("all local wallet tables", wallets.toString())
		}
	}
}

