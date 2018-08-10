package io.goldstone.blockchain.common.component

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.Language.AlarmClockText
import io.goldstone.blockchain.common.base.basecell.BaseRadioCell
import io.goldstone.blockchain.common.component.cell.TopBottomLineCell
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.wrapContent

/**
 * @date 02/08/2018 21:12 PM
 * @author wcx
 */
class PriceAlarmClockCreatorView(context: Context) : LinearLayout(context) {

  private var title: String = AlarmClockText.createNewAlarm
  private val titleCell = TopBottomLineCell(context)
  private val targetPriceEditText = EditText(context)
  private val currencyTextView = TextView(context)
  private val priceTypeTitleCell = TopBottomLineCell(context)
  private var priceType = 0
  private var alarmTypeView = AlarmTypeView(context)
  private var moreThanCell = object : BaseRadioCell(context) {
  }
  private var lessThanCell = object : BaseRadioCell(context) {
  }

  init {
    orientation = LinearLayout.VERTICAL
    layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)

    titleCell.apply {
      layoutParams = LinearLayout.LayoutParams(
        matchParent,
        wrapContent).apply {
        leftMargin = 20.uiPX()
        rightMargin = 20.uiPX()
      }
      setTitle(title)
    }.into(this)

    RelativeLayout(context).apply {
      layoutParams = RelativeLayout.LayoutParams(
        matchParent,
        matchParent).apply {
        leftMargin = 20.uiPX()
        rightMargin = 20.uiPX()
        bottomMargin = 20.uiPX()
      }

      targetPriceEditText.apply {
        layoutParams = ViewGroup.LayoutParams(
          matchParent,
          wrapContent)
        hint = AlarmClockText.targetPrice
        maxLines = 1
        inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        if (text.isNullOrBlank()) {
          val editTextString = "5800"
          setText(
            editTextString.toCharArray(),
            0,
            editTextString.length)
        }
      }
      addView(targetPriceEditText)

      currencyTextView.apply {
        layoutParams = ViewGroup.LayoutParams(
          wrapContent,
          wrapContent)
        if (text.isNullOrBlank()) {
          text = "USDT"
        }
      }
      addView(currencyTextView)
      currencyTextView.setAlignParentRight()
      currencyTextView.setCenterInVertical()
    }.into(this)

    priceTypeTitleCell.apply {
      layoutParams = LinearLayout.LayoutParams(
        matchParent,
        wrapContent).apply {
        leftMargin = 20.uiPX()
        rightMargin = 20.uiPX()
        bottomMargin = 20.uiPX()
      }
      setTitle(AlarmClockText.priceTypeTitle)
    }.into(this)

    // 大于输入价格
    moreThanCell.apply {
      if (getTitle().isEmpty()) {
        setTitle("1 BTC > 5800 USDT")
      }
      setSwitchStatusBy(true)
      onClick {
        moreThanCell.setSwitchStatusBy(true)
        lessThanCell.setSwitchStatusBy(false)
        priceType = 0
      }
    }
    moreThanCell.into(this)

    // 小于输入价格
    lessThanCell.apply {
      if (getTitle().isEmpty()) {
        setTitle("1 BTC < 5800 USDT")
      }
      onClick {
        moreThanCell.setSwitchStatusBy(false)
        lessThanCell.setSwitchStatusBy(true)
        priceType = 1
      }
    }
    lessThanCell.into(this)
    alarmTypeView.into(this)
  }

  fun setTitle(title: String) {
    this.title = title
    titleCell.setTitle(title)
    if (AlarmClockText.createNewAlarm == title) {
      alarmTypeView.visibility = View.VISIBLE
    } else {
      alarmTypeView.visibility = View.GONE
    }
  }

  fun setTargetPriceEditTextListener(
    price: Double?,
    currencyName: String?) {
    val editTextString = "" + price
    targetPriceEditText.setText(
      editTextString.toCharArray(),
      0,
      editTextString.length)
    targetPriceEditText.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(editable: Editable?) {
      }

      override fun beforeTextChanged(
        p0: CharSequence?,
        p1: Int,
        p2: Int,
        p3: Int) {
      }

      override fun onTextChanged(
        charSequence: CharSequence?,
        p1: Int,
        p2: Int,
        p3: Int) {
        if (("" + charSequence).isEmpty()) {
          moreThanCell.setTitle("1 BTC > 0 $currencyName")
          lessThanCell.setTitle("1 BTC < 0 $currencyName")

          moreThanCell.setSwitchStatusBy(true)
          lessThanCell.setSwitchStatusBy(false)
        } else {
          val inputPrice = ("" + charSequence).toDouble()
          if (inputPrice > 100000000) {
            val maxPrice = "" + 100000000
            targetPriceEditText.setText(
              maxPrice.toCharArray(),
              0,
              maxPrice.length)
            Toast.makeText(
              context,
              "已达到价格上限100000000",
              Toast.LENGTH_LONG).show()
            moreThanCell.setTitle("1 BTC > 100000000 $currencyName")
            lessThanCell.setTitle("1 BTC < 100000000 $currencyName")

            moreThanCell.setSwitchStatusBy(false)
            lessThanCell.setSwitchStatusBy(true)
          } else if (inputPrice < 0) {
            moreThanCell.setTitle("1 BTC > 0 $currencyName")
            lessThanCell.setTitle("1 BTC < 0 $currencyName")

            moreThanCell.setSwitchStatusBy(true)
            lessThanCell.setSwitchStatusBy(false)
          } else {
            moreThanCell.setTitle("1 BTC > $inputPrice $currencyName")
            lessThanCell.setTitle("1 BTC < $inputPrice $currencyName")
            if (inputPrice < price!!) {
              moreThanCell.setSwitchStatusBy(false)
              lessThanCell.setSwitchStatusBy(true)
            } else {
              moreThanCell.setSwitchStatusBy(true)
              lessThanCell.setSwitchStatusBy(false)
            }
          }
        }
      }
    })
  }

  fun setPriceChooseContent(
    price: Double?,
    currencyName: String?) {
    moreThanCell.setTitle("1 BTC > $price $currencyName")
    lessThanCell.setTitle("1 BTC < $price $currencyName")
  }

  fun setCurrencyName(currencyName: String?) {
    currencyTextView.text = currencyName
  }

  fun getTargetPriceEditText(): EditText {
    return targetPriceEditText
  }

  fun getPriceType(): Int {
    return priceType
  }

  fun getAlarmTypeView(): AlarmTypeView {
    return alarmTypeView
  }
}