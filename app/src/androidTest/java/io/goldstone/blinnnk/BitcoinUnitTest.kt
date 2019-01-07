@file:Suppress("DEPRECATION")

package io.goldstone.blinnnk

import android.support.test.filters.LargeTest
import android.support.test.internal.util.LogUtil
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import io.goldstone.blinnnk.crypto.multichain.ChainType
import io.goldstone.blinnnk.kernel.network.btcseries.insight.InsightApi
import io.goldstone.blinnnk.module.home.home.view.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

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
	private val position = this.javaClass.simpleName

	@Test
	fun getBitcoinBalance() {
		val address = "mh9F9Bpb9XcKmCnU6BkAe55bC8xwSqHyVw"
		InsightApi.getBalance(ChainType.BTC, true, address) { balance, error ->
			LogUtil.logDebug("$position getBitcoinBalance $balance", "$error")
		}
	}
}