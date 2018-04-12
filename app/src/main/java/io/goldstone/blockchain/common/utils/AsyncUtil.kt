package io.goldstone.blockchain.common.utils

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

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