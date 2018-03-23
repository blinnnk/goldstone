package io.goldstone.blockchain.module.home.home.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.blinnnk.extension.addFragment
import io.goldstone.blockchain.common.utils.hideStatusBar
import io.goldstone.blockchain.common.value.ContainerID
import org.jetbrains.anko.relativeLayout

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    hideStatusBar()

    relativeLayout {
      id = ContainerID.main
      addFragment<HomeFragment>(this.id)
    }.let {
      setContentView(it)
    }
  }
}
