package io.goldstone.blockchain

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.blinnnk.util.getIntFromSharedPreferences
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.SharesPreference
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.TinyNumber
import org.jetbrains.anko.doAsync
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
	
	@Test
	fun useAppContext() {
		// Context of the app under test.
		val appContext = InstrumentationRegistry.getTargetContext()
		assertEquals("io.goldstone.blockchain", appContext.packageName)
	}
	
	@Test
	fun getTokenInfoFromTargetChainByContractAddress() {
		doAsync {
			System.out.println("what 1")
			val appContext = InstrumentationRegistry.getTargetContext()
			val resultValue = appContext.getIntFromSharedPreferences(SharesPreference.activityIsResult)
			if (resultValue == TinyNumber.True.value) {
				GoldStoneEthCall.getTokenInfoByContractAddress(
					"0xba420c4dbaa8f41a91a974e1ad213dc24e53e661",
					GoldStoneApp.getCurrentChain(),
					{ error, reason ->
						LogUtil.error(
							reason ?: "getTokenInfoFromTargetChainByContractAddress Test",
							error
						)
					},
					{ symbol, name, decimal ->
						LogUtil.debug(
							"getTokenInfoFromTargetChainByContractAddress",
							"$symbol, $name, $decimal"
						)
						
						System.out.println("symbol$symbol, $name, $decimal")
					}
				)
			} else {
				Thread.sleep(1000L)
				System.out.println("what 2")
				getTokenInfoFromTargetChainByContractAddress()
			}
		}
	}
}
