package io.goldstone.blockchain.common.base.basecell

import android.content.Context
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.component.HoneyBaseSwitch
import com.blinnnk.extension.*
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.ElementID.createTimeTextView
import io.goldstone.blockchain.common.value.ElementID.marketTextView
import io.goldstone.blockchain.common.value.ElementID.priceTextView
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor

/**
 * @date 08/08/2018 10:34 AM
 * @author wcx
 */
open class BaseSwitchCell(context: Context) : BaseCell(context) {

  protected var createTime = TextView(context)
  protected val price = TextView(context)
  protected val market = TextView(context)
  val switchImageView by lazy { HoneyBaseSwitch(context) }

  protected var icon: ImageView? = null

  init {
    hasArrow = false
    setGrayStyle()

    this.addView(createTime.apply {
      id = createTimeTextView
      text = "创建时间"
      textSize = fontSize(10)
      textColor = GrayScale.black
      typeface = GoldStoneFont.medium(context)
    })

    this.addView(price.apply {
      id = priceTextView
      text = "价格"
      textSize = fontSize(16)
      textColor = GrayScale.black
      typeface = GoldStoneFont.medium(context)
    })


    this.addView(market.apply {
      id = marketTextView
      text = "市场"
      textSize = fontSize(12)
      textColor = GrayScale.black
      typeface = GoldStoneFont.medium(context)
    })


    this.addView(switchImageView.apply {
      layoutParams = RelativeLayout.LayoutParams(50.uiPX(), matchParent)
      setThemColor(Spectrum.green, Spectrum.lightGreen)
    })

    switchImageView.setCenterInVertical()
    switchImageView.setAlignParentRight()

    layoutParams.width = matchParent
    layoutParams.height = 150.uiPX()
  }

  fun showIcon(image: Int, color: Int = GrayScale.whiteGray) {
    market.x = 50.uiPX().toFloat()
    if (icon.isNull()) {
      icon = ImageView(context).apply {
        layoutParams = RelativeLayout.LayoutParams(35.uiPX(), 35.uiPX())
        addCorner(17.uiPX(), color)
      }
      icon?.into(this)
      icon?.setCenterInVertical()
    }
    icon?.imageResource = image
  }

  fun setTitle(text: String) {
    market.text = text
  }

  fun getTitle(): String {
    return market.text.toString()
  }
}