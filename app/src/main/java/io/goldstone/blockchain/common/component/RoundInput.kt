package io.goldstone.blockchain.common.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.RectF
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.EditText
import android.widget.LinearLayout
import com.blinnnk.extension.orZero
import com.blinnnk.honey.setCursorColor
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.alert
import io.goldstone.blockchain.common.value.BorderSize
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.PaddingSize
import io.goldstone.blockchain.common.value.Spectrum
import org.jetbrains.anko.hintTextColor
import org.jetbrains.anko.leftPadding
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

	private val maxCount = 20
	private val paint = Paint()
	private val textPaint = Paint()
	private val backgroundPaint = Paint()
	private val titleSize = 16.uiPX().toFloat()

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

		singleLine = true

		hintTextColor = GrayScale.lightGray

		this.setWillNotDraw(false)

		layoutParams = LinearLayout.LayoutParams(
			ScreenSize.Width - PaddingSize.device * 2, 65.uiPX()
		).apply {
			leftMargin = PaddingSize.device
		}

		leftPadding = 35.uiPX()
		backgroundTintMode = PorterDuff.Mode.CLEAR
		textColor = GrayScale.black
		typeface = GoldStoneFont.heavy(context)
		setCursorColor(Spectrum.blue)

		// `RoundInput` 主要用于输入用户名或密码, 防止输入太长内容做了长度限制
		this.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(content: Editable?) {
				afterContentChanged(content)
				afterTextChanged?.run()
			}

			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
			override fun onTextChanged(content: CharSequence?, start: Int, before: Int, count: Int) {
			}
		})

	}

	private var textContent: String = ""
	fun getContent(hold: (String) -> Unit) {
		hold(textContent)
	}

	override fun onTextContextMenuItem(id: Int): Boolean {
		when (id) {
			android.R.id.cut -> onTextCut?.run()
			android.R.id.paste -> onTextPaste?.run()
			android.R.id.copy -> onTextCopy?.run()
		}
		return super.onTextContextMenuItem(id)
	}

	var onTextPaste: Runnable? = null
	var onTextCut: Runnable? = null
	var onTextCopy: Runnable? = null

	var afterTextChanged: Runnable? = null

	open fun afterContentChanged(content: CharSequence?) {
		if (content?.length.orZero() > maxCount) {
			val newContent = content?.substring(0, maxCount) ?: ""
			setText(newContent)
			textContent = newContent
			context.alert("content is to long")
		} else {
			textContent = content.toString()
		}
	}

	private val paddingSize = 5.uiPX()

	@SuppressLint("DrawAllocation")
	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)

		val rectF = RectF(
			BorderSize.bold + paddingSize,
			BorderSize.bold + paddingSize,
			width - BorderSize.bold * 2 - paddingSize,
			height - BorderSize.bold * 2 - paddingSize
		)

		canvas?.drawRoundRect(rectF, height / 2f, height / 2f, paint)

		val textBackground = RectF(
			25.uiPX().toFloat(), 0f, textPaint.measureText(title) + 50.uiPX(), titleSize
		)
		canvas?.drawRect(textBackground, backgroundPaint)

		canvas?.drawText(title, 35.uiPX().toFloat(), 15.uiPX().toFloat(), textPaint)
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