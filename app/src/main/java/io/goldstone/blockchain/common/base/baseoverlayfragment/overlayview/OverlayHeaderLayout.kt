package io.goldstone.blockchain.common.base.baseoverlayfragment.overlayview

import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.blinnnk.extension.into
import com.blinnnk.extension.timeUpThen
import com.blinnnk.uikit.AnimationDuration
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.util.SoftKeyboard
import io.goldstone.blockchain.R
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.utils.click
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.module.home.quotation.quotationsearch.view.FilterSearchInput
import org.jetbrains.anko.*

/**
 * @date 22/03/2018 2:37 AM
 * @author KaySaith
 */
class OverlayHeaderLayout(context: Context) : RelativeLayout(context) {
	private var title: TextView
	private val closeButton by lazy {
		HeaderIcon(context).apply {
			id = ElementID.closeButton
			imageResource = R.drawable.close_icon
			setRightPosition()
		}
	}
	private val backButton by lazy {
		HeaderIcon(context).apply {
			id = ElementID.backButton
			imageResource = R.drawable.back
			setLeftPosition()
		}
	}
	private var addButton: HeaderIcon? = null
	private val searchButton by lazy {
		HeaderIcon(context).apply {
			id = ElementID.searchButton
			imageResource = R.drawable.search_icon
			setLeftPosition()
		}
	}

	private val filterButton by lazy {
		HeaderIcon(context).apply {
			id = ElementID.filterIcon
			imageResource = R.drawable.filter_icon
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

	var searchTextChangedEvent: Runnable? = null

	private var searchInput: FilterSearchInput? = null

	fun setTitle(title: String) {
		this.title.text = title
	}

	fun setSearchInputHint(hint: String) {
		searchInput?.editText?.hint = hint
	}

	fun showTitle(status: Boolean) {
		title.visibility = if (status) View.VISIBLE else View.GONE
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

	fun showFilterImage(status: Boolean) {
		searchInput?.showFilterImage(status)
	}

	fun showCloseButton(isShow: Boolean, clickEvent: () -> Unit) {
		val currentButton = findViewById<ImageView>(ElementID.closeButton)
		if (isShow) {
			showBackButton(false) {}
		}
		if (currentButton == null) {
			if (isShow) {
				addView(
					closeButton.click {
						clickEvent()
					}
				)
			}
		} else if (isShow) {
			closeButton.click { clickEvent() }
		} else {
			removeView(currentButton)
		}
	}

	fun showFilterIcon(isShow: Boolean, action: () -> Unit) {
		val currentButton = findViewById<ImageView>(ElementID.filterIcon)
		if (currentButton == null) {
			if (isShow) {
				addView(filterButton.click { action() })
			}
		} else if (isShow) currentButton.click { action() }
		else removeView(currentButton)
	}

	fun showScanButton(
		isShow: Boolean,
		isLeft: Boolean = false,
		action: () -> Unit
	) {
		val currentButton = findViewById<ImageView>(ElementID.scanButton)
		if (currentButton == null) {
			if (isShow) {
				if (isLeft) scanButton.setLeftPosition()
				addView(scanButton.click { action() })
			}
		} else if (isShow) currentButton.click { action() }
		else removeView(currentButton)
	}

	fun resetFilterStatus(filtered: Boolean) {
		searchInput?.setFiltered(filtered)
	}

	fun showBackButton(
		isShow: Boolean,
		setClickEvent: ImageView.() -> Unit
	) {
		if (isShow) {
			showAddButton(false) {}
			showCloseButton(false) {}
		}
		// BackButton 与 CloseButton 不会同时出现
		val currentButton = findViewById<ImageView>(ElementID.backButton)
		if (currentButton == null) {
			if (isShow) {
				backButton.click { setClickEvent(backButton) }.into(this)
			}
		} else if (isShow) {
			backButton.click { setClickEvent(backButton) }
		} else removeView(currentButton)
	}

	fun showAddButton(
		isShow: Boolean,
		isLeft: Boolean = true,
		setClickEvent: () -> Unit
	) {
		if (addButton == null) {
			if (isShow) {
				addButton = HeaderIcon(context).apply {
					id = ElementID.addButton
					imageResource = R.drawable.add_icon
					if (isLeft) setLeftPosition() else setRightPosition()
				}
				addButton?.click {
					setClickEvent()
				}?.into(this)
			}
		} else {
			if (isShow) {
				addButton?.apply {
					click { setClickEvent() }
					if (isLeft) setLeftPosition() else setRightPosition()
				}
			} else {
				removeView(addButton)
				addButton = null
			}
		}
	}

	fun setFilterEvent(action: () -> Unit) {
		searchInput?.setFilterClickEvent(action)
	}

	fun showSearchButton(isShow: Boolean, setClickEvent: () -> Unit) {
		if (isShow) searchButton.click {
			setClickEvent()
		}.into(this)
		else findViewById<ImageView>(ElementID.searchButton)?.let {
			removeView(it)
		}
	}

	fun showSearchInput(
		isShow: Boolean = true,
		cancelEvent: () -> Unit = {},
		enterKeyAction: () -> Unit
	) {
		if (!isShow) {
			title.visibility = View.VISIBLE
			removeView(searchInput)
			searchInput = null
		} else {
			showCloseButton(false) {}
			title.visibility = View.GONE
			val input =
				findViewById<FilterSearchInput>(ElementID.searchInput)
			if (input == null) {
				searchInput = FilterSearchInput(context)
				searchInput?.apply {
					enterKeyEvent = Runnable {
						enterKeyAction()
					}
					setCancelClick {
						// 取消搜索后自动清空搜索框里面的内容
						// editText.text.clear()
						SoftKeyboard.hide(context as Activity)
						// 等待键盘完全收起后在执行动作防止页面抖动
						cancelEvent()
						showSearchInput(false) {}
					}
					AnimationDuration.Default timeUpThen {
						editText.requestFocus()
						SoftKeyboard.show(context as Activity, editText)
					}
					editText.addTextChangedListener(object : TextWatcher {
						override fun afterTextChanged(content: Editable?) {
							searchTextChangedEvent?.run()
						}

						override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
						override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
					})
				}?.into(this)
			}
		}
	}

	fun getSearchContent(): String {
		return searchInput?.editText?.text?.toString().orEmpty()
	}
}