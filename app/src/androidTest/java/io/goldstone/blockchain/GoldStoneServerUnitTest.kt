@file:Suppress("DEPRECATION")

package io.goldstone.blockchain

import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import com.blinnnk.extension.isNull
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
	private val position = this.javaClass.simpleName

	@Test
	fun searchTokenBySymbolOrContract() {
		// Change any symbol or contract value to test the result
		val symbolOrContract = "t"
		GoldStoneAPI.getTokenInfoBySymbolFromServer(symbolOrContract, {
			LogUtil.error("$position GetSearchToken", it)
		}) {
			LogUtil.debug("$position GetSearchToken", it.toString())
			// it must has result with `t` value by contract, if result is empty will be failed
			assertTrue("Search token with `tr` by symbol or contract is empty", it.isNotEmpty())
		}
	}

	@Test
	fun searchQuotationByPair() {
		// Change any symbol value to test the result
		val symbol = "tr"
		GoldStoneAPI.getMarketSearchList(symbol, {
			LogUtil.error("$position SearchQuotation", it)
		}) {
			LogUtil.debug("$position SearchQuotation", it.toString())
			// it must has result with `t` value, if result is empty will be failed
			assertTrue("Search pair quotation with `tr` is empty", it.isNotEmpty())
		}
	}

	@Test
	fun getShareContent() {
		GoldStoneAPI.getShareContent(
			{
				LogUtil.error("$position getShareContent", it)
			}) {
			LogUtil.debug("$position getShareContent", it.toString())
			// Share content title, content, url must not be empty
			assertTrue("Share title is empty", it.title.isNotEmpty())
			assertTrue("Share content is empty", it.content.isNotEmpty())
			assertTrue("Share url is empty", it.url.isNotEmpty())
		}
	}

	@Test
	fun getNotificationList() {
		NotificationTable.getAllNotifications { localData ->
			val latestTime = localData.maxBy { it.createTime }?.createTime
			val requestTime = if (latestTime.isNull()) 0 else latestTime!!
			GoldStoneAPI.getNotificationList(
				requestTime,
				{
					LogUtil.error("$position getNotificationList", it)
				}
			) {
				Log.d("$position + getNotificationList", it.toString())
			}
		}
	}

	@Test
	fun getTermsFromServer() {
		GoldStoneAPI.getTerms(
			"hello",
			{
				LogUtil.error("$position GetTermsFromServer", it)
			}
		) {
			LogUtil.debug(position, it)
			assertTrue("Terms is empty", it.isNotEmpty())
		}
	}

	@Test
	fun getConfigList() {
		GoldStoneAPI.getConfigList(
			{
				LogUtil.error("$position GetConfigList", it)
			}
		) {
			LogUtil.debug(position, it.toString())
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
		AppConfigTable.getAppConfig { config ->
			config?.apply {
				GoldStoneAPI.getUnreadCount(
					config.goldStoneID,
					System.currentTimeMillis(),
					{ LogUtil.error(position, it) }
				) {
					LogUtil.debug(position + "getUnreadCount", it)
				}
			}
		}
	}

	@Test
	fun getETCTransactions() {
		GoldStoneAPI.getETCTransactions(
			"62",
			"0x2D6FAE3553F082B0419c483309450CaF6bC4573E",
			"0",
			{
				LogUtil.error("getETCTransactions", it)
			}
		) {
			LogUtil.debug("getETCTransactions", "$it")
		}
	}
}