@file:Suppress("DEPRECATION")

package io.goldstone.blockchain

import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.ChainID
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.module.home.home.view.MainActivity
import junit.framework.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @date 2018/6/9 7:31 PM
 * @author KaySaith
 */
@Suppress("DEPRECATION")
@RunWith(AndroidJUnit4::class)
@LargeTest
class GoldStoneEthereumUnitTest {
	
	@Rule
	@JvmField
	val mActivityRule = ActivityTestRule(MainActivity::class.java)
	private val positon = this.javaClass.simpleName
	
	@Test
	fun getTokenInfoByContract() {
		val contract = "0xe728460d9FFceEB836BfD2Bbf083536A596eaF93" // symbol = GSC
		val chainID = ChainID.Ropstan.id
		GoldStoneEthCall.getTokenInfoByContractAddress(
			contract,
			chainID,
			{ error, reason ->
				LogUtil.error("$positon getTokenInfoByContract $reason", error)
			}
		) { symbol, name, decimal ->
			val result = "symbol - $symbol name - $name decimal - $decimal"
			LogUtil.debug("$positon getTokenInfoByContract)", result)
			assertTrue("Symbol is empty", symbol.isNotEmpty())
			assertTrue("Name is empty", name.isNotEmpty())
			assertTrue("Decimal is nan or wrong value", decimal != 0.0)
		}
	}
	
	@Test
	fun getTransactionByHash() {
		val transactionHash = "0XF3652ACBADF1EB216E21FF77B742B30786438D63E5EEF4FEB96F24C2CAF54715"
		val chainID = ChainID.Ropstan.id
		GoldStoneEthCall.getTransactionByHash(
			transactionHash, chainID, {
			// Unfinished
		}, { error, reason ->
				LogUtil.error(
					positon + "getTransactionByHash" + reason,
					error
				)
			}
		) {
			// The ring result of blocknumber is 3396621
			LogUtil.debug(positon + "getTransactionByHash", it.toString())
			assertTrue("Blocknumber is null or empty", it.blockNumber.isBlank())
		}
	}
	
	@Test
	fun getEthBalance() {
		val address = "0x2D6FAE3553F082B0419c483309450CaF6bC4573E"
		val chainID = ChainID.Ropstan.id
		GoldStoneEthCall.getEthBalance(
			address,
			{ error, reason ->
				LogUtil.error(positon + reason, error)
			}, chainID
		) {
			LogUtil.debug("$positon + getEthBalance", it.toString())
		}
	}
	
	@Test
	fun getInputCodeByTaxHash() {
		val hash = "0x3dda45630cab85080320ee791a540f2f6ef2e9ad7e845831126e03f193088965"
		GoldStoneEthCall.getInputCodeByHash(
			hash,
			{ error, reason ->
				LogUtil.error(positon + reason, error)
			}
		) {
			LogUtil.debug("$positon + getInputCodeByTaxHash", it)
		}
	}
}