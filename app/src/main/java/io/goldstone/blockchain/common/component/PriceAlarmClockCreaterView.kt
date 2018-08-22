package io.goldstone.blockchain.common.component

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.extension.orZero
import com.blinnnk.extension.setAlignParentRight
import com.blinnnk.extension.setCenterInVertical
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.base.basecell.BaseRadioCell
import io.goldstone.blockchain.common.component.cell.TopBottomLineCell
import io.goldstone.blockchain.common.language.AlarmClockText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.Spectrum
import io.goldstone.blockchain.common.value.fontSize
import io.goldstone.blockchain.module.home.quotation.pricealarmclock.pricealarmclocklist.model.PriceAlarmClockTable
import org.jetbrains.anko.*

/**
 * @date 02/08/2018 21:12 PM
 * @author wcx
 */
class PriceAlarmClockCreaterView(context: Context) : LinearLayout(context) {

	private var title: String = AlarmClockText.createNewAlarm
	private val titleCell = TopBottomLineCell(context)
	private val targetPriceEditText = EditText(context)
	private val currencyTextView = TextView(context)
	private var priceType = 0
	private var priceTypeView = AlarmTypeView(context)
	private var alarmTypeView = AlarmTypeView(context)
	private var automaticChoosePriceTypeFlag = false

	init {
		orientation = LinearLayout.VERTICAL
		layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)

		titleCell.apply {
			layoutParams = LinearLayout.LayoutParams(
				matchParent,
				wrapContent
			).apply {
				leftMargin = 20.uiPX()
				rightMargin = 20.uiPX()
			}
			setTitle(title)
		}.into(this)

		RelativeLayout(context).apply {
			layoutParams = RelativeLayout.LayoutParams(
				matchParent,
				matchParent
			).apply {
				leftMargin = 17.uiPX()
				rightMargin = 17.uiPX()
				bottomMargin = 5.uiPX()
			}

			targetPriceEditText.apply {
				layoutParams = LinearLayout.LayoutParams(
					matchParent,
					56.uiPX()
				)
				hint = AlarmClockText.targetPrice
				hintTextColor = GrayScale.midGray
				maxLines = 1
				inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
				textSize = fontSize(15)
				textColor = GrayScale.black
				typeface = GoldStoneFont.medium(context)
				filters = arrayOf<InputFilter>(InputFilter.LengthFilter(20))
				singleLine = true
			}
			addView(targetPriceEditText)

			currencyTextView.apply {
				layoutParams = ViewGroup.LayoutParams(
					wrapContent,
					wrapContent
				)
				textSize = fontSize(15)
				textColor = GrayScale.black
				typeface = GoldStoneFont.medium(context)
			}
			addView(currencyTextView)
			currencyTextView.setAlignParentRight()
			currencyTextView.setCenterInVertical()
		}.into(this)

		priceTypeView.apply {
			showAlarmTypeTitleCell(AlarmClockText.priceTypeTitle)
			setRepeatingCellSwitchStatusBy(true)
		}.into(this)

		alarmTypeView.apply { showAlarmTypeTitleCell(AlarmClockText.alarmTypeTitle) }.into(this)
	}

	fun setTitle(title: String) {
		this.title = title
		titleCell.setTitle(
			title,
			fontSize(14),
			Spectrum.blue
		)
		if (AlarmClockText.createNewAlarm == title) {
			alarmTypeView.visibility = View.VISIBLE
		} else {
			alarmTypeView.visibility = View.GONE
		}
	}

	fun setTargetPriceEditTextListener(
		priceAlarmClockTable: PriceAlarmClockTable
	) {
		val editTextString = priceAlarmClockTable.price
		targetPriceEditText.setText(
			editTextString.toCharArray(),
			0,
			editTextString.length
		)

		targetPriceEditText.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(editable: Editable?) {
			}

			override fun beforeTextChanged(
				p0: CharSequence?,
				p1: Int,
				p2: Int,
				p3: Int
			) {
			}

			override fun onTextChanged(
				charSequence: CharSequence?,
				p1: Int,
				p2: Int,
				p3: Int
			) {
				if (charSequence.toString().isEmpty()) {
					priceTypeView.setRepeatingCellTitle("1 ${priceAlarmClockTable.symbol} > 0 ${priceAlarmClockTable.currencyName}")
					priceTypeView.setOneTimeCellTitle("1 ${priceAlarmClockTable.symbol} < 0 ${priceAlarmClockTable.currencyName}")
					changePriceType(true)
				} else {
					val inputPrice = (charSequence.toString()).toDouble()
					if (inputPrice > 100000000) {
						val maxPrice = 100000000.toString()
						targetPriceEditText.setText(
							maxPrice.toCharArray(),
							0,
							maxPrice.length
						)
						context.alert("已达到价格上限100000000")
						changePriceType(false)
					} else if (inputPrice < 0) {
						setPriceTypeContent(
							priceAlarmClockTable.symbol,
							"0",
							priceAlarmClockTable.currencyName
						)
						changePriceType(true)
					} else {
						setPriceTypeContent(
							priceAlarmClockTable.symbol,
							inputPrice.toString(),
							priceAlarmClockTable.currencyName
						)
						changePriceType(inputPrice > priceAlarmClockTable.marketPrice.toDouble())
					}
				}
			}
		})
	}

	fun setPriceChooseContent(priceAlarmClockTable: PriceAlarmClockTable) {
		setPriceTypeContent(priceAlarmClockTable.symbol, priceAlarmClockTable.price, priceAlarmClockTable.currencyName)
		changePriceType(priceType == 0)
	}

	fun setAlarmChooseContent(alarmType: Int) {
		alarmTypeView.setAlarmType(alarmType)
	}

	fun setCurrencyName(currencyName: String) {
		currencyTextView.text = currencyName
	}

	fun getTargetPriceEditTextContent(): String {
		return targetPriceEditText.text.toString().toIntOrNull().orZero().toString()
	}

	fun setPriceType(priceType: Int) {
		this.priceType = priceType
	}

	fun getPriceType(): Int {
		return priceType
	}

	fun getAlarmTypeView(): AlarmTypeView {
		return alarmTypeView
	}

	fun setAutomaticChoosePriceType(automaticChoosePriceTypeFlag: Boolean) {
		this.automaticChoosePriceTypeFlag = automaticChoosePriceTypeFlag
	}

	fun getGreaterThanPriceCell(): BaseRadioCell {
		return priceTypeView.getRepeatingCell()
	}

	fun getLessThanPriceCell(): BaseRadioCell {
		return priceTypeView.getOneTimeCell()
	}

	fun changePriceType(priceType: Boolean) {
		if (!automaticChoosePriceTypeFlag) {
			priceTypeView.setRepeatingCellSwitchStatusBy(priceType)
			priceTypeView.setOneTimeCellSwitchStatusBy(!priceType)
		}
	}

	fun setPriceTypeContent(
		symbol: String,
		rangeAmount: String,
		currencyName: String
	) {
		priceTypeView.setRepeatingCellTitle("1 $symbol > $rangeAmount $currencyName")
		priceTypeView.setOneTimeCellTitle("1 $symbol < $rangeAmount $currencyName")
	}
}