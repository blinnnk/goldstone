package io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.BaseRadioCell
import io.goldstone.blockchain.common.component.AlarmTypeView
import io.goldstone.blockchain.common.component.DashboardOverlay
import io.goldstone.blockchain.common.value.AlarmClockText
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.wrapContent
import android.text.InputType

/**
 * @date 02/08/2018 21:12 PM
 * @author wcx
 */
class PriceAlarmClockCreaterView(private val context: Context) {

  var title: String = ""
  val titleTextView = TextView(context)
  val targetPriceEditText = EditText(context)
  val currencyTextView = TextView(context)
  val priceTypeTitleTextView = TextView(context)
  var priceType = 0
  var alarmTypeView = AlarmTypeView(context)
  var moreThanRadioCell = object : BaseRadioCell(context) {
  }
  var lessThanRadioCell = object : BaseRadioCell(context) {
  }
  var repeatingRadioCell: BaseRadioCell = alarmTypeView.repeatingRadioCell
  var oneTimeRadioCell: BaseRadioCell = alarmTypeView.oneTimeRadioCell
  var outConfirmEvent: Runnable? = null

  @SuppressLint("SetTextI18n")
  fun getPriceAlarmClockCreaterView(): DashboardOverlay {
    return DashboardOverlay(context) {
      titleTextView.apply {
        val titleLayoutParams = RelativeLayout.LayoutParams(
          matchParent,
          matchParent)
        titleLayoutParams.setMargins(
          20.uiPX(),
          0,
          20.uiPX(),
          0)
        this.layoutParams = titleLayoutParams
        text = title
      }.into(this)

      View(context).apply {
        val lineLayoutParams = RelativeLayout.LayoutParams(
          matchParent,
          1.uiPX())
        lineLayoutParams.setMargins(
          20.uiPX(),
          20.uiPX(),
          20.uiPX(),
          20.uiPX())
        this.layoutParams = lineLayoutParams
        backgroundColor = Color.BLACK
      }.into(this)

      RelativeLayout(context).apply {
        val inputLayoutParams = RelativeLayout.LayoutParams(
          matchParent,
          matchParent)
        inputLayoutParams.setMargins(
          20.uiPX(),
          0,
          20.uiPX(),
          20.uiPX())
        this.layoutParams = inputLayoutParams

        targetPriceEditText.apply {
          this.layoutParams = ViewGroup.LayoutParams(
            matchParent,
            wrapContent)
          hint = AlarmClockText.targetPrice
          maxLines = 1
          inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
          if (text == null || text.toString() == "") {
            val editTextString = "5800"
            setText(
              editTextString.toCharArray(),
              0,
              editTextString.length)
          }
        }
        addView(targetPriceEditText)

        currencyTextView.apply {
          this.layoutParams = ViewGroup.LayoutParams(
            wrapContent,
            wrapContent)
          if (text == null || text.toString() == "") {
            text = "USDT"
          }
        }
        addView(currencyTextView)
        currencyTextView.setAlignParentRight()
        currencyTextView.setCenterInVertical()
      }.into(this)

      priceTypeTitleTextView.apply {
        text = AlarmClockText.priceTypeTitle
        val priceTypeTitleLayoutParams = RelativeLayout.LayoutParams(
          matchParent,
          matchParent)
        priceTypeTitleLayoutParams.setMargins(
          20.uiPX(),
          0,
          20.uiPX(),
          0)
        this.layoutParams = priceTypeTitleLayoutParams
      }.into(this)

      View(context).apply {
        val lineLayoutParams = RelativeLayout.LayoutParams(
          matchParent,
          1.uiPX())
        lineLayoutParams.setMargins(
          20.uiPX(),
          20.uiPX(),
          20.uiPX(),
          20.uiPX())
        this.layoutParams = lineLayoutParams
        backgroundColor = Color.BLACK
      }.into(this)

      // 大于输入价格
      moreThanRadioCell.apply {
        if (getTitle() == "") {
          setTitle("1 BTC > 5800 USDT")
        }
        setSwitchStatusBy(true)
        onClick {
          moreThanRadioCell.setSwitchStatusBy(true)
          lessThanRadioCell.setSwitchStatusBy(false)
          priceType = 0
        }
      }
      moreThanRadioCell.into(this)

      // 小于输入价格
      lessThanRadioCell.apply {
        if (getTitle() == "") {
          setTitle("1 BTC < 5800 USDT")
        }
        onClick {
          moreThanRadioCell.setSwitchStatusBy(false)
          lessThanRadioCell.setSwitchStatusBy(true)
          priceType = 1
        }
      }
      lessThanRadioCell.into(this)

      if (AlarmClockText.createNewAlarm == title) {
        alarmTypeView.into(this)
      }

    }.apply {
      confirmEvent = outConfirmEvent
    }
  }

}