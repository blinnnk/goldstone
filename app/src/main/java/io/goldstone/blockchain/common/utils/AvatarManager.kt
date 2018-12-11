@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package io.goldstone.blockchain.common.utils

import android.content.res.Resources
import android.graphics.*
import io.goldstone.blockchain.GoldStoneApp
import java.io.File
import java.io.FileOutputStream
import kotlin.math.sin

/**
 * @date: 2018-12-07.
 * @author: yangLiHai
 * @description:
 */
object AvatarManager {
  
  private val avatarPath = GoldStoneApp.appContext.getExternalFilesDir(null).absolutePath+"/avatar/"
  private const val clipCount = 3
  
  fun getAvatarPath(index: Int): String {
    val avatarName = "$index.png"
    val file = File(avatarPath+avatarName)
    if (!file.exists()) {
      if (!file.parentFile.exists()) file.parentFile.mkdirs()
			val size = (Resources.getSystem().displayMetrics.density * 100).toInt()
      val avatar = createAvatar(size, index)
      val outputStream = FileOutputStream(file)
      avatar.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
      outputStream.flush()
      outputStream.close()
    }
    return file.absolutePath
  }
  
  private fun createAvatar(size: Int, index: Int): Bitmap {
    val rotateAngles: FloatArray = getRotateAngles(index)
    val rectArray: Array<RectF> = getRectArray(size, index)
    val colorIndexArray = getColorIndexArray(index)
    val avatar = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val paint = Paint().apply {
      isAntiAlias = true
      style = Paint.Style.FILL
    }
    Canvas(avatar).apply {
      // 限制绘制区域
      val avatarPath = Path()
      avatarPath.apply {
        addCircle(size / 2f, size / 2f, size / 2f, Path.Direction.CCW)
        close()
      }
      clipPath(avatarPath)
      drawPath(avatarPath, paint)
  
      // 画背景
      paint.color = AvatarColorKit.colors[colorIndexArray[0]]
      drawRect(RectF(0f, 0f, size.toFloat(), size.toFloat()), paint)
  
      // 旋转画内容
      for (count in 0 until clipCount) {
        rotate(rotateAngles[count], size / 2f, size / 2f)
        paint.color = AvatarColorKit.colors[colorIndexArray[count + 1]]
        drawRect(rectArray[count], paint)
      }
    }
    return avatar
  }
  
  private fun getColorIndexArray(index: Int): IntArray {
    val colorBaseCount = AvatarColorKit.colors.size
    return intArrayOf(
      index % colorBaseCount,
      (2 + index * 1.2).toInt() % colorBaseCount,
      (0.4 + (index * 1.5).toInt() + index * 3).toInt() % colorBaseCount,
      (6 + index * 3.4).toInt() % colorBaseCount,
      (5 + index * 4.4).toInt() % colorBaseCount
    )
  }
  
  private fun getRotateAngles(index: Int): FloatArray {
    var rotateAngles: FloatArray = floatArrayOf()
    for (position in 0 .. clipCount) {
      rotateAngles += (360f * sin(index * 23.0 + position * 10.0).toFloat())
    }
    return rotateAngles
  }
  
  private fun getRectArray(size:Int, index: Int): Array<RectF> {
    var rectArray: Array<RectF> = arrayOf()
    for (position in 0 .. clipCount) {
      rectArray += RectF(
        (index % 4 * 0.1f + 0.2f * position) * size,
        (index % 3 * 0.1f + 0.2f * position) * size,
        1.5f * size,
        1.2f * size
      )
    }
    return rectArray
  }
	
	object AvatarColorKit {
		private val yellow = Color.parseColor("#FFFFDF00")
		private val darkPurple = Color.parseColor("#FF2B266C")
		private val bluePurple = Color.parseColor("#FF4C62B1")
		private val blue = Color.parseColor("#FF4A90E2")
		private val pink = Color.parseColor("#FFFF75A9")
		private val orange = Color.parseColor("#FFEE5012")
		private val lakeGreen = Color.parseColor("#FF17A4AC")
		private val purple = Color.parseColor("#FF9013FE")
		val colors = intArrayOf(
			yellow,
			pink,
			orange,
			purple,
			blue,
			darkPurple,
			lakeGreen,
			bluePurple
		)
	}
  
  

}
