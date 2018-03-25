package io.goldstone.blockchain.module.home.profile.view

import android.content.Context
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.BaseCell
import io.goldstone.blockchain.common.component.SquareIcon
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.CommonCellSize
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.module.home.profile.model.ProfileModel
import org.jetbrains.anko.textColor

/**
 * @date 25/03/2018 10:55 PM
 * @author KaySaith
 */

class ProfileCell(context: Context) : BaseCell(context) {

  var model: ProfileModel by observing(ProfileModel()) {
    icon.src = model.icon
    title.text = model.title
    info.text = model.info
  }

  private val icon by lazy { SquareIcon(context) }

  private val title by lazy { TextView(context) }
  private val info by lazy { TextView(context) }

  init {

    icon.into(this)
    icon.setCenterInVertical()

    title
      .apply {
        textColor = Spectrum.white
        textSize = 5.uiPX().toFloat()
        typeface = GoldStoneFont.medium(context)
        x += CommonCellSize.iconPadding
      }
      .into(this)

    info
      .apply {
        textColor = Spectrum.opacity5White
        textSize = 4.uiPX().toFloat()
        typeface = GoldStoneFont.medium(context)
        x -= CommonCellSize.rightPadding
      }
      .into(this)

    title.setCenterInVertical()

    info.apply {
      setCenterInVertical()
      setAlignParentRight()
    }

  }

}