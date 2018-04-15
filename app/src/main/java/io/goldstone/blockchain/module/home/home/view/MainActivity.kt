package io.goldstone.blockchain.module.home.home.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.RelativeLayout
import com.blinnnk.extension.addFragment
import com.blinnnk.extension.hideStatusBar
import com.blinnnk.extension.isNull
import io.goldstone.blockchain.common.component.LoadingView
import io.goldstone.blockchain.common.value.ContainerID
import io.goldstone.blockchain.common.value.FragmentTag
import org.jetbrains.anko.relativeLayout

class MainActivity : AppCompatActivity() {

  private val loadingView by lazy { LoadingView(this) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    hideStatusBar()

    relativeLayout {
      id = ContainerID.main
      savedInstanceState.isNull {
        // 判断 `SaveInstanceState` 防止旋转屏幕重新创建 `Fragment`
        addFragment<HomeFragment>(this.id, FragmentTag.home)
      }
    }.let {
      setContentView(it)
    }
  }

  fun showLoadingView() {
    findViewById<RelativeLayout>(ContainerID.main)?.apply {
      addView(loadingView)
    }
  }

  fun removeLoadingView() {
    findViewById<RelativeLayout>(ContainerID.main)?.apply {
      removeView(loadingView)
    }
  }

}
