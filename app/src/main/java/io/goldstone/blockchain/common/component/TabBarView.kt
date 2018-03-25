package io.goldstone.blockchain.common.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInHorizontal
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.HoneySvgPathConvert
import com.blinnnk.util.observing
import com.github.mmin18.widget.RealtimeBlurView
import io.goldstone.blockchain.common.utils.*
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.SvgPath
import org.jetbrains.anko.matchParent


/**
 * @date 23/03/2018 12:55 PM
 * @author KaySaith
 */

class TabBarView(context: Context) : RelativeLayout(context) {

  val walletButton by lazy { TabItem(context) }
  val marketButton by lazy { TabItem(context) }
  val profileButton by lazy { TabItem(context) }

  init {
    layoutParams = LinearLayout.LayoutParams(matchParent, 80.uiPX())

    // 添加动态玻璃蒙版
    RealtimeBlurView(context, null)
      .apply {
        setBlurRadius(20.uiPX().toFloat())
        setOverlayColor(Spectrum.opacity3White)
        layoutParams = LinearLayout.LayoutParams(matchParent, 80.uiPX())
      }
      .into(this)

    walletButton
      .apply {
        text = "wallet"
        type = TabItemType.Wallet
        setMargins<LinearLayout.LayoutParams> {
          leftMargin = PaddingSize.device
        }
      }
      .into(this)

    marketButton
      .apply {
        text = "markets"
        type = TabItemType.Market
        setMargins<LinearLayout.LayoutParams> {
          leftMargin = PaddingSize.device
        }
      }
      .into(this)

    profileButton
      .apply {
        text = "profile"
        type = TabItemType.Profile
        setMargins<LinearLayout.LayoutParams> {
          rightMargin = PaddingSize.device
        }
      }
      .into(this)

    // 修改位置
    marketButton.setCenterInHorizontal()
    profileButton.setAlignParentRight()

    // 默认选中
    walletButton.setSelectedStyle()

  }

}

enum class TabItemType {
  Market, Wallet, Profile
}

class TabItem(context: Context) : View(context) {

  var text by observing("") {
    invalidate()
  }

  var type by observing(TabItemType.Market) {
    invalidate()
  }

  private val iconPaint = Paint().apply {
    isAntiAlias = true
    style = Paint.Style.FILL
    color = Spectrum.white
  }

  private val iconSize = 80.uiPX()

  private val textPaint = Paint().apply {
    isAntiAlias = true
    style = Paint.Style.FILL
    color = Spectrum.white
    textSize = 12.uiPX().toFloat()
    typeface = GoldStoneFont.light(context)
  }

  private val path = HoneySvgPathConvert()

  init {
    layoutParams = LinearLayout.LayoutParams(iconSize, iconSize)
  }

  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)

    // `Wallet` 的 `Path` 尺寸不同这里的位置也相对调整
    val iconLeft =
      if (type == TabItemType.Wallet) (width - 30.uiPX()) / 2f
      else (width - 34.uiPX()) / 2f
    val textX =
      if (type == TabItemType.Wallet) (textPaint.measureText(text) - 26.uiPX()) / 2f
      else (textPaint.measureText(text) - 34.uiPX()) / 2f

    canvas?.translate(iconLeft, 15.uiPX().toFloat())

    val drawPath = when(type) {
      TabItemType.Market -> path.parser(SvgPath.market)
      TabItemType.Profile -> path.parser(SvgPath.profile)
      TabItemType.Wallet -> path.parser(SvgPath.wallet)
    }

    canvas?.drawPath(drawPath, iconPaint)
    canvas?.save()


    canvas?.drawText(text, -textX, 45.uiPX().toFloat(), textPaint)

  }

  fun setSelectedStyle() {
    iconPaint.color = GrayScale.Opacity7Black
    textPaint.color = GrayScale.Opacity7Black
    invalidate()
  }

  fun resetStyle() {
    iconPaint.color = Spectrum.white
    textPaint.color = Spectrum.white
    invalidate()
  }

}