package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.blinnnk.extension.*
import com.blinnnk.uikit.HoneyColor
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.component.GradientType
import io.goldstone.blockchain.common.component.GradientView
import io.goldstone.blockchain.common.component.RoundIcon
import io.goldstone.blockchain.common.component.TwoLineTitles
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

  private val info = TwoLineTitles(context)
  private val gradientView = GradientView(context)
  private val icon = RoundIcon(context)
  private var pendingIcon: ProgressBar? = null

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
          setColorFilter(GrayScale.Opacity2Black)
          setMargins<LinearLayout.LayoutParams> { topMargin = 20.uiPX() }
          elevation = 15.uiPX().toFloat()
        }
        .into(this)

      info
        .apply {
          layoutParams = LinearLayout.LayoutParams(matchParent, 60.uiPX())
          isCenter = true
          setMargins<LinearLayout.LayoutParams> { topMargin = 20.uiPX() }
          setWildStyle()
        }
        .into(this)
    }
  }

  @SuppressLint("SetTextI18n")
  fun setIconStyle(count: Double, targetAddress: String, isReceive: Boolean, isPending: Boolean) {
    val type = if(isReceive) "Received " else "Send "
    info.title.text = "$type$count ETH ${if(isReceive) "From" else "To"}"
    info.subtitle.text = targetAddress

    if (isPending) {
      icon.iconColor = Spectrum.lightRed
      showPendingIcon()
    } else {
      showPendingIcon(false)
      if(!isReceive) {
        icon.iconColor = Spectrum.yellow
        icon.src = R.drawable.send_icon
        icon.setColorFilter(GrayScale.Opacity5Black)
      } else {
        icon.iconColor = Spectrum.green
        icon.src = R.drawable.receive_icon
      }
    }
  }

  private fun showPendingIcon(status: Boolean = true) {
    pendingIcon.isNotNull {
      if (!status) removeView(pendingIcon)
    } otherwise {
      if (status) {
        pendingIcon = ProgressBar(this.context, null, android.R.attr.progressBarStyleInverse).apply {
          indeterminateDrawable.setColorFilter(HoneyColor.HoneyWhite, android.graphics.PorterDuff.Mode.MULTIPLY)
          RelativeLayout.LayoutParams(32.uiPX(), 32.uiPX())
          y += 46.uiPX()
        }
        addView(pendingIcon)
        pendingIcon?.setCenterInHorizontal()
      }
    }
  }

}