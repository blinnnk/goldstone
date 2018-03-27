package io.goldstone.blockchain.module.common.tokendetail.tokendetail.view

import android.content.Context
import android.graphics.Color
import android.widget.RelativeLayout
import com.blinnnk.uikit.uiPX
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.matchParent

/**
 * @date 27/03/2018 3:20 PM
 * @author KaySaith
 */

class TokenDetailHeaderView(context: Context) : RelativeLayout(context) {

  init {
    layoutParams = RelativeLayout.LayoutParams(matchParent, 100.uiPX())
    backgroundColor = Color.RED
  }
}