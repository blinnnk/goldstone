package io.goldstone.blockchain.common.utils

import com.blinnnk.util.observing

/**
 * @date 15/04/2018 3:49 AM
 * @author KaySaith
 */

abstract class MultipleAsyncCombine {

  abstract var asyncCount: Int

  private var finishedCount: Int by observing(0) {
    if (finishedCount == asyncCount) {
      mergeCallBack()
    }
  }

  abstract fun concurrentJobs()

  open fun completeMark() {
    finishedCount += 1
  }

  fun start() { concurrentJobs() }

  abstract fun mergeCallBack()

}