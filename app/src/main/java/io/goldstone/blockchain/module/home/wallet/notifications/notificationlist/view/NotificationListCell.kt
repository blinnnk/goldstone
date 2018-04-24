package io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.view

import android.content.Context
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.extension.orZero
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.base.baseInfocell.BaseValueCell
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationTable
import io.goldstone.blockchain.module.home.wallet.notifications.notificationlist.model.NotificationType
import org.jetbrains.anko.textColor

/**
 * @date 25/03/2018 1:49 AM
 * @author KaySaith
 */

class NotificationListCell(context: Context) : BaseValueCell(context) {

  var model: NotificationTable? by observing(null) {

    info.apply {
      title.text = CryptoUtils.scaleTo28(model?.title.orEmpty())
      subtitle.text = CryptoUtils.scaleTo28(model?.content.orEmpty())
    }

    date.text = model?.timeDescription

    when (model?.type.orZero()) {
      NotificationType.Transaction.code -> {
        setIconColor(Spectrum.green)
        setIconResource(R.drawable.transaction_icon)
      }
      NotificationType.System.code -> {
        setIconColor(GrayScale.midGray)
        setIconResource(R.drawable.system_message_icon)
      }
    }
  }

  private val date by lazy { TextView(context) }

  init {
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