package io.goldstone.blockchain.common.component.container

import android.content.Context
import android.widget.RelativeLayout
import io.goldstone.blockchain.common.value.ContainerID

/**
 * @date 22/03/2018 12:54 AM
 * @author KaySaith
 */

@Suppress("DEPRECATION")

class SplashContainer(context: Context) : RelativeLayout(context) {

  init {
    id = ContainerID.splash
  }

}