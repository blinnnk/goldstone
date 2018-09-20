package io.goldstone.blockchain.common.component.edittext

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.RectF
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.widget.EditText
import android.widget.LinearLayout
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.SafeLevel
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.*
import org.jetbrains.anko.hintTextColor
import org.jetbrains.anko.leftPadding
import org.jetbrains.anko.sdk25.coroutines.onFocusChange
import org.jetbrains.anko.singleLine
import org.jetbrains.anko.textColor

/**
 * @date 22/03/2018 3:11 PM
 * @author KaySaith
 */
open class RoundInput(context: Context) : EditText(context) {

	var title by observing("") {
		invalidate()
	}
	private var safeLevel = ""
	private val paint = Paint()
	private val textPaint = Paint()
	private val alertPaint = Paint()
	private val backgroundPaint = Paint()
	private val titleSize = 14.uiPX().toFloat()
	private var maxCount = 16
	private var themeColor = Spectrum.blue
	private var showAlert = false

	init {
		paint.isAntiAlias = true
		paint.style = Paint.Style.STROKE
		paint.color = GrayScale.lightGray
		paint.strokeWidth = BorderSize.bold

		backgroundPaint.isAntiAlias = true
		backgroundPaint.style = Paint.Style.FILL
		backgroundPaint.color = Spectrum.white

		textPaint.isAntiAlias = true
		textPaint.style = Paint.Style.FILL
		textPaint.color = GrayScale.midGray
		textPaint.typeface = GoldStoneFont.heavy(context)
		textPaint.textSize = titleSize

		alertPaint.isAntiAlias = true
		alertPaint.style = Paint.Style.FILL
		alertPaint.color = GrayScale.midGray
		alertPaint.typeface = GoldStoneFont.heavy(context)
		alertPaint.textSize = 11.uiPX().toFloat()

		singleLine = true
		hintTextColor = GrayScale.lightGray

		this.setWillNotDraw(false)

		layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, 56.uiPX())

		leftPadding = 35.uiPX()
		backgroundTintMode = PorterDuff.Mode.CLEAR
		textColor = GrayScale.black
		typeface = GoldStoneFont.heavy(context)
		textSize = fontSize(14)

		filters = arrayOf(InputFilter.LengthFilter(maxCount))
		// `RoundInput` 主要用于输入用户名或密码, 防止输入太长内容做了长度限制
		this.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(content: Editable?) {
				afterTextChanged?.run()
			}

			override fun beforeTextChanged(
				s: CharSequence?,
				start: Int,
				count: Int,
				after: Int
			) {
			}

			override fun onTextChanged(
				content: CharSequence?,
				start: Int,
				before: Int,
				count: Int
			) {
			}
		})

		onFocusChange { _, hasFocus ->
			if (hasFocus) {
				paint.color = themeColor
				textPaint.color = themeColor
				textColor = themeColor
				invalidate()
			} else {
				showAlert = false
				paint.color = GrayScale.lightGray
				textPaint.color = GrayScale.midGray
				textColor = GrayScale.black
				invalidate()
			}
		}
	}

	fun setAlertStyle(style: SafeLevel) {
		showAlert = true
		when (style) {
			SafeLevel.High -> {
				themeColor = Spectrum.green
				safeLevel = SafeLevel.High.info.toUpperCase()
			}

			SafeLevel.Strong -> {
				themeColor = Spectrum.green
				safeLevel = SafeLevel.Strong.info.toUpperCase()
			}

			SafeLevel.Normal -> {
				themeColor = Spectrum.darkBlue
				safeLevel = SafeLevel.Normal.info.toUpperCase()
			}

			SafeLevel.Weak -> {
				themeColor = Spectrum.lightRed
				safeLevel = SafeLevel.Weak.info.toUpperCase()
			}
		}
		alertPaint.color = themeColor
		paint.color = themeColor
		textPaint.color = themeColor
		textColor = themeColor
		invalidate()
	}

	fun getContent(hold: (String) -> Unit) {
		hold(text.toString())
	}

	override fun onTextContextMenuItem(id: Int): Boolean {
		when (id) {
			android.R.id.cut -> onTextCut?.run()
			android.R.id.paste -> onTextPaste?.run()
			android.R.id.copy -> onTextCopy?.run()
		}
		return super.onTextContextMenuItem(id)
	}

	private var onTextPaste: Runnable? = null
	private var onTextCut: Runnable? = null
	private var onTextCopy: Runnable? = null
	var afterTextChanged: Runnable? = null
	private val paddingSize = 5.uiPX()

	@SuppressLint("DrawAllocation")
	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)
		val rectF = RectF(
			BorderSize.bold + paddingSize, BorderSize.bold + paddingSize,
			width - BorderSize.bold * 2 - paddingSize, height - BorderSize.bold * 2 - paddingSize
		)

		canvas?.drawRoundRect(rectF, height / 2f, height / 2f, paint)
		val textBackground = RectF(
			25.uiPX().toFloat(), 0f, textPaint.measureText(title) + 50.uiPX(), titleSize
		)
		canvas?.drawRect(textBackground, backgroundPaint)

		canvas?.drawText(title, 35.uiPX().toFloat(), 15.uiPX().toFloat(), textPaint)

		if (showAlert) {
			canvas?.drawText(
				safeLevel,
				width - alertPaint.measureText(safeLevel) - 25.uiPX(),
				32.uiPX().toFloat(),
				alertPaint
			)
		}
	}

	fun setNumberInput() {
		inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
	}

	fun setTextInput() {
		inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
	}

	fun setPasswordInput(show: Boolean = false) {
		inputType =
			if (show == false) InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
			else InputType.TYPE_CLASS_TEXT
	}

	fun setPinCodeInput() {
		inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
	}
}