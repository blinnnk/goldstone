package io.goldstone.blockchain

import io.goldstone.blockchain.common.utils.LogUtil
import org.junit.Test

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
		LogUtil.debug("mergeListAndDistinct", "${list1.asSequence().plus(list2).distinct().toList()}")
	}
}
