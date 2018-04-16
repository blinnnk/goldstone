package io.goldstone.blockchain.module.home.profile.contacts.contracts.view

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.blinnnk.extension.addCorner
import com.blinnnk.extension.into
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.base.baseInfocell.BaseValueCell
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import org.jetbrains.anko.textColor

/**
 * @date 26/03/2018 1:37 PM
 * @author KaySaith
 */

class ContactsCell(context: Context) : BaseValueCell(context) {

  var model: ContactTable by observing(ContactTable()) {
    info.apply {
      title.text = model.name
      subtitle.text = CryptoUtils.scaleMiddleAddress(model.address)
    }
    fontIcon.text = model.name.substring(0, 1).toUpperCase()
  }

  private val fontIcon by lazy {
    TextView(context).apply {
      layoutParams = LinearLayout.LayoutParams(50.uiPX(), 50.uiPX())
      addCorner(25.uiPX(), GrayScale.lightGray)
      textSize = 6.uiPX().toFloat()
      textColor = GrayScale.gray
      gravity = Gravity.CENTER
    }
  }
  init {
    setGrayStyle()
    hasArrow = false
    removeView(icon)
    fontIcon.into(this)
    fontIcon.setCenterInVertical()
  }

}