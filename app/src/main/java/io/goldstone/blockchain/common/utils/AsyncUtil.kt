package io.goldstone.blockchain.common.utils

import com.blinnnk.util.observing
import kotlinx.coroutines.*

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
		GlobalScope.launch(Dispatchers.Default) {
			firstJob()
		}.join()
	}

	abstract fun firstJob()

	private suspend fun second() {
		GlobalScope.launch(Dispatchers.Default) {
			secondJob()
		}.join()
	}

	abstract fun secondJob()

	private suspend fun third() {
		GlobalScope.launch(Dispatchers.Default) {
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
			GlobalScope.launch(Dispatchers.Default) {
				if (completeInUIThread) withContext(Dispatchers.Main) {
					mergeCallBack()
				} else mergeCallBack()
			}
		}
	}

	abstract fun doChildTask(index: Int)

	open fun completeMark() {
		finishedCount += 1
	}

	fun start() {
		GlobalScope.launch(Dispatchers.Default) {
			if (asyncCount == 0) {
				if (completeInUIThread) withContext(Dispatchers.Main) {
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

	open fun mergeCallBack() {}
}

/**
 * 封装的配套协程工具
 */
fun <T> load(doThings: () -> T): Deferred<T> {
	return GlobalScope.async(Dispatchers.Default) { doThings() }
}

infix fun <T> Deferred<T>.then(block: (T) -> Unit): Job {
	return GlobalScope.launch(Dispatchers.Main) { block(await()) }
}

