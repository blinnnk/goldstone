package io.goldstone.blinnnk.module.common.qrcode.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Handler
import android.view.View
import com.blinnnk.extension.isTrue
import com.blinnnk.uikit.uiPX
import com.blinnnk.util.load
import com.blinnnk.util.then
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.CameraPreview
import io.goldstone.blinnnk.common.value.GrayScale
import io.goldstone.blinnnk.common.value.Spectrum
import java.util.*

/**
 * @date: 2018/9/13.
 * @author: yanglihai
 * @description:
 */
class QRCodeFinderView(context: Context) : View(context) {
	private val currentPointOpacity = 0xA0
	private val maxResultPoints = 20
	private val pointSize = 6

	private val grideDistance = 7.uiPX()

	private var gridBottomLine = 0

	private val cornerWidth = 5.uiPX().toFloat()
	private val cornerHeight = 25.uiPX().toFloat()
	private val cornerColor = Spectrum.darkBlue

	private var paint = Paint(Paint.ANTI_ALIAS_FLAG)
	private val maskColor: Int = GrayScale.Opacity2Black // 四周的 mask color
	private val resultPointColor: Int = Color.YELLOW
	private var scannerAlpha: Int = 0
	private var possibleResultPoints: MutableList<ResultPoint>
	private var lastPossibleResultPoints: MutableList<ResultPoint>
	private var cameraPreview: CameraPreview? = null

	// Cache the framingRect and previewFramingRect, so that we can still draw it after the preview
	// stopped.
	private var framingRect: Rect? = null
	private var previewFramingRect: Rect? = null

	private val calculateHandler = Handler()

	fun setCameraPreview(view: CameraPreview) {
		this.cameraPreview = view
		view.addStateListener(object : CameraPreview.StateListener {
			override fun previewSized() {
				refreshSizes()
				invalidate()
			}

			override fun previewStarted() {}
			override fun previewStopped() {}
			override fun cameraError(error: Exception) {}
			override fun cameraClosed() {}
		})
	}

	init {
		// Initialize these once for performance rather than calling them every time in onDraw().
		scannerAlpha = 0
		possibleResultPoints = ArrayList(maxResultPoints)
		lastPossibleResultPoints = ArrayList(maxResultPoints)
	}

	private fun refreshSizes() {
		cameraPreview?.apply {
			this@QRCodeFinderView.framingRect = this.framingRect
			this@QRCodeFinderView.previewFramingRect = this.previewFramingRect
		}
	}

	public override fun onDraw(canvas: Canvas) {
		refreshSizes()
		drawMask(canvas)
		drawScanGrid(canvas)
		drawCorners(canvas)
		drawResultPoints(canvas)
	}

	private fun drawMask(canvas: Canvas) {
		val width = canvas.width
		val height = canvas.height

		framingRect?.apply {
			// Draw the exterior (i.e. outside the framing rect) darkened
			paint.color = maskColor
			canvas.drawRect(
				0f,
				0f,
				width.toFloat(),
				top.toFloat(),
				paint
			)
			canvas.drawRect(
				0f,
				top.toFloat(),
				left.toFloat(),
				(bottom + 1).toFloat(),
				paint
			)
			canvas.drawRect(
				(right + 1).toFloat(),
				top.toFloat(),
				width.toFloat(),
				(bottom + 1).toFloat(),
				paint
			)
			canvas.drawRect(
				0f,
				(bottom + 1).toFloat(),
				width.toFloat(),
				height.toFloat(),
				paint
			)
		}
	}

	private fun calculateBottomGridLine() {
		load {
			framingRect?.apply {
				if (gridBottomLine == 0) {
					gridBottomLine = top + 20.uiPX()
				} else {
					gridBottomLine += 13
					if (gridBottomLine > bottom) {
						gridBottomLine = top
					}
				}
			}
			calculateHandler.removeCallbacksAndMessages(null)
			calculateHandler.postDelayed({
				calculateBottomGridLine()
			}, 20)
		} then {
			framingRect?.apply {
				postInvalidateDelayed(
					0,
					left - pointSize,
					top - pointSize,
					right + pointSize,
					bottom + pointSize
				)
			}
		}

	}

	private fun drawScanGrid(canvas: Canvas) {
		framingRect?.apply {
			paint.color = cornerColor
			var startTop = top.toFloat()
			var startLeft = left.toFloat()
			do {
				canvas.drawLine(startLeft,
					top.toFloat(),
					startLeft,
					gridBottomLine.toFloat(),
					paint)
				startLeft += grideDistance
			} while (startLeft < right)

			do {
				canvas.drawLine(left.toFloat(),
					startTop,
					right.toFloat(),
					startTop,
					paint)
				startTop += grideDistance
			} while (startTop < gridBottomLine.toFloat())

			canvas.drawRect(left.toFloat(),
				gridBottomLine.toFloat(),
				right.toFloat(),
				gridBottomLine + 5f,
				paint)

		}
	}

	private fun drawCorners(canvas: Canvas) {
		framingRect?.apply {
			paint.color = cornerColor
			// 左上角
			canvas.drawRect(left.toFloat(),
				top.toFloat(),
				left + cornerWidth,
				top + cornerHeight,
				paint)
			canvas.drawRect(left.toFloat(),
				top.toFloat(),
				left + cornerHeight,
				top + cornerWidth,
				paint)
			// 右上角
			canvas.drawRect(right - cornerHeight,
				top.toFloat(),
				right.toFloat(),
				top + cornerWidth,
				paint)
			canvas.drawRect(right - cornerWidth,
				top.toFloat(),
				right.toFloat(),
				top + cornerHeight,
				paint)
			// 左下角
			canvas.drawRect(left.toFloat(),
				bottom - cornerHeight,
				left + cornerWidth,
				bottom.toFloat(),
				paint)
			canvas.drawRect(left.toFloat(),
				bottom - cornerWidth,
				left + cornerHeight,
				bottom.toFloat(),
				paint)
			// 右下角
			canvas.drawRect(right - cornerHeight,
				bottom - cornerWidth,
				right.toFloat(),
				bottom.toFloat(),
				paint)
			canvas.drawRect(right - cornerWidth,
				bottom - cornerHeight,
				right.toFloat(),
				bottom.toFloat(),
				paint)
		}
	}

	private fun drawResultPoints(canvas: Canvas) {
		previewFramingRect?.let { previewFrame ->
			framingRect?.let { frame ->
				val scaleX = frame.width() / previewFrame.width().toFloat()
				val scaleY = frame.height() / previewFrame.height().toFloat()
				// draw the last possible result points
				lastPossibleResultPoints.isNotEmpty() isTrue {
					paint.alpha = currentPointOpacity / 2
					paint.color = resultPointColor
					val radius = pointSize / 2.0f
					lastPossibleResultPoints.forEach { point ->
						canvas.drawCircle(
							(frame.left + (point.x * scaleX).toInt()).toFloat(),
							(frame.top + (point.y * scaleY).toInt()).toFloat(),
							radius,
							paint
						)
					}
					lastPossibleResultPoints.clear()
				}

				// draw current possible result points
				possibleResultPoints.isNotEmpty() isTrue {
					paint.alpha = currentPointOpacity
					paint.color = resultPointColor
					possibleResultPoints.forEach { point ->
						canvas.drawCircle(
							(frame.left + (point.x * scaleX).toInt()).toFloat(),
							(frame.top + (point.y * scaleY).toInt()).toFloat(),
							pointSize.toFloat(),
							paint
						)
					}
					// swap and clear buffers
					val temp = possibleResultPoints
					possibleResultPoints = lastPossibleResultPoints
					lastPossibleResultPoints = temp
					possibleResultPoints.clear()
				}
			}

		}
	}

	fun onPause() {
		calculateHandler.removeCallbacksAndMessages(null)
	}

	fun onResume() {
		calculateBottomGridLine()
	}

	override fun onDetachedFromWindow() {
		super.onDetachedFromWindow()
		calculateHandler.removeCallbacksAndMessages(null)
	}

	/**
	 * Only call from the UI thread.
	 *
	 * @param point a point to draw, relative to the preview frame
	 */
	fun addPossibleResultPoint(point: ResultPoint) {
		if (possibleResultPoints.size < maxResultPoints) possibleResultPoints.add(point)
	}
}