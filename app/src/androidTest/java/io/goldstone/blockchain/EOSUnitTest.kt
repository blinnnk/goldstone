package io.goldstone.blockchain

import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.crypto.DefaultPath
import io.goldstone.blockchain.crypto.eos.EOSWalletUtils
import io.goldstone.blockchain.module.home.home.view.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @date 2018/6/9 7:25 PM
 * @author KaySaith
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class EOSUnitTest {

	@Rule
	@JvmField
	val mActivityRule = ActivityTestRule(MainActivity::class.java)
	private val position = this.javaClass.simpleName

	@Test
	fun generateEOSPublicKey() {
		val mnemonic = "card eager cotton tag rally include order cheap soda october giggle easy"
		val path = DefaultPath.eosPath
		val keyPair = EOSWalletUtils.generateKeyPair(mnemonic, path)
		LogUtil.debug("$position generateEOSPublicKey", "$keyPair")
	}
}