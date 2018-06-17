@file:Suppress("DEPRECATION")

package io.goldstone.blockchain

import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import com.blinnnk.extension.isNull
import com.blinnnk.extension.orEmpty
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.common.value.WebUrl
import io.goldstone.blockchain.kernel.commonmodel.AppConfigTable
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import io.goldstone.blockchain.module.home.home.view.MainActivity
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationTable
import junit.framework.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @date 2018/6/9 7:25 PM
 * @author KaySaith
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class GoldStoneServerUnitTest {
	
	@Rule
	@JvmField
	val mActivityRule = ActivityTestRule(MainActivity::class.java)
	private val positon = this.javaClass.simpleName
	
	@Test
	fun searchTokenBySymbolOrContract() {
		// Change any symbo or contract value to test the result
		val symbolOrContract = "t"
		GoldStoneAPI.getCoinInfoBySymbolFromGoldStone(symbolOrContract, {
			LogUtil.error("$positon GetSearchToken", it)
		}) {
			LogUtil.debug("$positon GetSearchToken", it.toString())
			// it must has result with `t` value by contract, if result is empty will be failed
			assertTrue("Search token with `tr` by symbol or contract is empty", it.isNotEmpty())
		}
	}
	
	@Test
	fun searchQuotationByPair() {
		// Change any symbo value to test the result
		val symbol = "tr"
		GoldStoneAPI.getMarketSearchList(symbol, {
			LogUtil.error("$positon SearchQuotation", it)
		}) {
			LogUtil.debug("$positon SearchQuotation", it.toString())
			// it must has result with `t` value, if result is empty will be failed
			assertTrue("Search pair quotation with `tr` is empty", it.isNotEmpty())
		}
	}
	
	@Test
	fun getShareContent() {
		GoldStoneAPI.getShareContent(
			{
				LogUtil.error("$positon getShareContent", it)
			}) {
			LogUtil.debug("$positon getShareContent", it.toString())
			// Share content title, content, url must not be empty
			assertTrue("Share title is empty", it.title.isNotEmpty())
			assertTrue("Share content is empty", it.content.isNotEmpty())
			assertTrue("Share url is empty", it.url.isNotEmpty())
		}
	}
	
	@Test
	fun getNotificationList() {
		NotificationTable.getAllNotifications { localData ->
			AppConfigTable.getAppConfig { config ->
				val latestTime = localData.maxBy { it.createTime }?.createTime
				val requestTime = if (latestTime.isNull()) 0 else latestTime!!
				GoldStoneAPI.getNotificationList(
					config?.goldStoneID.orEmpty(),
					requestTime,
					{
						LogUtil.error("$positon getNotificationList", it)
					}
				) {
					Log.d("$positon + getNotificationList", it.toString())
				}
			}
		}
	}
	
	@Test
	fun getTermsFromServer() {
		GoldStoneAPI.getTerms(
			"hello",
			{
				LogUtil.error("$positon GetTermsFromServer", it)
			}
		) {
			LogUtil.debug(positon, it)
			assertTrue("Terms is empty", it.isNotEmpty())
		}
	}
	
	@Test
	fun getConfigList() {
		GoldStoneAPI.getConfigList(
			{
				LogUtil.error("$positon GetConfigList", it)
			}
		) {
			LogUtil.debug(positon, it.toString())
		}
	}
	
	@Test
	fun getWebUrlValue() {
		val terms =
			"${WebUrl.header}/${WebUrl.webLanguage(Config.getCurrentLanguageCode())}/termAndConditions"
		LogUtil.debug("getWebUrlValue", terms)
	}
	
	@Test
	fun getUnreadCount() {
		AppConfigTable.getAppConfig {
			it?.apply {
				GoldStoneAPI.getUnreadCount(
					it.goldStoneID,
					System.currentTimeMillis(),
					{ LogUtil.error(positon, it) }
				) {
					LogUtil.debug(positon + "getUnreadCount", it)
				}
			}
		}
	}
}