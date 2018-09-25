package io.goldstone.blockchain.common.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import io.goldstone.blockchain.common.value.BorderSize
import io.goldstone.blockchain.common.value.GrayScale


/**
 * @author KaySaith
 * @date  2018/09/19
 */
class SpaceSplitLine(context: Context) : View(context) {
	val paint = Paint().apply {
		isAntiAlias = true
		style = Paint.Style.FILL
		color = GrayScale.lightGray
	}

	private var borderSize = BorderSize.bold

	@SuppressLint("DrawAllocation")
	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)
		val rectF = RectF(0f, (height - borderSize) / 2, width.toFloat(), (height + borderSize) / 2)
		canvas?.drawRect(rectF, paint)
	}

	fun setStyle(color: Int, borderSize: Float) {
		this.borderSize = borderSize
		paint.color = color
		invalidate()
	}

}