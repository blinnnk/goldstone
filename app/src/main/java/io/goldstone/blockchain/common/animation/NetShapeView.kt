package io.goldstone.blockchain.common.animation

import android.graphics.*

object NetShapeView {

	fun addNetWaveShape(
		canvas: Canvas,
		currentTime: Float,
		duration: Float,
		frameRectF: RectF,
		paint: Paint,
		horizontalSpeed: Float,
		verticalSpeed: Float
	) {

		val moveRate = currentTime / duration + 1.2f
		val framePath = Path()
		val height = frameRectF.height()
		val width = frameRectF.width()
		framePath.addRect(frameRectF, Path.Direction.CCW)

		canvas.apply {
			save()
			clipPath(framePath)
			translate(frameRectF.left, frameRectF.top)
			translate(0f, height * -0.1f)
			addNetShape(width * 1.2f, height * 1.2f, moveRate, paint, horizontalSpeed, verticalSpeed)

			restore()
		}
	}

	private fun Canvas.addNetShape(
		width: Float,
		height: Float,
		moveRate: Float,
		paint: Paint,
		horizontalSpeed: Float,
		verticalSpeed: Float
	) {

		// 控制主线的波浪动画
		val controlAngle = Math.PI * 0.4 * Math.sin(moveRate.toDouble())
		val verticalCurveCount = 36
		val horizontalCurveCount = 40

		val controlPoint = floatArrayOf(0.4f, 0.6f)
		val controlRadius = floatArrayOf(0.15f * height, 0.6f * height)

		val mainPathBaseHeight = height - controlRadius[1]
		val mainPathLeftStartX = 0f
		val mainPathControl1X =
			width * controlPoint[0] - Math.cos(controlAngle).toFloat() * controlRadius[0]
		val mainPathControl1Y = mainPathBaseHeight - Math.sin(controlAngle).toFloat() * controlRadius[0]
		val mainPathControl2X =
			width * controlPoint[0] + Math.cos(controlAngle).toFloat() * controlRadius[1]
		val mainPathControl2Y = mainPathBaseHeight + Math.sin(controlAngle).toFloat() * controlRadius[1]

		val mainPath = Path()
		mainPath.apply {
			moveTo(mainPathLeftStartX, mainPathBaseHeight)
			cubicTo(
				mainPathControl1X, mainPathControl1Y, mainPathControl2X, mainPathControl2Y, width,
				mainPathBaseHeight
			)
		}

		val verticalControl1Path = Path()
		verticalControl1Path.apply {
			val offsetY = height * 0.25f
			val offsetX = width * -0.3f
			val scaleX = 1.2f
			val scalePointX = width * 0.5f
			moveTo(
				scalePointX + (mainPathLeftStartX - scalePointX) * scaleX + offsetX,
				mainPathBaseHeight + offsetY
			)
			cubicTo(
				scalePointX + (mainPathControl1X - scalePointX) * scaleX + offsetX,
				mainPathControl1Y + offsetY,
				scalePointX + (mainPathControl2X - scalePointX) * scaleX + offsetX,
				mainPathControl2Y + offsetY, scalePointX + (width - scalePointX) * scaleX + offsetX,
				mainPathBaseHeight + offsetY
			)
		}

		val verticalControl2Path = Path()
		verticalControl2Path.apply {
			val offsetY = height * 0.38f
			val offsetX = width * 0.2f
			val scaleX = 1.2f
			val scalePointX = width * 0.7f
			moveTo(
				scalePointX + (mainPathLeftStartX - scalePointX) * scaleX + offsetX,
				mainPathBaseHeight + offsetY
			)
			cubicTo(
				scalePointX + (mainPathControl1X - scalePointX) * scaleX + offsetX,
				mainPathControl1Y + offsetY,
				scalePointX + (mainPathControl2X - scalePointX) * scaleX + offsetX,
				mainPathControl2Y + offsetY, scalePointX + (width - scalePointX) * scaleX + offsetX,
				mainPathBaseHeight + offsetY
			)
		}

		val verticalEndPath = Path()
		verticalEndPath.apply {
			val offsetY = height * 0.8f
			val offsetX = width * -0.6f
			val scaleX = 1.1f
			val scalePointX = width * -0.1f
			moveTo(
				scalePointX + (mainPathLeftStartX - scalePointX) * scaleX + offsetX,
				mainPathBaseHeight + offsetY
			)
			cubicTo(
				scalePointX + (mainPathControl1X - scalePointX) * scaleX + offsetX,
				mainPathControl1Y + offsetY,
				scalePointX + (mainPathControl2X - scalePointX) * scaleX + offsetX,
				mainPathControl2Y + offsetY, scalePointX + (width - scalePointX) * scaleX + offsetX,
				mainPathBaseHeight + offsetY
			)
		}

		val netPath = Path()
		val horizontalMainPath = Path()

		// 添加纵线
		for (index in 0 .. verticalCurveCount) {
			val percentage = (index.toFloat() - verticalSpeed * moveRate % 1f) / verticalCurveCount.toFloat()
			val verticalStartPosition = getPositionFromPathByPercent(percentage, mainPath)
			val verticalStartX = verticalStartPosition[0]
			val verticalStartY = verticalStartPosition[1]

			val verticalControl1Position = getPositionFromPathByPercent(percentage, verticalControl1Path)
			val verticalControl1X = verticalControl1Position[0]
			val verticalControl1Y = verticalControl1Position[1]

			val verticalControl2Position = getPositionFromPathByPercent(percentage, verticalControl2Path)
			val verticalControl2X = verticalControl2Position[0]
			val verticalControl2Y = verticalControl2Position[1]

			val verticalEndPosition = getPositionFromPathByPercent(percentage, verticalEndPath)
			val verticalEndX = verticalEndPosition[0]
			val verticalEndY = verticalEndPosition[1]

			val newDash = Path()
			newDash.apply {
				moveTo(verticalStartX, verticalStartY)
				cubicTo(
					verticalControl1X, verticalControl1Y, verticalControl2X, verticalControl2Y, verticalEndX,
					verticalEndY
				)
			}
			netPath.addPath(newDash)
			if (index == 0) {
				horizontalMainPath.apply {
					moveTo(verticalEndX, verticalEndY)
					cubicTo(
						verticalControl2X, verticalControl2Y, verticalControl1X, verticalControl1Y,
						mainPathLeftStartX, mainPathBaseHeight
					)
					cubicTo(
						mainPathControl1X, mainPathControl1Y, mainPathControl2X, mainPathControl2Y, width,
						mainPathBaseHeight
					)
				}
			}
		}

		//横向控制线
		val horizontalMainStartX = getPositionFromPathByPercent(0f, horizontalMainPath)[0] + width * 0.5f
		val horizontalMainStartY = getPositionFromPathByPercent(0f, horizontalMainPath)[1] - height * 0.1f
		val horizontalMainControl1X =
			getPositionFromPathByPercent(0.4f, horizontalMainPath)[0] + width * 0.5f
		val horizontalMainControl1Y =
			getPositionFromPathByPercent(0.4f, horizontalMainPath)[1] - height * 0.3f
		val horizontalMainControl2X =
			getPositionFromPathByPercent(0.8f, horizontalMainPath)[0] + width * 0.3f
		val horizontalMainControl2Y =
			getPositionFromPathByPercent(0.8f, horizontalMainPath)[1] + height * 0.8f
		val horizontalMainEndX = getPositionFromPathByPercent(1f, horizontalMainPath)[0] + width * 0.1f
		val horizontalMainEndY = getPositionFromPathByPercent(1f, horizontalMainPath)[1] - height * 0.4f

		val horizontalControl1Path = Path()
		horizontalControl1Path.apply {
			val offsetY = height * 0.15f
			val offsetX = width * 0.3f
			val scaleX = 0.8f
			val scalePointX = width * 0.5f

			moveTo(
				scalePointX + (horizontalMainStartX - scalePointX) * scaleX + offsetX,
				horizontalMainStartY - offsetY
			)
			cubicTo(
				scalePointX + (horizontalMainControl1X - scalePointX) * scaleX + offsetX,
				horizontalMainControl1Y + offsetY,
				scalePointX + (horizontalMainControl2X - scalePointX) * scaleX + offsetX,
				horizontalMainControl2Y + offsetY,
				scalePointX + (horizontalMainEndX - scalePointX) * scaleX + offsetX,
				horizontalMainEndY + offsetY
			)
		}

		val horizontalControl2Path = Path()
		horizontalControl2Path.apply {
			val offsetY = height * 0.33f
			val offsetX = width * 0.4f
			val scaleX = 1.2f
			val scalePointX = width * 0.7f
			moveTo(
				scalePointX + (horizontalMainStartX - scalePointX) * scaleX + offsetX,
				horizontalMainStartY + offsetY * -1f
			)
			cubicTo(
				scalePointX + (horizontalMainControl1X - scalePointX) * scaleX + offsetX,
				horizontalMainControl1Y + offsetY * 1f,
				scalePointX + (horizontalMainControl2X - scalePointX) * scaleX + offsetX,
				horizontalMainControl2Y + offsetY * 2f,
				scalePointX + (horizontalMainEndX - scalePointX) * scaleX + offsetX,
				horizontalMainEndY + offsetY * 3f
			)
		}

		val horizontalEndPath = Path()
		horizontalEndPath.apply {
			val offsetY = height * 0.45f
			val offsetX = width * 0.6f
			val scaleX = 1.1f
			val scalePointX = width * -0.1f
			val scaleY = 0.8f
			val scalePointY = height * 0.8f
			moveTo(
				scalePointX + (horizontalMainStartX - scalePointX) * scaleX + offsetX,
				horizontalMainStartY + offsetY
			)
			cubicTo(
				scalePointX + (horizontalMainControl1X - scalePointX) * scaleX + offsetX,
				scalePointY + (horizontalMainControl1Y - scalePointY) * scaleY + offsetY,
				scalePointX + (horizontalMainControl2X - scalePointX) * scaleX + offsetX,
				scalePointY + (horizontalMainControl2Y - scalePointY) * scaleY + offsetY,
				scalePointX + (horizontalMainEndX - scalePointX) * scaleX + offsetX,
				scalePointY + (horizontalMainEndY - scalePointY) * scaleY + offsetY
			)
		}

		//绘制横线
		for (index in 0 .. horizontalCurveCount) {
			val percentage = (index.toFloat() - horizontalSpeed * moveRate % 1f) / horizontalCurveCount.toFloat()
			val horizontalStartPosition = getPositionFromPathByPercent(percentage, horizontalMainPath)
			val horizontalStartX = horizontalStartPosition[0]
			val horizontalStartY = horizontalStartPosition[1]

			val horizontalControl1Position =
				getPositionFromPathByPercent(percentage, horizontalControl1Path)
			val horizontalControl1X = horizontalControl1Position[0]
			val horizontalControl1Y = horizontalControl1Position[1]

			val horizontalControl2Position =
				getPositionFromPathByPercent(percentage, horizontalControl2Path)
			val horizontalControl2X = horizontalControl2Position[0]
			val horizontalControl2Y = horizontalControl2Position[1]

			val horizontalEndPosition = getPositionFromPathByPercent(percentage, horizontalEndPath)
			val horizontalEndX = horizontalEndPosition[0]
			val horizontalEndY = horizontalEndPosition[1]

			val newDash = Path()
			newDash.apply {
				moveTo(horizontalStartX, horizontalStartY)
				cubicTo(
					horizontalControl1X, horizontalControl1Y, horizontalControl2X, horizontalControl2Y,
					horizontalEndX, horizontalEndY
				)
			}
			netPath.addPath(newDash)
		}
		netPath.addPath(mainPath)
		drawPath(netPath, paint)
	}

	private fun getPositionFromPathByPercent(
		percentage: Float,
		path: Path
	): FloatArray {
		val pathMeasure = PathMeasure(path, false)
		val pathLength = pathMeasure.length
		val positionArray = floatArrayOf(0f, 0f)
		pathMeasure.getPosTan(percentage * pathLength, positionArray, null)
		return positionArray
	}

}