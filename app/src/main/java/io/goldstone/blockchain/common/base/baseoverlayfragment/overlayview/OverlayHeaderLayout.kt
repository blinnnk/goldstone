package io.goldstone.blockchain.common.base.baseoverlayfragment.overlayview

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.extension.*
import com.blinnnk.uikit.AnimationDuration
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.SoftKeyboard
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.home.quotation.quotationsearch.view.SearchInputWithFilterLayout
import org.jetbrains.anko.*

/**
 * @date 22/03/2018 2:37 AM
 * @author KaySaith
 */
class OverlayHeaderLayout(context: Context) : RelativeLayout(context) {
	
	var title: TextView
	val closeButton by lazy {
		HeaderIcon(context).apply {
			id = ElementID.closeButton
			imageResource = R.drawable.close_icon
			setRightPosition()
		}
	}
	val backButton by lazy {
		HeaderIcon(context).apply {
			id = ElementID.backButton
			imageResource = R.drawable.back
			setLeftPosition()
		}
	}
	private val addButton by lazy {
		HeaderIcon(context).apply {
			id = ElementID.addButton
			imageResource = R.drawable.add_icon
			setLeftPosition()
		}
	}
	private val searchButton by lazy {
		HeaderIcon(context).apply {
			id = ElementID.searchButton
			imageResource = R.drawable.search_icon
			setLeftPosition()
		}
	}
	private val scanButton by lazy {
		HeaderIcon(context).apply {
			id = ElementID.scanButton
			imageResource = R.drawable.scan_qr_icon
			setRightPosition()
		}
	}
	private val searchInput by lazy {
		SearchInputWithFilterLayout(context)
	}
	private val headerHeight = HomeSize.headerHeight
	private val paint = Paint()
	
	init {
		backgroundColor = Spectrum.blue
		elevation = ShadowSize.Header
		layoutParams = RelativeLayout.LayoutParams(ScreenSize.Width, headerHeight)
		
		title = textView {
			textColor = Spectrum.white
			textSize = fontSize(15)
			typeface = GoldStoneFont.heavy(context)
			gravity = Gravity.CENTER
			layoutParams = RelativeLayout.LayoutParams(matchParent, headerHeight)
		}
		
		paint.color = GrayScale.lightGray
		paint.isAntiAlias = true
		paint.style = Paint.Style.FILL
	}
	
	fun showCloseButton(isShow: Boolean) {
		findViewById<ImageView>(ElementID.closeButton).apply {
			if (isShow) {
				isNull() isTrue { addView(closeButton) }
			} else {
				isNull() isFalse { removeView(this) }
			}
		}
	}
	
	fun showScanButton(
		isShow: Boolean,
		isLeft: Boolean = false,
		action: () -> Unit = {}
	) {
		findViewById<ImageView>(ElementID.scanButton).apply {
			if (isShow) {
				isNull() isTrue {
					if (isLeft) scanButton.setLeftPosition()
					addView(scanButton.click { action() })
				}
			} else {
				isNull() isFalse { removeView(this) }
			}
		}
	}
	
	fun resetFilterStatus(filtered: Boolean){
		searchInput.setFiltered(filtered)
	}
	
	fun showBackButton(
		isShow: Boolean,
		setClickEvent: ImageView.() -> Unit = {}
	) {
		findViewById<ImageView>(ElementID.backButton).let { it ->
			it.isNull() isTrue {
				isShow isTrue {
					backButton.click { setClickEvent(backButton) }.into(this)
				}
			} otherwise {
				isShow isTrue {
					backButton.click { setClickEvent(backButton) }
				} otherwise {
					removeView(it)
				}
			}
		}
	}
	
	fun showAddButton(
		isShow: Boolean,
		isLeft: Boolean = true,
		setClickEvent: () -> Unit = {}
	) {
		findViewById<ImageView>(ElementID.addButton).let { it ->
			it.isNull() isTrue {
				isShow isTrue {
					if (!isLeft) addButton.setRightPosition()
					addButton.click {
						setClickEvent()
					}.into(this)
				}
			} otherwise {
				isShow isTrue {
					addButton.click {
						setClickEvent()
						it.preventDuplicateClicks()
					}
				} otherwise {
					removeView(it)
				}
			}
		}
	}
	
	fun searchInputLinstener(isFocus: (Boolean) -> Unit, action: (String) -> Unit) {
		searchInput.editTextInput.setOnFocusChangeListener { _, isChanged ->
			isFocus(isChanged)
		}
		searchInput.editTextInput.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(content: Editable?) {
				if (!content.isNullOrBlank()) {
					action(content.toString())
				}
			}
			
			override fun beforeTextChanged(
				s: CharSequence?,
				start: Int,
				count: Int,
				after: Int
			) {
			}
			
			override fun onTextChanged(
				s: CharSequence?,
				start: Int,
				before: Int,
				count: Int
			) {
			}
		})
	}
	
	fun setSearchFilterClickEvent(callback: () -> Unit) {
		searchInput.setFilterClickEvent(callback)
	}
	
	fun showSearchButton(
		isShow: Boolean,
		setClickEvent: () -> Unit = {}
	) {
		if (isShow) {
			searchButton.click {
				setClickEvent()
			}.into(this)
		} else findViewById<ImageView>(ElementID.searchButton)?.let {
			removeView(it)
		}
	}
	
	fun showSearchInput(
		isShow: Boolean = true,
		cancelEvent: () -> Unit = {}
	) {
		showCloseButton(!isShow)
		
		isShow.isFalse {
			title.visibility = View.VISIBLE
			searchInput.visibility = View.GONE
			return
		}
		// 复用的 `OverlayFragment Header` 首先隐藏常规 `Title`
		title.visibility = View.GONE
		findViewById<SearchInputWithFilterLayout>(ElementID.searchInput).let {
			it.isNull() isTrue {
				searchInput.apply {
					setCancelClick {
						// 取消搜索后自动清空搜索框里面的内容
						searchInput.editTextInput.text.clear()
						SoftKeyboard.hide(context as Activity)
						// 等待键盘完全收起后在执行动作防止页面抖动
						cancelEvent()
						showSearchInput(false)
					}
					AnimationDuration.Default timeUpThen {
						editTextInput.requestFocus()
						SoftKeyboard.show(context as Activity, editTextInput)
					}
				}.into(this)
			} otherwise {
				it.visibility = View.VISIBLE
			}
		}
	}
}

class HeaderIcon(context: Context) : ImageView(context) {
	
	private val iconSize = 30.uiPX()
	
	init {
		setColorFilter(GrayScale.lightGray)
		scaleType = ImageView.ScaleType.CENTER_INSIDE
		addTouchRippleAnimation(Color.TRANSPARENT, Spectrum.blue, RippleMode.Round)
	}
	
	fun setLeftPosition() {
		layoutParams = RelativeLayout.LayoutParams(iconSize, iconSize).apply {
			topMargin = 18.uiPX()
			leftMargin = 15.uiPX()
			alignParentLeft()
		}
	}
	
	fun setRightPosition() {
		layoutParams = RelativeLayout.LayoutParams(iconSize, iconSize).apply {
			topMargin = 18.uiPX()
			rightMargin = 15.uiPX()
			alignParentRight()
		}
	}
}