package io.goldstone.blockchain

import android.app.Application
import io.goldstone.blockchain.common.value.setLanguage

/**
 * @date 22/03/2018 3:02 PM
 * @author KaySaith
 */

class GoldStoneApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    // 根据系统语言国际化
    setLanguage()

  }
}