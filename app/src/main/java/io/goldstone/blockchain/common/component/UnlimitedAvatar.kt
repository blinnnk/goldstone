package io.goldstone.blockchain.common.component

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import io.goldstone.blockchain.R

/**
 * @data 07/12/2018 18/56
 * @author wcx
 * @description 根据输入数字随机生成图片
 */
class UnlimitedAvatar(
	randomNumber: Int,
	val context: Context
) {
	
	private val absoluteRandomNumber = Math.abs(randomNumber)
	private var nameMark = 0
	private var bitmap: Bitmap? = null
	// 根据输入数字随机生成图片,图片集合为空则使用默认图片
	private fun generateImage(): Bitmap? {
		nameMark = absoluteRandomNumber % (avatarList.size + 1)
		val modulus = if (nameMark > 0) nameMark - 1 else nameMark // 数据库id自增从1开始
		val randomColor = getRandomColor(absoluteRandomNumber * 30) // 获得背景颜色
		return try {
			ContextCompat.getDrawable(context, avatarList[modulus])?.apply {
				val backgroundBitmap = Bitmap.createBitmap(
					intrinsicWidth,
					intrinsicHeight,
					Bitmap.Config.RGB_565
				)
				
				backgroundBitmap.eraseColor(Color.parseColor(randomColor))
				bitmap = combineBitmap(
					backgroundBitmap,
					convertDrawable2BitmapByCanvas(this)
				)
			}
			bitmap
		} catch (error: Exception) {
			null
		}
	}
	
	private val avatarList = listOf(
		R.drawable.avatar_brown_bear,
		R.drawable.avatar_cattle,
		R.drawable.avatar_elephant,
		R.drawable.avatar_elk,
		R.drawable.avatar_fox,
		R.drawable.avatar_frogrita,
		R.drawable.avatar_giraffe,
		R.drawable.avatar_hippo,
		R.drawable.avatar_jaguar,
		R.drawable.avatar_koala,
		R.drawable.avatar_lion,
		R.drawable.avatar_orangutan,
		R.drawable.avatar_owl,
		R.drawable.avatar_penguin,
		R.drawable.avatar_raccoon,
		R.drawable.avatar_rhinoceros,
		R.drawable.avatar_wolf
	)
	
	// 根据随机数字获得颜色
	private fun getRandomColor(absoluteRandomNumber: Int): String {
		val colorMax = 220
		val colorMin = 84
		val colorDifference = colorMax - colorMin
		val colorAll = colorDifference * 6
		//取模
		val modulusNumber = absoluteRandomNumber % colorAll
		var redNextInt = colorMax
		var greedNextInt = colorMin
		var blueNextInt = colorMin
		val colorArea = modulusNumber / colorDifference
		val colorChangeAmount = modulusNumber % colorDifference
		
		when (colorArea) {
			0 -> greedNextInt += colorChangeAmount
			
			1 -> {
				redNextInt -= colorChangeAmount
				greedNextInt = colorMax
			}
			
			2 -> {
				redNextInt = colorMin
				greedNextInt = colorMax
				blueNextInt += colorChangeAmount
			}
			
			3 -> {
				redNextInt = colorMin
				greedNextInt = colorMax - colorChangeAmount
				blueNextInt = colorMax
			}
			
			4 -> {
				redNextInt = colorMin + colorChangeAmount
				greedNextInt = colorMin
				blueNextInt = colorMax
			}
			
			5 -> {
				redNextInt = colorMax
				greedNextInt = colorMin
				blueNextInt = colorMax - colorChangeAmount
			}
		}
		var redToHexString = Integer.toHexString(redNextInt)
		if (redToHexString.length < 2) {
			redToHexString = "0$redToHexString"
		}
		var greedToHexString = Integer.toHexString(greedNextInt)
		if (greedToHexString.length < 2) {
			greedToHexString = "0$greedToHexString"
		}
		var blueToHexString = Integer.toHexString(blueNextInt)
		if (blueToHexString.length < 2) {
			blueToHexString = "0$blueToHexString"
		}
		return "#$redToHexString$greedToHexString$blueToHexString"
	}
	
	// 合成图片
	private fun combineBitmap(
		background: Bitmap?,
		foreground: Bitmap
	): Bitmap? {
		val backgroundWidth = background?.width?.toFloat() ?: 0f
		val backgroundHeight = background?.height?.toFloat() ?: 0f
		val foregroundWidth = foreground.width
		val foregroundHeight = foreground.height
		val newmap = Bitmap.createBitmap(
			backgroundWidth.toInt(),
			backgroundHeight.toInt(), Bitmap.Config.ARGB_8888
		)
		// 创建画笔
		val paint = Paint().apply {
			color = Color.WHITE
			shader = LinearGradient(
				backgroundWidth,
				0f,
				backgroundWidth,
				backgroundHeight,
				Color.WHITE,
				Color.TRANSPARENT,
				Shader.TileMode.MIRROR
			)
			isAntiAlias = true // 设置画笔的锯齿效果
			isDither = true
		}
		// 三角形坐标
		var firstTriangleFirstX = 0f
		var firstTriangleSecondX = 0f
		var firstTriangleEndX = 0f
		var firstTriangleFirstY = 0f
		var firstTriangleSecondY = 0f
		var firstTriangleEndY = 0f
		// 矩形坐标
		var rectangleFirstX = 0f
		var rectangleSecondX = 0f
		var rectangleThirdX = 0f
		var rectangleEndX = 0f
		var rectangleFirstY = 0f
		var rectangleSecondY = 0f
		var rectangleThirdY = 0f
		var rectangleEndY = 0f
		// 获得起始点所在区域
		val firstArea = (absoluteRandomNumber / 50) % 4
		val sin = Math.sin(Math.toRadians(20.toDouble()))
		val cos = Math.cos(Math.toRadians(20.toDouble()))
		val cosWidth = (backgroundWidth * cos).toFloat()
		val sinHeight = (backgroundWidth * sin).toFloat()
		val sinMobile = sinHeight / 2
		val speedMultiple = 11
		val coordinateMultiple = 8
		if (firstArea == 0 || firstArea == 2) {
			// 三角坐标
			firstTriangleFirstY = backgroundHeight / 2 - sinHeight - sinMobile
			
			firstTriangleSecondX = backgroundWidth / 2 + cosWidth
			firstTriangleSecondY = backgroundHeight / 2 + sinHeight - sinMobile
			
			firstTriangleEndX = backgroundWidth / 2 - cosWidth
			firstTriangleEndY = backgroundHeight / 2 + sinHeight - sinMobile
			
			firstTriangleFirstY -= absoluteRandomNumber % 50 * speedMultiple / 2
			// 四边形坐标
			rectangleFirstX = backgroundWidth / 2 - backgroundWidth / coordinateMultiple
			rectangleFirstY = backgroundHeight / 2 - cosWidth
			
			rectangleSecondX = backgroundWidth / 2 + backgroundWidth / coordinateMultiple
			rectangleSecondY = backgroundHeight / 2 - cosWidth
			
			rectangleThirdX = backgroundWidth / 2 + backgroundWidth / coordinateMultiple
			rectangleThirdY = backgroundHeight / 2 + cosWidth
			
			rectangleEndX = backgroundWidth / 2 - backgroundWidth / coordinateMultiple
			rectangleEndY = backgroundHeight / 2 + cosWidth
			var factor = 1
			when (firstArea) {
				0 -> {
					factor = -1
					firstTriangleEndY += absoluteRandomNumber % 50 * speedMultiple
					
					rectangleThirdX += absoluteRandomNumber % 50 * speedMultiple * 2
					rectangleEndX -= absoluteRandomNumber % 50 * speedMultiple * 2
				}
				
				2 -> {
					firstTriangleSecondY += absoluteRandomNumber % 50 * speedMultiple
					
					rectangleFirstX -= absoluteRandomNumber % 50 * speedMultiple * 2
					rectangleSecondX += absoluteRandomNumber % 50 * speedMultiple * 2
				}
			}
			firstTriangleFirstX = backgroundWidth / 2 + cosWidth * factor
		} else if (firstArea == 1 || firstArea == 3) {
			// 四边形坐标
			rectangleFirstX = backgroundWidth / 2 - cosWidth
			rectangleFirstY = backgroundHeight / 2 - backgroundWidth / coordinateMultiple
			
			rectangleSecondX = backgroundWidth / 2 + cosWidth
			rectangleSecondY = backgroundHeight / 2 - backgroundWidth / coordinateMultiple
			
			rectangleThirdX = backgroundWidth / 2 + cosWidth
			rectangleThirdY = backgroundHeight / 2 + backgroundWidth / coordinateMultiple
			
			rectangleEndX = backgroundWidth / 2 - cosWidth
			rectangleEndY = backgroundHeight / 2 + backgroundWidth / coordinateMultiple
			var factor = 1
			when (firstArea) {
				1 -> {
					factor = -1
					rectangleSecondY -= absoluteRandomNumber % 50 * speedMultiple * 2
					rectangleThirdY += absoluteRandomNumber % 50 * speedMultiple * 2
				}
				
				3 -> {
					rectangleFirstY -= absoluteRandomNumber % 50 * speedMultiple * 2
					rectangleEndY += absoluteRandomNumber % 50 * speedMultiple * 2
				}
			}
			// 三角坐标
			firstTriangleFirstX = backgroundWidth / 2 + (sinHeight - sinMobile) * factor
			firstTriangleFirstY = backgroundHeight / 2 + cosWidth * factor
			
			firstTriangleSecondX = backgroundWidth / 2 + (sinHeight + sinMobile) * -factor
			firstTriangleSecondY = backgroundHeight / 2 + cosWidth * -factor
			
			firstTriangleEndX = backgroundWidth / 2 + (sinHeight - sinMobile) * factor
			firstTriangleEndY = backgroundHeight / 2 + cosWidth * -factor
			
			firstTriangleSecondX += absoluteRandomNumber % 50 * speedMultiple / 2 * -factor
			firstTriangleEndX += absoluteRandomNumber % 50 * speedMultiple * factor
		}
		// 第一个三角形
		val firstTrianglePath = Path().apply {
			moveTo(
				firstTriangleFirstX,
				firstTriangleFirstY
			)
			lineTo(
				firstTriangleSecondX,
				firstTriangleSecondY
			)
			lineTo(
				firstTriangleEndX,
				firstTriangleEndY
			)
			close()
		}
		//矩形
		val rectanglePath = Path().apply {
			moveTo(
				rectangleFirstX,
				rectangleFirstY
			)
			lineTo(
				rectangleSecondX,
				rectangleSecondY
			)
			lineTo(
				rectangleThirdX,
				rectangleThirdY
			)
			lineTo(
				rectangleEndX,
				rectangleEndY
			)
			close()
		}
		
		Canvas(newmap).apply {
			// 第一个三角形
			drawBitmap(
				background,
				0f,
				0f,
				null
			)
			rotate(
				(absoluteRandomNumber % 50).toFloat(),
				backgroundWidth / 2,
				backgroundHeight / 2
			)
			drawPath(
				firstTrianglePath,
				paint
			)
			// 第二个三角形
			rotate(
				(absoluteRandomNumber % 50).toFloat() + 45,
				backgroundWidth / 2,
				backgroundHeight / 2
			)
			drawPath(
				firstTrianglePath,
				paint
			)
			// 四边形
			rotate(
				-3 * (absoluteRandomNumber % 50).toFloat() - 45,
				backgroundWidth / 2,
				backgroundHeight / 2
			)
			drawPath(
				rectanglePath,
				paint
			)
			
			rotate(
				(absoluteRandomNumber % 50).toFloat(),
				backgroundWidth / 2,
				backgroundHeight / 2
			)
			drawBitmap(
				foreground,
				((backgroundWidth - foregroundWidth) / 2),
				((backgroundHeight - foregroundHeight) / 2),
				null
			)
			save()
			restore()
		}
		return newmap
	}
	
	// 根据资源文件获取bitmap
	private fun convertDrawable2BitmapByCanvas(drawable: Drawable): Bitmap {
		val bitmap = Bitmap.createBitmap(
			drawable.intrinsicWidth,
			drawable.intrinsicHeight,
			if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888
			else Bitmap.Config.RGB_565
		)
		val canvas = Canvas(bitmap)
		drawable.setBounds(
			0,
			0,
			drawable.intrinsicWidth,
			drawable.intrinsicHeight
		)
		drawable.draw(canvas)
		return bitmap
	}
	
	fun getBitmap(): Bitmap? {
		return bitmap ?: generateImage()
	}
}