package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.GradientView
import io.goldstone.blockchain.common.component.RoundIcon
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.value.GradientType
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.TransactionSize
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.verticalLayout

/**
 * @date 27/03/2018 3:33 AM
 * @author KaySaith
 */

class TransactionDetailHeaderView(context: Context) : RelativeLayout(context) {

  private val gradientView = GradientView(context)
  private val icon = RoundIcon(context)
  private val info = TwoLineTitles(context)

  init {

    gradientView
      .apply {
        setStyle(GradientType.DarkGreen, TransactionSize.headerView)
        layoutParams = RelativeLayout.LayoutParams(matchParent, TransactionSize.headerView)
      }
      .into(this)

    verticalLayout {

      layoutParams = RelativeLayout.LayoutParams((ScreenSize.Width * 0.6).toInt(), 130.uiPX()).apply {
        leftMargin = (ScreenSize.Width * 0.2).toInt()
        addRule(CENTER_VERTICAL)
      }

      gravity = Gravity.CENTER_HORIZONTAL

      icon
        .apply {
          iconColor = Spectrum.yellow
          src = R.drawable.receive_icon
          setColorFilter(GrayScale.Opacity2Black)
          setMargins<LinearLayout.LayoutParams> { topMargin = 20.uiPX() }
          elevation = 15.uiPX().toFloat()
        }
        .into(this)

      info
        .apply {
          title.text = "Received 3 ETH From"
          subtitle.text = "0x9s889d2u7e657s6d78s7d65f7s65d57fs7s87d68f86s76d8f76s86d86f75d6s64f672"
          layoutParams = LinearLayout.LayoutParams(matchParent, 60.uiPX())
          isCenter = true
          setMargins<LinearLayout.LayoutParams> { topMargin = 20.uiPX() }
          setWildStyle()
        }
        .into(this)
    }
  }

}