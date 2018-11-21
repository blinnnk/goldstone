@file:Suppress("DEPRECATION")

package io.goldstone.blockchain

import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.sharedpreference.SharedWallet
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.Current
import io.goldstone.blockchain.common.value.WebUrl
import io.goldstone.blockchain.crypto.multichain.ChainID
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
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
		GoldStoneAPI.getTokenInfoBySymbol(symbolOrContract, Current.supportChainIDs()) { tokens, error ->
			LogUtil.debug("$position GetSearchToken $error", tokens.toString())
			// it must has result with `t` value by contract, if result is empty will be failed
			assertTrue("Search token with `tr` by symbol or contract is empty", tokens.isNullOrEmpty())
		}
	}

	@Test
	fun getShareContent() {
		GoldStoneAPI.getShareContent { content, _ ->
			LogUtil.debug("$position getShareContent", content.toString())
			// Share content title, content, url must not be empty
			assertTrue("Share title is empty", content?.title?.isNotEmpty() == true)
			assertTrue("Share content is empty", content?.content?.isNotEmpty() == true)
			assertTrue("Share url is empty", content?.url?.isNotEmpty() == true)
		}
	}

	@Test
	fun getNotificationList() {
		NotificationTable.getAllNotifications { localData ->
			val latestTime = localData.maxBy { it.createTime }?.createTime
			val requestTime = if (latestTime.isNull()) 0 else latestTime
			GoldStoneAPI.getNotificationList(requestTime) { list, _ ->
				Log.d("$position + getNotificationList", list.toString())
			}
		}
	}

	@Test
	fun getTermsFromServer() {
		GoldStoneAPI.getTerms { term, _ ->
			LogUtil.debug(position, term.orEmpty())
			assertTrue("Terms is empty", term?.isNotEmpty() == true)
		}
	}

	@Test
	fun getConfigList() {
		GoldStoneAPI.getConfigList { list, _ ->
			LogUtil.debug(position, list.toString())
		}
	}

	@Test
	fun getWebUrlValue() {
		val terms =
			"${WebUrl.header}/${WebUrl.webLanguage(SharedWallet.getCurrentLanguageCode())}/termAndConditions"
		LogUtil.debug("getWebUrlValue", terms)
	}

	@Test
	fun getUnreadCount() {
		GoldStoneAPI.getUnreadCount(
			SharedWallet.getGoldStoneID(),
			System.currentTimeMillis()
		) { count, _ ->
			LogUtil.debug(position + "getUnreadCount", count.toString())
		}
	}

	@Test
	fun getETCTransactions() {
		GoldStoneAPI.getETCTransactions(
			ChainID.ETCTest,
			"0x2D6FAE3553F082B0419c483309450CaF6bC4573E",
			0
		) { transaction, _ ->
			LogUtil.debug("getETCTransactions", "$transaction")
		}
	}
}