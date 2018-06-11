package io.goldstone.blockchain.common.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import android.widget.LinearLayout
import com.blinnnk.animation.addTouchRippleAnimation
import com.blinnnk.extension.into
import com.blinnnk.uikit.RippleMode
import com.blinnnk.uikit.ScreenSize
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing
import io.goldstone.blockchain.common.utils.GoldStoneFont
import io.goldstone.blockchain.common.value.BorderSize
import io.goldstone.blockchain.common.value.GrayScale
import io.goldstone.blockchain.common.value.ImporMneubar
import io.goldstone.blockchain.common.value.Spectrum
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * @date 23/03/2018 1:00 AM
 * @author KaySaith
 */
open class MenuBar(context: Context) : LinearLayout(context) {
	
	var clickEvent: Runnable? = null
	private val titles = arrayListOf(
		ImporMneubar.mnemonic,
		ImporMneubar.keystore,
		ImporMneubar.privateKey,
		ImporMneubar.watchOnly
	)
	private var totalItemWidth = 0
	private var clickItemID: Int? = null
	private val basicWidth = (ScreenSize.Width - 25.uiPX() * 2) / 4
	
	init {
		backgroundColor = Spectrum.white
		titles.forEachIndexed { index, string ->
			Item(context).apply {
				id = index
				text = string
				val itemWidth =
					if (getTextWidth(string) > basicWidth) getTextWidth(string)
					else basicWidth
				layoutParams = LinearLayout.LayoutParams(itemWidth, 35.uiPX()).apply {
					topMargin = 20.uiPX()
					leftMargin = 10.uiPX()
					totalItemWidth += getTextWidth(string) + 20.uiPX() // 计算总的 `Menu` 宽度
				}
				onClick {
					clickItemID = id
					clickEvent?.run()
				}
			}.into(this)
		}
		layoutParams = LinearLayout.LayoutParams(matchParent, 70.uiPX())
	}
	
	fun onClickItem(hold: Item.() -> Unit) {
		clickItemID?.apply {
			selectItem(this)
			hold(findViewById(this))
		}
	}
	
	fun selectItem(index: Int) {
		(0 until titles.size).forEach {
			findViewById<Item>(it)?.apply {
				if (it == index) setSelectedStyle(true)
				else setSelectedStyle(false)
			}
		}
	}
	
	fun floatRight() {
		(0 until titles.size).forEach {
			findViewById<Item>(it)?.apply {
				if (totalItemWidth > ScreenSize.Width) {
					x -= totalItemWidth - ScreenSize.Width
				}
			}
		}
	}
	
	fun floatLeft() {
		(0 until titles.size).forEach {
			findViewById<Item>(it)?.apply {
				if (totalItemWidth > ScreenSize.Width) {
					x += totalItemWidth - ScreenSize.Width
				}
			}
		}
	}
	
	private fun getTextWidth(text: String): Int {
		val textPaint = Paint()
		textPaint.textSize = 16.uiPX().toFloat()
		textPaint.typeface = GoldStoneFont.heavy(context)
		return textPaint.measureText(text).toInt()
	}
}

class Item(context: Context) : View(context) {
	
	var text by observing("") {
		invalidate()
	}
	private var hasUnderLine = false
	private var titleColor = GrayScale.black
	private val paint = Paint()
	private val textPaint = Paint()
	private val textSize = 15.uiPX().toFloat()
	
	init {
		addTouchRippleAnimation(Spectrum.white, Spectrum.green, RippleMode.Square)
		paint.isAntiAlias = true
		paint.style = Paint.Style.FILL
		paint.color = GrayScale.black
		
		textPaint.isAntiAlias = true
		textPaint.style = Paint.Style.FILL
		textPaint.color = titleColor
		textPaint.textSize = textSize
		textPaint.typeface = GoldStoneFont.heavy(context)
	}
	
	@SuppressLint("DrawAllocation")
	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)
		
		textPaint.color = GrayScale.midGray
		
		if (hasUnderLine) {
			val rectF = RectF(0f, height - BorderSize.bold, width.toFloat(), height.toFloat())
			canvas?.drawRect(rectF, paint)
			textPaint.color = GrayScale.black
		}
		val textY = (height + textSize) / 2 - 3.uiPX()
		canvas?.drawText(
			text,
			(width - textPaint.measureText(text)) / 2f,
			textY,
			textPaint
		)
	}
	
	fun setSelectedStyle(isSelect: Boolean) {
		hasUnderLine = isSelect
		invalidate()
	}
}