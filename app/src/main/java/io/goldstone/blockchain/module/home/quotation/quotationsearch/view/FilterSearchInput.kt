package io.goldstone.blockchain.module.home.quotation.quotationsearch.view

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.*
import com.blinnnk.extension.addCorner
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.language.CommonText
import io.goldstone.blockchain.common.language.EmptyText
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.*
import org.jetbrains.anko.*

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
				textAlignment = EditText.TEXT_ALIGNMENT_GRAVITY
				hint = EmptyText.searchInput
				backgroundTintMode = PorterDuff.Mode.CLEAR
				textSize = fontSize(12)
				textColor = GrayScale.black
				hintTextColor = GrayScale.midGray
				typeface = GoldStoneFont.medium(context)
				layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
				setHorizontallyScrolling(false)
				setPadding(0, 10.uiPX(), 0, 10.uiPX())
				setOnKeyListener { _, keyCode, event ->
					// If the event is a key-down event on the "enter" button
					if ((event?.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
						enterKeyEvent?.run()
						true
					} else false
				}
			}
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