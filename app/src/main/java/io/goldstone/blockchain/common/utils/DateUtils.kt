package io.goldstone.blockchain.common.utils

/**
 * @date 08/04/2018 2:23 AM
 * @author KaySaith
 */

object DateUtils {

  fun getSinceTime(lastTime: String): String {
    // 距离当前已过的时间，单位秒
    val timePass = (System.currentTimeMillis() - lastTime.toDouble() * 1000) / 1000
    return when {
      timePass > 30 * 7 * 24 * 60 * 60 -> {
        (Math.floor(timePass / 30 / 7 / 24 / 60 / 60).toInt()).toString() + " months ago"
      }

      timePass > 7 * 24 * 60 * 60 -> {
        (Math.floor(timePass / 7 / 24 / 60 / 60).toInt()).toString() + " weeks ago"
      }

      timePass > 24 * 60 * 60 -> {
        (Math.floor(timePass / 24 / 60 / 60).toInt()).toString() + " days ago"
      }

      timePass > 60 * 60 -> {
        (Math.floor(timePass / 60 / 60).toInt()).toString() + " hours ago"
      }

      timePass > 60 -> {
        (Math.floor(timePass / 60).toInt()).toString() + " minutes ago"
      }

      else -> {
        timePass.toInt().toString() + " seconds ago"
      }
    }
  }
}