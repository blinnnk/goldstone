package io.goldstone.blockchain.module.home.home

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import org.jetbrains.anko.relativeLayout

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    relativeLayout {

    }.let {
      setContentView(it)
    }
  }
}
