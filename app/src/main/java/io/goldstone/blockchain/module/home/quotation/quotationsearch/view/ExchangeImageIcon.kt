package io.goldstone.blockchain.module.home.quotation.quotationsearch.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.widget.ImageView
import io.goldstone.blockchain.common.value.GrayScale

/**
 * @date: 2018/10/26.
 * @author: yanglihai
 * @description:
 */
class ExchangeImageIcon(context: Context): ImageView(context) {
	@SuppressLint("DrawAllocation")
	override fun onDraw(canvas: Canvas?) {
		val backGroundRect = RectF(0f, 0f, width.toFloat(), height.toFloat())
		val backGroundPaint =Paint().apply { color = GrayScale.lightGray }
		canvas?.drawRoundRect(backGroundRect, width.toFloat()/2, height.toFloat()/2, backGroundPaint)
		super.onDraw(canvas)
	}
}