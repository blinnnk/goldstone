package io.goldstone.blockchain.module.home.home.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.blinnnk.extension.addFragment
import com.blinnnk.extension.hideStatusBar
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.value.ContainerID
import org.jetbrains.anko.relativeLayout

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    hideStatusBar()

    relativeLayout {
      id = ContainerID.main
      savedInstanceState.isNull {
        // 判断 `SaveInstanceState` 防止旋转屏幕重新创建 `Fragment`
        addFragment<HomeFragment>(this.id)
      }
    }.let {
      setContentView(it)
    }
  }
}
