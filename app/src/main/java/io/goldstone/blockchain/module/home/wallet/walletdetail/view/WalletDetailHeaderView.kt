package io.goldstone.blockchain.module.home.wallet.walletdetail.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.component.RoundBorderButton
import io.goldstone.blockchain.common.component.RoundButtonWithIcon
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.UIUtils
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.SymbolText
import io.goldstone.blockchain.common.value.WalletText
import io.goldstone.blockchain.crypto.fromatCurrency
import io.goldstone.blockchain.module.common.walletgeneration.createwallet.model.WalletTable
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout

/**
 * @date 23/03/2018 4:21 PM
 * @author KaySaith
 */

data class WalletDetailHeaderModel(
  val avatar: String?,
  val name: String,
  val address: String,
  val totalBalance: String,
  val totalAccount: Int
)

class WalletDetailHeaderView(context: Context) : RelativeLayout(context) {

  var model: WalletDetailHeaderModel? by observing(null) {
    model?.apply {
      if (avatar.isNull()) currentAccount.avatar.glideImage(UIUtils.generateAvatar(WalletTable.current.id))
      else currentAccount.avatar.glideImage(avatar)
      currentAccount.info.title.text = name
      currentAccount.info.subtitle.text = address
      balanceTitle.text = totalBalance.toDouble().fromatCurrency()
      manageButton.text = (WalletText.manage + " ($totalAccount)").toUpperCase()
    }
  }

  val manageButton by lazy { RoundButtonWithIcon(context) }
  val addTokenButton by lazy { RoundBorderButton(context) }
  val currentAccount by lazy { CurrentAccountView(context) }

  private val balanceTitle by lazy { TextView(context) }
  private val sectionHeaderHeight = 50.uiPX()

  init {

    setWillNotDraw(false)

    layoutParams = RelativeLayout.LayoutParams(matchParent, 365.uiPX())

    currentAccount.into(this)
    currentAccount.apply {
      setCenterInHorizontal()
      y += 30.uiPX()
    }

    verticalLayout {
      balanceTitle
        .apply {
          textSize = 12.uiPX().toFloat()
          typeface = GoldStoneFont.black(context)
          textColor = Spectrum.white
          gravity = Gravity.CENTER_HORIZONTAL
        }
        .into(this)

      textView(WalletText.totalAssets + SymbolText.usd) {
        textSize = 4.uiPX().toFloat()
        typeface = GoldStoneFont.light(context)
        textColor = Spectrum.opacity5White
        gravity = Gravity.CENTER_HORIZONTAL
      }.lparams(matchParent, matchParent)

    }.apply {
      setCenterInParent()
    }

    manageButton
      .apply {
        y -= sectionHeaderHeight + 25.uiPX()
      }
      .into(this)
    manageButton.setCenterInHorizontal()
    manageButton.setAlignParentBottom()

    textView {
      text = WalletText.section
      typeface = GoldStoneFont.heavy(context)
      textColor = Spectrum.white
      textSize = 5.uiPX().toFloat()
      y -= 20.uiPX()
    }.apply {
      setAlignParentBottom()
      x += PaddingSize.device
      y += 10.uiPX()
    }

    addTokenButton
      .apply {
        themeColor = Spectrum.white
        text = WalletText.addToken
        layoutParams = LinearLayout.LayoutParams(125.uiPX(), 24.uiPX())
        touchColor = Spectrum.yellow
        x -= PaddingSize.device
        y -= 10.uiPX()
      }
      .into(this)

    addTokenButton.apply {
      setAlignParentRight()
      setAlignParentBottom()
      setAdjustWidth()
    }

  }

  private val paint = Paint().apply {
    isAntiAlias = true
    style = Paint.Style.FILL
    color = Spectrum.opacity2White
  }

  @SuppressLint("DrawAllocation")
  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)
    canvas?.drawLine(
      PaddingSize.device.toFloat(),
      height - sectionHeaderHeight.toFloat(),
      width - PaddingSize.device.toFloat(),
      height - sectionHeaderHeight.toFloat(),
      paint
    )
  }

}