package io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.view

import android.content.Context
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.BaseCell
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationListModel
import org.jetbrains.anko.textColor

/**
 * @date 25/03/2018 1:49 AM
 * @author KaySaith
 */

class NotificationListCell(context: Context) : BaseCell(context) {

  var model: NotificationListModel by observing(NotificationListModel()) {

    info.apply {
      title.text = model.info
      subtitle.text = model.fromAddress
    }

    date.text = model.date

  }

  private val info by lazy { TwoLineTitles(context) }
  private val date by lazy { TextView(context) }

  init {

    info
      .apply { setBlackTitles() }
      .into(this)
    info.setCenterInVertical()

    date
      .apply {
        textColor = GrayScale.midGray
        textSize = 3.uiPX() + 1f
        typeface = GoldStoneFont.book(context)
        x -= 30.uiPX()
      }
      .into(this)
    date.apply {
      setCenterInVertical()
      setAlignParentRight()
    }

    setGrayStyle()

  }

}