package io.goldstone.blockchain.module.home.profile.contacts.contracts.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.*
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.extension.*
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.component.TwoLineTitles
import io.goldstone.blockchain.common.value.*
import io.goldstone.blockchain.crypto.CryptoUtils
import io.goldstone.blockchain.module.home.profile.contacts.contracts.model.ContactTable
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.relativeLayout
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.onTouch
import org.jetbrains.anko.textColor

/**
 * @date 26/03/2018 1:37 PM
 * @author KaySaith
 */

open class ContactsCell(context: Context) : HorizontalScrollView(context) {

	var clickEvent: Runnable? = null

	var model: ContactTable by observing(ContactTable()) {
		info.apply {
			title.text = model.name
			subtitle.text = CryptoUtils.scaleMiddleAddress(model.address)
		}
		model.name.isNotEmpty() isTrue {
			fontIcon.text = model.name.substring(0, 1).toUpperCase()
		}
	}

	fun setSlideCell(isSlide: Boolean) {
		if (isSlide) {
			findViewById<RelativeLayout>(ElementID.slideCellContainer).isNull {
				// 这个是可滑动的部分
				relativeLayout {
					id = ElementID.slideCellContainer
					layoutParams = RelativeLayout.LayoutParams(
						ScreenSize.widthWithPadding + deleteButtonWidth,
						matchParent
					)
					deleteButton.into(this)
					showContent()
				}
				slide()
			}
		} else {
			findViewById<RelativeLayout>(ElementID.slideCell).isNull {
				showContent()
			}
		}
	}

	fun onClickDeleteButton(action: () -> Unit) {
		deleteButton.apply {
			onClick {
				smoothScrollTo(0, 0)
				action()
				preventDuplicateClicks()
			}
		}
	}

	private val fontIcon by lazy {
		TextView(context).apply {
			layoutParams = LinearLayout.LayoutParams(50.uiPX(), 50.uiPX())
			addCorner(25.uiPX(), GrayScale.lightGray)
			textSize = fontSize(18)
			textColor = GrayScale.gray
			gravity = Gravity.CENTER
		}
	}

	private val info by lazy { TwoLineTitles(context) }
	private val deleteButton by lazy {
		Button(context).apply {
			text = CommonText.delete
			textSize = fontSize(12)
			layoutParams = RelativeLayout.LayoutParams(deleteButtonWidth, cellHeight)
			x = ScreenSize.widthWithPadding.toFloat() + 3f
			backgroundColor = Spectrum.red
		}
	}

	private val cellHeight = 75.uiPX()
	private val deleteButtonWidth = 100.uiPX()

	init {
		isHorizontalScrollBarEnabled = false
		layoutParams = LinearLayout.LayoutParams(ScreenSize.widthWithPadding, cellHeight)
		setMargins<LinearLayout.LayoutParams> { leftMargin = PaddingSize.device }
	}

	private fun ViewGroup.showContent() {
		// 这个是实际显示的部分
		relativeLayout {
			id = ElementID.slideCell
			layoutParams = RelativeLayout.LayoutParams(
				ScreenSize.widthWithPadding, (cellHeight - BorderSize.bold).toInt()
			)
			fontIcon.into(this)
			fontIcon.setCenterInVertical()
			info.apply {
				setBlackTitles()
				x += 60.uiPX()
			}.into(this)
			info.setCenterInVertical()
			addTouchRippleAnimation(Color.WHITE, GrayScale.lightGray, RippleMode.Square)
			onClick {
				clickEvent?.run()
				preventDuplicateClicks()
			}
		}
	}

	private val paint = Paint().apply {
		isAntiAlias = true
		style = Paint.Style.FILL
		color = GrayScale.lightGray
	}

	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)

		canvas?.drawLine(
			0f, height - BorderSize.default, width.toFloat(), height - BorderSize.default, paint
		)
	}

	private fun HorizontalScrollView.slide() {
		onTouch { _, event ->
			when (event.action) {
				MotionEvent.ACTION_UP -> {
					if (computeHorizontalScrollOffset() >= 50.uiPX()) {
						smoothScrollTo(100.uiPX(), 0)
					}
					if (computeHorizontalScrollOffset() < 50.uiPX()) {
						smoothScrollTo(0, 0)
					}
				}
			}
		}
	}

}