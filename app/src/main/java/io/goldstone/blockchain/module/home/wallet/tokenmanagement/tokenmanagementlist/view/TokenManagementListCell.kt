package io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.view

import android.content.Context
import android.widget.LinearLayout
import com.blinnnk.component.HoneyBaseSwitch
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.extension.setMargins
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.BaseCell
import io.goldstone.blockchain.common.component.SquareIcon
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagementlist.model.TokenManagementListModel

/**
 * @date 25/03/2018 5:12 PM
 * @author KaySaith
 */

open class TokenManagementListCell(context: Context) : BaseCell(context) {

  var model: TokenManagementListModel by observing(TokenManagementListModel()) {
    icon.src = model.icon
    tokenInfo.title.text = model.symbols
    tokenInfo.subtitle.text = model.tokenName
    switch.isChecked = model.isAdded
  }

  private val icon by lazy { SquareIcon(context) }
  private val tokenInfo by lazy { TwoLineTitles(context) }
  private val switch by lazy { HoneyBaseSwitch(context) }

  init {

    hasArrow = false

    this.addView(icon
      .apply {
        setGrayStyle()
        src = R.drawable.etc_icon
        setMargins<LinearLayout.LayoutParams> { topMargin = 16.uiPX() }
      })

    this.addView(tokenInfo
      .apply { setBlackTitles() })

    this.addView(switch
      .apply {
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

}