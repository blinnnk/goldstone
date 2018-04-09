package io.goldstone.blockchain.module.common.tokenpayment.paymentvaluedetail.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.text.InputType
import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentBottom
import com.blinnnk.honey.setCursorColor
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.component.GradientType
import io.goldstone.blockchain.common.component.GradientView
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionDetailModel
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view.TransactionDetailCell
import org.jetbrains.anko.*

/**
 * @date 28/03/2018 12:25 PM
 * @author KaySaith
 */
@SuppressLint("SetTextI18n")
class PaymentValueDetailHeaderView(context: Context) : RelativeLayout(context) {

  private val gradientView by lazy { GradientView(context) }
  private val description by lazy { TextView(context) }
  private val valueInput by lazy { EditText(context) }
  private val infoInput by lazy { TextView(context) }
  private val addressRemind by lazy { TransactionDetailCell(context) }

  private val gradientViewHeight = 170.uiPX()

  init {

    layoutParams = LinearLayout.LayoutParams(matchParent, 260.uiPX())

    gradientView
      .apply {
        layoutParams = LinearLayout.LayoutParams(matchParent, gradientViewHeight)
        setStyle(GradientType.DarkGreenYellow, gradientViewHeight)
      }
      .into(this)

    verticalLayout {
      layoutParams = RelativeLayout.LayoutParams(matchParent, gradientViewHeight)
      gravity = Gravity.CENTER
      description
        .apply {
          layoutParams = LinearLayout.LayoutParams(matchParent, 20.uiPX()).apply {
            topMargin = 18.uiPX()
          }
          text = "Send ETH Count"
          textColor = Spectrum.opacity5White
          textSize = 5.uiPX().toFloat()
          typeface = GoldStoneFont.medium(context)
          gravity = Gravity.CENTER
        }
        .into(this)

      valueInput
        .apply {
          hint = "0.0"
          hintTextColor = Spectrum.opacity5White
          textColor = Spectrum.white
          textSize = 16.uiPX().toFloat()
          typeface = GoldStoneFont.heavy(context)
          gravity = Gravity.CENTER
          inputType = InputType.TYPE_CLASS_NUMBER
          setCursorColor(Spectrum.blue)
          backgroundTintMode = PorterDuff.Mode.CLEAR
        }
        .into(this)

      infoInput
        .apply {
          layoutParams = LinearLayout.LayoutParams(matchParent, 20.uiPX()).apply {
            topMargin = -(20.uiPX())
          }
          text = "≈ 1298.29 (USD)"
          textColor = Spectrum.opacity5White
          textSize = 4.uiPX().toFloat()
          typeface = GoldStoneFont.medium(context)
          gravity = Gravity.CENTER
        }
        .into(this)
    }

    verticalLayout {
      layoutParams = LinearLayout.LayoutParams(matchParent, 90.uiPX())
      addressRemind.into(this)
      addressRemind.apply {
        setGrayInfoStyle()
        model = TransactionDetailModel("0x9d0x8d79s86d56s77d9s76d45s6d67s9d87f68s87d0", "Address To")
      }

      textView {
        text = "Miner Fee"
        textSize = 4.uiPX().toFloat()
        textColor = GrayScale.gray
        typeface = GoldStoneFont.book(context)
        layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, 20.uiPX()).apply {
          leftMargin = PaddingSize.device
          topMargin = 10.uiPX()
        }
      }
    }.setAlignParentBottom()

  }

  fun setInputFocus() {
    valueInput.hintTextColor = Spectrum.opacity1White
    valueInput.requestFocus()
  }

  fun showTargetAddress(address: String) {
    addressRemind.info.text = address
  }

}
