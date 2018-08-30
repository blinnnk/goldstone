package io.goldstone.blockchain.module.home.quotation.quotationsearch.view

import android.content.Context
import android.graphics.PorterDuff
import android.view.*
import android.widget.*
import com.blinnnk.extension.*
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
class SearchInputWithFilterLayout(context: Context) : LinearLayout(context) {
	private val imageViewFilter by lazy {
		ImageView(context).apply {
			layoutParams = LinearLayout.LayoutParams(38.uiPX(), matchParent)
			imageResource = R.drawable.search_icon
			gravity = Gravity.CENTER_VERTICAL
			visibility = View.GONE
		}
	}
	
	private val textViewFilter by lazy {
		TextView(context).apply {
			textSize = fontSize(18)
			textColor = GrayScale.black
			text = "EX"
			typeface = GoldStoneFont.book(context)
			layoutParams = LinearLayout.LayoutParams(wrapContent, matchParent)
			gravity = Gravity.CENTER_VERTICAL
			setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.arrow_icon, 0)
		}
	}
	
	private val viewDivider by lazy {
		View(context).apply {
			layoutParams = LinearLayout.LayoutParams(1.uiPX(), 30.uiPX())
			backgroundColor = Spectrum.darkBlue
		}
	}
	
	val editTextInput by lazy {
		EditText(context).apply {
			hint = EmptyText.searchInput
			textSize = fontSize(12)
			textColor = GrayScale.black
			hintTextColor = GrayScale.midGray
			typeface = GoldStoneFont.book(context)
			singleLine = true
			layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
			background = null
		}
	}
	
	private val cancelTextView by lazy {
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
		orientation = LinearLayout.HORIZONTAL
		layoutParams = LinearLayout.LayoutParams(matchParent, matchParent)
		leftPadding = 20.uiPX()
		rightPadding = 20.uiPX()
		
		linearLayout {
			gravity = Gravity.CENTER_VERTICAL
			orientation = LinearLayout.HORIZONTAL
			layoutParams = LinearLayout.LayoutParams(ScreenSize.Width - 100.uiPX(), 38.uiPX())
			leftPadding = 10.uiPX()
			
			backgroundTintMode = PorterDuff.Mode.CLEAR
			addCorner(CornerSize.default.toInt(), GrayScale.whiteGray)
			
			addView(textViewFilter)
			addView(imageViewFilter)
			addView(viewDivider)
			addView(editTextInput)
		}
		addView(cancelTextView)
		
	}
	
	fun setCancelClick(callback: () -> Unit) {
		cancelTextView.click { callback() }
	}
	
	fun setFilterClickEvent(callback: () -> Unit) {
		imageViewFilter.click { callback() }
		textViewFilter.click { callback() }
	}
	
	fun setFiltered(filtered: Boolean) {
		if (filtered) {
			imageViewFilter.visibility = View.VISIBLE
			textViewFilter.visibility = View.GONE
		} else {
			imageViewFilter.visibility = View.GONE
			textViewFilter.visibility = View.VISIBLE
		}
	}
	
}