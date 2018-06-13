package io.goldstone.blockchain

import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.CountryCode
import io.goldstone.blockchain.crypto.toCryptHexString
import io.goldstone.blockchain.crypto.toStringFromHex
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.module.home.home.view.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
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
}