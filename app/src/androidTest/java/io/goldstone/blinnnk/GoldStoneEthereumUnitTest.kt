@file:Suppress("DEPRECATION")

package io.goldstone.blinnnk

import android.support.test.filters.LargeTest
import android.support.test.internal.util.LogUtil
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.blinnnk.extension.orZero
import io.goldstone.blinnnk.common.sharedpreference.SharedChain
import io.goldstone.blinnnk.crypto.ethereum.EthereumMethod
import io.goldstone.blinnnk.kernel.network.ParameterUtil
import io.goldstone.blinnnk.kernel.network.ethereum.ETHJsonRPC
import io.goldstone.blinnnk.module.home.home.view.MainActivity
import junit.framework.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigInteger

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
	private val position = this.javaClass.simpleName

	@Test
	fun getTokenInfoByContract() {
		val contract = "0xe728460d9FFceEB836BfD2Bbf083536A596eaF93" // symbol = GSC
		val chainName = SharedChain.getCurrentETH()
		ETHJsonRPC.getTokenInfoByContractAddress(contract, chainName) { symbol, name, decimal, _ ->
			val result = "symbol - $symbol name - $name decimal - $decimal"
			LogUtil.logDebug("$position getTokenInfoByContract)", result)
			assertTrue("Symbol is empty", symbol?.isNotEmpty() == true)
			assertTrue("Name is empty", name?.isNotEmpty() == true)
			assertTrue("Decimal is nan or wrong value", decimal != 0)
		}
	}

	@Test
	fun getTransactionByHash() {
		val transactionHash = "0xfc71d21397ed9e4b3765d2b1fd37c388481bf023bdde1a9edbbd9a732884e3aa"
		val chainName = SharedChain.getCurrentETH()
		ETHJsonRPC.getTransactionByHash(transactionHash, chainName) { transaction, _ ->
			// The ring result of blocknumber is 3396621
			LogUtil.logDebug(position + "getTransactionByHash", transaction.toString())
			assertTrue("Blocknumber is wrong", transaction?.blockNumber == 396958)
		}
	}

	@Test
	fun getReceiptByHash() {
		val transactionHash = "0XF3652ACBADF1EB216E21FF77B742B30786438D63E5EEF4FEB96F24C2CAF54715"
		val chainName = SharedChain.getCurrentETH()
		ETHJsonRPC.getReceiptByHash(transactionHash, chainName) { success, _ ->
			// Result is False means isFailed == false
			LogUtil.logDebug(position + "getTransactionByHash", "$success")
			assertTrue("Get Transaction status failed", success == false)
		}
	}

	@Test
	fun getBlockTimeStampByBlockHash() {
		val blockHash = "0x67d97f9de7747023c4340be566db53f48e2bc9c0d953c14d65accb2abcc242db"
		val chainName = SharedChain.getCurrentETH()
		ETHJsonRPC.getBlockTimeStampByBlockHash(blockHash, chainName) { time, _ ->
			// Result should be 1485158072
			LogUtil.logDebug(position + "getBlockTimeStampByBlockHash", "$time")
			assertTrue("Get Wrong Timestamp", time == BigInteger.valueOf(1485158072L))
		}
	}

	@Test
	fun getBlockNumber() {
		val chainName = SharedChain.getCurrentETH()
		ETHJsonRPC.getBlockCount(chainName) { count, _ ->
			// Get Current Block number, it will always different than
			// before but it must bigger than before
			LogUtil.logDebug(position + "getBlockNumber", "$count")
			assertTrue("Get Wrong Block Number Value", count.orZero() > 3454242)
		}
	}

	@Test
	fun getTokenBalanceByContract() {
		val contract = "0xe728460d9FFceEB836BfD2Bbf083536A596eaF93" // GSC Ropstan
		val address = "0x2D6FAE3553F082B0419c483309450CaF6bC4573E"
		val chainName = SharedChain.getCurrentETH()
		ETHJsonRPC.getTokenBalanceWithContract(
			contract,
			address,
			chainName
		) { balance, _ ->
			// The balance in June 16/2018 is 1.882099E13
			LogUtil.logDebug(position + "getTokenBalanceByContract", "$balance")
			assertTrue("Get wrong balance", balance.orZero() > BigInteger.ZERO)
		}
	}

	@Test
	fun getTokenSymbolByContract() {
		val contract = "0xe728460d9FFceEB836BfD2Bbf083536A596eaF93" // GSC Ropstan
		val chainName = SharedChain.getCurrentETH()
		ETHJsonRPC.getTokenSymbolByContract(
			contract,
			chainName
		) { symbol, _ ->
			// Result should be GSC
			LogUtil.logDebug(position + "getTokenSymbolByContract", symbol.orEmpty())
			assertTrue("Get wrong Symbol", symbol == "GSC")
		}
	}

	@Test
	fun getTokenDecimal() {
		val contract = "0xe728460d9FFceEB836BfD2Bbf083536A596eaF93" // GSC Ropstan
		val chainName = SharedChain.getCurrentETH()
		ETHJsonRPC.getTokenDecimal(
			contract,
			chainName
		) { decimal, _ ->
			// Result should be 9.0
			LogUtil.logDebug(position + "getTokenDecimal", decimal.toString())
			assertTrue("Get wrong decimal", decimal == 9)
		}
	}

	@Test
	fun getTokenName() {
		val contract = "0xe728460d9FFceEB836BfD2Bbf083536A596eaF93" // GSC Ropstan
		val chainName = SharedChain.getCurrentETH()
		ETHJsonRPC.getTokenName(
			contract,
			chainName
		) { name, _ ->
			// Result should be GoldstoneCoin
			LogUtil.logDebug(position + "getTokenName", name.orEmpty())
			assertTrue("Get wrong getTokenName", name.equals("GoldstoneCoin", true))
		}
	}

	@Test
	fun getEthBalance() {
		val contract = "0x2D6FAE3553F082B0419c483309450CaF6bC4573E"
		val chainName = SharedChain.getCurrentETH()
		ETHJsonRPC.getEthBalance(contract, chainName) { amount, _ ->
			// Result should be greater than 0
			LogUtil.logDebug(position + "getEthBalance", amount.toString())
			assertTrue("Get wrong eth balance", amount.orZero() > BigInteger.ZERO)
		}
	}

	@Test
	fun getTokenTotalSupply() {
		val contract = "0xe728460d9FFceEB836BfD2Bbf083536A596eaF93" // GSC In Ropsten
		val chainName = SharedChain.getCurrentETH()
		ETHJsonRPC.getTokenTotalSupply(contract, chainName) { supply, _ ->
			// Result should be 1000000000000000000
			LogUtil.logDebug(position + "getTokenTotalSupply", supply.toString())
			assertTrue(
				"Get wrong total supply",
				supply?.toBigDecimal()?.toBigInteger() == BigInteger.valueOf(1000000000000000000)
			)
		}
	}

	@Test
	fun getTransactionExecutedValue() {
		val from = "0x2D6FAE3553F082B0419c483309450CaF6bC4573E"
		val to = "0x6E3DF901A984d50b68355eeDE503cBfC1eAd8F13"
		val data = "0x"
		val chainName = SharedChain.getCurrentETH()
		ETHJsonRPC.getTransactionExecutedValue(
			to,
			from,
			data,
			chainName
		) { executed, _ ->
			// Result should be 21000
			LogUtil.logDebug(position + "getTransactionExecutedValue", executed.toString())
			assertTrue("Get wrong Executed value", executed == BigInteger.valueOf(21000))
		}
	}

	@Test
	fun prepareJsonRPCParam() {
		ParameterUtil.prepareJsonRPC(
			true,
			EthereumMethod.GetBlockNumber.method,
			1,
			false,
			true,
			"hello"
		).let {
			LogUtil.logDebug(position + "prepareJsonRPCParam", it)
		}
	}

	@Test
	fun getInputCodeByTaxHash() {
		val hash = "0x6fe5d4a28755b260d01654b0e5b7f74d8ca236bd69229bf57a712b480d39f2b6"
		val chainName = SharedChain.getCurrentETH()
		ETHJsonRPC.getInputCodeByHash(hash, chainName) { input, _ ->
			val expectedValue =
				"0xa9059cbb000000000000000000000000ca6655dc28c4ecea148033bf6fac60b1398482e000000000000000000000000000000000000000000000000000000000002c252c4e69636520746f206d65657420796f752062616279"
			LogUtil.logDebug("$position + getInputCodeByTaxHash", input.orEmpty())
			assertTrue("Got Wrong Input Code", input.equals(expectedValue, true))
		}
	}

	@Test
	fun getUsableNonceByAddress() {
		val address = "0x2D6FAE3553F082B0419c483309450CaF6bC4573E"
		ETHJsonRPC.getUsableNonce(SharedChain.getCurrentETH(), address) { nonce, _ ->
			LogUtil.logDebug("getUsableNonceByAddress", "$nonce")
		}
	}
}