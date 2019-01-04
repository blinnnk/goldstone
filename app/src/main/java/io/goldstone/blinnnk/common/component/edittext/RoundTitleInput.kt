package io.goldstone.blinnnk.common.component.edittext

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.RectF
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.extension.*
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.R
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.value.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick


/**
 * @author KaySaith
 * @date  2018/09/19
 */
class RoundTitleInput(context: Context) : RelativeLayout(context) {

	var onTextChanged: Runnable? = null

	private val editText = EditText(context)
	private val titleView = TextView(context)
	private val viewHeight = 46.uiPX()
	private val imageSize = 32.uiPX()

	private val button = ImageView(context).apply {
		isClickable = true
		layoutParams = RelativeLayout.LayoutParams(imageSize, imageSize)
		addTouchRippleAnimation(GrayScale.lightGray, Spectrum.green, RippleMode.Square, imageSize.toFloat())
		imageResource = R.drawable.contacts_icon
		visibility = View.GONE
		scaleType = ImageView.ScaleType.CENTER_INSIDE
		setColorFilter(GrayScale.midGray)
		x -= 5.uiPX()
	}

	init {
		setWillNotDraw(false)
		padding = BorderSize.bold.toInt() + 1.uiPX()
		layoutParams = RelativeLayout.LayoutParams(matchParent, viewHeight)
		relativeLayout {
			lparams(matchParent, matchParent)
			addCorner(viewHeight / 2, Spectrum.white)
		}
		titleView.apply {
			leftPadding = PaddingSize.content
			layoutParams = RelativeLayout.LayoutParams(matchParent, matchParent)
			gravity = Gravity.CENTER_VERTICAL
			textColor = GrayScale.midGray
			typeface = GoldStoneFont.black(context)
		}.into(this)
		editText.apply {
			singleLine = true
			hintTextColor = GrayScale.lightGray
			backgroundTintMode = PorterDuff.Mode.CLEAR
			textColor = GrayScale.black
			typeface = GoldStoneFont.heavy(context)
			textSize = fontSize(14)
			rightPadding = 20.uiPX()
			editText.gravity = Gravity.END
		}
		addView(editText)
		editText.alignParentRight()
		button.into(this)
		button.alignParentRight()
		button.centerInVertical()
		editText.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(s: Editable?) {
				onTextChanged?.run()
			}

			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
		})
	}

	private val offsetSize = BorderSize.bold + 3f
	private val paint = Paint().apply {
		isAntiAlias = true
		color = GrayScale.lightGray
		style = Paint.Style.STROKE
		strokeWidth = offsetSize
	}

	@SuppressLint("DrawAllocation")
	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)
		val cornerSize = (height - offsetSize) / 2
		val rectF = RectF(offsetSize, offsetSize, width - offsetSize, height - offsetSize)
		canvas?.drawRoundRect(rectF, cornerSize, cornerSize, paint)
	}

	fun setTitle(text: String) {
		titleView.text = text
	}

	fun setContent(text: String) {
		editText.setText(text)
	}

	fun setHint(text: String) {
		editText.hint = text
	}

	fun showButton() {
		editText.rightPadding = 45.uiPX()
		button.visibility = View.VISIBLE
	}

	fun getContent(): String {
		return if (editText.text.isNullOrEmpty()) editText.hint.toString() else editText.text.toString()
	}

	fun clearText() {
		editText.text.clear()
	}

	fun setButtonClickEvent(action: () -> Unit) {
		button.onClick {
			action()
			button.preventDuplicateClicks()
		}
	}

	fun setNumberPadKeyboard() {
		editText.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
	}
}