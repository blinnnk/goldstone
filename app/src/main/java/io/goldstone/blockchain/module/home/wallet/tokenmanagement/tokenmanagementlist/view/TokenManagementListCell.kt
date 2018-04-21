package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.view

import android.content.Context
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.blinnnk.component.HoneyBaseSwitch
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.BaseCell
import io.goldstone.blockchain.common.component.SquareIcon
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.utils.glideImage
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.DefaultTokenTable
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.matchParent

/**
 * @date 25/03/2018 5:12 PM
 * @author KaySaith
 */

open class TokenManagementListCell(context: Context) : BaseCell(context) {

  open var model: DefaultTokenTable? by observing(null) {
    model?.apply {
      // 显示默认图判断
      if(iconUrl.isBlank()) {
        icon.image.imageResource = R.drawable.default_token
      } else {
        icon.image.glideImage(iconUrl)
      }
      tokenInfo.title.text = symbol
      tokenInfo.subtitle.text = name
      switch.isChecked = isUsed
    }
  }

  val switch by lazy { HoneyBaseSwitch(context) }

  private val tokenInfo by lazy { TwoLineTitles(context) }
  private val icon by lazy { SquareIcon(context) }

  init {

    hasArrow = false

    this.addView(icon
      .apply {
        setGrayStyle()
        setMargins<LinearLayout.LayoutParams> { topMargin = 16.uiPX() }
      })

    this.addView(tokenInfo
      .apply { setBlackTitles() })

    this.addView(switch
      .apply {
        layoutParams = RelativeLayout.LayoutParams(50.uiPX(), matchParent)
        setThemColor(Spectrum.green, Spectrum.lightGreen)
      })

    tokenInfo.apply {
      setCenterInVertical()
      x += 40.uiPX()
    }

    switch.apply {
      setCenterInVertical()
      setAlignParentRight()
    }

    setGrayStyle()

  }

  fun getSymbol(): String = tokenInfo.title.text.toString()

}