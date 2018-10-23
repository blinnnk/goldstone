package io.goldstone.blockchain.common.utils

import com.blinnnk.util.observing
import io.goldstone.blockchain.kernel.network.common.GoldStoneAPI
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
	}

	private suspend fun first() {
		launch(CommonPool) {
			firstJob()
		}.join()
	}

	abstract fun firstJob()

	private suspend fun second() {
		launch(CommonPool) {
			secondJob()
		}.join()
	}

	abstract fun secondJob()

	private suspend fun third() {
		launch(CommonPool) {
			thirdJob()
		}.join()
	}

	abstract fun thirdJob()
}

abstract class ConcurrentAsyncCombine {

	abstract var asyncCount: Int
	private var finishedCount: Int by observing(0) {
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
		if (asyncCount == 0) {
			if (getResultInMainThread()) GoldStoneAPI.context.runOnUiThread { mergeCallBack() }
			else doAsync { mergeCallBack() }
		}
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