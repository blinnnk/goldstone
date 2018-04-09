package io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.home.wallet.transactions.transactiondetail.model.TransactionDetailModel
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor
import org.jetbrains.anko.verticalLayout

/**
 * @date 27/03/2018 3:27 AM
 * @author KaySaith
 */

open class TransactionDetailCell(context: Context) : RelativeLayout(context) {

  var model: TransactionDetailModel by observing(TransactionDetailModel()) {
    description.text = model.description
    info.text = model.info
  }

  private val description = TextView(context)
  val info = TextView(context)

  init {

    this.setWillNotDraw(false)

    layoutParams = RelativeLayout.LayoutParams(matchParent, TransactionSize.cellHeight)

    verticalLayout {

      description
        .apply {
          textSize = 4.uiPX().toFloat()
          textColor = GrayScale.midGray
          typeface = GoldStoneFont.book(context)
          layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, 17.uiPX()).apply {
            topMargin = 15.uiPX()
          }
        }
        .into(this)

      info
        .apply {
          textSize = 5.uiPX().toFloat() - 1f
          textColor = GrayScale.black
          typeface = GoldStoneFont.medium(context)
          layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, 25.uiPX())
        }
        .into(this)

    }.let {
      setCenterInVertical()
      setMargins<RelativeLayout.LayoutParams> { leftMargin = PaddingSize.device }
    }

  }

  private val paint = Paint().apply {
    isAntiAlias = true
    color = GrayScale.lightGray
    style = Paint.Style.FILL
  }

  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)

    canvas?.drawLine(
      0f,
      height - BorderSize.default,
      (width - PaddingSize.device).toFloat(),
      height - BorderSize.default,
      paint
    )
  }

  fun setTitleColor(color: Int) {
    info.textColor = color
  }

  fun setGrayInfoStyle() {
    info.textSize = 4.uiPX().toFloat() - 1f
    info.textColor = GrayScale.midGray
  }

}



















