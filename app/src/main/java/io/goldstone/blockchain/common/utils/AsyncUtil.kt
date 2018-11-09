package io.goldstone.blockchain.common.utils

import com.blinnnk.util.observing
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI

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

// 使用协程和观察者模式实现封装的并发线程方法
abstract class ConcurrentAsyncCombine {

	abstract var asyncCount: Int
	open val completeInUIThread: Boolean = true
	// 当并发线程非常多的时候可以设置 DelayTime 减缓线程压力
	open val delayTime: Long? = null
	private var finishedCount: Int by observing(0) {
		if (finishedCount == asyncCount) {
			launch {
				if (completeInUIThread) withContext(UI) {
					mergeCallBack()
				} else withContext(CommonPool, CoroutineStart.LAZY) {
					mergeCallBack()
				}
			}
		}
	}

	abstract fun doChildTask(index: Int)

	open fun completeMark() {
		finishedCount += 1
	}

	fun start() {
		launch {
			withContext(CommonPool, CoroutineStart.LAZY) {
				if (asyncCount == 0) {
					if (completeInUIThread) withContext(UI) {
						mergeCallBack()
					} else mergeCallBack()
				} else {
					for (index in 0 until asyncCount) {
						delayTime?.let { delay(it) }
						doChildTask(index)
					}
				}
			}
		}
	}

	open fun mergeCallBack() {}
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
