package io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.view

import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.widget.ImageView
import android.widget.LinearLayout
import com.blinnnk.extension.into
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.clickToCopy
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.basefragment.BaseFragment
import io.goldstone.blockchain.common.component.AttentionTextView
import io.goldstone.blockchain.common.component.RoundButton
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.common.value.CommonText
import io.goldstone.blockchain.module.home.wallet.walletsettings.qrcodefragment.presenter.QRCodePresenter
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.verticalLayout

/**
 * @date 26/03/2018 11:06 PM
 * @author KaySaith
 */

class QRCodeFragment : BaseFragment<QRCodePresenter>() {

  private val address by lazy { AttentionTextView(context!!) }
  private val qrImage by lazy { ImageView(context) }
  private val saveImageButton by lazy { RoundButton(context!!) }
  private val copyAddressButton by lazy { RoundButton(context!!) }

  override val presenter = QRCodePresenter(this)

  @SuppressLint("SetTextI18n")
  override fun AnkoContext<Fragment>.initView() {
    relativeLayout {
      lparams(matchParent, matchParent)
      verticalLayout {
        qrImage
          .apply {
            val size = (ScreenSize.Width * 0.7).toInt()
            layoutParams = LinearLayout.LayoutParams(size, size).apply {
              leftMargin = (ScreenSize.Width * 0.15).toInt()
            }
            scaleType = ImageView.ScaleType.CENTER_CROP
            glideImage(R.drawable.qrcode)
          }
          .into(this)

        address
          .apply {
            text = "0x89d9s87d99sd879x98789d78979s7897d7979s786d678s876789d9s"
            setMargins<LinearLayout.LayoutParams> { topMargin = 10.uiPX() }
          }
          .into(this)

        copyAddressButton
          .apply {
            text = CommonText.copyAddress
            setBlueStyle()
            setMargins<LinearLayout.LayoutParams> { topMargin = 20.uiPX() }
          }
          .click { context.clickToCopy(address.text.toString()) }
          .into(this)

        saveImageButton
          .apply {
            text = CommonText.saveToAlbum
            setBlueStyle()
            setMargins<LinearLayout.LayoutParams> { topMargin = 15.uiPX() }
          }
          .into(this)
      }.apply {
        setCenterInVertical()
      }
    }
  }

}