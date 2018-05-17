package io.goldstone.blockchain.common.animation

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.View
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.observing

class NetWaveAnimationView(context: Context) : View(context) {

	var frame: RectF by observing(RectF(0f, 0f, 0f, 0f)) {
		invalidate()
	}

	var horizontalSpeed: Float by observing(0f) {
		invalidate()
	}

	var verticalSpeed: Float by observing(0f) {
		invalidate()
	}

	private val duration = 180f
	private val speedTime = 1f
	private var currentTime = 0f

	@SuppressLint("DrawAllocation")
	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)

		NetShapeView.addNetWaveShape(
			canvas,
			currentTime,
			duration,
			frame,
			animationLinePaint(frame.height()),
			horizontalSpeed,
			verticalSpeed
		)
		currentTime += speedTime
		postInvalidateDelayed(10L)
	}

	private fun animationLinePaint(height: Float): Paint {
		val paint = Paint()
		val gradient = LinearGradient(0f, 0f, 0f, height,
			intArrayOf(
				Color.argb(0, 255, 255, 255),
				Color.argb(60, 255, 255, 255),
				Color.argb(0, 255, 255, 255)
			), floatArrayOf(0f, 0.7f, 1f), Shader.TileMode.CLAMP
		)

		paint.apply {
			isAntiAlias = true
			shader = gradient
			strokeWidth = 1.uiPX().toFloat()
			style = Paint.Style.STROKE
		}
		return paint
	}
}
