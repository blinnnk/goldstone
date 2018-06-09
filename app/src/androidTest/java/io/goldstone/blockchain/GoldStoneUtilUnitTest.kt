package io.goldstone.blockchain

import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.CountryCode
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.quotation.quotation.view.QuotationCell
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
	fun getChartValue() {
		QuotationCell.getChardGridValue(4100f, 4111f) { min, max, step ->
			LogUtil.debug("$positon getChartValue", "min - $min max - $max step - $step")
		}
	}
	
	@Test
	fun getAppconfig() {
		AppConfigTable.getAppConfig {
			LogUtil.debug("$positon + getAppconfig", it.toString())
		}
	}
}