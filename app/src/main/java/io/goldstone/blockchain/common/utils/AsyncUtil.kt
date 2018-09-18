package io.goldstone.blockchain.common.utils

import com.blinnnk.util.observing
import io.goldstone.blockchain.kernel.network.GoldStoneAPI
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

/**
 * @date 11/04/2018 3:28 AM
 * @author KaySaith
 */
abstract class SequentialTask {

	fun start() = runBlocking {
		first()
		second()
		third()
		println("start sequential task")
	}

	private suspend fun first() {
		launch(CommonPool) {
			firstJob()
			println("start job1")
		}.join()
	}

	abstract fun firstJob()

	private suspend fun second() {
		launch(CommonPool) {
			secondJob()
			println("start job2")
		}.join()
	}

	abstract fun secondJob()

	private suspend fun third() {
		launch(CommonPool) {
			thirdJob()
			println("start job3")
		}.join()
	}

	abstract fun thirdJob()
}

abstract class ConcurrentAsyncCombine {

	abstract var asyncCount: Int
	private var finishedCount: Int by observing(0) {
		System.out.println("async$asyncCount and $finishedCount")
		if (finishedCount == asyncCount) {
			if (getResultInMainThread()) {
				GoldStoneAPI.context.runOnUiThread { mergeCallBack() }
			} else {
				doAsync { mergeCallBack() }
			}
		}
	}

	abstract fun concurrentJobs()

	open fun completeMark() {
		finishedCount += 1
	}

	open fun getResultInMainThread(): Boolean = true

	fun start() {
		doAsync { concurrentJobs() }
	}

	abstract fun mergeCallBack()
}

/**
 * 封装的配套协程工具
 */
fun <T> load(doThings: () -> T): Deferred<T> {
	return async(CommonPool, CoroutineStart.LAZY) { doThings() }
}

infix fun <T> Deferred<T>.then(block: (T) -> Unit): Job {
	return launch(UI) { block(await()) }
}