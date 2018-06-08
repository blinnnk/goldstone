package io.goldstone.blockchain

import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.CountryCode
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
class GoldStoneText {
	
	@Rule
	@JvmField
	val mActivityRule = ActivityTestRule(MainActivity::class.java)
	
	@Test
	fun getSysteDefaultLanguageSymbol() {
		LogUtil.debug("Get System Language Symbol", CountryCode.currentLanguageSymbol)
	}
}