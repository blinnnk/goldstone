package io.goldstone.blockchain.module.entrance.splash.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.blinnnk.extension.addFragment
import com.blinnnk.extension.hideStatusBar
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.component.SplashContainer
import io.goldstone.blockchain.module.entrance.starting.view.StartingFragment

/**
 * @date 21/03/2018 7:35 PM
 * @author KaySaith
 */


class SplashActivity : AppCompatActivity() {

  private val container by lazy { SplashContainer(this) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    hideStatusBar()

    container.apply {
      savedInstanceState.isNull {
        // 判断 `SaveInstanceState` 防止旋转屏幕重新创建 `Fragment`
        addFragment<StartingFragment>(container.id)
      }
    }.let {
      setContentView(it)
    }
  }
}