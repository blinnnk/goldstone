package io.goldstone.blinnnk.module.home.quotation.quotationsearch.view

import android.content.Context
import android.graphics.PorterDuff
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.*
import com.blinnnk.extension.addCorner
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blinnnk.R
import io.goldstone.blinnnk.common.language.CommonText
import io.goldstone.blinnnk.common.utils.GoldStoneFont
import io.goldstone.blinnnk.common.utils.click
import io.goldstone.blinnnk.common.value.ElementID
import io.goldstone.blinnnk.common.value.GrayScale
import io.goldstone.blinnnk.common.value.Spectrum
import io.goldstone.blinnnk.common.value.fontSize
import org.jetbrains.anko.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import io.goldstone.blinnnk.common.language.HoneyLanguage
import io.goldstone.blinnnk.common.language.currentLanguage


/**
 * @date: 2018/8/28.
 * @author: yanglihai
 * @description:
 */
class FilterSearchInput(context: Context) : LinearLayout(context) {
	var enterKeyEvent: Runnable? = null
	private val filterIcon by lazy {
		ImageView(context).apply {
			layoutParams = LinearLayout.LayoutParams(30.uiPX(), matchParent)
			imageResource = R.drawable.filter_icon
			scaleType = ImageView.ScaleType.CENTER_INSIDE
			visibility = View.GONE
		}
	}
	lateinit var editText: EditText
	private val cancelButton by lazy {
		TextView(context).apply {
			text = CommonText.cancel
			textColor = GrayScale.midGray
			textSize = fontSize(13)
			typeface = GoldStoneFont.book(context)
			layoutParams = RelativeLayout.LayoutParams(70.uiPX(), matchParent)
			gravity = Gravity.CENTER
		}
	}

	init {
		id = ElementID.searchInput
		layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
		leftPadding = 20.uiPX()
		rightPadding = 20.uiPX()
		gravity = Gravity.CENTER_VERTICAL
		linearLayout {
			gravity = Gravity.CENTER_VERTICAL
			layoutParams = LinearLayout.LayoutParams(ScreenSize.Width - 100.uiPX(), 38.uiPX())
			leftPadding = 10.uiPX()
			addCorner(5.uiPX(), Spectrum.white)
			addView(filterIcon)
			editText = editText {
				layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
				imeOptions = EditorInfo.IME_ACTION_SEARCH
				inputType = InputType.TYPE_CLASS_TEXT
				backgroundTintMode = PorterDuff.Mode.CLEAR
				textSize = fontSize(12)
				textColor = GrayScale.black
				hintTextColor = GrayScale.midGray
				typeface = GoldStoneFont.medium(context)
				setHorizontallyScrolling(false)
				if (currentLanguage == HoneyLanguage.Russian.code) {
					y = 5.uiPX().toFloat()
				}
			}
			editText.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					enterKeyEvent?.run()
					return@OnEditorActionListener true
				}
				false
			})
		}
		addView(cancelButton)
	}

	fun showFilterImage(visible: Boolean) {
		filterIcon.visibility = if (visible) View.VISIBLE else View.GONE
	}

	fun setCancelClick(callback: () -> Unit) {
		cancelButton.click { callback() }
	}

	fun setFilterClickEvent(callback: () -> Unit) {
		filterIcon.click { callback() }
	}

	fun setFiltered(hasFiltered: Boolean) {
		filterIcon.setColorFilter(if (hasFiltered) GrayScale.black else GrayScale.lightGray)
	}

}