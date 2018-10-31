package io.goldstone.blockchain.module.home.rammarket.ramtrade.view

import android.content.Context
import android.graphics.Canvas
import com.blinnnk.uikit.uiPX
import io.goldstone.blockchain.common.component.edittext.RoundInput

/**
 * @date: 2018/10/31.
 * @author: yanglihai
 * @description:
 */
class RAMPriceRoundInputView(context: Context, private val unit: String): RoundInput(context) {
	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)
		canvas?.drawText(
			unit,
			width - textPaint.measureText(unit) - 25.uiPX(),
			32.uiPX().toFloat(),
			textPaint
		)
	}
}