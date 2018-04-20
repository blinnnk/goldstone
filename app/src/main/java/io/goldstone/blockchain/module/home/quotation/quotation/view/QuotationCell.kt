package io.goldstone.blockchain.module.home.quotation.quotation.view

import android.content.Context
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.extension.addCorner
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.value.CornerSize
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.ScreenSize
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.home.quotation.quotation.model.QuotationModel
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.relativeLayout

/**
 * @date 20/04/2018 8:18 PM
 * @author KaySaith
 */

class QuotationCell(context: Context) : LinearLayout(context) {

  var model by observing(QuotationModel()) {

  }

  init {
    layoutParams = LinearLayout.LayoutParams(matchParent, 220.uiPX())

    relativeLayout {
      layoutParams = RelativeLayout.LayoutParams(ScreenSize.widthWithPadding, 200.uiPX())
      addCorner(CornerSize.default.toInt(), Spectrum.white)
      x += PaddingSize.device
    }

  }
}