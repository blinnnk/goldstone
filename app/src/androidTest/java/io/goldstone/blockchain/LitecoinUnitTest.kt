package io.goldstone.blockchain

import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.kernel.network.litecoin.LitecoinApi
import io.goldstone.blockchain.module.home.home.view.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @date 2018/8/13 12:29 PM
 * @author KaySaith
 */


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
		val address = "LfxK2wxsZXcmPjnRWb65Xq1PaLxpZ1AWn2"
		LitecoinApi.getBalanceByAddress(address) {
			LogUtil.debug("$positon getLitecoinBalance", "$it")
		}
	}

}
