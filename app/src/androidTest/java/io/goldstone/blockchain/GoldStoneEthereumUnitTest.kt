@file:Suppress("DEPRECATION")

package io.goldstone.blockchain

import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import io.goldstone.blockchain.common.utils.LogUtil
import io.goldstone.blockchain.common.value.Config
import io.goldstone.blockchain.crypto.ethereum.EthereumMethod
import io.goldstone.blockchain.crypto.multichain.ChainType
import io.goldstone.blockchain.kernel.network.GoldStoneEthCall
import io.goldstone.blockchain.kernel.network.ParameterUtil
import io.goldstone.blockchain.module.home.home.view.MainActivity
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
		val chainName = Config.getCurrentChainName()
		GoldStoneEthCall.getTokenInfoByContractAddress(
			contract,
			{
				LogUtil.error("$position getTokenInfoByContract ", it)
			},
			chainName
		) { symbol, name, decimal ->
			val result = "symbol - $symbol name - $name decimal - $decimal"
			LogUtil.debug("$position getTokenInfoByContract)", result)
			assertTrue("Symbol is empty", symbol.isNotEmpty())
			assertTrue("Name is empty", name.isNotEmpty())
			assertTrue("Decimal is nan or wrong value", decimal != 0)
		}
	}

	@Test
	fun getTransactionByHash() {
		val transactionHash = "0xfc71d21397ed9e4b3765d2b1fd37c388481bf023bdde1a9edbbd9a732884e3aa"
		val chainName = Config.getCurrentChainName()
		GoldStoneEthCall.getTransactionByHash(
			transactionHash,
			chainName,
			errorCallback = { LogUtil.error(position + "getTransactionByHash", it) }
		) {
			// The ring result of blocknumber is 3396621
			LogUtil.debug(position + "getTransactionByHash", it.toString())
			assertTrue("Blocknumber is wrong", it.blockNumber.toIntOrNull() == 396958)
		}
	}

	@Test
	fun getReceiptByHash() {
		val transactionHash = "0XF3652ACBADF1EB216E21FF77B742B30786438D63E5EEF4FEB96F24C2CAF54715"
		val chainName = Config.getCurrentChainName()
		GoldStoneEthCall.getReceiptByHash(
			transactionHash,
			{ LogUtil.error(position + "getTransactionByHash", it) },
			chainName
		) {
			// Result is False means isFailed == false
			LogUtil.debug(position + "getTransactionByHash", "$it")
			assertTrue("Get Transaction status failed", !it)
		}
	}

	@Test
	fun getBlockTimeStampByBlockHash() {
		val blockHash = "0x67d97f9de7747023c4340be566db53f48e2bc9c0d953c14d65accb2abcc242db"
		val chainName = Config.getCurrentChainName()
		GoldStoneEthCall.getBlockTimeStampByBlockHash(
			blockHash,
			{ LogUtil.error(position + "getBlockTimeStampByBlockHash", it) },
			chainName
		) {
			// Result should be 1485158072
			LogUtil.debug(position + "getBlockTimeStampByBlockHash", "$it")
			assertTrue("Get Wrong Timestamp", it == BigInteger.valueOf(1485158072L))
		}
	}

	@Test
	fun getBlockNumber() {
		val chainName = Config.getCurrentChainName()
		GoldStoneEthCall.getBlockNumber(
			{ LogUtil.error(position + "getBlockNumber", it) },
			chainName
		) {
			// Get Current Block number, it will always different than
			// before but it must bigger than before
			LogUtil.debug(position + "getBlockNumber", "$it")
			assertTrue("Get Wrong Block Number Value", it > 3454242)
		}
	}

	@Test
	fun getTokenBalanceByContract() {
		val contract = "0xe728460d9FFceEB836BfD2Bbf083536A596eaF93" // GSC Ropstan
		val address = "0x2D6FAE3553F082B0419c483309450CaF6bC4573E"
		val chainName = Config.getCurrentChainName()
		GoldStoneEthCall.getTokenBalanceWithContract(
			contract,
			address,
			{ LogUtil.error(position + "getTokenBalanceByContract", it) },
			chainName
		) {
			// The balance in June 16/2018 is 1.882099E13
			LogUtil.debug(position + "getTokenBalanceByContract", "$it")
			assertTrue("Get wrong balance", it > BigInteger.ZERO)
		}
	}

	@Test
	fun getTokenSymbolByContract() {
		val contract = "0xe728460d9FFceEB836BfD2Bbf083536A596eaF93" // GSC Ropstan
		val chainName = Config.getCurrentChainName()
		GoldStoneEthCall.getTokenSymbolByContract(
			contract,
			{ LogUtil.error(position + "getTokenBalanceByContract", it) },
			chainName
		) {
			// Result should be GSC
			LogUtil.debug(position + "getTokenSymbolByContract", it)
			assertTrue("Get wrong Symbol", it == "GSC")
		}
	}

	@Test
	fun getTokenDecimal() {
		val contract = "0xe728460d9FFceEB836BfD2Bbf083536A596eaF93" // GSC Ropstan
		val chainName = Config.getCurrentChainName()
		GoldStoneEthCall.getTokenDecimal(
			contract,
			{ LogUtil.error(position + "getTokenDecimal", it) },
			chainName
		) {
			// Result should be 9.0
			LogUtil.debug(position + "getTokenDecimal", it.toString())
			assertTrue("Get wrong decimal", it == 9)
		}
	}

	@Test
	fun getTokenName() {
		val contract = "0xe728460d9FFceEB836BfD2Bbf083536A596eaF93" // GSC Ropstan
		val chainName = Config.getCurrentChainName()
		GoldStoneEthCall.getTokenName(
			contract,
			{ LogUtil.error(position + "getTokenName", it) },
			chainName
		) {
			// Result should be GoldstoneCoin
			LogUtil.debug(position + "getTokenName", it)
			assertTrue("Get wrong getTokenName", it.equals("GoldstoneCoin", true))
		}
	}

	@Test
	fun getEthBalance() {
		val contract = "0x2D6FAE3553F082B0419c483309450CaF6bC4573E"
		val chainName = Config.getCurrentChainName()
		GoldStoneEthCall.getEthBalance(
			contract,
			{ LogUtil.error(position + "getEthBalance", it) },
			chainName
		) {
			// Result should be greater than 0
			LogUtil.debug(position + "getEthBalance", it.toString())
			assertTrue("Get wrong eth balance", it > BigInteger.ZERO)
		}
	}

	@Test
	fun getTokenTotalSupply() {
		val contract = "0xe728460d9FFceEB836BfD2Bbf083536A596eaF93" // GSC In Ropsten
		val chainName = Config.getCurrentChainName()
		GoldStoneEthCall.getTokenTotalSupply(
			contract,
			{ LogUtil.error(position + "getTokenTotalSupply", it) },
			chainName
		) {
			// Result should be 1000000000000000000
			LogUtil.debug(position + "getTokenTotalSupply", it.toString())
			assertTrue(
				"Get wrong total supply",
				it.toBigDecimal().toBigInteger() == BigInteger.valueOf(1000000000000000000)
			)
		}
	}

	@Test
	fun getTransactionExecutedValue() {
		val from = "0x2D6FAE3553F082B0419c483309450CaF6bC4573E"
		val to = "0x6E3DF901A984d50b68355eeDE503cBfC1eAd8F13"
		val data = "0x"
		val chainName = Config.getCurrentChainName()
		GoldStoneEthCall.getTransactionExecutedValue(
			to,
			from,
			data,
			{ LogUtil.error(position + "getTransactionExecutedValue", it) },
			chainName
		) {
			// Result should be 21000
			LogUtil.debug(position + "getTransactionExecutedValue", it.toString())
			assertTrue("Get wrong Executed value", it == BigInteger.valueOf(21000))
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
			LogUtil.debug(position + "prepareJsonRPCParam", it)
		}
	}

	@Test
	fun getInputCodeByTaxHash() {
		val hash = "0x6fe5d4a28755b260d01654b0e5b7f74d8ca236bd69229bf57a712b480d39f2b6"
		val chainName = Config.getCurrentChainName()
		GoldStoneEthCall.getInputCodeByHash(
			hash,
			{ LogUtil.error(position, it) },
			chainName
		) {
			val expectedValue =
				"0xa9059cbb000000000000000000000000ca6655dc28c4ecea148033bf6fac60b1398482e000000000000000000000000000000000000000000000000000000000002c252c4e69636520746f206d65657420796f752062616279"
			LogUtil.debug("$position + getInputCodeByTaxHash", it)
			assertTrue("Got Wrong Input Code", it.equals(expectedValue, true))
		}
	}

	@Test
	fun getUsableNonceByAddress() {
		val address = "0x2D6FAE3553F082B0419c483309450CaF6bC4573E"
		GoldStoneEthCall.getUsableNonce(
			{ LogUtil.error("getUsableNonceByAddress", it) },
			ChainType.ETH,
			address
		) {
			LogUtil.debug("getUsableNonceByAddress", "$it")
		}
	}
}