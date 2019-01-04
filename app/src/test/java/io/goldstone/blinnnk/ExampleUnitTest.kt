package io.goldstone.blinnnk

import android.util.Log
import io.goldstone.blinnnk.crypto.bitcoin.BTCUtils
import io.goldstone.blinnnk.crypto.eos.EOSUtils
import io.goldstone.blinnnk.crypto.eos.accountregister.EOSActor
import io.goldstone.blinnnk.crypto.eos.netcpumodel.BandWidthModel
import io.goldstone.blinnnk.crypto.eos.transaction.EOSAuthorization
import io.goldstone.blinnnk.crypto.multichain.CryptoValue
import io.goldstone.blinnnk.module.common.tokendetail.eosresourcetrading.common.basetradingfragment.view.StakeType
import org.junit.Assert
import org.junit.Test
import java.math.BigDecimal
import java.math.BigInteger

@Suppress("DEPRECATION")
/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

	@Test
	fun mergeListAndDistinct() {
		val list1 = listOf(1, 2, 3, 4, 5, 6)
		val list2 = listOf(10, 20, 30, 4, 5, 6)
		Log.d("mergeListAndDistinct", "${list1.asSequence().plus(list2).distinct().toList()}")
	}

	@Test
	fun generateObject() {
		val expectResult = "{\"account\":\"eosio\",\"name\":\"delegatebw\",\"authorization\":[{\"actor\":\"hello\",\"permission\":\"active\"}],\"data\":{\"from\":\"hello\",\"receiver\":\"love\",\"stake_net_quantity\":\"1.0000 EOS\",\"stake_cpu_quantity\":\"1.0000 EOS\"},\"hex_data\":\"\"}"
		// Test Everything
		BandWidthModel(
			listOf(EOSAuthorization("hello", EOSActor.Active)),
			"hello",
			"love",
			BigInteger.valueOf(10000L),
			BigInteger.valueOf(10000L),
			StakeType.Refund,
			true
		).let {
			Assert.assertTrue("get wrong band width object", it.createObject() == expectResult)
		}
	}

	@Test
	fun compare() {
		val list1 = listOf(Pair(1, 2), Pair(1, 3))
		val list2 = listOf(Pair(1, 2), Pair(1, 1))
		Log.d("compare", list1.containsAll(list2).toString())
		Log.d("compare", (list1.asSequence().plus(list2).distinct().toList() - list1).toString())
	}

	@Test
	fun bigNumber() {
		println(BigDecimal.valueOf(10).pow(18).add(BigDecimal.ZERO))
	}

	@Test
	fun validBTCAddressLength() {
		println(CryptoValue.isBitcoinAddressLength(" mgqkf2Y6YaiLzQckRWuvcbGXUh2ys2dbbN"))
		println(BTCUtils.isValidTestnetAddress("mgqkf2Y6YaiLzQckRWuvcbGXUh2ys2dbbN"))
	}

	@Test
	fun stringArrayConverter() {
		// expect result hello||hey||
		println(listOf("hello", "hey").map { "$it||" }.joinToString("") { it })
	}

	@Test
	fun utils() {
		println(EOSUtils.getLittleEndianCode("a04965497f1661313738303364393066353135633133"))
		println(EOSUtils.getVariableUInt(1))
		println(EOSUtils.convertAmountToCode(BigInteger.valueOf(34577769)))
		println(EOSUtils.convertAmountToCode(BigInteger.valueOf(790)))
	}

	@Test
	fun sortString() {
		val addresses = listOf(
			"EOS69UvbnXLnE3Kmzv7VkPbXnD1FQZjcv9DAxrASAXCPY1PYN2RZu",
			"EOS6ndAqVB4bWcU742CcPtbnie4VC32XtKx8WTSH4bBNtHTAUtC5V",
			"EOS7L2Eo6hx8TA3ZMe6YmaYLmN6eHkMoaivtnEoVGcBzsqBgbxSN3",
			"EOS6NdAqVB4bWcU742CcPtbnie4VC32XtKx8WTSH4bBNtHTAUtC5V"
		)
		println(addresses.sorted())
	}
}



