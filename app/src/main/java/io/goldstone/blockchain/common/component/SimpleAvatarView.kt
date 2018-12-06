package io.goldstone.blockchain.common.component

import android.content.Context
import android.graphics.*
import android.view.View
import kotlin.math.sin

class SimpleAvatarView(
  context: Context,
  private val index: Int
) : View(context) {
  
  private var size: Float = 0.0f
  private val colorsSet: IntArray
  private var rotateAngles: FloatArray = floatArrayOf() // 每一片的旋转角度
  private var rectArray: Array<RectF> = arrayOf()
  private val clipCount = 3 // 总共画的片数量
  
  
  init {
    val colorBaseCount = AvatarColorKit.colors.size
    colorsSet = intArrayOf(
      index % colorBaseCount,
      (2 + index * 1.2).toInt() % colorBaseCount,
      (0.4 + (index * 1.5).toInt() + index * 3).toInt() % colorBaseCount,
      (6 + index * 3.4).toInt() % colorBaseCount,
      (5 + index * 4.4).toInt() % colorBaseCount
    )
  }
  
  override fun onSizeChanged(width: Int, height: Int, oldwidth: Int, oldheight: Int) {
    super.onSizeChanged(width, height, oldwidth, oldheight)
    size = width.toFloat()
    for (position in 0 .. clipCount) {
      rotateAngles += (360f * sin(index * 23.0 + position * 10.0).toFloat())
      rectArray += RectF(
        (index % 4 * 0.1f + 0.2f * position) * size,
        (index % 3 * 0.1f + 0.2f * position) * size,
        1.5f * size,
        1.2f * size
      )
    }
    
  }
  
  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    if (Math.abs(width - height)> 10) return
    if (width < 20) return
    addAvatar(canvas)
  }

  private fun addAvatar(canvas: Canvas) {
    canvas.apply {
      drawBackground(size, avatarPaint(AvatarColorKit.colors[colorsSet[0]]))
      for (count in 0 until clipCount) {
        rotate(rotateAngles[count], size / 2, size / 2)
        drawRect(rectArray[count], avatarPaint(AvatarColorKit.colors[colorsSet[count + 1]]))
      }
    }
  }

  private fun Canvas.drawBackground(
    size: Float,
    paint: Paint
  ) {
    val avatarPath = Path()
    avatarPath.apply {
      addCircle(size / 2, size / 2, size / 2, Path.Direction.CCW)
      close()
    }
    clipPath(avatarPath)
    drawPath(avatarPath, paint)

  }

  private fun avatarPaint(color: Int): Paint {
    val paint = Paint()
    paint.reset()
    paint.isAntiAlias = true
    paint.color = color
    paint.style = Paint.Style.FILL
    return paint
  }

  private object AvatarColorKit {
    val yellow = Color.parseColor("#FFFFDF00")
    val darkPurple = Color.parseColor("#FF2B266C")
    val bluePurple = Color.parseColor("#FF4C62B1")
    val blue = Color.parseColor("#FF4A90E2")
    val pink = Color.parseColor("#FFFF75A9")
    val orange = Color.parseColor("#FFEE5012")
    val lakeGreen = Color.parseColor("#FF17A4AC")
    val purple = Color.parseColor("#FF9013FE")
    val colors = intArrayOf(
      yellow, pink, orange, purple, blue, darkPurple, lakeGreen, bluePurple
    )
  }
}