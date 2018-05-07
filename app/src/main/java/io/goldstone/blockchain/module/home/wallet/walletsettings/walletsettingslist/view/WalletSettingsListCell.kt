package io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.view

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.BaseCell
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.WalletSettingsText
import io.goldstone.blockchain.module.home.wallet.walletsettings.walletsettingslist.model.WalletSettingsListModel
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.textColor

/**
 * @date 25/03/2018 10:16 PM
 * @author KaySaith
 */

class WalletSettingsListCell(context: Context) : BaseCell(context) {

  var model: WalletSettingsListModel by observing(WalletSettingsListModel()) {
    title.text = model.title
    if (model.title == WalletSettingsText.checkQRCode) showQRIcon()
    else description.text = model.description
  }

  private val title = TextView(context)
  private val qrIcon by lazy { ImageView(context) }
  private val description by lazy { TextView(context) }

  private var titleColor = GrayScale.black

  init {

    title
      .apply {
        textColor = titleColor
        textSize = 4.uiPX().toFloat() + 1f
        typeface = GoldStoneFont.heavy(context)
      }
      .into(this)

    description
      .apply {
        textSize = 4.uiPX().toFloat() + 1f
        typeface = GoldStoneFont.heavy(context)
        textColor = GrayScale.gray
        x -= 30.uiPX()
      }
      .into(this)

    setGrayStyle()

    title.setCenterInVertical()

    description.apply {
      setAlignParentRight()
      setCenterInVertical()
    }

    layoutParams.height = 50.uiPX()

  }

  private fun showQRIcon() {
    removeView(description)
    qrIcon
      .apply {
        imageResource = R.drawable.qrcode_icon
        layoutParams = LinearLayout.LayoutParams(20.uiPX(), 20.uiPX())
        x -= 30.uiPX()
      }
      .into(this)
    qrIcon.apply {
      setAlignParentRight()
      setCenterInVertical()
    }
  }

}